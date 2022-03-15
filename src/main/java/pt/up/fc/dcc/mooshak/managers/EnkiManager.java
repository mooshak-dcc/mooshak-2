package pt.up.fc.dcc.mooshak.managers;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.client.Entity;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.AchievementImage;
import pt.up.fc.dcc.mooshak.content.types.AchievementsImages;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contest.Gui;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Skeleton;
import pt.up.fc.dcc.mooshak.content.types.Skeletons;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.content.util.Compare;
import pt.up.fc.dcc.mooshak.managers.utils.HttpRequestBroker;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.PoolingResponseWrapper;
import pt.up.fc.dcc.mooshak.shared.events.AchievementsUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.LeaderboardUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.ProblemStatisticsUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.RatingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.events.ResourceStateUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.StudentProfileUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;
import pt.up.fc.dcc.mooshak.shared.results.gamification.Achievement;
import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementsListResponse;
import pt.up.fc.dcc.mooshak.shared.results.gamification.Leaderboard;
import pt.up.fc.dcc.mooshak.shared.results.gamification.Leaderboard.Order;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;
import pt.up.fc.dcc.mooshak.shared.results.gamification.StudentInfo;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CompletionUpdate;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Course;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CoursePart;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseUpdate;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Resources;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StatusUpdate;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Student;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Summary;

public class EnkiManager extends Manager {
	
	private static EnkiManager manager = null;
	
	//private static LRUCache<String, Object> cache = new LRUCache<>(100, 30 * 1000);
	
	public static String gamificationServer = null;
	public static String sequenciationServer = null;
	public static final String INSTITUTION = "fcup";
	private static final long DEFAULT_POLLING_TIME = 30 * 1000;
	
	private static final int 	AVAILABLE 
			= Runtime.getRuntime().availableProcessors();
	private static final ExecutorService POOL
			= Executors.newFixedThreadPool(AVAILABLE);	
	
	/**
	 * Get a single instance of this class
	 * 
	 * @return
	 */
	public static EnkiManager getInstance() {
		if (manager == null) {
			manager = new EnkiManager();
		}
		return manager;
	}

	private EnkiManager() {
	}
	
	/**
	 * @return the gamificationServer
	 */
	public static String getGamificationServer() {
		return gamificationServer;
	}

	/**
	 * @param gamificationServer the gamificationServer to set
	 */
	public static void setGamificationServer(String gamificationServer) {
		EnkiManager.gamificationServer = gamificationServer;
	}

	/**
	 * @return the sequenciationServer
	 */
	public static String getSequenciationServer() {
		return sequenciationServer;
	}

	/**
	 * @param sequenciationServer the sequenciationServer to set
	 */
	public static void setSequenciationServer(String sequenciationServer) {
		EnkiManager.sequenciationServer = sequenciationServer;
	}

	/**
	 * Get xml string with resources
	 * @param session
	 * @return
	 * @throws MooshakException
	 */
	public Course getResources(Session session) throws MooshakException {
		Contest contest = session.getContest();
			
		return contest.getCourse();
	}

	/**
	 * Get id of the course
	 * @param session
	 * @return
	 * @throws MooshakException 
	 */
	public String getCourseId(Session session) throws MooshakException {
		
		Contest contest = session.getContest();
		
		Course course = contest.getCourse();
		
		if (course == null)
			return null;
	
		return course.getId();
	}


	/**
	 * Register a player in the gamification service
	 * 
	 * @param session {@link Session} session of the user
	 * @throws MooshakException
	 */
	public void registerPlayerAtGS(Session session)
			throws MooshakException {
		
		String studentId = session.getParticipant().getIdName();
		String studentName = session.getParticipant().getName();

		StudentInfo player = new StudentInfo();
		player.setPlayerId(studentId);
		player.setDisplayName(studentName == null ? studentId : studentName);
		
		try {
			HttpRequestBroker.getInstance().post(gamificationServer, 
					"gamify/institutions/" + INSTITUTION + "/players",
					Entity.json(player), Void.class);
		} catch (Exception e) {
			throw new MooshakException("Error registering player at Gamification "
					+ "Service: " + e.getMessage());
		}
	}
	
	
	/**
	 * Submit the result of a submission to the Tool Consumer (Moodle)
	 * @param session
	 * @param userId The id of the user
	 * @param grade Int 0 100
	 */
	public void submitScoreToTC(Session session, String userId,
			int grade) throws MooshakException {
		
		if (session.getLtiChannel() == null)
			return;
		
		if (!session.getLtiChannel().submitScore(
				(double)grade/(double)100))
			throw new MooshakException("Could not send grade to the "
					+ "Tool Consumer");
	}
	
	
	/**
	 * Submit the result of a submission to the Gamification service
	 * @param session
	 * @param userId The id of the user
	 * @param submission The submission made
	 * @throws MooshakException 
	 */
	public void submitScoreToGS(Session session, String leaderboardId, String userId, 
			Submission submission) throws MooshakException {
		
		Classification classify = submission.getClassify();
		
		if (classify.equals(Classification.EVALUATING))
			throw new MooshakException("Submission is evaluating. "
					+ "A grade is not available yet.");
		if (classify.equals(Classification.ACCEPTED))
			submitScoreToGS(session, userId, leaderboardId,
					100);
		else 
			submitScoreToGS(session, userId, leaderboardId,
					submission.getMark());
	}
	
	
	/**
	 * Submit the result of a submission to the Gamification service
	 * @param session
	 * @param userId The id of the user
	 * @param problemId 
	 * @param score The obtained score
	 * @throws MooshakException 
	 */
	public void submitScoreToGS(Session session, String userId, 
			String leaderboardId, float score) throws MooshakException {

		try {
			
			HttpRequestBroker.getInstance().post(gamificationServer,
					"gamify/institutions/" + INSTITUTION + "/leaderboards/" 
							+ leaderboardId + "/scores", null, String.class, 
							"playerId", userId, "score", String.valueOf(score));
		} catch (Exception e) {
			throw new MooshakException("Error submitting score to the Gamification "
					+ "Service: " + e.getMessage());
		}
	}
	
	/**
	 * Get the rankings (presented in the leaderboard identified as leaderboardId)
	 * from the gamification service
	 * 
	 * @param leaderboardId
	 * @return Leaderboard
	 * @throws MooshakException 
	 */
	public LeaderboardResponse getRankings(String courseId, String problemId)
			throws MooshakException {
		
		return getRankings(courseId, problemId,
				"PUBLIC", "ALL_TIME");
	}

	/**
	 * Get the rankings (presented in the leaderboard identified as leaderboardId)
	 * from the gamification service
	 * 
	 * @param leaderboardId
	 * @param ranking PUBLIC or SOCIAL
	 * @param timespan ALL_TIME, DAILY or WEEKLY
	 * @return Leaderboard
	 * @throws MooshakException 
	 */
	private LeaderboardResponse getRankings(String courseId, String problemId,
			String ranking, String timespan) throws MooshakException {
		
		if (ranking == null)
			ranking = "PUBLIC";
		
		if (timespan == null)
			timespan = "ALL_TIME";
		
		String leaderboardId = courseId;
		if (problemId != null && !problemId.equals(""))
			leaderboardId += "_" + problemId;
		
		LeaderboardResponse leaderboard;
		try {
			leaderboard = HttpRequestBroker.getInstance()
					.get(gamificationServer, "gamify/institutions/" + INSTITUTION + 
							"/leaderboards/" + leaderboardId + "/scores/" + ranking,
					LeaderboardResponse.class, 
					"timeSpan", timespan, "maxResults", String.valueOf(Integer.MAX_VALUE));
		} catch (Exception e) {
			throw new MooshakException("Could not get leaderboard from the Gamification Service: "
					+ e.getMessage());
		}
		
		return leaderboard;
	}
	
	/**
	 * Gathers the statistics of submissions to a problem
	 * @param session
	 * @param problemId
	 * @return
	 * @throws MooshakException
	 */
	public ProblemStatistics gatherProblemStatistics(Session session,
			String problemId) throws MooshakException {

		if (problemId != null && problemId.
				lastIndexOf("/" + session.getContest().getIdName() + "/") != -1) {
			String contestPath = "/" + session.getContest().getIdName() + "/";
			problemId = problemId.substring(problemId.
					lastIndexOf(contestPath) + contestPath.length());
		}
		
		ProblemStatistics problemStatistics = new ProblemStatistics();
		
		Submissions submissions = ParticipantManager.getInstance()
				.getSubmissions(session);
		
		List<Submission> problemSubmissions = submissions
				.getSubmissionsByProblem(problemId);
		problemStatistics.setSolvingAttempts(problemSubmissions.size());

		int total = 0, tried = 0, solved = 0;
		int solvedAtFirst = 0, solvedAtSecond = 0, solvedAtThird = 0;
		
		Groups groups = session.getContest().open("groups");
		try(POStream<Team> stream = groups.newPOStream()) {
			for (Team team : stream) {

				int wrongs = 0;
				int accepteds = 0;

				for (Submission submission : problemSubmissions) {
					if (submission.getTeamId() != null &&
							submission.getTeamId().equals(team.getIdName())) {
						if (submission.getClassify().equals(Classification.ACCEPTED))
							accepteds++;
						else 
							wrongs++;
					}
				}

				if (accepteds > 0) {
					solved++;
					if (wrongs == 2)
						solvedAtThird++;
					else if (wrongs == 1)
						solvedAtSecond++;
					else if (wrongs == 0)
						solvedAtThird++;
				}

				if ((wrongs + accepteds) > 0)
					tried++;

				total++;
			} } catch(Exception cause) {
				LOGGER.log(Level.SEVERE,"Error iterating over groups",cause);
			}
		
		problemStatistics.setTotalStudents(total);
		problemStatistics.setNumberOfStudentsWhoSolved(solved);
		problemStatistics.setSolvedAtFirst(solvedAtFirst);
		problemStatistics.setSolvedAtSecond(solvedAtSecond);
		problemStatistics.setSolvedAtThird(solvedAtThird);
		problemStatistics.setNumberOfStudentsWhoTried(tried);
		
		return problemStatistics;
	}

	
	/**
	 * Get the achievements of a player in a leaderboard
	 * 
	 * @param courseId {@link String} ID of the course
	 * @param problemId {@link String} ID of the problem
	 * @param playerId {@link String} ID of the player
	 * @return {@link AchievementsListResponse} list of achievements
	 * @throws MooshakException 
	 */
	public AchievementsListResponse getAchievements(String courseId,
			String problemId, String playerId, String state) throws MooshakException {
		
		String leaderboardId = courseId;
		if (problemId != null && !problemId.equals(""))
			leaderboardId += "_" + problemId;
		
		AchievementsListResponse achievements;
		try {
			achievements = HttpRequestBroker.getInstance()
					.get(gamificationServer, "gamify/institutions/" + INSTITUTION + 
							"/leaderboards/" + leaderboardId + "/achievements/players/" + playerId,
					AchievementsListResponse.class, 
					"state", state, "maxResults", String.valueOf(Integer.MAX_VALUE));
		} catch (Exception e) {
			throw new MooshakException("Could not get achievements from the Gamification Service: "
					+ e.getMessage());
		}
		
		return achievements;
	}

	/**
	 * Gets all skeletons wrapped in a MooshakValue
	 * @param session
	 * @param problemId
	 * @return
	 * @throws MooshakException
	 */
	public MooshakValue getAllSkeletons(Session session, String problemId) 
			throws MooshakException {
		
		Contest contest = session.getContest();
		
		Problems problems = contest.open("problems");
		Problem problem = null;
		try {
			problem = problems.open(problemId);
		} catch (Exception e) {
			throw new MooshakException("Problem not found.");
		}

		Skeletons skeletonsObj;
		try {
			skeletonsObj = problem.open("skeletons");
		} catch (Exception e) {
			throw new MooshakException("No Skeletons");
		}
		
		List<Skeleton> skeletons = skeletonsObj.getAllSkeletons(false);
		
		MooshakValue value = new MooshakValue("skeletons");
		
		for (Skeleton skeleton : skeletons) {
			value.addFileValue(new MooshakValue(skeleton.getIdName(), skeleton.getIdName(), 
					skeleton.getSkeletonCode().getBytes()));
			
		}
		
		return value;
	}
	
	/**
	 * Insert {@link Course} course into sequenciation service
	 * 
	 * @param course {@link Course} course
	 * @return {@link Course} course
	 */
	public Course insertCourseIntoSequenciationService(Course course) {
		
		try {
			HttpRequestBroker.getInstance()
					.put(sequenciationServer, "courses", Entity.xml(course), Void.class);
		} catch (Exception e) {
			LOGGER.severe("Could not insert course into Sequenciation Service: "
					+ e.getMessage());
		}

		return course;
	}
	
	/**
	 * Get the resources annotated for a student
	 * @param session
	 * @return
	 * @throws MooshakException
	 */
	public CourseList getResourcesForStudent(Session session)
			throws MooshakException {
		
		Course course = getResources(session);
		if (course == null)
			throw new MooshakException("Missing course structure!");

        CourseList courseList = new CourseList();
		
		String id = course.getId();
		String studentId = session.getParticipant().getIdName();
		
		try {
			course = HttpRequestBroker.getInstance()
					.get(sequenciationServer, "courses/" + id + "/students/" + studentId,
							Course.class);
		} catch (Exception e) {
			LOGGER.severe("Could not get course from the Sequenciation Service: "
					+ e.getMessage());
		}
		
        courseList.addCourse(course);
        return courseList;
	}
	
	/**
	 * Add resources of a course to the Gamification Service
	 * 
	 * @param course
	 * @throws MooshakException 
	 */
	public void updateGamificationServiceResources(Course course)
			throws MooshakException {
		
		HttpRequestBroker.getInstance()
			.post(gamificationServer, "gamify/institutions/" + INSTITUTION + "/leaderboards",
					Entity.json(new Leaderboard(course.getId(),
						Order.DESC, (float) 0, (float) Integer.MAX_VALUE)), Void.class);

		updateCourseAchievements(course);
	}

	/**
	 * @param course
	 * @throws MooshakContentException
	 * @throws MooshakException
	 */
	private void updateCourseAchievements(Course course) 
			throws MooshakException {
		
		AchievementsImages achievementsImages = PersistentObject.openPath("data/configs/"
				+ "achievements");
		
		AchievementImage courseRevealed = achievementsImages.open("course-revealed");
		AchievementImage courseCompleted = achievementsImages.open("course-completed");
		
		Achievement achievement = new Achievement("course_" + course.getId(), course.getId() 
				+ " Completion", course.getId(), "Completed Course " + course.getId(), 
				"STANDARD", 0, "0", courseRevealed.getIdName(), true, courseCompleted.getIdName(),
				false, "REVEALED", (long) 1000);
				
		HttpRequestBroker.getInstance()
			.put(gamificationServer, "gamify/institutions/" + INSTITUTION + "/leaderboards/"
					+ course.getId() + "/achievements", Entity.json(achievement), Void.class);
		
		for (CoursePart coursePart : course.getChildren()) {
			updatePartAchievements(course.getId(), coursePart);
		}
	}

	private void updatePartAchievements(String courseId, CoursePart coursePart) 
			throws MooshakException {
		
		AchievementsImages achievementsImages = PersistentObject.openPath("data/configs/"
				+ "achievements");
		
		AchievementImage partRevealed = achievementsImages.open("module-revealed");
		AchievementImage partCompleted = achievementsImages.open("module-completed");
		
		Achievement achievement = new Achievement("part_" + coursePart.getId(), 
				coursePart.getId() + " Completion", courseId,
				"Completed Part " + coursePart.getId(), "STANDARD", 0, "0", 
				partRevealed.getIdName(), true, partCompleted.getIdName(), false, 
				"HIDDEN", (long) 50);
		
		HttpRequestBroker.getInstance().put(gamificationServer,
				"gamify/institutions/" + INSTITUTION + "/leaderboards/" + courseId + "/achievements",
				Entity.json(achievement), Void.class);
		
		for (CoursePart part : coursePart.getChildren()) {
			updatePartAchievements(courseId, part);
		}
	}

	/**
	 * Get resources related to resourceId
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @return
	 */
	public Resources getRelatedResources(Session session, String courseId,
			String resourceId) throws MooshakException {
		
		Resources result;
		try {
			result = HttpRequestBroker.getInstance()
				.get(sequenciationServer, "courses/" + courseId + "/related", Resources.class, 
						"resourceId", resourceId, "studentId", session.getParticipant().getIdName());
		} catch (Exception e) {
			throw new MooshakException("Error retrieving related resources: "
					+ e.getMessage());
		}
		
		return result;
	}
	
	/**
	 * Get the profile of the student in the course
	 * 
	 * @param session
	 * @return the profile of the student in the course
	 * @throws MooshakException 
	 * @throws MooshakContentException 
	 */
	public StudentProfile getStudentProfile(Session session, String courseId) 
			throws MooshakException {
		StudentProfile profile = new StudentProfile();
		
		profile.setStudentName(session.getParticipant().getName() == null
				? session.getParticipant().getIdName() : session.getParticipant().getName());
		
		if (session.getParticipant() instanceof Team) {
			Team team = (Team) session.getParticipant();
			profile.setRegistrationDate(team.getStart());
		} else {
			profile.setRegistrationDate(session.getContest().getStart());
			/*BasicFileAttributes attr;
			try {
				attr = Files.readAttributes(PersistentObject.open(session.getParticipant()
						.getPath()).getAbsoluteFile(), BasicFileAttributes.class);
				FileTime fileCreated = attr.creationTime();
				profile.setRegistrationDate(new Date(fileCreated.toMillis()));
			} catch (MooshakContentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
		}
		
		Summary summary = getStudentSummary(session.getParticipant().getIdName(), courseId);
		profile.setSolvedExercises(summary.getSolved());
		profile.setVideoResourcesSeen(summary.getVideos());
		profile.setStaticResourcesSeen(summary.getStaticResources());
		profile.setCurrentPart(summary.getPart());
		
		Submissions submissions = session.getContest().open("submissions");
		List<Submission> submissionList = submissions
				.getSubmissionsByParticipant(session.getParticipant()
				.getIdName());
		
		int accepted = 0;
		for (Submission submission : submissionList) {
			if (submission.getClassify().equals(Classification.ACCEPTED))
				accepted++;
		}
		
		profile.setAcceptedSubmissions(accepted);
		profile.setSubmissions(submissionList.size());
		
		return profile;
	}

	/**
	 * Get a summary of the student's state in a course
	 * 
	 * @param studentId ID of the student
	 * @param courseId ID of the course
	 * @return {@link Summary} summary of the student's state in a course
	 * @throws MooshakException
	 */
	private Summary getStudentSummary(String studentId, String courseId)
			throws MooshakException {
		
		Summary summary;
		try {
			summary = HttpRequestBroker.getInstance()
				.get(sequenciationServer, "courses/" + courseId + "/students/"
					+ studentId + "/summary", Summary.class);
		} catch (Exception e) {
					/*summary = new Summary();
					summary.setSolved(countSolvedProblems(courseId, studentId));
					summary.setVideos(countSeenVideos(courseId, studentId));
					summary.setStaticResources(countSeenStatic(courseId, studentId));
					summary.setPart(getCurrentPart(courseId, studentId));*/
			throw new MooshakException("Error retrieving summary of the student's state: "
					+ e.getMessage());
		}	
		
		return summary;
	}

	/**
	 * Get current part
	 * 
	 * @param courseId
	 * @param studentId
	 * @return
	 * @throws MooshakException
	 *//*
	@Deprecated
	private String getCurrentPart(String courseId, String studentId) 
			throws MooshakException {
		
		String targetURL = sequenciationServer + "courses/" + courseId + "/students/"
				+ studentId + "/part";
		
		WebTarget resource = client.target(targetURL);
		Response response =	resource
				.request()
				.get();
		
		String resp = null;

		if (response == null || response.getStatusInfo().getStatusCode() != 200) {
			
			if (response != null) {
				resp = response.readEntity(String.class);
				response.close();
			}
			throw new MooshakException("Error retrieving current part!");
		}
		
		resp = response.readEntity(String.class);
		
		if (response != null)
			response.close();
		
		return resp;
	}*/

	/**
	 * Get number of resources solved
	 * 
	 * @param courseId
	 * @param studentId
	 * @return
	 * @throws MooshakException
	 *//*
	@Deprecated
	private int countSolvedProblems(String courseId, String studentId)
			throws MooshakException {

		WebTarget resource = client
				.target(sequenciationServer + "courses/" + courseId + "/students/"
						+ studentId + "/solved");
		Response response =	resource
				.request()
				.get();

		if (response == null || response.getStatusInfo().getStatusCode() != 200) {
			
			if (response != null)
				response.close();

			throw new MooshakException("Error retrieving number of solved"
					+ " problems!");
		}
		
		String respXml = response.readEntity(String.class);
		
		if (response != null)
			response.close();
		
		return Integer.parseInt(respXml);
	}*/

	/**
	 * Get number of videos seen
	 * 
	 * @param courseId
	 * @param studentId
	 * @return
	 * @throws MooshakException
	 *//*
	@Deprecated
	private int countSeenVideos(String courseId, String studentId)
			throws MooshakException {

		WebTarget resource = client
				.target(sequenciationServer + "courses/" + courseId + "/students/"
						+ studentId + "/seen/videos");
		Response response =	resource
				.request()
				.get();

		if (response == null || response.getStatusInfo().getStatusCode() != 200) {
			
			if (response != null)
				response.close();
			throw new MooshakException("Error retrieving number of seen videos!");
		}
		
		String respXml = response.readEntity(String.class);
		
		if (response != null)
			response.close();
		
		return Integer.parseInt(respXml);
	}*/

	/**
	 * Get number of static resources seen
	 * 
	 * @param courseId
	 * @param studentId
	 * @return
	 * @throws MooshakException
	 *//*
	@Deprecated
	private int countSeenStatic(String courseId, String studentId)
			throws MooshakException {

		WebTarget resource = client
				.target(sequenciationServer + "courses/" + courseId + "/students/"
						+ studentId + "/seen/static");
		Response response =	resource
				.request()
				.get();

		if (response == null || response.getStatusInfo().getStatusCode() != 200) {
			
			if (response != null)
				response.close();
			throw new MooshakException("Error retrieving number of static resources"
					+ " seen!");
		}
		
		String respXml = response.readEntity(String.class);
		
		if (response != null)
			response.close();
		
		return Integer.parseInt(respXml);
	}*/
	
	
	/**
	 * Update resource state
	 * @param session
	 * @param courseId
	 * @param studentId
	 * @param resourceId
	 * @param state
	 * @param learningTime
	 * @return
	 * @throws MooshakException
	 */
	public StatusUpdate updateResourceState(Session session, String courseId, 
			String studentId, String resourceId, 
			ResourceState state, Date learningTime) throws MooshakException {
		
		return updateResourceState(session, courseId, studentId, resourceId, state, 
				learningTime, -1);
	}

	/**
	 * Update resource state
	 * @param session
	 * @param courseId
	 * @param studentId
	 * @param resourceId
	 * @param state
	 * @param learningTime
	 * @param grade
	 * @return
	 * @throws MooshakException
	 */
	public StatusUpdate updateResourceState(Session session, String courseId, 
			String studentId, String resourceId, 
			ResourceState state, Date learningTime, int grade) throws MooshakException {
		
		List<String> queryParams = new ArrayList<>();
		queryParams.add("resource");
		queryParams.add(resourceId);
		
		if (state != null) {
			queryParams.add("state");
			queryParams.add(state.toString());
		}

		if (learningTime != null) {
			queryParams.add("learningTime");
			queryParams.add(String.valueOf(learningTime.getTime()));
		}
		
		queryParams.add("grade");
		queryParams.add(String.valueOf(grade));

		StatusUpdate update;
		try {
			update = HttpRequestBroker.getInstance()
				.post(sequenciationServer, "courses/" + courseId + "/students/"	+ studentId,
						null, StatusUpdate.class, queryParams.toArray(new String[queryParams.size()]));
		} catch (Exception e) {
			throw new MooshakException("Error updating resource state: " + e.getMessage());
		}
		
		try {
			
			for (CourseUpdate courseUpdate : update.getUpdates()) {
				sendResourceStateUpdateEvent(session, courseId, courseUpdate.getNodeId(), 
						courseUpdate.getState());
			}
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Failed to send resource update "
					+ "event!");
		}
		
		for (CompletionUpdate completionUpdate : update.getCompletions()) {
			finishPart(courseId, completionUpdate.getPartId(), studentId);
		}
		
		POOL.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					// refresh achievements of the player submitting to the problem
					refreshAchievements(session, courseId, resourceId, session.getParticipant()
							.getIdName());
				} catch (MooshakException e) {
					// ignore errors silently
				}
			}
		});
		
		POOL.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					// refresh profile
					refreshProfile(session, courseId);
				} catch (MooshakException e) {
					// ignore errors silently
				}
			}
		});

		return update;
	}

	/**
	 * Method called when a student has finished a part of the course
	 * @param courseId
	 * @param topic
	 * @param studentId
	 * @throws MooshakException 
	 */
	private void finishPart(String courseId, String partId, String studentId) 
			throws MooshakException {
		
		try {
			HttpRequestBroker.getInstance().post(gamificationServer, "gamify/institutions/" + INSTITUTION +
				"/leaderboards/" + courseId + "/achievements/part_" + partId + "/players/" + studentId + "/unlock", 
				null, Void.class);
		} catch (Exception e) {
			throw new MooshakException("Error finishing part on the Gamification Service: "
					+ e.getMessage());
		}
	}

	/**
	 * Set resource as seen in Sequenciation Service
	 * 
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @throws MooshakException
	 */
	public void notifyResourceSeen(Session session, String courseId, String resourceId)
		throws MooshakException{

		String studentId = session.getParticipant().getIdName();
			
		updateResourceState(session, courseId, studentId, 
				resourceId, ResourceState.SEEN, null);
	}

	/**
	 * Notify Sequencing and Gamification services of submission to a problem
	 * 
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @param submissionId
	 * @throws MooshakException
	 */
	public void syncSubmissionResult(Session session, String courseId, 
			String resourceId, String submissionId) 
					throws MooshakException {
		
		Contest contest = session.getContest();
		
		if (!contest.getGui().equals(Gui.ENKI))
			return;
		
		Submissions submissions = contest.open("submissions");
		Submission submission = submissions.open(submissionId);
		
		if (submission == null)
			throw new MooshakException("Submission " + submissionId + " doesn't exist!");
		
		String studentId = session.getParticipant().getIdName();
		
		StatusUpdate update = null;
		if (submission.getClassify().equals(Classification.ACCEPTED)) {
			update = updateResourceState(session, courseId, studentId, resourceId, 
					ResourceState.SOLVED, null, submission.getMark());
		}	
		else {
			update = updateResourceState(session, courseId, studentId, resourceId, 
					null, null, submission.getMark());
		}
		
		try {
			submitScoreToGS(session, studentId, courseId, update.getSolved());
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Failed submitting score to "
					+ "Gamification Service. Reason: " + e.getMessage());
		}
		
		/*
		try {
			submitScoreToGS(session, courseId + "_" + submission.getProblemId(), studentId, 
					submission);
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Failed submitting score to Gamification "
					+ "Service. Reason: " + e.getMessage());
		}
		*/
		
		try {
			submitScoreToTC(session, studentId, getSolvedEvaluativePercentage(courseId, studentId));
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Failed submitting score to Tool "
					+ "Consumer. Reason: " + e.getMessage());
		}
		
		POOL.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					// refresh achievements of the player submitting to the problem
					refreshAchievements(session, courseId, resourceId, session.getParticipant()
							.getIdName());
				} catch (MooshakException e) {
					// ignore errors silently
				}
			}
		});
		
		POOL.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					// refresh profile
					refreshProfile(session, courseId);
				} catch (MooshakException e) {
					// ignore errors silently
				}
			}
		});
		
		POOL.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					// refresh leaderboard of the course and problem
					refreshLeaderboard(session, courseId, resourceId);
				} catch (MooshakException e) {
					// ignore errors silently
				}
			}
		});
		
		POOL.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					// refresh problem statistics
					refreshProblemStatistics(session, courseId, resourceId);
				} catch (MooshakException e) {
					// ignore errors silently
				}
			}
		});
		
	}

	/**
	 * Refresh leaderboard and broadcast events
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @throws MooshakException 
	 */
	private void refreshLeaderboard(Session session, String courseId, String resourceId) 
			throws MooshakException {
		
		LeaderboardResponse leaderboard = getRankings(courseId, null);

		broadcastLeaderboardUpdate(session.getContest().getIdName(), leaderboard, 
				null);
		
		// TODO: Uncomment following code block to refresh problem-specific
		// leaderboards
		/*try {
			leaderboard = getRankings(courseId, resourceId);
		} catch (MooshakException e) {
			// No leaderboard for this resource, broadcast general!
		}
		
		broadcastLeaderboardUpdate(leaderboard);*/
	}

	/**
	 * Informs all listeners that a leaderboard was updated
	 * @param contestId 
	 * @param leaderboard
	 * @param resourceId (optional)
	 */
	private void broadcastLeaderboardUpdate(String contestId, 
			LeaderboardResponse leaderboard, String resourceId) {
		LeaderboardUpdateEvent event = new LeaderboardUpdateEvent();
		event.setLeaderboard(leaderboard);
		if (resourceId != null)
			event.setResourceId(resourceId);
		EventSender.getEventSender().send(contestId, event);
	}

	/**
	 * Refresh achievements and send events
	 * @param session 
	 * @param courseId
	 * @param resourceId
	 * @param participantId
	 * @throws MooshakException 
	 */
	private void refreshAchievements(Session session, String courseId, String resourceId, 
			String participantId) throws MooshakException {
		
		AchievementsListResponse achievements = null;
		
		// revealed general achievements
		achievements = getAchievements(courseId, null, participantId, "REVEALED");
		
		sendAchievementsUpdateEvent(session.getContest().getIdName(), null, 
				session.getParticipant().getIdName(), "REVEALED", achievements);
		
		// TODO: Uncomment following code block to refresh revealed problem-specific
		// achievements
		/*getAchievements(courseId, resourceId, participantId, "REVEALED");*/
		
		// unlocked general achievements
		achievements = getAchievements(courseId, null, participantId, "UNLOCKED");
		
		sendAchievementsUpdateEvent(session.getContest().getIdName(), null, 
				session.getParticipant().getIdName(), "UNLOCKED", achievements);
		
		// TODO: Uncomment following code block to refresh unlocked problem-specific
		// achievements
		/*getAchievements(courseId, resourceId, participantId, "UNLOCKED");*/
	}
	
	/**
	 * Informs participant that his achievements were updated
	 * @param contestId
	 * @param resourceId
	 * @param participantId
	 * @param state
	 * @param achievements
	 */
	private void sendAchievementsUpdateEvent(String contestId, String resourceId, 
			String participantId, String state,	AchievementsListResponse achievements) {
		AchievementsUpdateEvent event = new AchievementsUpdateEvent();
		event.setRecipient(new Recipient(participantId));
		if (resourceId != null)
			event.setResourceId(resourceId);
		event.setAchievements(achievements);
		event.setState(state);
		EventSender.getEventSender().send(contestId, event);
	}

	/**
	 * Refresh the profile of the student and send events
	 * @param session 
	 * @param courseId
	 * @throws MooshakException 
	 */
	private void refreshProfile(Session session, String courseId)
			throws MooshakException {
		
		StudentProfile profile = getStudentProfile(session, courseId);
		
		sendProfileUpdateEvent(session.getContest().getIdName(), session
				.getParticipant().getIdName(), profile);
	}

	/**
	 * Informs participant that his profile was updated
	 * @param contestId
	 * @param participantId
	 * @param profile 
	 */
	private void sendProfileUpdateEvent(String contestId, String participanId,
			StudentProfile profile) {
		StudentProfileUpdateEvent event = new StudentProfileUpdateEvent();
		event.setRecipient(new Recipient(participanId));
		event.setAcceptedSubmissions(profile.getAcceptedSubmissions());
		event.setCurrentPart(profile.getCurrentPart());
		event.setRegistrationDate(profile.getRegistrationDate());
		event.setSolvedExercises(profile.getSolvedExercises());
		event.setStaticResourcesSeen(profile.getStaticResourcesSeen());
		event.setStudentName(profile.getStudentName());
		event.setSubmissions(profile.getSubmissions());
		event.setUnsolvedExercises(profile.getUnsolvedExercises());
		event.setVideoResourcesSeen(profile.getVideoResourcesSeen());
		EventSender.getEventSender().send(contestId, event);
	}

	/**
	 * Refresh the stats 
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @throws MooshakException 
	 */
	private void refreshProblemStatistics(Session session, String courseId,
			String resourceId) throws MooshakException {
		
		ProblemStatistics stats = gatherProblemStatistics(session, resourceId);
		sendProblemStatisticsUpdateEvent(session.getContest().getIdName(), session
				.getParticipant().getIdName(), resourceId, stats);
	}

	/**
	 * 
	 * @param contestId
	 * @param participantId
	 * @param resourceId 
	 * @param stats
	 */
	private void sendProblemStatisticsUpdateEvent(String contestId, String participantId,
			String resourceId, ProblemStatistics stats) {
		
		ProblemStatisticsUpdateEvent event = new ProblemStatisticsUpdateEvent();
		event.setResourceId(resourceId);
		event.setNumberOfStudentsWhoSolved(stats.getNumberOfStudentsWhoSolved());
		event.setNumberOfStudentsWhoTried(stats.getNumberOfStudentsWhoTried());
		event.setSolvedAtFirst(stats.getSolvedAtFirst());
		event.setSolvedAtSecond(stats.getSolvedAtSecond());
		event.setSolvedAtThird(stats.getSolvedAtThird());
		event.setSolvingAttempts(stats.getSolvingAttempts());
		event.setTotalStudents(stats.getTotalStudents());
		EventSender.getEventSender().send(contestId, event);
	}

	/**
	 * Get percentage of solved evaluative resources
	 * @return
	 */
	private int getSolvedEvaluativePercentage(String courseId, String studentId)
			throws MooshakException {
		
		
		String gradeStr;
		try {
			gradeStr = HttpRequestBroker.getInstance().get(sequenciationServer, 
					"courses/" + courseId + "/students/" + studentId + "/grade",
					String.class);
		} catch (Exception e) {
			throw new MooshakException("Error retrieving grade: " + e.getMessage());
		}

		return Integer.parseInt(gradeStr);
	}

	/**
	 * Send time spent in an evaluative resource to the Sequenciation Service
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @param learningTime
	 * @throws MooshakException 
	 */
	public void syncResourceLearningTime(Session session, String courseId, 
			String resourceId, Date learningTime) throws MooshakException {
		
		String studentId = session.getParticipant().getIdName();
			
		updateResourceState(session, courseId, studentId, 
				resourceId, null, learningTime);
	}
	
	/**
	 * Checks if the obtained and expected output are similar in a way that
	 * the test passes
	 * @param expected
	 * @param obtained
	 * @return
	 */
	public boolean checkTestPassed(Session session, String expected, 
			String obtained) {
		
		if(expected.equals(obtained)) 
			return true;
		else if(Compare.normalize(expected).equals(Compare.normalize(obtained)))
			return true;
		
		return false;
	}
	
	/**
	 * Send event to client notifying of an update to a resource state
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @param state
	 * @throws MooshakException 
	 */
	public void sendResourceStateUpdateEvent(Session session, String courseId, 
			String resourceId, ResourceState state) throws MooshakException {
		
		if (resourceId == null || courseId == null || state == null)
			return;
		ResourceStateUpdateEvent event = new ResourceStateUpdateEvent();
		event.setCourseId(courseId);
		event.setId(resourceId);
		event.setState(state);
		event.setRecipient(new Recipient(session.getParticipant().getIdName()));
		
		EventSender.getEventSender().send(session.getContestId(), event);
	}

	/**
	 * Registers the student in the sequenciation service
	 * 
	 * @param session {@link Session} session of the student
	 * @throws MooshakException 
	 */
	public void registerStudentAtSequenciationService(Session session) 
			throws MooshakException {
		
		String studentId = session.getParticipant().getIdName();
		
		Student student = new Student(studentId);
		student.getFeatures().put("name", session.getParticipant().getName());
		
		try {
			HttpRequestBroker.getInstance().put(sequenciationServer, "students", 
					Entity.xml(student), Void.class);
		} catch (Exception e) {
			throw new MooshakException("Error inserting student in Sequenciation service: "
					+ e.getMessage());
		}

		try {
			HttpRequestBroker.getInstance().post(sequenciationServer, "courses/" + getCourseId(session)
						+ "/students/" + studentId + "/enroll", null, Void.class);
		} catch (Exception e) {
			throw new MooshakException("Error enrolling student in Sequenciation service: "
					+ e.getMessage());
		}
	}

	/**
	 * Get the learning time of a student in a resource
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @throws MooshakException 
	 */
	public Date getResourceLearningTime(Session session, String courseId, 
			String resourceId) throws MooshakException {
		
		String studentId = session.getParticipant().getIdName();
		
		String learningTimeStr;
		try {
			learningTimeStr = HttpRequestBroker.getInstance().get(sequenciationServer, 
					"courses/" + courseId + "/students/" + studentId + "/learningtime",
					String.class, "resourceId", resourceId);
		} catch (Exception e) {
			throw new MooshakException("Error retrieving student learning time in a resource: "
					+ e.getMessage());
		}
		
		return new Date(Long.parseLong(learningTimeStr));
	}

	/**
	 * Get feedback to a resource
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @return
	 * @throws MooshakException 
	 */
	public Feedback getResourceFeedback(Session session, String courseId, String resourceId) 
			throws MooshakException {
		
		String studentId = session.getParticipant().getIdName();
		
		Feedback feedback;
		try {
			feedback = HttpRequestBroker.getInstance().get(sequenciationServer, 
					"courses/" + courseId + "/students/" + studentId + "/rate",
					Feedback.class, "resource", resourceId);
		} catch (Exception e) {
			throw new MooshakException("Error retrieving feedback of student to resource: "
					+ e.getMessage());
		}
		
		return feedback;
	}

	/**
	 * Send feedback to a resource
	 * @param session
	 * @param courseId
	 * @param resourceId
	 * @return
	 * @throws MooshakException 
	 */
	public Feedback sendFeedbackToResource(Session session, String courseId, String resourceId, 
			int rating, String comment) throws MooshakException {
		
		String studentId = session.getParticipant().getIdName();
		
		Feedback feedback;
		try {
			feedback = HttpRequestBroker.getInstance().post(sequenciationServer, 
					"courses/" + courseId + "/students/" + studentId + "/rate",
					null, Feedback.class,
					"resource", resourceId, "rating", String.valueOf(rating), "comment", comment);
		} catch (Exception e) {
			throw new MooshakException("Error sending feedback of student to resource: "
					+ e.getMessage());
		}

		RatingUpdateEvent event = new RatingUpdateEvent();
		event.setRecipient(new Recipient(session.getParticipant().getIdName()));
		if (resourceId != null)
			event.setResourceId(resourceId);
		event.setRating(feedback.getRating());
		event.setComment(feedback.getComment());
		EventSender.getEventSender().send(session.getContestId(), event);
		
		return feedback;
	}

	/**
	 * Get the kind of editor recommended for this problem
	 * @param session
	 * @param problemId
	 * @return
	 * @throws MooshakException
	 */
	public EditorKind getEditorKind(Session session, String problemId)
			throws MooshakException {
		
		Contest contest = session.getContest();
		Problems problems = contest.open("problems");
		
		Problem problem = problems.open(problemId);
		return problem.getEditorKind();
	}
	
	
	/**
	 * Get skeleton by name
	 * @param problemId
	 * @param name
	 * @return skeleton by name
	 * @throws MooshakException 
	 */
	public String getProgramSkeletonByFilename(Session session, String problemId, 
			String name) throws MooshakException {
		
		Contest contest = session.getContest();
		
		Problems problems = contest.open("problems");
		Problem problem = null;
		try {
			problem = problems.open(problemId);
		} catch (Exception e) {
			throw new MooshakException("Problem not found.");
		}
		
		Skeletons skeletons;
		try {
			skeletons = problem.open("skeletons");
		} catch (Exception e) {
			throw new MooshakException("No Skeletons");
		}
		
		Skeleton ske = skeletons.getSkeletonByName(name, false);
		if(ske != null)
			return ske.getSkeletonCode();
		return "";
	}
	
	/**
	 * If a solution made by user to the problem exists, it is returned
	 * @param session
	 * @param problemId
	 * @return solution made by user to the problem
	 * @throws MooshakException 
	 */
	public byte[] getSolution(Session session, String problemId) 
			throws MooshakException {
		Contest contest = session.getContest();
		Submissions submissions = contest.open("submissions");
		
		List<Submission> participantSubmissions = submissions.getSubmissionsByParticipant(
				session.getParticipant().getIdName());
		participantSubmissions.removeIf(new Predicate<Submission>() {

			@Override
			public boolean test(Submission submission) {
				return !submission.getProblemId().equals(problemId);
			}
		});
		
		if (participantSubmissions.isEmpty())
			throw new MooshakException("No solution provided by this user");
		
		participantSubmissions.sort(new Comparator<Submission>() {

			@Override
			public int compare(Submission o1, Submission o2) {
				return o1.getDate().compareTo(o2.getDate());
			}
			
		});
		
		List<Submission> acceptedSubmissions = new ArrayList<>(participantSubmissions);
		acceptedSubmissions.removeIf(new Predicate<Submission>() {

			@Override
			public boolean test(Submission submission) {
				
				return !submission.getClassify().equals(Classification.ACCEPTED);
			}
		});
		
		Submission submission = null;
		if (!acceptedSubmissions.isEmpty())
			submission = acceptedSubmissions.get(acceptedSubmissions.size()-1);
		else
			submission = participantSubmissions.get(participantSubmissions.size()-1);
		
		try {
			byte[] solution = Files.readAllBytes(submission.getAbsoluteFile()
					.resolve(submission.getProgramName()));
			return solution;
		} catch (IOException e) {
			throw new MooshakException("Cannot read solution!");
		}
	}
	
	/**
	 * Concatenates parameters to create a key to access cache
	 * @param params {@code String...} parameters to build key string
	 * @return a key to access cache
	 */
	public String getCacheKey(String... params) {
		return String.join(".", params);
	}
	
	/**
	 * Wraps a response in an object with a next polling interval
	 * @param resp
	 * @return a response wrapped in an object with a next polling interval
	 */
	public <T extends Object> PoolingResponseWrapper<T> wrapPollingResponse(T resp) {
		
		double systemLoadAvg = AdministratorManager.OS.getSystemLoadAverage();
		
		long interval = DEFAULT_POLLING_TIME;
		
		if (systemLoadAvg >= 4.0f) // if system load is too high, quadruplicate polling time
			interval *= 4;
		else if (systemLoadAvg >= 0.25f) // if system load is too high, adjust polling time
			interval += (long) (systemLoadAvg * (DEFAULT_POLLING_TIME / 2));
		
		return new PoolingResponseWrapper<T>(resp, interval);
	}
	
}
