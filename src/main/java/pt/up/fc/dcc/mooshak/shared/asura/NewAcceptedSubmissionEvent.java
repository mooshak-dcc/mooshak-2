package pt.up.fc.dcc.mooshak.shared.asura;

import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;

/**
 * Event to notify students of a new accepted submission
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class NewAcceptedSubmissionEvent extends MooshakEvent {

	private String teamId;
	private String problemId;
	private String submissionId;
	
	public NewAcceptedSubmissionEvent() {
		super();
	}

	/**
	 * @return the teamId
	 */
	public String getTeamId() {
		return teamId;
	}

	/**
	 * @param teamId the teamId to set
	 */
	public void setTeamId(String teamId) {
		this.teamId = teamId;
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
}
