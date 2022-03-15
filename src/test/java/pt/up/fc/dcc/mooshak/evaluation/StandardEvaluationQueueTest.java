package pt.up.fc.dcc.mooshak.evaluation;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

public class StandardEvaluationQueueTest {

	StandardEvaluationQueue queue;
		
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {	
		PersistentObject.setHome(CustomData.HOME);
		SafeExecution.setWebInf(CustomData.WEB_INF);
	}
	
	@Before
	public void setUp() throws Exception {
		queue = StandardEvaluationQueue.getInstance();
	}

	@Test
	public void testConcludeEvaluation() throws MooshakContentException {
		
		EventSender.setQueueSize(100);
		
		Submission submission = PersistentObject.openPath(CustomData.SUBMISSION_PATHNAME);
		Team team = (Team) submission.getTeam();
		Contest contest = submission.getParent().getParent();
		String contestId = contest.getIdName();
		EventSender sender = EventSender.getEventSender();
		Recipient recipient = new Recipient(team.getIdName());
		int serial = sender.getSerial(contestId, new Date());
		List<MooshakEvent> allEvents;
		List<MooshakEvent> events;
		
		queue.concludeEvaluation(submission);
		
		allEvents = sender.eventsToDeliver(contestId, recipient, serial);
		events = allEvents.stream()
				.filter(e -> ! (e instanceof RankingUpdate)).collect(Collectors.toList());
		
		
		for(MooshakEvent event: allEvents) {
			assertEquals(getNextSerial(serial),event.getSerial());
			serial = event.getSerial();
			// System.out.println(event.getSerial());
		}
		
		assertEquals(2,events.size());
		

		for(int i=0; i<1000; i++) {
			queue.concludeEvaluation(submission);
			
			// System.out.println("---");
			allEvents = sender.eventsToDeliver(contestId, recipient, serial);
		
			for(MooshakEvent event: allEvents) {
				assertEquals(getNextSerial(serial),event.getSerial());
				serial = event.getSerial();
				// System.out.println(serial);
				// System.out.println(event);
			}
			assertEquals(3,allEvents.size());
		}
	}
	
	private int getNextSerial(int serial) {
		return serial+1 == EventSender.getQueueSize() ? 0 : serial+1;
	}

}
