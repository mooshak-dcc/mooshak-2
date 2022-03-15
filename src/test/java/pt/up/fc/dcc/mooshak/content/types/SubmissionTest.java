package pt.up.fc.dcc.mooshak.content.types;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submission.State;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class SubmissionTest { 
	
	Submission loaded;
	Submission created;
	Submission other;
	
	static Path createdPath;
	static Path somePath;
	
	static Contest contest;
	static Submissions submissions;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistentObject.setHome(CustomData.HOME);
		
		Contests contests = PersistentObject.openPath(CustomData.CONTESTS);
		contest = contests.open(CustomData.CONTEST_ID);
		submissions = contest.open("submissions");
		
		createdPath = Files.createTempDirectory("submission");
		somePath = createdPath.resolve(Paths.get("some_file.txt"));
		
		SafeExecution.setWebInf(CustomData.WEB_INF);
	}
		
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Submission.class);
		loaded = PersistentObject.openPath(CustomData.SUBMISSION_PATHNAME);
		
		String id = contest.getTransactionId(CustomData.TEAM_ID,CustomData.PROBLEM_ID);
		other = submissions.create(id, Submission.class);
	}
	
	@After
	public void cleanup() throws MooshakContentException { 		
 		other.delete();
 		created.delete();
 		loaded.reopen();
	}
	
	@Test
	public void testConsider() throws MooshakContentException {
		assertEquals(true, created.isConsider());
		assertEquals(true, other.isConsider());

		created.setConsider(false);
		created.save(); created.reopen();
		assertEquals(false, created.isConsider());

		other.setConsider(false);
		other.save(); other.reopen();
		assertEquals(false, other.isConsider());
	}
	
	@Test
	public void testDate() throws MooshakContentException {
		assertEquals(new Date(0),created.getDate());
		assertEquals(1284137034,loaded.getDate().getTime()/1000);

		for(Date date: CustomData.DATES) {
			created.setDate(date);
			created.save(); created.reopen();
			assertEquals(date.getTime()/1000, created.getDate().getTime()/1000);

			loaded.setDate(date);
			assertEquals(date, loaded.getDate());
		}
	}
	
	@Test
	public void testTime() throws MooshakContentException {
		assertEquals(new Date(0),created.getTime());
		assertEquals(5075034,loaded.getTime().getTime()/1000);

		for(Date date: CustomData.DATES) {
			created.setTime(date);
			created.save(); created.reopen();
			assertEquals(date.getTime()/1000, created.getTime().getTime()/1000);

			loaded.setTime(date);
			assertEquals(date, loaded.getTime());
		}
	}
	
	@Test
	public void testProblem() throws IOException, MooshakContentException {
		Problems problems = other.open("../../problems");
		assertEquals(problems.open("P"), loaded.getProblem());
	}
	
	@Test
	public void testTeam() throws MooshakContentException, IOException {
		Team team = (Team) loaded.getTeam();
		
		assertEquals("team",team.getPath().getFileName().toString());
		assertEquals(null,created.getTeam());
		
		Groups groups = other.open("../../groups");
		Team copy = groups.find("team");
		
		loaded.setTeam(copy);
		assertEquals(copy,loaded.getTeam());
		
	}
	
	@Test 
	public void testTeamName() throws MooshakContentException {
		Team team = (Team) loaded.getTeam();
		String name = team.getIdName().toString();
		
		assertEquals(name,loaded.getTeamId());
	}
	
	
	@Test
	public void testClassification() throws MooshakContentException, IOException {
		assertEquals(Submission.Classification.REQUIRES_REEVALUATION,
				created.getClassify());
		assertEquals(Submission.Classification.WRONG_ANSWER,
				loaded.getClassify());
		
		for(Classification classification: Classification.values()) {
			
			created.setClassify(classification);
			created.save(); created.reopen();
			assertEquals(classification,created.getClassify());
			
			assertTrue(CustomData.checkContent(created, "Classify",
					classification.toString()));
			
			loaded.setClassify(classification);
			assertEquals(classification,loaded.getClassify());
			
		}		
	}
	
	@Test
	public void testMark() throws MooshakContentException {
		assertEquals(0, created.getMark());
		assertEquals(0, loaded.getMark());

		for(int value: CustomData.INTS) {
			created.setMark(value);
			created.save(); created.reopen();
			assertEquals(value, created.getMark());

			loaded.setMark(value);
			assertEquals(value, loaded.getMark());
		}
	}

	@Test
	public void testSize() throws MooshakContentException {
		assertEquals(0, created.getSize());
		assertEquals(97, loaded.getSize());

		for(int value: CustomData.INTS) {
			created.setSize(value);
			created.save(); created.reopen();
			assertEquals(value, created.getSize());

			loaded.setSize(value);
			assertEquals(value, loaded.getSize());
		}
	}
	
	@Test
	public void testObservations() throws MooshakContentException {
		assertEquals("", created.getObservations());
		assertEquals("", loaded.getObservations());

		for(String text: CustomData.TEXTS) {
			created.setObservations(text);
			created.save(); created.reopen();
			assertEquals(text, created.getObservations());

			loaded.setObservations(text);
			assertEquals(text, loaded.getObservations());
		}
	}
	
	@Test
	public void testState() throws MooshakContentException, IOException {
		assertEquals(Submission.State.PENDING, created.getState());
		assertEquals(Submission.State.PENDING, loaded.getState());

		for(State state: State.values()) {
			
			created.setState(state);
			created.save(); created.reopen();
			assertEquals(state,created.getState());
			
			assertTrue(CustomData.checkContent(created, "State", 
					state.toString().toLowerCase()));
			
			loaded.setState(state);
			assertEquals(state,loaded.getState());
		}		

	}
	
	@Test
	public void testProgram() throws IOException, MooshakContentException {

		assertEquals(null,created.getProgram());
		assertEquals(loaded.getPath().resolve("Bug.java"),loaded.getProgram());
		
		created.setProgram(somePath);
		created.save(); created.reopen();
		assertEquals(somePath,created.getProgram());
		
		loaded.setProgram(somePath);
		Path expected = loaded.getPath().resolve(somePath.getFileName());
		assertEquals(expected,loaded.getProgram());
		
	}
	
	@Test
	public void testReportPath() throws MooshakContentException {
		Reports reports = created.open("reports");
		assertEquals(0,reports.getContent().size());
		assertEquals(loaded.getPath().resolve("1.html"),loaded.getReportPath());
		
		created.setReportPath(somePath);
		created.save(); created.reopen();
		assertEquals(somePath.getFileName(),
				created.getReportPath().getFileName());
		
		loaded.setReportPath(somePath);
		Path expected = loaded.getPath().resolve(somePath.getFileName());
		assertEquals(expected,loaded.getReportPath());
	}
	
	@Test
	public void testElapsed() throws MooshakContentException {
		assertTrue(created.getElapsed() == 0);
		assertTrue(loaded.getElapsed() == 0);

		for(int value: CustomData.INTS) {
			created.setElapsed(value);
			created.save(); created.reopen();
			assertTrue(created.getElapsed() == value);

			loaded.setElapsed(value);
			assertTrue(loaded.getElapsed() == value);
		}
	}

	@Test
	public void testCPU() throws MooshakContentException {
		assertTrue(created.getCpu() == 0);
		assertTrue(loaded.getCpu() == 0.060);

		for(double value: CustomData.DOUBLES) {
			created.setCpu(value);
			created.save(); created.reopen();
			assertTrue(created.getCpu() == value);

			loaded.setCpu(value);
			assertTrue(loaded.getCpu() == value);
		}
	}

	@Test
	public void testMemory() throws MooshakContentException {
		assertTrue(created.getMemory() == 0);
		assertTrue(loaded.getMemory() == 9500);

		for(double value: CustomData.DOUBLES) {
			created.setMemory(value);
			created.save(); created.reopen();
			assertTrue(created.getMemory() == value);

			loaded.setMemory(value);
			assertTrue(loaded.getMemory() == value);
		}
	}

	@Test
	public void testFeedback() throws MooshakContentException {
		assertEquals("",created.getFeedback());
		assertEquals("",loaded.getFeedback());

		for(String text: CustomData.TEXTS) {
			created.setFeedback(text);
			created.save(); created.reopen();
			assertEquals(text,created.getFeedback());

			loaded.setFeedback(text);
			assertEquals(text,loaded.getFeedback());
		}
	}
	
	@Test
	public void testProblemId() {
		assertEquals("P", loaded.getProblemId());
		assertEquals(null, created.getProblemId());
		
		for(String text: CustomData.TEXTS) {
			created.setProblemId(text);
			assertEquals(text, created.getProblemId());
		}
	}
	
	@Test
	public void testGetContest() throws MooshakContentException {
		assertEquals(PersistentObject.openPath(CustomData.CONTEST_PATHNAME),loaded.getContest());
		
		try {
			assertEquals(null,created.getContest());
			fail("Exception expected");
		} catch(MooshakContentException e) {}
	}
	
	@Test
	public void testGetContestId() {
		assertEquals("proto_icpc", loaded.getContestId());
		assertEquals(null, created.getContestId());
	}
	
	@Test
	public void testReceiveEmpty() throws MooshakException {

		other.receive(
 				CustomData.TEAM_ID, 
 				null,
 				CustomData.HELLO_NAME, 
 				CustomData.HELLO_CODE.getBytes(), 
 				CustomData.NO_PROBLEM_ID, 
 				CustomData.NO_INPUT,
 				CustomData.CONSIDER);
 		
 		assertEquals(State.PENDING,other.getState());
 		assertEquals(Classification.EVALUATING,other.getClassify());
 			}
	
	@Test
	public void testEvaluateHello() throws MooshakException, IOException  {

		other.receive(
 				CustomData.TEAM_ID, 
 				null,
 				CustomData.HELLO_NAME, 
 				CustomData.HELLO_CODE.getBytes(), 
 				CustomData.NO_PROBLEM_ID, 
 				CustomData.SINGLE_EMPTY_INPUT,
 				CustomData.DONT_CONSIDER);
 		 		
 		other.analyze();
 		
 		assertEquals(State.PENDING,other.getState());
 		if(other.getClassify() != Classification.ACCEPTED)
 			System.out.println(other.getObservations());
 		assertEquals(Classification.ACCEPTED ,other.getClassify());
 		

 		Map<Integer,String> outputs = other.getUserOutputs();
 		assertEquals("Just 1 output file",1,outputs.keySet().size());
 		assertEquals("Hello Mooshak",outputs.get(0));
 		
 	}
	
	@Test
	public void testEvaluateProblemJ() throws MooshakException, IOException  {

		Problem problem = PersistentObject.openPath(CustomData.PROBLEM_J_PATHNAME);
		Path program = problem.getSolutions().iterator().next();
		String name = program.getName(program.getNameCount()-1).toString();
		String code = PersistentObject
				.getRelativeFileContentGuessingCharset(program);
		
		other.receive(
 				CustomData.TEAM_ID, 
 				null,
 				name, 
 				code.getBytes(), 
 				"J", 
 				null,
 				CustomData.DONT_CONSIDER);
 		 		
 		other.analyze();
 		
 		assertEquals(State.PENDING,other.getState());
 		assertEquals(Classification.ACCEPTED ,other.getClassify());

	}
	
	@Test
	public void testEvaluateAdd() throws MooshakException  {
		
		other.receive(
 				CustomData.TEAM_ID, 
 				null,
 				CustomData.ADD_NAME, 
 				CustomData.ADD_CODE.getBytes(), 
 				CustomData.NO_PROBLEM_ID, 
 				CustomData.NUMBER_PAIRS_INPUT,
 				CustomData.DONT_CONSIDER);
 		 		
 		other.analyze();
 		
 		assertEquals(State.PENDING,other.getState());
 		assertEquals(Classification.ACCEPTED ,other.getClassify());
 		
 		Map<Integer,String> outputs = other.getUserOutputs();
 		for(int count: outputs.keySet()) {
 			String inputData = CustomData.NUMBER_PAIRS_INPUT.get(count);
 			try(Scanner in = new Scanner(new StringReader(inputData))) {
 				int a = in.nextInt();
 				int b = in.nextInt();

 				
 				String output = outputs.get(count);
 	 			assertEquals((a+"+"+b),a+b,Integer.parseInt(output));
 			}
 		}
 		assertEquals(CustomData.NUMBER_PAIRS_INPUT.size(),outputs.size());

	}
	
	@Test
	public void testEvaluateAdd2() throws MooshakException, IOException  {
 		other.receive(
 				CustomData.TEAM_ID, 
 				null,
 				CustomData.ADD_NAME, 
 				CustomData.ADD_CODE.getBytes(), 
 				CustomData.NO_PROBLEM_ID, 
 				CustomData.NUMBER_PAIRS_INPUT,
 				CustomData.DONT_CONSIDER);
 		 		
 		other.analyze();
 		
 		assertEquals(State.PENDING,other.getState());
 		assertEquals(Classification.ACCEPTED ,other.getClassify());
 		
 		Iterator<Path> outputFiles = 
 				other.getUserTestData().getOutputFiles().iterator();
 		int length = 0;
 		while(outputFiles.hasNext()) {
 			length++;
 			Path path = outputFiles.next();
 			String inputData = CustomData.NUMBER_PAIRS_INPUT.get(orderOfTest(path));
 			try(Scanner in = new Scanner(new StringReader(inputData))) {
 				int a = in.nextInt();
 				int b = in.nextInt();

 				List<String> output = Files.readAllLines(
 					path,
 	 				Charset.defaultCharset());
 				assertEquals("Just one line in the output",1,output.size());
 				assertEquals((a+"+"+b),a+b,Integer.parseInt(output.get(0)));
 			}
 		}
 		assertEquals(CustomData.NUMBER_PAIRS_INPUT.size(),length);

 	}

	@Test public void testGetLine() throws MooshakContentException {
		Map<String, String> line = loaded.getRow();
		
		assertEquals("Wrong Answer",line.get("classification"));
		assertEquals("pending",		line.get("state"));
		assertEquals("team",		line.get("team"));
		assertEquals("myGroup",		line.get("group"));
		assertEquals("P",			line.get("problem"));
		assertEquals("058 17:43",	line.get("time"));
	}
	
	
	/**
	 * Runs will be randomly ordered. Get their order from the suffix
	 * @param file
	 * @return
	 */
	private int orderOfTest(Path file) {
		String fileName = Filenames.getSafeFileName(file);
		return Integer.parseInt(fileName.substring(3,fileName.indexOf('.')));
	}
	
	
	private static final int AVAILABLE = Runtime.getRuntime().availableProcessors();
	private static final ExecutorService POOL = Executors.newFixedThreadPool(AVAILABLE);
	
	@Test
	public void testGetTeamsConcurrently() throws MooshakContentException {
		
		Contest contest = PersistentObject.openPath(CustomData.CONTEST);
		
		Submissions submissions = contest.open("submissions");
		
		List<Future<Team>> futures = new ArrayList<>();
		List<String> submissionIds = new ArrayList<>();
		
		// 500 requests for submissions
		for (int i = 0; i < 500; i++) {
			for (PersistentObject submissionPO : submissions.getChildren(true)) {
				final Submission submission = (Submission) submissionPO;
				
				Callable<Team> task = () -> {
					Team team = (Team) submission.getTeam();
					return team;
				};
				
				Future<Team> future = POOL.submit(task);
				futures.add(future);
				submissionIds.add(submission.getIdName());
			}
			
		}
		
		int i = 0;
		for (Future<Team> future : futures) {
			try {
				if (future.get() == null)
					System.out.println("Team not found: " + submissionIds.get(i));
			} catch (InterruptedException e) {
				fail("Thread interrupted");
			} catch (ExecutionException e) {
				fail("Thread exception: " + e.getMessage());
			}
			
			i++;
		}
		
	}
}
