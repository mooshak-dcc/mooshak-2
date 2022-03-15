package pt.up.fc.dcc.mooshak.managers;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.management.UnixOperatingSystemMXBean;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

/**
 * Abstract class with definitions common to all managers.
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Manager {
	protected final static Logger LOGGER = Logger.getLogger("");
	
	protected static OperatingSystemMXBean OS = 
			ManagementFactory.getOperatingSystemMXBean();
	protected static final UnixOperatingSystemMXBean LINUX = 
			OS instanceof UnixOperatingSystemMXBean ? 
					(UnixOperatingSystemMXBean) OS : null;
	protected static Runtime RUNTIME = Runtime.getRuntime();
	
	private static final double LOAD_RATIO = 0.7D;
	
	/**
	 * Load of host is low enough to continue performing heavy tasks
	 * 
	 * @return {@code true} if can perform task, otherwise {@code false}
	 */
	public static boolean isHostLoadAcceptable() {
		
		return OS.getSystemLoadAverage() / 
				OS.getAvailableProcessors() < LOAD_RATIO;
	}
	
	
	/**
	 * Convenience method for creating a recipient for given session
	 * @param session
	 * @throws MooshakContentException
	 */
	public static Recipient makeRecipient(Session session) 
			throws MooshakContentException {
		
		String userId = null;
		String sessionId = null;
		if(session != null) {
			Authenticable auth = session.getParticipant();
		
			userId = (auth == null ? null : auth.getIdName());
			sessionId = session.getIdName();
		}
		return new Recipient(userId,sessionId);
	}
	
	protected void info(String message) {
		LOGGER.log(Level.INFO,message);
	}
	
	protected void warning(String message) {
		LOGGER.log(Level.WARNING,message);
	}
	
	protected void severe(String message) {
		LOGGER.log(Level.SEVERE,message);
	}
	
	protected void severe(String message,Exception cause) {
		LOGGER.log(Level.SEVERE,message,cause);
	}
	
	protected void error(String message) throws MooshakException {
		warning(message);
		throw new MooshakException(message);
	}
	
	protected void error(String message,Exception cause) throws MooshakException {
		severe(message,cause);
		throw new MooshakException(message,cause);
	}

}
