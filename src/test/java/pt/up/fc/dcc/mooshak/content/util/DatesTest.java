package pt.up.fc.dcc.mooshak.content.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;

public class DatesTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testShow() {
		
		assertEquals("1970-01-01 00:00:00",Dates.show(new Date(0)));	
	}
	
	@Test
	public void testParse() throws MooshakContentException {
		Date now = new Date();
		
		assertEquals(Dates.show(now),Dates.show(Dates.parse(Dates.show(now))));
		
		try {
			Dates.parse("this is not a valid date");
		} catch(MooshakContentException e) {
			assertNotNull(e);
		}
	}

}
