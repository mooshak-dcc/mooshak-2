package pt.up.fc.dcc.mooshak.client.guis.icpc.event;

import com.google.gwt.event.shared.GwtEvent;

public class ViewStatementEvent extends GwtEvent<ViewStatementEventHandler> {
	
	public static Type<ViewStatementEventHandler> TYPE = 
			new Type<ViewStatementEventHandler>();
	
	private String problemId;
	private String problemLabel;
	
	public ViewStatementEvent(String problemId, String problemLabel) {
		this.setProblemId(problemId);
		this.setProblemLabel(problemLabel);
	}

	@Override
	public Type<ViewStatementEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewStatementEventHandler handler) {
		handler.onViewStatement(this);
	}

	/**
	 * @return the problem id
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param problem the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * @return the problemLabel
	 */
	public String getProblemLabel() {
		return problemLabel;
	}

	/**
	 * @param problemLabel the problemLabel to set
	 */
	public void setProblemLabel(String problemLabel) {
		this.problemLabel = problemLabel;
	}
	
}
