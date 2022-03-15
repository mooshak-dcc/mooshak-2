package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.client.form.admin.CustomRadioButton;
import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.CustomLabelPath;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class RegisterDeliveryViewImpl extends Composite implements
		RegisterDeliveryView, HasFormData,
		HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue> {


	private static final int MESSAGE_VIEWING_TIME = 5*1000;

	private static RegisterDeliveryUiBinder uiBinder = GWT
			.create(RegisterDeliveryUiBinder.class);

	@UiTemplate("RegisterDeliveryView.ui.xml")
	interface RegisterDeliveryUiBinder extends
			UiBinder<Widget, RegisterDeliveryViewImpl> {
	}
	
	private Presenter presenter = null;
	
	private FormDataProvider formDataProvider;
	
	private String objectId;
	
	@UiField
	Label message;
	
	@UiField
	Label id;
	
	@UiField
	CustomLabelPath team;
	
	@UiField
	CustomLabelPath problem;

	@UiField
	CustomRadioButton delivered;
	
	@UiField
	CustomRadioButton undelivered;
	
	private Map<String,MooshakWidget> fields=new HashMap<String,MooshakWidget>();
	

	public RegisterDeliveryViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		linkFieldsToHandlers();
	}

	private void linkFieldsToHandlers() {
		fields.put("Team", team);
		team.addValueChangeHandler(this);
		
		fields.put("Problem", problem);
		problem.addValueChangeHandler(this);

		delivered.addValueChangeHandler(this);
		undelivered.addValueChangeHandler(this);

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setFormDataProvider(FormDataProvider provider) {
		provider.addFormDataProvider(this);
		this.formDataProvider = provider;
		
		provider.refresh();
	}

	@Override
	public void setObjectId(String objectId) {
		this.objectId = objectId;
		id.setText(objectId);
	}
	
	@Override
	public void refreshProviders() {
		if(formDataProvider != null)
			formDataProvider.refresh();
	}
	
	@Override
	public void setMessage(String text) {
		message.setText(text);
		
		resetMessage();
		
	}

	private Timer cleanupTimer = null;

	/**
	 * Reset message after some time
	 */
	private void resetMessage() {
		
		if(cleanupTimer != null)
			cleanupTimer.cancel();
		
		new Timer() {

			@Override
			public void run() {
				message.setText("");
				cleanupTimer = null;
			}
			
		}.schedule(MESSAGE_VIEWING_TIME);
	}

	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		MooshakValue pair = event.getValue();
		
		presenter.onChange(objectId,pair);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<MooshakValue> handler) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Populate to form's field values with a map indexed by field names
	 * 
	 * @param data a map with the field values indexed by field names
	 */
	@Override
	public void setFieldValues(Map<String,MooshakValue> data) {
		
		for(String fieldName: data.keySet()) {
			if(fieldName.equals("State")) {
				if(data.get(fieldName).getSimple()
						.equalsIgnoreCase("delivered")) {
					delivered.setValue(new MooshakValue(fieldName, "true"));
					undelivered.setValue(new MooshakValue(fieldName, "false"));
				}
				else {
					delivered.setValue(new MooshakValue(fieldName, "false"));
					undelivered.setValue(new MooshakValue(fieldName, "true"));
				}
					
			}
			
			if(fields.containsKey(fieldName))
				if(!fields.get(fieldName).isEditing())
					fields.get(fieldName).setValue(data.get(fieldName));
		}
	}
	
	/**
	 * Get field values as map indexed by field names
	 * @param data
	 */
	@Override
	public Map<String,MooshakValue> getFieldValues() {
		Map<String,MooshakValue> data = new HashMap<String,MooshakValue>();
		
		for(String fieldName: data.keySet()) {
			data.put(fieldName,fields.get(fieldName).getValue());
		}
		
		return data;
	}
	
}
