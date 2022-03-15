package pt.up.fc.dcc.mooshak.client.form.admin;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

/**
 * Radio Button with the MooshakWidget interface	
 */
public class CustomRadioButton extends Composite implements MooshakWidget {

	private static CustomTextBoxUiBinder uiBinder = 
			GWT.create(CustomTextBoxUiBinder.class);

	@UiTemplate("CustomRadioButton.ui.xml")
	interface CustomTextBoxUiBinder extends UiBinder<Widget, CustomRadioButton> {
	}
	
	@UiField
	RadioButton radioButton;
	
	private FormStyle style = FormResources.INSTANCE.style();
	
	private boolean isEditing = false;

	private String field = null;

	CustomRadioButton() {
		initWidget(uiBinder.createAndBindUi(this));
		
		style.ensureInjected();
		radioButton.getElement().setClassName(style.entry());
	}
	
	public void setText(String text) {
		radioButton.setText(text);
	}
	
	public String getText() {
		return radioButton.getText();
	}

	public void setChecked(String text) {
		setValue(new MooshakValue(field, text));
	}

	public void setName(String text) {
		radioButton.setName(text);
	}

	@Override
	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public MooshakValue getValue() {
		if(radioButton.getValue().booleanValue())
			return new MooshakValue(field, getText());
		return new MooshakValue(field,radioButton.getValue().toString());
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value,false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		field = value.getField();
		
		radioButton.setValue(Boolean.parseBoolean(value.getSimple()),fireEvents);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {
		
		return radioButton.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> originalEvent) {
				if(!originalEvent.getValue().booleanValue())
					return;
				
				MooshakValue value = new MooshakValue(
						field ,
						getText());
				ValueChangeEvent<MooshakValue> event = 
						new ValueChangeEvent<MooshakValue>(value){};
						
				handler.onValueChange(event);
				
			}
		} );
	}
}