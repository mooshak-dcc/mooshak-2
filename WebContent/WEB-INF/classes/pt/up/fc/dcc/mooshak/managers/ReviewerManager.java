package pt.up.fc.dcc.mooshak.managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Balloon;
import pt.up.fc.dcc.mooshak.content.types.Balloon.State;
import pt.up.fc.dcc.mooshak.content.types.Balloons;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.content.types.Printout;
import pt.up.fc.dcc.mooshak.content.types.Printouts;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Question;
import pt.up.fc.dcc.mooshak.content.types.Questions;
import pt.up.fc.dcc.mooshak.content.types.Report;
import pt.up.fc.dcc.mooshak.content.types.Reports;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.evaluation.StandardEvaluationQueue;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationQueue.EvaluationRequest;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Test;
import pt.up.fc.dcc.mooshak.content.types.Tests;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.events.AlertNotificationEvent;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

/**
 * Manages requests from judges and teachers. This class ignores types from any
 * particular communication layer, such as GWT RPC or Jersey
 * 
 * @author José paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class ReviewerManager extends Manager {

	private static ReviewerManager manager = null;

	/**
	 * Get single instance of this class for testing purposes
	 * 
	 * @return
	 */
	public static ReviewerManager getInstance() {
		if (manager == null)
			manager = new ReviewerManager();
		return manager;
	}

	private ReviewerManager() {

	}

	/**
	 * Change the contest of this session
	 * 
	 * @param session
	 * @param contest
	 * @throws MooshakException
	 */
	public void changeContest(Session session, String contestName)
			throws MooshakException {
		
		if (!AuthManager.getInstance().canChangeContest(contestName, session.getParticipant(), 
				session.getProfile().getIdName()))
			throw new MooshakException("Not allowed to enter this contest");

		Contest contest = null;

		try {
			Contests contests = session.getContest().getParent();
			contest = contests.find(contestName);
			session.setContest(contest);
			session.save();
		} catch (MooshakContentException cause) {
			error("Error getting submission", cause);
		}
	}

	/**
	 * Get an XML formated submission report of given submission
	 * 
	 * @param session
	 * @param submissionId
	 * @return String with XML
	 * @throws MooshakException
	 */
	public Map<String, String> getSubmissionReports(Session session, String submissionId)
			throws MooshakException {
		Submission submission = null;

		try {
			Contest contest = session.getContest();

			Submissions submissions = contest.open("submissions");
			submission = submissions.open(submissionId);

		} catch (MooshakContentException cause) {
			error("Error getting submission", cause);
		}

		Reports reports = submission.open("reports");
		
		Map<String, String> submissionReports = new HashMap<>();
		
		Path reportFileName = null;
		
		if (submission.getReportPath() != null &&
				submission.getReportPath().getNameCount() > 0) {
			reportFileName = submission.getReportPath().getFileName();
		}
		
		for (Report report : reports.getContent()) {
			try {
				String content = getFileContentsAsString(report.getPath().toString(), 
						Submission.REPORT_FILE_NAME.toString());
				
				submissionReports.put(report.getIdName(), content);
			} catch (Exception e) {
				LOGGER.log(Level.SEVERE, "Reading report");
			}
		}
		
		if (reportFileName != null &&
				Files.exists(PersistentObject.getAbsoluteFile(Paths.get(submission.getPath().toString(), 
				reportFileName.toString())))) {

			String content = getFileContentsAsString(submission.getPath().toString(), 
					reportFileName.toString());
			
			submissionReports.put("1", content);
		}
		
		return submissionReports;
	}

	/**
	 * @param submission
	 * @param report
	 * @return
	 * @throws MooshakException
	 */
	private String getFileContentsAsString(String path, String name) throws MooshakException {
		Path abs = PersistentObject.getAbsoluteFile(Paths.get(path, 
				name));

		StringBuilder content = new StringBuilder();
		try {
			for (String line : Files.readAllLines(abs)) {
				content.append(line);
				content.append('\n');
			}
		} catch (IOException cause) {
			throw new MooshakException("Error reading XML report", cause);
		}
		return content.toString();
	}

	public void commentProblem(Session session, String problemId,
			String subject, String comment) throws MooshakException {

		Contest contest = session.getContest();
		Questions questions = contest.open("questions");

		String id;
		if (problemId == null)
			id = contest.getTransactionId(session.getParticipant().getIdName(), "All");
		else
			id = contest.getTransactionId(session.getParticipant().getIdName(), problemId);

		Question question = questions.create(id, Question.class);
		question.setTeam(session.getParticipant());
		question.setDate(new Date());
		question.setTime(new Date(new Date().getTime() - 
				contest.getStart().getTime()));

		if (problemId == null)
			question.setProblem(null);
		else {
			Problem problem = ParticipantManager.getInstance().getProblem(
					session, problemId);
			question.setProblem(problem);
		}

		question.setState(Question.State.ANSWERED);
		question.setSubject(subject);
		question.setAnswer(comment);
		question.save();

		RowManager.getInstance().broadcast(question);
	}

	public void createBalloon(Session session, String objectId)
			throws MooshakContentException {

		Contest contest = session.getContest();
		Balloons balloons = contest.open("balloons");

		Submissions submissions = contest.open("submissions");
		Submission submission = submissions.find(objectId);
		String teamId = submission.getTeam().getIdName();
		String problemId = submission.getProblem().getIdName();
		
		List<Balloon> existingBalloons = balloons.getChildren(true);
		List<String> balloonsIds = existingBalloons.stream().map(new Function<Balloon, String>() {

			@Override
			public String apply(Balloon t) {
				return t.getIdName();
			}
		}).collect(Collectors.toList());
	
		if (!getMatchingIndexes(balloonsIds, ".*\\_" + problemId + "\\_" + teamId).isEmpty())
			return;

		String id = contest.getTransactionId(teamId, problemId);

		Balloon balloon = balloons.create(id, Balloon.class);
		balloon.setDate(new Date());
		balloon.setTime(contest.transactionTime(teamId));

		balloon.setState(State.UNDELIVERED);
		balloon.setProblem(submission.getProblem());
		balloon.setSubmission(submission);
		balloon.setTeam(submission.getTeam());

		balloon.save();

		RowManager.getInstance().broadcast(balloon);
	}

	public void sendAlertNotification(String contest, String recipientObject,
			String message) throws MooshakException {
		Recipient recipient = null;
		if (recipientObject != null) {
			MooshakObject object = AdministratorManager.getInstance()
					.getMooshakObject(recipientObject);
			recipient = new Recipient(object.getFieldValue("Team").getSimple());
		}

		AlertNotificationEvent event = new AlertNotificationEvent();
		event.setMessage(message);
		event.setRecipient(recipient);
		EventSender.getEventSender().send(contest, event);
	}

	public Map<String, String> getQuestionsSubjects(Session session,
			String problemId) throws MooshakException {

		Contest contest = session.getContest();
		Questions questions = contest.open("questions");

		Map<String, String> subjectMap = new HashMap<>();
		if (problemId == null)
			return subjectMap;

		for (Question question : questions.getChildren(Question.class, false)) {
			if (question.getProblem() == null)
				continue;
			if (question.getProblem().getId().toString().equals(problemId)) {
				if (question.getSubject() == null)
					subjectMap.put(question.getIdName(), question.getIdName());
				else
					subjectMap.put(question.getIdName(), question.getSubject());
			}

		}
		return subjectMap;
	}

	/**
	 * @param submissionId
	 * @param session
	 * @return
	 * @throws MooshakException
	 */
	public byte[] getSubmissionProgram(String submissionId, Session session)
			throws MooshakException {

		Path file;
		Submission submission;
		boolean consider = true;// must be a regular submission, not a
								// validation
		try {

			submission = ParticipantManager.getInstance().getSubmission(
					session, submissionId, consider);
			file = PersistentObject.getAbsoluteFile(submission.getProgram());
		} catch (MooshakContentException cause) {
			throw new MooshakException(cause.getMessage(), cause);
		}

		byte[] code;
		try {
			code = Files.readAllBytes(file);
		} catch (IOException cause) {
			throw new MooshakException(cause.getMessage(), cause);
		}
		return code;
	}

	/**
	 * @param programName
	 * @param problemId
	 * @param submissionId
	 * @param session
	 * @throws MooshakException
	 */
	public void reevaluate(Session session, String programName,
			String problemId, String submissionId) throws MooshakException {
		Submission submission;
		boolean consider = true;// must be a regular submission, not a
								// validation

		try {
			submission = ParticipantManager.getInstance().getSubmission(
					session, submissionId, consider);
		} catch (MooshakContentException cause) {
			throw new MooshakException(cause.getMessage(), cause);
		}

		submission.cleanup();
		submission.analyze();
		RowManager.getInstance().broadcast(submission);
	}

	/**
	 * @param session
	 * @param type
	 * @param id
	 * @throws MooshakException
	 */
	public void broadcastRowChange(Session session, String type, String id)
			throws MooshakException {
		Contest contest = session.getContest();

		switch (type.toLowerCase()) {
		case "submissions":
			Submissions submissions = contest.open("submissions");
			Submission submission = submissions.find(id);
			RowManager.getInstance().broadcast(submission);
			break;
		case "questions":
			Questions questions = contest.open("questions");
			Question question = questions.find(id);
			RowManager.getInstance().broadcast(question);
			break;
		case "balloons":
			Balloons balloons = contest.open("balloons");
			Balloon balloon = balloons.find(id);
			RowManager.getInstance().broadcast(balloon);
			break;
		case "printouts":
			Printouts printouts = contest.open("printouts");
			Printout printout = printouts.find(id);
			RowManager.getInstance().broadcast(printout);
			break;
		}
	}

	/**
	 * @param session
	 * @param questionId
	 * @return
	 * @throws MooshakException
	 */
	public List<SelectableOption> getQuestionsSubjectList(Session session,
			String questionId) throws MooshakException {
		List<SelectableOption> subjects = new ArrayList<>();
		MooshakObject question = AdministratorManager.getInstance()
				.getMooshakObject(questionId);

		Map<String, String> subjectsMap = getQuestionsSubjects(session,
				question.getFieldValue("Problem").getSimple());

		for (String key : subjectsMap.keySet())
			subjects.add(new SelectableOption(key, subjectsMap.get(key)));
		return subjects;
	}

	/**
	 * Get the problem id associated with a submission
	 * @param session
	 * @param submissionId
	 * @return
	 * @throws MooshakException 
	 */
	public String getProblemIdAssociatedToSubmission(Session session, String submissionId) 
			throws MooshakException {
		Contest contest = session.getContest();
		Submissions submissions = contest.open("submissions");
		Submission submission = submissions.find(submissionId);
		return submission.getProblemId();
	}

	/**
	 * Update the result of a submission
	 * @param submissionId
	 * @param oldResult
	 * @param newResult
	 * @throws MooshakException 
	 */
	public void updateSubmissionResult(Session session, String submissionId, String oldResult, 
			String newResult) throws MooshakException {

		Contest contest = session.getContest();
		Submissions submissions = contest.open("submissions");
		Submission submission = submissions.find(submissionId);
		
		contest.getRankingPolicy().removeSubmission(submission);
		submission.setClassify(Classification.valueOf(newResult));
		
		if (newResult.equalsIgnoreCase("Accepted")) {
		
			Problem problem = submission.getProblem();
			Tests tests = problem.open("tests");
			
			List<Test> testList = tests.getContent();
			int points = 0;
			for (Test test : testList) {
				points += test.getPoints();
			}
			
			submission.setMark(points);
		} else
			submission.setMark(0);
		
		submission.save();
		
		contest.getRankingPolicy().addSubmission(submission);
	}

	/**
	 * Is printouts list-pending?
	 * @param session
	 * @return
	 */
	public boolean isPrintoutsListPending(Session session) throws MooshakException {
		
		Contest contest = session.getContest();
		Printouts printouts = contest.open("printouts");
		
		return printouts.isListPending();
	}

	/**
	 * Is balloons list-pending?
	 * @param session
	 * @return
	 */
	public boolean isBalloonsListPending(Session session) throws MooshakException {
		
		Contest contest = session.getContest();
		Balloons balloons = contest.open("balloons");
		
		return balloons.isListPending();
	}

	/**
	 * Get the obtained output of a submission
	 * @param session
	 * @param submissionId
	 * @return the list of obtained outputs of a submission as a map
	 */
	public List<String> getObtainedOutputs(Session session, 
			String submissionId) throws MooshakException {
		
		List<String> result = new ArrayList<>();
		
		Contest contest = session.getContest();

		Submissions submissions = contest.open("submissions");
		Submission submission = submissions.open(submissionId);
		
		Problems problems = contest.open("problems");
		Problem problem = problems.open(submission.getProblemId());
		Tests tests = problem.open("tests");
		
		List<PersistentObject> testsPO = tests.getChildren(true);
		Collections.sort(testsPO, new Comparator<PersistentObject>() {

			@Override
			public int compare(PersistentObject o1, PersistentObject o2) {
				
				return o1.getIdName().compareTo(o2.getIdName());
			}
		});
		for (PersistentObject testPO : testsPO) {
			
			String testName = testPO.getIdName();
			String obtainedName = "."+testName+".obtained";
			Path obtained = submission.getAbsoluteFile().resolve(obtainedName);
			
			try {
				List<String> lines = Files.readAllLines(obtained);
				result.add(String.join("\n", lines));
			} catch (IOException e) {
				continue;
			}
		}
		
		return result;
	}

	/**
	 * Get the expected output of a submission
	 * @param session
	 * @param submissionId
	 * @return the list of expected outputs of a submission as a map
	 */
	public List<String> getExpectedOutputs(Session session, String submissionId)
			throws MooshakException {
		
		List<String> result = new ArrayList<>();
		
		Contest contest = session.getContest();

		Submissions submissions = contest.open("submissions");
		Submission submission = submissions.open(submissionId);
		
		Problems problems = contest.open("problems");
		Problem problem = problems.open(submission.getProblemId());
		Tests tests = problem.open("tests");
		
		List<PersistentObject> testsPO = tests.getChildren(true);
		Collections.sort(testsPO, new Comparator<PersistentObject>() {

			@Override
			public int compare(PersistentObject o1, PersistentObject o2) {
				
				return o1.getIdName().compareTo(o2.getIdName());
			}
		});
		for (PersistentObject testPO : testsPO) {
			
			Path expected = testPO.getAbsoluteFile().resolve(((Test) testPO)
					.getOutput().getFileName());
			
			try {
				List<String> lines = Files.readAllLines(expected);
				result.add(String.join("\n", lines));
			} catch (IOException e) {
				continue;
			}
		}
		
		return result;
	}
	
	/**
	 * Finds the index of all entries in the list that matches the regex
	 * @param list The list of strings to check
	 * @param regex The regular expression to use
	 * @return list containing the indexes of all matching entries
	 */
	public List<Integer> getMatchingIndexes(List<String> list, String regex) {
		ListIterator<String> li = list.listIterator();
		
		List<Integer> indexes = new ArrayList<Integer>();
		
		while(li.hasNext()) {
			int i = li.nextIndex();
			String next = li.next();
			if(Pattern.matches(regex, next)) {
				indexes.add(i);
			}
		}
		
		return indexes;
	}

	/**
	 * Gets the problem test cases
	 * @param session
	 * @param problemId
	 * @return problem test cases
	 */
	public Map<String, String> getProblemTestCases(Session session,
			String problemId) throws MooshakException {
		Map<String, String> result = new HashMap<String, String>();
		
		Contest contest = session.getContest();
		
		Problems problems = contest.open("problems");
		Problem problem = problems.open(problemId);
		
		Tests tests = problem.open("tests");

		for (PersistentObject po : tests.getChildren(true)) {
			Test test = (Test) po;
			
			if (test.getInput() == null || test.getOutput() == null)
				continue;
			
			String input = null;
			try {
				Path filenamePath = test.getInput().getFileName();
				if (filenamePath != null)
					input = new String(Files.readAllBytes(test
							.getAbsoluteFile(filenamePath.toString())));
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
				continue;
			}
			
			String output = null;
			try {
				Path filenamePath = test.getOutput().getFileName();
				if (filenamePath != null)
					output = new String(Files.readAllBytes(test
							.getAbsoluteFile(filenamePath.toString())));
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
				continue;
			}
			
			result.put(input, output);
		}
		
		return result;
	}

	/**
	 * Validate submission.
	 * 
	 * @param submissionId
	 * @param programCode
	 * @param problemId
	 * @param inputs 
	 * @throws MooshakException 
	 */
	public void validateSubmission(Session session, String submissionId, byte[] programCode, 
			String problemId, List<String> inputs) throws MooshakException {
		Contest contest = session.getContest();

		Submissions submissions = contest.open("submissions");
		Submission submission = submissions.find(submissionId);

		if (problemId == null)
			error("Missing problem id");
		else if (programCode == null) {
			error("Missing program code");
		} else {

			LOGGER.log(Level.INFO, "evaluate:\t" + contest + "\tjudge\t"
					+ problemId + "\t" + submission.getProgramName());

			try {
				StandardEvaluationQueue.getInstance()
					.enqueueEvaluationRequest(
							new EvaluationRequest(submission)
					);
			} catch (MooshakException cause) {
				LOGGER.log(Level.SEVERE, "Enqueuing evaluation request", cause);
			}
		}
	}

	/**
	 * Get the report of the validation of the submission
	 * @param session
	 * @param submissionId
	 * @return
	 * @throws MooshakException
	 */
	public EvaluationSummary getValidationSummary(Session session,
			String submissionId) throws MooshakException {
		Contest contest = session.getContest();
		
		Submissions submissions = contest.open("validations");
		Submission submission = submissions.find(submissionId);
		EvaluationSummary report = new EvaluationSummary();

		report.setState(submission.getState().toString());
		String status = submission.getClassify().equals(Classification.ACCEPTED) ? "VALIDATED" :
			submission.getClassify().toString();

		report.setLanguage(submission.getLanguage().getName());
		report.setEvaluatedAt(submission.getEvaluatedAt());
		report.setStatus(status);
		report.setObservations(submission.getObservations()); 
		report.setFeedback(null);

		report.setOutputs(submission.getUserOutputs());
		report.setUserExecutionTimes(submission.getUserExecutionTimes());
		
		// collect metrics
		report.setExecutionTime(submission.getElapsed());
		report.setProgramSize(submission.getSize());
		report.setCpuUsage(submission.getCpu());
		report.setMemoryUsage(submission.getMemory());
		report.setLinesOfCode(submission.getLines());
		
		return report;
	}
}