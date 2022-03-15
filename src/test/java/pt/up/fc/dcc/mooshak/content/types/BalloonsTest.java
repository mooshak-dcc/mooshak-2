package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class BalloonsTest {

	Balloons balloons;
	Balloons empty;
	
	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {
		PersistentObject.setHome(CustomData.HOME);
	}
	
	@Before
	public void setUp() throws Exception {
		balloons = PersistentObject.openPath(CustomData.BALLOONS_PATHNAME);
		empty= new Balloons();
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		balloons.reopen();
	}

	@Test
	public void testListPending() {
		assertEquals(false,balloons.isListPending());
		balloons.setListPending(true);
		assertEquals(true,balloons.isListPending());
	}

}
