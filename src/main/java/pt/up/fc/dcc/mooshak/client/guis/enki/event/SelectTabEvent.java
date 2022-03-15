package pt.up.fc.dcc.mooshak.client.guis.enki.event;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;

public class SelectTabEvent extends GwtEvent<SelectTabEventHandler> {
	
	public static Type<SelectTabEventHandler> TYPE = 
			new Type<SelectTabEventHandler>();
	
	private Widget widget;
	
	public SelectTabEvent(Widget widget) {
		this.setWidget(widget);
	}

	@Override
	public Type<SelectTabEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(SelectTabEventHandler handler) {
		handler.onSelectTab(this);
	}

	/**
	 * @return the widget
	 */
	public Widget getWidget() {
		return widget;
	}

	/**
	 * @param widget the widget to set
	 */
	public void setWidget(Widget widget) {
		this.widget = widget;
	}

}
