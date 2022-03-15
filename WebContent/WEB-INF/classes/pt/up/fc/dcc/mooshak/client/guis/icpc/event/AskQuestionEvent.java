package pt.up.fc.dcc.mooshak.client.guis.icpc.event;

import com.google.gwt.event.shared.GwtEvent;

public class AskQuestionEvent extends GwtEvent<AskQuestionEventHandler> {
	
	public static Type<AskQuestionEventHandler> TYPE = 
			new Type<AskQuestionEventHandler>();
	
	private String problemId;
	private String problemName;
	
	public AskQuestionEvent(String problemId, String problemName) {
		this.setProblemId(problemId);
		this.setProblemName(problemName);
	}
	
	@Override
	public Type<AskQuestionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(AskQuestionEventHandler handler) {
		handler.onAskQuestion(this);
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

	public String getProblemName() {
		return problemName;
	}

	public void setProblemName(String problemName) {
		this.problemName = problemName;
	}

}