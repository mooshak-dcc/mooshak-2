package pt.up.fc.dcc.mooshak.client.utils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * An object implementing the HasClickHandlers interface for testing purposes
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 *
 */
public class BasicHasClickHandlers implements HasClickHandlers {

	ClickHandler handler;
	
	@Override
	public void fireEvent(GwtEvent<?> event) {
		// TODO Auto-generated method stub

	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		this.handler = handler;
		return null;
	}

	public void press() {
		// ClickEvent has a protected constructor, create an anonymous extension
		handler.onClick(new ClickEvent() {});
	}
}