package pt.up.fc.dcc.mooshak.content;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.CustomData;


/**
 * Tests on the publish/subscribe mechanism for events on persistent objects  
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class PersistentObjectSenderTest {

	private static final int MAX = 10;
	
	PersistentObjectSender sender;

	class CountListener<T extends PersistentObject> 
		implements PersistentObjectListener<T> {
		
		int counter = 0;
				
		@Override
		public void receivedPersistentObject(T persistent) {
			counter++;
		}
		
		int getCounter() {
			return counter;
		}
	
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistentObject.setHome(CustomData.HOME);
	}
	
	@Before
	public void setUp() {
		sender = new PersistentObjectSender();
	}
	
	@Test 
	public void testSendSubmissionToSubmission() throws MooshakContentException {
		CountListener<Submission> listener = new CountListener<>();
		sender.addPersistentObjectListener(Submission.class,listener);
		
		Submission submission = PersistentObject
				.openPath(CustomData.SUBMISSION_PATHNAME);
		
		for(int c=0; c<MAX; c++) {
			assertEquals(c,listener.getCounter());
			sender.broadcat(submission);
		}
		assertEquals(MAX,listener.getCounter());
	}
	
	@Test 
	public void testSendSubmissionsToSubmissions() throws MooshakContentException {
		CountListener<Submission> listener = new CountListener<>();
		sender.addPersistentObjectListener(Submissions.class,listener);
		
		Submissions submissions = PersistentObject.openPath(CustomData
				.SUBMISSIONS_PATHNAME);
		
		for(int c=0; c<MAX; c++) {
			assertEquals(c,listener.getCounter());
			sender.broadcat(submissions);
		}
		assertEquals(MAX,listener.getCounter());
	}
	
	@Test 
	public void testSendSubmissionToSubmissions() throws MooshakContentException {
		CountListener<Submissions> listener = new CountListener<>();
		sender.addPersistentObjectListener(Submissions.class,listener);
		
		Submission submission = PersistentObject
				.openPath(CustomData.SUBMISSION_PATHNAME);
		
		for(int c=0; c<MAX; c++) {
			assertEquals(0,listener.getCounter());
			sender.broadcat(submission);
		}
		assertEquals(0,listener.getCounter());
	}
	
	
	@Test 
	public void testSendSubmissionReceivePesistentObject() throws MooshakContentException {
		CountListener<PersistentObject> listener = new CountListener<>();
		sender.addPersistentObjectListener(Submission.class,listener);
		
		Submission submission = PersistentObject
				.openPath(CustomData.SUBMISSION_PATHNAME);
		
		for(int c=0; c<MAX; c++) {
			assertEquals(c,listener.getCounter());
			sender.broadcat(submission);
		}
		assertEquals(MAX,listener.getCounter());
	}
	
	@Test 
	public void testSendSubmissionReceiveSubmissionAndPesistentObject() throws MooshakContentException {
		CountListener<Submission> listenerSubmission = new CountListener<>();
		CountListener<PersistentObject> listenerPersistentObject = 
				new CountListener<>();
		
		sender.addPersistentObjectListener(Submission.class,listenerSubmission);
		sender.addPersistentObjectListener(Submission.class,listenerPersistentObject);
		
		Submission submission = PersistentObject
				.openPath(CustomData.SUBMISSION_PATHNAME);
		
		for(int c=0; c<MAX; c++) {
			assertEquals(c,listenerSubmission.getCounter());
			assertEquals(c,listenerPersistentObject.getCounter());
			sender.broadcat(submission);
		}
		assertEquals(MAX,listenerSubmission.getCounter());
		assertEquals(MAX,listenerPersistentObject.getCounter());
	}
	
	
	public void testSendSubmissionReceiveSubmissionAndNotPesistentContainer() 
			throws MooshakContentException {
		CountListener<Submission> listenerSubmission = new CountListener<>();
		CountListener<PersistentContainer<?>> listenerPersistentContainer = 
				new CountListener<>();
		
		sender.addPersistentObjectListener(Submission.class,listenerSubmission);
		sender.addPersistentObjectListener(PersistentContainer.class,listenerPersistentContainer);
		
		Submission submission = PersistentObject
				.openPath(CustomData.SUBMISSION_PATHNAME);
		
		for(int c=0; c<MAX; c++) {
			assertEquals(c,listenerSubmission.getCounter());
			assertEquals(0,listenerPersistentContainer.getCounter());
			sender.broadcat(submission);
		}
		assertEquals(MAX,listenerSubmission.getCounter());
		assertEquals(0,listenerPersistentContainer.getCounter());
	}
	
	@Test 
	public void testSendSubmissionsReceivePesistentContainer() 
			throws MooshakContentException {
		CountListener<PersistentContainer<?>> listener = new CountListener<>();
		sender.addPersistentObjectListener(PersistentContainer.class,listener);
		
		Submissions submissions = PersistentObject
				.openPath(CustomData.SUBMISSIONS_PATHNAME);
		
		for(int c=0; c<MAX; c++) {
			assertEquals(c,listener.getCounter());
			sender.broadcat(submissions);
		}
		assertEquals(MAX,listener.getCounter());
	}
	
}
