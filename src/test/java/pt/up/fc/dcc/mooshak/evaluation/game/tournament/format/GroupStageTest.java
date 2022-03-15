package pt.up.fc.dcc.mooshak.evaluation.game.tournament.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Group;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Match;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Round;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.format.GroupStage;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class GroupStageTest {
	private static final int MAX_PLAYERS = 20;

	private Map<String, ProcessBuilder> players;
	private GroupStage groupStage;
	private int nrPlayersPerMatch, minPlayersPerMatch, maxPlayersPerMatch, minPlayersPerGroup, nrQualifiedPlayers;

	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {

	}

	@Before
	public void setUp() throws Exception {
		players = new HashMap<>();
		for (int i = 0; i < MAX_PLAYERS; i++)
			players.put("player" + (i + 1), new ProcessBuilder("/bin/true"));

		minPlayersPerMatch = 2;
		maxPlayersPerMatch = 3;
		nrPlayersPerMatch = 3;
		minPlayersPerGroup = 6;
		nrQualifiedPlayers = 8;

		groupStage = new GroupStage(minPlayersPerMatch, maxPlayersPerMatch,
				nrPlayersPerMatch, minPlayersPerGroup, nrQualifiedPlayers, -1);

	}

	@Test
	public void testPlayersDistribution() throws MooshakException {

		groupStage.prepare(players);

		Set<String> playerSet = new HashSet<>();
		for (Group group : groupStage.groups) {
			assertTrue(6 <= group.getPlayers().size());
			playerSet.addAll(group.getPlayers().keySet());
		}

		assertEquals(MAX_PLAYERS, playerSet.size());
	}

	@Test
	public void testGroupRank() {

		try {
			groupStage.prepare(players);

			for (Group group : groupStage.groups) {
				for (String player : group.getPlayers().keySet())
					group.setPoints(player, (int) (Math.random() * 997));
			}

			for (Group group : groupStage.groups) {
				checkGroupRank(group, true);
				checkGroupRank(group, false);
				group.setPoints(group.getPlayers().keySet().iterator().next(), (int) (Math.random() * 997));
				checkGroupRank(group, false);
				checkGroupRank(group, true);
			}
		} catch (MooshakException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Check ranking of the group
	 */
	private void checkGroupRank(Group group, boolean descendant) {

		List<String> ranks = group.getRanks(descendant);

		assertEquals(ranks.size(), group.getPlayers().size());
		for (int i = 0; i < group.getPlayers().size() - 1; i++) {
			try {
				if (descendant)
					assertTrue(group.getPoints(ranks.get(i)) >= group.getPoints(ranks.get(i + 1)));
				else
					assertTrue(group.getPoints(ranks.get(i)) <= group.getPoints(ranks.get(i + 1)));
			} catch (MooshakException e) {
				fail(e.getMessage());
			}
		}
	}

	@Test
	public void testNumberOfGeneratedMatches() {

		try {
			groupStage.prepare(players);
			groupStage.run();
		} catch (MooshakException e) {
			fail(e.getMessage());
		}

		for (int i = 0; i < groupStage.groups.size(); i++) {

			Group group = groupStage.groups.get(i);

			List<Round> rounds = groupStage.rounds.get(i);

			int groupSize = group.getPlayers().size();

			assertTrue(rounds.size() == groupSize - 1);

			List<Match> matches = new ArrayList<>();
			for (Round round : rounds) {

				Set<String> players = new HashSet<>();
				for (Match match : round.getMatches())
					for (String playerId : match.getPlayers().keySet())
						players.add(playerId);

				assertEquals(round.getMatches().size() * nrPlayersPerMatch, players.size());
				assertEquals(groupSize / nrPlayersPerMatch, round.getMatches().size());
				assertEquals(groupSize % nrPlayersPerMatch, round.getByes().size());
				players.addAll(round.getByes());
				assertEquals(groupSize, players.size());
				matches.addAll(round.getMatches());
			}

			assertTrue(matches.size() == new HashSet<>(matches).size());
		}
	}

	@Test
	public void testQualifiedPlayers() {

		try {
			groupStage.prepare(players);
		
			for (Group group : groupStage.groups) {
			
				for (String playerId : group.getPlayers().keySet()) {
					int score = (int) (Math.random() * 997);
					group.setPoints(playerId, score);
				}
				
			}
			
			List<String> qualified = groupStage.getQualifiedPlayers();
			
			assertEquals(nrQualifiedPlayers, qualified.size());
			
			Map<String, Integer> pointsQualifiers = new HashMap<>();
			for (Group group : groupStage.groups) {
				
				List<String> rank = group.getRanks(true);
				assertTrue(qualified.contains(rank.get(0)));
				assertTrue(qualified.contains(rank.get(1)));
				pointsQualifiers.put(rank.get(2), group.getPoints(rank.get(2)));
			}
			
			List<String> pointsQualifiersPlayers = new ArrayList<>(pointsQualifiers.keySet());
			Collections.sort(pointsQualifiersPlayers, new Comparator<String>() {

				@Override
				public int compare(String o1, String o2) {
					return pointsQualifiers.get(o2).compareTo(pointsQualifiers.get(o1));
				}
			});
			
			assertTrue(qualified.contains(pointsQualifiersPlayers.get(0)));			
			assertTrue(qualified.contains(pointsQualifiersPlayers.get(1)));
		
		} catch (MooshakException e) {
			fail(e.getMessage());
		}
		
	}
}
