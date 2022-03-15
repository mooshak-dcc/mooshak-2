package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.FileDescriptors;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.evaluation.SubmissionSecurity;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.evaluation.SubmissionSecurity.Level;

public class LanguageTest {
	
	private static final String WARNING = 
			"\n\nHave you run the 'prepare' script as root?\n\n";

	Language loaded;
	Language created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome("home");
		SafeExecution.setWebInf(CustomData.WEB_INF);
		
		createdPath =  Paths.get(CustomData.CONTEST_PATHNAME,"languages",
				"SOME_LANGUAGE");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Language.class);
		loaded= PersistentObject.openPath(CustomData.JAVA_PATHNAME);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}
	
	@Test
	public void testFatal() throws MooshakContentException {

		assertEquals("",loaded.getFatal());
		assertEquals("",created.getFatal());

		for(String text: CustomData.TEXTS) {
			loaded.setFatal(text);
			assertEquals(text,loaded.getFatal());

			created.setFatal(text);
			created.save(); created.reopen();
			assertEquals(text,created.getFatal());
		}
	}

	@Test
	public void testWarning() throws MooshakContentException {

		assertEquals("",loaded.getWarning());
		assertEquals("",created.getWarning());

		for(String text: CustomData.TEXTS) {
			loaded.setWarning(text);
			assertEquals(text,loaded.getWarning());

			created.setWarning(text);
			created.save(); created.reopen();
			assertEquals(text,created.getWarning());
		}
	}

	@Test
	public void testName() throws MooshakContentException {
		assertEquals("Java",loaded.getName());
		assertEquals("",created.getName());

		for(String text: CustomData.TEXTS) {
			loaded.setName(text);
			assertEquals(text,loaded.getName());

			created.setName(text);
			created.save(); created.reopen();
			assertEquals(text,created.getName());
		}		
	}
	
	@Test
	public void testExtension() throws MooshakContentException {
		assertEquals("java",loaded.getExtension());
		assertEquals("",created.getExtension());

		for(String text: CustomData.TEXTS) {
			loaded.setExtension(text);
			assertEquals(text,loaded.getExtension());

			created.setExtension(text);
			created.save(); created.reopen();
			assertEquals(text,created.getExtension());
		}
	}

	@Test
	public void testCompiler() throws MooshakContentException {
		assertEquals("jdk",loaded.getCompiler());
		assertEquals("",created.getCompiler());

		for(String text: CustomData.TEXTS) {
			loaded.setCompiler(text);
			assertEquals(text,loaded.getCompiler());

			created.setCompiler(text);
			created.save(); created.reopen();
			assertEquals(text,created.getCompiler());
		}
	}

 	@Test
	public void testCompile() throws MooshakContentException {
		assertEquals("/usr/bin/javac $file",loaded.getCompile());
		assertEquals("",created.getCompile());

		for(String text: CustomData.TEXTS) {
			loaded.setCompile(text);
			assertEquals(text,loaded.getCompile());

			created.setCompile(text);
			created.save(); created.reopen();
			assertEquals(text,created.getCompile());
		}
	}

 	@Test
	public void testExecute() throws MooshakContentException {
		assertEquals("/usr/bin/java -classpath . $name",loaded.getExecute());
		assertEquals("",created.getExecute());

		for(String text: CustomData.TEXTS) {
			loaded.setExecute(text);
			assertEquals(text,loaded.getExecute());

			created.setExecute(text);
			created.save(); created.reopen();
			assertEquals(text,created.getExecute());
		}
	}
 	
 	@Test
	public void testData() throws MooshakContentException {
 		assertTrue(6147483647L == loaded.getData());
		assertTrue(0 == created.getData());

		for(long value: CustomData.LONGS) {
			loaded.setData(value);
			assertTrue(value == loaded.getData());

			created.setData(value);
			created.save(); created.reopen();
			assertTrue(value == created.getData());
		}
	}

 	@Test
	public void testFork() throws MooshakContentException {
 		assertTrue(100 == loaded.getFork());
		assertTrue(0 == created.getFork());

		for(long value: CustomData.LONGS) {
			loaded.setFork(value);
			assertTrue(value == loaded.getFork());

			created.setFork(value);
			created.save(); created.reopen();
			assertTrue(value == created.getFork());
		}
	}
 	
 	@Test
	public void testOmit() throws MooshakContentException {
		assertEquals("",loaded.getOmit());
		assertEquals("",created.getOmit());

		for(String text: CustomData.TEXTS) {
			loaded.setOmit(text);
			assertEquals(text,loaded.getOmit());

			created.setOmit(text);
			created.save(); created.reopen();
			assertEquals(text,created.getOmit());
		}
	}
 	

	@Test
	public void testExpand() {
		
		Language language = new Language();
		
		Map<String,String> vars = new HashMap<String,String>();
		vars.put("home","/a/b/c");
		vars.put("program","Hello.java");
		vars.put("name","Hello");
		
		
		assertEquals("javac Hello.java",language.expand("javac $program", vars));
		assertEquals("java Hello",language.expand("java $name", vars));
		assertEquals("cd /a/b/c; java Hello",language.expand("cd $home; java $name", vars));
		
		assertEquals("echo 'you owe my $10'",language.expand("echo 'you owe my $10'",vars));
		
	}
	
	@Test
	public void testReuseUserId() throws MooshakException, IOException {
		String home = PersistentCore.getHome();
		
		PersistentCore.setHome("");
		assertEquals(WARNING,30001,loaded.reuseUserId(Paths.get("testFiles")));
		
		PersistentCore.setHome(home);
		
		assertEquals("Unexpected open file descriptors",
				0,FileDescriptors.countUnusual());
	}
	
	@Test
	public void testCompileValid() throws Exception {
		
		loaded.executeIgnoringFSNotifications(() -> testCompileValid2());
	}
	
	public void testCompileValid2() throws MooshakException, IOException {
		Submission submission =  PersistentObject.openPath(CustomData.SUBMISSION_PATHNAME);
		Problem problemJ = PersistentObject.openPath(CustomData.PROBLEM_PATHNAME);
		
		EvaluationParameters parameters = new EvaluationParameters();
		
		parameters.setProgram(submission.getProgram());
		parameters.setDirectory(submission.getPath());
		parameters.setProblem(problemJ);
		parameters.setLanguage(loaded);
		parameters.setSubmission(submission);
		
		Path objectFile = classFile(submission.getProgram());
		
		if(Files.exists(objectFile))
			Files.delete(objectFile);
		
		SubmissionSecurity security = 
				new SubmissionSecurity(parameters, Level.COMPILATION); 
		
		security.relax();
		
		assertFalse(Files.exists(objectFile));
		assertEquals(WARNING,"",loaded.compile(parameters));
		assertTrue(Files.exists(objectFile));
		
		long modified = Files.getLastModifiedTime(objectFile).toMillis(); 		
		assertEquals(WARNING,"",loaded.compile(parameters));
		assertTrue(Files.exists(objectFile));
		
		// check re-submission
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {		}
				
		assertEquals(WARNING,"",loaded.compile(parameters));
		
		assertTrue(Files.exists(objectFile));
		assertTrue( Files.getLastModifiedTime(objectFile).toMillis()> modified);
		
		security.tighten();
	}

	@Test
	public void testCompileError() throws MooshakException, IOException {

		Submission submission =  PersistentObject.openPath(
				CustomData.COMPILATION_ERROR_SUBMISSION_PATHNAME);
		Problem problemJ = PersistentObject.openPath(CustomData.PROBLEM_PATHNAME);
		
		EvaluationParameters parameters = new EvaluationParameters();
		
		parameters.setProgram(submission.getProgram());
		parameters.setDirectory(submission.getPath());
		parameters.setProblem(problemJ);
		parameters.setLanguage(loaded);
		parameters.setSubmission(submission);
		
		Path objectFile = classFile(submission.getProgram());
		
		if(Files.exists(objectFile))
			Files.delete(objectFile);

		assertFalse("".equals(loaded.compile(parameters)));		
	}
	
	static Pattern dotJava = Pattern.compile("\\.java$");
	
	private Path classFile(Path program) {
		
		Matcher matcher = dotJava.matcher(program.toString());
		Path objectFile = null;
		if(matcher.find())
			objectFile = Paths.get(matcher.replaceFirst(".class"));
		else
			fail("Not a java source file: "+ program);
		return PersistentCore.getAbsoluteFile(objectFile);
	}
}
