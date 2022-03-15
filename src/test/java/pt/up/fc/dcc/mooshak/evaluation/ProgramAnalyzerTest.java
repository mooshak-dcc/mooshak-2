package pt.up.fc.dcc.mooshak.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.FileDescriptors;
import pt.up.fc.dcc.mooshak.Threads;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Tests;

import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.ACCEPTED;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.COMPILE_TIME_ERROR;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.INVALID_EXIT_VALUE;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.INVALID_FUNCTION;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.MEMORY_LIMIT_EXCEEDED;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.OUTPUT_LIMIT_EXCEEDED;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.RUNTIME_ERROR;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.TIME_LIMIT_EXCEEDED;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.WRONG_ANSWER;


public class ProgramAnalyzerTest {

	private static Set<Submission> trash = new HashSet<>();
	private static Set<Runnable> undo = new HashSet<>();

	private static Contest contest;
	private static Submissions submissions;
	private static Path examples;	
	private static Problem problemZ; 
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistentObject.setHome(CustomData.HOME);
		contest = PersistentObject.openPath(CustomData.CONTEST);
		submissions = PersistentObject.openPath(CustomData.SUBMISSIONS_PATHNAME);

		// reduce timeout to speedup unit tests
		Languages languages = contest.open("languages");
		languages.setExecTimeout(1);
		languages.setRealTimeout(5);
		languages.setMaxData(5000000);
		
		Path homePath= PersistentObject.getHomePath();
		examples = homePath.resolve("data/configs/checks/team/resources");
		
		SafeExecution.setWebInf(CustomData.WEB_INF);
		
		int count = Runtime.getRuntime().availableProcessors();
		Threads.clearRegistredExpectedThreadNames();
		Threads.registerExpectedThreadName("pool-\\d+-thread-\\d+", count);
		Threads.registerExpectedThreadName("process reaper",count);
		
		makeProblemWithManyTests();
	}
	
	private static void makeProblemWithManyTests() 
			throws MooshakContentException, IOException {
		
		Problems problems = contest.open("problems");
		Problem problemC = problems.open("C"); 
		
		problemZ = problems.create("Z",Problem.class);
		
		Tests testsC = problemC.open("tests");
		Tests testsZ = problemZ.create("tests",Tests.class);
		
		List<pt.up.fc.dcc.mooshak.content.types.Test> testListC = 
				testsC.getChildren(
				 		pt.up.fc.dcc.mooshak.content.types.Test.class,false);
		pt.up.fc.dcc.mooshak.content.types.Test aTest = testListC.get(0);
		Path aTestPath = aTest.getAbsoluteFile();
		Path aTestInput = aTestPath.resolve(aTest.getInput().getFileName());
		Path aTestOutput = aTestPath.resolve(aTest.getOutput().getFileName());
		int processors = Runtime.getRuntime().availableProcessors();
		
		for(int i=1; i<= 2*processors; i++) {
			pt.up.fc.dcc.mooshak.content.types.Test test = 
					testsZ.create("T"+i, 
							pt.up.fc.dcc.mooshak.content.types.Test.class);
			
			Path testPath = test.getAbsoluteFile();
			
			test.setInput(aTestInput);
			Path testInput = testPath.resolve(test.getInput().getFileName());
			Files.copy(aTestInput,testInput);
			
			test.setOutput(aTestOutput);			
			Path testOutput = testPath.resolve(test.getOutput().getFileName());
			Files.copy(aTestOutput,testOutput);
		}
	}
	
	
	
	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void terUp() throws Exception {	
		
		for(Runnable runnable: undo)
			runnable.run();
	}
	
	@AfterClass
	public static void cleanUpAfterClass() throws MooshakContentException {
		for(Submission submission: trash)
			if(Files.exists(submission.getPath()))
				submission.delete();
		
		problemZ.delete();
	}
	
	
	@Test
	public void testInvokeCorrector() throws MooshakContentException, IOException {
		Path submissionPath = Paths.get(CustomData.SUBMISSION_PATHNAME);
		Submission submission = PersistentObject.open(submissionPath);
		
		ProgramAnalyzer analyzer = new ProgramAnalyzer(submission);
		Map<String,String> vars = new HashMap<>();
		EvaluationParameters parameters = new EvaluationParameters();
		String commandLine;
		
		parameters.setSubmission(submission);
		parameters.setProgram(Submission.getAbsoluteFile(submission.getProgram()));
		parameters.setDirectory(submission.getAbsoluteFile());
		parameters.setTimeout(2);

		try {
			commandLine = "/bin/true";
			analyzer.invokeCorretor(parameters, commandLine, vars);
		} catch (MooshakEvaluationException cause) {
			cause.printStackTrace();
			fail("unexpected exception");
		}

		try {
			commandLine = "/bin/false";
			analyzer.invokeCorretor(parameters, commandLine, vars);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {			
			assertEquals(Classification.PRESENTATION_ERROR,
					cause.getClassification());
			assertEquals("",cause.getFeedback());
			assertEquals(new Integer(0),cause.getMark());
		}

		commandLine = "/tmp/test";
		makeScript(commandLine,"#!/bin/bash\n echo message\n exit 2\n");		
		try {
			
			analyzer.invokeCorretor(parameters, commandLine, vars);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {			
			assertEquals(Classification.WRONG_ANSWER,cause.getClassification());
			assertEquals("message\n",cause.getMessage());
			assertEquals(new Integer(0),cause.getMark());
		}
		
		makeScript(commandLine,"#!/bin/bash\n >&2 echo feedback\n exit 2\n");		
		try {
			
			analyzer.invokeCorretor(parameters, commandLine, vars);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {			
			assertEquals(Classification.WRONG_ANSWER,
					cause.getClassification());
			assertEquals("feedback\n",cause.getFeedback());
			assertEquals(new Integer(0),cause.getMark());
		}
		
		// launch an shell as a process 
		makeScript(commandLine,"#!/bin/bash\n (>&2 echo feedback)\n exit 2\n");		
		try {
			
			analyzer.invokeCorretor(parameters, commandLine, vars);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {			
			assertEquals(Classification.WRONG_ANSWER,cause.getClassification());
			assertEquals("feedback\n",cause.getFeedback());
			assertEquals(new Integer(0),cause.getMark());
		}
		

		makeScript(commandLine,
				"#!/bin/bash\n echo message\n >&2 echo feedback\n exit -30\n");		
		try {
			
			analyzer.invokeCorretor(parameters, commandLine, vars);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {			
			assertEquals(Classification.ACCEPTED,cause.getClassification());
			assertEquals("message\n",cause.getMessage());
			assertEquals("feedback\n",cause.getFeedback());
			assertEquals(new Integer(30),cause.getMark());
		}
		
		vars.put("file", commandLine);
		
		makeScript(commandLine,	"#!/bin/bash\n echo $FILE\n exit -10 \n");		
		try {
			
			analyzer.invokeCorretor(parameters, commandLine, vars);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {			
			assertEquals(Classification.ACCEPTED,cause.getClassification());
			assertEquals(commandLine+"\n",cause.getMessage());
			assertEquals(new Integer(10),cause.getMark());
		}
		
		String args = "hello holla ola";
		makeScript(commandLine,	"#!/bin/bash\n echo $*\n exit -10 \n");		
		try {
			
			analyzer.invokeCorretor(parameters, commandLine+" "+args, vars);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {			
			assertEquals(Classification.ACCEPTED,cause.getClassification());
			assertEquals(args+"\n",cause.getMessage());
			assertEquals(new Integer(10),cause.getMark());
		}
		
		Path path = PersistentCore.getAbsoluteFile(submission.getProgram());
		String content = new String(Files.readAllBytes(path));
		vars.put("file", path.toString());
		
		makeScript(commandLine,	"#!/bin/bash\n cat  $FILE\n exit -10 \n");		
		try {
			
			analyzer.invokeCorretor(parameters, commandLine+" "+args, vars);
			fail("Exception expected");
		} catch (MooshakEvaluationException cause) {			
			assertEquals(Classification.ACCEPTED,cause.getClassification());
			assertEquals(content,cause.getMessage());
			assertEquals(new Integer(10),cause.getMark());
		}
		
		Files.deleteIfExists(Paths.get(commandLine));
	}
	
	private void makeScript(String pathname,String code) throws IOException {
		String commandLine = pathname;
		Path path = Paths.get(commandLine);
		Files.write(path, code.getBytes());
		Set<PosixFilePermission> permissions = new HashSet<>();
		
		for(PosixFilePermission permission: PosixFilePermission.values())
			permissions.add(permission);
		
		Files.setPosixFilePermissions(path, permissions);
	}
	

	@Test
	public void testInvokeCorrectorWithoutTermination() throws MooshakContentException, IOException {
		Path submissionPath = Paths.get(CustomData.SUBMISSION_PATHNAME);
		Submission submission = PersistentObject.open(submissionPath);
		
		ProgramAnalyzer analyzer = new ProgramAnalyzer(submission);
		Map<String,String> vars = new HashMap<>();
		EvaluationParameters parameters = new EvaluationParameters();
		
		parameters.setSubmission(submission);
		parameters.setProgram(Submission.getAbsoluteFile(submission.getProgram()));
		parameters.setDirectory(submission.getAbsoluteFile());
		parameters.setTimeout(2);

		try {
			analyzer.invokeCorretor(parameters, examples.resolve("static_corretor_loop")
					.toString(), vars);
		} catch (MooshakEvaluationException cause) {
			assertEquals(Classification.TIME_LIMIT_EXCEEDED, cause.classification);
		}
	}
	

	@Test
	public void testCompilationErrorInC() throws Exception {	
		testWith("C", "comp_err.c",Classification.COMPILE_TIME_ERROR);
	}

	@Test
	public void testRuntimeErrorInC() throws Exception {
						
		testWith("C", "exec_err.c",Classification.RUNTIME_ERROR);
	}

	@Test
	public void testLargeProgramInC() throws Exception {
						
		testWith("C", "large.c",Classification.RUNTIME_ERROR);
	}
	
	@Test
	public void testMemoryLimitExceededInC() throws Exception {
						
		testWith("C", "out_of_memory.c",MEMORY_LIMIT_EXCEEDED,INVALID_FUNCTION);
	}
	
	@Test
	public void testInvalidExitInC() throws Exception {
								
		testWith("C", "invalid_exit.c",INVALID_EXIT_VALUE);
	}
	
	@Test
	public void testForkBombInC() throws Exception {
								
		testWith("C", "fork.c",TIME_LIMIT_EXCEEDED,INVALID_FUNCTION);
	}

	@Test
	public void testTimeLimitExceededInC()  throws Exception {
		testWith("C", "loop.c",TIME_LIMIT_EXCEEDED);
	}
	
	@Test
	public void testTimeLimitExceededInCWithManyTests() throws Exception {
		
		submissions.setRunAllTests(true);
		
		long start1 = System.currentTimeMillis();
		testWith("Z", "loop.c",TIME_LIMIT_EXCEEDED,INVALID_FUNCTION);
		long duration1 = System.currentTimeMillis()-start1;
		
		// System.out.println("duration 1:"+duration1);
		
		submissions.setRunAllTests(false);
		
		long start2 = System.currentTimeMillis();
		testWith("Z", "loop.c",TIME_LIMIT_EXCEEDED);
		long duration2 = System.currentTimeMillis()-start2;

		// System.out.println("duration 1:"+duration1);
		// System.out.println("duration 2:"+duration2);

		
		assertTrue( (double) duration1 / (double) duration2 >  1.8D );
	}

	@Test
	public void testOutputLimitExceededInC() throws Exception {
						
		testWith("C", "long_output.c",OUTPUT_LIMIT_EXCEEDED);
	}

	@Test
	public void testFileNamesWithSpacesInC() throws Exception {
						
		testWith("C", "filename with spaces.c",WRONG_ANSWER);
	}

	@Test
	public void testAcceptedInC() throws Exception {
						
		testWith("C", "accepted.c",ACCEPTED);
	}
	
	@Test
	public void testStaticEvaluatorInC() throws Exception {
		
		Problem problemC = contest.open("problems").open("C");
		
		
		
		problemC.setStaticCorrector(
				"pt.up.fc.dcc.mooshak.evaluation.special.HelloEvaluator");
		
		
		testWith("C", "accepted.c",WRONG_ANSWER);

		testWith("C", "return1.c",INVALID_EXIT_VALUE); // has an "hello"
		
		
		problemC.setStaticCorrector("");
		
		testWith("C", "accepted.c",ACCEPTED);
	}
	
	/**
	 * Should not compile due to insufficient memory
	 * @throws Exception
	 */
	@Test
	public void testInsufficientMemoryToCompileInC() throws Exception {
		Languages languages = PersistentObject.openPath(CustomData.LANGUAGES);
		int maxData = languages.getMaxData();
		
		undo.add( () -> languages.setMaxData(maxData) ); // revert max data
		languages.setMaxData(50*1024); 
		testWith("C", "accepted.c",COMPILE_TIME_ERROR);
	}


	@Test
	public void testCompilationErrorInCPP() throws Exception {
						
		testWith("M", "comp_err.cpp",COMPILE_TIME_ERROR);
	}
	
	@Test
	public void testCerrBombInCPP() throws Exception {
						
		testWith("M", "cerr_bomb.cpp",TIME_LIMIT_EXCEEDED,MEMORY_LIMIT_EXCEEDED);
	}
	
	@Test
	public void testCompilationBomb1InCPP() throws Exception {
						
		testWith("M", "comp_bomb1.cpp",COMPILE_TIME_ERROR,INVALID_FUNCTION);
	}
	
	@Test
	public void testCompilationBomb2InCPP() throws Exception {
						
		testWith("M", "comp_bomb2.cpp",COMPILE_TIME_ERROR,INVALID_FUNCTION);
	}
	
	@Test
	public void testCompilationBomb3InCPP() throws Exception {
						
		testWith("M", "a1.cpp",COMPILE_TIME_ERROR,INVALID_FUNCTION);
	}
	
	@Test
	public void testCompilationBomb4InCPP() throws Exception {
						
		testWith("M", "a2.cpp",COMPILE_TIME_ERROR,INVALID_FUNCTION);
	}
	
	@Test
	public void testCompilationBomb5InCPP() throws Exception {
						
		testWith("M", "a3.cpp",COMPILE_TIME_ERROR,INVALID_FUNCTION);
	}
	
	@Test
	public void testCompilationBomb6InCPP() throws Exception {
						
		testWith("M", "a4.cpp",WRONG_ANSWER);
	}
	
	@Test
	public void testExecutionErrorInCPP() throws Exception {
						
		testWith("M", "exec_err.cpp",RUNTIME_ERROR);
	}

	@Test
	public void testLargeProgramInCPP() throws Exception {
						
		testWith("M", "large.cpp",RUNTIME_ERROR);
	}

	@Test
	public void testLoopInCPP() throws Exception {
						
		testWith("M", "loop.cpp",TIME_LIMIT_EXCEEDED);
	}

	@Test
	public void testLongOutputInCPP() throws Exception {
				
		testWith("M", "long_output.cpp",OUTPUT_LIMIT_EXCEEDED);
	}

	@Test
	public void testAcceptedInCPP() throws Exception {
		
		testWith("M", "accepted.cpp",ACCEPTED);
	}
	
	/**
	 * Should not compile due to insufficient memory
	 * @throws Exception
	 */
	@Test
	public void testInsufficientMemoryToCompileInCPP() throws Exception {
		Languages languages = PersistentObject.openPath(CustomData.LANGUAGES);
		int maxData = languages.getMaxData();
		
		undo.add( () -> languages.setMaxData(maxData) ); // revert max data
		languages.setMaxData(120*1024); 
		testWith("M", "accepted.cpp",COMPILE_TIME_ERROR);
	}

	// Java
	
	@Test
	public void testCompilationErrorInJava() throws Exception {
						
		testWith("J", "comp_err.java",COMPILE_TIME_ERROR);
	}
	

	@Test
	public void testRuntimeErrorInJava() throws Exception {
						
		testWith("J", "exec_err.java",RUNTIME_ERROR);
	}

	@Test
	public void testLargeMemoryInJava() throws Exception {
						
		testWith("J", "large.java",RUNTIME_ERROR);
	}

	@Test
	public void testForkBombInJava() throws Exception {
						
		testWith("J", "fork.java",TIME_LIMIT_EXCEEDED,RUNTIME_ERROR);
	}

	@Test
	public void testInfiniteLoopJava() throws Exception {
						
		testWith("J", "loop.java",TIME_LIMIT_EXCEEDED);
	}

	@Test
	public void testLongOutputJava() throws Exception {
						
		testWith("J", "long_output.java",OUTPUT_LIMIT_EXCEEDED);
	}

	@Test
	public void testAcceptedInJava() throws Exception {
						
		testWith("J", "accepted.java",ACCEPTED);
	}
	
	/**
	 * Should not compile due to insufficient memory.
	 * Java has its own limit that overrides languages 
	 * @throws Exception
	 */
	@Test
	public void testInsufficientMemoryToCompileInJava() throws Exception {
		Language java = PersistentObject.openPath(CustomData.JAVA_PATHNAME);
		long maxData = java.getData();
		
		undo.add( () -> java.setData(maxData) ); // revert max data
		java.setData(120*1024L); 
		testWith("J", "accepted.java",COMPILE_TIME_ERROR);
	}
	
	/**
	 * Should not compile due to insufficient memory
	 * @throws Exception
	 */
	@Test
	public void testInsufficientTimeToCompileInJava() throws Exception {
		Languages languages = PersistentObject.openPath(CustomData.LANGUAGES);
		int timeout = languages.getCompTimeout();
		
		undo.add( () -> languages.setCompTimeout(timeout) ); // revert max data
		languages.setCompTimeout(1); 
		testWith("J", "accepted.java",COMPILE_TIME_ERROR);
	}
	
	// Pascal

	@Test
	public void testCompilationErrorInPascal() throws Exception {
						
		testWith("P", "comp_err.pas",COMPILE_TIME_ERROR);
	}	

	@Test
	public void testRuntimeErrorInPascal() throws Exception {
						
		testWith("P", "exec_err.pas",RUNTIME_ERROR);
	}

	@Test
	public void testLargeMemoryInPascal() throws Exception {
						
		testWith("P", "large.pas",MEMORY_LIMIT_EXCEEDED, OUTPUT_LIMIT_EXCEEDED);

	}

	
	// @Test // Cannot fork in Pascal
	public void testForkBombInPascal() throws Exception {
						
		testWith("P", "fork.pas",TIME_LIMIT_EXCEEDED);
	}

	@Test
	public void testInfiniteLoopPascal() throws Exception {
						
		testWith("P", "loop.pas",TIME_LIMIT_EXCEEDED);
	}

	@Test
	public void testLongOutputPascal() throws Exception {
						
		testWith("P", "long_output.pas",OUTPUT_LIMIT_EXCEEDED);
	}

	@Test
	public void testAcceptedInPascal() throws Exception {
						
		testWith("P", "accepted.pas",ACCEPTED);
	}	
	
	/**
	 * Should not compile due to insufficient memory
	 * @throws Exception
	 */
	@Test
	public void testInsufficientMemoryToCompileInPascal() throws Exception {
		Languages languages = PersistentObject.openPath(CustomData.LANGUAGES);
		int maxData = languages.getMaxData();
		
		undo.add( () -> languages.setMaxData(maxData) ); // revert max data
		languages.setMaxData(60*1024); 
		testWith("P", "accepted.pas",COMPILE_TIME_ERROR);
	}

	/**
	 * Should not compile due to insufficient time
	 * @throws Exception
	 */
	@Test
	public void testInsufficientTimeToCompileInPascal() throws Exception {
		Languages languages = PersistentObject.openPath(CustomData.LANGUAGES);
		int timeout = languages.getCompTimeout();
		
		undo.add( () -> languages.setCompTimeout(timeout) ); // revert max data
		languages.setCompTimeout(0); 
		testWith("P", "accepted.pas",COMPILE_TIME_ERROR);
	}
	
	/**
	 * If execution produces a segmentation fault before reading input
	 * then it should produce Runtime Error rather than Requires Reevaluation
	 * 
	 * @throws Exception 
	 */
	@Test
	public void testSegmentationFaultBeforeReadingInput() throws Exception {
		List<String> inputs = new ArrayList<>();
		
		inputs.add(readExampleFileContent("segmentation_fault_in.txt"));
		
		testWith("C", "segmentation_fault.c",inputs,RUNTIME_ERROR);
	}
	
	
	@Test
	public void testStrangeInteration() throws Exception  {
		testWith("C", "long_output.c",OUTPUT_LIMIT_EXCEEDED);
		testWith("J", "accepted.java",ACCEPTED);
	}
	
	private void testWith(
			String problemId,
			String programName,
			Classification... classifications) throws Exception {
		
		testWith(problemId,programName,null,classifications);
	}
	
	private void testWith(
			String problemId,
			String programName,
			List<String> inputs,
			Classification... classifications) throws Exception {
		ProgramAnalyzer analyzer = getAnalyzer(problemId, programName, inputs);
		Submission submission = analyzer.getSubmission();
	
		assertTrue(Threads.withoutUnexpectedThreads(analyzer::analyze));
				
		Classification obtained = submission.getClassify();
		boolean match = false;
		
		for(Classification classification: classifications)
			if(classification == obtained)
				match = true;
		
		if(!match) {
			System.out.println(programName);
			System.out.print("\texpected:");
			String sep = "";
			for(Classification classification: classifications) {
				System.out.print(sep+classification);
				sep = " or ";
			}
			System.out.println("");
			
			System.out.println("\tobtained:"+submission.getClassify());
			System.out.println("\tobservations:"+
					getTruncated(submission.getObservations()));
		}
		

		submission.delete();
		
		assertEquals(true,match);
		
		assertEquals("Unusual file descriptors",0,FileDescriptors.countUnusual());
		
	}

	private static int TRUNCATE_AT = 10000;
	
	private String getTruncated(String text) {
		if(text.length() < TRUNCATE_AT)
			return text;
		else
			return text.substring(0,TRUNCATE_AT)+
					(text.length()>TRUNCATE_AT?"...":"");
	}
	
	
	private ProgramAnalyzer getAnalyzer(
			String problemId,
			String programName,
			List<String> inputs)
			
			throws Exception {
		
		String id = contest.getTransactionId(CustomData.TEAM_ID,problemId);
		
		Submission submission = submissions.create(id,Submission.class);
		String programCode = readExampleFileContent(programName);
		
		submission.receive(
				CustomData.TEAM_ID,
				null,
				programName, 
				programCode.getBytes(), 
				problemId, 
				inputs, 
				(inputs == null));
		
		trash.add(submission);
		return new ProgramAnalyzer(submission);
	}
	
	private static final Charset CS = Charset.defaultCharset();
	
	private String readExampleFileContent(String name) throws IOException {
		StringBuilder builder = new StringBuilder();
		Path path = examples.resolve(name);
		try(BufferedReader buffer = Files.newBufferedReader(path, CS)){
			String line;
			while((line = buffer.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}
			
		} 
		
		return builder.toString();
	}
	
}
