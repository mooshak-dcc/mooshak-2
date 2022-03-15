package pt.up.fc.dcc.mooshak.evaluation.game;

import static pt.up.fc.dcc.mooshak.evaluation.SubmissionSecurity.Level.EXECUTION;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Opponent;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakEvaluationException;
import pt.up.fc.dcc.mooshak.evaluation.MooshakSafeExecutionException;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.evaluation.SubmissionSecurity;
import pt.up.fc.dcc.mooshak.evaluation.game.wrappers.GameManagerWrapper;
import pt.up.fc.dcc.mooshak.evaluation.game.wrappers.GamePlayerStatusWrapper;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Evaluator of Asura. Runs a match with a set of submissions 
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AsuraEvaluator {
	
	protected static final int 	AVAILABLE 
			= Runtime.getRuntime().availableProcessors();
	protected static final ExecutorService POOL
			= Executors.newFixedThreadPool(AVAILABLE);

	// counter for relaxations of submissions
	private static HashMap<String, Integer> submissionRelaxations = new HashMap<>();
		
	// evaluation parameters
	private EvaluationParameters parameters = new EvaluationParameters();
	
	// owners of the submissions
	private Map<String, String> submissionOwners = new HashMap<>();
	
	// submissions indexed by ID
	private Map<String, Submission> submissions = new HashMap<>();

	// safe execution environments for selected submissions indexed by their ID
	private Map<String, SafeExecution> safeExecutions = new HashMap<>();
	
	// securities that need to be relaxed to run the game
	private Map<String, SubmissionSecurity> securities = new HashMap<>();
	
	// POs to ignore changes during evaluation
	// private List<PersistentObject> ignoringPOs = new ArrayList<>();

	public AsuraEvaluator(Contest contest, Problem problem) {
		parameters.setContest(contest);
		parameters.setProblem(problem);
		parameters.setTimeout(problem.getTimeout());
	}

	/**
	 * Prepare the evaluator to run
	 * 
	 * @param {@link Submission}
	 * @param submissionsIds {@link Set} Set of opponent submission ids
	 * @throws MooshakEvaluationException - If any error occurs during the preparation
	 */
	public void prepare(Submission submission, Set<String> submissionsIds)
			throws MooshakEvaluationException {
		
		submissions.put(submission.getIdName(), submission);
		submissionOwners.put(submission.getIdName(), submission.getTeamId());
	
		SafeExecution safeExec;
		try {
			safeExec = createSafeExecutionForSubmission(parameters, submission);
		} catch (MooshakException e) {
			throw new MooshakEvaluationException(
					"Unexpected error while setting up SAs for running the match", e,
					Classification.REQUIRES_REEVALUATION);
		}
		safeExecutions.put(submission.getIdName(), safeExec);
		
		prepare(submissionsIds);
	}
	
	/**
	 * Prepare the evaluator to run
	 * 
	 * @param submissionsIds {@link Set} Set of submission ids
	 * @throws MooshakEvaluationException - If any error occurs during the preparation
	 */
	public void prepare(Set<String> submissionsIds)
			throws MooshakEvaluationException {
			
		try {
		
			Submissions submissionsPO = parameters.getContest().open("submissions");
		
			for (String submissionId : submissionsIds) {
				
				Submission submission = submissionsPO.open(submissionId);
	
				submissions.put(submissionId, submission);
				submissionOwners.put(submissionId, submission.getTeamId());
	
				SafeExecution safeExec = createSafeExecutionForSubmission(parameters, submission);
	
				safeExecutions.put(submission.getIdName(), safeExec);
			}
		} catch (MooshakException e) {
			throw new MooshakEvaluationException(
					"Unexpected error while setting up SAs for running the match", e,
					Classification.REQUIRES_REEVALUATION);
		}
	}
	
	/**
	 * Execute the evaluation
	 * 
	 * @return {@link MatchRun} result of the match
	 * @throws MooshakEvaluationException - If any error occurs during the preparation
	 */
	public MatchRun execute() throws MooshakEvaluationException {
	
		MatchRun result;
		try {
		
			Future<MatchRun> future = runMatchFuture(parameters);
			try {
				result = future.get(parameters.getTimeout(), TimeUnit.SECONDS);
			} catch (TimeoutException e) {
				result = endMatchRunWith(submissions.keySet(), Classification.TIME_LIMIT_EXCEEDED,
						"You or one of your opponents have taken too much time to play! "
						+ "The game has been aborted.");
			} finally {
				future.cancel(true);
			}			
		} catch (Exception cause) {
			throw new MooshakEvaluationException("Unexpected error while running game", cause,
					Classification.REQUIRES_REEVALUATION);
		}
			
		return result;
	}
	

	/**
	 * Create a {@link Future} task for running match
	 * 
	 * @param parameters {@link EvaluationParameters} parameters to run the evaluation
	 * @return {@link MatchRun} result of the match
	 * @throws MooshakEvaluationException - If any error occurs during the preparation
	 */
	private Future<MatchRun> runMatchFuture(EvaluationParameters parameters) {
		
		return POOL.submit(() -> {
			
			// initialize game manager to manage this match
			final GameManagerWrapper gameManager;
			try {
				gameManager = parameters.getProblem().checkoutGameManager();
			} catch (MooshakException e) {
				throw new MooshakEvaluationException("Cannot start game manager", e);
			}

			// relax securities of submissions for running matches
			try {
				relaxSecurities(submissions.keySet());
			} catch (MooshakSafeExecutionException e) {
				throw new MooshakEvaluationException("Could not relax securities", e);
			}

			// start processes for each player
			final Map<String, Process> processes = new HashMap<>();
			for (String playerId : submissions.keySet()) {

				// start process
				Process process;
				try {
					process = safeExecutions.get(playerId).startProcessWithoutIO();
				} catch (MooshakSafeExecutionException e) {
					throw new MooshakEvaluationException("Cannot start process for player " + playerId, e);
				}

				// add process
				processes.put(playerId, process);
			}

			// run match
			try {
				gameManager.manage(processes);
			} catch (MooshakException e) {

				throw new MooshakEvaluationException("Unexpected error while running match", e,
						Classification.REQUIRES_REEVALUATION);
			}

			// destroy players' processes
			for (String submissionId : submissions.keySet()) {

				try {
					safeExecutions.get(submissionId)
						.stopProcessWithoutIO(processes.get(submissionId));
				} catch (MooshakSafeExecutionException e) {
					throw new MooshakEvaluationException("Error destroying player processes", e,
							Classification.REQUIRES_REEVALUATION);
				}
			}

			// tighten securities of submissions after running matches
			try {
				tightenSecurities(submissions.keySet());
			} catch (MooshakSafeExecutionException e) {
				throw new MooshakEvaluationException("Could not tighten securities", e);
			}

			// collect match results
			MatchRun matchRun = collectMatchResults(gameManager);

			try {
				parameters.getProblem().checkinGameManager(gameManager);
			} catch (MooshakException e) {
				throw new MooshakEvaluationException("Cannot stop game manager", e);
			}

			return matchRun;
		});
	}
	
	/**
	 * Sets the a common classification and observations to every player of a match
	 * 
	 * @param submissionIds {@link Set} set of submission IDs
	 * @param classification {@link Classification} common classification
	 * @param observations {@link String} common observations
	 * @return {@link MatchRun} data collected during the execution of the match
	 */
	private MatchRun endMatchRunWith(Set<String> submissionIds, Classification classification,
			String observations) {

		MatchRun matchRun = new MatchRun();
		
		for (String submissionId : submissionIds) {
			matchRun.getPlayers().put(submissionId, submissionOwners.get(submissionId));
			matchRun.getClassifications().put(submissionId, classification);
			matchRun.getObservations().put(submissionId, observations);
		}
		
		return matchRun;
	}

	/**
	 * Collect match results into a {@link MatchRun} object 
	 * 
	 * @param gameManager {@link GameManagerWrapper} wrapped game manager
	 * @return {@link MatchRun} collected match results
	 * @throws MooshakEvaluationException if an error occurs while collecting match results
	 */
	private MatchRun collectMatchResults(GameManagerWrapper gameManager)
			throws MooshakEvaluationException {
		
		MatchRun matchRun = new MatchRun();
		
		try {
			for (String submissionId : submissions.keySet()) {
				
				matchRun.getPlayers().put(submissionId, submissionOwners.get(submissionId));
				
				GamePlayerStatusWrapper statusWrapper = gameManager
						.getGamePlayerStatus(submissionId);
	
				if (statusWrapper != null) {
					matchRun.getClassifications().put(submissionId, statusWrapper.getClassification());
					matchRun.getPoints().put(submissionId, statusWrapper.getPoints());
					matchRun.getObservations().put(submissionId, statusWrapper.getObservations() +
							preProcessedErrors(submissionId));
				}
			}
				
			matchRun.setMovie(gameManager.getGameMovieString("LZ77"));
		} catch (MooshakException e) {
			throw new MooshakEvaluationException("Could not collect match results.", e);
		}
		
		return matchRun;		
	}
	
	public String preProcessedErrors(String submissionId) {
		
		String errors = safeExecutions.get(submissionId).getErrors();
		if (errors == null || errors.trim().isEmpty())
			return "";
		
		int stackStartIndex = errors.indexOf("Stack:");
		
		if (stackStartIndex > 0)
			errors = errors.substring(0, stackStartIndex);
		
		return errors;
	}

	/**
	 * Create a safe execution environment for an SA of a submission
	 * 
	 * @param parameters
	 *            {@link EvaluationParameters} parameters collected and used
	 *            throughout the evaluation
	 * @param opponent
	 *            {@link Opponent} to which the safe execution is being
	 *            requested
	 * @return {@link SafeExecution} process builder for a submission
	 * @throws MooshakException
	 */
	private SafeExecution createSafeExecutionForSubmission(EvaluationParameters parameters,
			Submission submission) throws MooshakException {

		EvaluationParameters aiParameters = parameters.clone();

		aiParameters.setContest(submission.getContest());
		aiParameters.setSubmission(submission);

		try {
			aiParameters.setTeam(submission.getTeam());
		} catch (MooshakContentException e) {
			throw new MooshakException(e.getMessage());
		}

		aiParameters.setLanguage(submission.getLanguage());
		aiParameters.setMaxOutput(aiParameters.getLanguages().getMaxOutput());

		aiParameters.setProgram(submission.getProgram());

		aiParameters.setDirectory(submission.getPath());

		SubmissionSecurity security = new SubmissionSecurity(aiParameters, EXECUTION);
		securities.put(submission.getIdName(), security);

		return aiParameters.getLanguage().newSafeExecution(aiParameters);
	}

	/**
	 * Relax securities
	 * 
	 * @param submissionIds {@link Set} Set of submissions to relax
	 * @throws MooshakSafeExecutionException - if an exception occurs while relaxing submissions
	 */
	private void relaxSecurities(Set<String> submissionIds) 
			throws MooshakSafeExecutionException {

		for (String submissionId : submissionIds) {
			
			SubmissionSecurity security = securities.get(submissionId);
			
			synchronized (submissionRelaxations) {
				Integer relaxCounter = submissionRelaxations.get(submissionId);
				if (relaxCounter == null || relaxCounter <= 0) {
					security.relax(); 
					submissionRelaxations.put(submissionId, 1);
				} else
					submissionRelaxations.put(submissionId, relaxCounter + 1);
			}
		}
	}

	/**
	 * Tighten securities
	 * 
	 * @param submissionIds {@link Set} Set of submissions to tighten
	 * @throws MooshakSafeExecutionException - if an exception occurs while tightening submissions
	 */
	private void tightenSecurities(Set<String> submissionIds)
			throws MooshakSafeExecutionException {

		for (String submissionId : submissionIds) {
			
			SubmissionSecurity security = securities.get(submissionId);
			
			synchronized (submissionRelaxations) {
				Integer relaxCounter = submissionRelaxations.get(submissionId);
				if (relaxCounter == null || relaxCounter <= 1) {
					security.tighten();
					submissionRelaxations.remove(submissionId);
				} else
					submissionRelaxations.put(submissionId, relaxCounter - 1);
			}
		}
	}

}
