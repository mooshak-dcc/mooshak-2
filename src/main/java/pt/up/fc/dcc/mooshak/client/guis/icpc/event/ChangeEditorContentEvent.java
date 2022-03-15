package pt.up.fc.dcc.mooshak.client.guis.icpc.event;

import com.google.gwt.event.shared.GwtEvent;

public class ChangeEditorContentEvent extends GwtEvent<ChangeEditorContentEventHandler> {

	public static Type<ChangeEditorContentEventHandler> TYPE = new Type<ChangeEditorContentEventHandler>();

	private String id;
	private String team;
	private String problemId;

	public ChangeEditorContentEvent(String id, String team, String problemId) {
		this.setId(id);
		this.setTeam(team);
		this.problemId = problemId;
	}

	@Override
	public Type<ChangeEditorContentEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ChangeEditorContentEventHandler handler) {
		handler.onChangeEditorContent(this);
	}

	/**
	 * @return the problemId
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param problemId
	 *            the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * @return the team
	 */
	public String getTeam() {
		return team;
	}

	/**
	 * @param team
	 *            the team to set
	 */
	public void setTeam(String team) {
		this.team = team;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

}
