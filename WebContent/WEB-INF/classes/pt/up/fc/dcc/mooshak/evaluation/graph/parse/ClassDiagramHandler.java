package pt.up.fc.dcc.mooshak.evaluation.graph.parse;


import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GObject;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyValue;


public class ClassDiagramHandler extends DiagramHandler {
	GObject current = null;
	List<Node> nodes;
	List<Edge> edges;
	Edge currentEdge = null;
	int level = 0;
	boolean isEdge = false;
	boolean isSource = false;
	boolean isName = false;
	boolean isAttribute = false;
	boolean isOperation = false;
	boolean isParameter = false;
	boolean getString = false;
	boolean getName = false;
	boolean isObject = false;
	boolean isComposite = false;
	boolean getMultiplicity = false;
	boolean getAssociationType = false;
	String propertyNameKey = null;
	String propertyNameValue = null;
	String propertyValueKey = null;
	String propertyValueValue = null;
	String parameterName = null;
	CompositePropertyName propertyName = null;
	CompositePropertyValue propertyValue = new CompositePropertyValue();

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		level++;
		if (qName.equalsIgnoreCase("dia:layer")) {
			level = 0;
			handleStartLayer();
		}

		if (qName.equalsIgnoreCase("dia:object")) {
			isObject = true;
			handleStartObject(attributes);
		}

		if (qName.equalsIgnoreCase("dia:connections")) {
			handleStartConnections();
		}

		if (qName.equalsIgnoreCase("dia:connection")) {
			handleStartEdge(attributes);
		}

		if (qName.equalsIgnoreCase("dia:enum")) {
			if(getAssociationType){
				if(attributes.getValue("val").equals("1"))
					current.setType("UML - Aggregation");
				else if (attributes.getValue("val").equals("2"))
					current.setType("UML - Composition");
				getAssociationType = false;
			}
			
		}

		if (qName.equalsIgnoreCase("dia:attribute")) {
			if (isObject) {
				if (level == 2) {
					String name = attributes.getValue("name");
					if (name.contains("multipicity")) {
						propertyNameKey = name;
						getMultiplicity = true;
					}
					else if(current.getType().equals("UML - Association") && name.equals("assoc_type")){
						getAssociationType = true;
					}
				} else if (level == 4) {
					getString = true;
					String name = attributes.getValue("name");
					if (name.equals("name"))
						getName = true;
					else if (name.equals("type"))
						propertyValueKey = name;
				}

				else if (level == 6) {
					getString = false;
					String name = attributes.getValue("name");
					if (name.equals("type")) {
						getName = true;
						getString = true;
					}
				}
			}
		}

		if (qName.equalsIgnoreCase("dia:composite")) {
			if (isObject) {
				if (level == 3)
					createCompositeProperty(attributes);

				else if (level == 5) {
					addParameterToOperation(attributes);
				}
			}
		}

	}

	private void addParameterToOperation(Attributes attributes) {
		parameterName = attributes.getValue(0);
	}

	private void createCompositeProperty(Attributes attributes) {
		if (attributes.getValue(0).equals("dict")
				|| attributes.getValue(0).equals("text")) {
			isComposite = false;
			return;
		}
		isComposite = true;
		propertyName = null;
		propertyValue = new CompositePropertyValue();
		propertyNameKey = attributes.getValue(0);
		getName = true;
	}

	private void handleStartEdge(Attributes attributes) {
		Node node = findNodeById(attributes.getValue("to"));
		String type = currentEdge.getType();
		if (isSource) {
			currentEdge.setSource(node);
			if(node.getOutDegree().containsKey(type)){
				int currentDegree = node.getOutDegree().get(type);
				node.getOutDegree().put(type, currentDegree + 1);
			}
			else{
				node.getOutDegree().put(type, 1);
			}
			isSource = false;
		}

		else {
			currentEdge.setTarget(node);
			if(node.getInDegree().containsKey(type)){
				int currentDegree = node.getInDegree().get(type);
				node.getInDegree().put(type, currentDegree + 1);
			}
			else{
				node.getInDegree().put(type, 1);
			}
		}
	}

	private void handleStartConnections() {
		isEdge = true;
		isSource = true;
		currentEdge = new Edge(current);
	}

	private void handleStartObject(Attributes attributes) {
		String id = attributes.getValue("id");
		String type = attributes.getValue("type");
		current = new GObject(id, type);
	}

	private void handleStartLayer() {
		nodes = new ArrayList<>();
		edges = new ArrayList<>();
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		super.endElement(uri, localName, qName);

		if (qName.equalsIgnoreCase("dia:layer")) {
			handleEndLayer();
		}

		if (qName.equalsIgnoreCase("dia:object")) {
			handleEndObject();
		}

		if (qName.equalsIgnoreCase("dia:composite")) {
			handleEndComposite();
		}

		if (qName.equalsIgnoreCase("dia:string")) {
			getString = false;
		}
		level--;
	}

	private void handleEndComposite() {
		if (isComposite && level == 3) {
			current.addProperty(propertyName, propertyValue);
			propertyNameKey = null;
			propertyNameValue = null;
			propertyName = null;
			propertyValue = new CompositePropertyValue();
			getName = false;
			getString = false;
		}
		propertyValueKey = null;
		propertyValueValue = null;
	}

	private void handleEndObject() {
		if (isEdge) {
			edges.add(currentEdge);
			isEdge = false;
			if (currentEdge.getType().equals("UML - Association")) {
				Edge reverseEdge = currentEdge.reverse();
				edges.add(reverseEdge);
			}

		}

		else
			nodes.add(new Node(current));
		isObject = false;
	}

	private void handleEndLayer() {
		graph = new Graph(nodes, edges);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (getMultiplicity && level == 3) {
			String string = new String(ch, start, length);
			if (!string.equals("##"))
				current.addProperty(new SimplePropertyName(propertyNameKey),
						new SimplePropertyValue(string));
			propertyNameKey = null;
			getMultiplicity = false;
		} else if (getString && level == 5) {
			String string = new String(ch, start, length);
			if (getName) {
				propertyNameValue = string;
				getName = false;
				propertyName = new CompositePropertyName(propertyNameKey,
						propertyNameValue);
			}

			else if (propertyValueKey != null) {
				propertyValueValue = string;
				propertyValue.add(propertyValueKey, propertyValueValue);
				propertyValueKey = null;
			}
		}

		else if (getString && level == 7) {
			String string = new String(ch, start, length);
			if (getName) {
				propertyValueKey = parameterName + string;
				propertyValueValue = "0";

				if (propertyValue.getValue().containsKey(propertyValueKey))
					propertyValueValue = propertyValue.getValue().get(
							propertyValueKey);

				int count = Integer.parseInt(propertyValueValue);
				count++;
				propertyValueValue = String.valueOf(count);
				propertyValue.add(propertyValueKey, propertyValueValue);
				getName = false;
			}
		}
	}

	private Node findNodeById(String id) {
		for (Node node : nodes) {
			if (node.getId().equals(id))
				return node;
		}
		return null;
	}
}
