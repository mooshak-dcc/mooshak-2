package pt.up.fc.dcc.mooshak.client.guis.replay;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.services.EnkiCommandService;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;
import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementsListResponse;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Resources;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

public class ReplayEnkiServiceCaller extends ReplayServiceCaller {
	private static EnkiCommandServiceAsync enkiService = GWT.create(EnkiCommandService.class);

	public static void getResources() {

		logMessage("Executing getResources");
		enkiService.getResources(new AsyncCallback<CourseList>() {
			
			@Override
			public void onSuccess(CourseList result) {
				logMessage("getResources succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("getResources failed: " + caught.getMessage());
			}
		});
	}

	public static void getLeaderboard(String courseId, String problemId) {

		logMessage("Executing getLeaderboard: " + courseId + " " + problemId);
		enkiService.getLeaderboard(courseId, problemId, new AsyncCallback<LeaderboardResponse>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getLeaderboard failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(LeaderboardResponse result) {
				logMessage("getLeaderboard succeeded");
			}
		});
	}

	public static void getProblemStatistics(String problemId) {

		logMessage("Executing getProblemStatistics: " + problemId);
		enkiService.getProblemStatistics(problemId, new AsyncCallback<ProblemStatistics>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getProblemStatistics failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(ProblemStatistics result) {
				logMessage("getProblemStatistics succeeded");
			}
		});
	}

	public static void getAchievementsUnlocked(String courseId, String problemId) {

		logMessage("Executing getAchievementsUnlocked: " + courseId + " " + problemId);
		enkiService.getAchievementsUnlocked(courseId, problemId, 
				new AsyncCallback<AchievementsListResponse>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getAchievementsUnlocked failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(AchievementsListResponse result) {
						logMessage("getAchievementsUnlocked succeeded");
					}
				});
	}

	public static void getAchievementsRevealed(String courseId, String problemId) {

		logMessage("Executing getAchievementsRevealed: " + courseId + " " + problemId);
		enkiService.getAchievementsRevealed(courseId, problemId, 
				new AsyncCallback<AchievementsListResponse>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getAchievementsRevealed failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(AchievementsListResponse result) {
						logMessage("getAchievementsRevealed succeeded");
					}
				});
	}

	public static void getAllSkeletons(String problemId) {

		logMessage("Executing getAllSkeletons: " + problemId);
		enkiService.getAllSkeletons(problemId, new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getAllSkeletons failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(MooshakValue result) {
				logMessage("getAllSkeletons succeeded");
			}
		});
	}

	public static void getPublicTestCases(String problemId) {

		logMessage("Executing getPublicTestCases: " + problemId);
		enkiService.getPublicTestCases(problemId, new AsyncCallback<Map<String,String>>() {
			
			@Override
			public void onSuccess(Map<String, String> result) {
				logMessage("getPublicTestCases succeeded");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logMessage("getPublicTestCases failed: " + caught.getMessage());
			}
		});
	}

	public static void getRelatedResources(String courseId, String resourceId)  {

		logMessage("Executing getRelatedResources: " + courseId + " " + resourceId);
		enkiService.getRelatedResources(courseId, resourceId,
				new AsyncCallback<Resources>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getRelatedResources failed: "
								+ caught.getMessage());
					}

					@Override
					public void onSuccess(Resources result) {
						logMessage("getRelatedResources succeeded");
					}
		});
	}

	public static void getProfile(String courseId) {

		logMessage("Executing getProfile: " + courseId);
		enkiService.getProfile(courseId, new AsyncCallback<StudentProfile>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getProfile failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(StudentProfile result) {
				logMessage("getProfile succeeded");
			}
		});
	}

	public static void syncSubmissionResult(String courseId, String resourceId, 
			String submissionId) {

		logMessage("Executing syncSubmissionResult: " + courseId + " " +
				resourceId + " " + submissionId);
		enkiService.syncSubmissionResult(courseId, resourceId, submissionId, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("syncSubmissionResult failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("syncSubmissionResult succeeded");
					}
				});
	}

	public static void syncResourceLearningTime(String courseId, String resourceId, 
			String learningTime) throws ParseException {

		logMessage("Executing syncResourceLearningTime: " + courseId + " " +
				resourceId + " " + learningTime);
		DateTimeFormat df = DateTimeFormat.getFormat("dd-MM-yy_hh:mm:ss");
		enkiService.syncResourceLearningTime(courseId, resourceId, df.parse(learningTime), 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("syncResourceLearningTime failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("syncResourceLearningTime succeeded");
					}
				});
	}

	public static void notifyResourceSeen(String courseId, String resourceId) {

		logMessage("Executing notifyResourceSeen: " + courseId + " " +
				resourceId);
		enkiService.notifyResourceSeen(courseId, resourceId, 
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("notifyResourceSeen failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Void result) {
						logMessage("notifyResourceSeen succeeded");
					}
				});
	}

	public static void checkTestPassed(String expected, String obtained) {

		logMessage("Executing checkTestPassed: " + expected + " " +
				obtained);
		enkiService.checkTestPassed(expected, obtained, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("checkTestPassed failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				logMessage("checkTestPassed succeeded");
			}
		});
	}

	public static void getResourceLearningTime(String courseId, String resourceId) {

		logMessage("Executing getResourceLearningTime: " + courseId + " " +
				resourceId);
		enkiService.getResourceLearningTime(courseId, resourceId, 
				new AsyncCallback<Date>() {
					
					@Override
					public void onSuccess(Date result) {
						logMessage("getResourceLearningTime succeeded: " + result.toString());
					}
					
					@Override
					public void onFailure(Throwable caught) {
						logMessage("getResourceLearningTime failed: " + caught.getMessage());
					}
				});
	}

	public static void getFeedbackToResource(String courseId, String resourceId) {

		logMessage("Executing getFeedbackToResource: " + courseId + " " +
				resourceId);
		enkiService.getFeedbackToResource(courseId, resourceId, 
				new AsyncCallback<Feedback>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("getFeedbackToResource failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Feedback result) {
						logMessage("getFeedbackToResource succeeded");
					}
		});
	}

	public static void sendFeedbackToResource(String courseId, String resourceId,
			String rating, String comment) {

		logMessage("Executing sendFeedbackToResource: " + courseId + " " +
				resourceId + " " + rating + " " + resourceId);
		enkiService.sendFeedbackToResource(courseId, resourceId, Integer.parseInt(rating), 
				comment, new AsyncCallback<Feedback>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("sendFeedbackToResource failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Feedback result) {
						logMessage("sendFeedbackToResource succeeded");
					}
				});
	}

	public static void getProgramSkeletonByFilename(String problemId, String name) {

		logMessage("Executing getProgramSkeletonByFilename: " + problemId + " " +
				name);
		enkiService.getProgramSkeletonByFilename(problemId, name, 
				new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getProgramSkeletonByFilename failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getProgramSkeletonByFilename succeeded");
			}
		});
	}

	public static void getSolution(String problemId) {

		logMessage("Executing getSolution: " + problemId);
		enkiService.getSolution(problemId, new AsyncCallback<byte[]>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getSolution failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(byte[] result) {
				logMessage("getSolution succeeded");
			}
		});
	}

	public static void refreshMySubmissionRows() {

		logMessage("Executing refreshMySubmissionRows");
		enkiService.refreshMySubmissionRows(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("refreshMySubmissionRows failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("refreshMySubmissionRows succeeded");
			}
		});
	}

	public static void refreshQuestionRows() {

		logMessage("Executing refreshQuestionRows");
		enkiService.refreshQuestionRows(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("refreshQuestionRows failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("refreshQuestionRows succeeded");
			}
		});
	}
}
