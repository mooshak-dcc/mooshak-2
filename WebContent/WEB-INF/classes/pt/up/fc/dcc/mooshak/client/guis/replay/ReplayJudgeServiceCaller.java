package pt.up.fc.dcc.mooshak.client.guis.replay;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.services.JudgeCommandService;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public class ReplayJudgeServiceCaller extends ReplayServiceCaller {
	private static JudgeCommandServiceAsync judgeService = 
			GWT.create(JudgeCommandService.class);

	public static void getProgramCode(String id) {

		logMessage("Executing getProgramCode: " + id);
		judgeService.getProgramCode(id, new AsyncCallback<byte[]>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getProgramCode failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(byte[] result) {
				logMessage("getProgramCode succeeded");
			}
		});
	}

	public static void reevaluate(String programName, String programCode, String problemId,
			String submissionId) {

		logMessage("Executing reevaluate: " + programName + " " + programCode
				 + " " + problemId + " " + submissionId);
		judgeService.reevaluate(programName, programCode.getBytes(), problemId, submissionId, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("reevaluate failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("reevaluate succeeded");
					}
				});
	}

	public static void comment(String problemId, String subject, String comment) {

		logMessage("Executing comment: " + problemId + " " + subject
				 + " " + comment);
		judgeService.comment(problemId, subject, comment, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("comment failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("comment succeeded");
					}
				});
	}

	public static void getSubmissionReports(String id) {

		logMessage("Executing getSubmissionReports: " + id);
		judgeService.getSubmissionReports(id, new AsyncCallback<Map<String,String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				logMessage("getSubmissionReports succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("getSubmissionReports failed: " + caught.getMessage());
			}
		});
	}

	public static void getFileSeparator() {

		logMessage("Executing getFileSeparator");
		judgeService.getFileSeparator(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String result) {
				logMessage("getFileSeparator succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("getFileSeparator failed: " + caught.getMessage());
			}
		});
	}

	public static void broadcastRowChange(String type, String id) {

		logMessage("Executing broadcastRowChange: " + type + " " + id);
		judgeService.broadcastRowChange(type, id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("broadcastRowChange failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("broadcastRowChange succeeded");
			}
		});
	}

	public static void createBalloon(String objectId) {

		logMessage("Executing createBalloon: " + objectId);
		judgeService.createBalloon(objectId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("createBalloon failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("createBalloon succeeded");
			}
		});
	}

	public static void sendAlertNotificationEvent(String objectId, String message) {

		logMessage("Executing sendAlertNotificationEvent: " + objectId + " " +
				message);
		judgeService.sendAlertNotificationEvent(objectId, message, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("sendAlertNotificationEvent failed: "
								+ caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("sendAlertNotificationEvent succeeded");
					}
				});
	}

	public static void getQuestionsSubjectList(String questionId) {

		logMessage("Executing getQuestionsSubjectList: " + questionId);
		judgeService.getQuestionsSubjectList(questionId, 
				new AsyncCallback<List<SelectableOption>>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getQuestionsSubjectList failed: "
								+ caught.getMessage());
					}

					@Override
					public void onSuccess(List<SelectableOption> result) {
						logMessage("getQuestionsSubjectList succeeded");
					}
				});
	}

	public static void getProblemId(String submissionId) {

		logMessage("Executing getProblemId: " + submissionId);
		judgeService.getProblemId(submissionId, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getProblemId failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getProblemId succeeded");
			}
		});
	}

	public static void updateSubmissionResult(String submissionId, String oldResult,
			String newResult) {

		logMessage("Executing updateSubmissionResult: " + submissionId + " " +
				oldResult + " " + newResult);
		judgeService.updateSubmissionResult(submissionId, oldResult, newResult, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("updateSubmissionResult failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("updateSubmissionResult succeeded");
					}
				});
	}

	public static void isPrintoutsListPending() {

		logMessage("Executing isPrintoutsListPending");
		judgeService.isPrintoutsListPending(new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				logMessage("isPrintoutsListPending succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("isPrintoutsListPending failed: " + caught.getMessage());
			}
		});
	}

	public static void isBalloonsListPending() {

		logMessage("Executing isBalloonsListPending");
		judgeService.isBalloonsListPending(new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				logMessage("isBalloonsListPending succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("isBalloonsListPending failed: " + caught.getMessage());
			}
		});
	}

}
