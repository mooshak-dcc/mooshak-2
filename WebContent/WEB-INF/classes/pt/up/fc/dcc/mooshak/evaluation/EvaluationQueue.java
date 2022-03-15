package pt.up.fc.dcc.mooshak.evaluation;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public interface EvaluationQueue {

	enum Command {
		EVALUATE, // the request
		TERMINATE, // the evaluator, no more request will be send
		EXIT, // the JVM, this worker is no longer needed
	};

	public static class EvaluationRequest implements Serializable, Comparable<EvaluationRequest> {

		private static final long serialVersionUID = 1L;

		Command command = Command.EVALUATE;
		Date date = new Date();

		private Submission submission = null;
		private String entityTag;

		EvaluationRequest(Command command) {
			this.command = command;
		}

		public EvaluationRequest(Submission submission) {
			this.submission = submission;
		}

		public EvaluationRequest(Submission submission, String entityTag) {
			this.submission = submission;
			this.entityTag = entityTag;
		}

		@Override
		public int compareTo(EvaluationRequest other) {
			return (int) (date.getTime() - other.date.getTime());
		}

		public Command getCommand() {
			return command;
		}

		public void setCommand(Command command) {
			this.command = command;
		}

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public Submission getSubmission() {
			return submission;
		}

		public void setSubmission(Submission submission) {
			this.submission = submission;
		}

		public String getEntityTag() {
			return entityTag;
		}

		public void setEntityTag(String entityTag) {
			this.entityTag = entityTag;
		}
	}

	/**
	 * Push a new evaluation request to queue
	 * 
	 * @param request
	 * @throws IOException
	 *             for compatibility with RMI
	 */
	public void enqueueEvaluationRequest(EvaluationRequest request) throws MooshakException;

	/**
	 * Pop an evaluation from the queue (request is removed from queue)
	 * 
	 * @return
	 * @throws IOException
	 *             for compatibility with RMI
	 * @throws MooshakException
	 */
	public EvaluationRequest dequeueEvaluationRequest() throws MooshakException;

	/**
	 * Return the submission resulting from and evaluation
	 * 
	 * @param submission
	 *            of concluded evaluation
	 * @throws IOException
	 *             for compatibility with RMI
	 */
	public void concludeEvaluation(Submission submission) throws MooshakException; // required
																					// by
																					// RMI

}
