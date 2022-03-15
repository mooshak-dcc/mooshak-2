package pt.up.fc.dcc.mooshak.evaluation.game.tournament.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Match;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Round;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.AbstractStage;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Stage of a tournament of knockout category
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class KnockoutStage extends AbstractStage {

	protected Map<String, ProcessBuilder> players = null;
	protected List<Round> rounds = null;

	protected Set<String> alreadyBye = null;
	protected Set<String> qualified = null;

	public KnockoutStage(
			int minNrOfPlayersPerMatch, int maxNrOfPlayersPerMatch, 
			int nrOfPlayersPerMatch, int minNrOfPlayersPerGroup,
			int nrOfQualifiedPlayers, int maxNrOfRounds) 
					throws MooshakException {
		
		super(minNrOfPlayersPerMatch, maxNrOfPlayersPerMatch, 
				nrOfPlayersPerMatch, minNrOfPlayersPerGroup, 
				nrOfQualifiedPlayers, maxNrOfRounds);
	}

	@Override
	public void prepare(Map<String, ProcessBuilder> players) throws MooshakException {
		this.players = players;
	}

	@Override
	public void run() throws MooshakException {

		rounds = new ArrayList<>();
		alreadyBye = new HashSet<>();

		Map<String, ProcessBuilder> qualifiedPlayers = new HashMap<>(players);

		while (qualifiedPlayers.size() > nrOfQualifiedPlayers) {
			Round round = new Round();

			List<String> roundBye = new ArrayList<>(qualifiedPlayers.keySet());

			List<Match> matches = generateMatches(qualifiedPlayers);
			for (Match match : matches) {
				try {
					match.run();
				} catch (IOException e) {
					throw new MooshakException(e.getMessage());
				}

				List<String> ranks = match.getMatchRanking(decreasing);
				for (int i = ranks.size() - 1; i >= 1; i--)
					qualifiedPlayers.remove(ranks.get(i));

				roundBye.removeAll(match.getPlayers().keySet());
				
				round.addMatch(match);
			}

			for (String playerId : roundBye) {
				round.addBye(playerId);
				alreadyBye.add(playerId);
			}

			if (qualifiedPlayers.size() < nrOfQualifiedPlayers) {
				int completePos = (nrOfQualifiedPlayers - qualifiedPlayers.size()) / matches.size();
				Map<String, Integer> pointsQualifier = new HashMap<>();
				for (Match match : matches) {
					List<String> ranks = match.getMatchRanking(decreasing);
					for (int i = 1; i <= completePos; i++)
						qualifiedPlayers.put(ranks.get(i), players.get(ranks.get(i)));
					pointsQualifier.put(ranks.get(completePos + 1), match.getPoints(ranks.get(completePos + 1)));
				}

				List<String> pointsQualifierIds = new ArrayList<>(pointsQualifier.keySet());
				Collections.sort(pointsQualifierIds, new Comparator<String>() {

					@Override
					public int compare(String p1, String p2) {

						Integer pointsP1 = pointsQualifier.get(p1);
						Integer pointsP2 = pointsQualifier.get(p2);

						if (pointsP1 == null)
							return decreasing ? -1 : 1;

						if (pointsP2 == null)
							return decreasing ? 1 : -1;

						return decreasing ? pointsP2.compareTo(pointsP1) : pointsP1.compareTo(pointsP2);
					}
				});

				int missing = nrOfQualifiedPlayers - qualifiedPlayers.size();
				for (int i = 0; i < missing; i++)
					qualifiedPlayers.put(pointsQualifierIds.get(i), players.get(pointsQualifierIds.get(i)));
			}

			rounds.add(round);
		}

		this.qualified = qualifiedPlayers.keySet();
	}

	/**
	 * Generate knockout matches
	 * 
	 * @param players
	 *            Players in the current round
	 * @return matches of this stage
	 * @throws MooshakException
	 */
	protected List<Match> generateMatches(Map<String, ProcessBuilder> players) throws MooshakException {

		List<String> playersIds = new ArrayList<String>(players.keySet());
		Collections.shuffle(playersIds);

		int maxMatches = playersIds.size() / nrOfPlayersPerMatch;

		List<Match> matches = new ArrayList<>();

		int nrByes = players.size() - maxMatches * nrOfPlayersPerMatch;
		List<String> byes = new ArrayList<>();
		for (String playerId : byes) {
			if (!alreadyBye.contains(playerId))
				byes.add(playerId);
			if (byes.size() >= nrByes)
				break;
		}

		List<Map<String, ProcessBuilder>> matchesPlayers = new ArrayList<>();

		int placed = 0;
		for (int i = 0; i < maxMatches; i++) {
			Map<String, ProcessBuilder> matchPlayers = new HashMap<>();
			for (int j = 0; j < nrOfPlayersPerMatch; j++) {
				String playerId = playersIds.get(placed);
				if (byes.contains(playerId))
					continue;
				matchPlayers.put(playerId, players.get(playerId));
				placed++;
			}
			matchesPlayers.add(matchPlayers);
		}

		for (Map<String, ProcessBuilder> matchPlayers : matchesPlayers)
			matches.add(new Match(matchPlayers));

		return matches;
	}

	@Override
	public List<String> getQualifiedPlayers() {
		return new ArrayList<>(qualified);
	}

}
