package pt.up.fc.dcc.mooshak.client.guis.judge.event;

import com.google.gwt.event.shared.GwtEvent;

public class CommentProblemEvent extends GwtEvent<CommentProblemEventHandler> {
	
	public static Type<CommentProblemEventHandler> TYPE = 
			new Type<CommentProblemEventHandler>();
	
	public CommentProblemEvent() {
		
	}
	
	@Override
	public Type<CommentProblemEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CommentProblemEventHandler handler) {
		handler.onCommentProblem(this);
	}
	
}
