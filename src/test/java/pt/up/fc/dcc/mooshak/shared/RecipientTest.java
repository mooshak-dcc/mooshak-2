package pt.up.fc.dcc.mooshak.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.shared.events.Recipient;

public class RecipientTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testEquals() {
		Recipient ab = new Recipient("a","b");
		Recipient ax = new Recipient("a",null);
		Recipient xx = new Recipient(null,null);
		Recipient oo = new Recipient();
		
		assertTrue(oo.equals(xx));
		assertTrue(! oo.equals(ax));
		
		oo.setUserId("c");
		assertTrue(! oo.equals(ax));
		assertTrue(! ax.equals(oo));
		
		oo.setUserId("a");
		assertEquals(oo,ax);
		assertEquals(oo,ab);
		assertEquals(ax,oo);
		assertEquals(ab,oo);
		
		oo.setSessionId("b");
		assertEquals(oo,ax);
		assertEquals(oo,ax);
		
		assertEquals(ab,ax);
		assertEquals(ax,ab);
		
		assertTrue(! xx.equals(ax));
		assertTrue(! ax.equals(xx));
	}

}
