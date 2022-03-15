package pt.up.fc.dcc.mooshak.evaluation.game.tournament;

import java.io.IOException;
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

/**
 * Single match of a tournament
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class Match implements Jsonifiable {

	private Map<String, ProcessBuilder> players = null;
	private Map<String, Integer> playersPoints = null;
	
	private List<String> ranks = null;
	private boolean changed = true;
	private boolean descendant = true;

	public Match(Map<String, ProcessBuilder> players) {
		this.players = players;
		this.playersPoints = new HashMap<>();
	}

	/**
	 * @return the players
	 */
	public Map<String, ProcessBuilder> getPlayers() {
		return players;
	}

	/**
	 * Run the match of the tournament
	 * @throws IOException 
	 */
	public void run() throws IOException {

		Map<String, Process> playersProcesses = new HashMap<>();
		for (String playerId : players.keySet()) {
			if (players.get(playerId) != null)
				playersProcesses.put(playerId, players.get(playerId).start());
			else
				playersProcesses.put(playerId, null);
		}
		
		// TODO Run the game & delete test code
		
		
		
		/* START of test code */
		List<String> matchRank = new ArrayList<>(playersProcesses.keySet());
		Collections.shuffle(matchRank);
		
		int points = 0;
		for (String playerId : matchRank) {
			playersPoints.put(playerId, points++);
		}
		/* END of test code */
		
		changed = true;
	}

	/**
	 * Get points of player in the match
	 * 
	 * @param playerId
	 * @return points of player in the match
	 */
	public int getPoints(String playerId) {
		return playersPoints.get(playerId);
	}
	
	/**
	 * Get ranking of the match
	 * 
	 * @return {@code List<Player>} ranking of the match
	 */
	public List<String> getMatchRanking(final boolean descendant) {
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
		
		JsonObjectBuilder builder = Json.createObjectBuilder();
		
		JsonArrayBuilder playersBuilder = Json.createArrayBuilder();
		
		List<String> ranking = getMatchRanking(descendant);
		
		int pos = 1;
		for (String player : ranking) {
			JsonObjectBuilder playerBuilder = Json.createObjectBuilder();
			playerBuilder.add("id", player);
			playerBuilder.add("rank", pos++);
			playerBuilder.add("points", playersPoints.get(player));
			playersBuilder.add(playerBuilder);
		}
		
		builder.add("players", playersBuilder);
		builder.add("movie", "");

		return builder.build();
	}
}
