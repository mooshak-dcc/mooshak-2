package pt.up.fc.dcc.mooshak.evaluation.game.tournament;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Stage of a tournament
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public abstract class AbstractStage {
	
	protected int minNrOfPlayersPerMatch;
	protected int maxNrOfPlayersPerMatch;
	protected int nrOfPlayersPerMatch;
	protected int minNrOfPlayersPerGroup;
	protected int nrOfQualifiedPlayers;
	protected int maxNrOfRounds;
	
	protected boolean decreasing = true;

	public AbstractStage(
			int minNrOfPlayersPerMatch, int maxNrOfPlayersPerMatch,
			int nrOfPlayersPerMatch, int minNrOfPlayersPerGroup,
			int nrOfQualifiedPlayers, int maxNrOfRounds) {
		super();
		this.minNrOfPlayersPerMatch = minNrOfPlayersPerMatch;
		this.maxNrOfPlayersPerMatch = maxNrOfPlayersPerMatch;
		this.nrOfPlayersPerMatch = nrOfPlayersPerMatch;
		this.minNrOfPlayersPerGroup = minNrOfPlayersPerGroup;
		this.nrOfQualifiedPlayers = nrOfQualifiedPlayers;
		this.maxNrOfRounds = maxNrOfRounds;
	}

	/**
	 * @return the decreasing
	 */
	public boolean isDecreasing() {
		return decreasing;
	}

	/**
	 * @param decreasing the decreasing to set
	 */
	public void setDecreasing(boolean decreasing) {
		this.decreasing = decreasing;
	}

	/**
	 * Prepare stage to run
	 * 
	 * @param players
	 *            Players that are present in this stage
	 * @throws MooshakException
	 */
	public abstract void prepare(Map<String, ProcessBuilder> players)
			throws MooshakException;

	/**
	 * Method that actually runs the group stage
	 * 
	 * @throws MooshakException
	 */
	public abstract void run() throws MooshakException;

	/**
	 * Get players qualified to next stage
	 * 
	 * @return {@code List<String>} IDs of players qualified to next stage
	 * @throws MooshakException
	 */
	public abstract List<String> getQualifiedPlayers() 
			throws MooshakException;

}
