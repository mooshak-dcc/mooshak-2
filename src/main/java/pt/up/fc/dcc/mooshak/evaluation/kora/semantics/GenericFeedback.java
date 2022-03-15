/**
 *  @author Helder correia
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;

/**
 * @author Helder Correia
 *
 */
public class GenericFeedback {
	public static final int TOTAL_GRADE_INCOMPLETE_EVALUATION = 80;
	public static final int TOTAL_GRADE_COMPLETE_EVALUATION = 20;
	public static final int TOTAL_GRADE = 100;
	
	protected Graph solution;
	protected Graph attempt;
	protected String feedback;
	Evaluation evaluation;
	protected JSONArray jsonNodes;
	protected JSONArray jsonEdges;
	protected JSONObject jsonFeedback;
	Map<Node, Double> mapNodesMaxValue = null;
	protected HashMap <Node, ArrayList<String>>  nodeInfo ;
	protected HashMap <Edge, ArrayList<String>>  edgeInfo ;
	
	public GenericFeedback( EvaluationWithGrade evaluationWithGrade ) {
		this.solution=evaluationWithGrade.getSolution();
		this.attempt=evaluationWithGrade.getAttempt();
		this.evaluation=evaluationWithGrade.getEvaluation();
		this.jsonFeedback = new JSONObject();
		this.jsonNodes = new JSONArray();
		this.jsonEdges = new JSONArray();
		this.feedback = "";
//		this.nodeInfo = evaluationWithGrade.get;
//		this.edgeInfo = evaluationWithGrade.edgeInfo;
	}
	
	

	public Graph getSolution() {
		return solution;
	}
	public void setSolution(Graph solution) {
		this.solution = solution;
	}
	public Graph getAttempt() {
		return attempt;
	}
	public void setAttempt(Graph attempt) {
		this.attempt = attempt;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public Evaluation getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Evaluation evaluation) {
		this.evaluation = evaluation;
	}

	public JSONArray getJsonNodes() {
		return jsonNodes;
	}

	public void setJsonNodes(JSONArray jsonNodes) {
		this.jsonNodes = jsonNodes;
	}

	public JSONArray getJsonEdges() {
		return jsonEdges;
	}

	public void setJsonEdges(JSONArray jsonEdges) {
		this.jsonEdges = jsonEdges;
	}
	
	public JSONObject getJsonFeedback() {
		return jsonFeedback;
	}

	public void setJsonFeedback(JSONObject jsonFeedback) {
		this.jsonFeedback = jsonFeedback;
	}



	/**
	 * @return the nodeInfo
	 */
	public HashMap <Node, ArrayList<String>> getNodeInfo() {
		return nodeInfo;
	}



	/**
	 * @param nodeInfo the nodeInfo to set
	 */
	public void setNodeInfo(HashMap <Node, ArrayList<String>> nodeInfo) {
		this.nodeInfo = nodeInfo;
	}



	/**
	 * @return the edgeinfo
	 */
	public HashMap <Edge, ArrayList<String>> getEdgeinfo() {
		return edgeInfo;
	}



	/**
	 * @param edgeinfo the edgeinfo to set
	 */
	public void setEdgeinfo(HashMap <Edge, ArrayList<String>> edgeinfo) {
		this.edgeInfo = edgeinfo;
	}
}



