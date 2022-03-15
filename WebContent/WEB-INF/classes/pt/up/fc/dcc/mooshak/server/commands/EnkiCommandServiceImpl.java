package pt.up.fc.dcc.mooshak.server.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.services.EnkiCommandService;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;
import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementsListResponse;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Resources;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

public class EnkiCommandServiceImpl extends CommandService implements
		EnkiCommandService {
	
	private static final long serialVersionUID = 1L;
	private static final String STUDENT_PROFILE_ID = "team";
	
	/* ----------------- Enki Commands ----------------- */
	
	@Override
	public CourseList getResources() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);

		CourseList response = enkiManager.getResourcesForStudent(session);
		auditLog("getResources");
		return response;
	}

	@Override
	public LeaderboardResponse getLeaderboard(String courseId, 
			String problemId) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);

		LeaderboardResponse response = enkiManager.getRankings(courseId, problemId);
		auditLog("getLeaderboard", courseId, problemId);
		return response;
	}

	@Override
	public ProblemStatistics getProblemStatistics(String problemId)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		ProblemStatistics problemStatistics = enkiManager
				.gatherProblemStatistics(session, problemId);
		auditLog("getProblemStatistics", problemId);
		
		return problemStatistics;
	}

	@Override
	public AchievementsListResponse getAchievementsUnlocked(
			String courseId, String problemId) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		AchievementsListResponse response = enkiManager
				.getAchievements(courseId, problemId, session.getParticipant().getIdName(), 
						"UNLOCKED");
		auditLog("getAchievementsUnlocked", courseId, problemId);
		
		return response;
	}

	@Override
	public AchievementsListResponse getAchievementsRevealed(
			String courseId, String problemId) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		AchievementsListResponse response = enkiManager
				.getAchievements(courseId, problemId, session.getParticipant()
						.getIdName(), "REVEALED");

		auditLog("getAchievementsRevealed", courseId, problemId, 
				session.getParticipant().getIdName());
		return response;
	}

	@Override
	public MooshakValue getAllSkeletons(String problemId)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		MooshakValue value = enkiManager.getAllSkeletons(session, problemId);
		auditLog("getAllSkeletons",problemId);
		return value;
	}

	@Override
	public Map<String, String> getPublicTestCases(String problemId)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		Map<String, String> tests = participantManager.getPublicTestCases(session, problemId);
		auditLog("getPublicTestCases",problemId);
		return tests;
	}

	@Override
	public Resources getRelatedResources(String courseId, String resourceId) 
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);

		Resources related = enkiManager.getRelatedResources(session, courseId, 
				resourceId);
		auditLog("getRelatedResources", courseId, resourceId);
		return related;
	}

	@Override
	public StudentProfile getProfile(String courseId)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		StudentProfile profile = enkiManager.getStudentProfile(session, 
				courseId);
		auditLog("getProfile", courseId);
		return profile;
	}

	@Override
	public void syncSubmissionResult(String courseId, String resourceId, 
			String submissionId) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		enkiManager.syncSubmissionResult(session, courseId, resourceId, submissionId);
		auditLog("syncSubmissionResult", courseId, resourceId, submissionId);
	}

	@Override
	public void syncResourceLearningTime(String courseId, String resourceId, 
			Date learningTime) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		enkiManager.syncResourceLearningTime(session, courseId, resourceId, 
				learningTime);
		auditLog("syncResourceLearningTime", courseId, resourceId, 
				new SimpleDateFormat("dd-MM-yy_hh:mm:ss").format(learningTime));
	}

	@Override
	public void notifyResourceSeen(String courseId, String resourceId) 
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		enkiManager.notifyResourceSeen(session, courseId, resourceId);
		auditLog("notifyResourceSeen", courseId, resourceId);
	}
	
	@Override
	public boolean checkTestPassed(String expected, String obtained)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		boolean pass = enkiManager.checkTestPassed(session, expected, obtained);
		auditLog("checkTestPassed", expected, obtained);
		return pass;
	}

	@Override
	public Date getResourceLearningTime(String courseId, String resourceId) 
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		Date date = enkiManager.getResourceLearningTime(session, courseId, resourceId);
		auditLog("getResourceLearningTime", courseId, resourceId);
		
		return date;
	}

	@Override
	public Feedback getFeedbackToResource(String courseId, String resourceId)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		Feedback feedback = enkiManager.getResourceFeedback(session, courseId, resourceId);
		auditLog("getFeedbackToResource", courseId, resourceId);
		
		return feedback;
	}
	
	@Override
	public Feedback sendFeedbackToResource(String courseId, String resourceId,
			int rating, String comment) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		Feedback feedback = enkiManager.sendFeedbackToResource(session, courseId, resourceId,
				rating, comment);
		auditLog("sendFeedbackToResource", courseId, resourceId, rating + "", 
				comment);
		
		return feedback;
	}

	@Override
	public String getProgramSkeletonByFilename(String problemId, String name) 
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		String skeleton = enkiManager.getProgramSkeletonByFilename(session, problemId, name);
		auditLog("getProgramSkeletonByFilename", problemId, name);
		return skeleton;
	}
	
	@Override
	public byte[] getSolution(String problemId)	throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		byte[] solution = enkiManager.getSolution(session, problemId);
		auditLog("getSolution", problemId);
		return solution;
	}

	@Override
	public void refreshMySubmissionRows() throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		rowManager.refreshMySubmissionRows(session);
		auditLog("refreshMySubmissionRows");
	}

	@Override
	public void refreshQuestionRows() throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, STUDENT_PROFILE_ID);
		
		rowManager.refreshQuestionRows(session);
		auditLog("refreshQuestionRows");
	}
}
