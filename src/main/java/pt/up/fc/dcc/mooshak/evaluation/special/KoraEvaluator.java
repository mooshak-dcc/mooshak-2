package pt.up.fc.dcc.mooshak.evaluation.special;

import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.REQUIRES_REEVALUATION;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.WRONG_ANSWER;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakEvaluationException;
import pt.up.fc.dcc.mooshak.evaluation.SpecialCorrector;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Evaluation;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.evaluation.kora.Configuration;
import pt.up.fc.dcc.mooshak.evaluation.kora.EvaluationController;
import pt.up.fc.dcc.mooshak.evaluation.kora.feedback.FeedbackManager;
import pt.up.fc.dcc.mooshak.evaluation.kora.feedback.FeedbackerCache;
import pt.up.fc.dcc.mooshak.evaluation.kora.feedback.Key;
import pt.up.fc.dcc.mooshak.evaluation.kora.parse.config.Diagram;

public class KoraEvaluator implements SpecialCorrector{
	private Evaluation bestEvaluation = null;
	private static FeedbackerCache feedbackerCache = new FeedbackerCache();
	private Map<String, Configuration> configurationsMap = new HashMap<String, Configuration>() ;
	final String SYNTAXE="syntaxe";
	public Path configurationFile;
	
	@Override
	public void evaluate(EvaluationParameters parameters) throws MooshakEvaluationException {
		
		String attempt = parameters.getProgramPath().toString();
		Collection<Path> solutions = null;
		
		Path configurationFile = parameters.getLanguage().getConfiguration();

//		Path configurationFile= Paths.get("inputs/casouso/CasoUsoConfig.xml");
//		Path configurationFile= Paths.get("inputs/class/ClassConfig.xml");
//		Path configurationFile= Paths.get("inputs/eer/eer.xml");
		
		double bestGrade = 0;
		String bestTextualFeedback = null;
		JSONObject bestJsonFeedback = null;
		
		try {
			solutions = parameters.getSolutions();
		} catch (MooshakException cause) {
			reevaluate("Error retriving diagram solutions",cause);
		}
		FeedbackManager feedbackManager=null;
		EvaluationController controller=null;
		
		
		for(Path path: solutions) {
			String solution = PersistentCore.getAbsoluteFile(path).toString();
			try {
				Configuration fileConfig= new Configuration(PersistentCore.getHomePath().resolve(configurationFile).toString());
				
	
				controller = new EvaluationController(solution, attempt,fileConfig.getDL2().getDiagram());
				if(!controller.evaluationSyntax()){
					Key key =new Key(parameters.getTeam(), parameters.getProblem());
					feedbackManager = getFeedbacker(key, fileConfig.getDL2().getDiagram());
					controller.generateFeedback(feedbackManager);
					feedbackerCache.put(key,feedbackManager);
				}

//				
			} catch (JSONException | MooshakException cause) {
				reevaluate("Error parsing diagrams",cause);
			}
			Evaluation evaluation = controller.getEvaluation();
			double grade =controller.getGrade();
//			System.out.println("Kora "+feedbackManager.getTextFeedback());
//			System.out.println("Kora  "+feedbackManager.getJsonFeedback());
//			System.out.println(grade + " : GRADE ******");
			
//			if(bestEvaluation == null || grade > bestGrade) {
			if(grade>=0) {
				bestGrade = grade;
				bestEvaluation = evaluation;
				bestTextualFeedback = controller.getTextualFeeback();
				bestJsonFeedback = controller.getJsonFeedback();
			}
		}
		
		if(!controller.accept&& controller.getStateEvaluation().equals(SYNTAXE)) {
			throw new MooshakEvaluationException(
					controller.getTextualFeeback(), 
					WRONG_ANSWER,
					controller.getJsonFeedback().toString(),
					0);
		}
		else if(!controller.accept && bestGrade < 100) {
			throw new MooshakEvaluationException(
					bestTextualFeedback, 
					WRONG_ANSWER,
					bestJsonFeedback.toString(),
					(int) bestGrade);
		}
		else if(bestEvaluation == null) {
			reevaluate("Evaluation not found");
		}
		
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
	

	public static FeedbackManager getFeedbacker(Key key,Diagram diagram) {
		FeedbackManager feedbacker;
		
		if(feedbackerCache.containsKey(key)){
			feedbacker = feedbackerCache.get(key);
//			System.out.println("Keyyyyyyyyy old "+key.toString() );
		}
		else {
			feedbacker = new FeedbackManager(diagram.getUrlMap());
			feedbackerCache.put(key,feedbacker);
//			System.out.println("Keyyyyyyyyy new "+key.toString() );
		}
			
		return feedbacker;
	}

	
	/**
	 * if configuration exist in configurationsMap return the value else if 
	 * create a new configuration store in configurationsMap and return 
	 * String path- path to configuration
	 * @return the configurations
	 * @throws MooshakException 
	 */
	public Configuration getConfiguration(String path) throws MooshakException{
		
		if(this.configurationsMap.containsKey(path))
			return this.configurationsMap.get(path);
		
		Configuration config= new Configuration(path);
		this.configurationsMap.put(path, config);
		return new Configuration(path);
	}
	
}
