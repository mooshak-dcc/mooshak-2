package pt.up.fc.dcc.mooshak.client.gadgets.quiz;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView;
import pt.up.fc.dcc.mooshak.client.gadgets.quiz.QuizView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.QuizCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;

/**
 * Presenter of Quiz in the Model-View Presenter architectural pattern
 * 
 * @author Helder Correia <code>hppc25@gmail.com</code>
 */
public class QuizPresenter extends GadgetPresenter<QuizView> implements Presenter {
	private static final RegExp REGEX_ACCEPTED_SUBMISSION = RegExp.compile("^accepted\\s*", "mi");

	/* Views that this presenter communicates with */
	private ProgramObservationsView observationsView;

	/* Storage map */
	private StorageMap storageMap = null;

	/* Your fields here */
	

	/**
	 * Number of MS before invoking refresh
	 */
	private static final int REFRESH_DELAY_MS = 5000;

	/**
	 * Timer to invoke refresh
	 */
	private Timer refreshTimer = null;

	public QuizPresenter(HandlerManager eventBus, BasicCommandServiceAsync basicService,
			ParticipantCommandServiceAsync participantService, EnkiCommandServiceAsync enkiService,
			QuizCommandServiceAsync quizService, QuizView view, Token token) {

		super(eventBus, basicService, participantService, enkiService, null, null, quizService, view, token);

		this.view.setPresenter(this);

		
		Storage storage = Storage.getLocalStorageIfSupported();
		if (storage != null)
			storageMap = new StorageMap(storage);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		recoverAnswersFromCache();
		getQuizHTML(problemId); 
	}

	
	private void getQuizHTML(String id) {
//		String input="text"
		quizService.getQuizHTML(id, new AsyncCallback<String>() { 

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Error: onFailure quiz html " + caught.getMessage());
				view.createQuizzes(caught.getMessage(),problemId);// 
			}

			@Override
			public void onSuccess(String result) {
				view.createQuizzes(result,problemId);
//				view.createEshu(result);
//				
//				if (recover)
//					recoverGraphFromCache();
			}
		});
	}
	
	
	
	private void getQuizRevised(String id) {
//		String input="text"
		quizService.getQuizHTMLFinal(id, new AsyncCallback<String>() { 

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Error: onFailure quiz html " + caught.getMessage());
				view.createQuizzes(caught.getMessage(),problemId);
			}

			@Override
			public void onSuccess(String result) {
				view.createQuizzes(result, problemId);
			}
		});
	}
	
	private void recoverAnswersFromCache() {

		String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();

		if (storageMap.containsKey(prefix + ".code"))
			view.importAnswersAsJson(new String(Base64Coder.decode(storageMap.get(prefix + ".code"))));

	}

	/* GETTERS & SETTERS */

	/**
	 * @param observationsView
	 *            the observationsView to set
	 */
	public void setObservationsView(ProgramObservationsView observationsView) {
		this.observationsView = observationsView;
	}

	/* Handle view requests */

	@Override
	public void getSubmissionsTransactionsData() {

		participantService.getTransactionsData("submissions", new AsyncCallback<TransactionQuota>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setSubmitTooltip(-1, -1);
			}

			@Override
			public void onSuccess(TransactionQuota result) {
				if (result == null)
					return;

				view.setSubmitTooltip(result.getTransactionsLimit() - result.getTransactionsUsed(),
						result.getTimeToReset());
			}
		});
	}

	@Override
	public void onQuizEvaluate(String json, boolean consider) {

		Date now = new Date();
		if (contextInfo.getEnd() != null && now.after(contextInfo.getEnd())) {
			observationsView.setObservations("Contest has ended");
			return;
		}
		if (contextInfo.getStart() != null && now.before(contextInfo.getStart())) {
			observationsView.setObservations("Contest has not started yet");
			return;
		}

		String message = consider ? ICPC_MESSAGES.submitDiagramConfirmation(problemId)
				: ICPC_MESSAGES.validateDiagramConfirmation(problemId);

		new OkCancelDialog(message) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				participantService.evaluate("Answers.json", json.getBytes(), problemId,
						new ArrayList<String>(), consider, new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								EventManager.getInstance().refresh();
								observationsView.setObservations(ICPC_CONSTANTS.processing());

								if (consider)
									getSubmissionsTransactionsData();

								refreshTimer = new Timer() {

									@Override
									public void run() {
										EventManager.getInstance().refresh();
									}
								};
								refreshTimer.schedule(REFRESH_DELAY_MS);

							}

							@Override
							public void onFailure(Throwable caught) {
								observationsView.setObservations(caught.getMessage());
								if (consider)
									getSubmissionsTransactionsData();

								view.decreaseQuizWaitingEvaluation();
							}
						});
				observationsView.setObservations(ICPC_CONSTANTS.submitted());
				view.increaseQuizWaitingEvaluation();
			}
		});
	}

	@Override
	public void setObservations(String obs) {
		observationsView.setObservations(obs);
	}

	@Override
	public void saveToLocalStorage(String json) {
		if (storageMap != null) {
			String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();
			storageMap.put(prefix + ".code", Base64Coder.encodeString(json));
		}
	}

	/* Additional Functions */

	/**
	 * Replace contents of the editor for submission with id
	 * 
	 * @param id
	 *            of the submission to set
	 */
	public void replaceSubmissionContent(String id, String team) {
		participantService.getSubmissionContent(id, team, 
				new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				observationsView.setObservations("Could not set submission contents");
			}

			@Override
			public void onSuccess(MooshakValue result) {
				view.importAnswersAsJson(new String(result.getContent()));
			}
		});
	}

	/**
	 * Sends event requesting the resource to show on successful submission of
	 * an exercise
	 */
	private void getOnSuccessResource() {

		// eventBus.fireEvent(new
		// ResourceOnSuccessEvent(contextInfo.getactivityId(), resourceId));
	}

	/**
	 * Retrieve evaluation summary from server
	 * 
	 * @param submissionId
	 *            Id of the submission
	 * @param consider
	 *            ?
	 */
	public void update(final String submissionId, final boolean consider) {

		LOGGER.info("Updating:" + submissionId);

		if (refreshTimer != null) {
			refreshTimer.cancel();
			refreshTimer = null;
		}

		participantService.getEvaluationSummary(submissionId, consider, new AsyncCallback<EvaluationSummary>() {

			@Override
			public void onFailure(Throwable caught) {
				observationsView.setObservations("Error:" + caught.getMessage());

			}

			@Override
			public void onSuccess(EvaluationSummary result) {

				// TODO: On receive EvaluationSummary

				view.decreaseQuizWaitingEvaluation();

				if (consider) {
					syncSubmissionResult(submissionId);

					if (result.getStatus() != null && REGEX_ACCEPTED_SUBMISSION.test(result.getStatus())) {
						getOnSuccessResource();
						System.out.println(result.getFeedback());
					}
				}
//				System.out.println(result.getFeedback());
				
				observationsView.clearObservations();
				observationsView.addStatus(result.getStatus());
				observationsView.addObservations(result.getObservations());
				getQuizRevised(submissionId);
			}
		});

	}

	/**
	 * Notify services of the result of a submission to a problem
	 * 
	 * @param submissionId
	 *            Id of the submission
	 */
	private void syncSubmissionResult(String submissionId) {

		enkiService.syncSubmissionResult(contextInfo.getactivityId(), resourceId, submissionId,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable t) {
						Logger.getLogger("").log(Level.SEVERE, t.getMessage());
						EventManager.getInstance().refresh();
					}

					@Override
					public void onSuccess(Void arg0) {
						EventManager.getInstance().refresh();
					}
				});
	}

}
