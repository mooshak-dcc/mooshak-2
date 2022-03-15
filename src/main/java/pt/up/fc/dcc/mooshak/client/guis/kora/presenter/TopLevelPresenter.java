package pt.up.fc.dcc.mooshak.client.guis.kora.presenter; 

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiMessages;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCMessages;
import pt.up.fc.dcc.mooshak.client.guis.kora.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;
 
public class TopLevelPresenter implements Presenter, TopLevelView.Presenter {
	private static final String PROBLEM_ID = "FCUP";
 
	/* Logging */
	public static final Logger LOGGER = Logger.getLogger("");
	
	/* Constants and messages */
	public static final ICPCConstants ICPC_CONSTANTS = GWT.create(ICPCConstants.class);
	public static final ICPCMessages ICPC_MESSAGES = GWT.create(ICPCMessages.class);
	public static final EnkiConstants ENKI_CONSTANTS = GWT.create(EnkiConstants.class);
	public static final EnkiMessages ENKI_MESSAGES = GWT.create(EnkiMessages.class);

	private KoraCommandServiceAsync rpcKora;
	private ParticipantCommandServiceAsync rpcParticipant;
	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private TopLevelView view;
	
	private ContextInfo contextInfo;

	/* Storage map */
	private StorageMap storageMap = null;

	/* Your fields here */
	private String lastSubmissionJson = "";

	public TopLevelPresenter(KoraCommandServiceAsync rpcKora, ParticipantCommandServiceAsync rpcParticipant, 
			BasicCommandServiceAsync rpcBasic, HandlerManager eventBus, TopLevelView view) {
		this.setRpcKora(rpcKora);
		this.rpcParticipant = rpcParticipant;
		this.rpcBasic = rpcBasic;
		this.setEventBus(eventBus);
		this.view = view;

		this.view.setPresenter(this);
	}

	@Override
	public void go(final HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
		setDependentData();
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	private void setDependentData() {
		
		rpcBasic.context(new AsyncCallback<ContextInfo>() {
			
			@Override
			public void onSuccess(ContextInfo contextInfo) {
				
				setContextInfo(contextInfo);
				
				Storage storage = Storage.getLocalStorageIfSupported();
				if (storage != null) {
					String prefix = contextInfo.getactivityId() + "." + PROBLEM_ID + "." + contextInfo.getParticipantId();
					storageMap = new StorageMap(storage);
					if (storageMap.containsKey(prefix + ".code")) {
						view.importGraphAsJson(new String(Base64Coder.decodeLines(storageMap.get(prefix + ".code"))));
					}
				}
				
				getAvailableLanguages();
		
				// Load configs and pass to the view?
			}
			
			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
			}
		});

	}

	/* GETTERS & SETTERS */

	/**
	 * @return the contextInfo
	 */
	public ContextInfo getContextInfo() {
		return contextInfo;
	}

	/**
	 * @param contextInfo the contextInfo to set
	 */
	public void setContextInfo(ContextInfo contextInfo) {
		this.contextInfo = contextInfo;
	}

	/* Handle view requests */

	@Override
	public void getSubmissionsTransactionsData() {

		rpcParticipant.getTransactionsData("submissions", new AsyncCallback<TransactionQuota>() {

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
	public void onDiagramEvaluate(boolean consider) {

		Date now = new Date();
		if (contextInfo.getEnd() != null && now.after(contextInfo.getEnd())) {
			view.setObservations("Contest has ended");
			return;
		}
		if (contextInfo.getStart() != null && now.before(contextInfo.getStart())) {
			view.setObservations("Contest has not started yet");
			return;
		}

		String message = consider ? ICPC_MESSAGES.submitDiagramConfirmation(PROBLEM_ID)
				: ICPC_MESSAGES.validateDiagramConfirmation(PROBLEM_ID);

		new OkCancelDialog(message) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				lastSubmissionJson = view.getGraphAsJSON();
				rpcParticipant.evaluate("diagram.json", lastSubmissionJson.getBytes(), 
						PROBLEM_ID, new ArrayList<String>(),
						consider, new AsyncCallback<Void>() {

							@Override
							public void onSuccess(Void result) {
								EventManager.getInstance().refresh();
								view.setObservations(ICPC_CONSTANTS.processing());

								if (consider)
									getSubmissionsTransactionsData();
							}

							@Override
							public void onFailure(Throwable caught) {
								view.setObservations(caught.getMessage());
								if (consider)
									getSubmissionsTransactionsData();

								view.decreaseProgramWaitingEvaluation();
							}
						});
				view.setObservations(ICPC_CONSTANTS.submitted());
				view.increaseProgramWaitingEvaluation();
			}
		});
	}

	@Override
	public void saveToLocalStorage(String jsonGraph) {
		if (storageMap != null) {
			String prefix = contextInfo.getactivityId() + "." + PROBLEM_ID + "." + contextInfo.getParticipantId();
			storageMap.put(prefix + ".code", Base64Coder.encodeString(jsonGraph));
		}
	}

	@Override
	public void revertToLastSubmission() {
		view.importGraphAsJson(lastSubmissionJson);
	}
	
	
	/* Additional Functions */

	/**
	 * Replace contents of the editor for submission with id
	 * @param id of the submission to set 
	 */
	public void replaceSubmissionContent(String id, String team) {
		rpcParticipant.getSubmissionContent(id, team, new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setObservations("Could not set submission contents");
			}

			@Override
			public void onSuccess(MooshakValue result) {
				view.importGraphAsJson(new String(result.getContent()));
			}
		});
	}

	/**
	 * Get the languages available from the contest
	 */
	private void getAvailableLanguages() {
		rpcParticipant.getAvailableLanguages(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// ignore silently
				LOGGER.log(Level.SEVERE, caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				view.setLanguages(result);
			}
		});
	}

	/**
	 * Retrieve evaluation summary from server
	 * @param submissionId Id of the submission
	 * @param consider ?
	 */
	public void update(final String submissionId, final boolean consider) {
		
		LOGGER.info("Updating:" + submissionId);
		rpcParticipant.getEvaluationSummary(submissionId, consider, new AsyncCallback<EvaluationSummary>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setObservations("Error:" + caught.getMessage());

			}

			@Override
			public void onSuccess(EvaluationSummary result) {

				// TODO: On receive EvaluationSummary

				view.decreaseProgramWaitingEvaluation();

				view.setObservations(result.getStatus() + "\n" + result.getObservations());

				view.importGraphDiff(result.getFeedback());
			}
		});

	}

	public HandlerManager getEventBus() {
		return eventBus;
	}

	public void setEventBus(HandlerManager eventBus) {
		this.eventBus = eventBus;
	}

	public KoraCommandServiceAsync getRpcKora() {
		return rpcKora;
	}

	public void setRpcKora(KoraCommandServiceAsync rpcKora) {
		this.rpcKora = rpcKora;
	}
}
