package pt.up.fc.dcc.mooshak.managers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.erl.ReportType;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Tests for Reviewer Manager
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ReviwerManagerTest {

	static final String SUBMISSION = 
			"data/contests/proto_icpc/submissions/05075034_P_team";
	static final String SUBMISSION_COPY = 
			"data/contests/proto_icpc/submissions/05075034_P_team_copy";

	Submission loaded;
	Submission loadedCopy;
	Submission created;
	//Submission other;
	
	static Contest contest;
	static Submissions submissions;

	ReviewerManager manager = ReviewerManager.getInstance();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistentObject.setHome(CustomData.HOME);
		
		Contests contests = PersistentObject.openPath(CustomData.CONTESTS);
		contest = contests.open(CustomData.CONTEST_ID);
		submissions = contest.open("submissions");
		
		SafeExecution.setWebInf(CustomData.WEB_INF);
	}
	
	@Before 
	public void setUp() throws MooshakException {
		loaded = PersistentObject.openPath(SUBMISSION);
		
		String id = contest.getTransactionId(CustomData.TEAM_ID,CustomData.PROBLEM_ID);
		created = submissions.create(id, Submission.class);
		
		AdministratorManager.getInstance().pasteMooshakObject(loaded.getParent()
				.getPath().toString(), SUBMISSION);
		loadedCopy = PersistentObject.openPath(SUBMISSION_COPY);
		
	}
	
	@After
	public void cleanup() throws MooshakContentException { 		
 		created.delete();
 		loadedCopy.delete();
 		loaded.reopen();
	}
	

	@Test
	public void testGetSubmissionReport() throws MooshakException, InterruptedException, IOException {
		Contest contest = loaded.getContest();
		Session session = new Session();
		
		session.setContest(contest);
		
		cloneSubmission(session);
		
		assertEquals(loaded.getClassify(), created.getClassify());
		assertEquals(loaded.getMark(), created.getMark());
	
		Map<String, String> reports = manager.getSubmissionReports(session, 
				created.getIdName());
		
		assertEquals(1, reports.keySet().size());
		manager.reevaluate(session, created.getProgramName(), created.getProblemId(), 
				created.getIdName());
		
		Thread.sleep(5000);
		reports = manager.getSubmissionReports(session, created.getIdName());
		assertEquals(2, reports.keySet().size());
	}

	@Test
	public void testReevaluate() throws MooshakException, IOException {
		Contest contest = loaded.getContest();
		Session session = new Session();
		
		session.setContest(contest);
		
		cloneSubmission(session);
		
		assertEquals(loaded.getClassify(), created.getClassify());
		assertEquals(loaded.getMark(), created.getMark());
		
		manager.reevaluate(session, created.getProgramName(), created.getProblemId(), 
				created.getIdName());
		
		ReportType evaluationReport = created.getReportType(1);
		ReportType reevaluationReport = created.getReportType(2);
		
		assertEquals(loaded.getClassify(), created.getClassify());
		assertEquals(evaluationReport.getExercise(), reevaluationReport.getExercise());
		assertEquals(evaluationReport.getProgrammingLanguage(), reevaluationReport.getProgrammingLanguage());
		assertEquals(evaluationReport.getSummary(), reevaluationReport.getSummary());
		assertEquals(evaluationReport.getTests(), reevaluationReport.getTests());
	}
	
	private void cloneSubmission(Session session) throws MooshakException, IOException {
		created.receive(
 				loaded.getTeamId(), 
 				null,
 				loaded.getProgramName(), 
 				Files.readAllBytes(loaded.getAbsoluteFile().resolve(loaded.getProgramName())), 
 				loaded.getProblemId(), 
 				CustomData.SINGLE_EMPTY_INPUT,
 				CustomData.CONSIDER);
 		 		
 		created.analyze();
	}

	@Test
	public void testReevaluateOwnerBug() throws MooshakException, IOException {
		Contest contest = loadedCopy.getContest();
		Session session = new Session();
		
		session.setContest(contest);
		
		manager.reevaluate(session, loadedCopy.getProgramName(), loadedCopy.getProblemId(), 
				loadedCopy.getIdName());
		
		ReportType evaluationReport = loadedCopy.getReportType(1);
		ReportType reevaluationReport = loadedCopy.getReportType(2);
		
		assertEquals(loaded.getClassify(), loadedCopy.getClassify());
		assertEquals(evaluationReport.getExercise(), reevaluationReport.getExercise());
		assertEquals(evaluationReport.getProgrammingLanguage(), reevaluationReport.getProgrammingLanguage());
		assertEquals(evaluationReport.getSummary(), reevaluationReport.getSummary());
		assertEquals(evaluationReport.getTests(), reevaluationReport.getTests());
	}

}
