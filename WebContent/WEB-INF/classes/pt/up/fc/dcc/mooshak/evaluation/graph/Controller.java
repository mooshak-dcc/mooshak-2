package pt.up.fc.dcc.mooshak.evaluation.graph;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluator;
import pt.up.fc.dcc.mooshak.evaluation.graph.parse.JSONHandler;

public class Controller {
	Evaluation evaluation;
	JSONObject jsonFeedback;
	String textFeedback;
	
	/**
	 * 1. Receives the paths of two files (one with the solution and the other
	 * with the attempt) and the type of the language used. 2. Convert the
	 * files' information into graphs. 3. Compares the graphs finding the
	 * differences. 4. Produces the feedback based on the set of differences
	 * 
	 * @param solutionFile
	 * @param attemptFile
	 * @param type
	 * @throws JSONException
	 */
	public Controller(String solutionFile, String attemptFile, String type) throws JSONException {
		Graph solution = createGraph(solutionFile, type, -1);
		Graph attempt = createGraph(attemptFile, type, 1);
		
		this.evaluation = new Evaluator(solution, attempt, type).evaluate();
		
		Messenger messenger = new Messenger(solution, attempt, evaluation, type, true);
		this.jsonFeedback = messenger.jsonFeedback;
		this.textFeedback = messenger.getTextualFeedback();
	}


	/**
	 * Creates the graph for the given file
	 * 
	 * @param file
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	private Graph createGraph(String file, String type, int idFactor) throws JSONException {
		FileInputStream fileStream = null;
		try {
			fileStream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//*diagram esta null depois eliminar *******************************************************************************
		JSONHandler jsonHandler = new JSONHandler(fileStream, idFactor ,null);
		return jsonHandler.parseReducible();
	}

	public Evaluation getEvaluation() {
		return evaluation;
	}
	
	/**
	 * Get feedback as a JSON string in Eshu's schema
	 * @return
	 */
	public JSONObject getJsonFeedback() {
		return this.jsonFeedback;
	}
	
	/**
	 * Get feedback as a string containing a text 
	 * @return
	 */
	public String getTextualFeeback() {
		return this.textFeedback;
	}
	
	/**
	 * The evaluation is complete? Or stopped by timeout?
	 * @return {@code true} if complete; {@code false} otherwise 
	 */
	public boolean isComplete() {
		if(evaluation == null)
			throw new IllegalStateException("evaluation not performed yet");
		else
			return evaluation.isComplete();
	}
}
