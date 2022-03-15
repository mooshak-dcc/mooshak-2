/**
 * 
 */
package pt.up.fc.dcc.mooshak.client.form.admin;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Textbox containing only digits and '.'
 * @author josepaiva
 */
public class DoubleBox extends Composite implements MooshakWidget {

	private FormStyle style = FormResources.INSTANCE.style();

	private static CustomTextBoxUiBinder uiBinder = 
			GWT.create(CustomTextBoxUiBinder.class);

	@UiTemplate("CustomTextBox.ui.xml")
	interface CustomTextBoxUiBinder extends UiBinder<Widget, DoubleBox> {
	}
	
	@UiField
	TextBox textBox;
	
	private String field = null;
	
	private boolean isEditing = false;
	
	DoubleBox() {
		initWidget(uiBinder.createAndBindUi(this));
		
		textBox.setAlignment(TextBox.TextAlignment.RIGHT);
		
		textBox.addKeyPressHandler(new KeyPressHandler() {
		
			@Override
			public void onKeyPress(KeyPressEvent event) {
				char code = event.getCharCode();
				if((code < '0' || code > '9') &&
						code != '.')
					event.preventDefault();
			}
		});
		
		textBox.addBlurHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				isEditing = false;
			}
		});
		textBox.addFocusHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				isEditing = true;
			}
		});
		
		style.ensureInjected();
		textBox.getElement().setClassName(style.entry());
	}

	@Override
	public boolean isEditing() {
		return isEditing ;
	}

	@Override
	public MooshakValue getValue() {
		
		return new MooshakValue(field,textBox.getValue());
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value,false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		field = value.getField();
		
		textBox.setValue(value.getSimple(),fireEvents);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {
		
		return textBox.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> originalEvent) {
				MooshakValue value = new MooshakValue(
						field,
						originalEvent.getValue());
				ValueChangeEvent<MooshakValue> event = 
						new ValueChangeEvent<MooshakValue>(value){};
						
				handler.onValueChange(event);
				
			}
		} );
	}
}
