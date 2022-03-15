package pt.up.fc.dcc.mooshak.shared.events;

public class ProblemTestSummary extends MooshakEvent {

	private String problemId;
	private String text;
	
	
	public ProblemTestSummary() {
		super();
	}
	
	public ProblemTestSummary(Recipient recipient, String text, String problemId) {
		this.recipient = recipient;
		this.text = text;
		this.problemId = problemId;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
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
