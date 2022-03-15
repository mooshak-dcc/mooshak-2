package pt.up.fc.dcc.mooshak.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
 
/**
 * A ListBox that can be populated with any Collection
 * 
 * Published under Apache License v2
 * 
 * * Changed getSelectedValue() to getSelectedOption() for GWT SDK 2.7
 * by José Paulo Leal <zp@dcc.fc.up.pt>
 * 
 * @author David Chandler
 * @param <T>
 */
public class SelectOneListBox<T> extends Composite implements HasSelectedValue<T>
{
 
    public interface OptionFormatter<T>
    {
        abstract String getLabel(T option);
        abstract String getValue(T option);
    }
     
    private boolean valueChangeHandlerInitialized;
    private T[] options;
    private OptionFormatter<T> formatter;
    
    private ListBox listBox = new ListBox();
 
    public SelectOneListBox()
    {
    	listBox.setMultipleSelect(false);
    	initWidget(listBox);
    }
    public SelectOneListBox(Collection<T> selections, OptionFormatter<T> formatter)
    {
        setSelections(selections);
        setFormatter(formatter);
    }
 
    public SelectOneListBox(OptionFormatter<T> formatter)
    {
        this(new ArrayList<T>(), formatter);
    }
 
    public void setFormatter(OptionFormatter<T> formatter)
    {
        this.formatter = formatter; 
    }
 
    @SuppressWarnings("unchecked")
    @Override
    public void setSelections(Collection<T> selections)
    {
        // Remove prior options
        if (options != null)
        {
            int numItems = listBox.getItemCount();
            int firstOption = numItems - options.length;
            for (int i=firstOption; i<numItems; i++)
            	listBox.removeItem(firstOption);
        }
        options = (T[]) selections.toArray();
        for (T option : options)
        {
            String optionLabel = formatter.getLabel(option);
            String optionValue = formatter.getValue(option);
            listBox.addItem(optionLabel, optionValue);
        }
    }
 
    @Override
    public T getSelectedOption()
    {
        if (listBox.getSelectedIndex() >= 0)
        {
            String name = listBox.getValue(listBox.getSelectedIndex());
 
            for (T option : options)
            {
                if (formatter.getValue(option).equals(name))
                    return option;
            }
        }
 
        return null;
    }
 
    @Override
    public void setSelectedValue(T value)
    {
        if (value == null)
            return;
         
        for (int i=0; i < listBox.getItemCount(); i++)
        {
            String thisLabel = listBox.getItemText(i);
            if (formatter.getLabel(value).equals(thisLabel))
            {
            	listBox.setSelectedIndex(i);
                return;
            }
        }
        throw new IllegalArgumentException("No index found for label " + value.toString());
    }
 
    /*
     * Methods to implement HasValue 
     */
     
    @Override
    public T getValue()
    {
        return this.getSelectedOption();
    }
 
    @Override
    public void setValue(T value)
    {
        this.setValue(value, false);
    }
 
    @Override
    public void setValue(T value, boolean fireEvents)
    {
        T oldValue = getValue();
        this.setSelectedValue(value);
        if (fireEvents)
        {
            ValueChangeEvent.fireIfNotEqual(this, oldValue, value);
        }
    }
 
    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler)
    {
        // Initialization code
        if (!valueChangeHandlerInitialized)
        {
            valueChangeHandlerInitialized = true;
            listBox.addChangeHandler(new ChangeHandler()
            {
                public void onChange(ChangeEvent event)
                {
                    ValueChangeEvent.fire(SelectOneListBox.this, getValue());
                }
            });
        }
        return addHandler(handler, ValueChangeEvent.getType());
    }
    
	@Override
	public Collection<T> getSelections() {
		List<T> listOptions = new ArrayList<>();
		for (T option : options) {
            listOptions.add(option);
        }
		return listOptions;
	}
	
	public void setItemSelected(int j, boolean b) {
		listBox.setItemSelected(j, b);
	}
	
	public String getItemText(int j) {
		return listBox.getItemText(j);
	}
	
	public int getItemCount() {
		return listBox.getItemCount();
	}
 
	public HandlerRegistration addChangeHandler(ChangeHandler handler) {
		return listBox.addChangeHandler(handler);
	}
}