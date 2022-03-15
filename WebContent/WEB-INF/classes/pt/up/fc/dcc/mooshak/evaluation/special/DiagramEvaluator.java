package pt.up.fc.dcc.mooshak.evaluation.special;

import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.REQUIRES_REEVALUATION;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.WRONG_ANSWER;

import java.nio.file.Path;
import java.util.Collection;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakEvaluationException;
import pt.up.fc.dcc.mooshak.evaluation.SpecialCorrector;
import pt.up.fc.dcc.mooshak.evaluation.graph.Controller;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Static corrector to evaluate a submission as a graph.
 * Solutions and attempt graphs must be provided in JSON following the
 * Eshu's format.
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class DiagramEvaluator implements SpecialCorrector {
	private Evaluation bestEvaluation = null;
	
	@Override
	public void evaluate(EvaluationParameters parameters)
			throws MooshakEvaluationException {
		
		String attempt = parameters.getProgramPath().toString();
		double bestGrade = 0;
		String bestTextualFeedback = null;
		JSONObject bestJsonFeedback = null;
		Collection<Path> solutions = null;
		
		try {
			solutions = parameters.getSolutions();
		} catch (MooshakException cause) {
			reevaluate("Error retriving diagram solutions",cause);
		}
		
		for(Path path: solutions) {
			String solution = PersistentCore.getAbsoluteFile(path).toString();
			Controller controller = null;
			
			try {
				controller = new Controller(solution, attempt, "EER");
			} catch (JSONException cause) {
				reevaluate("Error parsing diagrams",cause);
			}
			Evaluation evaluation = controller.getEvaluation();
			double grade =evaluation.getGrade();
			
			if(bestEvaluation == null || grade > bestGrade) {
				bestGrade = grade;
				bestEvaluation = evaluation;
				bestTextualFeedback = controller.getTextualFeeback();
				bestJsonFeedback = controller.getJsonFeedback();
				
			}
		}
		
		if(bestEvaluation == null) 
			reevaluate("No evaluation found");
		else if(bestGrade < 100) {
			throw new MooshakEvaluationException(
					bestTextualFeedback, 
					WRONG_ANSWER,
					bestJsonFeedback.toString(),
					(int) bestGrade);
		}
	}
	
	/**
	 * The evaluation is complete? Or was stopped by timeout?
	 * This method is mostly for unit testing
	 * @return {@code true} if complete; {@code false} otherwise 
	 */
	 boolean isComplete() {
		if(bestEvaluation != null)
			return bestEvaluation.isComplete();
		else 
			throw new IllegalStateException("evaluation not performed yet");
	}

	
	/**
	 * Throw a MooshakEvaluationException to signal the need for a reevaluation
	 * providing a message and the exception that caused it, if one is provided.
	 * If more than one cause is provided only the first is reported and the
	 * remainder are silently ignored.
	 * 
	 * @param message
	 * @param cause
	 * @throws MooshakEvaluationException
	 */
	private void reevaluate(String message,Throwable... causes) 
			throws MooshakEvaluationException {
		
		if(causes.length == 0)
			throw new MooshakEvaluationException(message,REQUIRES_REEVALUATION);
		else {
			Throwable cause = causes[0];
			throw new MooshakEvaluationException(
					message+": "+cause+cause.getMessage(),
					cause,
					REQUIRES_REEVALUATION);
		}
	}

}
