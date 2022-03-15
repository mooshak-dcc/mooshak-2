package pt.up.fc.dcc.mooshak.evaluation.graph.parse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Containers;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Diagram;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Field;
import pt.up.fc.dcc.mooshak.evaluation.kora.syntaxe.EvaluationSyntax;

public class JSONHandler {
	public Graph graph;
	private InputStream inputStream;
	protected Diagram diagram;
	int idFactor;
	List<String> reduciblesList;
	List<Node> nodesList;
	List<Edge> edgesList;
	List<Node> nodesLabelError;
	JSONObject eshuDiagram ;
	JSONArray reducibles;
	String graphJson;
	private EvaluationSyntax evaluationSyntax;
	
	

	public JSONHandler(InputStream inputStream, Diagram diagram) {
		this.inputStream = inputStream;
		this.graph = null;
		this.idFactor = 0;
		this.graphJson="";
		this.diagram=diagram;
	}
	
	public JSONHandler(InputStream inputStream, int idFactor,Diagram diagram) {
		this.inputStream = inputStream;
		this.graph = null;
		this.idFactor = idFactor;
		this.graphJson="";
		this.diagram=diagram;
	}

	public Graph parseReducible() throws JSONException {
		
		if (graphJson.equals("")) {
			Scanner sc = new Scanner(inputStream);

			while (sc.hasNext()) {
				graphJson += sc.next();
			}
			sc.close();
		}
		
		JSONObject eshuDiagram = new JSONObject(graphJson);
		JSONArray reducibles = getJSONArray(eshuDiagram, "reducibles");
		JSONArray nodes = getJSONArray(eshuDiagram, "nodes");
		JSONArray edges = getJSONArray(eshuDiagram, "edges");
		List<String> reduciblesList = getReducibles(reducibles);
		List<Node> nodesList = getNodes(nodes);
		List<Edge> edgesList = getEdges(edges, nodesList);
		this.graph = new Graph(nodesList, edgesList);
		simplifyGraph(reduciblesList);
		return this.graph;
		
	}
	
	public Graph parseReducible(List<String> reduciblesList,List<Node> nodesList,List<Edge> edgesList ) throws JSONException {
	
		this.graph = new Graph(nodesList, edgesList);
//		simplifyGraph(reduciblesList);
		return this.graph;
	}
	
	
	public Graph parse() throws JSONException {
		Scanner sc = new Scanner(inputStream);
		String json = "";
		while (sc.hasNext()) {
			json += sc.next();
		}
		sc.close();
		graphJson=json;
		try {
			this.eshuDiagram = new JSONObject(json);
		} catch (Exception e) {
			if(this.idFactor==1)
				System.err.println("ERROR IN PARSE JSON TO DIAGRAM  ATTEMPET "+" "+e.getMessage());
			else
				System.err.println("ERROR IN PARSE JSON TO DIAGRAM  SOLUTION "+" "+e.getMessage());
		}
		
		JSONArray reducibles = getJSONArray(eshuDiagram, "reducibles");
		JSONArray nodes = getJSONArray(eshuDiagram, "nodes");
		JSONArray edges = getJSONArray(eshuDiagram, "edges");
		this.reduciblesList = getReducibles(reducibles);
		this.nodesList = getNodes(nodes);
		this.edgesList = getEdgesSimple(edges, this.nodesList);
		this.graph = new Graph(this.nodesList, this.edgesList);
		evaluationSyntax = new EvaluationSyntax(this.graph, diagram);
		evaluationSyntax.isErrorLabelContainer(nodes);
//		System.out.println("----------------");
//		System.out.println(this.graph);
//		System.out.println("---------------*****");
		return this.graph;
	}
	
	public boolean isErrorSyntaxe(){
		return evaluationSyntax.isEvaluationError(diagram.getExtension());
	}
	private void simplifyGraph(List<String> reduciblesList) {
		Iterator<Node> itNodes = this.graph.getNodes().iterator();
		
		while(itNodes.hasNext()){
			Node node = itNodes.next();
			String mainType = node.getType().split(" ")[0];
//			System.out.println();
//			
			
//			
			if(reduciblesList.contains(mainType)){
				int countConnections = 0;
				
				for (String type : node.getInDegree().keySet())
					countConnections += node.getInDegree().get(type);
					
				for (String type : node.getOutDegree().keySet())
					countConnections += node.getOutDegree().get(type);
				
				if (countConnections > 1) {
					node.setType(node.getType() + " - Composite");
				} else if(countConnections == 1){
					Iterator<Edge> itEdges = this.graph.getEdges().iterator();
//					System.out.println(node + " \nTEST "+ node.getInDegree().get(type));
					int removedEdges = 0;
					while(itEdges.hasNext()){
						Edge edge = itEdges.next();
						if(edge.getSource().equals(node)) {
							
							CompositePropertyName name = new CompositePropertyName(mainType, node.getId());
							CompositePropertyValue value = new CompositePropertyValue("type", node.getType());
							value.addInfo("id",node.getId());
							value.addInfo("name", node.getName());
							
							if(removedEdges == 0 && !edge.getTarget().getType().equals("Relationship")){
								edge.getTarget().addProperty(name, value);
							}
							removedEdges++;
							itEdges.remove();
//							System.out.println("AWAW "+ edge.getTarget());
						} else if(edge.getTarget().equals(node)) {
							CompositePropertyName name = new CompositePropertyName(mainType, node.getId());
							CompositePropertyValue value = new CompositePropertyValue("type", node.getType());
							value.addInfo("name", node.getName());
							value.addInfo("id",node.getId());
							if(removedEdges == 0){
								edge.getSource().addProperty(name, value);
							}
							removedEdges++;
							itEdges.remove();
						}
						
						if(removedEdges == 1) {
							break;
						}
					}
					itNodes.remove();
				}
			}
		}
		
	}

	private List<String> getReducibles(JSONArray reducibles) throws JSONException {
		List<String> reduciblesList = new ArrayList<>();
		for(int i = 0; i < reducibles.length(); i++) {
			reduciblesList.add(reducibles.getString(i));
		}
		return reduciblesList;
	}

	private List<Node> getNodes(JSONArray nodes) throws JSONException {
		List<Node> nodesList = new ArrayList<>();
		for (int i = 0; i < nodes.length(); i++) {
			JSONObject jsonNode = nodes.getJSONObject(i);
			Node node = new Node();
			node.setId(String.valueOf(idFactor * jsonNode.getInt("id")));
			node.setType(jsonNode.getString("type"));
			node.setName(jsonNode.getString("label"));
			JSONArray features = getJSONArray(jsonNode, "features");
			node.setProperties(getFeatures(node, features));
//			node.setProperties(getContainers(node, getJSONArray(jsonNode, "containers")));
			if(jsonNode.has("containers") && !jsonNode.get("containers").toString().equals("[]"))
				node.setProperties(getContainers(node, jsonNode));
			nodesList.add(node);
		}
		return nodesList;
	}
	
	private List<Edge> getEdges(JSONArray edges, List<Node> nodesList) throws JSONException {
		List<Edge> edgesList = new ArrayList<>();
		for (int i = 0; i < edges.length(); i++) {
			JSONObject jsonEdge = edges.getJSONObject(i);
			Edge edge = new Edge();
			edge.setId(String.valueOf(idFactor * jsonEdge.getInt("id")));
			edge.setType(jsonEdge.getString("type"));
			edge.setSource(getNodeById(nodesList, String.valueOf(idFactor * jsonEdge.getInt("source"))));
			edge.setTarget(getNodeById(nodesList, String.valueOf(idFactor * jsonEdge.getInt("target"))));
			JSONArray features = getJSONArray(jsonEdge, "features");
			JSONArray featuresSource = getJSONArray(jsonEdge, "featuresSource");
			JSONArray featuresTarget = getJSONArray(jsonEdge, "featuresTarget");
			
			edge.setProperties(getFeatures(edge, features)); 
			edge.addProperties(getFeaturesSource(edge,featuresSource));
			edge.addProperties(getFeaturesTarget(edge,featuresTarget));
			edgesList.add(edge);
			/*System.out.println("***************");
			System.out.println(edge);
			System.out.println("---------------------------");
			if( jsonEdge.getString("type").equals("Line")){
				
//				System.out.println(edge.getType());
				Edge reverseEdge = edge.reverse();
				edgesList.add(reverseEdge);
			}*/
		}
		return edgesList;
	}

		
	private List<Edge> getEdgesSimple(JSONArray edges, List<Node> nodesList) throws JSONException {
		List<Edge> edgesList = new ArrayList<>();
		
		for (int i = 0; i < edges.length(); i++) {
			JSONObject jsonEdge = edges.getJSONObject(i);
			Edge edge = new Edge();
			edge.setId(String.valueOf(idFactor * jsonEdge.getInt("id")));
			edge.setType(jsonEdge.getString("type"));
			edge.setSource(getNodeById(nodesList, String.valueOf(idFactor * jsonEdge.getInt("source"))));
			edge.setTarget(getNodeById(nodesList, String.valueOf(idFactor * jsonEdge.getInt("target"))));
			JSONArray features = getJSONArray(jsonEdge, "features");
			edge.setProperties(getFeatures(edge, features)); 
			edgesList.add(edge);
		}
		return edgesList;
	}

	private Map<PropertyName, PropertyValue> getFeatures(GObject object, JSONArray features) throws JSONException {
		
		Map<PropertyName, PropertyValue> propertiesMap = new HashMap<>();
		for(int i = 0; i < features.length(); i++) {
			JSONObject feature = features.getJSONObject(i);
			SimplePropertyName name = new SimplePropertyName(feature.getString("name"));
			SimplePropertyValue value = new SimplePropertyValue(String.valueOf(feature.get("value")).toLowerCase());
			
			if(value.getValue().equalsIgnoreCase("true")) {
				object.setType(object.getType() + " " + name.getName());
			}
			if(name.getName().equals("cardinalitySource") || name.getName().equals("cardinalityTarget")){
				value.setValue(sanitizationCardinality(value.getValue()));
			}
				
			propertiesMap.put(name, value);
		
		}
		return propertiesMap;
	}
	
	
private Map<PropertyName, PropertyValue> getFeaturesSource(Edge edge, JSONArray features) throws JSONException {
		
		Map<PropertyName, PropertyValue> propertiesMap = new HashMap<>();
		for(int i = 0; i < features.length(); i++) {
			JSONObject feature = features.getJSONObject(i);
			
			CompositePropertyName name;
			CompositePropertyValue value;
			
			name = new CompositePropertyName("cardinality","cardinality",edge.getSource());
			value= new CompositePropertyValue("value", sanitizationCardinality(feature.getString("value")));
			
			/*if(value.getValue().equalsIgnoreCase("true")) {
				edge.setType(edge.getType() + " " + name.getName());
			}*/
			
			
				
			propertiesMap.put(name, value);
		
//			System.out.println("**********AQUI***********");
//			System.out.println(propertiesMap);
//			System.out.println();
		}
		return propertiesMap;
	}



private Map<PropertyName, PropertyValue> getFeaturesTarget(Edge edge, JSONArray features) throws JSONException {
	
	Map<PropertyName, PropertyValue> propertiesMap = new HashMap<>();
	
	for(int i = 0; i < features.length(); i++) {
		JSONObject feature = features.getJSONObject(i);
		
		propertiesMap.put(//feature.getString("name")
				new CompositePropertyName("cardinality","cardinality",edge.getTarget()), //feature.getString("name")
				new CompositePropertyValue("value", sanitizationCardinality(feature.getString("value"))));
	}
	return propertiesMap;
}
	
	private String sanitizationCardinality(String value) {
		if(value.equals("0")) return "0";
		if(value.equals("1")) return "1";
		if(value.length()==1 )
			return "M";
		return value;
	}

	private Map<PropertyName, PropertyValue> getContainers(GObject object, JSONObject jsonNodeParam) throws JSONException {
		Map<PropertyName, PropertyValue> propertiesMap = new HashMap<>();
		JSONArray jsonNode = getJSONArray(jsonNodeParam, "containers");
		if(this.diagram==null)
			return propertiesMap;
		Containers rule =this.diagram.getContainerRules().get(jsonNodeParam.getString("type"));
		if(rule==null)
			return propertiesMap;
		for(int i = 0; i < jsonNode.length(); i++) {
			JSONObject containers = jsonNode.getJSONObject(i);
			String containerName=containers.getString("name");
			
			CompositePropertyName name = new CompositePropertyName(jsonNodeParam.getString("label"),containerName);
			CompositePropertyValue values= new CompositePropertyValue();
			
			
			
			String regex="";
			regex =rule.getContainer(containerName).getPattern();
//			System.out.println("********** regex " +regex);
			String stringValue = removeCharExtreme(containers.get("value").toString());
			stringValue.replaceAll("\\s+","");
			stringValue.toLowerCase(); // verificar depois
			String[] strArray=stringValue.split(",");
		    Pattern r = Pattern.compile(regex); // Create a Pattern object
		     
		    for(String s: strArray){
				s=removeCharExtreme(s);
				if(!s.equals("")){
					Matcher m = r.matcher(s);   // Now create matcher object.
					if((m.matches())){
						 List<Field> fields = rule.getContainer(containerName).getField();
				         for(Field field:  fields){
				        	 if(field.getValue().equals("0"))
				 		    	continue;
				        	values.add(field.getName(), m.group(Integer.parseInt(field.getValue())).toLowerCase());
//				        	System.out.println(field.getName() +" "+  m.group(Integer.parseInt(field.getValue())));
				         }
					}
				}
			 }
		    if(name.getName().equals("Responsibility")) // remover ao adicionar no ficheiro config
				continue;
			propertiesMap.put(name, values);
//			System.out.println("the name "+name +" "+values );
		}
		return propertiesMap;
	}
	

	private String removeCharExtreme(String string){
		if(string.length()<2) return "";
		int stringValueLen=string.length();
		return (String) string.subSequence(1, stringValueLen-1);
	}
	
	
	private Node getNodeById(List<Node> nodesList, String id) {
		for (Node node : nodesList) {
			if (node.getId().equals(id))
				return node;
		}
		return null;
	}
	public EvaluationSyntax getEvaluationSyntax() {
		return evaluationSyntax;
	}

	public void setEvaluationSyntax(EvaluationSyntax evaluationSyntax) {
		this.evaluationSyntax = evaluationSyntax;
	}
	
	private JSONArray getJSONArray(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getJSONArray(key);
		} catch (JSONException e) {
			return new JSONArray();
		}
	}
}
