package pt.up.fc.dcc.mooshak.content.types;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Problem.ProblemDifficulty;
import pt.up.fc.dcc.mooshak.content.types.Problem.ProblemType;

public class ProblemTest {

	//private static final Path PROBLEM_C_FILE = Paths.get(TestData.PROBLEM_C_PATHNAME);
	private static final Path PROBLEM_J_FILE = Paths.get(CustomData.PROBLEM_J_PATHNAME);
	private static final Path PROBLEM_M_FILE = Paths.get(CustomData.PROBLEM_M_PATHNAME);

	private static final Path SOME_FILE =  PROBLEM_J_FILE.resolve("some_file");
	private static final String text = "SOME TEXT";
	private static final int SOME_INT = 10;

	
	Problem problemJ;
	Problem problemC;
	Problem problemM;
	Problem created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome("home");
		
		createdPath = Paths.get(CustomData.PROBLEMS_PATHNAME,"SOME_PROBLEM");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Problem.class);
		problemJ = PersistentObject.openPath(CustomData.PROBLEM_J_PATHNAME);
		problemC = PersistentObject.openPath(CustomData.PROBLEM_C_PATHNAME);
		problemM = PersistentObject.openPath(CustomData.PROBLEM_M_PATHNAME);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		problemJ.reopen();
		problemC.reopen();
		created.delete();
	}
	
	
	@Test
	public void testFatal() throws MooshakContentException {

		assertEquals("",problemJ.getFatal());
		assertEquals("",created.getFatal());

		for(String text: CustomData.TEXTS) {
			problemJ.setFatal(text);
			assertEquals(text,problemJ.getFatal());

			created.setFatal(text);
			created.save(); created.reopen();
			assertEquals(text,created.getFatal());
		}
	}

	@Test
	public void testWarning() throws MooshakContentException {
		String warning = "Undefined variable <code>Color</code><br>";

		assertEquals(warning,problemJ.getWarning());
		assertEquals("",created.getWarning());

		for(String text: CustomData.TEXTS) {
			problemJ.setWarning(text);
			assertEquals(text,problemJ.getWarning());

			created.setWarning(text);
			created.save(); created.reopen();
			assertEquals(text,created.getWarning());
		}
	}
	
	@Test
	public void testName() throws MooshakContentException {
		assertEquals(null,created.getName());
		assertEquals("J",problemJ.getName());
		
		for(String problemId: new String [] { "A", "B", "1.1", "111", "A1" }) {
			created.setName(problemId.toString());
			created.save(); created.reopen();
			assertEquals(problemId,created.getName());
			
			problemJ.setName(problemId.toString());
			assertEquals(problemId,problemJ.getName());
		}
	}
	
	@Test
	public void testDifficulty() throws MooshakContentException, IOException  {
		assertEquals(null,created.getDifficulty());
		assertEquals(null,problemJ.getDifficulty());
		assertEquals(ProblemDifficulty.MEDIUM,problemC.getDifficulty());
		
		for(ProblemDifficulty difficulty:ProblemDifficulty.values()) {
			created.setDifficulty(difficulty);
			created.save(); created.reopen();
			assertEquals(difficulty,created.getDifficulty());
			
			assertTrue(CustomData.checkContent(created, "Difficulty", 
					difficulty.toString()));
			
			problemJ.setDifficulty(difficulty);
			assertEquals(difficulty,problemJ.getDifficulty());
		}
	}
	
	@Test
	public void testType() throws MooshakContentException, IOException {
		assertEquals(null,created.getType());
		assertEquals(null,problemJ.getType());
		assertEquals(ProblemType.AD_HOC,problemC.getType());
		
		for(ProblemType type: ProblemType.values()) {
			created.setType(type);
			created.save(); created.reopen();
			assertEquals(type,created.getType());
			
			assertTrue(CustomData.checkContent(created, "Type", 
					type.toString().toLowerCase()));
			
			problemJ.setType(type);
			assertEquals(type,problemJ.getType());
		}
	}
	

	@Test
	public void testHtmlDescription() throws MooshakContentException {
		assertEquals(null,created.getHtmlDescription());
		assertEquals(PROBLEM_J_FILE.resolve("risk.html"),
				problemJ.getHtmlDescription());
		
		created.setHtmlDescription(SOME_FILE);
		created.save(); created.reopen();
		assertNull(null,created.getHtmlDescription());
		
		problemJ.setHtmlDescription(SOME_FILE);
		assertNull(problemJ.getHtmlDescription());
	}
	
	
	@Test
	public void testPDF() throws MooshakContentException {
		
		assertEquals(null,created.getPdfDescription());
		assertEquals(null,problemJ.getPdfDescription());
		
		assertEquals(PROBLEM_M_FILE.resolve("Rotations and Reflections.pdf"),
				problemM.getPdfDescription());
		
		created.setPdfDescription(SOME_FILE);
		created.save(); created.reopen();
		assertEquals(null,created.getPdfDescription());
		
		problemJ.setPdfDescription(SOME_FILE);
		assertEquals(null,problemJ.getPdfDescription());
	}
	
	@Test
	public void testProgram() throws MooshakContentException {
		assertEquals(null,created.getProgram());
		assertEquals(PROBLEM_J_FILE.resolve("risk.java"),problemJ.getProgram());
		
		created.setProgram(SOME_FILE);
		created.save(); created.reopen();
		assertEquals(SOME_FILE.getFileName(),created.getProgram().getFileName());
		
		problemJ.setProgram(SOME_FILE);
		assertEquals(SOME_FILE,problemJ.getProgram());
	}
	
	@Test
	public void testEnvironment() throws MooshakContentException {
		assertEquals(null,created.getEnvironment());
		assertEquals(PROBLEM_J_FILE.resolve("Environment"),
				problemJ.getEnvironment());
		
		created.setEnvironment(SOME_FILE);
		created.save(); created.reopen();
		assertEquals(SOME_FILE.getFileName(),
				created.getEnvironment().getFileName());
		
		problemJ.setEnvironment(SOME_FILE);
		assertEquals(SOME_FILE,problemJ.getEnvironment());
	}

	
	@Test
	public void testTimeout() throws MooshakContentException {
		assertEquals(1,created.getTimeout());
		assertEquals(2,problemJ.getTimeout());
		
		created.setTimeout(SOME_INT);
		created.save(); created.reopen();
		assertEquals(SOME_INT,created.getTimeout());
		
		problemJ.setTimeout(SOME_INT);
		assertEquals(SOME_INT,problemJ.getTimeout());
	}
	
	@Test
	public void testStaticCorrector() throws MooshakContentException {
		assertEquals(null,created.getStaticCorrector());
		assertEquals(null,problemJ.getStaticCorrector());
		
		created.setStaticCorrector(text);
		created.save(); created.reopen();
		assertEquals(text,created.getStaticCorrector());
		
		problemJ.setStaticCorrector(text);
		assertEquals(text,problemJ.getStaticCorrector());
	}
	
	@Test
	public void testDynamicCorrector() throws MooshakContentException {
		assertEquals(null,created.getDynamicCorrector());
		assertEquals(null,problemJ.getDynamicCorrector());
		
		created.setDynamicCorrector(text);
		created.save(); created.reopen();
		assertEquals(text,created.getDynamicCorrector());
		
		problemJ.setDynamicCorrector(text);
		assertEquals(text,problemJ.getDynamicCorrector());
	}

	public void testGetEnvironmentPathname() {
		assertEquals("",problemJ.getEnvironmentPathname());
	}

	@Test
	public void testGetHTMLstatement() throws MooshakContentException {
		
		assertNotNull(problemC.getHTMLstatement());
		assertNotNull(problemJ.getHTMLstatement());
		
		assertNull(created.getHTMLstatement());
		
	}
	
	
}
