package pt.up.fc.dcc.mooshak.client.guis.icpc.event;

import com.google.gwt.event.shared.GwtEvent;

public class EditProgramEvent extends GwtEvent<EditProgramEventHandler> {
	
	public static Type<EditProgramEventHandler> TYPE = 
			new Type<EditProgramEventHandler>();
	
	private String problemId;
	
	public EditProgramEvent(String problemId) {
		this.setProblemId(problemId);
	}
	
	@Override
	public Type<EditProgramEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(EditProgramEventHandler handler) {
		handler.onEditProgram(this);
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
