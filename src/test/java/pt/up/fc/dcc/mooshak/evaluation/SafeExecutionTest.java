package pt.up.fc.dcc.mooshak.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Tests on SafeExecution
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class SafeExecutionTest {
	SafeExecution safeExecution;
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		SafeExecution.setWebInf(CustomData.WEB_INF);
	}
	
	@Before
	public void setUp() {
		safeExecution = new SafeExecution();
	}

	@Test
	public void testIsSafeexecOk() throws MooshakException {
		
		assertTrue(SafeExecution.isSafexecOk());
	}
	
	@Test
	public void testGetSafeexecPermissions() throws MooshakException {
		
		assertEquals("-rwsr-xr-x",SafeExecution.getSafeexecPermissions());
	}
	
	private static final int SIZE = 2048;
	
	@Test
	public void testWriteStream() throws MooshakSafeExecutionException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream(SIZE);
		
		safeExecution.writeStream(Paths.get("MooshakProperties.xml"),buffer);
		
		assertTrue(buffer.toString().
				startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
	}
	
	

}
