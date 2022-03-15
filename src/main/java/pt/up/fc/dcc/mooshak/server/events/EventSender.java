package pt.up.fc.dcc.mooshak.server.events;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

/**
 * <p>
 * A singleton of this class sends events to recipients in contests. Events are
 * delivered to the recipient's client by the EventService
 * </p>
 * 
 * <p>
 * If events are addressed to a specific recipient then they are delivered just
 * to that recipient. Examples of such events are notification that problem was
 * accepted or event was answered.
 * </p>
 * 
 * <p>
 * events without a recipient are broadcast to every recipient in the contest to
 * which it was sent. Examples of such events are updates of listings and
 * notifications of events sent by the judges/teachers. (Broadcast to all
 * contests will be implemented if needed).
 * </p>
 * 
 * <p>
 * A typical use of this class to send an event is the following:
 * </p>
 * 
 * <pre>
 * EventSender sender = EventSender.geteventSender();
 * NotificationEvent event = new NotificationEvent(recipient, text);
 * sender.send(event);
 * </pre>
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class EventSender {

	/**
	 * Number of milliseconds a contest queue may be idle before being removed.
	 * Queues are automatically recreated when needed. This static field exists
	 * for changing its value for testing purposes.
	 */
	private static long maxIdle = 5 * 60 * 1000;

	/**
	 * The single instance of this class
	 */
	private static EventSender eventSender = null;

	/** Maximum number of events posted between consecutive pools **/
	private static int queueSize = 20000;

	/**
	 * Map with circular queues indexed by contest name
	 */
	private Map<String, EventCircularQueue> queues = new HashMap<>();

	protected final static Logger logger = Logger.getLogger("");

	/**
	 * Circular queue with events to deliver to recipients from a same contest.
	 * events are sent with the <code>send()</code> method. events for a given
	 * recipient are collected with the method <code>eventsToDeliver()</code>.
	 * events sent with an unspecified recipient are broadcast to all recipients
	 * in contest.
	 * 
	 */
	class EventCircularQueue {

		private MooshakEvent[] buffer = new MooshakEvent[queueSize];
		private int serial = 0;

		private Date lastAcess = new Date(0);

		/**
		 * Send (enqueue) event
		 * 
		 * @param event
		 */
		<T extends MooshakEvent> void send(T event) {

			event.setSerial(serial);
			buffer[serial] = event;
			serial = next(serial);
		}

		/**
		 * Return serial number of most recent event sent before date
		 * 
		 * @param date
		 * @return
		 */
		Integer getSerial(Date date) {

			int startPoint = previous(serial);
			
			if (buffer[startPoint] == null || !buffer[startPoint].getDate().after(date))
				return startPoint;
			
			int point = startPoint;
			
			do {
				point = previous(point);
			} while (buffer[point] != null && buffer[point].getDate().after(date)
					&& startPoint != point);

			return point;
		}

		/**
		 * Collect events to deliver to recipient sent after date. If no events
		 * are available then an empty list is returned
		 * 
		 * @param recipient
		 * @param date
		 * @return
		 */
		List<MooshakEvent> eventsToDeliver(Recipient recipient, int start) {

			List<MooshakEvent> events = new LinkedList<>();

			int point = next(start);
			while (point != serial && buffer[point] != null) {
				Recipient toWhom = buffer[point].getRecipient();
				if (toWhom == null || recipient == null
						|| toWhom.equals(recipient)) {

					events.add(buffer[point]);
				}
				point = next(point);
			}

			lastAcess = new Date();
			return events;
		}

		/**
		 * Checks if this queue has been idle for howLong milliseconds, both
		 * client side and server side
		 * 
		 * @param howLong
		 * @return
		 */
		boolean idle(long howLong) {
			Date now = new Date();

			if (now.getTime() - lastAcess.getTime() < howLong)
				return false;
			else {
				MooshakEvent lastMessage = buffer[previous(serial)];

				if (lastMessage == null)
					return true;
				else if (now.getTime() - lastMessage.getDate().getTime() < howLong)
					return false;
				else
					return true;
			}
		}

		/**
		 * Previous index in the circular queue
		 * 
		 * @return previous serial
		 */
		private int previous(int value) {
			return value == 0 ? queueSize - 1 : value - 1;
		}

		/**
		 * Next index in the circular queue
		 * 
		 * @return next serial
		 */
		private int next(int value) {
			return value == queueSize - 1 ? 0 : value + 1;
		}

	}

	/**
	 * Creates single instance that schedules cleanups
	 */
	private EventSender() {

		ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(
				1);
		executor.scheduleWithFixedDelay(new Runnable() {

			@Override
			public void run() {
				try {
					cleanup();
				} catch (Exception cause) {
					logger.log(Level.SEVERE, "Cleanup error", cause);
				}
			}
		}, getMaxIdle(), getMaxIdle(), TimeUnit.MILLISECONDS);
	}

	/**
	 * Return a EventSender singleton
	 * 
	 * @return
	 */
	public synchronized static EventSender getEventSender() {
		if (eventSender == null)
			eventSender = new EventSender();
		return eventSender;
	}

	/**
	 * Propagate event to all queues
	 * 
	 * @param event
	 * @throws CloneNotSupportedException
	 */
	public <T extends MooshakEvent> void send(T event) {
		
		synchronized (queues) {
			
			for(String contest: queues.keySet())
				send(contest, cloneMooshakEvent(event));
		}
	}

	/**
	 * Sends event to a contest. A new queue for contest if none exists
	 * 
	 * @param contest
	 *            to send
	 * @param event
	 *            to send
	 */
	public <T extends MooshakEvent> void send(String contest, T event) {
		
		synchronized (queues) {
				
			if (!queues.containsKey(contest))
				queues.put(contest, new EventCircularQueue());
	
			queues.get(contest).send(event);
		}
	}

	/**
	 * Get serial number of most recent event posted in contest before date.
	 * 
	 * @param contest
	 *            where events where posted
	 * @param date
	 *            after which events are requested
	 * @return
	 */
	public int getSerial(String contest, Date date) {
		if (!queues.containsKey(contest))
			queues.put(contest, new EventCircularQueue());
		return queues.get(contest).getSerial(date);
	}

	/**
	 * Collects events posted after serial, to deliver to recipient logged in
	 * contest.
	 * 
	 * @param contest
	 *            where recipient is logged
	 * @param recipient
	 *            of events
	 * @param serial
	 *            of last delivered event to recipient
	 * @return list of events
	 */
	public List<MooshakEvent> eventsToDeliver(String contest,
			Recipient recipient, int serial) {
		if (queues.containsKey(contest))
			return queues.get(contest).eventsToDeliver(recipient, serial);
		else
			return null;
	}

	/**
	 * Cleanup this sender by removing contest queues that are idle
	 */
	public void cleanup() {
		// avoid ConcurrentModificationException by not changing
		// queues when it is being iterated
		List<String> toRemove = new ArrayList<>();

		for (String name : queues.keySet())
			if (queues.get(name).idle(getMaxIdle()))
				toRemove.add(name);

		for (String name : toRemove) {
			queues.remove(name);
			logger.log(Level.INFO, "Queue " + name + " cleaned up");
		}
	}

	/**
	 * Returns the current number of milliseconds a contest queue may be idle
	 * before being removed. Queues are automatically recreated when needed.
	 * This method exists for testing purposes.
	 * 
	 * @return the maxIdle
	 */
	static long getMaxIdle() {
		return maxIdle;
	}

	/**
	 * Sets the number of milliseconds a contest queue may be idle before being
	 * removed. Queues are automatically recreated when needed. This method
	 * exists for testing purposes.
	 * 
	 * @param maxIdle
	 *            the maxIdle to set
	 */
	static void setMaxIdle(long maxIdle) {
		EventSender.maxIdle = maxIdle;
	}

	/**
	 * Returns the current size of the circular queues used for events. This
	 * method exists for testing purposes.
	 * 
	 * @return the queueSize
	 */
	public static int getQueueSize() {
		return queueSize;
	}

	/**
	 * Sets the size of the circular queues used for events. This method exists
	 * for testing purposes.
	 * 
	 * @param queueSize
	 *            the queueSize to set
	 */
	public static void setQueueSize(int queueSize) {
		EventSender.queueSize = queueSize;
	}

	/**
	 * Remove all queues in this sender. This method is available for testing
	 * purposes only. Avoid using it.
	 */
	void reset() {

		for (String name : queues.keySet())
			queues.remove(name);
	}

	/**
	 * Clone a mooshak event
	 * @param event
	 * @return the clone
	 */
	@SuppressWarnings("unchecked")
	public <T extends MooshakEvent> T cloneMooshakEvent(T event) {
		Object clone = null;

		try {
			clone = event.getClass().newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		// Walk up the superclass hierarchy
		for (Class<?> obj = event.getClass(); !obj.equals(Object.class); obj = obj
				.getSuperclass()) {
			Field[] fields = obj.getDeclaredFields();
			for (int i = 0; i < fields.length; i++) {
				fields[i].setAccessible(true);
				try {
					fields[i].set(clone, fields[i].get(event));
				} catch (IllegalArgumentException e) {
				} catch (IllegalAccessException e) {
				}
			}
		}
		return (T) clone;
	}
}
