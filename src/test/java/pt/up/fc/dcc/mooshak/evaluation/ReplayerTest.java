package pt.up.fc.dcc.mooshak.evaluation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.FileDescriptors;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.CustomData;

public class ReplayerTest {
	
	private static final String SUBMISSIONS = "data/contests/ToPAS14/submissions/";
	
	Replayer replayer;	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistentObject.setHome(CustomData.HOME);
		
		SafeExecution.setWebInf(CustomData.WEB_INF);
		
	}

	@Before
	public void setUp() throws Exception {
		Submissions submissions = PersistentObject.openPath(SUBMISSIONS);
		replayer = new Replayer(submissions);
	}

	@Test
	public void testReplay() throws InterruptedException, IOException  {
		Logger logger = Logger.getLogger("");
		
		logger.setLevel(Level.WARNING);
		
		replayer.begin();
		
		replayer.join();
		
		assertEquals("Unusual file descriptors",0,FileDescriptors.countUnusual());
	}

}
