package pt.up.fc.dcc.mooshak.evaluation.game.tournament.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Match;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Round;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Matches are scheduled one round at a time; a competitor will play another who
 * has a similar record in previous rounds
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class SwissSystemGroupStage extends GroupStage {

	public SwissSystemGroupStage(
			int minNrOfPlayersPerMatch, int maxNrOfPlayersPerMatch,
			int nrOfPlayersPerMatch, int minNrOfPlayersPerGroup, 
			int nrOfQualifiedPlayers, int maxNrOfRounds) {
		
		super(minNrOfPlayersPerMatch, maxNrOfPlayersPerMatch, 
				nrOfPlayersPerMatch, minNrOfPlayersPerGroup,
				nrOfQualifiedPlayers, maxNrOfRounds);
		
		if (this.maxNrOfRounds <= 0)
			this.maxNrOfRounds = Integer.MAX_VALUE;
	}

	@Override
	public void run() throws MooshakException {

		rounds = new HashMap<>();

		for (int i = 0; i < groups.size(); i++) {

			int nrRounds = Math.min(findTotalRounds(groups.get(i).getPlayers().size(), 0),
					maxNrOfRounds);

			if (rounds.get(i) == null)
				rounds.put(i, new ArrayList<>());

			for (int j = 0; j < nrRounds; j++) {
				Round round = new Round();

				List<String> ranks = groups.get(i).getRanks(decreasing);

				Draw draw = new Draw(ranks, rounds.get(i));
				draw.draw();

				List<Match> matches = draw.getMatches();

				List<String> byes = new ArrayList<>(ranks);
				for (Match match : matches) {
					round.addMatch(match);

					byes.removeAll(match.getPlayers().keySet());

					try {
						match.run();
					} catch (IOException e) {
						throw new MooshakException(e.getMessage());
					}

					groups.get(i).addMatch(match);
				}

				for (String bye : byes)
					round.addBye(bye);

				rounds.get(i).add(round);
			}
		}
	}

	/**
	 * Find the exact number of rounds that must be played throughout the
	 * tournament
	 * 
	 * @param nrPlayers
	 *            Number of players
	 * @param add
	 *            the positive integer to be added to number of players in order
	 *            to make that amount an exact power of the number of players
	 *            per match
	 * @return exact number of rounds that must be played throughout the
	 *         tournament
	 */
	private int findTotalRounds(int nrPlayers, int add) {

		if (nrPlayers <= 1)
			return 0;

		double rounds = Math.log(nrPlayers + add) / Math.log(nrOfPlayersPerMatch);

		if (rounds % 1 == 0)
			return (int) rounds;

		return findTotalRounds(nrPlayers, add + 1);
	}

	/**
	 * This class helps us to draw sets of players for a Swiss-system tournament
	 * round
	 * 
	 * @author josepaiva
	 */
	class Draw {

		private List<String> ranks = null;
		private List<Round> previousRounds = null;

		private List<Integer> alreadyDrawn = null;
		private List<String> alreadyBye = null;

		private List<Integer> currentMatchPlayers = null;
		private List<List<Integer>> matchesPlayers = null;

		public Draw(List<String> ranks, List<Round> previousRounds) {
			this.ranks = ranks;
			this.previousRounds = previousRounds;

			this.alreadyDrawn = new ArrayList<>();
			this.alreadyBye = getPlayersWithByeRound();

			this.currentMatchPlayers = new ArrayList<>();
			this.matchesPlayers = new ArrayList<>();
		}

		/**
		 * Look for players who have already been assigned a bye round
		 * 
		 * @return List of players who have already been assigned a bye round
		 */
		private List<String> getPlayersWithByeRound() {

			Set<String> alreadyBye = new HashSet<>();

			for (Round round : previousRounds) {
				alreadyBye.addAll(round.getByes());
			}

			return new ArrayList<>(alreadyBye);
		}

		/**
		 * Mark players passed as arguments as already drawn
		 * 
		 * @param playersPos
		 *            Positions of the players to add to the alreadyDrawn.
		 */
		private void reset(Integer... playersPos) {

			for (Integer player : playersPos) {
				alreadyDrawn.add(player);
			}

			currentMatchPlayers = new ArrayList<>();
		}

		/**
		 * Check if a set of players have already made a match previously
		 * 
		 * @return <code>true</code> if players have already made a match,
		 *         <code>false</code> otherwise
		 */
		private boolean checkAlreadyMatched(List<Integer> playersPos) {

			List<String> players = new ArrayList<>();

			for (Integer pos : playersPos)
				players.add(ranks.get(pos));

			for (Round round : previousRounds) {

				for (Match match : round.getMatches()) {

					if (match.getPlayers().keySet().containsAll(players))
						return true;
				}
			}

			return false;
		}

		/**
		 * Find players that haven't been drawn yet and that haven't played
		 * against the current set of players
		 * 
		 * @param playerPos
		 * @return List of players that haven't been drawn yet and that haven't
		 *         played against the current set of players
		 */
		private List<Integer> getMatchables(Integer playerPos) {

			List<Integer> notAlreadyDrawn = new ArrayList<>();
			for (int i = 0; i < ranks.size(); i++) {
				if (!alreadyDrawn.contains(i))
					notAlreadyDrawn.add(i);
			}

			List<Integer> matchables = new ArrayList<>();
			for (Integer otherPlayerPos : notAlreadyDrawn) {
				if (!otherPlayerPos.equals(playerPos)) {
					List<Integer> tmpPlayerPos = new ArrayList<>();
					tmpPlayerPos.add(playerPos);
					tmpPlayerPos.add(otherPlayerPos);
					if (!checkAlreadyMatched(tmpPlayerPos))
						matchables.add(otherPlayerPos);
				}
			}
			return matchables;
		}

		/**
		 * If in the end a player who was left out already has a bye, then we
		 * have to proceed backwards and replace this player by a player which
		 * can have a bye round and whose set is matchable with the new player
		 * 
		 * @param playerPos
		 *            The position of the player which has to be included in a
		 *            previous
		 */
		private void swapPreviousPlayerSet(Integer playerPos) {
			swapPreviousPlayerSet(playerPos, nrOfPlayersPerMatch - 1);
		}

		/**
		 * If in the end a player who was left out already has a bye, then we
		 * have to proceed backwards and replace this player by a player which
		 * can have a bye round and whose set is matchable with the new player
		 * 
		 * @param playerPos
		 *            The position of the player which has to be included in a
		 *            previous
		 * @param delta
		 *            the desired ranking gap for making the new set
		 */
		private void swapPreviousPlayerSet(Integer playerPos, int delta) {

			boolean foundSwap = false;

			for (int i = matchesPlayers.size() - 1; i >= 0; i--) {
				List<Integer> matchPlayers = matchesPlayers.get(i);

				int j;
				for (j = 0; j < matchPlayers.size(); j++) {
					Integer otherPlayerPos = matchPlayers.get(j);
					if (Math.max(playerPos, otherPlayerPos) <= delta) {

						if (!alreadyBye.contains(ranks.get(otherPlayerPos))) {

							List<Integer> tmpMatchPlayers = new ArrayList<>(matchPlayers);
							tmpMatchPlayers.remove(otherPlayerPos);
							tmpMatchPlayers.add(playerPos);

							if (!checkAlreadyMatched(tmpMatchPlayers)) {

								foundSwap = true;
								break;
							}
						}
					}
				}

				if (foundSwap) {
					matchPlayers.remove(j);
					alreadyDrawn.remove(j);
					matchPlayers.add(playerPos);
					reset(playerPos);
					break;
				}
			}

			if (!foundSwap)
				swapPreviousPlayerSet(playerPos, delta + 1);
		}

		/**
		 * Make the sets of matchable players
		 */
		void draw() {

			for (int i = 0; i < ranks.size(); i++) {

				if (alreadyDrawn.contains(i))
					continue;

				String player = ranks.get(i);

				if (currentMatchPlayers.isEmpty()) {

					if (ranks.size() % nrOfPlayersPerMatch != 0
							&& (ranks.size() / nrOfPlayersPerMatch) == matchesPlayers.size()) {

						if (alreadyBye.contains(player)) {
							swapPreviousPlayerSet(i);
						} else {
							currentMatchPlayers.add(i);
							reset(i);
						}
					} else {
						currentMatchPlayers.add(i);
					}
				} else if (currentMatchPlayers.size() < nrOfPlayersPerMatch) {
					List<Integer> tmpCurrentMatchPlayers = new ArrayList<>(currentMatchPlayers);
					tmpCurrentMatchPlayers.add(i);
					if (currentMatchPlayers.contains(i) || checkAlreadyMatched(tmpCurrentMatchPlayers)) {

						if (getMatchables(i).size() >= nrOfPlayersPerMatch - 1)
							continue;
						else if (!matchesPlayers.isEmpty()) {
							List<Integer> previousSet = matchesPlayers.remove(matchesPlayers.size() - 1);
							alreadyDrawn.removeAll(previousSet);

							previousSet.remove(0);
							previousSet.add(i);

							reset(previousSet.toArray(new Integer[previousSet.size()]));
							matchesPlayers.add(previousSet);
						}
					} else {
						currentMatchPlayers.add(i);
						if (currentMatchPlayers.size() == nrOfPlayersPerMatch) {
							matchesPlayers.add(currentMatchPlayers);
							reset(currentMatchPlayers.toArray(new Integer[currentMatchPlayers.size()]));
						}
					}
				}
			}

			if (alreadyDrawn.size() < ranks.size())
				draw();
		}

		/**
		 * Get subsets of players that will match each others
		 * 
		 * @return subsets of players that will match each others
		 */
		List<Match> getMatches() {

			List<Match> matches = new ArrayList<>();
			for (List<Integer> playersPos : matchesPlayers) {
				Map<String, ProcessBuilder> matchPlayers = new HashMap<>();
				for (Integer playerPos : playersPos) {
					String player = ranks.get(playerPos);
					matchPlayers.put(player, players.get(player));
				}
				matches.add(new Match(matchPlayers));
			}
			return matches;
		}
	}
}
