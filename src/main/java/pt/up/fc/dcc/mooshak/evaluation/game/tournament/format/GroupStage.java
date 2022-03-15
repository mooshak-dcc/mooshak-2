package pt.up.fc.dcc.mooshak.evaluation.game.tournament.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Group;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Match;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Round;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.AbstractStage;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Stage of a tournament of group category. Defaults to single round-robin,
 * where each competitor plays all the others a given number of times.
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class GroupStage extends AbstractStage {
	
	protected int repeat = 1;

	protected Map<String, ProcessBuilder> players = null;
	protected List<Group> groups = null;
	protected Map<Integer, List<Round>> rounds = null;

	public GroupStage(
			int minNrOfPlayersPerMatch, int maxNrOfPlayersPerMatch, 
			int nrOfPlayersPerMatch, int minNrOfPlayersPerGroup,
			int nrOfQualifiedPlayers, int maxNrOfRounds) {
		super(minNrOfPlayersPerMatch, maxNrOfPlayersPerMatch, nrOfPlayersPerMatch,
				minNrOfPlayersPerGroup, nrOfQualifiedPlayers, maxNrOfRounds);
	}

	/**
	 * @return the repeat
	 */
	public int getRepeat() {
		return repeat;
	}

	/**
	 * @param repeat the repeat to set
	 */
	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	/**
	 * @return the groups
	 */
	public List<Group> getGroups() {
		return groups;
	}

	@Override
	public void prepare(Map<String, ProcessBuilder> players) throws MooshakException {
		this.players = players;

		distributePlayers();
	}

	/**
	 * Distribute players randomly in equal-sized groups
	 * 
	 * @throws MooshakException
	 *             If the set of players is not consistent with stage's
	 *             parameters
	 */
	protected void distributePlayers() throws MooshakException {

		groups = new ArrayList<>();

		int size = players.size();

		if (size < minNrOfPlayersPerGroup)
			throw new MooshakException("Not enough players for group!");

		if (size < nrOfQualifiedPlayers)
			throw new MooshakException("Not enough players to qualify!");

		int nGroups = size / minNrOfPlayersPerGroup;

		while (nGroups > nrOfQualifiedPlayers && nGroups > 1)
			nGroups--;

		for (int i = 0; i < nGroups; i++)
			groups.add(new Group());

		List<String> playerIds = new ArrayList<>(players.keySet());
		Collections.shuffle(playerIds);

		int groupIdx = 0;
		for (int i = 0; i < size; i++) {
			groups.get(groupIdx).addPlayer(playerIds.get(i), players.get(playerIds.get(i)));
			groupIdx = (groupIdx + 1) % nGroups;
		}
	}

	@Override
	public void run() throws MooshakException {

		generateMatches();

		for (int i = 0; i < groups.size(); i++) {

			for (Round round : rounds.get(i)) {

				for (Match match : round.getMatches()) {

					try {
						match.run();
					} catch (IOException e) {
						throw new MooshakException(e.getMessage());
					}

					groups.get(i).addMatch(match);
				}
			}
		}
	}

	/**
	 * Generate matches of this stage
	 */
	private void generateMatches() {

		rounds = new HashMap<>();

		for (int i = 0; i < groups.size(); i++) {

			List<Round> groupRounds = new ArrayList<>();

			List<String> playerIds = new ArrayList<>(groups.get(i).getPlayers().keySet());

			int[] perms = new int[playerIds.size()];
			for (int j = 0; j < perms.length; j++)
				perms[j] = j + 1;

			int count = 0;
			while (count < playerIds.size() - 1) {

				int tmp = perms[perms.length - 1];
				for (int j = perms.length - 2; j >= 1; j--)
					perms[j + 1] = perms[j];
				perms[1] = tmp;

				Round round = new Round();
				int nrMatches = playerIds.size() / nrOfPlayersPerMatch;
				List<Map<String, ProcessBuilder>> matchesPlayers = new ArrayList<>();
				for (int j = 0; j < nrMatches * nrOfPlayersPerMatch; j++) {
					if (matchesPlayers.size() <= j % nrMatches)
						matchesPlayers.add(new HashMap<>());

					matchesPlayers.get(j % nrMatches).put(playerIds.get(perms[j] - 1),
							players.get(playerIds.get(perms[j] - 1)));
				}

				for (Map<String, ProcessBuilder> matchPlayers : matchesPlayers)
					round.addMatch(new Match(matchPlayers));

				for (int j = nrMatches * nrOfPlayersPerMatch; j < playerIds.size(); j++)
					round.addBye(playerIds.get(perms[j] - 1));

				groupRounds.add(round);

				count++;
			}

			rounds.put(i, groupRounds);
			for (int j = 1; j < repeat; j++)
				rounds.get(i).addAll(groupRounds);
		}
	}

	@Override
	public List<String> getQualifiedPlayers() throws MooshakException {

		List<String> qualified = new ArrayList<>();

		List<List<String>> ranks = new ArrayList<>();
		for (Group group : groups) {
			ranks.add(group.getRanks(decreasing));
		}

		int nrGroupQualified = nrOfQualifiedPlayers / groups.size();
		int nrRemainingQualifiers = nrOfQualifiedPlayers % groups.size();

		final Map<String, Integer> remainingQualifiers = new HashMap<>();

		// get players qualified by position
		for (int i = 0; i < ranks.size(); i++) {
			List<String> groupRank = ranks.get(i);

			for (int j = 0; j < nrGroupQualified; j++) {
				String player = groupRank.get(j);
				qualified.add(player);
			}

			if (nrRemainingQualifiers > 0)
				remainingQualifiers.put(groupRank.get(nrGroupQualified),
						groups.get(i).getPoints(groupRank.get(nrGroupQualified)));
		}

		// get n first points qualifier
		List<String> remainingQualifiersIds = new ArrayList<>(remainingQualifiers.keySet());
		Collections.sort(remainingQualifiersIds, new Comparator<String>() {

			@Override
			public int compare(String p1, String p2) {
				if (decreasing)
					return remainingQualifiers.get(p2).compareTo(remainingQualifiers.get(p1));
				return remainingQualifiers.get(p1).compareTo(remainingQualifiers.get(p2));
			}
		});
		for (int i = 0; i < nrRemainingQualifiers; i++)
			qualified.add(remainingQualifiersIds.get(i));

		return qualified;
	}
}
