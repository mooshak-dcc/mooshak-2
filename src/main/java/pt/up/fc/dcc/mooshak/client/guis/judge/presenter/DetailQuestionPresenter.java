package pt.up.fc.dcc.mooshak.client.guis.judge.presenter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager.Notifier;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCMessages;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.CommentProblemEvent;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.DetailQuestionView;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public class DetailQuestionPresenter implements Presenter,
		DetailQuestionView.Presenter {

	private static Logger logger = Logger.getLogger("");

	private DetailQuestionView view;
	private HandlerManager eventBus;

	private DataManager dataManager = DataManager.getInstance();

	private JudgeCommandServiceAsync rpcService;
	
	private ICPCMessages messages = GWT.create(ICPCMessages.class);

	private String questionId;

	public DetailQuestionPresenter(JudgeCommandServiceAsync rpcService,
			HandlerManager eventBus, DetailQuestionView view, String questionId) {

		this.rpcService = rpcService;
		this.view = view;
		this.eventBus = eventBus;
		this.questionId = questionId;
		
		this.view.setPresenter(this);
		setDependentData();
	}

	@Override
	public void onCommentProblemClicked() {
		eventBus.fireEvent(new CommentProblemEvent());
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		logger.log(Level.SEVERE, caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	public void setObjectId(String objectId) {
		view.setObjectId(objectId);
	}

	public void setDataProvider(FormDataProvider dataProvider) {
		view.setFormDataProvider(dataProvider);
	}
	
	@Override
	public void onChange(String objectId, MooshakValue value) {		
		DataObject dataObject = dataManager.getMooshakObject(objectId);

		dataObject.getData().setFieldValue(value.getField(), value);

		dataManager.setMooshakObject(objectId, new Notifier() {
			
			@Override
			public void notify(String message) {
				
				view.setMessage(message);
			}
		});
		
		if(value.getField().equals("State")) {
			if(value.getSimple().equalsIgnoreCase("answered"))
				sendAlertNotification(messages.answeredQuestion(dataManager.getMooshakObject(view
						.getObjectId()).getData().getFieldValue("Subject").getSimple()));
		}
		
		logger.info("!! Saving data on "+objectId);
		broadcastQuestion();
		view.refreshProviders();
	}

	private void sendAlertNotification(String message) {
		rpcService.sendAlertNotificationEvent(view.getObjectId(), 
				message, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logger.info("Could not send alert");
					}

					@Override
					public void onSuccess(Void result) {
						
					}
		});
	}

	protected void broadcastQuestion() {
		rpcService.broadcastRowChange("questions", questionId, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logger.severe("Could not broadcast question: " 
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Void result) {
						EventManager.getInstance().refresh();
						view.refreshProviders();
					}
		});
	}

	@Override
	public void onSubmitClicked() {
		DataObject dataObject = dataManager.getMooshakObject(view
				.getObjectId());

		dataObject.getData().setFieldValue(view.getQuestion().getField(), 
				view.getQuestion());
		
		MooshakValue answer = view.getAnswer();
		switch (view.getState().getSimple().toLowerCase()) {
		case "already_answered":
		case "already answered":
			answer = new MooshakValue(answer.getField(), "See: " 
					+ view.getAnsweredSubject());
			break;
		case "without_answer":
		case "without answer":
			answer = new MooshakValue(answer.getField(), "");
			break;
		default:
			break;
		}
		dataObject.getData().setFieldValue(answer.getField(), 
				answer);
		dataObject.getData().setFieldValue(view.getSubject().getField(), 
				view.getSubject());
		dataObject.getData().setFieldValue(view.getState().getField(), 
				view.getState());

		dataManager.setMooshakObject(view.getObjectId(), new Notifier() {
			
			@Override
			public void notify(String message) {
				
				view.setMessage(message);
			}
		});
		broadcastQuestion();
		view.refreshProviders();
		
		if(view.getState().getField().equals("State")) {
			if(view.getState().getSimple().equalsIgnoreCase("answered"))
				sendAlertNotification(messages.answeredQuestion(dataManager.getMooshakObject(view
						.getObjectId()).getData().getFieldValue("Subject").getSimple()));
		}
	}
	
	@Override
	public void getSubjectsList() {

		rpcService.getQuestionsSubjectList(view.getObjectId(), 
				new AsyncCallback<List<SelectableOption>>() {

			@Override
			public void onFailure(Throwable caught) {
				logger.severe("Could not get subjects list: " 
						+ caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(List<SelectableOption> result) {
				view.setSubjectsList(result);
			}
		});
	}

}
