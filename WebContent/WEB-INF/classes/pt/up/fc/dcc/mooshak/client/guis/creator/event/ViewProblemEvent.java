package pt.up.fc.dcc.mooshak.client.guis.creator.event;

import com.google.gwt.event.shared.GwtEvent;

public class ViewProblemEvent extends GwtEvent<ViewProblemEventHandler> {
	
	public static Type<ViewProblemEventHandler> TYPE = 
			new Type<ViewProblemEventHandler>();
	
	private String problemId;
	private String problemLabel;
	
	public ViewProblemEvent(String problemId, String problemLabel) {
		this.setProblemId(problemId);
		this.setProblemLabel(problemLabel);
	}

	@Override
	public Type<ViewProblemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewProblemEventHandler handler) {
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
