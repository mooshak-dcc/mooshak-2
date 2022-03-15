package pt.up.fc.dcc.mooshak.client.utils;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;

/**
 * An object implementing the HasValue<T> interface for testing purposes
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 *
 * @param <T>
 */
public class BasicHasValue<T> implements HasValue<T> {

	T value;
	
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<T> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public T getValue() {
		return value;
	}

	@Override
	public void setValue(T value) {
		this.value = value;
	}

	@Override
	public void setValue(T value, boolean fireEvents) {
		this.value = value;
	}

}
