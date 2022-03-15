package pt.up.fc.dcc.mooshak.client.guis.icpc.event;

import com.google.gwt.event.shared.GwtEvent;

public class HelpTutorialEvent extends GwtEvent<HelpTutorialEventHandler> {
	
	public static Type<HelpTutorialEventHandler> TYPE = 
			new Type<HelpTutorialEventHandler>();
	
	public HelpTutorialEvent() {
	}
	
	@Override
	public Type<HelpTutorialEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(HelpTutorialEventHandler handler) {
		handler.onHelpTutorial(this);
	}
	
}