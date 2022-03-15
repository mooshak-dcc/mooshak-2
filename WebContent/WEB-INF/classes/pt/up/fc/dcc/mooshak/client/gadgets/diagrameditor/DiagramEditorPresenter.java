package pt.up.fc.dcc.mooshak.client.gadgets.diagrameditor;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.diagrameditor.DiagramEditorView.Presenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView;
import pt.up.fc.dcc.mooshak.client.guis.enki.event.ResourceOnSuccessEvent;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;

public class DiagramEditorPresenter extends GadgetPresenter<DiagramEditorView> implements Presenter {
	private static final RegExp REGEX_ACCEPTED_SUBMISSION = RegExp.compile("^accepted\\s*", "mi");

	private ProgramObservationsView observationsView;

	private Date startDate = null;
	private Date stopDate = null;

	private String lastSubmissionJson = "";

	private StorageMap storageMap = null;

	public DiagramEditorPresenter(HandlerManager eventBus, ParticipantCommandServiceAsync rpcService,
			EnkiCommandServiceAsync enkiService, DiagramEditorView view, Token token) {
		super(eventBus, null, rpcService, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);

		Storage storage = Storage.getLocalStorageIfSupported();
		if (storage != null) {
			String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();
			storageMap = new StorageMap(storage);
			if (storageMap.containsKey(prefix + ".code")) {
				view.importGraphAsJson(new String(Base64Coder.decodeLines(storageMap.get(prefix + ".code"))));
			}
		}
	}

	@Override
	public void go(HasWidgets container) {
		getAvailableLanguages();
	}

	/**
	 * @param observationsView
	 *            the observationsView to set
	 */
	public void setObservationsView(ProgramObservationsView observationsView) {
		this.observationsView = observationsView;
	}

	@Override
	public void setObservations(String obs) {
		observationsView.setObservations(obs);
	}

	/**
	 * Send a program for remote evaluation with data collected from the view
	 * 
	 * @param consider
	 *            this submission for evaluation
	 */
	public void onDiagramEvaluate(final boolean consider) {
		Date now = new Date();
		if (stopDate != null && now.after(stopDate)) {
			observationsView.setObservations("Contest has ended");
			return;
		}
		if (startDate != null && now.before(startDate)) {
			observationsView.setObservations("Contest has not started yet");
			return;
		}

		String message = consider ? ICPC_MESSAGES.submitDiagramConfirmation(problemId)
				: ICPC_MESSAGES.validateDiagramConfirmation(problemId);

		new OkCancelDialog(message) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				lastSubmissionJson = view.getGraphAsJSON();
				participantService.evaluate("diagram.json", lastSubmissionJson.getBytes(), problemId, new ArrayList<String>(),
						consider, new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								EventManager.getInstance().refresh();
								observationsView.setObservations(ICPC_CONSTANTS.processing());

								if (consider)
									getSubmissionsTransactionsData();
							}

							@Override
							public void onFailure(Throwable caught) {
								observationsView.setObservations(caught.getMessage());
								if (consider)
									getSubmissionsTransactionsData();

								view.decreaseProgramWaitingEvaluation();
							}
						});
				observationsView.setObservations(ICPC_CONSTANTS.submitted());
				view.increaseProgramWaitingEvaluation();
			}
		});
	}

	public void update(final String submissionId, final boolean consider) {
		LOGGER.info("Updating:" + submissionId);
		participantService.getEvaluationSummary(submissionId, consider, new AsyncCallback<EvaluationSummary>() {

			@Override
			public void onFailure(Throwable caught) {
				observationsView.setObservations("Error:" + caught.getMessage());

			}

			@Override
			public void onSuccess(EvaluationSummary result) {

				// TODO: On receive EvaluationSummary

				view.decreaseProgramWaitingEvaluation();

				if (consider) {
					syncSubmissionResult(submissionId);

					if (REGEX_ACCEPTED_SUBMISSION.test(result.getObservations())) {
						getOnSuccessResource();
					}
				}

				observationsView.clearObservations();
				observationsView.addStatus(result.getStatus());
				observationsView.addObservations(result.getObservations());

				view.importGraphDiff(result.getFeedback());
			}
		});

	}

	private void syncSubmissionResult(String submissionId) {

		enkiService.syncSubmissionResult(contextInfo.getactivityId(), resourceId, submissionId, new AsyncCallback<Void>() {

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

	public void setContestDates(Date startDate, Date stopDate) {
		this.startDate = startDate;
		this.stopDate = stopDate;
	}

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

	/**
	 * Sends event requesting the resource to show on successful submission of
	 * an exercise
	 */
	private void getOnSuccessResource() {

		eventBus.fireEvent(new ResourceOnSuccessEvent(contextInfo.getactivityId(), resourceId));
	}

	/**
	 * Get the languages available from the contest
	 */
	private void getAvailableLanguages() {
		participantService.getAvailableLanguages(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// ignore silently
				Logger.getLogger("").log(Level.SEVERE, caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				view.setLanguages(result);
			}
		});
	}

	@Override
	public void revertToLastSubmission() {
		view.importGraphAsJson(lastSubmissionJson);
	}


	public void replaceSubmissionContent(String id, String team) {
		participantService.getSubmissionContent(id, team, 
				new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				observationsView.setObservations("Could not set submission contents");
			}

			@Override
			public void onSuccess(MooshakValue result) {
				view.importGraphAsJson(new String(result.getContent()));
			}
		});
	}

	@Override
	public void saveToLocalStorage(String jsonGraph) {
		if (storageMap != null) {
			String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();
			storageMap.put(prefix + ".code", Base64Coder.encodeString(jsonGraph));
		}
	}
}
