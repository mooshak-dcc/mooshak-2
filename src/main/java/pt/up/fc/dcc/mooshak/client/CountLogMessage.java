package pt.up.fc.dcc.mooshak.client;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.Timer;

/**
 * Class for aggregating repeated log messages.
 * Logging is delayed and if equal messages are reported then they are counted
 * and the final messages reported the number of repeated messages.
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class CountLogMessage {
	
	static int DELAY = 100;
	static Logger LOGGER = Logger.getLogger("");
	
	static Map<String,CountLogMessage> counters = 
			new HashMap<String,CountLogMessage>();
	
	/**
	 * Add counted message as info 
	 * @param message
	 */
	public static void info(String message) {
		log(message,Level.INFO);
	}
	
	/**
	 * Add counted message as warning 
	 * @param message
	 */
	public static void warn(String message) {
		log(message,Level.WARNING);
	}
	
	public static void log(String message,Level level) {
		CountLogMessage counter;
		
		if((counter = counters.get(message)) == null)
			counters.put(message,new CountLogMessage(message,level));
		else
			counter.increment();
	}
	
	int count = 0;
	Timer timer;
	
	private CountLogMessage(final String message,final Level level ) {
		timer = new Timer() {

			@Override
			public void run() {
				LOGGER.log(level,message+" (repeated "+count+" times)");
				counters.remove(message);
			}
			
		};
		increment();
	}

	private void increment() {
		count++;
		timer.schedule(DELAY);
	}

	

}
