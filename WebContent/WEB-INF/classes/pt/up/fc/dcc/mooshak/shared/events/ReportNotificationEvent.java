package pt.up.fc.dcc.mooshak.shared.events;

/**
 * Event to notify that the evaluation report of a certain submission 
 * is available. Client should use the RPC <code>getReport()</code> method
 * to obtain the report.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class ReportNotificationEvent extends MooshakEvent {

	String submissionId;
	String problemId;
	boolean consider;

	public ReportNotificationEvent() {
		super();
	}

	public ReportNotificationEvent(String submissionId, String problemId) {
		super();

		this.submissionId = submissionId;
		this.problemId = problemId;
	}

	/**
	 * @return the submission Id
	 */
	public String getSubmissionId() {
		return submissionId;
	}

	/**
	 * @param problemId the submission Id to set
	 */
	public void setSubmissionId(String submissionId) {
		this.submissionId = submissionId;
	}

	/**
	 * @return the problemId
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param problemId the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * Returns true if this as a regular submission, 
	 * otherwise is a validation
	 * @return the consider
	 */
	public boolean isConsider() {
		return consider;
	}

	/**
	 * If true then consider this as a regular submission, 
	 * otherwise is a validation
	 * @param consider the consider to set
	 */
	public void setConsider(boolean consider) {
		this.consider = consider;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReportNotificationEvent [submissionId=" + submissionId
				+ ", problemId=" + problemId + ", consider=" + consider
				+ ", date=" + date + ", recipient=" + recipient + "]";
	}

	

}
