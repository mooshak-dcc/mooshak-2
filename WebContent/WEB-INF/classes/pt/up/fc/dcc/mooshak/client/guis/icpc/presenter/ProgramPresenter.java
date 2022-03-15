package pt.up.fc.dcc.mooshak.client.guis.icpc.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCMessages;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.ProgramView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TabButton;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.Filenames;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class ProgramPresenter implements Presenter, ProgramView.Presenter {
	
	private static final long MAX_FILE_SIZE = 5 * 100 * 1000; // 500kb

	private ParticipantCommandServiceAsync rpcService;
	// private HandlerManager eventBus;
	private ProgramView view;
	private String contestId;
	private String teamId;
	private String problemId;
	// private String problemLabel;

	private ICPCConstants constants = GWT.create(ICPCConstants.class);
	private ICPCMessages messages = GWT.create(ICPCMessages.class);
	
	private static final Logger LOGGER = Logger.getLogger("");
	
	private Date startDate = null;
	private Date stopDate = null;

	private Storage storage = null;
	private StorageMap storageMap = null;


	public ProgramPresenter(ParticipantCommandServiceAsync rpcService,
			HandlerManager eventBus, ProgramView view, String contestId,
			String problemId, String problemLabel, String teamId) {
		this.rpcService = rpcService;
		// this.eventBus = eventBus;
		this.view = view;
		this.contestId = contestId;
		this.teamId = teamId;
		this.problemId = problemId;
		// this.problemLabel = problemLabel;

		this.view.setPresenter(this);

		storage = Storage.getLocalStorageIfSupported();
		if (storage != null) {
			String prefix = contestId + "." + teamId + "." + problemId;
			
			storageMap = new StorageMap(storage);
			if (storageMap.containsKey(prefix + ".name")) {
				view.setProgramName(new String(Base64Coder.decodeLines(storageMap
						.get(prefix + ".name"))));
			}
			if (storageMap.containsKey(prefix + ".code")) {
				view.setProgramCode(Base64Coder.decode(storageMap
						.get(prefix + ".code")));
			}
			if (storageMap.containsKey(prefix + ".inputs.size")) {

				int size = Integer.parseInt(storageMap.get(prefix + ".inputs.size"));
				List<String> inputs = new ArrayList<>();
				for (int i = 0; i < size; i++) {
					if (storageMap.containsKey(prefix + ".inputs." + i))
						inputs.add(new String(Base64Coder.decodeLines(storageMap
								.get(prefix + ".inputs." + i))));
				}
				view.setInputs(inputs);
			}
		}
	}

	@Override
	public void go(HasWidgets container) {
		//container.add(view.asWidget());//added in CardPanel by AppController
		getPrintoutsTransactionsData();
		getSubmissionsTransactionsData();
		getValidationsTransactionsData();
		
		setDependentData();
	}
	
	
	/**
	 * Set problem statement in this view
	 */
	private void setDependentData() {
		rpcService.view(problemId, false, new AsyncCallback<ProblemInfo>() {
			
			@Override
			public void onSuccess(ProblemInfo result) {

				view.setProgramIdentification(
						result.getLabel(), 
						result.getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE,caught.getMessage());
			}
		});
		
		getAvailableLanguages();
	}

	@Override
	public void onProgramPrint() {
		
		if(view.getProgramName().equals("")) {
			view.setObservations("No filename given");
			return;
		}
		
		new OkCancelDialog(messages.printConfirmation(view.getProgramName())) {
		}.addDialogHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				view.setObservations("");
				
				rpcService.print(problemId, view.isLanguageEditable() ? 
						new String(view.getProgramCode()) : "",
						view.getProgramName(), new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setObservations("Failed to print problem: "
								+ caught.getMessage());
						getPrintoutsTransactionsData();
					}

					@Override
					public void onSuccess(Void result) {
						view.setObservations("Problem printed successfully!");
						getPrintoutsTransactionsData();
					}
				});
			}
		});
	}

	
	/**
	 * Send a program for remote evaluation with data collected from the view
	 * @param consider	this submission for evaluation 
	 */
	public void onProgramEvaluate(final boolean consider) {
		
		Date now = new Date();
		if (stopDate != null && now.after(stopDate)) {
			view.setObservations("Contest has ended");
			return;
		}
		if (startDate != null && now.before(startDate)) {
			view.setObservations("Contest has not started yet");
			return;
		}
		
		if(view.getProgramName().equals("")) {
			view.setObservations("No filename given");
			return;
		}
		
		byte[] utf8Bytes = view.getProgramCode();
		if (utf8Bytes == null)
			utf8Bytes = new byte[0];
		
		if(utf8Bytes.length > MAX_FILE_SIZE) {
			view.setObservations("File too long, please try again");
			return;
		}
		
		String message = consider ? 
				messages.submitConfirmation(view.getProgramName(), problemId) : 
				messages.validateConfirmation(view.getProgramName(), problemId);
		
		new OkCancelDialog(message) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				LOGGER.info("Inputs size:"+ view.getInputs().size());
				
				rpcService.evaluate(
						view.getProgramName(), 
						view.getProgramCode(), 
						problemId, 
						view.getInputs(), 
						consider,
						new AsyncCallback<Void>() {
							
							@Override
							public void onSuccess(Void result) {
								EventManager.getInstance().refresh();
								view.setObservations(constants.processing());
								
								if(consider)
									getSubmissionsTransactionsData();
								else
									getValidationsTransactionsData();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								view.setObservations(caught.getMessage());
								if(consider)
									getSubmissionsTransactionsData();
								else
									getValidationsTransactionsData();
								
								view.decreaseProgramWaitingEvaluation();
							}
						});
				view.setObservations(constants.submitted());
				view.increaseProgramWaitingEvaluation();
			}
		});
	}

	public void update(String submissionId, boolean consider) {
		LOGGER.info("Updating:"+submissionId);
		rpcService.getEvaluationSummary(submissionId, consider,
				new AsyncCallback<EvaluationSummary>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setObservations("Error:"+caught.getMessage());
						
						view.decreaseProgramWaitingEvaluation();
						
					}

					@Override
					public void onSuccess(EvaluationSummary result) {
						List<String> outputList = new ArrayList<String>();
						Map<Integer,String> outputs = result.getOutputs();
						LOGGER.info("Update result outputs:"+outputs.size());
						List<Integer> keys = 
								new ArrayList<Integer>(outputs.keySet());
						Collections.sort(keys);
								
						LOGGER.info("Update result size:"+keys.size());
						for(Integer key: keys)
							outputList.add(outputs.get(key));
						
						List<String> timesList = new ArrayList<String>();
						Map<Integer,String> times = result.getUserExecutionTimes();
						
						if (times != null) {
							LOGGER.info("Update result execution times:"+times.size());
							for(Integer key: keys)
								timesList.add(times.get(key));
						}
						
						view.setOutputs(outputList);
						view.setExecutionTimes(timesList);
						view.clearObservations();
						view.addStatus(result.getStatus());
						view.addObservations(result.getObservations());
						view.addFeedback(result.getFeedback());
						
						view.decreaseProgramWaitingEvaluation();
					}
		});
		
	}

	@Override
	public void getPrintoutsTransactionsData() {
		rpcService.getTransactionsData("printouts", 
				new AsyncCallback<TransactionQuota>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setPrintTooltip(-1, -1);
					}

					@Override
					public void onSuccess(TransactionQuota result) {
						if(result == null) return;
						
						view.setPrintTooltip(result.getTransactionsLimit() -
								result.getTransactionsUsed(), 
								result.getTimeToReset());
					}
		});
	}

	@Override
	public void getSubmissionsTransactionsData() {
		rpcService.getTransactionsData("submissions", 
				new AsyncCallback<TransactionQuota>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setSubmitTooltip(-1, -1);
					}

					@Override
					public void onSuccess(TransactionQuota result) {
						if(result == null) return;
						
						view.setSubmitTooltip(result.getTransactionsLimit() -
								result.getTransactionsUsed(), 
								result.getTimeToReset());
					}
		});
	}

	@Override
	public void getValidationsTransactionsData() {
		rpcService.getTransactionsData("validations", 
				new AsyncCallback<TransactionQuota>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setValidateTooltip(-1, -1);
					}

					@Override
					public void onSuccess(TransactionQuota result) {
						if(result == null) return;
						
						view.setValidateTooltip(result.getTransactionsLimit() -
								result.getTransactionsUsed(), 
								result.getTimeToReset());
					}
		});
	}

	@Override
	public void getProgramSkeleton(String extension) {
		rpcService.getProgramSkeleton(problemId, extension, 
				new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(final String result) {
						if(result.equals(""))
							return;
						
						new OkCancelDialog(messages.skeletonReplaceConfirmation()) {
						}.addDialogHandler(new ClickHandler() {
							
							@Override
							public void onClick(ClickEvent event) {
								view.setProgramCode(result.getBytes());
							}
						});
					}
		});
	}

	
	public native void log(String message) /*-{
		$wnd.console.log(message);
	}-*/;
	
	public void replaceSubmissionContent(final String id, String team) {
		rpcService.getSubmissionContent(id, team, 
				new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setObservations("Could not get submission contents");
			}

			@Override
			public void onSuccess(MooshakValue result) {
				
				view.setProgramName(result.getName());
				view.setProgramCode(result.getContent());
				
				updateEditable(Filenames.getExtension(result.getName()));
				
				TabButton.deselect("listings");

				// observations will be sent as if code was submitted
				view.increaseProgramWaitingEvaluation(); 

				update(id, true);
			}
		});
	}

	public void setContestDates(Date startDate, Date stopDate) {
		this.startDate = startDate;
		this.stopDate = stopDate;
	}
	
	@Override
	public void saveToLocalStorage(String name, byte[] code, List<String> inputs) {
		if (storageMap != null) {
			String prefix = contestId + "." + teamId + "." + problemId;
			storageMap.put(prefix + ".name", Base64Coder.encodeString(name));
			if (code != null)
				storageMap.put(prefix + ".code", new String(Base64Coder.encode(code)));
			else
				storageMap.put(prefix + ".code", "");

			storageMap.put(prefix + ".inputs.size", inputs.size() + "");
			int i = 0;
			for (String input : inputs) {
				storageMap.put(prefix + ".inputs." + i, 
						Base64Coder.encodeString(input));
				i++;
			}
		}
	}

	/**
	 * Get the languages available from the contest
	 */
	private void getAvailableLanguages() {
		rpcService.getAvailableLanguages(new AsyncCallback<Map<String, String>>() {

			@Override
			public void onFailure(Throwable caught) {
				// ignore silently
				LOGGER.severe(caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				view.setLanguages(result);
			}
		});
	}
	
	/**
	 * Check if files uploaded must be editable
	 * 
	 * @param extension Extension of the file
	 */
	@Override
	public void updateEditable(final String extension) {
		
		rpcService.isEditableContents(extension, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				
				LOGGER.severe(caught.getMessage());
				
				// default behavior
				view.setEditable(true);
			}

			@Override
			public void onSuccess(Boolean editable) {
				if (editable != null)
					view.setEditable(editable.booleanValue());
				else
					view.setEditable(true);
			}
		});
	}
	
}
