package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ListBox;

/**
 * List box with the MooshakWidget interface	
 */
public class TextListBox extends ListBox implements MooshakWidget {
	public static final String PATTERN_MULTIPLE = "\\{([^\\}]*)\\}*";
	public static final RegExp REGEX_MULTIPLE = RegExp.compile(PATTERN_MULTIPLE,"g");
	public static final RegExp blancs = RegExp.compile("\\s+");

	private boolean isEditing = false;
	private String field = null;
	
	public TextListBox() {
		super();
	}

	private static final int NUMBER_OF_VISIBLE_ELEMENTS = 5;
	FormStyle style = FormResources.INSTANCE.style();
	
	public TextListBox(List<String> items, boolean multipleSelect) {
		super();
		setMultipleSelect(multipleSelect);

		if(multipleSelect)
			setVisibleItemCount(NUMBER_OF_VISIBLE_ELEMENTS);
		
		style.ensureInjected();
		
		getElement().setClassName(style.entry());
		
		resetItemList(items);
		
		addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				isEditing = false;
			}
		});
		addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				isEditing = true;
			}
		});
	}  
	
	/**
	 * Replace the existing item list by the given list
	 * @param items
	 */
	public void resetItemList(List<String> items) {
		clear();
		
		Collections.sort(items, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {

				if (o1 == null)
					return 1;
				if (o2 == null)
					return -1;
				return o1.compareToIgnoreCase(o2);
			}
		});
		
		addItem("");
		if(items != null)	
			for(String item: items)
				addItem(item);
	}
	
	  
	@Override
	public MooshakValue getValue() {
	    
	    String value = null;
	    
	    if (!isMultipleSelect()) {
	    	int selected = getSelectedIndex();
		    if(selected == -1)
		    	value = "";
		    else 
		    	value = getItemText(selected);
	    } else {
	    	List<String> selected = new ArrayList<String>();
	    	for (int i = 0; i < getItemCount(); i++) {
	    		if (isItemSelected(i))
	    			selected.add(getItemText(i));
			}
	    	
			StringBuilder text = new StringBuilder();
			boolean first = true;
			for (String item: selected) {
				if(first)
					first = false;
				else
					text.append(' ');
				
				text.append('{');
				text.append(item);
				text.append('}');
				
			}
				
			value = text.toString();
			Window.alert(value);
	    }
	    
	    return new MooshakValue(field, value);
	}

	@Override
	public void setValue(MooshakValue value) {
		
		setValue(value,false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		
		if (!isMultipleSelect()) {
			setSelectedIndex(0);
			for(int index=0; index< getItemCount(); index++)
				if(getItemText(index).equals(value.getSimple()))
					setSelectedIndex(index);
		} else {
			
			String multipleValue = value.getSimple();
			MatchResult result = REGEX_MULTIPLE.exec(multipleValue);
			while (!multipleValue.isEmpty() && result != null) {
				for(int index=0; index< getItemCount(); index++)
					if(getItemText(index).equals(result.getGroup(1)))
						setItemSelected(index, true);
				result = REGEX_MULTIPLE.exec(multipleValue);
			}
		}
		
		field = value.getField();

		if(fireEvents)
			fireEvent(new ChangeEvent(){});
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {
		
		return addChangeHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent originalEvent) {
				ValueChangeEvent<MooshakValue> event = new ValueChangeEvent<MooshakValue>(
						getValue()){};
				handler.onValueChange(event);
			}
			
		});
	}

	@Override
	public boolean isEditing() {
		return isEditing;
	}
	  
 }