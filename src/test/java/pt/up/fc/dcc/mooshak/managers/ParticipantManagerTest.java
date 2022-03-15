package pt.up.fc.dcc.mooshak.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class ParticipantManagerTest {

	ParticipantManager manager = ParticipantManager.getInstance();

	@BeforeClass
	public static void setUpBeforeClass() {
		PersistentObject.setHome(CustomData.HOME);
	}
	
	@Test
	public void testGetInstance() {
		assertNotNull(manager);
	}

	@Test
	public void testEvaluateMissingArguments() throws MooshakException {
		
	
	    try {
		   manager.evaluate(
				CustomData.CONTEST_ID,
				CustomData.TEAM_ID,
				CustomData.NO_SESSION,
				CustomData.HELLO_NAME,
				CustomData.HELLO_CODE.getBytes(),
				CustomData.NO_PROBLEM_ID,
				CustomData.NO_INPUT,
				CustomData.DONT_CONSIDER);
			fail("Exception expected");
		} catch(MooshakException cause) {
			assertNotNull(cause);
			assertEquals("Missing problem id",cause.getMessage());
		};
		
		try {
			manager.evaluate(
					null,
					CustomData.TEAM_ID,
					CustomData.NO_SESSION,
					CustomData.HELLO_NAME,
					CustomData.HELLO_CODE.getBytes(),
					CustomData.NO_PROBLEM_ID,
					CustomData.NO_INPUT,
					CustomData.DONT_CONSIDER);
			fail("Exception expected");
		} catch(MooshakException cause) {
			assertNotNull(cause);
			assertEquals("Missing contest id",cause.getMessage());
		};
		
		try {
			manager.evaluate(
					CustomData.CONTEST_ID,
					null,
					CustomData.NO_SESSION,
					CustomData.HELLO_NAME,
					CustomData.HELLO_CODE.getBytes(),
					CustomData.NO_PROBLEM_ID,
					CustomData.NO_INPUT,
					CustomData.DONT_CONSIDER);
			fail("Exception expected");
		} catch(MooshakException cause) {
			assertNotNull(cause);
			assertEquals("Missing team id",cause.getMessage());
		};
		
		try {
			manager.evaluate(
					CustomData.CONTEST_ID,
					CustomData.TEAM_ID,
					CustomData.NO_SESSION,
					CustomData.HELLO_NAME,
					null,
					CustomData.NO_PROBLEM_ID,
					CustomData.NO_INPUT,
					CustomData.DONT_CONSIDER);
			fail("Exception expected");
		} catch(MooshakException cause) {
			assertNotNull(cause);
			assertEquals("Missing problem id",cause.getMessage());
		};
	}

	@Test
	public void testSubmissions() throws MooshakException {
		Contest contest = PersistentObject.openPath(CustomData.CONTEST_PATHNAME);
		Path submissionsPath = Paths.get(CustomData.SUBMISSIONS_PATHNAME);
		Path id = submissionsPath.getName(submissionsPath.getNameCount()-1);
		Session session = new Session();
		
		session.setContest(contest);
		
		Submissions submissions = manager.getSubmissions(session);
		
		assertEquals(id.toString(),submissions.getIdName());
	}
	
	@Test
	public void testSubmission() throws MooshakException {
		Contest contest = PersistentObject.openPath(CustomData.CONTEST_PATHNAME);
		Path submissionPath = Paths.get(CustomData.SUBMISSION_PATHNAME);
		Path id = submissionPath.getName(submissionPath.getNameCount()-1);
		Session session = new Session();
		
		session.setContest(contest);
		

		Submission submission = manager.getSubmission(session,id.toString(),true);

		assertEquals(id.toString(),submission.getIdName());
		assertEquals("pending",submission.getState().toString());
		assertEquals("",submission.getObservations());
		
	}

	@Test
	public void testProblems() throws MooshakException {
		Contest contest = PersistentObject.openPath(CustomData.CONTEST_PATHNAME);
		Session session = new Session();
		
		session.setContest(contest);
		
		Map<String,String> problemMap = manager.getProblems(session);
		
		Map<String,String> expected = new HashMap<>();
		String[] data = { "P", "P", "C", "B", "M", "M", "J", "J" };
		for(int i=0; i< data.length; i+=2)
			expected.put(data[i], data[i+1]);
		
		assertEquals(expected,problemMap);
	}
	
	@Test
	public void testProblem() throws MooshakException {
		Contest contest = PersistentObject.openPath(CustomData.CONTEST_PATHNAME);
		Path problemPath = Paths.get(CustomData.PROBLEM_J_PATHNAME);
		Path id = problemPath.getName(problemPath.getNameCount()-1);
		Session session = new Session();
		
		session.setContest(contest);
		
		Problem problem = manager.getProblem(session, id.toString());
		
		assertEquals(id.toString(),problem.getIdName());
		assertEquals("J",problem.getName().toString());
		assertEquals("Financial Risk",problem.getTitle());
	}
	
	@Test
	public void testOpponents() throws MooshakException {
		Contest contest = PersistentObject.openPath(CustomData.GAME_CONTEST);
		Problem problem = PersistentObject.openPath(CustomData.PROBLEM_TICTACTOE_PATHNAME);
		Session session = new Session();
		
		session.setContest(contest);
		
		Map<String, String> opponents = manager.getOpponents(contest, problem.getIdName());
		
		Assert.assertTrue(opponents.values().containsAll(
				Arrays.asList("test", "TicTacToePlayer1", "TTTBot")));

		Assert.assertEquals("test", opponents.get("data/contests/proto_game/submissions/00000000_A_test"));
		Assert.assertEquals("TicTacToePlayer1", opponents.get("data/contests/proto_game/submissions/35551074_A_TicTacToePlayer1"));
		Assert.assertEquals("TTTBot", opponents.get("data/contests/proto_game/submissions/35551075_A_TTTBot"));
	}
	
}
