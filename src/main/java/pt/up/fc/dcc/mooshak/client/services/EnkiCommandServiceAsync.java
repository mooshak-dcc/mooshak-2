package pt.up.fc.dcc.mooshak.client.services;

import java.util.Date;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;
import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementsListResponse;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Resources;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface EnkiCommandServiceAsync {

	void getResources(AsyncCallback<CourseList> callback);

	void getLeaderboard(String courseId, String problemId,
			AsyncCallback<LeaderboardResponse> callback);

	void getProblemStatistics(String problemId,
			AsyncCallback<ProblemStatistics> callback);

	void getAchievementsUnlocked(String courseId, String problemId, 
			AsyncCallback<AchievementsListResponse> callback);

	void getAchievementsRevealed(String courseId, String problemId, 
			AsyncCallback<AchievementsListResponse> callback);

	void getAllSkeletons(String problemId, AsyncCallback<MooshakValue> callback);

	void getPublicTestCases(String problemId,
			AsyncCallback<Map<String, String>> callback);

	void getRelatedResources(String courseId, String resourceId,
			AsyncCallback<Resources> callback);

	void getProfile(String courseId, 
			AsyncCallback<StudentProfile> callback);

	void syncSubmissionResult(String courseId, String resourceId, String submissionId,
			AsyncCallback<Void> callback);
	
	void checkTestPassed(String expected, String obtained, AsyncCallback<Boolean> callback);

	void syncResourceLearningTime(String courseId, String resourceId, Date learningTime,
			AsyncCallback<Void> asyncCallback);

	void notifyResourceSeen(String courseId, String resourceId, 
			AsyncCallback<Void> callback);

	void getResourceLearningTime(String courseId, String resourceId,
			AsyncCallback<Date> callback);

	void getFeedbackToResource(String courseId, String resourceId, 
			AsyncCallback<Feedback> callback);

	void sendFeedbackToResource(String courseId, String resourceId, int rating, String comment,
			AsyncCallback<Feedback> callback);

	void getProgramSkeletonByFilename(String problemId, String name, 
			AsyncCallback<String> callback);

	void getSolution(String problemId, AsyncCallback<byte[]> callback);

	void refreshMySubmissionRows(AsyncCallback<Void> asyncCallback);

	void refreshQuestionRows(AsyncCallback<Void> asyncCallback);

}
