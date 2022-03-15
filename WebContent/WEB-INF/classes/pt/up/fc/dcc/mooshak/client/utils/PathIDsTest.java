package pt.up.fc.dcc.mooshak.client.utils;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class PathIDsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNormalize() {
		assertEquals("a/b",PathIDs.normalize("a/b/c/../../b"));
		assertEquals("ola/pois",PathIDs.normalize("ola/ole/../pois"));
		assertEquals("ola/../../pois",PathIDs.normalize("ola/ole/../../../pois"));
	}
	
	@Test
	public void testGetIdName() {
		assertEquals("name",PathIDs.getIdName("name"));
		assertEquals("name",PathIDs.getIdName("full/name"));
		assertEquals("name",PathIDs.getIdName("a/very/long/name"));
	}

}
