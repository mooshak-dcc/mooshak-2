package pt.up.fc.dcc.mooshak.evaluation;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.UserTestData;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.evaluation.game.AsuraEvaluator;
import pt.up.fc.dcc.mooshak.evaluation.game.MatchRun;
import pt.up.fc.dcc.mooshak.evaluation.game.wrappers.GameManagerWrapper;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.asura.NewAcceptedSubmissionEvent;

/**
 * An analyzer of SAs submissions. Game analysis is divided in 2 phases: static
 * analysis and dynamic analysis. Static analysis checks submission data,
 * collects parameters and performs compilation. Dynamic analysis executes the
 * AI against a set of other SAs.
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class GameAnalyzer extends ProgramAnalyzer {
	
	// IDs of the submissions
	private Set<String> submissionIds = new HashSet<>();

	public GameAnalyzer(Submission submission) {
		super(submission);
	}

	@Override
	protected void dynamicAnalysis(final EvaluationParameters parameters) throws MooshakEvaluationException {
		
		List<PersistentObject> ignoringPOs = new ArrayList<>();
		ignoringPOs.add(submission);
		try {
			ignoringPOs.add(submission.getParent());
			ignoringPOs.add(submission.getUserTestData());
		} catch (MooshakContentException e) {
			throw new MooshakEvaluationException(
					"Unexpected error while setting up opponent's SAs from user test data for testing the submission", e,
					Classification.REQUIRES_REEVALUATION);
		}

		// set initial classification and mark
		submission.setClassify(Classification.ACCEPTED);
		submission.setMark(0);

		// for validations we run a game against players passed in, submissions
		// don't
		collectGameSAsSubmissionIds(parameters);

		PersistentObject.executeIgnoringFSNotifications(() -> runGame(parameters), ignoringPOs);
	}

	/**
	 * Set up SAs for the execution of the game
	 * 
	 * @param parameters
	 *            {@link EvaluationParameters} parameters collected and used
	 *            throughout the evaluation
	 * @return {@code List<PersistentObject>} persistent objects added
	 * @throws MooshakEvaluationException
	 */
	private void collectGameSAsSubmissionIds(EvaluationParameters parameters) 
			throws MooshakEvaluationException {

		// add current submission
		submissionIds.add(submission.getIdName());

		// add opponent submissions
		if (submission.isConsider())
			collectGameOpponentSAsProblem(parameters);
		else
			collectGameOpponentSAsUserTestData(parameters);
	}

	/**
	 * Get IDs of submissions of solutions to problem
	 * 
	 * @param parameters
	 *            {@link EvaluationParameters} parameters collected and used
	 *            throughout the evaluation
	 * @param addedPOs
	 *            persistent objects added
	 * @throws MooshakEvaluationException - If an exception occurs while collecting opponent SAs from problem
	 */
	private void collectGameOpponentSAsProblem(EvaluationParameters parameters) 
			throws MooshakEvaluationException {

		try {
			Contest contest = parameters.getContest();
	
			Submissions submissions = contest.open("submissions");
	
			Problem problem = parameters.getProblem();
			
			List<String> solutionsNames = new ArrayList<>();
			try {
				for (Path solution : problem.getSolutions()) {
					String filename = Filenames.getSafeFileName(solution).replace('.', '_');
	
					if (submission.getTeamId().equals(filename))
						continue;
	
					solutionsNames.add(filename);
				}
			} catch (MooshakException e) {
				throw new MooshakException("Finding solutions for problem " + problem.getIdName() 
					+ ": " + e.getMessage());
			}
			
			for (String name : solutionsNames) {
	
				Submission submission = submissions
						.getMostRecentlyAcceptedOfParticipant(problem.getIdName(), name);
	
				if (submission == null)
					continue;
	
				submissionIds.add(submission.getIdName());
			}
		} catch (MooshakException e) {
			throw new MooshakEvaluationException(
					"Unexpected error while setting up opponent's SAs for testing the submission", e,
					Classification.REQUIRES_REEVALUATION);
		}
	}

	/**
	 * Build safe execution environment for user selected opponents
	 * 
	 * @param parameters
	 *            {@link EvaluationParameters} parameters collected and used
	 *            throughout the evaluation
	 * @throws MooshakException
	 */
	private void collectGameOpponentSAsUserTestData(EvaluationParameters parameters)
			throws MooshakEvaluationException {
		
		try {
			UserTestData userTestData = submission.getUserTestData();
			for (final Path input : userTestData.getInputFiles()) {
	
				String opponentSubmissionId;
				try {
					opponentSubmissionId = new String(Files.readAllBytes(input)).trim();
				} catch (IOException e) {
					throw new MooshakException("Reading input from file");
				}			
	
				submissionIds.add(opponentSubmissionId);
			}

		} catch (MooshakException e) {
			throw new MooshakEvaluationException(
					"Unexpected error while setting up opponent's SAs from user test data for testing the submission", e,
					Classification.REQUIRES_REEVALUATION);
		}
	}

	/**
	 * Run this submission as a game
	 * 
	 * @param parameters
	 *            {@link EvaluationParameters} parameters collected and used
	 *            throughout the evaluation
	 * @throws MooshakEvaluationException
	 */
	private void runGame(EvaluationParameters parameters) throws MooshakEvaluationException {

		try {

			// organize matches among loaded players and submitting player
			Map<Integer, Set<String>> matches = organizeMatches();

			// start running matches
			Map<Integer, MatchRun> matchResults = new HashMap<>();
			for (Integer matchId : matches.keySet()) {

				final AsuraEvaluator evaluator = new AsuraEvaluator(parameters.getContest(),
						parameters.getProblem());
				
				final Set<String> submissionIds = matches.get(matchId);
				submissionIds.remove(submission.getIdName());

				// prepare match runner
				evaluator.prepare(submission, submissionIds);
				
				// submit matches for running
				MatchRun matchRun = evaluator.execute();
				
				// add result
				matchResults.put(matchId, matchRun);
			}
			
			// process results
			summarizeMatchResults(matchResults);
			
			// save movies
			try {
				saveMatchResults(matchResults);
			} catch (MooshakContentException e) {
				LOGGER.severe("Error saving match results!");
			}

			// handle summary
			reporter.conclude();
			
			// notify all (if ACCEPTED)
			if (submission.getClassify().equals(Classification.ACCEPTED)
					&& submission.isConsider()) {
				NewAcceptedSubmissionEvent event = new NewAcceptedSubmissionEvent();
				event.setSubmissionId(submission.getIdName());
				event.setProblemId(submission.getProblemId());
				event.setTeamId(submission.getTeamId());
				EventSender.getEventSender().send(event);
			}

		} catch (Exception cause) {
			throw new MooshakEvaluationException("Unexpected error while running game", cause,
					Classification.REQUIRES_REEVALUATION);
		}
	}

	/**
	 * Organize matches with loaded players
	 * 
	 * @param managerWrapper
	 *            {@link GameManagerWrapper} game manager to get info about
	 *            match structure
	 * 
	 * @return {@link Map} set of players per match indexed by match ID
	 * @throws MooshakException
	 *             - If an error occurs while organizing matches
	 */
	private Map<Integer, Set<String>> organizeMatches() throws MooshakException {

		// get game manager 
		GameManagerWrapper gameManager = submission.getProblem().checkoutGameManager();

		// check if there are enough players to run the match
		if (submissionIds.size() < gameManager.getMinPlayersPerMatch()) {

			if (submissionIds.size() > 1 && !submission.isConsider())
				throw new MooshakEvaluationException("You have not selected enough players for a match!");
			else
				return new HashMap<>();
		}
		
		if (gameManager.getMaxPlayersPerMatch() == 1) {
			Map<Integer, Set<String>> matches = new HashMap<>();
			matches.put(1, new HashSet<>(Arrays.asList(submission.getIdName())));
			return matches;
		}

		// filter opponents
		List<String> opponents = submissionIds.stream()
				.filter(s -> !s.equals(submission.getIdName()))
				.collect(Collectors.toList());

		// organize matches
		int diffMaxMin = gameManager.getMaxPlayersPerMatch() - gameManager.getMinPlayersPerMatch();

		Map<Integer, Set<String>> matches = new HashMap<>();
		int matchCount = 0;
		while (!opponents.isEmpty() && opponents.size() >= gameManager.getMaxPlayersPerMatch() - 1) {

			Set<String> playersInMatch = new HashSet<>();
			for (int i = 0; i < gameManager.getMaxPlayersPerMatch() - 1; i++) {
				playersInMatch.add(opponents.remove(0));
			}
			playersInMatch.add(submission.getIdName());

			matches.put(++matchCount, playersInMatch);
		}

		if (!opponents.isEmpty() && diffMaxMin > 0 
				&& opponents.size() >= gameManager.getMinPlayersPerMatch() - 1) {
			
			Set<String> playersInMatch = new HashSet<>();
			for (int i = 0; i < gameManager.getMaxPlayersPerMatch() - 1 
					&& !opponents.isEmpty(); i++) {
				playersInMatch.add(opponents.remove(0));
			}
			playersInMatch.add(submission.getIdName());

			matches.put(++matchCount, playersInMatch);
		}
		
		// return game manager
		submission.getProblem().checkinGameManager(gameManager);

		return matches;
	}

	/**
	 * Summarizes match results to obtain a result to the submission. Also saves 
	 * 
	 * @param matchResults {@link Map} match results indexed by ID
	 * @throws MooshakException - if an error occurs while saving summary results
	 */
	private void summarizeMatchResults(Map<Integer, MatchRun> matchResults) 
			throws MooshakException {
		
		String submissionId = submission.getIdName();
		
		boolean hasMovie = false, hasObs = false;
		
		StringBuilder moviePlaylistBuilder = new StringBuilder()
				/*.append('[')*/;
		
		Classification globalClassification = Classification.ACCEPTED;
		int globalPoints = 0;
		StringBuilder observationsBuilder = new StringBuilder();
		for (Integer matchId : matchResults.keySet()) {
			
			MatchRun result = matchResults.get(matchId);

			// filter results of the current submission
			Classification classification = result.getClassification(submissionId);
			Integer points = result.getPoints(submissionId);
			String observation = result.getObservations(submissionId);
			
			if (classification != null && globalClassification.ordinal() < classification.ordinal())
				globalClassification = classification;
			else if (classification == null)
				globalClassification = Classification.REQUIRES_REEVALUATION;
			
			if (points != null)
				globalPoints += points;

			String playerIds = result.getPlayers().values().stream()
					.collect(Collectors.joining(", "));
			
			if (observation != null) {
				if (hasObs)
					observationsBuilder.append('\n');
				
				observationsBuilder
					.append("Observations of (")
					.append(playerIds)
					.append("): ")
					.append(observation);
				hasObs = true;
			}
			
			if (result.getMovie() != null && !result.getMovie().isEmpty()) {
				/*if (hasMovie)
					moviePlaylistBuilder.append(',');*/
				
				moviePlaylistBuilder.append(result.getMovie());
				hasMovie = true;
			}
			
			break;
		}
		
		submission.setClassify(globalClassification);
		submission.setMark(globalPoints);
		submission.setObservations(observationsBuilder.toString());
		submission.setFeedback(moviePlaylistBuilder/*.append(']')*/.toString(), true);
		
		submission.save();
	}

	/**
	 * Save match results.
	 * 
	 * @param matchResults {@link Map} match results indexed by ID
	 * @throws MooshakContentException - if an exception occurs while saving 
	 * 		match results
	 */
	private void saveMatchResults(Map<Integer, MatchRun> matchResults)
			throws MooshakContentException {
		
		Charset cs = PersistentCore.getCharset();
		
		for (Integer matchId : matchResults.keySet()) {
			
			String movie = matchResults.get(matchId).getMovie();
			if (movie == null)
				continue;
			
			String obtainedName = ".match" + matchId +  ".obtained";
			Path obtained = PersistentCore.getAbsoluteFile(
					submission.getPath().resolve(obtainedName));
			try(Writer writer = Files.newBufferedWriter(obtained, cs)) {
				writer.write(movie);
			} catch (IOException cause) {
				LOGGER.severe("Could not save match result. Error writing input file: " 
						+ obtainedName);
			}

			try {
				Files.setPosixFilePermissions(obtained, 
						PersistentCore.OWNER_READ_WRITE_PERMISSIONS);
			} catch (IOException cause) {
				LOGGER.severe("Could not set permissions on saved file: " 
						+ obtainedName);
			}
		}
	}
	
}
