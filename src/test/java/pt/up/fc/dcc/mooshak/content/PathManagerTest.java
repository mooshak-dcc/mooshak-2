package pt.up.fc.dcc.mooshak.content;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.types.Flag;
import pt.up.fc.dcc.mooshak.content.types.Flags;
import pt.up.fc.dcc.mooshak.content.types.CustomData;


public class PathManagerTest {
	
	static final int TEST_MAX_ENTRIES = 10;  
	static int defaultMaxEntries = 0;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException {
		PersistentObject.setHome("home");
		defaultMaxEntries = PathManager.getMaxEntries();
		PathManager.setMaxEntries(TEST_MAX_ENTRIES);
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		PathManager.setMaxEntries(defaultMaxEntries);
	}
	
	
	@Test
	public void testFill() throws Exception {
		
		Flags flags = PersistentObject.openPath(CustomData.FLAGS_PATHNAME);
		List<Flag> before = new ArrayList<>();
		List<Flag> after = new ArrayList<>();
		
		try(POStream<Flag> stream = flags.newPOStream()) {
			int c = 0;
			for(Flag flag: stream) {
				if(c == 2 * TEST_MAX_ENTRIES) 
					break;
				if(c < TEST_MAX_ENTRIES)
					before.add(flag);
				else
					after.add(flag);
				c++;
			}
		}
		
		for(Flag flag:before) 
			assertTrue(! PathManager.check(flag.getPath()));
		for(Flag flag:after) 
			assertTrue(PathManager.check(flag.getPath()));
	}

}
