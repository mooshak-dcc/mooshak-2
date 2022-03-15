package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Tab buttons of the ICPC interface.
 * They must be bound to groups so that when select 
 * they automatically remove selection from buttons in the same group  
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class TabButton extends Composite  
implements HasText, HasClickHandlers, ClickHandler {
	
	interface BaseStyle extends CssResource {
		    String selected();
		    String tab();
		    String problem();
		    String listing();
	}

	private String id;
	
	@UiField BaseStyle style; 
	
	@UiField(provided=true)
    Label label = new Label();

    @UiField(provided=true)
    FocusPanel pane = new FocusPanel();
	
    boolean selected;
    
    String group;
    
    static HashMap<String,List<TabButton>> inGroup =
    		new HashMap<String,List<TabButton>>();
    
    static HashMap<String,TabButton> currentSelection = 
    		new HashMap<String,TabButton>();
    
    private static TabButtonUiBinder uiBinder = GWT
			.create(TabButtonUiBinder.class);

	interface TabButtonUiBinder extends
			UiBinder<Widget, TabButton> {
	}
	
	public TabButton() {
		pane.addClickHandler(this); 
	    initWidget(uiBinder.createAndBindUi(this));
	}
    
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
	      	return addHandler(handler, ClickEvent.getType());
	}
	 
	@Override
	public void onClick(ClickEvent event) {
		if(this.group.equals("problemActions"))
			return;
		
		select();
		event.preventDefault();
	    pane.setFocus(false);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
    public String getText() {
        return label.getText();
    }
 
    @Override
    public void setText(String text) {
        label.setText(text);
    }

	/** Is this tab button currently marked as selected **/
    public boolean isSelected() {
    	return selected;
    }
    
    /**
     * Set selection according to parameter
     * This method affects only this button and doesn't
     * change the previously selected button.
     * For that purpose use {@link #select}
     * @param selected
     */
    public void setSelected(boolean selected) {
    	this.selected = selected;
    	if(selected)
    		pane.getElement().addClassName(style.selected());
    	else
    		pane.getElement().removeClassName(style.selected());
    }

    /**
     * Returns the selection group to which this button is bound 
     * @return
     */
	public String getGroup() {
		return group;
	}

	
	/**
	 * Bind this button to a selection group. When a button is selected
     * all other buttons of its groups automatically lose selection
	 * @param group
	 */
	public void setGroup(String group) {
		this.group = group;
		
		List<TabButton> buttons;
		if(inGroup.containsKey(group))
			buttons = inGroup.get(group);
		else {
			buttons = new ArrayList<TabButton>();
			inGroup.put(group,buttons);
		}
		buttons.add(this);
	}

    /**
     * Select this button and remove selection from the previously 
     * select button in this group 
     */
    public void select() {
    	deselect(group);
    	this.setSelected(true);
    	currentSelection.put(group, this);
    }
    
    /**
     * Remove selection from any button in given list of groups 
     * @param groups names of groups that must lose selection
     */
    public static void deselect(String... groups) {
    	
    	for(final String group: groups) {
    		List<TabButton> buttons = inGroup.get(group);
    	
    		if(buttons != null)
    			for(TabButton button :buttons)
    				button.setSelected(false);
    	}
    }

    /**
     * Assign given style to this button
     * @param name of class
     */
    public void styleAs(String name) {
    	
    	String className = null;
		if("tab".equals(name))
    		className = style.tab();
		else if("problem".equals(name))
			className = style.problem();
		else if("listing".equals(name))
			className = style.listing();
		
		if(className != null)
			pane.getElement().setClassName(className);
  
    }
    
    /**
     * Get current selection of given group. 
     * If no button in group was selected yet then
     * force the selection of the first element in the group.
     * If selection is not visible (because group was deselected)
     * then make it visible.
     * @param group
     * @return
     */
    public static TabButton getSelection(String group) {
    	TabButton select = currentSelection.get(group);
    	
    	if(select==null)
    		select = inGroup.get(group).get(0);
    	select.select();
    	return currentSelection.get(group);
    }
}
