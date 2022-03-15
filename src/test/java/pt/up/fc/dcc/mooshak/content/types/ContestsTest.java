package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;

public class ContestsTest {

	Contests loaded;
	Contests created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get("data","SOME_CONTESTS");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Contests.class);
		loaded = PersistentObject.openPath(CustomData.CONTESTS);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}

	@Test
	public void testContests() throws MooshakContentException {
		
		
		assertNotNull(loaded);
		assertTrue(loaded instanceof Contests);
	}

	
	@Test
	public void testGetDomains() throws MooshakException {
		List<ResultsContestInfo> domains = loaded.getDomains(false, false);
		String protoDesignation = null;
		String topasDesignation = null;
		
		for (ResultsContestInfo resultsContestInfo : domains) {
			if(resultsContestInfo.getContestId().equals("proto_icpc"))
				protoDesignation = resultsContestInfo.getContestName();
			else if(resultsContestInfo.getContestId().equals("ToPAS14"))
				topasDesignation = resultsContestInfo.getContestName();
		}
		
		
		assertEquals("ICPC contest prototype",protoDesignation);
		assertEquals(null,topasDesignation); // ToPAS is concluded
		
		
	}
}
