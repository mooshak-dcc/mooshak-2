package pt.up.fc.dcc.mooshak.evaluation.game.tournament;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import pt.up.fc.dcc.mooshak.evaluation.game.tournament.format.GroupStageTest;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.format.KnockoutStageTest;
import pt.up.fc.dcc.mooshak.evaluation.game.tournament.format.SwissSystemGroupStageTest;

@RunWith(Suite.class)
@SuiteClasses({
	GroupStageTest.class,
	KnockoutStageTest.class,
	SwissSystemGroupStageTest.class,
})
public class TournamentTests {
}
