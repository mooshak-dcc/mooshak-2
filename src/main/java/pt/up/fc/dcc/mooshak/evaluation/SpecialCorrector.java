package pt.up.fc.dcc.mooshak.evaluation;

/**
 * Type of a class implementing a special evaluator. 
 * A Java special evaluator can be used instead of either a compilation line,
 * or an execution line, the the definition of a language; or either as a static
 * corrector or a dynamic corrector in a problem definition. 
 * 
 * Special evaluators must have an {@code evaluate()} method thats
 * receive collecting parameters object, and should throw
 * a MooshakEvaluationException to change the current classification 
 * (otherwise is accepted). 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface SpecialCorrector {

	/**
	 * Evaluate submission with give parameters.
	 * Throw exception to change classification, otherwise it will be accepted.
	 * 
	 * @param parameters
	 * @throws MooshakEvaluationException
	 */
	public void evaluate(EvaluationParameters parameters) 
			throws MooshakEvaluationException ;
}
