package pt.up.fc.dcc.mooshak.content.types;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pt.up.fc.dcc.asura.tournamentmanager.TournamentManager;
import pt.up.fc.dcc.asura.tournamentmanager.TournamentManagerImpl;
import pt.up.fc.dcc.asura.tournamentmanager.builders.MatchBuilder;
import pt.up.fc.dcc.asura.tournamentmanager.exceptions.TournamentManagerException;
import pt.up.fc.dcc.asura.tournamentmanager.models.Match;
import pt.up.fc.dcc.asura.tournamentmanager.models.Player;
import pt.up.fc.dcc.asura.tournamentmanager.models.ResultType;
import pt.up.fc.dcc.asura.tournamentmanager.models.StageFormat;
import pt.up.fc.dcc.asura.tournamentmanager.models.TieBreaker;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.evaluation.game.AsuraEvaluator;
import pt.up.fc.dcc.mooshak.evaluation.game.MatchRun;
import pt.up.fc.dcc.mooshak.evaluation.game.wrappers.GameManagerWrapper;
import pt.up.fc.dcc.mooshak.server.Configurator;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * A {@link Tournament} consists of a game-like tournament of a game-based
 * problem.
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Tournament extends PersistentContainer<pt.up.fc.dcc.mooshak.content.types.Match> {
	private static final String TOURNAMENT_FILE_NAME = "tournament.json";

	private static final long serialVersionUID = 1L;
	
	private static final String SEP = "<br/>";

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal = null;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning = null;

	@MooshakAttribute(name = "Title", 
			type = AttributeType.TEXT, 
			tip = "Title of this tournament")
	private String title = null;

	@MooshakAttribute(
			name = "Date",
			type = AttributeType.DATE,
			tip = "Date/time that the tournament was realized")
	private Date date;

	@MooshakAttribute(
			name = "Problem", 
			type = AttributeType.PATH, 
			refType = "Problem", 
			complement = "../../problems")
	private Path problem = null;
	
	@MooshakAttribute(name = "Submissions",
			type = AttributeType.LABEL)
	private String submissions = "";
	
	@MooshakAttribute(name = "OutputFile",
			type = AttributeType.FILE)
	private Path output = null;

	@MooshakAttribute(
			name = "stage", 
			type = AttributeType.CONTENT)
	private Void stage;
	
	/*-------------------------------------------------------------------*\
	 * 		            Setters and getters                              *
	\*-------------------------------------------------------------------*/
	

	/**
	 * Fatal errors messages of this folder
	 * 
	 * @return the fatal
	 */
	public String getFatal() {
		if (fatal == null)
			return "";
		else
			return fatal;
	}

	/**
	 * Set fatal error messages
	 * 
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
	 * Warning errors messages of this folder
	 * 
	 * @return the warning
	 */
	public String getWarning() {
		if (warning == null)
			return "";
		else
			return warning;
	}

	/**
	 * Set warning error messages
	 * 
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * Title of the tournament
	 * 
	 * @return the title
	 */
	public String getTitle() {
		if (title == null)
			return "";
		else
			return title;
	}

	/**
	 * Set the title of the tournament
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		if (date == null)
			return new Date(0);
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the problem that this {@link Tournament} refers to
	 * 
	 * @return problem
	 * @throws MooshakContentException
	 */
	public Problem getProblem() throws MooshakContentException {
		if(problem == null)
			return null;
		else 
			return openRelative("Problem", Problem.class);
	}
	
	/**
	 * Change the problem that this {@link Tournament} refers to
	 * 
	 * @param problem
	 */
	public void setProblem(Problem problem) {
		if(problem == null)
			this.problem = null;
		else
			this.problem = problem.getId();
	}
	
	/**
	 * Fetch the list of submissions that participated in this tournament
	 * 
	 * @return {@link Submission[]} that played this tournament
	 * @throws MooshakContentException 
	 */
	public Submission[] getSubmissions() throws MooshakContentException {
		
		if (submissions.isEmpty()) 
			return new Submission[0];
		
		String[] submissionIds = submissions.split(SEP);
		
		Submission[] submissions = new Submission[submissionIds.length];
		
		// tournament -> tournaments -> contest
		Contest contest = getGrandParent();
		
		Submissions submissionsPO = contest.open("submissions");
		
		int count = 0;
		for (String submissionId : submissionIds) {
			Submission submission = submissionsPO.find(submissionId);
			
			if (submission == null)
				continue;
			
			submissions[count++] = submission;
		}
		
		return submissions;
	}
	
	
	/**
	 * Set the submissions that will participate in the tournament
	 * 
	 * @param submissions that will participate in the tournament
	 */
	public void setSubmissions(Submission...submissions) {
		
		if (submissions.length == 0) {
			this.submissions = "";
			return;
		}
		
		if (!this.submissions.isEmpty()) 
			this.submissions += SEP;
		
		this.submissions += String.join(SEP, Arrays.stream(submissions)
				.map(Submission::getIdName)
				.collect(Collectors.toList())); 
	}
	
	/**
	 * Add a submission to the match
	 * 
	 * @param submission
	 */
	public void addSubmission(Submission submission) {
		if (!this.submissions.isEmpty()) 
			this.submissions += SEP;
		this.submissions += submission.getIdName();
	}
	
	/**
	 * Remove a submission of the match
	 * 
	 * @param submission {@link Submission} submission to remove
	 */
	public void removeSubmission(Submission submission) {
		submissions = submissions.replace(submission.getIdName(), "")
				.replace(SEP + SEP, SEP).trim();
	}

	/**
	 * Get the path to the JSON output file of the tournament
	 * 
	 * @return {@link Path} to the JSON output file of the tournament
	 */
	public Path getOutput() {
		if (output == null)
			return null;
		else
			return getPath().resolve(output);
	}

	/**
	 * Set the path to the JSON output file of the tournament
	 * 
	 * @param output {@link Path} to the JSON output file of the tournament
	 */
	public void setOutput(Path output) {
		if (output == null)
			this.output = null;
		else
			this.output = output.getFileName();
	}
	
	/*-------------------------------------------------------------------*\
	 * 		                     Operations                              *
	\*-------------------------------------------------------------------*/
	
	@MooshakOperation(
			name = "Setup Tournament Stages", 
			inputable = true, 
			show = false)
	private CommandOutcome setupTournamentStages(MethodContext context) {
		
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			
			if (context.getValue("nr_stages") == null)
				throw new MooshakException("The number of stages must be set!");
			
			int nrStages = Integer.parseInt(context.getValue("nr_stages"));
			if (nrStages < 0)
				throw new MooshakException("The number of stages is invalid!");
			
			List<String> submissionsIds = context.getValues("submission");
			if (submissionsIds == null || submissionsIds.size() < 2)
				throw new MooshakException("The number of players is invalid!");
			
			Submissions submissions = getParent().getParent().open("submissions");
			for (String submissionId : submissionsIds) 
				addSubmission(submissions.find(submissionId));

			Problem problem = getProblem();
			GameManagerWrapper gameManager = problem.checkoutGameManager();
			int minPlayersPerMatch = gameManager.getMinPlayersPerMatch();
			int maxPlayersPerMatch = gameManager.getMaxPlayersPerMatch();
			problem.checkinGameManager(gameManager);
			
			for (int i = 1; i <= nrStages; i++) {
				
				String stageId = "stage_" + i;
				
				Stage stagePO = create(stageId, Stage.class);
				
				String type = context.getValue(stageId + "_type");
				if (type == null)
					throw new MooshakException("The type of stage must be set!");
				
				stagePO.setFormat(StageFormat.valueOf(type));

				if (context.getValue(stageId + "_nr_players_match") == null)
					throw new MooshakException("The number of players per match must be set!");
				
				int nrPlayersPerMatch = Integer.parseInt(
						context.getValue(stageId + "_nr_players_match"));
				
				if (nrPlayersPerMatch < minPlayersPerMatch && nrPlayersPerMatch > maxPlayersPerMatch)
					throw new MooshakException("The number of players per match is invalid!");
				
				stagePO.setNrOfPlayersPerMatch(nrPlayersPerMatch);

				if (context.getValue(stageId + "_min_players_group") != null)
					stagePO.setMinNrOfPlayersPerGroup(Integer.parseInt(
							context.getValue(stageId + "_min_players_group")));
				 
				if (context.getValue(stageId + "_max_rounds") != null)
					stagePO.setMaxNrOfRounds(
							Integer.parseInt(context.getValue(stageId + "_max_rounds")));
				
				if (context.getValue(stageId + "_nr_qualified") != null)
					stagePO.setNrOfQualifiedPlayers(
							Integer.parseInt(context.getValue(stageId + "_nr_qualified")));
			
				stagePO.setResultType(ResultType.valueOf(
						context.getValue(stageId + "_match_result_type")));

				stagePO.setMatchTieBreakers(context.getValues(stageId + "_match_tiebreaker"));
				stagePO.setRankingTieBreakers(context.getValues(stageId + "_ranking_tiebreaker"));
				
				List<String> rankingPointsStr = context.getValues(stageId + "_ranking_points");
				if (rankingPointsStr != null)
					stagePO.setRankingPoints(rankingPointsStr.stream()
							.map(Double::parseDouble)
							.collect(Collectors.toList()));
				
				stagePO.save();
			}
			
			save();
			
			run();

			outcome.setMessage("Running tournament ...");
		} catch (Exception e) {
			outcome.setMessage("Error: " + e.getMessage());
		}

		return outcome;
	}
	
	@MooshakOperation(
			name = "Run Tournament", 
			inputable = false, 
			show = true)
	private CommandOutcome runTournament() {
		
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			
			run();

			outcome.setMessage("Running tournament ...");
		} catch (Exception e) {
			outcome.setMessage("Error: " + e.getMessage());
		}
        
		return outcome;
	}
	
	@MooshakOperation(
			name = "Export Tournament JSON",
			inputable = false,
			show = true)
	private CommandOutcome exportTournamentJson() {
		
		CommandOutcome outcome = new CommandOutcome();
		
		Path filePath = getAbsoluteFile(getOutput());
		
		try {
			outcome.addPair("mimeType", Configurator.getMime(
					Filenames.extension(filePath.getFileName().toString())));
			outcome.addPair("filename", filePath.getFileName().toString());
			outcome.addPair("file", Base64Coder.encodeLines(PersistentCore
					.getAbsoluteFileContentGuessingCharset(filePath).getBytes()));
		} catch (Exception cause) {
			outcome.setMessage("Cannot download tournament JSON file:" + cause
					.getLocalizedMessage());
			return outcome;
		}
		
		outcome.setContinuation("Tournament JSON Download");
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Tournament JSON Download",
			inputable=true,
			show=false)
	private CommandOutcome downloadTournamentJson(MethodContext context) {	
		return new CommandOutcome();
	}
	
	@MooshakOperation(
			name = "Select Tournament Participants", 
			inputable = false, 
			show = true)
	private CommandOutcome selectTournamentParticipants() {

		CommandOutcome outcome = new CommandOutcome();
		
		try {

			Submissions submissions = getGrandParent().open("submissions");
			Map<String, Submission> playersSubmissions = submissions.
					getMostRecentlyAcceptedOfEachParticipant(
							problem.getFileName().toString());
			
			for (String playerId : playersSubmissions.keySet()) {
				outcome.addPair("player_id", playerId);
				outcome.addPair("submission_" + playerId, 
						playersSubmissions.get(playerId).getIdName());
			}
			
			outcome.setContinuation("Select Tournament Participants Step 2");
		} catch (Exception e) {
			outcome.setMessage("Error: " + e.getMessage());
		}
        
		return outcome;
	}
	
	@MooshakOperation(
			name = "Select Tournament Participants Step 2", 
			inputable = true, 
			show = false)
	private CommandOutcome selectTournamentParticipantsStep2(MethodContext context) {

		CommandOutcome outcome = new CommandOutcome();
		
		setSubmissions();
		
		try {
		
			List<String> submissionsIds = context.getValues("submission");
			for (String submissionId : submissionsIds) {
				
				Submissions submissions = getGrandParent().open("submissions");
				Submission submission = submissions.open(submissionId);
				
				addSubmission(submission);
			}
		} catch (Exception e) {
			outcome.setMessage("Error: " + e.getMessage());
		}
		
		outcome.setMessage("Selected submissions changed!");
        
		return outcome;
	}

	/**
	 * Run tournament
	 * 
	 * @throws TournamentManagerException - if an error occurs while running the tournament
	 * @throws MooshakException 
	 */
	private void run() throws TournamentManagerException, MooshakException {
		
		TournamentManager tournamentManager = new TournamentManagerImpl(getIdName());
		tournamentManager.setTitle(getTitle());
		
		Problem problem = getProblem();
		if (problem == null)
			throw new MooshakException("There is no problem associated to this tournament.");
		tournamentManager.setGameMetadata(problem.getIdName(), problem.getName());
		
		Submission[] submissions = getSubmissions();
		for (Submission submission : submissions) {
			String teamName = null;
			
			Authenticable team = submission.getTeam();
			if (team == null)
				teamName = "Unknown Player";
			else if (team.getName() == null)
				teamName = team.getName();
			else
				teamName = team.getIdName();
			
			tournamentManager.addPlayer(submission.getIdName(), teamName);
		}
		
		List<Stage> stages = getChildren(true);
		for (Stage stage : stages) {
			tournamentManager.addStage(stage.getFormat());

			if (stage.getNrOfPlayersPerMatch() != null)
				tournamentManager.setNrOfPlayersPerMatch(stage.getNrOfPlayersPerMatch());
			if (stage.getMinNrOfPlayersPerGroup() != null)
				tournamentManager.setMinNrOfPlayersPerGroup(stage.getMinNrOfPlayersPerGroup());
			if (stage.getMaxNrOfRounds() != null)
				tournamentManager.setMaxNrOfRounds(stage.getMaxNrOfRounds());
			if (stage.getNrOfQualifiedPlayers() != null)
				tournamentManager.setNrOfQualifiedPlayers(stage.getNrOfQualifiedPlayers());
			
			tournamentManager.setMatchResultType(stage.getMatchResultType(),
					stage.getMatchTieBreakers().stream()
						.map(tb -> tb.startsWith("!") ? new TieBreaker(tb.substring(1), true) : new TieBreaker(tb))
						.toArray(TieBreaker[]::new));
			tournamentManager.setRankingPoints(
					stage.getRankingPoints().stream().mapToDouble(x -> x).toArray());
			tournamentManager.setRankingTiebreakers(stage.getRankingTieBreakers().stream()
					.map(tb -> tb.startsWith("!") ? new TieBreaker(tb.substring(1), true) : new TieBreaker(tb))
					.toArray(TieBreaker[]::new));
			
			tournamentManager.startStage();
			
			while (tournamentManager.hasMatches()) {
			
				MatchBuilder matchBuilder = tournamentManager.nextMatch();
				
				AsuraEvaluator evaluator = new AsuraEvaluator(getGrandParent(), getProblem());
				evaluator.prepare(matchBuilder.getPlayers()
						.stream()
						.map(p -> p.getId()).collect(Collectors.toSet()));
				
				MatchRun matchRun = evaluator.execute();
				for (Player player: matchBuilder.getPlayers()) {
                    matchBuilder.setPoints(player, matchRun.getPoints(player.getId()));
                }
				
				Match match = matchBuilder.build();
				
				// save match movie
				stage.addMatch(match.getId(), matchRun.getMovie());

				tournamentManager.endMatch(match);
			}
			
			tournamentManager.endStage();
		}
		
		tournamentManager.conclude();
		
		setDate(new Date());
		
		// write to file
		writeTournamentJson(tournamentManager.getJson());
	}
	
	/**
	 * Write tournament JSON output to a file
	 * 
	 * @param tournamentJson {@link String} tournament JSON output
	 * @throws MooshakException 
	 */
	public void writeTournamentJson(String tournamentJson) 
			throws MooshakException {
		
		File file = getAbsoluteFile(TOURNAMENT_FILE_NAME).toFile();
		
		try (
				BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			
			bw.append(tournamentJson.toString());
			bw.flush();
		} catch (IOException e) {
			throw new MooshakException("Writing JSON file.");
		}
		
		setOutput(file.toPath());
		save();
	}
	
	/**
	 * Read tournament JSON from file
	 * 
	 * @return tournament JSON
	 * @throws MooshakException - if an error occurs while reading JSON
	 */
	public String getTournamentJson() 
			throws MooshakException {
		
		File file = getAbsoluteFile()
				.resolve(getOutput().getFileName()).toFile();
	
		String tournamentJson = "";
		try (
				BufferedReader br = new BufferedReader(new FileReader(file))) {
			
			String line;
			while ((line = br.readLine()) != null) {
				tournamentJson += line;
			}
		} catch (IOException e) {
			throw new MooshakException("Writing JSON file.");
		}
		
		return tournamentJson;
	}

	/**
	 * Get the player id within tournament for a given participant
	 * 
	 * @param participantId {@link String} ID of the participant
	 * @return {@link String} the player id within tournament for a given participant
	 * @throws MooshakException
	 */
	public String getPlayerId(String participantId) throws MooshakException {
		
		Submission[] submissions = getSubmissions();
		
		for (Submission submission : submissions) {
			if (submission.getTeamId().equals(participantId))
				return submission.getIdName();
		}

		return null;
	}
}
