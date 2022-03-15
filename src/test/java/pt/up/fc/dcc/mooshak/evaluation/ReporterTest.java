package pt.up.fc.dcc.mooshak.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.MooshakTypeException;
import pt.up.fc.dcc.mooshak.content.types.CustomData;

public class ReporterTest {
	Reporter reporter;

	private static final String SMALL_FILE = "data/contests/proto_icpc/problems/J/tests/T1/input";
	private static final String LARGE_FILE = "data/contests/proto_icpc/problems/J/tests/T0/input";
			
	
	
	@BeforeClass
	public static void setUpClass() throws Exception {
		// SafeExecution.setWebInf(TestData.WEB_INF);
		PersistentObject.setHome(CustomData.HOME);
	}
	
	@Before
	public void setUp() throws Exception {
		reporter = new Reporter(null);
	}

	@Test
	public void testGetFileContent() throws MooshakTypeException, IOException {
		Path path = null;
		String text;
		
		assertEquals("",reporter.getFileContent(path));
		
		path = PersistentCore.getAbsoluteFile(Paths.get(SMALL_FILE));
		text = reporter.getFileContent(path);
		
		assertEquals(Files.size(path),text.length());
		
		path = PersistentCore.getAbsoluteFile(Paths.get(LARGE_FILE));
		text = reporter.getFileContent(path);
		
		assertTrue(text.endsWith(Reporter.LARGE_OUTPUT_MESSAGE));
	}

}
