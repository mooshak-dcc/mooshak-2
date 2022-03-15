package pt.up.fc.dcc.mooshak.client.form.admin;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

/**
 * Text area as a Mooshak widget
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class CustomTextArea extends Composite implements MooshakWidget {

	private static CustomTextAreaUiBinder uiBinder = 
			GWT.create(CustomTextAreaUiBinder.class);

	@UiTemplate("CustomTextArea.ui.xml")
	interface CustomTextAreaUiBinder extends UiBinder<Widget, CustomTextArea> {
	}
	
	@UiField
	TextArea textArea;
	
	private String field= null;
	
	private FormStyle style = FormResources.INSTANCE.style();

	private boolean isEditing = false;

	CustomTextArea() {
		initWidget(uiBinder.createAndBindUi(this));

		textArea.addBlurHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				isEditing = false;
			}
		});
		textArea.addFocusHandler(new FocusHandler() {

			@Override
			public void onFocus(FocusEvent event) {
				isEditing = true;
			}
		});
		
		style.ensureInjected();
		getElement().setClassName(style.entry());
	}

	@Override
	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public MooshakValue getValue() {
		
		return new MooshakValue(field,textArea.getValue());
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value,false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		field = value.getField();
		
		textArea.setValue(value.getSimple(),fireEvents);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {
		
		return textArea.addValueChangeHandler(new ValueChangeHandler<String>() {
			
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