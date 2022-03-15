package pt.up.fc.dcc.mooshak.client.guis.judge.presenter;

import java.util.List;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCMessages;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.CommentProblemView;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class CommentProblemPresenter implements Presenter,
		CommentProblemView.Presenter {

	private static final Logger LOGGER = Logger.getLogger("");

	private JudgeCommandServiceAsync rpcService;
	private ParticipantCommandServiceAsync rpcParticipant;
	private CommentProblemView view;
	
	private ICPCMessages messages = GWT.create(ICPCMessages.class);


	public CommentProblemPresenter(JudgeCommandServiceAsync rpcService,
			ParticipantCommandServiceAsync rpcParticipant, 
			HandlerManager eventBus, CommentProblemView view) {
		this.rpcService = rpcService;
		this.rpcParticipant = rpcParticipant;

		this.view = view;

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		rpcParticipant.getProblems(new AsyncCallback<List<SelectableOption>>() {

			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
			}

			@Override
			public void onSuccess(List<SelectableOption> result) {
				view.setProblems(result);
			}
		});
	}

	@Override
	public void onComment() {
		rpcService.comment(view.getProblem(), view.getSubject(),
				view.getComment(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						sendAlertNotification(messages.judgeComment(view.getSubject()));
						EventManager.getInstance().refresh();
						view.setMessage("Submitted!");
					}

					@Override
					public void onFailure(Throwable caught) {
						processfailure(caught);
					}
				});
	}

	private void sendAlertNotification(String message) {
		rpcService.sendAlertNotificationEvent(null, 
				message, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.info("Could not send alert");
					}

					@Override
					public void onSuccess(Void result) {
						
					}
		});
	}

	@Override
	public void onClear() {
		view.setComment("");
		view.setSubject("");
		view.clearSelectedProblem();
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

}
