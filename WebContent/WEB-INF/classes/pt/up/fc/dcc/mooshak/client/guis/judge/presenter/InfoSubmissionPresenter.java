package pt.up.fc.dcc.mooshak.client.guis.judge.presenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager.Notifier;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCMessages;
import pt.up.fc.dcc.mooshak.client.guis.icpc.presenter.StatementPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.InfoSubmissionView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class InfoSubmissionPresenter implements Presenter,
		InfoSubmissionView.Presenter {

	private static final Logger LOGGER = Logger.getLogger("");

	private JudgeCommandServiceAsync rpcService;
	private BasicCommandServiceAsync rpcBasic;
	private ParticipantCommandServiceAsync rpcParticipant;
	private InfoSubmissionView view;
	private String submissionId;
	private String problemId;

	private ICPCMessages messages = GWT.create(ICPCMessages.class);
	private ICPCConstants constants = GWT.create(ICPCConstants.class);

	private DataManager dataManager = DataManager.getInstance();

	private HandlerManager eventBus;


	public InfoSubmissionPresenter(JudgeCommandServiceAsync rpcService,
			BasicCommandServiceAsync rpcBasic, ParticipantCommandServiceAsync rpcParticipant, 
			HandlerManager eventBus, InfoSubmissionView view,
			String submissionId, String problemId) {
		this.rpcService = rpcService;
		this.rpcBasic = rpcBasic;
		this.rpcParticipant = rpcParticipant;

		this.eventBus = eventBus;
		this.view = view;
		this.submissionId = submissionId;
		this.problemId = problemId;

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
		
		getProblemTestCases();
		getExpectedOutputs();
		getObtainedOutputs();
	}

	@Override
	public void onProgramReEvaluate() {
		rpcService.reevaluate(view.getProgramName(), view.getProgramCode(),
				view.getProblemId(), submissionId, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						processfailure(caught);
					}

					@Override
					public void onSuccess(Void result) {
						broadcastSubmission();
						updateSubmissionReports();
						
						getExpectedOutputs();
						getObtainedOutputs();
					}
				});
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	/**
	 * Set submission information in this view
	 */
	private void setDependentData() {
		
		loadStatement();
		
		updateSubmissionReports();
	}

	/**
	 * 
	 */
	private void updateSubmissionReports() {
		rpcService.getSubmissionReports(submissionId,
				new AsyncCallback<Map<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setMessage("Error reading report(s)");
					}

					@Override
					public void onSuccess(Map<String, String> result) {
						view.setData(result);
					}
				});
	}

	private void setEditorKind(String problemId) {
		rpcParticipant.getEditorKind(problemId, new AsyncCallback<EditorKind>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setEditorKind(EditorKind.CODE);
				setDependentCode();
			}

			@Override
			public void onSuccess(EditorKind kind) {
				view.setEditorKind(kind);
				setDependentCode();
			}
		});
	}

	/**
	 * Loads the problem statement
	 */
	private void loadStatement() {
		
		rpcService.getProblemId(submissionId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage("Error loading statement");
			}

			@Override
			public void onSuccess(String result) {
				StatementView statementView = new StatementViewImpl();

				StatementPresenter presenter = new StatementPresenter(
						rpcParticipant,
						eventBus,
						statementView,
						result, "");
				presenter.go(null);
				view.setStatement(statementView);
				
				setEditorKind(result);
			}
		});
		
	}

	/**
	 * Set submission code in this view
	 * 
	 */
	private void setDependentCode() {
		rpcService.getProgramCode(submissionId, new AsyncCallback<byte[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(byte[] result) {
				view.setProgramCode(result);
			}
		});
	}
	
	public void getProblemTestCases() {
		rpcService.getProblemTestCases(problemId,
				new AsyncCallback<Map<String,String>>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Could not get public test cases: " + 
						caught.getMessage());
			}

			@Override
			public void onSuccess(Map<String, String> result) {
				view.setProblemTestCases(result);
			}
		});
	}

	public void setObjectId(String objectId) {
		view.setObjectId(objectId);
	}

	public void setDataProvider(FormDataProvider dataProvider) {
		view.setFormDataProvider(dataProvider);
		
		// refresh tests
		setDependentData();
	}
	
	@Override
	public void onChange(String objectId, MooshakValue value) {		
		
		DataObject dataObject = dataManager.getMooshakObject(objectId);
		
		if(value.getField().equalsIgnoreCase("classify")) {
			updateSubmissionResult(objectId, submissionId, dataObject.getData().
					getFieldValue(value.getField()).getSimple(), value.getSimple());
			return;
		} else if (value.getField().equalsIgnoreCase("state")) {
				
			String classify = dataObject.getData().getFieldValue("Classify").getSimple();
			if (value.getSimple().equalsIgnoreCase("final") && 
					classify.equalsIgnoreCase("accepted")) {
				createBalloon();
				sendAlertNotification(messages.acceptedSubmission(problemId));
			}
		}

		dataObject.getData().setFieldValue(value.getField(), value);

		dataManager.setMooshakObject(objectId, new Notifier() {
			
			@Override
			public void notify(String message) {
				
				view.setMessage(message);
			}
		});
		
		LOGGER.info("!! Saving data on "+objectId);
		broadcastSubmission();
	}

	private void updateSubmissionResult(final String objectId, 
			String submissionId, String oldResult, final String newResult) {
		rpcService.updateSubmissionResult(submissionId, oldResult, newResult, 
				new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				if(newResult.equalsIgnoreCase("accepted")) {
					DataObject dataObject = dataManager.getMooshakObject(objectId);
					String status = dataObject.getData().getFieldValue("State").getSimple();
					if (status.equalsIgnoreCase("final")) {
						createBalloon();
						sendAlertNotification(messages.acceptedSubmission(problemId));
					}
				}
				view.setMessage("Saved!");
				broadcastSubmission();
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.setMessage("Error updating submission");
				broadcastSubmission();
			}
		});
	}

	private void sendAlertNotification(String message) {
		rpcService.sendAlertNotificationEvent(view.getObjectId(), 
				message, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.info("Could not send alert");
					}

					@Override
					public void onSuccess(Void result) {
						LOGGER.info("Alert sent");
					}
		});
	}

	private void createBalloon() {
		rpcService.createBalloon(submissionId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe("Could not create balloon: " 
						+ caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(Void result) {
				EventManager.getInstance().refresh();
			}
		});
	}

	protected void broadcastSubmission() {
		rpcService.broadcastRowChange("submissions", submissionId, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.severe("Could not broadcast submission: " 
								+ caught.getLocalizedMessage());
					}

					@Override
					public void onSuccess(Void result) {
						EventManager.getInstance().refresh();
					}
		});
	}
	
	@Override
	public void getExpectedOutputs() {
		
		rpcService.getExpectedOutputs(submissionId, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(List<String> result) {
				view.setExpectedOutputs(result);
			}
		});
	}
	
	@Override
	public void getObtainedOutputs() {
		
		rpcService.getObtainedOutputs(submissionId, new AsyncCallback<List<String>>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(List<String> result) {
				view.setObtainedOutputs(result);
			}
		});
	}
	
	@Override
	public void getDiffTest(final String expected, final String obtained) {
		rpcBasic.diffStrings(expected, obtained, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				
			}

			@Override
			public void onSuccess(String diff) {
				view.showDiffWindowBox(expected, obtained, diff);
			}
		});
	}

	@Override
	public void validateSubmission() {
		view.clearObservations();
		rpcService.validateSubmission(
				submissionId, 
				view.getProgramCode(),
				problemId, 
				view.getInputs(), 
				new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void result) {
						EventManager.getInstance().refresh();
						view.setMessage(constants.processing());
						view.setObservations(constants.processing());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						view.setMessage(caught.getMessage());
					}
				});
		view.setMessage(constants.submitted());
		view.addObservations(constants.submitted());
	}

	public void update(String submissionId) {
		LOGGER.info("Updating:"+submissionId);
		rpcService.getValidationSummary(submissionId,
				new AsyncCallback<EvaluationSummary>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setObservations("Error:"+caught.getMessage());
						view.setMessage("Validation Error");
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

						view.setMessage(result.getStatus());
					}
		});
		
	}
}
