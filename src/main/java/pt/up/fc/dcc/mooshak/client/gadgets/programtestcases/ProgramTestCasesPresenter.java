package pt.up.fc.dcc.mooshak.client.gadgets.programtestcases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView;
import pt.up.fc.dcc.mooshak.client.gadgets.programtestcases.ProgramTestCasesView.Presenter;
import pt.up.fc.dcc.mooshak.client.guis.enki.event.ResourceOnSuccessEvent;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;

public class ProgramTestCasesPresenter extends GadgetPresenter<ProgramTestCasesView> implements Presenter {

	private static final long MAX_FILE_SIZE = 5 * 100 * 1000; // 500kb
	private static final RegExp REGEX_ACCEPTED_SUBMISSION = RegExp.compile("^accepted\\s*", "mi");

	// external views that this view needs access (MVP problem? need to re-think
	// this)
	private ProgramObservationsView observationsView;
	private ProgramEditorView editorView;

	public ProgramTestCasesPresenter(HandlerManager eventBus, ParticipantCommandServiceAsync rpcService,
			EnkiCommandServiceAsync enkiService, ProgramTestCasesView view, Token token) {
		super(eventBus, null, rpcService, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		/* getPrintoutsTransactionsData(); */
		getSubmissionsTransactionsData();
		getValidationsTransactionsData();
		setDependentData();
	}

	private void setDependentData() {
		getPublicTestCases();
	}

	/**
	 * @param observationsView
	 *            the observationsView to set
	 */
	public void setObservationsView(ProgramObservationsView observationsView) {
		this.observationsView = observationsView;
	}

	/**
	 * @param editorView
	 *            the editorView to set
	 */
	public void setEditorView(ProgramEditorView editorView) {
		this.editorView = editorView;
	}

	/**
	 * Send a program for remote evaluation with data collected from the view
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
		
		byte[] utf8Bytes = editorView.getProgramCode();
		if (utf8Bytes == null)
			utf8Bytes = new byte[0];

		if (utf8Bytes.length > MAX_FILE_SIZE) {
			observationsView.setObservations("File too long, please try again");
			return;
		}

		// Window.alert();
		String message = consider ? ICPC_MESSAGES.submitConfirmation(editorView.getProgramName(), name)
				: ICPC_MESSAGES.validateConfirmation(editorView.getProgramName(), name);

		new OkCancelDialog(message) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				LOGGER.info("Inputs size:" + view.getInputs().size());

				participantService.evaluate(
						editorView.getProgramName(), 
						editorView.getProgramCode(), 
						problemId, 
						view.getInputs(), 
						consider,
						new AsyncCallback<Void>() {

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
				List<String> list = new ArrayList<String>();
				Map<Integer, String> outputs = result.getOutputs();
				LOGGER.info("Update result outputs:" + outputs.size());
				List<Integer> keys = new ArrayList<Integer>(outputs.keySet());
				Collections.sort(keys);

				LOGGER.info("Update result size:" + keys.size());
				for (Integer key : keys)
					list.add(outputs.get(key));

				view.setOutputs(list);
				observationsView.clearObservations();
				observationsView.addStatus(result.getStatus());
				observationsView.addObservations(result.getObservations());
				observationsView.addFeedback(result.getFeedback());

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


	public void getPublicTestCases() {
		enkiService.getPublicTestCases(problemId, new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not get public test cases: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				view.insertPublicTestCases(result);
			}
		});
	}

	@Override
	public void hasTestPassed(final int pos, String expected, String obtained) {

		enkiService.checkTestPassed(expected, obtained, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable t) {
				Logger.getLogger("").log(Level.SEVERE, "Could not check if test "
						+ "passed");
			}

			@Override
			public void onSuccess(Boolean passed) {
				view.setTestPassed(pos, passed);
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

	public void replaceSubmissionContent(String id, String team) {
		participantService.getSubmissionContent(id, team, 
				new AsyncCallback<MooshakValue>() {

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

}
