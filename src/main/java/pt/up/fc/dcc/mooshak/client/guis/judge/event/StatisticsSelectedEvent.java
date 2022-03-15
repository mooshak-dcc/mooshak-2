package pt.up.fc.dcc.mooshak.client.guis.judge.event;

import com.google.gwt.event.shared.GwtEvent;

public class StatisticsSelectedEvent 
	extends GwtEvent<StatisticsSelectedEventHandler> {

	public static Type<StatisticsSelectedEventHandler> TYPE = 
			new Type<StatisticsSelectedEventHandler>();
	
	public StatisticsSelectedEvent() {
	}

	@Override
	public Type<StatisticsSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(StatisticsSelectedEventHandler handler) {
		handler.onStatisticsSelected(this);
	}

}
