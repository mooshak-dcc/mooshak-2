package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.client.form.admin.CustomRadioButton;
import pt.up.fc.dcc.mooshak.client.form.admin.CustomTextArea;
import pt.up.fc.dcc.mooshak.client.form.admin.CustomTextBox;
import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.CustomLabelPath;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DetailQuestionViewImpl extends Composite implements
		DetailQuestionView, HasFormData,
		HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue> {


	private static final int MESSAGE_VIEWING_TIME = 5*1000;

	private static DetailQuestionUiBinder uiBinder = GWT.create(DetailQuestionUiBinder.class);

	@UiTemplate("DetailQuestionView.ui.xml")
	interface DetailQuestionUiBinder extends UiBinder<Widget, DetailQuestionViewImpl> {}
	
	private Presenter presenter = null;
	
	private FormDataProvider formDataProvider;
	
	private String id;
	
	@UiField
	Label message;
	
	@UiField
	Label questionId;
	
	@UiField
	CustomLabelPath team;
	
	@UiField
	CustomLabelPath problemId;
	
	@UiField
	CustomTextBox subject;
	
	@UiField
	CustomTextArea question;
	
	@UiField
	HTMLPanel answerBlock;
	
	@UiField
	CustomTextArea answer;
	
	@UiField
	HTMLPanel answeredSubject;
	
	@UiField
	SelectOneListBox<SelectableOption> subjectList;
	
	@UiField
	CustomRadioButton answered;
	
	@UiField
	CustomRadioButton withoutAnswer;
	
	@UiField
	CustomRadioButton alreadyAnswered;
	
	@UiField
	CustomRadioButton unanswered;
	
	private Map<String,MooshakWidget> fields=new HashMap<String,MooshakWidget>();

	private OptionFormatter<SelectableOption> formatter = 
			new OptionFormatter<SelectableOption>() {
		 public String getLabel(SelectableOption option) { 
			 return option.getLabel(); 
			 };
         public String getValue(SelectableOption option)  { 
        	 return option.getId(); 
        	 };
	};
	
	public DetailQuestionViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		linkFieldsToHandlers();
		
		setRadioOptionsHandlers();
		subjectList.setFormatter(formatter);
	}

	private void setRadioOptionsHandlers() {
		
		answer.addDomHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				answered.setValue(new MooshakValue("State", "true"));
				unanswered.setValue(new MooshakValue("State", "false"));
				withoutAnswer.setValue(new MooshakValue("State", "false"));
				alreadyAnswered.setValue(new MooshakValue("State", "false"));
			}
		}, ClickEvent.getType());
		
		withoutAnswer.addValueChangeHandler(new ValueChangeHandler<MooshakValue>() {

			@Override
			public void onValueChange(ValueChangeEvent<MooshakValue> event) {
				if(!event.getValue().getSimple().equals("false")) {
					answerBlock.getElement().getStyle().setDisplay(Display.NONE);
					answeredSubject.getElement().getStyle().setDisplay(Display.NONE);
				}
			}
		});
		
		alreadyAnswered.addValueChangeHandler(new ValueChangeHandler<MooshakValue>() {

			@Override
			public void onValueChange(ValueChangeEvent<MooshakValue> event) {
				if(!event.getValue().getSimple().equals("false")) {
					answerBlock.getElement().getStyle().setDisplay(Display.NONE);
					answeredSubject.getElement().getStyle().setDisplay(Display.BLOCK);
					presenter.getSubjectsList();
				}
			}
		});
		
		answered.addValueChangeHandler(new ValueChangeHandler<MooshakValue>() {

			@Override
			public void onValueChange(ValueChangeEvent<MooshakValue> event) {
				if(!event.getValue().getSimple().equals("false")) {
					answerBlock.getElement().getStyle().setDisplay(Display.BLOCK);
					answeredSubject.getElement().getStyle().setDisplay(Display.NONE);
				}
			}
		});
		
		unanswered.addValueChangeHandler(new ValueChangeHandler<MooshakValue>() {

			@Override
			public void onValueChange(ValueChangeEvent<MooshakValue> event) {
				if(!event.getValue().getSimple().equals("false")) {
					answerBlock.getElement().getStyle().setDisplay(Display.BLOCK);
					answeredSubject.getElement().getStyle().setDisplay(Display.NONE);
				}
			}
		});
	}

	private void linkFieldsToHandlers() {
		fields.put("Team", team);
		team.addValueChangeHandler(this);
		
		fields.put("Problem", problemId);
		problemId.addValueChangeHandler(this);

		fields.put("Subject", subject);
		//subject.addValueChangeHandler(this);

		fields.put("Question", question);
		//question.addValueChangeHandler(this);

		fields.put("Answer", answer);
		//answer.addValueChangeHandler(this);

		/*answered.addValueChangeHandler(this);
		unanswered.addValueChangeHandler(this);
		withoutAnswer.addValueChangeHandler(this);
		alreadyAnswered.addValueChangeHandler(this);*/

	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public MooshakValue getAnswer() {
		return answer.getValue();
	}

	@Override
	public MooshakValue getSubject() {
		return subject.getValue();
	}

	@Override
	public MooshakValue getQuestion() {
		return question.getValue();
	}

	@Override
	public MooshakValue getState() {
		if(!answered.getValue().getSimple().equalsIgnoreCase("false"))
			return answered.getValue();
		if(!unanswered.getValue().getSimple().equalsIgnoreCase("false"))
			return unanswered.getValue();
		if(!withoutAnswer.getValue().getSimple().equalsIgnoreCase("false"))
			return withoutAnswer.getValue();
		if(!alreadyAnswered.getValue().getSimple().equalsIgnoreCase("false"))
			return alreadyAnswered.getValue();
		return null;
	}

	@Override
	public void clearAnswer() {
		answer.setValue(new MooshakValue("Answer", ""));
	}

	@Override
	public void setFormDataProvider(FormDataProvider provider) {
		provider.addFormDataProvider(this);
		this.formDataProvider = provider;
		
		provider.refresh();
	}

	@Override
	public void setObjectId(String objectId) {
		id = objectId;
		questionId.setText(id);
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
		
		presenter.onChange(id,pair);
	}
	
	/* Command button */
	
	@UiField
	Button comment;
	
	@UiHandler(value = { "comment" })
	void comment(ClickEvent event) {
		presenter.onCommentProblemClicked();
	}
	
	@UiField
	Button submit;
	
	@UiHandler(value = { "submit" })
	void submit(ClickEvent event) {
		presenter.onSubmitClicked();
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
				String state = data.get(fieldName).getSimple().toLowerCase();
				setStateValue(fieldName, state);
			}
			
			if(fields.containsKey(fieldName))
				if(!fields.get(fieldName).isEditing())
					fields.get(fieldName).setValue(data.get(fieldName));
		}
	}

	/**
	 * @param fieldName
	 * @param state
	 */
	private void setStateValue(String fieldName, String state) {
		switch (state) {
		case "answered":
			answered.setValue(new MooshakValue(fieldName, "true"));
			unanswered.setValue(new MooshakValue(fieldName, "false"));
			withoutAnswer.setValue(new MooshakValue(fieldName, "false"));
			alreadyAnswered.setValue(new MooshakValue(fieldName, "false"));
			answerBlock.getElement().getStyle().setDisplay(Display.BLOCK);
			answeredSubject.getElement().getStyle().setDisplay(Display.NONE);
			break;
		case "unanswered":
			answered.setValue(new MooshakValue(fieldName, "false"));
			unanswered.setValue(new MooshakValue(fieldName, "true"));
			withoutAnswer.setValue(new MooshakValue(fieldName, "false"));
			alreadyAnswered.setValue(new MooshakValue(fieldName, "false"));
			answerBlock.getElement().getStyle().setDisplay(Display.BLOCK);
			answeredSubject.getElement().getStyle().setDisplay(Display.NONE);
			break;
		case "without_answer":
			answered.setValue(new MooshakValue(fieldName, "false"));
			unanswered.setValue(new MooshakValue(fieldName, "false"));
			withoutAnswer.setValue(new MooshakValue(fieldName, "true"));
			alreadyAnswered.setValue(new MooshakValue(fieldName, "false"));
			answerBlock.getElement().getStyle().setDisplay(Display.NONE);
			answeredSubject.getElement().getStyle().setDisplay(Display.NONE);
			break;
		case "already_answered":
			answered.setValue(new MooshakValue(fieldName, "false"));
			unanswered.setValue(new MooshakValue(fieldName, "false"));
			withoutAnswer.setValue(new MooshakValue(fieldName, "false"));
			alreadyAnswered.setValue(new MooshakValue(fieldName, "true"));
			answerBlock.getElement().getStyle().setDisplay(Display.NONE);
			answeredSubject.getElement().getStyle().setDisplay(Display.BLOCK);
			presenter.getSubjectsList();
			break;

		default:
			break;
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

	@Override
	public String getObjectId() {
		return id;
	}
	
	@Override
	public void setSubjectsList(List<SelectableOption> options) {
		subjectList.setSelections(options);

		String subjectSelected = answer.getValue().getSimple().replace("See: ", "");
		for (SelectableOption selectableOption : options) {
			if(selectableOption.getLabel().equals(subjectSelected))
				subjectList.setSelectedValue(selectableOption);
				
		}
	}
	
	@Override
	public String getAnsweredSubject() {
		return subjectList.getSelectedOption().getLabel();
	}
}
