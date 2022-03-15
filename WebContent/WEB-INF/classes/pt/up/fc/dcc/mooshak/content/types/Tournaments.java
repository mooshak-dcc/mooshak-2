package pt.up.fc.dcc.mooshak.content.types;

import java.util.Date;
import java.util.Map;

import pt.up.fc.dcc.asura.tournamentmanager.models.ResultType;
import pt.up.fc.dcc.asura.tournamentmanager.models.StageFormat;
import pt.up.fc.dcc.asura.tournamentmanager.models.StageConfigurationField;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.Operations;
import pt.up.fc.dcc.mooshak.content.Operations.Operation;
import pt.up.fc.dcc.mooshak.evaluation.game.wrappers.GameManagerWrapper;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Container of {@link Tournament}s
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Tournaments extends PersistentContainer<Tournament> implements Preparable {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal = null;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning = null;

	@MooshakAttribute(name = "Tournament", type = AttributeType.CONTENT)
	private Void tournament;

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
	
	/*-------------------------------------------------------------------*\
	 * 		                     Operations                              *
	\*-------------------------------------------------------------------*/
	
	@MooshakOperation(name = "Initialize Tournament", 
			inputable = false, 
			tip = "Initialize a Tournament")
	private CommandOutcome initTournament() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Problems problems = getParent().open("problems");
			for (PersistentObject problemPO : problems.getChildren(true)) {
				Problem problem = (Problem) problemPO;
				if (problem.isGame()) {
					outcome.addPair("game_id", problem.getIdName());
					outcome.addPair("game_name", problem.getName() == null ? problem.getIdName() : problem.getName());
				}
			}
			
			outcome.setContinuation("Create Tournament");
		} catch (Exception e) {
			outcome.setMessage("Error: " + e.getMessage());
		}
        
		return outcome;
	}
	
	@MooshakOperation(name = "Create Tournament", 
			inputable = true, 
			show = false)
	private CommandOutcome createTournament(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			String title = context.getValue("tournament_title");
			String gameId = context.getValue("game_id");
			
			Submissions submissions = getParent().open("submissions");
			Map<String, Submission> playersSubmissions = submissions
					.getMostRecentlyAcceptedOfEachParticipant(gameId);
			
			for (String playerId : playersSubmissions.keySet()) {
				outcome.addPair("player_id", playerId);
				outcome.addPair("submission_" + playerId, playersSubmissions.get(playerId).getIdName());
			}
			
			Problems problems = getParent().open("problems");
			Problem problem = problems.find(gameId);
			
			GameManagerWrapper gameManager = problem.checkoutGameManager();
			
			Tournament tournamentPO = create(generateTournamentId(gameId), Tournament.class);
			tournamentPO.setTitle(title);
			tournamentPO.setProblem(problem);
			tournamentPO.save();

			outcome.addPair("tournament_id", tournamentPO.getIdName());
			outcome.addPair("min_players_match", Integer.toString(gameManager.getMinPlayersPerMatch()));	
			outcome.addPair("max_players_match", Integer.toString(gameManager.getMaxPlayersPerMatch()));
			
			// options for lists
			for (ResultType resultType: ResultType.values()) {
				outcome.addPair("match_type", resultType.name());
				outcome.addPair(resultType.name() + "_dsc", resultType.toPrettyString());
				outcome.addPair(resultType.name() + "_nr_ranking_points", 
						String.valueOf(resultType.getNrOfRankingBoxes()));
			}

			for (StageFormat stageFormat: StageFormat.values()) {
				outcome.addPair("stage_format", stageFormat.name());
				outcome.addPair(stageFormat.name() + "_dsc", stageFormat.toPrettyString());
				
				for (StageConfigurationField field: stageFormat.getConfigFields()) {
					outcome.addPair(stageFormat.name() + "_fields", field.name());
				}
			}
			
			problem.checkinGameManager(gameManager);
			
			outcome.setContinuation("Set Up Tournament");
		} catch (Exception e) {
			outcome.setMessage("Error: " + e.getMessage());
		}
        
		return outcome;
	}
	
	@MooshakOperation(name = "Set Up Tournament", 
			inputable = true, 
			show = false)
	private CommandOutcome setUpTournament(MethodContext context) {
		
		String tournamentId = context.getValue("tournament_id");
		
		CommandOutcome outcome = new CommandOutcome();
		try {
			Tournament tournamentPO = open(tournamentId);
			Operation operation = Operations.getOperation(Tournament.class, "Setup Tournament Stages");
			return operation.execute(tournamentPO, context);
		} catch (MooshakException e) {
			outcome.setMessage("Error: " + e.getMessage());
		}
        
		return outcome;
	}
	
	/*-------------------------------------------------------------------*\
	 * 		                       Helpers                               *
	\*-------------------------------------------------------------------*/

	@Override
	public void prepare() throws MooshakException {
		try (POStream<Tournament> stream = newPOStream()) {
			for (Tournament tournament : stream)
				tournament.delete();
		} catch (Exception cause) {
			throw new MooshakContentException("Error iterating over tournaments", cause);
		}
	}
	
	/**
	 * Generate ID for a new {@link Tournament}
	 * 
	 * @param gameId {@link String} ID of the game
	 * @return {@link String} ID for a new {@link Tournament}
	 */
	private String generateTournamentId(String gameId) {
		
		StringBuilder sb = new StringBuilder();
		sb.append("tournament_").append(gameId).append("_").append(new Date().getTime());
		
		return sb.toString();
	}
}
