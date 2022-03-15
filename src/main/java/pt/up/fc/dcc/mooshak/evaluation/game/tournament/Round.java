package pt.up.fc.dcc.mooshak.evaluation.game.tournament;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

/**
 * Representation of a round of a stage
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Round implements Jsonifiable {

	private Set<String> byes = new HashSet<>();
	private List<Match> matches = new ArrayList<>();
	private Set<String> players = new HashSet<>();

	public Round() {
	}

	/**
	 * @return the byes
	 */
	public Set<String> getByes() {
		return byes;
	}

	/**
	 * @return the matches
	 */
	public List<Match> getMatches() {
		return matches;
	}

	/**
	 * @return the players
	 */
	public Set<String> getPlayers() {
		return players;
	}


	/**
	 * Add a player or team who was allowed to advance to the next round without
	 * playing
	 * 
	 * @param player
	 */
	public void addBye(String playerId) {
		byes.add(playerId);
	}

	/**
	 * Add match to the round
	 * 
	 * @param match
	 */
	public void addMatch(Match match) {
		matches.add(match);
		players.addAll(match.getPlayers().keySet());
	}

	/**
	 * Check if player {@code playerId} was allowed to advance
	 * to the next round without playing
	 * 
	 * @param playerId
	 * @return <code>true</code> if {@code playerId} was allowed to advance,
	 *         <code>false</code> otherwise
	 */
	public boolean hasBye(String playerId) {
		return byes.contains(playerId);
	}

	/**
	 * Check if player {@value player} has played in this
	 * round
	 * 
	 * @param player
	 * @return <code>true</code> if {@value player} has played in this round,
	 *         <code>false</code> otherwise
	 */
	public boolean hasPlayed(String player) {
		return players.contains(player);
	}

	@Override
	public JsonValue toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		JsonArrayBuilder byesBuilder = Json.createArrayBuilder();
		for (String bye : byes)
			byesBuilder.add(bye);

		JsonArrayBuilder matchesBuilder = Json.createArrayBuilder();
		for (Match match : matches) 
			matchesBuilder.add(match.toJson());

		builder.add("matches", matchesBuilder);
		builder.add("byes", byesBuilder);

		return builder.build();
	}

}
