package pt.up.fc.dcc.mooshak.evaluation.quiz.examgenerator;

import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.REQUIRES_REEVALUATION;

import java.nio.file.Path;
import java.util.Collection;

import javax.xml.transform.TransformerException;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakEvaluationException;
import pt.up.fc.dcc.mooshak.evaluation.SpecialCorrector;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class QuizAnalyzer implements SpecialCorrector{
	public Path configurationFile;
	
	@Override
	public void evaluate(EvaluationParameters parameters) throws MooshakEvaluationException {
		
		String attempt = parameters.getProgramPath().toString();
		Path quizSolutionPath = parameters.getProblem().getQuiz();
		String quizSolutionXML = PersistentCore.getAbsoluteFile(quizSolutionPath).toString();
		System.out.println(quizSolutionXML);
		
		Collection<Path> solutions = null;
		String solutionXML="";
//		System.out.println("28258 AQUIIIIIIIIIIIIIIIIIIIIII Test");
		parameters.getProblem().getQuiz();
//		try {
//			solutions = parameters.getSolutions();
//			
//			for(Path path: solutions) {
//				solutionXML = PersistentCore.getAbsoluteFile(path).toString();
//				System.out.println("AQUIIIIIIIIIIIIIIIIIIIIII Test");
//				System.out.println(solutionXML);
//			}
//		} catch (MooshakException cause) {
//			reevaluate("Error retriving diagram solutions",cause);
//		}
		
		
//		ExamGenerator examGenerator = new ExamGenerator();
//		Submission submission = parameters.getSubmission();
//		
//		System.out.println(attempt);
//		
//		JSONHandler jsonHandler = new JSONHandler("test");
//		jsonHandler.update(attempt);
//		try {
//			examGenerator.analyzer();
//		} catch (MooshakException | TransformerException e) {
//			e.printStackTrace();
//		}
//		
//		try {
//			String html=examGenerator.generateHtmlFinal();
////			submission.setFeedback(html);
//		} catch (MooshakException | TransformerException e) {
//			e.printStackTrace();
//		}
		
		System.out.println(1234);
//		try {
//			controller = new Controller(solution, attempt, "EER");
//		} catch (JSONException cause) {
//			reevaluate("Error parsing diagrams",cause);
//		}
		
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
