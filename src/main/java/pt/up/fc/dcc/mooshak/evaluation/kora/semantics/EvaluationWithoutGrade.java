/**
 * @author Helder Correia
 */

package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Diagram;

/**
 * @author Helder Correia
 *
 */
public class EvaluationWithoutGrade extends GenericEvaluation{
	
	
	protected JSONArray jsonNodes=new JSONArray();
	protected JSONArray jsonEdges=new JSONArray();
	protected JSONObject jsonFeedback = new JSONObject();
	private DifferenceHandler differences;
	public enum Lang { EN, PT };
	public Lang lang = Lang.PT;
	
	Map<Node, Double> mapNodesMaxValue = null;
	
	int totalSolutionNodes = 0;
	int totalSolutionEdges = 0;
	Map<String, Integer> solutionNodesByType = new HashMap<>();
	Map<String, Integer> attemptNodesByType = new HashMap<>();
	Map<String, Integer> solutionEdgesByType = new HashMap<>();
	Map<String, Integer> attemptEdgesByType = new HashMap<>();
	
	private List<JSONObject> nodes= new LinkedList<JSONObject>();
	private List<JSONObject> edges= new LinkedList<JSONObject>();
	private Map<String, List<String>> urlMap;
	
	public EvaluationWithoutGrade(Graph solution,Graph attempt,Evaluation evaluation,Diagram diagram) throws JSONException {
		super(solution,attempt,evaluation);
		urlMap =diagram.getUrlMap();
		this.setDifferences(new DifferenceHandler());
		handleIncompleteEEREvaluation();
		this.jsonFeedback.put("nodes", this.jsonNodes);
		this.jsonFeedback.put("edges", this.jsonEdges);
		generateTextFeedback();
		
		
	}

	
	private void handleIncompleteEEREvaluation() throws JSONException {
		

		// Map with each attempt node and max comparison value - initialization
		if (getAttempt().getNodes().size() > getSolution().getNodes().size()) {
			mapNodesMaxValue = new HashMap<>();
			for (Node attemptNode : getAttempt().getNodes()) {
				double maxValue = attemptNode.getMaxComparisonValue(getSolution().getNodes());
				mapNodesMaxValue.put(attemptNode, maxValue);
			}
		}

		 totalSolutionNodes =getNumberSolutionNodesByType();
		 totalSolutionEdges = getNumberSolutionEdgesByType();
		 
		nodesTypesAdd();
		nodesTypesRemove();

		edgesTypeAddRemove();
		
	}
	
	
	private void nodesTypesRemove() throws JSONException{
		for (String type : attemptNodesByType.keySet()) {
			if (!solutionNodesByType.containsKey(type)) {
				writeJsonNodesDifferences(type, attemptNodesByType.get(type), "delete");
			}
		}

	}
	private void nodesTypesAdd() throws JSONException{
		
		
		for (String type : solutionNodesByType.keySet()) {
			
			if (attemptNodesByType.containsKey(type)) {
				int diff = solutionNodesByType.get(type) - attemptNodesByType.get(type);
				if (diff > 0) {
					writeJsonNodesDifferences(type, diff, "insert");
				}
				else if(diff<0){
					writeJsonNodesDifferences(type, -diff, "delete");
				}
			
			}
		
		
		}
		
		for (String type : solutionNodesByType.keySet()) {
//			if (type.equals("attribute"))
			if (attemptNodesByType.containsKey(type)) {
				int diff = solutionNodesByType.get(type) - attemptNodesByType.get(type);
				if (diff > 0) {
					writeJsonNodesDifferences(type, diff, "insert");
				}

				else if (diff < 0) {
					writeJsonNodesDifferences(type, -diff, "delete");
				}
			} else {
					writeJsonNodesDifferences(type, solutionNodesByType.get(type), "insert");
			}
		}
	}
	
	private void edgesTypeAddRemove() throws JSONException{
		for (String type : solutionEdgesByType.keySet()) {
			if (!type.contains("Spec")) {
				if (attemptEdgesByType.containsKey(type)) {

					int diff = solutionEdgesByType.get(type) - attemptEdgesByType.get(type);
					if (diff > 0) {
						writeJsonEdgesDifferences(type, diff , "insert");
					}

					else if (diff < 0) {
						writeJsonEdgesDifferences(type, -diff , "delete");
					}
				} else {
					writeJsonEdgesDifferences(type, solutionEdgesByType.get(type), "insert");
				}
			}
		}

		for (String type : attemptEdgesByType.keySet()) {
			if (!solutionEdgesByType.containsKey(type) && !type.contains("Spec")) {
				writeJsonEdgesDifferences(type, attemptEdgesByType.get(type) / 2, "delete");
			}
		}
	}
	
	public int getNumberSolutionEdgesByType(){
		int totalSolutionEdges=0;
		for (Edge edge : solution.getEdges()) {
			totalSolutionEdges++;
			int thisType = 1;
			if (solutionEdgesByType.containsKey(edge.getType()))
				thisType += solutionEdgesByType.get(edge.getType());
			solutionEdgesByType.put(edge.getType(), thisType);
		}
		
		for (Edge edge : attempt.getEdges()) {
			int thisType = 1;
			if (attemptEdgesByType.containsKey(edge.getType()))
				thisType += attemptEdgesByType.get(edge.getType());
			attemptEdgesByType.put(edge.getType(), thisType);
		}
		
		
		return totalSolutionEdges;
	}
	
	
	public int getNumberSolutionNodesByType(){
		int totalNodes=0;
		for (Node node : solution.getNodes()) {
			totalNodes ++;
			int thisType = 1;
			if (solutionNodesByType.containsKey(node.getType()))
				thisType += solutionNodesByType.get(node.getType());
			solutionNodesByType.put(node.getType(), thisType);
		}
		
		for (Node node : attempt.getNodes()) {
			int thisType = 1;
			if (attemptNodesByType.containsKey(node.getType()))
				thisType += attemptNodesByType.get(node.getType());
			attemptNodesByType.put(node.getType(), thisType);
		}
		
		return  totalNodes;
		
	}
	
	private void writeJsonEdgesDifferences(String fullType, int size, String temporaryType) throws JSONException {
		String types[] = fullType.split(" ");
		for (int i = 0; i < size; i++) {
			JSONObject edgetemporary = new JSONObject();
			edgetemporary.put("type", types[0]);
			JSONArray properties = new JSONArray();
			for (int j = 1; j < types.length; j++) {
				JSONObject property = new JSONObject();

				// property.put(types[j], true);
				property.put("name", types[j]);
				property.put("value", true);

				properties.put(property);
			}
			edgetemporary.put("temporary", temporaryType);
			edgetemporary.put("features", properties);
			//createEdgeInfo(edgetemporary );
			this.edges.add(edgetemporary);
			this.jsonEdges.put(edgetemporary);
		}

	}

	private void writeJsonNodesDifferences(String fullType, int size, String temporaryType) throws JSONException {
		String types[] = fullType.split(" ");
		for (int i = 0; i < size; i++) {
			Node nodeToRemove = null;
			if (temporaryType.equals("delete")) {
				nodeToRemove = getPossibleNodeToRemove(types[0]);
			}

			JSONObject nodetemporary = new JSONObject();
			
			if (nodeToRemove != null) {
				nodetemporary.put("label", nodeToRemove.getName());
				nodetemporary.put("id", Integer.parseInt(nodeToRemove.getId()));
			}

			nodetemporary.put("type", types[0]);
			JSONArray properties = new JSONArray();
			for (int j = 1; j < types.length; j++) {
				JSONObject property = new JSONObject();

				// property.put(types[j], true);
				property.put("name", types[j]);
				property.put("value", true);

				properties.put(property);
			}
			nodetemporary.put("temporary", temporaryType);
			nodetemporary.put("features", properties);
			//createNodeInfo(nodetemporary);
			this.nodes.add(nodetemporary);
			this.jsonNodes.put(nodetemporary);
			
			
		}

	}

	private Node getPossibleNodeToRemove(String type) {
		if (mapNodesMaxValue != null) {
			Iterator<Node> itAttemptNodes = mapNodesMaxValue.keySet().iterator();
			while (itAttemptNodes.hasNext()) {
				Node node = itAttemptNodes.next();
				// Check if it has the same type of the nodes to remove
				if (type.equals(node.getType().split(" ")[0])) {
					double maxValue = mapNodesMaxValue.get(node);
					if (maxValue < 0.3) {
						itAttemptNodes.remove();
						return node;
					}
				}
			}
		}
		return null;
	}

	




	public JSONObject getJsonFeedback() {
		return this.jsonFeedback;
	}
	
//	public void createNodeInfo(JSONObject nodetemporary ){
//		String type =nodetemporary.getString("type");
//		Node node = new Node(type);
//		if(nodetemporary.isNull("id"))
//			node.setId("-1");
//		else
//			node.setId(nodetemporary.get("id").toString());
//		
//		if(nodetemporary.isNull("name"))
//			node.setName(nodetemporary.getString("type"));
//		else
//			node.setName(nodetemporary.get("name").toString());
//		
//		if(type.equals("insert"))
//		else
//			this.differences.getNodeDeletions().add(new NodeDeletion(node));
//		
//		this.nodes.add(new Node(node,nodetemporary.getString("temporary")));
//	}
	
//	public void createEdgeInfo(JSONObject edgetemporary) {
//		Edge edge = new Edge();
//		if (edgetemporary.isNull("id"))
//			edge.setId("-1");
//		else
//			edge.setId(edgetemporary.get("id").toString());
//
//		if (edgetemporary.isNull("name"))
//			edge.setName(edgetemporary.getString("type"));
//		else
//			edge.setName(edgetemporary.get("name").toString());
//
//		edge.setType(edgetemporary.getString("type"));
//
//		this.edgeInfo.add(new EdgeInfo(edge, edgetemporary.getString("temporary")));
//	}

	
	public Map<String, Integer> getMapTypesNode(List<JSONObject> nodesInsert){
		Map <String, Integer> map = new HashMap<String, Integer>();
		for (JSONObject node :nodesInsert){
			String key = node.getString("type");
			int value=1;
			if(map.containsKey(key)){
				value+=map.get(key);
			}
				map.put(key,value);
		}
		return map;
	}

	
	public Map<String, Integer> getMapTypesEdge(List<JSONObject> edgeInsert){
		Map <String, Integer> map = new HashMap<String, Integer>();
		for (JSONObject edge :edgeInsert){
			String key =edge.getString("type");
			int value=1;
			if(map.containsKey(key)){
				value+=map.get(key);
			}
			map.put(key,value);
		}
		return map;
	}
	
	/**
	 * @return the list of JSONObject edges 
	 */
	public List<JSONObject> getListEdgeInfoByCode(String code) {
		List<JSONObject> list = new LinkedList<JSONObject>();
		for (JSONObject edge : this.edges){
			if (edge.getString("temporary").equals(code)) 
				list.add(edge);
		}
		return list;
	}
	
	/**
	 * @return the list of JSONObject edges 
	 */
	public List<JSONObject> getListNodeInfoByCode(String code) {
		List<JSONObject> list = new LinkedList<JSONObject>();
		for (JSONObject node : this.nodes){
			if (node.getString("temporary").equals(code)) 
				list.add(node);
		}
		return list;
	}

	
	private void generateTextFeedback() {
		
//		this.feedback = "WARNING: These hints might not be exactly correct!\n";
		this.feedback="";
		List<JSONObject> nodesInsert = getListNodeInfoByCode("insert");
		if(nodesInsert.size()!=0){
//			this.feedback += " It's missing add " + nodesInsert.size() + " node(s):\n";
//			this.feedback +=SummaryFormat.ADD_NODE.format(lang, nodesInsert.size());
			this.feedback += getStringMessage(5,"addNode",nodesInsert.size()) + "@";
			Map<String, Integer> map= getMapTypesNode(nodesInsert);
			for(Entry<String, Integer> entry : map.entrySet()){
//				this.feedback += SummaryFormat.NODE_TYPE.format(lang, entry.getValue(),entry.getKey());
//				this.feedback +=getUrlInfo(urlMap.get(entry.getKey()))+"\n";
				this.feedback += getStringMessage(5,"nodeType",entry.getValue(),entry.getKey(),getUrlInfo(urlMap.get(entry.getKey()))) + "@";
			}
		}
		
		
		List<JSONObject> edgeInsert = getListEdgeInfoByCode("insert");
		if(edgeInsert.size()!=0){
//			
			this.feedback += getStringMessage(5,"addEdge", edgeInsert.size()) + "@";
			Map<String, Integer> map= getMapTypesEdge(edgeInsert);
			
			for(Entry<String, Integer> entry : map.entrySet()){
//				this.feedback += SummaryFormat.EDGE_TYPE.format(lang, entry.getValue(),entry.getKey());
//				this.feedback +=getUrlInfo(urlMap.get(entry.getKey()))+"\n";
				this.feedback += getStringMessage(5,"edgeType",entry.getValue(),entry.getKey(),getUrlInfo(urlMap.get(entry.getKey()))) + "@";
			}
		}
		
		
		List<JSONObject> nodesDelete = getListNodeInfoByCode("delete");
		if(nodesDelete.size()!=0){
//			this.feedback += "\n Should be removed " + nodesDelete.size() + " node(s):\n";
//			this.feedback +=SummaryFormat.REMOVE_NODE.format(lang, nodesDelete.size());
			this.feedback += getStringMessage(5,"removeNode",nodesDelete.size()) + "@";
			
			Map<String, Integer> map= getMapTypesNode(nodesDelete);
			
			for(Entry<String, Integer> entry : map.entrySet()){
//				this.feedback +=  entry.getValue() + " node(s) from type: " + entry.getKey() +" "+ getUrlInfo(urlMap.get(entry.getKey()))+ "\n";
//				this.feedback += SummaryFormat.NODE_TYPE.format(lang, entry.getValue(),entry.getKey());
//				this.feedback +=getUrlInfo(urlMap.get(entry.getKey()))+"\n";
				this.feedback += getStringMessage(5,"nodeType",entry.getValue(),entry.getKey(),getUrlInfo(urlMap.get(entry.getKey()))) + "@";
			}
		}
		
		
		List<JSONObject> edgeDelete = getListEdgeInfoByCode("delete");
		if(edgeDelete.size()!=0){
//			this.feedback += "\n Should be removed " + edgeDelete.size() + " edge(s):\n";
//			this.feedback +=SummaryFormat.REMOVE_EDGE.format(lang, edgeDelete.size());
			this.feedback += getStringMessage(5,"removeEdge", edgeDelete.size()) + "@";
			Map<String, Integer> map= getMapTypesEdge(edgeDelete);
			
			for(Entry<String, Integer> entry : map.entrySet()){
//				this.feedback += entry.getValue() + " edge(s) from type: " + entry.getKey() +" "+ getUrlInfo(urlMap.get(entry.getKey()))+ "\n";
//				this.feedback += SummaryFormat.EDGE_TYPE.format(lang, entry.getValue(),entry.getKey());
//				this.feedback +=getUrlInfo(urlMap.get(entry.getKey()))+"\n";
				this.feedback += getStringMessage(5,"edgeType",entry.getValue(),entry.getKey(),getUrlInfo(urlMap.get(entry.getKey()))) + "@";
			}
		}
		
		
		
	}

	private static String getUrlInfo(List<String> listURL){
		String text="";
		for(String url : listURL)
			text+="<a target=\"blank\" href=\" "+url+" \">info</a>  ";
		return text;
	} 
	
/**
	 * @return the differences
	 */
	public DifferenceHandler getDifferences() {
		return differences;
	}


	/**
	 * @param differences the differences to set
	 */
	public void setDifferences(DifferenceHandler differences) {
		this.differences = differences;
	}

enum SummaryFormat{
		
		ADD_NODE(
				"It's missing add <b>%d</b> node(s):\n",
				"Falta adicionar <b>%d</b> nós"
				),	
		ADD_EDGE(
				"It's missing <b>%d</b> edge(s): \n",
				"Faltam adicionar <b>%d</b> arestas "
				),
		REMOVE_NODE(
				"Should be removed <b>%d</b> node(s):\n",
				"Precisam ser removidas <b>%d</b> nós "
				),
		REMOVE_EDGE(
				"Should be removed <b>%d</b> edge(s):\n",
				"Precisam ser removidas <b>%d</b> arestas"
				),
		NODE_TYPE(
				"<b>%d</b> node(s) from type:  <b>%s</b>",
				"<b>%d</b> node(s) do tipo:  <b>%s</b>"
				),
		EDGE_TYPE(
				"<b>%d</b> edge(s) from type:  <b>%s</b>",
				"<b>%d</b> edge(s) do tipo:  <b>%s</b>"
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


