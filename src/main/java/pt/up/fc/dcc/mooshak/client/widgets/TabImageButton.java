package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
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
import com.google.gwt.user.client.ui.Widget;

/**
 * Tab buttons with images of the ICPC interface.
 * They must be bound to groups so that when select 
 * they automatically remove selection from buttons in the same group  
 * 
 * @author josepaiva
 *
 */
public class TabImageButton extends Composite  
	implements HasText, HasClickHandlers, ClickHandler {
	
	interface BaseStyle extends CssResource {
		    String selected();
		    String tab();
		    String problem();
		    String listing();
	}

	private String id;
	
	@UiField BaseStyle style; 
	
	@UiField
    CustomImageButton image;

    @UiField(provided=true)
    FocusPanel pane = new FocusPanel();
	
    boolean selected;
    
    String group;
    
    static HashMap<String,List<TabImageButton>> inGroup =
    		new HashMap<String,List<TabImageButton>>();
    
    static HashMap<String,TabImageButton> currentSelection = 
    		new HashMap<String,TabImageButton>();
    
    private static TabButtonUiBinder uiBinder = GWT
			.create(TabButtonUiBinder.class);

	interface TabButtonUiBinder extends
			UiBinder<Widget, TabImageButton> {
	}
	
	public TabImageButton() {
		pane.addClickHandler(this); 
	    initWidget(uiBinder.createAndBindUi(this));
	    
	    Style style = image.getElement().getStyle();
	    style.setMarginTop(5, Unit.PX);
	    style.setMarginLeft(10, Unit.PX);
	}
    
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		image.addClickHandler(handler);
	      	return addHandler(handler, ClickEvent.getType());
	}
	 
	@Override
	public void onClick(ClickEvent event) {
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
        return getTitle();
    }
 
    @Override
    public void setText(String text) {
        setTitle(text);
        image.setTitle(text);
    }
    
    public void setUrl(String url) {
    	image.setUrl(url);
    }
    
    public void setPixelSize(int width, int height) {
    	image.setPixelSize(width, height);
    }
    
    public void setHeight(String height) {
    	image.setHeight(height);
    }
    
    public void setWidth(String width) {
    	image.setWidth(width);
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
		
		List<TabImageButton> buttons;
		if(inGroup.containsKey(group))
			buttons = inGroup.get(group);
		else {
			buttons = new ArrayList<TabImageButton>();
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
    		List<TabImageButton> buttons = inGroup.get(group);
    	
    		if(buttons != null)
    			for(TabImageButton button :buttons)
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
    public static TabImageButton getSelection(String group) {
    	TabImageButton select = currentSelection.get(group);
    	
    	if(select==null)
    		select = inGroup.get(group).get(0);
    	select.select();
    	return currentSelection.get(group);
    }
}
