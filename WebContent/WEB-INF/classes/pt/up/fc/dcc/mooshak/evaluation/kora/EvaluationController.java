/**
 * @author Helder correia
 *
 */

package pt.up.fc.dcc.mooshak.evaluation.kora;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Edge;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.Graph;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluator;
import pt.up.fc.dcc.mooshak.evaluation.graph.parse.JSONHandler;
import pt.up.fc.dcc.mooshak.evaluation.kora.feedback.FeedbackManager;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Diagram;
import pt.up.fc.dcc.mooshak.evaluation.kora.semantics.EvaluationWithGrade;
import pt.up.fc.dcc.mooshak.evaluation.kora.semantics.EvaluationWithoutGrade;
import pt.up.fc.dcc.mooshak.evaluation.kora.syntaxe.EvaluationSyntax;

public class EvaluationController {
	final String COMPLETE="complete";
	final String INCOMPLETE="incomplete";
	final String SYNTAXE="syntaxe";
	
	public Evaluation evaluation;
	public Graph solution;
	public Graph attempt;
	private JSONObject jsonFeedback;
	private String textFeedback;
	
	private JSONHandler attemptJsonHandler;
	private JSONHandler solutionJsonHandler;
	private Diagram diagram;
	private String stateEvaluation;
	private String solutionFile; 
	private String attemptFile;
	private double grade;
	public boolean accept;
	public String type ;
	/**
	 * 1. Receives the paths of two files (one with the solution and the other
	 * with the attempt) and the type of the language used. 2. Convert the
	 * files' information into graphs. 3. Compares the graphs finding the
	 * differences. 4. Produces the feedback based on the set of differences
	 * 
	 * @param solutionFile
	 * @param attemptFile
	 * @throws JSONException
	 */
	public EvaluationController(String solutionFile, String attemptFile, Diagram diagram) throws JSONException {
//		System.out.println(attemptFile);
		this.attemptJsonHandler = createGraph(attemptFile, 1 , diagram);
		this.solutionJsonHandler = createGraph(solutionFile, -1, diagram);
		this.solutionFile=solutionFile; 
		this.attemptFile=attemptFile;
		this.diagram =diagram;
		this.setStateEvaluation("");
		solution = this.solutionJsonHandler.parse();
		attempt = this.attemptJsonHandler.parse();
		this.accept=false;
		this.type="EER"; //FilenameUtils.getExtension(solutionFile).toUpperCase();
		
		
	}

	public boolean evaluationSyntax() {
		
		EvaluationSyntax evaluationSyntax = this.attemptJsonHandler.getEvaluationSyntax();
		String ext1 = FilenameUtils.getExtension(solutionFile); 
		String ext2 = FilenameUtils.getExtension(attemptFile); 
		 
		if (evaluationSyntax.isEvaluationError(ext2) || evaluationSyntax.isExtensionValid(ext1, ext2)) { 
			this.setTextFeedback(evaluationSyntax.getTextFeedback());
			this.setJsonFeedback(evaluationSyntax.getJsonFeedback());
			this.stateEvaluation=SYNTAXE;
			setGrade(0);
			accept = false;
			return true;
		}
		return false;
	}

	public void generateFeedback(FeedbackManager feedbackManager) {
			// \.out.println("************* Evaluation Semantics
//		System.out.println(solution);
//		System.out.println("<--------------------------------------->");
		solution=createGraph2(solutionFile, "eer",-1);
		
//		System.out.println();
		attempt= createGraph2(attemptFile, "eer",1 );
//		System.out.println(solution);
//		System.out.println(attempt.getEdges());
		List<Edge> edges = attempt.getEdges();
		for(Edge e : edges){
			System.out.println(e.getType());
		}
		
//		System.out.println("<--------------------------------------->");
//		System.out.println(attempt);*/
			this.evaluation = new Evaluator(solution, attempt, this.type).evaluate();
//			System.out.println(evaluation.getDifferences());
//			System.out.println(evaluation.getGrade());
//			System.out.println(attempt);
//			System.out.println("\n\n");
		
			if (this.evaluation.getGrade()!=-1 ) {
				EvaluationWithGrade completeEvaluation = new EvaluationWithGrade(solution, attempt, this.evaluation);
//				System.out.println(evaluation.getDifferences());

				if (completeEvaluation.getDifferences().totalError() == 0 ||evaluation.getGrade()==100) {
					accept = true;
					setGrade(100);
					this.setJsonFeedback(new JSONObject());
					return;
				}
				feedbackManager.summarize(completeEvaluation.getDifferences());
				this.setTextFeedback(feedbackManager.getTextFeedback());
				this.setJsonFeedback(feedbackManager.getJsonFeedback());
				this.stateEvaluation=COMPLETE;
				setGrade(evaluation.getGrade());
			} else {
				this.attemptJsonHandler = createGraph(attemptFile, 1 , diagram);
				this.solutionJsonHandler = createGraph(solutionFile, -1, diagram);
				solution = this.solutionJsonHandler.parse();
				
				
				attempt = this.attemptJsonHandler.parse();
				EvaluationWithoutGrade incompleteEvaluation = new EvaluationWithoutGrade(solution, attempt, null,diagram);
				this.setTextFeedback(incompleteEvaluation.getFeedback());//
				this.setJsonFeedback(incompleteEvaluation.getJsonFeedback());
				this.stateEvaluation=INCOMPLETE;
				setGrade(0);
			}
			
	}


	
	
	
	/**
	 * Creates the graph for the given file
	 * 
	 * @param file
	 * @param type
	 * @return
	 * @throws JSONException
	 */
	private JSONHandler createGraph(String file, int idFactor, Diagram diagram) throws JSONException {
		FileInputStream fileStream = null;
			try {
				fileStream = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			JSONHandler jsonHandler = new JSONHandler(fileStream, idFactor, diagram);
			return jsonHandler;
		
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


	/**
	 * @return the attemptJsonHandler
	 */
	public JSONHandler getAttemptJsonHandler() {
		return attemptJsonHandler;
	}


	/**
	 * @param attemptJsonHandler the attemptJsonHandler to set
	 */
	public void setAttemptJsonHandler(JSONHandler attemptJsonHandler) {
		this.attemptJsonHandler = attemptJsonHandler;
	}


	/**
	 * @return the solutionJsonHandler
	 */
	public JSONHandler getSolutionJsonHandler() {
		return solutionJsonHandler;
	}


	/**
	 * @param solutionJsonHandler the solutionJsonHandler to set
	 */
	public void setSolutionJsonHandler(JSONHandler solutionJsonHandler) {
		this.solutionJsonHandler = solutionJsonHandler;
	}

	public String getTextFeedback() {
		return textFeedback;
	}

	public void setTextFeedback(String textFeedback) {
		this.textFeedback = textFeedback;
	}

	public void setJsonFeedback(JSONObject jsonFeedback) {
		this.jsonFeedback = jsonFeedback;
	}

	public String getStateEvaluation() {
		return stateEvaluation;
	}

	public void setStateEvaluation(String stateEvaluation) {
		this.stateEvaluation = stateEvaluation;
	}

	/**
	 * @return the grade
	 */
	public double getGrade() {
		return grade;
	}

	/**
	 * @param grade the grade to set
	 */
	public void setGrade(double grade) {
		this.grade = grade;
	}

	
	private Graph createGraph2(String file, String type, int idFactor) throws JSONException {
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
	
}

