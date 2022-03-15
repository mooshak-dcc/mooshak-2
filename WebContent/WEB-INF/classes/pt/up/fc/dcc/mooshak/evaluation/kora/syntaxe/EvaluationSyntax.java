package pt.up.fc.dcc.mooshak.evaluation.kora.syntaxe;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.kora.ParseInfo;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Connects;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Containers;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Diagram;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.NodeTypes;

/**
 * 
 * @author hcorreia
 *
 */



public class EvaluationSyntax {
	private List<Node> nodes ;
	private List<Edge> edges ;
	public enum Lang { EN, PT };
	public Lang lang = Lang.EN;
	private ArrayList <List<Node>> graphComponent;
	protected JSONObject jsonFeedback;
	protected String textFeedback;
	protected ParseInfo createElementsTemporary;
	protected Diagram diagram;
	protected Map<String, Containers> rulesContainers; 
	private boolean hasErrorLabelContainer;
	
	private Graph graph;
	NodeTypes nodeTypes;
	
	public EvaluationSyntax(Graph graph, Diagram diagram) {
		this.setGraph(graph);
		this.setNodes(graph.getNodes());
		this.setEdges(graph.getEdges()) ;
		jsonFeedback = new JSONObject();
		textFeedback="";
		this.diagram=diagram;
		//this.nodeTypes = diagram.getNodeTypes();
		createElementsTemporary = new ParseInfo();
		this.rulesContainers=this.diagram.getContainerRules();
		this.hasErrorLabelContainer=false;
		
	}
	
	/**
	 * 
	 * @return true if number components of graph is different of expected
	 * 
	 */
	
	public boolean isErrorComponentGraph(){
		ArrayList <List<Node>> componentsList = new ArrayList<List<Node>>();
		if(this.nodes.isEmpty() ) return false ;
		
		List<Edge> edgesAdJ=this.edges;
		List<Node> nodesWhite = this.nodes;
		List<Node> nodesGray = new LinkedList<Node>();
		
		while(!nodesWhite.isEmpty()){
		List<Node> nodesBlack = new LinkedList<Node>();
		nodesGray.add(nodesWhite.remove(0));
			while(!nodesGray.isEmpty()){
				Node n = nodesGray.remove(0);
				for(Edge e :edgesAdJ){
					if(e.getSource().equals(n) ){
						if(!nodesBlack.contains(e.getTarget()))
							nodesGray.add(e.getTarget());
					}
					else if(e.getTarget().equals(n)){
						if(!nodesBlack.contains(e.getSource()))
							nodesGray.add(e.getSource());
					}
				}
				nodesBlack.add(n);
				nodesWhite.remove(n);
			}
		componentsList.add(nodesBlack);
		}
		
		if(componentsList.size()==1)
			return false;
			
		setFeedbackComponentGraph(componentsList);	
		return true;
		
	}
	
	/**
	 * 
	 * @return List elements with error in label 
	 */
	public boolean isNodesValidLabel(){
		String text="";
		JSONArray jsonNodes = new JSONArray();
		
		for(Node node : this.nodes ){
			if(node.isDisconnected()){
				text+=" "+node.getName() +"("+node.getType()+")";
				jsonNodes.put(createElementsTemporary.createJsonNodeTemporary(node,"syntaxe"));
				
			}
		}
		if(text.equals(""))
			return false;
		return false;
	}
	
	/**
	 * 
	 * @return List with the isolated nodes in the graph
	 */
	public boolean isNodesDisconnected(){
		JSONArray jsonNodes = new JSONArray();
		String text="";
		for(Node node : this.nodes ){
			if(node.isDisconnected()){
				text+=node.getName() +"("+node.getType()+")";
				jsonNodes.put(createElementsTemporary.createJsonNodeTemporary(node,"syntaxe"));
				
			}
		}
		if(text.equals(""))
			return false;
		
		 this.textFeedback = getStringMessage(0,"nodeDisconnected",jsonNodes.length(),text);
		 this.jsonFeedback.put("nodes", jsonNodes);
		 this.jsonFeedback.put("edges", new JSONArray());
		 
		return true;
	} 
	
	/**
	 * 
	 * @return boolean 
	 */
	public boolean isErrorSytaxeValidationEdges(){
		Map<String, Connects> syntaxeNodeRules=this.diagram.getConnectRuleConnects();
		JSONArray jsonEdges = new JSONArray();
		this.textFeedback="";
		
		for(Edge edge: this.edges){
			String sourcetype =edge.getSource().getType();
			String targetType =edge.getTarget().getType();
			if(!syntaxeNodeRules.get(sourcetype).isConnectionValid(edge.getType(),targetType)){
				this.textFeedback += getStringMessage(0,"invalidConnection",edge.getType(),edge.getSource().getName(), 
						sourcetype, edge.getTarget().getName(),targetType) +"@";
				jsonEdges.put(createElementsTemporary.createJsonEdgeTemporary(edge,"syntaxe"));
			}
		}
		if(this.textFeedback.equals(""))
			return false;
		this.jsonFeedback.put("nodes", new JSONArray());
		this.jsonFeedback.put("edges",jsonEdges );
		return true;
	}
	
	/**
	 * 
	 * @return boolean
	 */
	public boolean isErrorSytaxeInvalidDegreeNode(){

		this.textFeedback="";
		JSONArray jsonNodes = new JSONArray();
		for(Node node: this.nodes){
			if(!this.diagram.isDegreeValid(node.getType(), node.getTotalDegreeIn(),node.getTotalDegreeOut())){
				this.textFeedback += getStringMessage(0,"invalidDegree",node.getName(), node.getType(),node.getTotalDegreeInOut()) +"@";
				jsonNodes.put(createElementsTemporary.createJsonNodeTemporary(node,"sintaxe"));
			}
		
		}
		if(textFeedback.equals(""))
			return false;
		 this.jsonFeedback.put("nodes", jsonNodes);
		 this.jsonFeedback.put("edges", new JSONArray());
		 return true;
	}
	
	
	public boolean isErrorSytaxeInvalidTypeConnection(){
		Map<String, Connects> syntaxeNodeRules=this.diagram.getConnectRuleConnects();

		this.textFeedback="";
		JSONArray jsonNodes = new JSONArray();
		for(Node node: this.nodes){
			
			Map<String, Integer> degreeList = node.getlDegreeInOut();
			for(Entry<String, Integer> n:degreeList.entrySet()){
				if(!(syntaxeNodeRules.get(node.getType()).isConnectionValid(n.getKey(),n.getValue()))){
					this.textFeedback += getStringMessage(0,"invalidTypeConnection",node.getType(), node.getName()) +"@";
					jsonNodes.put(createElementsTemporary.createJsonNodeTemporary(node,"sintaxe"));
				}
			} 
		}
		if(textFeedback.equals(""))
			return false;
		 this.jsonFeedback.put("nodes", jsonNodes);
		 this.jsonFeedback.put("edges", new JSONArray());
		 return true;
	}
	
	
	
	public boolean isErrorLabelContainer(JSONArray nodesPar){
		
		this.textFeedback="";
		for (int i = 0; i < nodesPar.length(); i++) {
			JSONObject node = nodesPar.getJSONObject(i);
			if(node.has("containers"))
				validLabelContainer(node);
		}
		return !textFeedback.equals("");
	}
	
	public boolean isExtensionValid(String solutionFile, String attemptFile){
		if(solutionFile.equals(attemptFile))
			return false;
		
		this.textFeedback=SummaryFormat.INVALID_EXTENSION.format(lang )+"\n ";
		this.textFeedback=getStringMessage(0,"invalidExtension",attemptFile,solutionFile);
		
		 this.jsonFeedback.put("nodes", new JSONArray());
		 this.jsonFeedback.put("edges", new JSONArray());
		return true;
	}
	public void validLabelContainer(JSONObject jsonNodeParam){	
		JSONArray nodeContainers = jsonNodeParam.getJSONArray("containers");
		Containers rule =this.rulesContainers.get(jsonNodeParam.getString("type"));
		
		for(int i = 0; i < nodeContainers.length(); i++) {
			JSONObject containers = nodeContainers.getJSONObject(i);

			String name1=containers.getString("name");
			String regex="";
			regex =rule.getContainer(name1).getPattern();
			String stringValue = removeCharExtreme(containers.get("value").toString());
			stringValue.replaceAll("\\s+","");
			String[] strArray=stringValue.split(",");
		    Pattern r = Pattern.compile(regex); // Create a Pattern object

		     for(String s: strArray){
				s=removeCharExtreme(s);
				if(!s.equals("")){
					Matcher m = r.matcher(s);   // Now create matcher object.
					if((!m.matches())){
						int id =Math.abs(jsonNodeParam.getInt("id"));
						Node node =getNodeById(id +"");
						this.textFeedback+=getStringMessage(0,"invalidContainer",node.getType(),node.getName(),name1,s) +"@";
						setErrorLabelContainer(true);
					}
				}
			 }
		}
		
	}
	
	private Node getNodeById( String id) {
		for (Node node : this.nodes) {
			if (node.getId().equals(id) || node.getId().equals("-"+id))
				return node;
		}
		return null;
	}
	
	private String removeCharExtreme(String string){
		if(string.length()<2) return "";
		int stringValueLen=string.length();
		return (String) string.subSequence(1, stringValueLen-1);
	}
	/**
	 * 
	 * @return true if an occurred error in validation syntax  
	 */
	public boolean isEvaluationError(String extension){  // Ao adicionar tipo de avaliação no ficheiro configuração eliminar essa parte
//		System.out.println(this.nodes);
//		if(this.nodes.size()>0) return true;
		if(extension.equals("eer"))
			if(isNodesDisconnected() ||
					isErrorSytaxeInvalidDegreeNode() ||
					isErrorSytaxeInvalidTypeConnection() ||
					isErrorSytaxeValidationEdges() ||
			 		isErrorComponentGraph()) //alterar o ficheiro confifuração antes de usar
				return true;
		else if(extension.equals("umlcl")){
			if(this.nodes.size()>0) return true;
			if(isNodesDisconnected() ||
					isErrorSytaxeInvalidDegreeNode() ||
					isErrorSytaxeInvalidTypeConnection() ||
					isErrorSytaxeValidationEdges() )
				return true;
		}
		
		else if(extension.equals("umluc")){
			if(isNodesDisconnected() ||
					isErrorSytaxeInvalidDegreeNode() ||
					isErrorSytaxeInvalidTypeConnection() ||
					isErrorSytaxeValidationEdges() )
				return true;
		}
		return false;
	}

	/**
	 * set the feedback when occurred error with number of component 
	 * @param componentsList
	 */
	public void setFeedbackComponentGraph(ArrayList <List<Node>> componentsList){
		String text="";
//		if(lang.equals("EN"))
//		 text = "Invalid Number of Component :\n";
//		else
//			text="Diagrama com componentes desconexos, diagrama EER é um grafo conexo \n";
		int i = 1;
		for (List<Node> list : componentsList) {
				text += "Component " + i++ + " : ";
			for (Node node : list) {
					text = text + node.getName() + "(type: " + node.getType() + "), ";
			}
			text+="\n";
		}
		
//		 textFeedback=text;
		 this.textFeedback = getStringMessage(0,"invalidNumberComponent",text) ;
		 jsonFeedback.put("nodes", new JSONArray());
		 jsonFeedback.put("edges", new JSONArray());
		 
	}
	
	
	public JSONObject getJsonFeedback() {
		return jsonFeedback;
	}


	public void setJsonFeedback(JSONObject jsonFeedback) {
		this.jsonFeedback = jsonFeedback;
	}


	public String getTextFeedback() {
		return textFeedback;
	}


	public void setTextFeedback(String textFeedback) {
		this.textFeedback = textFeedback;
	}
	
	public List<Node> getNodes() {
		return nodes;
	}
	
	public void setNodes(List<Node> nodes) {
		this.nodes = nodes;
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = edges;
	}

	public Graph getGraph() {
		return graph;
	}


	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * @return the graphComponent
	 */
	public ArrayList <List<Node>> getGraphComponent() {
		return graphComponent;
	}

	/**
	 * @param graphComponent the graphComponent to set
	 */
	public void setGraphComponent(ArrayList <List<Node>> graphComponent) {
		this.graphComponent = graphComponent;
	}
	
	public boolean isErrorLabelContainer() {
		return hasErrorLabelContainer;
	}

	public void setErrorLabelContainer(boolean isErrorLabelContainer) {
		this.hasErrorLabelContainer = isErrorLabelContainer;
	}

	enum SummaryFormat{
		
		NODE_DISCONNECTED(
				"<b>%d</b> node(s) is/are disconnected: <b>%s</b>",
				"<b>%d</b> nó(s) está(ão) desconectado(s): <b>%s</b>"
				),	
		INVALID_DEGREE_IN_OUT(
				"A node (name: <b>%s</b>, type: <b>%s</b> ) has wrong number of degree in/out: <b>%d</b>",
				"um nó (nome: <b>%s</b>, tipo: <b>%s</b> ) tem número de ligações inválido: <b>%d</b>"
				),
		INVALID_CONNECTION(
				"Connection invalid. Edge type: <b>%s</b> connect to source: name - %s type - <b>%s</b>, target: name- <b>%s</b>, type: <b>%s</b>",
				"Ligação inválida. Aresta do tipo:<b>%s</b>, origem: nome - <b>%s</b>, tipo <b>%s</b>, destino: nome - <b>%s</b>, tipo <b>%s</b>"
				),
		INVALID_TYPE_CONNECTED(
				"A Node from type: <b>%s</b>, name: <b>%s</b> has invalid number of links ",
				"Um nó (nome: <b>%s</b>, tipo: <b>%s</b> ) tem o grau de entrada/saída errado."
				
				),
		
		INVALID_CONTAINER(
				"A Node from type: <b>%s</b>, name: <b>%s</b> has invalid Property <b>%s</b> : <b>%s</b> ",
				"Um nó do tipo: <b>%s</b>, nome: <b>%s</b> tem a propriedade <b>%s</b>: <b>%s</b> inválida"
				),
		
		INVALID_EXTENSION(
				"The type diagram attempt is different of type diagram solution",
				"O tipo de diagrama tentativa é diferente do tipo de diagrama solução"
				);
		
		String en,pt;
		
		SummaryFormat(String en,String pt){
			this.en = en;
			this.pt = pt;
		}
		
		public String format(Lang lang,Object...values) {
			String format;
			
			switch(lang) {
			case EN:
				format = en;
				break;
			case PT:
				format = pt;
				break;
			default:
				throw new InvalidParameterException("Unknown language:"+lang);
			}
			
			return String.format(format,values);
		}

		
	}
	
	private  String getStringMessage(int type, String typeMessage, Object...args){
		 String message="";
		 for(Object s : args)
			 message+=";"+s;
		return type+";"+typeMessage+message;
		
	}
	
}

