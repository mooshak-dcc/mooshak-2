package pt.up.fc.dcc.mooshak.client.gadgets.gamesubmission;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.gamesubmission.GameSubmissionView.Presenter;
import pt.up.fc.dcc.mooshak.client.gadgets.gameviewer.GameViewerView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView;
import pt.up.fc.dcc.mooshak.client.guis.enki.event.ResourceOnSuccessEvent;
import pt.up.fc.dcc.mooshak.client.services.AsuraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;

public class GameSubmissionPresenter extends GadgetPresenter<GameSubmissionView> implements Presenter {

	private static final RegExp REGEX_ACCEPTED_SUBMISSION = RegExp.compile("^accepted\\s*", "mi");
	private static final long MAX_FILE_SIZE = 20000;

	private ProgramObservationsView observationsView;
	private ProgramEditorView editorView;
	private GameViewerView gameViewerView;

	public GameSubmissionPresenter(
			ParticipantCommandServiceAsync participanService,
			EnkiCommandServiceAsync enkiService,
			AsuraCommandServiceAsync asuraService, 
			GameSubmissionView view, Token token) {
		super(null, null, participanService, enkiService, null, asuraService, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		getOpponents();
	}

	/**
	 * @param editorView
	 *            the editorView to set
	 */
	public void setEditorView(ProgramEditorView editorView) {
		this.editorView = editorView;
	}

	/**
	 * @param gameViewerView
	 *            the gameViewerView to set
	 */
	public void setGameViewerView(GameViewerView gameViewerView) {
		this.gameViewerView = gameViewerView;
	}

	/**
	 * @param observationsView
	 *            the observationsView to set
	 */
	public void setObservationsView(ProgramObservationsView observationsView) {
		this.observationsView = observationsView;
	}

	/**
	 * Send a game for remote evaluation with data collected from the view
	 * 
	 * @param consider
	 *            this submission for evaluation
	 */
	public void onProgramEvaluate(final boolean consider) {
		Date now = new Date();
		if (contextInfo.getEnd() != null && now.after(contextInfo.getEnd())) {
			observationsView.setObservations("Contest has ended");
			return;
		}
		if (contextInfo.getStart() != null && now.before(contextInfo.getStart())) {
			observationsView.setObservations("Contest has not started yet");
			return;
		}

		if (editorView.getProgramName().equals("")) {
			observationsView.setObservations("No filename given");
			return;
		}

		byte[] utf8Bytes = null;
		try {
			utf8Bytes = editorView.getProgramCode();
		} catch (Exception e) {
			utf8Bytes = new byte[0];
		}

		if (utf8Bytes.length > MAX_FILE_SIZE) {
			observationsView.setObservations("File too long, please try again");
			return;
		}

		String message = consider ? ICPC_MESSAGES.submitConfirmation(editorView.getProgramName(), name)
				: ICPC_MESSAGES.validateConfirmation(editorView.getProgramName(), name);

		new OkCancelDialog(message) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				
				List<String> opponents = view.getSelectedOpponents();
				
				LOGGER.info("Opponents size:" + opponents.size());

				participantService.evaluate(editorView.getProgramName(), editorView.getProgramCode(), problemId,
						opponents, consider, new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								EventManager.getInstance().refresh();
								observationsView.setObservations(ICPC_CONSTANTS.processing());

								if (consider)
									getSubmissionsTransactionsData();
								else
									getValidationsTransactionsData();
							}

							@Override
							public void onFailure(Throwable caught) {
								observationsView.setObservations(caught.getMessage());
								if (consider)
									getSubmissionsTransactionsData();
								else
									getValidationsTransactionsData();

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

				view.decreaseProgramWaitingEvaluation();
			}

			@Override
			public void onSuccess(EvaluationSummary result) {

				observationsView.clearObservations();
				observationsView.addStatus(result.getStatus());
				observationsView.addObservations(result.getObservations());

				try {
					gameViewerView.setSubmissionId(submissionId);
					gameViewerView.setMovie(result.getFeedback());
				} catch (Exception e) {
					observationsView.addFeedback(result.getFeedback());
				}

				view.decreaseProgramWaitingEvaluation();

				if (consider) {
					syncSubmissionResult(submissionId);

					if (REGEX_ACCEPTED_SUBMISSION.test(result.getObservations())) {
						getOnSuccessResource();
					}
				}
			}
		});

	}

	/**
	 * Synchronize submission result 
	 * 
	 * @param submissionId {@link String} ID of submission
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

	/**
	 * Sends event requesting the resource to show on successful submission of
	 * an exercise
	 */
	private void getOnSuccessResource() {
		eventBus.fireEvent(new ResourceOnSuccessEvent(contextInfo.getactivityId(), resourceId));
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

	@Override
	public void getValidationsTransactionsData() {
		participantService.getTransactionsData("validations", new AsyncCallback<TransactionQuota>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setValidateTooltip(-1, -1);
			}

			@Override
			public void onSuccess(TransactionQuota result) {
				if (result == null)
					return;

				view.setValidateTooltip(result.getTransactionsLimit() - result.getTransactionsUsed(),
						result.getTimeToReset());
			}
		});
	}

	public void replaceSubmissionContent(String id, String team) {
		participantService.getSubmissionContent(id, team, new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				observationsView.setObservations("Could not set submission contents");
			}

			@Override
			public void onSuccess(MooshakValue result) {
				editorView.setProgramCode(result.getContent());
				editorView.setProgramName(result.getName());
			}
		});
	}

	/**
	 * Load opponents
	 */
	public void getOpponents() {

		participantService.getOpponents(problemId, new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				observationsView.setObservations("No opponents available");
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				view.setOpponents(result);
			}
		});
	}
	
	/**
	 * Add submission with ID and team
	 * 
	 * @param id {@link String} submission ID
	 * @param teamId {@link String} team ID
	 */
	public void addSubmission(String id, String teamId) {
		view.addOpponent(id, teamId);
	}
}
