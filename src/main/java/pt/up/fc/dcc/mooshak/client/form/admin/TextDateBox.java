package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.events.ChangeDateEvent;
import org.gwtbootstrap3.extras.datetimepicker.client.ui.base.events.ChangeDateHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.widgets.CustomDateTimePicker;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

public class TextDateBox extends Composite implements MooshakWidget {
	private static final long SECONDS_IN_MILLI = 1000;

	private static TextDateBoxUiBinder uiBinder = GWT.create(TextDateBoxUiBinder.class);

	@UiTemplate("TextDateBox.ui.xml")
	interface TextDateBoxUiBinder extends UiBinder<Widget, TextDateBox> {
	}

	@UiField
	CustomDateTimePicker dateTextBox;

	private String field = null;
	
	private boolean editing = false;
	
	private Date current = null;

	public TextDateBox() {
		initWidget(uiBinder.createAndBindUi(this));
		
		dateTextBox.getTextBox().sinkEvents(Event.ONFOCUS | Event.ONBLUR);
		
		dateTextBox.addDomHandler(new BlurHandler() {
			
			@Override
			public void onBlur(BlurEvent event) {
				editing = false;

				if (current != null) {
					if (dateTextBox.getValue() == null || !current.equals(dateTextBox.getValue()))
						dateTextBox.fireEvent(new ChangeDateEvent(null));
					return;
				} else if (dateTextBox.getValue() != null) {
					dateTextBox.fireEvent(new ChangeDateEvent(null));
				}
			}
		}, BlurEvent.getType());
		
		dateTextBox.addDomHandler(new FocusHandler() {
			
			@Override
			public void onFocus(FocusEvent event) {
				editing = true;
			}
		}, FocusEvent.getType());
	}

	@Override
	public MooshakValue getValue() {
		Date date = dateTextBox.getValue();
		String value = null;
		if (date != null)
			value = Long.toString(date.getTime() / SECONDS_IN_MILLI);
		return new MooshakValue(field, value);
	}

	@Override
	public void setValue(MooshakValue value) {

		if (isEditing())
			return;
		setValue(value, false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		if ("".equals(value.getSimple()) || value.getSimple() == null) {
			current = null;
			dateTextBox.setValue(null, fireEvents);
		} else {
			try {
				Date date = new Date(Long.parseLong(value.getSimple()) * SECONDS_IN_MILLI);
				current = date;
				dateTextBox.setValue(date, fireEvents);
			} catch (Exception cause) {
				final Logger logger = Logger.getLogger("");
				logger.log(Level.INFO, "Parsing date as long", cause);
			}
		}

		field = value.getField();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<MooshakValue> handler) {

		return dateTextBox.addChangeDateHandler(new ChangeDateHandler() {
			
			@Override
			public void onChangeDate(ChangeDateEvent evt) {
				String dateStr = dateTextBox.getTextBox().getValue();
				
				String value = "";
				if (dateStr != null)
					value = Long.toString(DateTimeFormat.getFormat("dd/MM/yyyy HH:mm:ss")
							.parse(dateStr).getTime() / SECONDS_IN_MILLI);
					
				ValueChangeEvent<MooshakValue> event = new ValueChangeEvent<MooshakValue>(
						new MooshakValue(field, value)) {
				};
				handler.onValueChange(event);
			}
		});
	}
	
	@Override
	public boolean isEditing() {
		return editing;
	}

	/**
	 * Sets whether the date box is enabled.
	 *
	 * @param enabled
	 *            is the box enabled
	 */
	public void setEnabled(boolean enabled) {
		dateTextBox.setEnabled(enabled);
	}
}