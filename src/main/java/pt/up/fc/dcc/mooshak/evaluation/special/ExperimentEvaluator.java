package pt.up.fc.dcc.mooshak.evaluation.special;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakEvaluationException;
import pt.up.fc.dcc.mooshak.evaluation.SpecialCorrector;

public class ExperimentEvaluator implements SpecialCorrector {
	KoraEvaluator koraEvaluator = new KoraEvaluator();
	DiagramEvaluator diagramEvaluator = new DiagramEvaluator();

	
	@Override
	public void evaluate(EvaluationParameters parameters) throws MooshakEvaluationException {
		boolean useKora;
		try {
			useKora = useKora(parameters);
		} catch (MooshakContentException e) {
			useKora = true;
		}
		String message =  "with" + (useKora ? "" : "out") + " Kora\n";
		Classification classification = Classification.ACCEPTED;
		String feedback = "";
		int mark = 100;
		try {
			if(useKora)
				koraEvaluator.evaluate(parameters);
			else {
					diagramEvaluator.evaluate(parameters);
			}
		} catch(MooshakEvaluationException error) {
			message += error.getMessage();
			classification = error.getClassification();
			mark = error.getMark();
			
		}
		
		throw new MooshakEvaluationException(message, classification, feedback,  mark);
	}
	
	boolean useKora(EvaluationParameters parameters) throws MooshakContentException {
			return (parameters.getTeam().getName().hashCode() + parameters.getProblem().getName().hashCode() ) % 2 == 0;
			
	}

}
