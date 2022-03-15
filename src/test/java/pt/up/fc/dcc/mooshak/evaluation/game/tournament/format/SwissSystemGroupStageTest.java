package pt.up.fc.dcc.mooshak.evaluation.game.tournament.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Match;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Round;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.format.SwissSystemGroupStage;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class SwissSystemGroupStageTest {
	private static final int MAX_PLAYERS = 20;

	private Map<String, ProcessBuilder> players;
	private SwissSystemGroupStage groupStage;
	private int minPlayersPerMatch, maxPlayersPerMatch, nrPlayersPerMatch, minPlayersPerGroup, nrQualifiedPlayers;

	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {

	}

	@Before
	public void setUp() throws Exception {
		players = new HashMap<>();
		for (int i = 0; i < MAX_PLAYERS; i++)
			players.put("player" + (i + 1), null);

		minPlayersPerMatch = 2;
		maxPlayersPerMatch = 3;
		nrPlayersPerMatch = 3;
		minPlayersPerGroup = 6;
		nrQualifiedPlayers = 8;

		groupStage = new SwissSystemGroupStage(minPlayersPerMatch, maxPlayersPerMatch, nrPlayersPerMatch,
				minPlayersPerGroup, nrQualifiedPlayers, 10);
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
			
			List<Match> matches = new ArrayList<>();
			for (Round round : groupStage.rounds.get(i)) {
				
				for (Match match : round.getMatches()) {
					
					for (String playerId : match.getPlayers().keySet()) {
						System.out.print(playerId + " ");
					}
					System.out.println();
				}
				
				System.out.print("Byes: ");
				for (String playerId : round.getByes()) {
					System.out.print(playerId + " ");
				}
				System.out.println();
				
				System.out.println();
				matches.addAll(round.getMatches());
			}

			assertEquals(2, groupStage.rounds.get(i).size());
			assertEquals(2 * groupStage.groups.get(i).getPlayers().size() / nrPlayersPerMatch, 
					matches.size());
		}
	}

}
