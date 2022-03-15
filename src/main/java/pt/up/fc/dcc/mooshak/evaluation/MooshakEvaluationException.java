package pt.up.fc.dcc.mooshak.evaluation;

import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Exception raised in the evaluation process to notify that non-accept
 * classification. These exception include a message, that is reported as 
 * an observation, a classification (that defaults to invalid submission),
 * and an optional cause. 
 * This exception is also used to return a feedback an a mark, which is
 * particularly useful for a special corrector to return this data. 
 */
public class MooshakEvaluationException extends MooshakException {
	private static final long serialVersionUID = 1L;
	
	Classification classification = Classification.INVALID_SUBMISSION;
	String feedback = null;
	Integer mark = null;
	
	public MooshakEvaluationException(String message) {
		super(message);
	}
	
	
	public MooshakEvaluationException(String message,Classification classification){
		super(message);
		this.classification = classification;
	}
	
	public MooshakEvaluationException(
			String message,
			Classification classification,
			String feedback,
			Integer mark){
		super(message);
		this.classification = classification;
		this.feedback = feedback;
		this.mark = mark;
	}
	
	public MooshakEvaluationException(String message, Throwable cause) {
		super(message,cause);
	}
	
	public MooshakEvaluationException(String message, 
			Throwable cause,
			Classification classification) {
		super(message,cause);
		this.classification = classification;
	}
	
	public MooshakEvaluationException(
			String message,
			Throwable cause,
			Classification classification,
			String feedback,
			Integer mark){
		super(message,cause);
		this.classification = classification;
		this.feedback = feedback;
		this.mark = mark;
	}
	
	/**
	 * Classification assigned to this error
	 * @return
	 */
	public Classification getClassification() {
		return classification;
	}

	/**
	 * Feedback on this error
	 * @return
	 */
	public String getFeedback() {
		return feedback;
	}

	/**
	 * Mark resulting from the submission with this status
	 * @return
	 */
	public Integer getMark() {
		return mark;
	}
	
	
}