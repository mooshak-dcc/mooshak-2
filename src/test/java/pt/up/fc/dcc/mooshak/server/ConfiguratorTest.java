package pt.up.fc.dcc.mooshak.server;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class ConfiguratorTest {

	Configurator configurator;
	
	@Before
	public void setUp() throws Exception {
		configurator = new Configurator();
	}

	@Test
	public void testMimeTable() {
		assertEquals("image/jpeg",Configurator.getMime("jpg"));
		assertEquals("image/jpeg",Configurator.getMime("jpeg"));
		assertEquals("image/png",Configurator.getMime("png"));
		
	}
	
	// @Test 
	public void testGetProperties() {
		
		for(Object property: System.getProperties().keySet()) {
			System.out.println(property+":\t"+System.getProperty((String) property));
		}
	}
	
	
	
}
