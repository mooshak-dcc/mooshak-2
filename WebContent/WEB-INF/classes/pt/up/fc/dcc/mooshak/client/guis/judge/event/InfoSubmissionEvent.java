package pt.up.fc.dcc.mooshak.client.guis.judge.event;

import com.google.gwt.event.shared.GwtEvent;

public class InfoSubmissionEvent extends GwtEvent<InfoSubmissionEventHandler> {
	
	public static Type<InfoSubmissionEventHandler> TYPE = 
			new Type<InfoSubmissionEventHandler>();
	
	private String submissionId;
	private String problemId;
	
	public InfoSubmissionEvent(String submissionId,
			String problemId) {
		this.submissionId = submissionId;
		this.setProblemId(problemId);
	}
	
	@Override
	public Type<InfoSubmissionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(InfoSubmissionEventHandler handler) {
		handler.onInfoSubmission(this);
	}
	
	/**
	 * @return the submissionId
	 */
	public String getSubmissionId() {
		return submissionId;
	}

	/**
	 * @param submissionId the submissionId to set
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

}
