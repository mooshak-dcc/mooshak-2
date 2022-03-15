package pt.up.fc.dcc.mooshak;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import pt.up.fc.dcc.mooshak.content.ContentTests;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationTests;
import pt.up.fc.dcc.mooshak.managers.ManagersTests;
import pt.up.fc.dcc.mooshak.server.ServerTests;
import pt.up.fc.dcc.mooshak.shared.SharedTests;


@RunWith(Suite.class)
@Suite.SuiteClasses({
		ContentTests.class,
		ManagersTests.class,
		ServerTests.class,
		SharedTests.class,
		EvaluationTests.class,
	})

public class AllTests {

}
