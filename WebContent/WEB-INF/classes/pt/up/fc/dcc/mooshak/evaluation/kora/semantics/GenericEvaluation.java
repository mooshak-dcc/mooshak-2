/**
 *  @author Helder correia
 */
package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.GObject;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;

/**
 * @author Helder Correia
 *
 */
public class GenericEvaluation {
	public static final int TOTAL_GRADE_INCOMPLETE_EVALUATION = 80;
	public static final int TOTAL_GRADE_COMPLETE_EVALUATION = 20;
	public static final int TOTAL_GRADE = 100;
	
	protected Graph solution;
	protected Graph attempt;
	protected String feedback="";
	protected Evaluation evaluation;
	protected HashMap <String, Integer>  errorRate ;
	

	public GenericEvaluation( Graph solution,Graph attempt,Evaluation evaluation) {
		this.solution=solution;
		this.attempt=attempt;
		this.evaluation=evaluation;
		this.errorRate = new HashMap <String, Integer>();
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






	public HashMap<String, Integer> getErrorRate() {
		return errorRate;
	}



	public void setErrorRate(HashMap<String, Integer> errorRate) {
		this.errorRate = errorRate;
	}

	
}



