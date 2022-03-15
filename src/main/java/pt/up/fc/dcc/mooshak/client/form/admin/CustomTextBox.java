package pt.up.fc.dcc.mooshak.client.form.admin;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Text box as a Mooshak widget
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class CustomTextBox extends Composite implements MooshakWidget {

	private static CustomTextBoxUiBinder uiBinder = GWT
			.create(CustomTextBoxUiBinder.class);

	@UiTemplate("CustomTextBox.ui.xml")
	interface CustomTextBoxUiBinder extends UiBinder<Widget, CustomTextBox> {
	}

	@UiField
	TextBox textBox;

	private FormStyle style = FormResources.INSTANCE.style();

	private boolean isEditing = false;

	private String field = null;

	private int maxLength = Integer.MAX_VALUE;

	CustomTextBox() {
		initWidget(uiBinder.createAndBindUi(this));
		textBox.sinkEvents(Event.ONPASTE | Event.ONKEYDOWN);

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

		textBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				String str;
				try {
					str = textBox.getText();
				} catch (Exception e) {
					str = "";
				}

				if (str.length() >= maxLength 
						&& !event.isLeftArrow()
						&& !event.isRightArrow()
						&& event.getNativeKeyCode() != KeyCodes.KEY_TAB
						&& event.getNativeKeyCode() != KeyCodes.KEY_DELETE
						&& event.getNativeKeyCode() != KeyCodes.KEY_BACKSPACE
						&& event.getNativeKeyCode() != KeyCodes.KEY_SHIFT
						&& event.getNativeKeyCode() != KeyCodes.KEY_CTRL) {
					event.preventDefault();
				}
				
				if (maxLength < Integer.MAX_VALUE)
					if (event.getNativeKeyCode() == KeyCodes.KEY_CTRL)
						event.preventDefault();
			}
		});

		style.ensureInjected();
		textBox.getElement().setClassName(style.entry());
	}

	@Override
	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public MooshakValue getValue() {

		return new MooshakValue(field, textBox.getValue());
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value, false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		field = value.getField();

		textBox.setValue(value.getSimple(), fireEvents);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {

		return textBox.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> originalEvent) {
				MooshakValue value = new MooshakValue(field, originalEvent
						.getValue().substring(
								0,
								Math.min(maxLength, originalEvent.getValue()
										.length())));
				ValueChangeEvent<MooshakValue> event = new ValueChangeEvent<MooshakValue>(
						value) {
				};

				handler.onValueChange(event);

			}
		});
	}

	public int getMaxLength() {
		return this.maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}
}