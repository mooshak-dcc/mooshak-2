package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submission.State;
import pt.up.fc.dcc.mooshak.content.types.Submissions.FeedbackCategories;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class SubmissionsTest {

	Contest contest;
	
	Submissions loaded;
	Submissions created;
	
	static Path contestPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		contestPath = Paths.get("data/contests/JUST_A_TEST_CONTEST");
	}
	
	@Before
	public void setUp() throws Exception {
		contest = PersistentObject.create(contestPath,Contest.class);
		
		created = contest.create("submissions",Submissions.class);
		loaded = PersistentObject.openPath(CustomData.SUBMISSIONS_PATHNAME);
	}
	
	@After
	public void cleanUp()  throws Exception {
		loaded.reopen();
		contest.delete();
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
	public void testMultiple() throws MooshakContentException {
		assertEquals(false, created.isMultipleAccepts());
		assertEquals(false, loaded.isMultipleAccepts());

		created.setMultipleAccepts(true);
		created.save(); created.reopen();
		assertEquals(true, created.isMultipleAccepts());

		loaded.setMultipleAccepts(true);
		assertEquals(true, loaded.isMultipleAccepts());
	}

	@Test
	public void testShowOwnCode() throws MooshakContentException {
		assertEquals(true, created.isShowOwnCode());
		assertEquals(true, loaded.isShowOwnCode());

		created.setShowOwnCode(false);
		created.save(); created.reopen();
		assertEquals(false, created.isShowOwnCode());

		loaded.setShowOwnCode(false);
		assertEquals(false, loaded.isShowOwnCode());
	}

	@Test
	public void testGiveFeedback() throws MooshakContentException {
		assertEquals(FeedbackCategories.REPORT, loaded.getGiveFeedback());
		assertEquals(FeedbackCategories.REPORT, created.getGiveFeedback());

		for(FeedbackCategories feedback: 
			EnumSet.allOf(FeedbackCategories.class)) {
			
			loaded.setGiveFeedback(feedback);
			assertEquals(feedback,loaded.getGiveFeedback());
			
			created.setGiveFeedback(feedback);
			created.save(); created.reopen();
			assertEquals(feedback,created.getGiveFeedback());
		}
	}


	@Test
	public void testDefaultState() {
		assertEquals(State.PENDING, loaded.getDefaultState());
		assertEquals(State.PENDING, created.getDefaultState());

		for(State state: EnumSet.allOf(State.class)) {
			created.setDefaultState(state);
			assertEquals(state, created.getDefaultState());

			loaded.setDefaultState(state);
			assertEquals(state, loaded.getDefaultState());
		}
	}

	@Test
	public void testShowErrors() throws MooshakContentException {

		EnumSet<Classification> errors = EnumSet.noneOf(Classification.class);

		for(Classification classification: Classification.values()) {
			EnumSet<Classification> singleError = EnumSet.of(classification);
			created.setShowErrors(singleError);
			created.save(); created.reopen();
			assertEquals(singleError,created.getShowErrors());
		
			errors.add(classification);
			
			created.setShowErrors(errors);
			created.save(); created.reopen();
			assertEquals(errors,created.getShowErrors());
		}

		errors.remove(Classification.ACCEPTED);
		errors.remove(Classification.EVALUATING);
		
		errors.remove(Classification.EVALUATION_SKIPPED);
		errors.remove(Classification.INVALID_EXIT_VALUE);
		
		assertEquals(errors,loaded.getShowErrors());
	}


	@Test
	public void testFeedbackDelay() throws MooshakContentException {
		assertEquals(0,created.getFeedbackDelay());
		assertEquals(0,loaded.getFeedbackDelay());

		for(int value: CustomData.INTS) {
			created.setFeedbackDelay(value);
			created.save(); created.reopen();
			assertEquals(value,created.getFeedbackDelay());

			loaded.setFeedbackDelay(value);
			assertEquals(value,loaded.getFeedbackDelay());
		}
	}

	@Test
	public void testMaximumPending() throws MooshakContentException {
		assertEquals(Integer.MAX_VALUE,created.getMaximumPending());
		assertEquals(Integer.MAX_VALUE,loaded.getMaximumPending());

		for(int value: CustomData.INTS) {
			created.setMaximumPending(value);
			created.save(); created.reopen();
			assertEquals(value,created.getMaximumPending());

			loaded.setMaximumPending(value);
			assertEquals(value,loaded.getMaximumPending());
		}
	}

	final static String TEAM_ID ="team";
	final static String PROBLEM_ID = "A";
	final static String PROGRAM_NAME = "Test";
	final static String PROGRAM_CODE = 
			"class Test {public static void main(String[] args){} } ";
	final static List<String> EMPTY_INPUTS = null;
	final static Date NOW = new Date();
	final static boolean CONSIDER = true;
	
	@Test
	public void testAcceptableFirstSubmission() throws MooshakException {
		
		
		setContest();	
		
		try {
			created.acceptable(TEAM_ID, PROBLEM_ID, PROGRAM_CODE.getBytes());
		} catch (MooshakException e) {
			fail("unexpected exception");
		}
		
	}
	
	@Test
	public void testAcceptableMinimumInterval() throws MooshakException,
		InterruptedException {
		
		Submission submission;
		int minimumInterval = 1;
				
		setContest();	
		
		created.setMinimumInterval(minimumInterval);
				
		String id=contest.getTransactionId(TEAM_ID,PROBLEM_ID);

		submission = created.create(id, Submission.class);
		submission.receive(TEAM_ID,null,PROGRAM_NAME,PROGRAM_CODE.getBytes(),
				PROBLEM_ID,EMPTY_INPUTS,CONSIDER);
		
		try { // other teams submissions are OK
			created.acceptable("other-team", PROBLEM_ID, PROGRAM_CODE.getBytes());
		} catch (MooshakException e) {
			fail("Unexpected excecption");
		}
		
		try { // immediate new submissions from same team are not OK 
			created.acceptable(TEAM_ID, PROBLEM_ID, (PROGRAM_CODE+" ").getBytes());
		} catch (MooshakException e) {
			assertEquals("Too frequent submissions",e.getMessage());
		}
		
		Thread.sleep(minimumInterval*1000L);
		
		try { // after the interval new submissions from same team are OK
			created.acceptable(TEAM_ID, PROBLEM_ID, (PROGRAM_CODE+" ").getBytes());
		} catch (MooshakException e) {
			fail("Unexpected exception:"+e.getMessage());
		}
	} 
	
	@Test
	public void testAcceptableDuplicateSubmission() throws MooshakException {
		
		Submission submission;
				
		setContest();	
				
		String id=contest.getTransactionId(TEAM_ID,PROBLEM_ID);

		submission = created.create(id, Submission.class);
		submission.receive(TEAM_ID,null,PROGRAM_NAME,PROGRAM_CODE.getBytes(),
				PROBLEM_ID,EMPTY_INPUTS,CONSIDER);
		
		try { // duplicate submissions are not OK
			created.acceptable(TEAM_ID, PROBLEM_ID, PROGRAM_CODE.getBytes());
		} catch (MooshakException e) {
			assertEquals("Duplicate submission",e.getMessage());
		}
		
		try { // with a small change they are ok
			created.acceptable(TEAM_ID, PROBLEM_ID, (PROGRAM_CODE+" ").getBytes());
		} catch (MooshakException e) {
			fail("Unexpected exception");
		}
	}
	
	@Test
	public void testAcceptableAccepted() throws MooshakException {
		
		Submission submission;
				
		setContest();	
				
		String id=contest.getTransactionId(TEAM_ID,PROBLEM_ID);

		submission = created.create(id, Submission.class);
		submission.receive(TEAM_ID,null,PROGRAM_NAME,PROGRAM_CODE.getBytes(),
				PROBLEM_ID,EMPTY_INPUTS,CONSIDER);
		submission.setClassify(Classification.ACCEPTED);
		
		try { // new submissions to the same problem after accepted not OK
			created.acceptable(TEAM_ID, PROBLEM_ID, (PROGRAM_CODE+" ").getBytes());
		} catch (MooshakException e) {
			assertEquals("Duplicate submission",e.getMessage());
		}
		
		created.setMultipleAccepts(true);
		
		try { // unless multiple submissions are allowed 
			created.acceptable(TEAM_ID, PROBLEM_ID, (PROGRAM_CODE+" ").getBytes());
		} catch (MooshakException e) {
			fail("Unexpected exception");
		}
	}
	
	@Test
	public void testAcceptableMaximumPending() throws MooshakException {
		
		Submission submission;
				
		setContest();	
				
		String id=contest.getTransactionId(TEAM_ID,PROBLEM_ID);

		submission = created.create(id, Submission.class);
		submission.receive(TEAM_ID,null,PROGRAM_NAME,PROGRAM_CODE.getBytes(),
				PROBLEM_ID,EMPTY_INPUTS,CONSIDER);
		
		created.setMaximumPending(1);
		
		try { // new submissions after maximinum pending are not ok
			created.acceptable(TEAM_ID, PROBLEM_ID, (PROGRAM_CODE+" ").getBytes());
		} catch (MooshakException e) {
			assertEquals("Duplicate submission",e.getMessage());
		}
		
		created.setMaximumPending(2);
		
		try { //  new submissions after maximinum pending are not ok
			created.acceptable(TEAM_ID, PROBLEM_ID, (PROGRAM_CODE+" ").getBytes());
		} catch (MooshakException e) {
			fail("Unexpected exception");
		}
	}	
	

	/**
	 * @throws MooshakContentException
	 */
	private void setContest() throws MooshakContentException {
		contest.setOpen(NOW);;
		contest.setStart(NOW);
		
		Groups  	groups 		= 	contest.create("groups", 	Groups.class);
		Group   	group 		= 	groups.create("MyGroup",	Group.class);
		/*Team    	team  		=*/	group.create(TEAM_ID, 		Team.class);
		
		Problems 	problems 	= 	contest.create("problems", 	Problems.class);
		/*Problem  	problem		=*/problems.create(PROBLEM_ID,	Problem.class);
	}
}
