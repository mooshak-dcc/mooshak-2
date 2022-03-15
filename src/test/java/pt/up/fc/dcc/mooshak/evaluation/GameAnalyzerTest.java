package pt.up.fc.dcc.mooshak.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.ACCEPTED;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.COMPILE_TIME_ERROR;
import static pt.up.fc.dcc.mooshak.content.types.Submission.Classification.WRONG_ANSWER;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.FileDescriptors;
import pt.up.fc.dcc.mooshak.Threads;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;

/**
 * Set of tests for game analysis
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class GameAnalyzerTest {

	private static Set<Submission> trash = new HashSet<>();
	private static Set<Runnable> undo = new HashSet<>();

	private static Contest game_contest;
	private static Submissions game_submissions;
	private static Path examples;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistentObject.setHome(CustomData.HOME);
		game_contest = PersistentObject.openPath(CustomData.GAME_CONTEST);
		game_submissions = PersistentObject.openPath(CustomData.GAME_SUBMISSIONS_PATHNAME);

		Path homePath = PersistentObject.getHomePath();
		examples = homePath.resolve("data/configs/checks/team/resources");

		SafeExecution.setWebInf(CustomData.WEB_INF);

		int count = Runtime.getRuntime().availableProcessors();
		Threads.clearRegistredExpectedThreadNames();
		Threads.registerExpectedThreadName("pool-\\d+-thread-\\d+", count);
		Threads.registerExpectedThreadName("process reaper", count);
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearUp() throws Exception {

		for (Runnable runnable : undo)
			runnable.run();
	}

	@AfterClass
	public static void cleanUpAfterClass() throws MooshakContentException {
		for (Submission submission : trash)
			if (Files.exists(submission.getPath()))
				submission.delete();
	}

	@Test
	public void testGameJava() throws Exception {
		testWith("A", "TTTBot.java", 
				Arrays.asList("00000000_A_test_java"),
				ACCEPTED);
	}

	@Test
	public void testGameWrongAnswer() throws Exception {

		testWith("A", "TTTBotWA.java", 
				Arrays.asList("00000000_A_test_java"),
				WRONG_ANSWER);
	}

	@Test
	public void testGameCompErr() throws Exception {

		testWith("A", "TTTBotCTE.java",
				Arrays.asList("00000000_A_test_java"),
				COMPILE_TIME_ERROR);
	}

	@Test
	public void testGameSameClassname() throws Exception {
		testWith("A", "MyTicTacToePlayer.java", 
				Arrays.asList("00000000_A_test_java"),
				ACCEPTED);
	}
	
	@Test
	public void testGameNoOpponents() throws Exception {
		
		testWith("A", "TTTBot.java", Arrays.asList(), ACCEPTED);
	}
	
	@Test
	public void testGameSubmission() throws Exception {
		
		testWith("A", "TTTBot.java", null, ACCEPTED);
	}

	/*@Test
	public void testParallelGame() throws Exception {
		
		ExecutorService executor = Executors.newFixedThreadPool(1);
		
		final String problemId = "A";
		final String programName = "TTTBot.java";
		final List<String> inputs = Arrays.asList("00000000_A_test");
		final Classification[] classifications = new Classification[] {ACCEPTED};
		
		Callable<String> c = () -> {
			ProgramAnalyzer analyzer;
			try {
				analyzer = getAnalyzer(problemId, programName, inputs);
			} catch (Exception e) {
				return "Error: " + e.getMessage();
			}
			Submission submission = analyzer.getSubmission();

			if (!Threads.withoutUnexpectedThreads(analyzer::analyze))
				return "Fail: Unexpected threads";

			Classification obtained = submission.getClassify();
			boolean match = false;

			for (Classification classification : classifications)
				if (classification == obtained)
					match = true;

			if (!match) {
				System.out.println(programName);
				System.out.print("\texpected:");
				String sep = "";
				for (Classification classification : classifications) {
					System.out.print(sep + classification);
					sep = " or ";
				}
				System.out.println("");

				System.out.println("\tobtained:" + submission.getClassify());
				System.out.println("\tobservations:" + getTruncated(submission.getObservations()));
			}

			trash.add(submission);

			if (!match)
				return "Fail: Classification do not match";
			
			int unusualFd = FileDescriptors.countUnusual();
			if (unusualFd > 0)
				return "Fail: Unusual file descriptors " + unusualFd;
			
			return null;
		};
		
		List<Future<String>> futures = new ArrayList<>();
		for (int i = 0; i < 5; i++) {
			futures.add(executor.submit(c));
			Thread.sleep(1000);
		}

		for (Future<String> future: futures) {
			String s = future.get();
			if (s != null)
				fail(s);
		}
	}*/

	private void testWith(String problemId, String programName, List<String> inputs,
			Classification... classifications) throws Exception {
		
		ProgramAnalyzer analyzer = getAnalyzer(problemId, programName, inputs);
		Submission submission = analyzer.getSubmission();

		assertTrue(Threads.withoutUnexpectedThreads(analyzer::analyze));

		Classification obtained = submission.getClassify();
		boolean match = false;

		for (Classification classification : classifications)
			if (classification == obtained)
				match = true;

		if (!match) {
			System.out.println(programName);
			System.out.print("\texpected:");
			String sep = "";
			for (Classification classification : classifications) {
				System.out.print(sep + classification);
				sep = " or ";
			}
			System.out.println("");

			System.out.println("\tobtained:" + submission.getClassify());
			System.out.println("\tobservations:" + getTruncated(submission.getObservations()));
		}

		trash.add(submission);

		assertEquals(true, match);

		assertEquals("Unusual file descriptors", 0, FileDescriptors.countUnusual());

	}

	private static int TRUNCATE_AT = 10000;

	private String getTruncated(String text) {
		if (text.length() < TRUNCATE_AT)
			return text;
		else
			return text.substring(0, TRUNCATE_AT) + (text.length() > TRUNCATE_AT ? "..." : "");
	}
	
	private static Random random = new Random();

	private GameAnalyzer getAnalyzer(String problemId, String programName, List<String> inputs)
			throws Exception {
		
		long rnd = random.nextInt(1000);

		String id = "test" + rnd + 
				game_contest.getTransactionId(CustomData.TEAM_ID, problemId);

		Submission submission = game_submissions.create(id, Submission.class);
		String programCode = readExampleFileContent(programName);

		submission.receive(CustomData.TEAM_ID, null, programName, programCode.getBytes(),
				problemId, inputs, (inputs == null));

		trash.add(submission);
		return new GameAnalyzer(submission);
	}

	private static final Charset CS = Charset.defaultCharset();

	private String readExampleFileContent(String name) throws IOException {
		StringBuilder builder = new StringBuilder();
		Path path = examples.resolve(name);
		try (BufferedReader buffer = Files.newBufferedReader(path, CS)) {
			String line;
			while ((line = buffer.readLine()) != null) {
				builder.append(line);
				builder.append("\n");
			}

		}

		return builder.toString();
	}
}
