/**
 * 
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;


import org.json.JSONException;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;

/**
 * @author Helder Correia
 *
 */
public class EvaluationWithGrade extends GenericEvaluation{
	private DifferenceHandler differences= new DifferenceHandler();
	public EvaluationWithGrade(Graph solution,Graph attempt,Evaluation evaluation) {
		super(solution, attempt,evaluation);
		handleCompleteEEREvaluation();
	}

	/**
	 * 
	 * @throws JSONException
	 */
	private void handleCompleteEEREvaluation() throws JSONException {
		if (evaluation.getGrade() != 100) {
			
			differences = new DifferenceHandler(this.evaluation,this.solution,this.attempt);
			differences.removeDuplicatedDifferences();
			differences.simplifyDifferences();
			differences.setDifferences();
			
		//	handleNodesDifferences(differences);
			//handleEdgesDifferences(differences);
			//handleCardinalityDifferences(differences);
			//calculateErrorRate();
		}

	}
	
//	/**
//	 * 
//	 * @param differences
//	 * @throws JSONException
//	 */
//	private void handleNodesDifferences(DifferenceHandler differences) throws JSONException {
//		for (NodeInsertion insertion : differences.getNodeInsertions()) {
//			Node node = insertion.getInsertion();
//			addInfoNode(node, "insert");
//		}
//
//		for (NodeDeletion deletion : differences.getNodeDeletions()) {
//			Node node = deletion.getDeletion();
//			
//			addInfoNode(node, "delete");
//		}
//
//		for (DifferentType difType : differences.getDifferentNodeTypes()) {
//			Node node = (Node) difType.getObject();
//			Node attemptNode = evaluation.getBestMap().get(node).getAttempt();
//			addInfoNode(attemptNode, "modify");
//		}
//	}
	
	
	/**
	 * 
	 * @param differences
	 * @throws JSONException
	 */
//	private void handleEdgesDifferences(DifferenceHandler differences) throws JSONException {
//		for (EdgeInsertion insertion : differences.getEdgeInsertions()) {
//			Edge edge = insertion.getInsertion();
//			edge.setSource(getNodeAttempt(edge.getSource()));
//			edge.setTarget(getNodeAttempt(edge.getTarget()));
//			
//			addInfoEdge(edge, "insert");
//		}
//		
//		for (EdgeDeletion deletion : differences.getEdgeDeletions()) {
//			Edge edge = deletion.getDeletion();
//			addInfoEdge(edge, "delete");
//			
//		}
//
//		for (DifferentType difType : differences.getDifferentEdgeTypes()) {
//			Edge edge = (Edge) difType.getObject();
//			edge.setSource(getNodeAttempt(edge.getSource()));
//			edge.setTarget(getNodeAttempt(edge.getTarget()));
//			addInfoEdge(edge, "modify");
//		}
//
//	}
//	
//	/**
//	 * 
//	 * @param differences
//	 */
//	private void handleCardinalityDifferences(DifferenceHandler differences) {
//		
//		for (PropertyInsertion insertion : differences.getCardinalityInsertions()) {
//			PropertyName name = insertion.getPropertyName();
//			
//			SimplePropertyName simple = (SimplePropertyName) name;
//			if (simple.getName().equals("cardinality")) {
//				Edge edge = (Edge) insertion.getObject();
//				addInfoEdge(edge, "insertCardinality");
//			}
//		}
//
//		for (PropertyDeletion deletion : differences.getCardinalityDeletions()) {
//			PropertyName name = deletion.getPropertyName();
//			SimplePropertyName simple = (SimplePropertyName) name;
//			if (simple.getName().equals("cardinality")) {
//				Edge edge = (Edge) deletion.getObject();
//				addInfoEdge(edge, "deleteCardinality");
//			}
//		}
//
//
//		for (DifferentPropertyValue difValue : differences.getDifferentCardinality()) {
//			Edge edge = (Edge) difValue.getObject();
//			addInfoEdge(edge, "modifyCardinality");
//		}
//	}
//	
//	
//	/**
//	 * 
//	 * @param nodeSolution
//	 * @return
//	 */
//	private Node getNodeAttempt (Node nodeSolution){
//		if(evaluation.getBestMap().get(nodeSolution)==null){
//			return nodeSolution;
//		}
//		return evaluation.getBestMap().get(nodeSolution).getAttempt();
//	}
//	
//	
//	
//	private void calculateErrorRate(){
//		for(NodeInfo node  :this.nodeInfo){
//			String type =node.getType();
//			if(errorRate.containsKey(type)){
//				int value = errorRate.get(type);
//				errorRate.put(type, value);
//			}
//			else
//				errorRate.put(type, 1);
//		}
//		
//		for(EdgeInfo edge :this.edgeInfo){
//			String type =edge.getType();
//			if(errorRate.containsKey(type)){
//				int value = errorRate.get(type);
//				errorRate.put(type, value);
//			}
//			else
//				errorRate.put(type, 1);
//		}
//	} 
	
	public int getGrade(){
		return (int) this.evaluation.getGrade();
	}

	public DifferenceHandler getDifferences() {
		return differences;
	}

	public void setDifferences(DifferenceHandler differences) {
		this.differences = differences;
	}
}
