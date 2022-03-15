package pt.up.fc.dcc.mooshak.client.events;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * This class is responsible for broadcasting locally (client side)
 * the events sent by the server.
 * Event receivers must register for notification using the method 
 * <code>addListener(T type,MooshakEventListener(&lt;t&gt; listener)</code>
 * where <code>T</code> is an extension of <code>MooshakEvent</code>.
 * 
 * A Timer is scheduled to pool events from the server. If the client is
 * waiting for its own events (such as after a submission) it can execute
 * the <code>refresh()</code> method. This will change the timer to a short
 * delay until events for this participant are received.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class EventManager {
	
	private Timer timer = null; 					// schedule event pooling
    private int serial;								// serial of last event
    private boolean available = true;				// no request pending?
	private boolean waiting = false;				// waiting 4 you own events?
	private boolean holding = false;				// hold on wait?   
	private static final int SHORT_DELAY =    1000;	// delay when waiting
	private static final int LONG_DELAY  = 45*1000;	// delay when idle

	
	final static Logger LOGGER = Logger.getLogger("");
	
	
    private final EventServiceAsync updateService = GWT
            .create(EventService.class);
	
    private  Map<Class<? extends MooshakEvent>,
    			List<MooshakEventListener<? extends MooshakEvent>>> 
    	listeners = new HashMap<Class<? extends MooshakEvent>,
    			List<MooshakEventListener<? extends MooshakEvent>>>(); 
    	
    
    static EventManager manager = null;

    /**
     * Get a single event manager with given features
     * @param activity
     * @param participant
     * @return
     */
    public static EventManager getInstance() {
    	
    	if(manager == null)
    		manager = new EventManager();

    	manager.checkStarted();
    	
    	return manager;
    }
    
    /**
     * Cannot instance this class. Just a single manager 
     */
	private EventManager() {}
	
	/**
	 * Make sure this event manager is pooling events from the server. 
	 * May have been stopped and in need to be restarted 
	 */
	private void checkStarted() {
		if(timer == null || ! timer.isRunning())
			initSerial();
	}
	
	/** 
	 * Get the serial of the first event posted after service started 
	 */
	private void initSerial() {	
		updateService.getSerial(new Date(),
				new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.log(Level.SEVERE,caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						serial = result;
						schedulePooling();
					}
		});
	}
	
	/**
	 * Schedule regular updates to fetch new events
	 */
	private void schedulePooling() {
		timer = new Timer() {

			@Override
			public void run() {
				if(available) { 
					available = false;
					updateService.getEvents(serial,
							new AsyncCallback<List<MooshakEvent>>() {

						@Override
						public void onFailure(Throwable caught) {
						
							AuthenticationPresenter.logout(caught);
						}

						@Override
						public void onSuccess(List<MooshakEvent> result) {
							processUpdateMessages(result);
						}
					});
				}
			};
		};
		timer.scheduleRepeating(LONG_DELAY);  
	}

	/**
	 * Make pooling delay shorter until it becomes idle again
	 */
	public void refresh() {
		if(timer != null) {
			timer.scheduleRepeating(SHORT_DELAY);
			waiting = true;
			holding = false;
		}
	}
	
	
	/**
	 * Stop requesting events 
	 */
	public void stop() {
		if(timer != null && timer.isRunning()) {
			timer.cancel();
			LOGGER.log(Level.INFO,"Stop processing events");
		}
	}

	
	/**
	 * Add an event listener with a given type 
	 * @param type		of update events, must extend UpdateEvents
	 * @param listener  of events, must extend UpdateEventListener for 
	 * 					the same type 
	 */
	public <T extends MooshakEvent> void addListener(Class<T> type,
			MooshakEventListener<T> listener) {
		
		if(!listeners.containsKey(type))
			listeners.put(type, 
					new LinkedList<MooshakEventListener<? extends MooshakEvent>>());
			
		listeners.get(type).add(listener);
	}
	
	/**
	 * Remove an event listener  with a given type, if it exists 
	 * @param type		of update events, must extend UpdateEvents
	 * @param listener  of events, must extend UpdateEventListener for 
	 * 					 the same type 
	 */
	public <T extends MooshakEvent> void removeListener(
			Class<T> type,
			MooshakEventListener<T> listener) {

		if(listeners.containsKey(type))
			listeners.get(type).remove(listener);
	}
	
	Date last = new Date();
	
	/**
	 * Processes list of messages sent by the server and delivers them
	 * to requesters  
	 * @param messages
	 */
	private <T extends MooshakEvent> 
		void processUpdateMessages(List<T> messages) {
				
		if(messages == null) // queue is not active
			return;
		
		Date now = new Date(); 
		// LOGGER.log(Level.INFO,"#:"+messages.size()+" t:"+(now.getTime()-last.getTime()));
		last = now;
		
		if(waiting && messages.size() > 0) {
			holding = true;
		} else if(holding && messages.size() == 0) {
			waiting = false;
			timer.scheduleRepeating(LONG_DELAY);
		}
		
		for(T message: messages) {
			serial = message.getSerial();
						
			@SuppressWarnings("unchecked")
			Class<T> type = (Class<T>) message.getClass();
			if(listeners.containsKey(type)) 
				for(MooshakEventListener<? extends MooshakEvent> listener: 
						listeners.get(type)) {
					@SuppressWarnings("unchecked")
					MooshakEventListener<T> safe = 
						(MooshakEventListener<T>) listener;
					
					try {
						safe.receiveEvent(message);
					} catch (Exception e) {
						// Catch possible exceptions before they break events (silently)
					}
				}
		}
		available = true;
	}
		
	
}
