package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class UserTestDataTest {
	UserTestData usertestData;
	
	@Before
	public void setUp() throws Exception {
		usertestData = new UserTestData();
	}

	@Test
	public void testExecutionTimes() {
		usertestData.addExecutionTimes(1, 0.1);
		usertestData.addExecutionTimes(3, 0.3);
		usertestData.addExecutionTimes(2, 0.2);
		
		Map<Integer,String> map = usertestData.getExecutionTimes();
		
		assertEquals(3,map.size());
		assertEquals("0.1",map.get(1));
		assertEquals("0.2",map.get(2));
		assertEquals("0.3",map.get(3));
	}

}
