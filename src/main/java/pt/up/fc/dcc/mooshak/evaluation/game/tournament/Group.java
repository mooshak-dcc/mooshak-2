package pt.up.fc.dcc.mooshak.evaluation.game.tournament;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonValue;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Representation of a group
 * 
 * @author josepaiva
 */
public class Group implements Jsonifiable {

	private Map<String, ProcessBuilder> players = new HashMap<>();
	private Map<String, Integer> playersPoints = new HashMap<>();

	private boolean changed = true;
	private List<String> ranks = null;
	private boolean descendant = true;

	public Group() {
	}

	/**
	 * @return the players
	 */
	public Map<String, ProcessBuilder> getPlayers() {
		return players;
	}

	/**
	 * Adds a player to the group with 0 points
	 * 
	 * @param playerId
	 *            Id of the player to add
	 * @param processBuilder
	 *            process builder of the player
	 */
	public void addPlayer(String playerId, ProcessBuilder processBuilder) {
		players.put(playerId, processBuilder);
		playersPoints.put(playerId, 0);
		changed = true;
	}
	
	/**
	 * Add a match of players of this group
	 * @param match
	 */
	public void addMatch(Match match) {
		
		for (String playerId : match.getPlayers().keySet()) {
			if (players.containsKey(playerId)) {
				if (playersPoints.containsKey(playerId))
					playersPoints.put(playerId, playersPoints.get(playerId) + match.getPoints(playerId));
				else
					playersPoints.put(playerId, match.getPoints(playerId));
			}
		}
		changed = true;
	}

	/**
	 * Set points of a player
	 * 
	 * @param playerId
	 *            Id of the player to set points
	 * @param points
	 *            points to set points
	 * @throws MooshakException
	 *             If the player is not found
	 */
	public void setPoints(String playerId, int points) throws MooshakException {
		if (players.get(playerId) == null)
			throw new MooshakException("Player not found");
		playersPoints.put(playerId, points);
		changed = true;
	}
	
	/**
	 * Get points of a player
	 * 
	 * @param playerId
	 *            Id of the player to set points
	 * @return points of the player
	 * @throws MooshakException
	 *             If the player is not found
	 */
	public int getPoints(String playerId) throws MooshakException {
		if (players.get(playerId) == null)
			throw new MooshakException("Player not found");
		return playersPoints.get(playerId);
	}

	/**
	 * Get ranking of the group
	 * 
	 * @return {@code List<String>} ranking of the group
	 */
	public List<String> getRanks(boolean descendant) {
		if (!changed && this.descendant == descendant)
			return ranks;
		List<String> ranks = new ArrayList<>(players.keySet());
		Collections.sort(ranks, new Comparator<String>() {

			@Override
			public int compare(String p1, String p2) {

				Integer pointsP1 = playersPoints.get(p1);
				Integer pointsP2 = playersPoints.get(p2);
				
				if (pointsP1 == null)
					return descendant ? -1 : 1;
				
				if (pointsP2 == null)
					return descendant ? 1 : -1;

				return descendant ? pointsP2.compareTo(pointsP1) :
					pointsP1.compareTo(pointsP2);
			}
		});

		this.ranks = ranks;
		this.descendant = descendant;
		changed = false;

		return ranks;
	}

	@Override
	public JsonValue toJson() {
		JsonArrayBuilder builder = Json.createArrayBuilder();
		
		List<String> ranking = getRanks(descendant);
		
		int pos = 1;
		for (String player : ranking) {
			JsonObjectBuilder playerBuilder = Json.createObjectBuilder();
			playerBuilder.add("id", player);
			playerBuilder.add("rank", pos++);
			playerBuilder.add("points", playersPoints.get(player));
			builder.add(playerBuilder);
		}

		return builder.build();
	}
}
