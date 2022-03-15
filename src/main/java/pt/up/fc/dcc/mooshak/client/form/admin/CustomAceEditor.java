package pt.up.fc.dcc.mooshak.client.form.admin;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

/**
 * AceEditor widget that implements MooshakWidget 
 * 
 * @author josepaiva
 */
public class CustomAceEditor extends Composite
	implements MooshakWidget {
	
	private boolean isEditing = false;
	private String field = null;
	
	private AceEditor aceEditor = new AceEditor();
	
	// Used for firing change events
	private String lastText;
	
	public CustomAceEditor() {
		HorizontalPanel panel = new HorizontalPanel();
		panel.add(aceEditor);
		
		
		configureAceEditor();
		panel.setCellHeight(aceEditor, "100%");
		panel.getElement().getStyle().setPosition(Position.RELATIVE);
		initWidget(panel);
		
		final Element ace = aceEditor.getElement().getElementsByTagName("textarea")
				.getItem(0);
		Event.sinkEvents(ace, Event.ONFOCUS | Event.ONBLUR);
	    Event.setEventListener(ace, new EventListener() {

	        @Override
	        public void onBrowserEvent(Event event) {
	             if(Event.ONFOCUS == event.getTypeInt()) {
	            	 isEditing = true;
	            	 lastText = aceEditor.getText();
	             }
	             else if(Event.ONBLUR == event.getTypeInt()) {
	            	 isEditing = false;
	            	 aceEditor.fireEvent(new ValueChangeEvent<String>(
	            				 aceEditor.getValue()){});
	             }
	        }
	    });
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value, false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		field = value.getField();
		
		try {
			aceEditor.setValue(new String(value.getContent(), "UTF-8"));
			enableEditing();
		} catch (Exception e) {
			aceEditor.setValue("Content preview not available");
			disableEditing();
		}
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {
		
		return aceEditor.addHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> originalEvent) {
				if(field == null)
					return;

				MooshakValue value = new MooshakValue(
						field, "tobeSetAfter",
						originalEvent.getValue().getBytes());
				ValueChangeEvent<MooshakValue> event = 
						new ValueChangeEvent<MooshakValue>(value){};

				handler.onValueChange(event);
			}
		}, ValueChangeEvent.getType() );
	}

	@Override
	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public MooshakValue getValue() {
		return new MooshakValue(field, "toBeSetAfter", 
				aceEditor.getValue().getBytes());
	}

	/** AceEditor methods  **/
	private void configureAceEditor() {
		aceEditor.startEditor();
		aceEditor.setMode(AceEditorMode.JAVA);
		aceEditor.setTheme(AceEditorTheme.ECLIPSE);
		aceEditor.getElement().getStyle().setHeight(418, Unit.PX);
		aceEditor.getElement().setId("fileContent");
	}

	public void setMode(AceEditorMode mode) {
		aceEditor.setMode(mode);
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(int height) {
		aceEditor.setHeight(height+"px");
	}

	/**
	 * @param height the height to set
	 */
	public void setHeight(String height) {
		aceEditor.setHeight(height);
	}

	public String getText() {
		return aceEditor.getText();
	}
	
	public void disableEditing() {
		aceEditor.setReadOnly(true);
	}
	
	public void enableEditing() {
		aceEditor.setReadOnly(false);
	}

	public void setField(String field) {
		this.field = field;
	}

	public AceEditor getEditor() {
		return aceEditor;
	}
	

}
