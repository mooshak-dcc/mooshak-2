package pt.up.fc.dcc.mooshak.evaluation.special;

import java.io.IOException;
import java.nio.file.Files;

import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakEvaluationException;
import pt.up.fc.dcc.mooshak.evaluation.SpecialCorrector;


/**
 * This is a test evaluator that simply checks if the code contains the word
 * hello.
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class HelloEvaluator implements SpecialCorrector {

	@Override
	public void evaluate(EvaluationParameters parameters)
			throws MooshakEvaluationException {
			
		
		try {
			boolean missing = true;
			
			for(String line: Files.readAllLines(parameters.getProgramPath()))
				if(line.contains("hello"))
					missing = false;
			
			if(missing)
				throw new MooshakEvaluationException("No 'hello' string fround",
						Classification.WRONG_ANSWER);
			
		} catch (IOException cause) {
			
			throw new MooshakEvaluationException("Error reading program",
					cause,Classification.REQUIRES_REEVALUATION);
		}
		
	}

}
