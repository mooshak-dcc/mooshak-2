package pt.up.fc.dcc.mooshak.client.gadgets.kora;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import pt.up.fc.dcc.mooshak.client.gadgets.kora.KoraView.Presenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

/**
 * Presenter of Kora in the Model-View Presenter architectural pattern
 * 
 * @author heldercorria  
 */
public class KoraPresenter extends GadgetPresenter<KoraView> implements Presenter {
	private static final RegExp REGEX_ACCEPTED_SUBMISSION = RegExp.compile("^accepted\\s*", "mi");

	/* Views that this presenter communicates with */
	private ProgramObservationsView observationsView;

	/* Storage map */
	private StorageMap storageMap = null;

	/* Your fields here */
	private String lastSubmissionJson = "";

	/**
	 * Number of MS before invoking refresh
	 */
	private static final int REFRESH_DELAY_MS = 5000;

	/**
	 * Timer to invoke refresh
	 */
	private Timer refreshTimer = null;
	
	/**
	 * to convert messages to text
	 */
	private FormatMessage messages= new FormatMessage();
//	private KoraMessages messages = GWT.create(KoraMessages.class);

	public KoraPresenter(HandlerManager eventBus, BasicCommandServiceAsync basicService,
			ParticipantCommandServiceAsync participantService, EnkiCommandServiceAsync enkiService,
			KoraCommandServiceAsync koraService, KoraView view, Token token) {

		super(eventBus, basicService, participantService, enkiService, koraService, null, null, view, token);

		this.view.setPresenter(this);
		this.view.setDefaultLanguage(this.language);

		Storage storage = Storage.getLocalStorageIfSupported();
		if (storage != null)
			storageMap = new StorageMap(storage);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		getAvailableLanguages();
	}

	private void getEshuConfiguration(String id, boolean recover) {

		koraService.getEshuConfig(id, new AsyncCallback<ConfigInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Error: onFailure getEshuConfig " + caught.getMessage());
			}

			@Override
			public void onSuccess(ConfigInfo result) {
				view.createEshu(result);
				
				if (recover)
					recoverGraphFromCache();
			}
		});
	}

	private void recoverLangFromCache(String defaultLang) {

		String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();

		if (storageMap.containsKey(prefix + ".lang"))
			if (setSelectedLanguage(storageMap.get(prefix + ".lang"))) {
				getEshuConfiguration(storageMap.get(prefix + ".lang"), true);
				return;
			}
		getEshuConfiguration(defaultLang, false);
	}

	private void recoverGraphFromCache() {

		String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();
		
		if (storageMap.containsKey(prefix + ".code"))
			view.importGraphAsJson(new String(Base64Coder.decodeLines(storageMap.get(prefix + ".code"))));
		
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
	public void onDiagramEvaluate(boolean consider) {

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
				lastSubmissionJson = view.getGraphAsJSON();
				participantService.evaluate("Diagram." + view.getValueSkeleton(), 
						lastSubmissionJson.getBytes(), problemId,
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

								view.decreaseProgramWaitingEvaluation();
							}
						});
				observationsView.setObservations(ICPC_CONSTANTS.submitted());
				view.increaseProgramWaitingEvaluation();
			}
		});
	}

	@Override
	public void setObservations(String obs) {
		observationsView.setObservations(obs);
	}

	@Override
	public void saveToLocalStorage(String jsonGraph, String lang) {
		if (storageMap != null) {
			String prefix = contextInfo.getactivityId() + "." + problemId + "." + contextInfo.getParticipantId();
			storageMap.put(prefix + ".lang", lang);
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
	 * 
	 * @param id
	 *            of the submission to set
	 */
	public void replaceSubmissionContent(String id, String team) {
		participantService.getSubmissionContent(id, team, new AsyncCallback<MooshakValue>() {

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

	/**
	 * Sends event requesting the resource to show on successful submission of
	 * an exercise
	 */
	private void getOnSuccessResource() {

//		eventBus.fireEvent(new ResourceOnSuccessEvent(contextInfo.getactivityId(), resourceId));
	}

	/**
	 * Get the languages available from the contest
	 */
	private void getAvailableLanguages() {
		participantService.getAvailableLanguages(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE, caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				view.setLanguages(result);
				List<SelectableOption> options = new ArrayList<SelectableOption>();

				String defaultLang = null;
				boolean first = true;
				for (Entry<String, String> lang : result.entrySet()) {

					options.add(new SelectableOption(lang.getKey(), lang.getValue()));
					if (first) {
						defaultLang = lang.getKey();
						first = false;
					}

				}
				view.setSkeletonOptions(options);
				
				recoverLangFromCache(defaultLang);
			}
		});
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

				view.decreaseProgramWaitingEvaluation();

				if (consider) {
					syncSubmissionResult(submissionId);

					if (result.getStatus() != null && REGEX_ACCEPTED_SUBMISSION.test(result.getStatus())) {
						getOnSuccessResource();
					}
				}

				// Window.alert(result.getFeedback().toString());
				observationsView.clearObservations();
				observationsView.addStatus(result.getStatus());
//				Window.alert("RESULT "+result.getObservations());
				if(result.getMark()>0)
					observationsView.addObservations(messages.getMarkMessage(result.getMark()+"")+messages.getMessage(result.getObservations()));
				else 
					observationsView.addObservations(messages.getMessage(result.getObservations()));
//				Window.alert("RESULT "+result.getObservations());
				
				
				view.importGraphDiff(result.getFeedback());
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

	@Override
	public void onSkeletonSelectedChanged(String id) {
		// Window.alert(id);

		if (!id.equals("")) {
			getEshuConfiguration(id, false);
			// view.setLanguages(result);
		}
	}

	private boolean setSelectedLanguage(String id) {
		return view.setSelectedLanguage(id);
	}
	
	
//	public String missingElement(String string){
//		
//		String[] result = string.split(";");
//		System.out.println("RESULT "+result);
//		System.out.println("TYPEMESSAGE "+result[1]);
//		switch (result[1]) {
//		case "insertNode":
//			return getMessages().insertNode(result[2],result[3]);
//		case "insertNode1":
//			return getMessages().insertNode1(result[2],result[3],result[4]);
//		case "insertNode2":
//			return getMessages().insertNode2(result[2],result[3]);
//		case "insertNode3":
//			return getMessages().insertNode3(result[2],result[3],result[4]);
//		case "insertEdge":
//			return getMessages().insertEdge(result[2]);
//		case "insertEdge1":
//			return getMessages().insertEdge1(result[2],result[3],result[4]);
//		case "insertEdge2":
//			return getMessages().insertEdge2(result[2],result[3],result[4]);
//		case "insertEdge3":
//			return getMessages().insertEdge3(result[2],result[3],result[4],result[5],result[6],result[7]);
//
//		default:
//			break;
//		}
//		return string;
//	}

//	public KoraMessages getMessages() {
//		return messages;
//	}
//
//	public void setMessages(KoraMessages messages) {
//		this.messages = messages;
//	}

}
