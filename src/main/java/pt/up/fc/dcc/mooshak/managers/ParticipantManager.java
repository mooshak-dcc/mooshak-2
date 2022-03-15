package pt.up.fc.dcc.mooshak.managers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.HasListingRows;
import pt.up.fc.dcc.mooshak.content.types.HasTransactions;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.Printout;
import pt.up.fc.dcc.mooshak.content.types.Printouts;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Question;
import pt.up.fc.dcc.mooshak.content.types.Questions;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Skeleton;
import pt.up.fc.dcc.mooshak.content.types.Skeletons;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.content.types.Test;
import pt.up.fc.dcc.mooshak.content.types.Tests;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationQueue;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationQueue.EvaluationRequest;
import pt.up.fc.dcc.mooshak.evaluation.StandardEvaluationQueue;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;

public class ParticipantManager extends Manager {
	
	private static ParticipantManager manager = null;

	private EvaluationQueue evaluationQueue = null;
	private Contests contests = null;
	
	/**
	 * Get a single instance of this class
	 * 
	 * @param master
	 * @return
	 */
	public static ParticipantManager getInstance() {

		if (manager == null)
			manager = new ParticipantManager();
		return manager;
	}

	private ParticipantManager() {
		try {
			evaluationQueue = StandardEvaluationQueue.getInstance();
		} catch (MooshakException cause) {
			LOGGER.log(Level.SEVERE, "Error getting evaluation queue", cause);
		}
		try {
			contests = PersistentObject.openPath("data/contests");
		} catch (MooshakContentException cause) {
			LOGGER.log(Level.SEVERE, "Opening contests", cause);
		}
	}

	/**
	 * Evaluate a program
	 * 
	 * @param contest
	 * @param teamId
	 * @param programName
	 * @param programCode
	 * @param problemId
	 * @param inputs
	 * @throws MooshakException
	 */
	public Submission evaluate(String contestId, String teamId, String sessionId,
			String programName, byte[] programCode, String problemId, 
			List<String> inputs, boolean consider) throws MooshakException {

		if (contestId == null)
			error("Missing contest id");
		
		Contest contest = contests.open(contestId);
		Submission submission = null;
		
		if (teamId == null)
			error("Missing team id");
		else if(! contest.isRunning(teamId))
			error("Contest not running");
		else if (problemId == null)
			error("Missing problem id");
		else if (programCode == null) {
			error("Missing program code");
		} else {
				
			submission = createSubmission(
					sessionId, contestId, teamId, problemId,
					programName, programCode,
					inputs, consider, null
			);
			
			try {
				
				LOGGER.log(Level.INFO, "evaluate:\t" + contest + "\t" + teamId + "\t"
						+ problemId + "\t" + programName);

				evaluationQueue.enqueueEvaluationRequest(new EvaluationRequest(submission));
			} catch (MooshakException cause) {
				LOGGER.log(Level.SEVERE, "Sending evaluation request", cause);
			}
		}
		
		return submission;
	}
	
	public Submission createSubmission(
			String sessionId, String contestId, String teamId, String problemId,
			String programName, byte[] programCode,
			List<String> inputs, boolean consider, String entityTag
	) throws MooshakException {
		
		Contest contest = contests.open(contestId);
		Submissions submissions = contest.open(
				consider ? "submissions" : "validations");
		
		if (consider) {
			submissions.acceptable(teamId, problemId, programCode);
		}
		
		Submission submission = null;
		if (submissions.isReplaceExisting()) {
			
			String id = contest.getTransactionId(teamId, problemId, false);
			submission = submissions.find(id);
			
			if (submission == null)
				submission = submissions.create(id, Submission.class);
				
		} else {
			
			String id;
			if ((id = entityTag) == null)
				id = contest.getTransactionId(teamId, problemId);

			submission = submissions.create(id, Submission.class);
		}
			
		try {
			submission.receive(teamId, sessionId, programName, programCode,
					problemId, inputs, consider);
		} catch (MooshakException e) {
			if (submission != null)
				submission.setObservations(e.getMessage());
			throw e;
		}
		
		return submission;
	}

	/**
	 * Get the report of an evaluation
	 * @param session
	 * @param submissionId
	 * @param consider
	 * @return
	 * @throws MooshakException
	 */
	public EvaluationSummary getEvaluationSummary(Session session,
			String submissionId, boolean consider) throws MooshakException {

		Submission submission = getSubmission(session,
				submissionId,consider);
		EvaluationSummary report = new EvaluationSummary();

		report.setState(submission.getState().toString());
		
		String status;
		if (!consider && submission.getClassify().equals(Classification.ACCEPTED)) {
			status = "VALIDATED";
		} else {
			status = submission.getClassify().toString();
			
			/*if (submission.getContest().getGui().equals(Gui.ENKI))
				EnkiManager.getInstance().processSubmission(session, submission);*/
		}
		
		report.setStatus(status);
		report.setObservations(submission.getObservations()); 
		report.setLanguage(submission.getLanguage() != null ? submission.getLanguage().getName() : null);
		report.setEvaluatedAt(submission.getEvaluatedAt());
		
		StringBuilder feedback = new StringBuilder().append(submission.getFeedback());
		if (submission.getReviewerFeedback() != null && !submission.getReviewerFeedback().trim().isEmpty())
			feedback.append("\n\n").append("Reviewer Feedback: ").append(submission.getReviewerFeedback());
		report.setFeedback(feedback.toString());
		
		report.setMark(submission.getMark());
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
	
	
	/**
	 * Get submissions for current session
	 * 
	 * @param session
	 * @return
	 * @throws MooshakException
	 */
	public Submissions getSubmissions(Session session) throws MooshakException {
		Submissions submissions = null;

		try {
			Contest contest = session.getContest();

			submissions = contest.open("submissions");

		} catch (MooshakContentException cause) {
			error("Error getting submissions folder", cause);
		}
		return submissions;
	}

	/**
	 * Get submission given id, in current session
	 * 
	 * @param session
	 * @param submissionId
	 * @param consider 
	 * @return
	 * @throws MooshakException
	 */
	public Submission getSubmission(Session session, 
			String submissionId, boolean consider)
					throws MooshakException {
		Submission submission = null;
		String folderName = consider ? "submissions" : "validations";

		try {
			Contest contest = session.getContest();

			Submissions submissions = contest.open(folderName);			
			
			submission = submissions.open(submissionId);

		} catch (MooshakContentException cause) {
			error("Error getting submission", cause);
		}
		return submission;
	}

	/**
	 * Get all available problems as a map indexed by if and having the name (A,
	 * B, C, ...) as value.
	 * 
	 * @param session
	 * @return
	 * @throws MooshakException
	 */
	public Map<String, String> getProblems(Session session)
			throws MooshakException {
		Map<String, String> problemMap = new HashMap<>();

		try {
			Contest contest = session.getContest();

			Problems problems = contest.open("problems");

			for (Problem problem : problems.getChildren(Problem.class, true)) {
				if(problem.getName() == null)
					problemMap.put(problem.getIdName(), problem.getIdName());
				else
					problemMap.put(problem.getIdName(), problem.getName()
							.toString());
			}

		} catch (MooshakContentException cause) {
			error("Error getting problem list", cause);
		}
		return problemMap;
	}

	/**
	 * Get problem given id, in current context
	 * 
	 * @param session
	 * @param problemId
	 * @return
	 * @throws MooshakException
	 */
	public Problem getProblem(Session session, String problemId)
			throws MooshakException {
		Problem problem = null;

		try {
			Contest contest = session.getContest();

			Problems problems = contest.open("problems");
			problem = problems.open(problemId);

		} catch (MooshakContentException cause) {
			error("Error getting problem", cause);
		}
		return problem;
	}

	/**
	 * Get team given id, in current context
	 * 
	 * @param session
	 * @param teamId
	 * @return
	 * @throws MooshakException
	 */
	public Team getTeam(Session session, String teamId) throws MooshakException {
		Team team = null;

		try {
			Contest contest = session.getContest();

			Groups groups = contest.open("groups");
			team = groups.find(teamId);

		} catch (MooshakContentException cause) {
			error("Error getting team", cause);
		}
		return team;
	}

	/**
	 * Return a list of column names for the given kind of listing
	 * 
	 * @param kind
	 * @return
	 * @throws MooshakException
	 */
	public List<ColumnInfo> getColumns(Session session, String kind)
			throws MooshakException {
		List<ColumnInfo> columns = null;
		PersistentObject persistent;
		HasListingRows hasListingRows = null;

		kind = kind.toLowerCase();

		try {
			Contest contest = session.getContest();
			switch (kind) {
			case "submissions":
			case "questions":
			case "balloons":
			case "printouts":
				persistent = contest.open(kind);
				if (persistent instanceof HasListingRows)
					hasListingRows = (HasListingRows) persistent;
				break;
			case "pending":
				// TODO: verify this
				persistent = contest.open("submissions");
				if (persistent instanceof HasListingRows)
					hasListingRows = (HasListingRows) persistent;
				break;
			case "rankings":
				hasListingRows = contest.getRankingPolicy();
				break;
			default:
				throw new MooshakException("Invalid kind of listing"+kind);
			}

		} catch (MooshakException cause) {
			throw new MooshakException(cause.getMessage(), cause);
		}

		if (hasListingRows != null)
			columns = hasListingRows.getColumns();

		if (columns == null)
			throw new MooshakException("No column names found for " + kind);

		return columns;
	}

	
	/**
	 * Submit a question
	 * 
	 * @param session
	 * @param team
	 * @param problem
	 * @param question
	 * @throws MooshakException
	 */
	public String askQuestion(Session session, String team, String problemId,
			String subject, String questionText) throws MooshakException {

		Contest contest = session.getContest();
		Questions questions = contest.open("questions");

		String id = contest.getTransactionId(team, problemId);

		Question question = questions.create(id, Question.class);

		question.setDate(new Date());
		question.setTime(contest.transactionTime(team));

		question.setTeam(getTeam(session, team));

		Problem problem = getProblem(session, problemId);
		question.setProblem(problem);

		question.setState(Question.State.UNANSWERED);
		question.setSubject(subject);
		question.setQuestion(questionText);
		question.save();

		RowManager.getInstance().broadcast(question);
		
		return question.getIdName();
	}


	/**
	 * Submits a printout
	 * 
	 * @param session
	 * @param problem
	 * @param content
	 * @param fileName
	 * @throws MooshakException
	 */
	public String submitPrintoutAndPrint(Session session, String problem, String content,
			String fileName) throws MooshakException {
		Contest contest = session.getContest();
		Printouts printouts = contest.open("printouts");
		Authenticable participant = session.getParticipant();
		
		String id = contest.getTransactionId(participant.getProfile().getIdName(), problem);

		Printout printout = printouts.create(id, Printout.class);

		printout.setTeam(getTeam(session, session.getParticipant()
				.getIdName()));

		Date now = new Date();

		printout.setDate(now);
		printout.setTime(contest.transactionTime(participant.getName()));

		printout.setProblem(getProblem(session, problem));
		
		printout.setProgram(fileName, content);

		printout.setDelay(new Date(now.getTime() - new Date().getTime()));

		PersistentObject persistent = PersistentObject.openPath(printout
				.getPath().toString());
		Path full = persistent.getAbsoluteFile(fileName);
		try {
			persistent.executeIgnoringFSNotifications(() -> Files.write(full,
					content.getBytes()));
		} catch (IOException e) {
			Path fullFileName = full.getFileName();
			throw new MooshakException(e.getMessage() + 
					(fullFileName == null ? "??" : fullFileName.toString()) + 
					printout.getId().toString());
		}

		printout.save();
		
		RowManager.getInstance().broadcast(printout);

		printout.sendToPrinter();
		
		return printout.getIdName();
	}

	/**
	 * Returns the quota of transactions of the given type of the user
	 * @param session
	 * @param type
	 * @return
	 * @throws MooshakException
	 */
	public TransactionQuota getTransactionsData(Session session, String type)
			 throws MooshakException {
		Contest contest = session.getContest();

		Team team;
		try {
			team = getTeam(session, session.getParticipant().getIdName());
		} catch (MooshakException e) {
			return new TransactionQuota();
		}
		
		if(Files.exists(contest.getAbsoluteFile(type))) {
			HasTransactions hasTransactions = contest.open(type);
		
			return hasTransactions.getTransactionQuota(team);
		} else 
			return new TransactionQuota();
	}

	/**
	 * Makes a transaction of the given type
	 * @param session
	 * @param type
	 * @throws MooshakException if transactions reached the limit
	 */
	public void makeTransaction(Session session, String type)
			 throws MooshakException {
		Contest contest = session.getContest();
		
		Team team;
		try {
			team = getTeam(session, session.getParticipant().getIdName());
		} catch (MooshakException e) {
			return ;
		}
		
		HasTransactions hasTransactions = contest.open(type);
		
		hasTransactions.makeTransaction(team);
		
	}

	/**
	 * Get program skeleton for problemId
	 * @param session
	 * @param problemId
	 * @param extension
	 * @return
	 * @throws MooshakException 
	 */
	public String getProgramSkeleton(Session session, String problemId,
			String extension) throws MooshakException {
		
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
			throw new MooshakException("No skeletons found.");
		}
		
		Skeleton ske = skeletons.getSkeleton(extension, false);
		if(ske != null)
			return ske.getSkeletonCode();
		return "";
	}

	public MooshakValue getSubmissionContent(Session session, String id) 
			throws MooshakException {
		Contest contest = session.getContest();
		
		Submissions submissions = contest.open("submissions");
		
		Submission submission = submissions.open(id);
		
		if (!session.getParticipant().getIdName().equals(submission.getTeamId()))
			throw new MooshakException("Not Allowed");
		
		MooshakValue value = null;
		try {
			String submissionPath = submission.getAbsoluteFile().toString();
			String programName = submission.getProgramName();
			value = new MooshakValue("submission", 
					programName,
					Files.readAllBytes(Paths.get(submissionPath, programName)));
		} catch (IOException e) {
			throw new MooshakException("Getting submission");
		}
		
		return value;
	}

	public boolean getShowOwnCode(Session session) throws MooshakException {
		Contest contest = session.getContest();
		
		Submissions submissions = contest.open("submissions");
		
		return submissions.isShowOwnCode();
	}
	
	/**
	 * Get the languages available in a contest
	 * @param session
	 * @return
	 * @throws MooshakException
	 */
	public Map<String, String> getAvailableLanguages(Session session)
			 throws MooshakException {
		Contest contest = session.getContest();
		
		Languages languages = contest.open("languages");
		
		Map<String, String> availableLanguages = new HashMap<>();
		
		List<Language> langs = languages.getChildren(true);
		for (Language language : langs) {
			availableLanguages.put(language.getExtension(), language.getName());
		}
		
		return availableLanguages;
	}

	/**
	 * Get the problem name given its id
	 */
	public String getProblemNameById(Session session, String problemId)
			 throws MooshakException {
		Contest contest = session.getContest();
		
		Problems problems = contest.open("problems");
		
		Problem problem = problems.find(problemId);	
		
		if (problem == null)
			throw new MooshakException("Problem " + problemId + " not found!");
		
		return problem.getName();
	}
	
	/**
	 * 
	 * @param session
	 * @param teamId
	 * @param problemId
	 * @param evalTime
	 * @return
	 * @throws MooshakException 
	 */
	public String getSubmissionContentsWithoutId(Session session, String teamId,
			String problemId, long evalTime, String programName) throws MooshakException {

		Contest contest = session.getContest();
		
		Submissions submissions = contest.open("submissions");
		
		List<Submission> submissionList = new ArrayList<>();
		try(POStream<Submission> stream = submissions.newPOStream()) {
			for (Submission submission : stream) {
				
				if (!submission.isConsider())
					continue;
				
				if (submission.getDate() != null && submission.getDate().getTime() < evalTime)
					continue;
				
				if (submission.getProblemId() != null && 
						submission.getProblemId().equals(problemId) && 
						submission.getTeamId() != null && 
						submission.getTeamId().equals(teamId))
					submissionList.add(submission);

			}
		} catch (Exception cause) {
			throw new MooshakContentException("Error iterating over submissions"
					,cause);
		}
		
		Submission submission = null;
		long minDiff = Long.MAX_VALUE;
		for (Submission s : submissionList) {
			if (s.getDate() != null) {
				
				long diff = s.getDate().getTime() - evalTime;
				
				if (diff < minDiff) 
					submission = s;
			}
		}
		
		if (submission == null)
			return "";
		Path programPath = submission.getAbsoluteFile(programName);
		
		try {
			List<String> lines = Files.readAllLines(programPath);
			
			return String.join("\n", lines);
		} catch (IOException e) {
			throw new MooshakException("Could not read file");
		}
	}

	/**
	 * Check if files uploaded by participants must be editable
	 * 
	 * @param session
	 * @param extension Extension of the file
	 * @return {@link Boolean} <code>false</code> only if explicitly stated; 
	 * 		<code>true</code> otherwise 
	 * @throws MooshakException 
	 */
	public boolean isEditableContents(Session session, String extension) throws MooshakException {
		
		Contest contest = session.getContest();
		
		Languages languages = contest.open("languages");
		Language language = languages.findLanguageWithExtension(extension);
		
		if (language == null)
			throw new MooshakException("Language for extension " + extension + " not available.");
		
		return language.isEditableContents();
	}
	
	/**
	 * Return a list of possible opponents, including those provided by the 
	 * game author, and previous submissions 
	 * 
	 * @param contest {@link Contest} Contest to which required problem belongs
	 * @param problemId {@link String} ID of the required problem 
	 * @return {@link Map} Map of submissions indexed by student ID
	 * @throws MooshakException
	 */
	public Map<String, String> getOpponents(Contest contest, String problemId) 
			throws MooshakException {
		
		if(contest == null)
			throw new MooshakException("Contest not specified");
		
		Problems problems = contest.open("problems");
		Problem problem = problems.open(problemId);
		
		if(problem.getEditorKind() != EditorKind.GAME)
			throw new MooshakException("Problem " + problemId + " is not a game.");
		
		// collect accepted submissions
		Submissions submissions = contest.open("submissions");
		Map<String, Submission> acceptedSubmissions = submissions
				.getMostRecentlyAcceptedOfEachParticipant(problemId);
		
		return acceptedSubmissions.keySet().stream().collect(Collectors.toMap(
				Function.identity(),
				k -> acceptedSubmissions.get(k).getIdName().toString()));
	}
	


	
	/**
	 * Gets the public test cases.
	 * 
	 * @param session {@link Session} The current session.
	 * @param problemId {@link String} ID of the problem.
	 * @return the public test cases.
	 */
	public Map<String, String> getPublicTestCases(Session session,
			String problemId) throws MooshakException {
		Map<String, String> result = new HashMap<String, String>();
		
		Contest contest = session.getContest();
		
		Problems problems = contest.open("problems");
		Problem problem = null;
		try {
			problem = problems.open(problemId);
		} catch (Exception e) {
			throw new MooshakException("Problem not found.");
		}
		
		Tests tests = problem.open("tests");

		for (PersistentObject po : tests.getChildren(true)) {
			Test test = (Test) po;
			
			if (test.getInput() == null || test.getOutput() == null)
				continue;
			
			if (test.isShow()) {
				String input = null;
				try {
					Path filenamePath = test.getInput().getFileName();
					if (filenamePath != null)
						input = new String(Files.readAllBytes(test
								.getAbsoluteFile(filenamePath.toString())));
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
					continue;
				}
				
				String output = null;
				try {
					Path filenamePath = test.getOutput().getFileName();
					if (filenamePath != null)
						output = new String(Files.readAllBytes(test
								.getAbsoluteFile(filenamePath.toString())));
				} catch (IOException e) {
					LOGGER.severe(e.getMessage());
					continue;
				}
			
				result.put(input, output);
			}
		}
		
		return result;
	}

}
