package pt.up.fc.dcc.mooshak.evaluation.policy;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy.Policy;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class RankingPolicyTest {

	Contest contest;
	
	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {
		PersistentObject.setHome("home");
	}
	
	@Before
	public void setUp() throws Exception {
		contest= PersistentObject.openPath(CustomData.CONTEST_PATHNAME);
		contest.reopen();
	}

	@Test
	public void testGetPolicy() throws MooshakException {
				
		RankingPolicy policy = RankingPolicy.getPolicy(Policy.ICPC, contest);
		
		assertNotNull(policy);
	}

}
