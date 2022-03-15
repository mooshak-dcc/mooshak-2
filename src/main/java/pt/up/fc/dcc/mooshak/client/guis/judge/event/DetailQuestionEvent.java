package pt.up.fc.dcc.mooshak.client.guis.judge.event;

import com.google.gwt.event.shared.GwtEvent;

public class DetailQuestionEvent extends GwtEvent<DetailQuestionEventHandler> {
	
	public static Type<DetailQuestionEventHandler> TYPE = 
			new Type<DetailQuestionEventHandler>();
	
	private String questionId;
	
	public DetailQuestionEvent(String questionId) {
		this.questionId = questionId;
	}
	
	@Override
	public Type<DetailQuestionEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(DetailQuestionEventHandler handler) {
		handler.onDetailQuestion(this);
	}
	
	/**
	 * @return the questionId
	 */
	public String getQuestionId() {
		return questionId;
	}

	/**
	 * @param questionId the questionId to set
	 */
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}


}
