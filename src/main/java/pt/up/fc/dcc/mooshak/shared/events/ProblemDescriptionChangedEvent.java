package pt.up.fc.dcc.mooshak.shared.events;

/**
 * Event with fired by changes on problem's description
 * 
 * @author josepaiva
 */
public class ProblemDescriptionChangedEvent extends MooshakEvent {

	private String problemId;
	
	public ProblemDescriptionChangedEvent() {
		super();
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
