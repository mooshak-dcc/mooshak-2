package pt.up.fc.dcc.mooshak.client.services;

import java.util.Date;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;
import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementsListResponse;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Resources;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("enkiCommand")
public interface EnkiCommandService extends RemoteService {

	public CourseList getResources() throws MooshakException;
	
	public LeaderboardResponse getLeaderboard(String courseId, 
			String problemId) throws MooshakException;
	
	public ProblemStatistics getProblemStatistics(String problemId)
		throws MooshakException;
	
	public AchievementsListResponse getAchievementsUnlocked(
			String courseId, String problemId) throws MooshakException;
	
	public AchievementsListResponse getAchievementsRevealed(
			String courseId, String problemId) throws MooshakException;
	
	public MooshakValue getAllSkeletons(String problemId) 
			throws MooshakException;
	
	public Map<String, String> getPublicTestCases(String problemId) 
			throws MooshakException;

	public Resources getRelatedResources(String courseId, 
			String resourceId) throws MooshakException;

	public StudentProfile getProfile(String courseId) 
			throws MooshakException;
	
	public void syncSubmissionResult(String courseId, String resourceId,
			String submissionId) throws MooshakException;
	
	public void notifyResourceSeen(String courseId, String resourceId)
			throws MooshakException;

	public boolean checkTestPassed(String expected, String obtained)
			throws MooshakException;

	void syncResourceLearningTime(String courseId, String resourceId, 
			Date learningTime) throws MooshakException;
	
	public Date getResourceLearningTime(String courseId, String resourceId) 
			throws MooshakException;
	
	public Feedback getFeedbackToResource(String courseId, String resourceId) 
			throws MooshakException;

	public Feedback sendFeedbackToResource(String courseId, String resourceId,
			int rating, String comment) throws MooshakException;

	public String getProgramSkeletonByFilename(String problemId, String name) 
			throws MooshakException;

	public byte[] getSolution(String problemId) throws MooshakException;

	void refreshMySubmissionRows() throws MooshakException;

	void refreshQuestionRows() throws MooshakException;
	
	
}
