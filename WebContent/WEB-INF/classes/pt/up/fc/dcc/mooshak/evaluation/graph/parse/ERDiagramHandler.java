package pt.up.fc.dcc.mooshak.evaluation.graph.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import pt.up.fc.dcc.mooshak.evaluation.graph.GraphEvalException;
import pt.up.fc.dcc.mooshak.evaluation.graph.Messenger;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GObject;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyValue;

public class ERDiagramHandler extends DiagramHandler {
	GObject current = null;
	List<Node> nodes;
	List<Edge> edges;
	Edge currentEdge = null;
	int level = 0;
	boolean getName = false;
	boolean isSource = false;
	boolean isAttribute = false;
	boolean isEntity = false;
	boolean isRelationship = false;
	boolean isParticipation = false;
	boolean isObject = false;
	boolean isComposite = false;
	boolean getMultiplicity = false;
	boolean getAssociationType = false;
	String propertyNameKey = null;
	String propertyNameValue = null;
	String propertyValueKey = null;
	String propertyValueValue = null;
	String parameterName = null;
	String typeSpecification = null;
	boolean getBoolean = false;
	CompositePropertyName propertyName = null;
	CompositePropertyValue propertyValue = new CompositePropertyValue();
	EdgeHandler edgeHandler = new EdgeHandler();
	List<EdgeHandler> edgesToAdd = new ArrayList<>();

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

		if (qName.equalsIgnoreCase("dia:boolean")) {
			if (getBoolean) {
				String val = attributes.getValue("val");
				if (val.equals("true"))
					current.setType(current.getType() + " - "
							+ typeSpecification);
				getBoolean = false;
				typeSpecification = null;
			}
		}

		if (qName.equalsIgnoreCase("dia:connections")) {
			handleStartConnections();
		}

		if (qName.equalsIgnoreCase("dia:connection")) {
			handleStartEdge(attributes);
		}

		if (qName.equalsIgnoreCase("dia:attribute")) {
			if (isObject) {
				String name = attributes.getValue("name");
				if (name.equals("name"))
					getName = true;

				if (isEntity) {
					if (name.equals("weak")) {
						typeSpecification = name;
						getBoolean = true;
					}
				}

				else if (isAttribute) {
					if (name.equals("key") || name.equals("weak_key")
							|| name.equals("derived")
							|| name.equals("multivalued")) {
						typeSpecification = name;
						getBoolean = true;
					}
				}

				else if (isParticipation) {
					if (name.equals("total")) {
						typeSpecification = name;
						getBoolean = true;
					}
				}

				else if (isRelationship) {
					if (name.equals("identifying")) {
						typeSpecification = name;
						getBoolean = true;
					}

					else if (name.equals("left_card")
							|| name.equals("right_card")) {
						getMultiplicity = true;
						propertyNameKey = name;
					}
				}
			}
		}

	}

	String putCardinality = null;
	SimplePropertyName pToRemove = null;
	Node nodeToRemove = null;

	private void handleStartEdge(Attributes attributes) {
		String nodeId = attributes.getValue("to");
		String nodeConnector = attributes.getValue("connection");
		if (isSource) {
			edgeHandler.setSource(nodeId, nodeConnector);
			isSource = false;
		}

		else {
			edgeHandler.setTarget(nodeId, nodeConnector);
			isSource = true;
		}
	}

	private void handleStartConnections() {
		isSource = true;
		currentEdge = new Edge(current);
	}

	private void handleStartObject(Attributes attributes) {
		String id = attributes.getValue("id");
		String type = attributes.getValue("type");
		if (type.contains("Entity"))
			isEntity = true;
		else if (type.contains("Relation"))
			isRelationship = true;
		else if (type.contains("Attribute"))
			isAttribute = true;
		else if (type.contains("Part")) {
			isParticipation = true;
			edgeHandler = new EdgeHandler(id, type);
		} else if (type.contains("Standard - ")) {
			new Messenger(8, "Detected unkonwn element: " + type
					+ ". You should use just ER and EER elements' on Dia.");
		}
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
			try {
				handleEndObject();
			} catch (GraphEvalException e) {
				e.printStackTrace();
			}
		}

		level--;
	}

	private void handleEndObject() throws GraphEvalException {
		if (!isParticipation)
			nodes.add(new Node(current));
		else{
			edgeHandler.setType(currentEdge.getType());
			edgesToAdd.add(edgeHandler);
		}
		isObject = false;
		isAttribute = false;
		isEntity = false;
		isRelationship = false;
		isParticipation = false;
	}

	private void handleEndLayer() throws SAXException {
		putEdges();
		graph = new Graph(nodes, edges);
		identifyCompositeAttributes();
		simplifyGraph();
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		if (getName && level == 3) {
			String name = new String(ch, start, length);
			current.setName(name);
			getName = false;
		} else if (getMultiplicity && level == 3) {
			String string = new String(ch, start, length);
			if (!string.equals("##")) {
				string = string.substring(1, string.length() - 1);

				try {
					Integer.parseInt(string);
				} catch (Exception e) {
					if (string.length() == 1)
						string = "many";
				}
				current.addProperty(new SimplePropertyName(propertyNameKey),
						new SimplePropertyValue(string));
			}
			propertyNameKey = null;
			getMultiplicity = false;
		}

	}

	private Node findNodeById(String id) {
		for (Node node : nodes) {
			if (node.getId().equals(id))
				return node;
		}
		return null;
	}

	private void simplifyGraph() {
		Iterator<Node> nodeIterator = graph.getNodes().iterator();
		while (nodeIterator.hasNext()) {
			Node node = nodeIterator.next();
			if (node.getType().contains("ER - Attribute")
					&& !node.getType().contains("Composite")) {
				try {
					changeNodeToProperty(node);
					nodeIterator.remove();
				} catch (GraphEvalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			else if (node.getType().contains("EER - Special"))
				try {
					changeNodeToConnection(node);
					nodeIterator.remove();
				} catch (GraphEvalException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			else if (node.getType().contains("Relationship")) {
				Iterator<PropertyName> iterator = node.getProperties().keySet()
						.iterator();
				while (iterator.hasNext()) {
					PropertyName name = iterator.next();
					if (name.isSimple()) {
						SimplePropertyName simple = (SimplePropertyName) name;
						if (simple.getName().contains("_card"))
							iterator.remove();
					}
				}
			}
		}
		graph.computeNodesDegree();
	}

	private void changeNodeToConnection(Node node) throws GraphEvalException {
		int maxConnections = 1;
		int inDegree = 0;
		for (String type : node.getInDegree().keySet()) {
			inDegree += node.getInDegree().get(type);
		}

		if (inDegree > maxConnections)
			throw new GraphEvalException(
					"One specialization is connected to more than one node");

		int outDegree = 0;
		for (String type : node.getOutDegree().keySet()) {
			outDegree += node.getOutDegree().get(type);
		}

		if (outDegree > maxConnections)
			throw new GraphEvalException(
					"One specialization is connected to more than one node");

		Iterator<Edge> edgeIterator = graph.getEdges().iterator();
		int connectionsRemoved = 0;
		Edge newEdge = null;

		while (edgeIterator.hasNext()) {
			Edge edge = edgeIterator.next();
			Node source = edge.getSource();
			Node target = edge.getTarget();

			if (source.equals(node)) {
				if (connectionsRemoved == 0) {
					newEdge = edge;
				}
				newEdge.setTarget(target);
				edgeIterator.remove();
				connectionsRemoved++;
			}

			else if (target.equals(node)) {
				if (connectionsRemoved == 0) {
					newEdge = edge;
				}
				newEdge.setSource(source);
				edgeIterator.remove();
				connectionsRemoved++;
			}

			if (connectionsRemoved == inDegree + outDegree) {
				break;
			}
		}

		if (newEdge == null)
			throw new GraphEvalException("Error on parsing specialization");
		else {
			newEdge.setType("EER - Specialization");
			graph.getEdges().add(newEdge);
		}
	}

	private void identifyCompositeAttributes() {
		for (Node node : graph.getNodes()) {
			if (node.getType().contains("Attribute")) {
				int countConnections = 0;
				for (String type : node.getInDegree().keySet())
					countConnections += node.getInDegree().get(type);
				for (String type : node.getOutDegree().keySet())
					countConnections += node.getOutDegree().get(type);
				if (countConnections > 2)
					node.setType(node.getType() + " Composite");
			}
		}
	}

	private void changeNodeToProperty(Node node) throws GraphEvalException {

		Iterator<Edge> edgeIterator = graph.getEdges().iterator();
		int connectionsRemoved = 0;
		while (edgeIterator.hasNext()) {
			Edge edge = edgeIterator.next();
			Node source = edge.getSource();
			Node target = edge.getTarget();
			if (source.equals(node)) {
				if (connectionsRemoved == 0) {
					CompositePropertyName name = new CompositePropertyName(
							"Attribute", source.getName());
					CompositePropertyValue value = new CompositePropertyValue(
							"type", source.getType());
					target.addProperty(name, value);
				}
				edgeIterator.remove();
				connectionsRemoved++;
			}

			else if (target.equals(node)) {
				if (connectionsRemoved == 0) {
					CompositePropertyName name = new CompositePropertyName(
							"Attribute", target.getName());
					CompositePropertyValue value = new CompositePropertyValue(
							"type", target.getType());
					source.addProperty(name, value);
				}
				edgeIterator.remove();
				connectionsRemoved++;
			}

			if (connectionsRemoved == 2) {
				break;
			}
		}
	}

	boolean changeSourceTarget;

	private void putEdges() {
		for (EdgeHandler edgeToAdd : edgesToAdd) {
			changeSourceTarget = false;
			Node source = findNodeById(edgeToAdd.getSourceId());
			if (source == null) {
				new Messenger(
						8,
						"You have created a connection without a source or target.\n"
								+ "Make sure you connect every edge to an object");
			}

			Node target = findNodeById(edgeToAdd.getTargetId());
			if (target == null) {
				new Messenger(
						8,
						"You have created a connection without a source or target.\n"
								+ "Make sure you connect every edge to an object");
			}
			String id = edgeToAdd.getId();
			String type = edgeToAdd.getType();
			Edge edge = new Edge(id, type, source, target);
			handleNode(edge, source, edgeToAdd.getSourceConnector(), true);
			handleNode(edge, target, edgeToAdd.getTargetConnector(), false);

			if (changeSourceTarget) {
				Node aux = source;
				edge.setSource(target);
				edge.setTarget(aux);
			}

			edges.add(edge);
			int inDegree = 1;
			int outDegree = 1;
			if (source.getOutDegree().containsKey(type)) {
				outDegree += source.getOutDegree().get(type);
			}

			if (target.getInDegree().containsKey(type)) {
				inDegree += target.getInDegree().get(type);
			}

			source.getOutDegree().put(type, outDegree);
			target.getInDegree().put(type, inDegree);

			if (!edge.getSource().getType().contains("EER - Special")
					&& !edge.getTarget().getType().contains("EER - Special")) {
				Edge reverseEdge = edge.reverse();
				edges.add(reverseEdge);
				inDegree = 1;
				if (source.getInDegree().containsKey(type)) {
					inDegree += source.getInDegree().get(type);
				}

				outDegree = 1;
				if (target.getOutDegree().containsKey(type)) {
					outDegree += target.getOutDegree().get(type);
				}

				source.getInDegree().put(type, inDegree);
				target.getOutDegree().put(type, outDegree);
			}
		}
		
	}

	private void handleNode(Edge edge, Node node, String connector,
			boolean isSource) {

		if (node.getType().contains("Relationship")) {
			if (connector.equals("0") || connector.equals("2")) {
				Iterator<PropertyName> iterator = node.getProperties().keySet()
						.iterator();
				while (iterator.hasNext()) {
					PropertyName p = iterator.next();
					if (p.isSimple()) {
						if (((SimplePropertyName) p).getName().equals(
								"left_card")) {
							edge.addProperty(p, node.getProperties().get(p));
							node.getProperties().remove(p);
							break;
						}
					}
				}
			}

			else if (connector.equals("4") || connector.equals("6")) {
				Iterator<PropertyName> iterator = node.getProperties().keySet()
						.iterator();
				while (iterator.hasNext()) {
					PropertyName p = iterator.next();
					if (p.isSimple()) {
						if (((SimplePropertyName) p).getName().equals(
								"right_card")) {
							edge.addProperty(p, node.getProperties().get(p));
							node.getProperties().remove(p);
							break;
						}
					}
				}
			}

		}

		else if (node.getType().contains("EER - Special")) {
			if (connector.equals("0") && isSource) {
				changeSourceTarget = true;
			}

			else if (connector.equals("1") && !isSource) {
				changeSourceTarget = true;
			}
		}
	}
}

class EdgeHandler {
	String id;
	String type;
	String sourceId;
	String sourceConnector;
	String targetId;
	String targetConnector;
	Map<PropertyName, PropertyValue> properties = new HashMap<>();

	EdgeHandler(String id, String type) {
		this.id = id;
		this.type = type;
	}

	public EdgeHandler() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the sourceId
	 */
	public String getSourceId() {
		return sourceId;
	}

	public void setSource(String sourceId, String sourceConnector) {
		this.sourceId = sourceId;
		this.sourceConnector = sourceConnector;
	}

	/**
	 * @return the targetId
	 */
	public String getTargetId() {
		return targetId;
	}

	public void setTarget(String targetId, String targetConnector) {
		this.targetId = targetId;
		this.targetConnector = targetConnector;
	}

	/**
	 * @return the sourceConnector
	 */
	public String getSourceConnector() {
		return sourceConnector;
	}

	/**
	 * @return the targetConnector
	 */
	public String getTargetConnector() {
		return targetConnector;
	}

	/**
	 * @return the properties
	 */
	public Map<PropertyName, PropertyValue> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<PropertyName, PropertyValue> properties) {
		this.properties = properties;
	}

}
