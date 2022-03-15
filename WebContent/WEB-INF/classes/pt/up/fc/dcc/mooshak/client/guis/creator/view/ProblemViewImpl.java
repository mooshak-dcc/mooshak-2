package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.guis.creator.data.ProblemTestsDataLine;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ProblemViewImpl extends Composite implements ProblemView,
	ValueChangeHandler<MooshakValue> {

	private static final int MESSAGE_VIEWING_TIME = 5*1000;

	private static ProblemUiBinder uiBinder = GWT.create(ProblemUiBinder.class);

	@UiTemplate("ProblemView.ui.xml")
	interface ProblemUiBinder extends UiBinder<Widget, ProblemViewImpl> {
	}
	
	@UiField
	Label message;

	@UiField
	ProblemForm form;
	
	private String id;
	
	private Presenter presenter = null;

	private Set<FormDataProvider> providers;
	private FormDataProvider formDataProvider;
	
	public ProblemViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		providers = new HashSet<>();
	}
	
	@Override
	protected void onLoad() {
		form.addValueChangeHandler(this);
	}

	/**
	 * Fills the possible selection options
	 * 
	 * @param values
	 */
	@Override
	public void fillPossibleValues(Map<String, List<String>> values) {
		form.fillPossibleValues(values);
	}

	@Override
	public void setFormDataProvider(FormDataProvider provider) {
		provider.addFormDataProvider(form);
		this.formDataProvider = provider;
		
		provider.refresh();
	}

	@Override
	public void changeLanguageAceEditor() {
		form.changeLanguageAceEditor();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
		form.setPresenter(presenter);
	}

	@Override
	public Presenter getPresenter() {
		return presenter;
	}

	@Override
	public String getObjectId() {
		return id;
	}

	@Override
	public void setObjectId(String objectId) {
		id = objectId;
	}
	
	@Override
	public void refreshProviders() {
		if(formDataProvider != null)
			formDataProvider.refresh();
		
		for (FormDataProvider provider : providers)
			provider.refresh();
	}
	
	@Override
	public void setMessage(String text, boolean expires) {
		message.setText(text);
		
		if (expires)
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
	public String getSelectedItem() {
		return form.getSelectedItem();
	}
 	 
	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		MooshakValue pair = event.getValue();
		presenter.onChange(id,pair);
	}

	@Override
	public void addTestToList(String test, FormDataProvider formDataProvider) {
		ProblemTestsDataLine line = form.addTestToList(test, formDataProvider);
		line.addValueChangeHandler(this);
		formDataProvider.addFormDataProvider(line);
		formDataProvider.refresh();
		providers.add(formDataProvider);
	}

	@Override
	public void deleteIODataProviderRow(String id) {
		form.deleteIODataProviderRow(id);
	}
	
	@Override
	public void setSelectedValue(MooshakValue value) {
		form.setSelectedValue(value);
	}

	@Override
	public void setImagesDataProvider(FormDataProvider dataProvider) {
		form.setImagesDataProvider(dataProvider);
	}

	@Override
	public void setSolutions(MooshakValue value, String id) {
		form.setSolutions(value, id);
	}

	@Override
	public void clearTestProvider() {
		form.clearTestProvider();
	}

	@Override
	public void clearProgramDataProvider(String field) {
		form.clearProgramDataProvider(field);
	}

	@Override
	public void deleteProgramDataProviderRow(String id) {
		form.deleteProgramDataProviderRow(id);
	}

	@Override
	public void addProgramDataToList(String program,
			MooshakObject object) {
		form.addProgramDataToList(program, object);
	}

	@Override
	public void setSelectedField(String id, MooshakValue value) {
		form.setSelectedField(id, value);
	}

	@Override
	public void addObservation(String obs) {
		form.addObservation(obs);
	}

	@Override
	public void createEshu(ConfigInfo configInfo) {
		form.createEshu(configInfo);
	}

	@Override
	public void setDiagramLanguageOptions(List<SelectableOption> options) {
		form.setDiagramLanguageOptions(options);
	}

}
