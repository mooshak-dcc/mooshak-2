package pt.up.fc.dcc.mooshak.server.commands;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class TeamCommandServiceImplTest {

	ParticipantCommandServiceImpl service;
	
	@BeforeClass
	public static void prepare() {
		SafeExecution.setWebInf(CustomData.WEB_INF);
		PersistentObject.setHome(CustomData.HOME);
	}
	
	@Before
	public void setUp() throws Exception {
		service =  new ParticipantCommandServiceImpl();
	}


	@Test
	public void testEvaluate() throws MooshakException {

		service.evaluate(
				CustomData.HELLO_NAME,
				CustomData.HELLO_CODE.getBytes(),
				CustomData.NO_PROBLEM_ID,
				CustomData.NO_INPUT,
				CustomData.DONT_CONSIDER);
	}
}
