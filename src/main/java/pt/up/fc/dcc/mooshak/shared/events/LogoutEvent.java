package pt.up.fc.dcc.mooshak.shared.events;

import java.util.ArrayList;
import java.util.List;

/**
 * Event to force logout on client side, typically due to an error.
 * 
 * @author José Paulo Leal <zp.dcc.fc.up.pt>
 */
public class LogoutEvent extends MooshakEvent {
	
	String reason = "";
	
	public LogoutEvent() {
		super();
	}
	
	public LogoutEvent(String reason) {
		super();
		
		this.reason = reason;
	}

	/**
	 * Convenience method for crating a list of messages with a single
	 * logout event, typically on error
	 *  
	 * @param message
	 * @return
	 */
	public static List<MooshakEvent> getLogoutEventAsList(String message) {
		List<MooshakEvent> events = new ArrayList<MooshakEvent>();
		
		events.add(new LogoutEvent(message));
		
		return events;
	}
	
	/**
	 * Get reason for logout
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * Set reason for logout
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

	
	
}
