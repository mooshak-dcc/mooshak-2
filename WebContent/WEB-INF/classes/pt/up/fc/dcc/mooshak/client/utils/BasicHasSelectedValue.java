package pt.up.fc.dcc.mooshak.client.utils;

import java.util.ArrayList;
import java.util.Collection;

import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * An object implementing the HasSelectedValue interface for testing purposes
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 *
 */
public class BasicHasSelectedValue implements HasSelectedValue<SelectableOption> {

	Collection<SelectableOption> data = new ArrayList<SelectableOption>();
	
	@Override
	public SelectableOption getValue() {
		return data.iterator().next();
	}

	@Override
	public void setValue(SelectableOption value) {
		getSelectedOption();
	}

	@Override
	public void setValue(SelectableOption value, boolean fireEvents) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<SelectableOption> handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void fireEvent(GwtEvent<?> event) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public void setSelections(Collection<SelectableOption> selections) {
		data = selections;
		
	}

	@Override
	public void setSelectedValue(SelectableOption selected) {
		// TODO Auto-generated method stub
	}

	@Override
	public SelectableOption getSelectedOption() {
		if(data.size() == 0)
			return null;
		else
			return data.iterator().next();
	}

	@Override
	public Collection<SelectableOption> getSelections() {
		return data;
	}	
}