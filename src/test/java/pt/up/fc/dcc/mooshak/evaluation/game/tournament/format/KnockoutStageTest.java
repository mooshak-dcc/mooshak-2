package pt.up.fc.dcc.mooshak.evaluation.game.tournament.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Match;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.Round;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class KnockoutStageTest {
	private static final int MAX_PLAYERS = 52;

	private Map<String, ProcessBuilder> players;
	private KnockoutStage knockoutStage;
	private int minPlayersPerMatch, maxPlayersPerMatch, nrPlayersPerMatch, nrQualifiedPlayers;

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
		nrQualifiedPlayers = 8;

		knockoutStage = new KnockoutStage(minPlayersPerMatch, maxPlayersPerMatch, nrPlayersPerMatch, 
				-1, nrQualifiedPlayers, -1);

	}

	@Test
	public void testRunSingleElimination() {

		try {
			knockoutStage.prepare(players);
			knockoutStage.run();
		} catch (MooshakException e) {
			fail(e.getMessage());
		}
		
		assertEquals(2, knockoutStage.rounds.size());

		assertEquals(17, knockoutStage.rounds.get(0).getMatches().size());
		assertEquals(1, knockoutStage.rounds.get(0).getByes().size());
		assertEquals(6, knockoutStage.rounds.get(1).getMatches().size());
		assertEquals(0, knockoutStage.rounds.get(1).getByes().size());
		
		for (Round round : knockoutStage.rounds) {
			
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
		}

		assertEquals(nrQualifiedPlayers, knockoutStage.getQualifiedPlayers().size());
	}

}
