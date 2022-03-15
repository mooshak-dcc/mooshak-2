package pt.up.fc.dcc.mooshak.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.FileDescriptors;
import pt.up.fc.dcc.mooshak.Threads;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationQueue.EvaluationRequest;
import pt.up.fc.dcc.mooshak.evaluation.Evaluator.EvaluationWorker;
import pt.up.fc.dcc.mooshak.managers.ParticipantManager;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class EvaluatorTest {
	Evaluator evaluator;
	Submission submission = null;
	
	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {
		PersistentObject.setHome(CustomData.HOME);
		
		SafeExecution.setWebInf(CustomData.WEB_INF);
		
		int count = Runtime.getRuntime().availableProcessors();
		Threads.clearRegistredExpectedThreadNames();
		Threads.registerExpectedThreadName("pool-\\d+-thread-\\d+", count);
		Threads.registerExpectedThreadName("process reaper",count);
	}
	
	@Before
	public void setUp() throws Exception {
		evaluator = new Evaluator("TesteEvaluator",
				StandardEvaluationQueue.getInstance());
	}
	
	@After
	public void tearDown() throws Exception {
		if (submission != null)
			submission.delete();
		submission = null;
	}

	@Test
	public void testEvaluationWorkerProcessHelloNoInput()
			throws MooshakException, IOException, InterruptedException {
		
		Submission submission = ParticipantManager.getInstance().createSubmission(
				null,
				CustomData.CONTEST_ID,
				CustomData.TEAM_ID,
				null,
				CustomData.HELLO_NAME, 
				CustomData.HELLO_CODE.getBytes(), 
				null,
				CustomData.DONT_CONSIDER,
				null
		);
		
		EvaluationWorker worker = evaluator.new EvaluationWorker(new EvaluationRequest(submission));

		assertTrue(Threads.withoutUnexpectedThreads(worker::processRequest));
		
		submission = worker.submission;
		
		assertEquals(Submission.State.PENDING,submission.getState());
		assertEquals(Submission.Classification.ACCEPTED,submission.getClassify());
		assertEquals("",submission.getObservations());
		assertEquals(0,FileDescriptors.countUnusual());
		assertTrue(submission.getUserExecutionTimes() == null 
				|| submission.getUserExecutionTimes().isEmpty());
		
		submission.delete();
	}

	@Test
	public void testEvaluationWorkerProcessHelloSingleInput() 
			throws MooshakException, InterruptedException, IOException {
		
		Submission submission = ParticipantManager.getInstance().createSubmission(
				null,
				CustomData.CONTEST_ID,
				CustomData.TEAM_ID,
				null,
				CustomData.HELLO_NAME, 
				CustomData.HELLO_CODE.getBytes(), 
				CustomData.SINGLE_EMPTY_INPUT,
				CustomData.DONT_CONSIDER,
				null
		);
		
		EvaluationWorker worker = evaluator.new EvaluationWorker(new EvaluationRequest(submission));
		
		assertTrue(Threads.withoutUnexpectedThreads(worker::processRequest));
		
		submission = worker.submission;
		
		assertEquals(Submission.State.PENDING,submission.getState());
		assertEquals(Submission.Classification.ACCEPTED,submission.getClassify());
		assertEquals("",submission.getObservations());
		assertEquals(0,FileDescriptors.countUnusual());
		assertEquals(1,submission.getUserExecutionTimes().size());
		
		submission.delete();
	}
	
	@Test
	public void testEvaluationWorkerProcessAdd() 
			throws MooshakException, InterruptedException, IOException {
		
		Submission submission = ParticipantManager.getInstance().createSubmission(
				null,
				CustomData.CONTEST_ID,
				CustomData.TEAM_ID,
				null,
				CustomData.ADD_NAME, 
				CustomData.ADD_CODE.getBytes(),
				CustomData.NUMBER_PAIRS_INPUT,
				CustomData.DONT_CONSIDER,
				null
		);
		
		EvaluationWorker worker = evaluator.new EvaluationWorker(new EvaluationRequest(submission));
		
		assertTrue(Threads.withoutUnexpectedThreads(worker::processRequest));
		
		submission = worker.submission;
		
		Map<Integer,String> outputs = submission.getUserOutputs();
		Map<Integer,String> executionTimes = submission.getUserExecutionTimes();
		
		assertEquals(Submission.State.PENDING,submission.getState());
		assertEquals(Submission.Classification.ACCEPTED,submission.getClassify());
		assertEquals("",submission.getObservations());
		assertEquals(0,FileDescriptors.countUnusual());
		
		checkOutputsOfAdd(CustomData.NUMBER_PAIRS_INPUT, outputs);
		
		assertEquals(outputs.size(),executionTimes.size());
		assertEquals(outputs.keySet(),executionTimes.keySet());
				
		submission.delete();
	}

	
	@Test
	public void testEvaluationWorkerProcessAddManyTestCases() 
			throws MooshakException, InterruptedException, IOException {
		
		List<String> manyInputs = new ArrayList<>();
		int n = 20;
		
		for(int i=0; i<n; i++) {
			String d = Integer.toString(i);
			manyInputs.add(d+" -"+d);
		}
		
		Submission submission = ParticipantManager.getInstance().createSubmission(
				null,
				CustomData.CONTEST_ID,
				CustomData.TEAM_ID,
				null,
				CustomData.ADD_NAME, 
				CustomData.ADD_CODE.getBytes(),
				manyInputs,
				CustomData.DONT_CONSIDER,
				null
		);

		EvaluationWorker worker = evaluator.new EvaluationWorker(new EvaluationRequest(submission));
				
		assertTrue(Threads.withoutUnexpectedThreads(worker::processRequest));
		
		submission = worker.submission;
		
		Map<Integer,String> outputs = submission.getUserOutputs();
		Map<Integer,String> executionTimes = submission.getUserExecutionTimes();
		
		System.out.println(submission.getObservations());
		
		assertEquals(Submission.State.PENDING,submission.getState());
		assertEquals(Submission.Classification.ACCEPTED,submission.getClassify());
		assertEquals("",submission.getObservations());
		
		checkOutputsOfAdd(manyInputs,outputs);
		
		assertEquals(outputs.size(),executionTimes.size());
		assertEquals(outputs.keySet(),executionTimes.keySet());
		
		assertEquals(0,FileDescriptors.countUnusual());
		
		submission.delete();
	}
	
	private void checkOutputsOfAdd(
			List<String> inputs,
			Map<Integer,String> outputs) {
		
		assertEquals(inputs.size(),outputs.size());
		
		int pos = 0;
		for(String pair: inputs) {
			String[] args = pair.split(" ");
			int a = Integer.parseInt(args[0]);
			int b = Integer.parseInt(args[1]);
			int s = Integer.parseInt(outputs.get(pos++).trim());
			assertEquals(a+b,s);
		}
	}
	
	
}
