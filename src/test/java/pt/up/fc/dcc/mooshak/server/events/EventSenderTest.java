package pt.up.fc.dcc.mooshak.server.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.shared.events.BalloonsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.ListingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;
import pt.up.fc.dcc.mooshak.shared.events.NotificationEvent;
import pt.up.fc.dcc.mooshak.shared.events.PendingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.PrintoutsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.QuestionsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.events.SubmissionsUpdate;

public class EventSenderTest {

	EventSender sender;
	
	String contest = "MyFirstContest";
	Recipient recipient = new Recipient("zp");
	Recipient otherRecipient = new Recipient("other");
	String text  = "How are you "+recipient+"?";
	
	public EventSenderTest() {
		EventSender.setMaxIdle(1);
		sender = EventSender.getEventSender();
	}
	
	@Before
	public void setUp() throws Exception {
		sender.reset();
	}

	@Test
	public void testGetMessageSender() {
		assertNotNull(sender);
	}

	@Test
	public void testsWithNotificationEvent() throws InterruptedException {
		List<MooshakEvent> messages, otherMessages;

		// Queue is not yet created (serial is meaningless)
		assertNull(sender.eventsToDeliver(contest, recipient, 0));
		
		int serial = sender.getSerial(contest, new Date());
		int otherSerial = serial;
		
		// Queue was created when serial was requested but is empty
		messages = sender.eventsToDeliver(contest, recipient, serial);
		assertNotNull(messages);
		assertEquals(0,messages.size());
		
		// send messages to  recipient
		MooshakEvent message = new NotificationEvent(recipient,text);
		sender.send(contest,message);
		messages = sender.eventsToDeliver(contest, recipient, serial);
		assertNotNull(messages);
		assertEquals(1,messages.size());
		assertEquals(message,messages.get(0));
		
		// other recipient received no messages 
		otherMessages = sender.eventsToDeliver(contest, otherRecipient,otherSerial); 
		assertEquals(0,otherMessages.size());
		
		// no message since previous one
		otherMessages = sender.eventsToDeliver(contest, recipient, message.getSerial());
		assertNotNull(otherMessages);
		assertEquals(0,otherMessages.size());
		
		// several messages at once just to the recipient
		for(int messageCount = 2; messageCount<10; messageCount++) {
			serial = lastSerialOf(messages);
			for(int count = 0; count < messageCount; count++) 
				sender.send(contest,
						new NotificationEvent(recipient,count+""));
			
			// recipient receives them all
			messages = sender.eventsToDeliver(contest, recipient,serial);
			assertEquals(messageCount,messages.size());
			
			// and in order too
			for(int count = 0; count < messageCount; count++) 
				assertEquals(count+"",
					((NotificationEvent) messages.get(count)).getText());
			
			// but the other recipient doesn't receive them 
			otherMessages = sender.eventsToDeliver(contest, otherRecipient,serial);
			assertEquals(0,otherMessages.size());
		}

		// several messages at once to both recipients
		serial = lastSerialOf(messages);
		for(int messageCount = 2; messageCount<10; messageCount++) {
			
			for(int count = 0; count < messageCount; count++) { 
				sender.send(contest,
						new NotificationEvent(recipient,count+""));
				sender.send(contest,
						new NotificationEvent(otherRecipient,count+""));
			}
				
			// recipient receives them all
			messages = sender.eventsToDeliver(contest, recipient,serial);
			assertEquals(messageCount,messages.size());
			
			// and in order too
			for(int count = 0; count < messageCount; count++) 
				assertEquals(count+"",
					((NotificationEvent) messages.get(count)).getText());
			
			// as does the other recipient receives them all
			otherMessages = sender.eventsToDeliver(contest, otherRecipient,otherSerial);
			assertEquals(messageCount,otherMessages.size());
			
			// which are also in order
			for(int count = 0; count < messageCount; count++) 
				assertEquals(count+"",
					((NotificationEvent) otherMessages.get(count)).getText());

			serial = lastSerialOf(messages);
			otherSerial = lastSerialOf(otherMessages);

		}

		// after a while the queue is removed
		assertNotNull(sender.eventsToDeliver(contest, recipient, serial));
		Thread.sleep(10);
		// sender.timer.getActiveCount();
		assertNull(sender.eventsToDeliver(contest, recipient, serial));
	}
	
	@Test
	public void testCleanUp() throws InterruptedException {

		List<MooshakEvent> messages;

		// Queue is not yet created (serial is meaningless)
		assertNull(sender.eventsToDeliver(contest, recipient, 0));
		
		int serial = sender.getSerial(contest, new Date());

		// Queue was created when serial was requested but is empty
		messages = sender.eventsToDeliver(contest, recipient, serial);
		assertNotNull(messages);
		assertEquals(0,messages.size());
	
		Thread.sleep(2);
		sender.cleanup();
		
		// Time passed, queue was neither used or accesses used and was cleanup
		messages = sender.eventsToDeliver(contest, recipient, serial);
		assertNull(messages);
		
		
		serial = sender.getSerial(contest, new Date());

		// Queue was created when serial was requested but is empty
		messages = sender.eventsToDeliver(contest, recipient, serial);
		assertNotNull(messages);
		
		Thread.sleep(1);
		sender.eventsToDeliver(contest, recipient, serial);
		Thread.sleep(1);
		sender.eventsToDeliver(contest, recipient, serial);
		sender.cleanup();
		
		// Time passed but queue was accessed so was not cleanup
		messages = sender.eventsToDeliver(contest, recipient, serial);
		assertNotNull(messages);

		
		serial = sender.getSerial(contest, new Date());
		Thread.sleep(2);
		sender.send(contest, new MooshakEvent());
		sender.cleanup();
		// Time passed but queue was used so it was not cleanup
		messages = sender.eventsToDeliver(contest, recipient, serial);
		assertNotNull(messages);
		
		Thread.sleep(2);
		sender.cleanup();
		
		// Time passed, queue was neither used or accesses used and was cleanup
		messages = sender.eventsToDeliver(contest, recipient, serial);
		assertNull(messages);
		
	}
	
	
	@Test
	public void testsWithSubmissionsUpdate() 
			throws InstantiationException, IllegalAccessException {
		testsListingUpdateEvent(SubmissionsUpdate.class);
		
	}
	
	@Test
	public void testsWithRankingUpdate() 
			throws InstantiationException, IllegalAccessException {
		testsListingUpdateEvent(RankingUpdate.class);
		
	}
	
	@Test
	public void testsWithBallonsUpdate() 
			throws InstantiationException, IllegalAccessException {
		testsListingUpdateEvent(BalloonsUpdate.class);
		
	}
	
	@Test
	public void testsWithQuestionsUpdate() 
			throws InstantiationException, IllegalAccessException {
		testsListingUpdateEvent(QuestionsUpdate.class);
		
	}
	
	@Test
	public void testsWithPendingUpdate() 
			throws InstantiationException, IllegalAccessException {
		testsListingUpdateEvent(PendingUpdate.class);
		
	}
	
	@Test
	public void testsWithPrintoutsUpdate() 
			throws InstantiationException, IllegalAccessException {
		testsListingUpdateEvent(PrintoutsUpdate.class);
		
	}
	
	void testsListingUpdateEvent(Class<? extends ListingUpdateEvent> type) 
			throws InstantiationException, IllegalAccessException {
		List<MooshakEvent> messages, otherMessages;
		int serial = sender.getSerial(contest, new Date());
		int otherSerial = serial;
		
		ListingUpdateEvent event = type.newInstance();
		event.addValue("#", "1");
		event.addValue("Status", "pending");
		event.addValue("Team", "team");
		
		// send one message and both receive
		sender.send(contest, event);
		messages = sender.eventsToDeliver(contest, recipient,serial);
		otherMessages = sender.eventsToDeliver(contest, recipient,otherSerial);
		assertEquals(1,messages.size());
		assertEquals(1,messages.size());
		
		// no other message was sent and none receives
		serial = lastSerialOf(messages);
		otherSerial = lastSerialOf(otherMessages);
		messages = sender.eventsToDeliver(contest, recipient,serial);
		otherMessages = sender.eventsToDeliver(contest, recipient,otherSerial);
		assertEquals(0,messages.size());
		assertEquals(0,messages.size());		
		
		for(int count=2; count<10; count++) {
			
			for(int c=0; c<count; c++) {
				Map<String,String> record = new HashMap<>();
				record.put("Status", "pending");
				record.put("Team", "team");
				
				event = type.newInstance();
				record.put("#",c+"");
				event.setRecord(record);
				sender.send(contest, event);
			}
			
			messages = sender.eventsToDeliver(contest, recipient,serial);
			otherMessages = sender.eventsToDeliver(contest, recipient,otherSerial);
			
			assertEquals(count,messages.size());
			assertEquals(count,otherMessages.size());

			for(int c=0; c<count; c++) {
				assertEquals(c+"",((ListingUpdateEvent) messages.get(c)).getValue("#"));
				assertEquals(c+"",((ListingUpdateEvent) otherMessages.get(c)).getValue("#"));
			}
			
			serial = lastSerialOf(messages);
			otherSerial = lastSerialOf(otherMessages);
		}
		
	}

	// serial of last message
	private int lastSerialOf(List<MooshakEvent> messages) {
		return messages.get(messages.size()-1).getSerial();
	}


}
