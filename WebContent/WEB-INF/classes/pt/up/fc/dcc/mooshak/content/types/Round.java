package pt.up.fc.dcc.mooshak.content.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * A {@link Round} is a set of matches that can happen at the same time, where
 * each player plays at most one of the matches and every player should play one
 * match (unless there is no possibility of arranging a match with the remaining
 * players).
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Round extends PersistentContainer<Match> {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning;
	
	@MooshakAttribute(
			name = "GroupNumber",
			type = AttributeType.INTEGER)
	private Integer groupNumber;

	@MooshakAttribute(
			name = "match", 
			type = AttributeType.CONTENT)
	private Void match;
	
	
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
	 * @return the groupNumber
	 */
	public Integer getGroupNumber() {
		return groupNumber;
	}

	/**
	 * @param groupNumber the groupNumber to set
	 */
	public void setGroupNumber(Integer groupNumber) {
		this.groupNumber = groupNumber;
	}

	/**
	 * Get {@link Match}es of this round excluding byes matches
	 * 
	 * @return array of {@link Match}es of this round excluding byes matches
	 * @throws MooshakException 
	 */
	public Match[] getMatches() throws MooshakException {
		
		return getChildren(true).stream().filter(po -> !((Match) po).isBye())
				.toArray(Match[]::new);
	}
	
	/**
	 * Get byes of this round
	 * 
	 * @return array of byes of this round
	 * @throws MooshakException 
	 */
	public Match[] getByes() throws MooshakException {
		
		return getChildren(true).stream().filter(po -> ((Match) po).isBye())
				.toArray(Match[]::new);
	}
	
	/**
	 * Get awarded scores by player to the final ranking stage
	 * 
	 * @return {@link Map} awarded scores by player to the final ranking stage
	 * @throws MooshakException 
	 */
	public Map<String, Double> getAwardedScoresByPlayer() throws MooshakException {
		
		// stage -> tournament
		Tournament tournament = getParent().getParent();
		
		Map<String, Double> awardedScoresByPlayer = new HashMap<>();
		
		Match[] matches = getMatches();
		for (Match match : matches) {
			
			
			//match.g
		}
		
		return awardedScoresByPlayer;
	}
	
	/**
	 * Get awarded scores by player to the final ranking stage
	 * 
	 * @param {@link List} matches list
	 * @return {@link Map} awarded scores by player to the final ranking stage
	 * @throws MooshakException 
	 */
	public Map<String, Double> getAwardedScoresByPlayerWDL(List<Match> matches,
			int pointsWin, int pointsDraw, int pointsLoss) 
					throws MooshakException {
		
		Map<String, Double> awardedScoresByPlayer = new HashMap<>();
		
		for (Match match : matches) {
			
			
			//match.
		}
		
		return awardedScoresByPlayer;
	}
}
