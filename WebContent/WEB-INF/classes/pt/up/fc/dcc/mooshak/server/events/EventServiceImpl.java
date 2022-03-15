package pt.up.fc.dcc.mooshak.server.events;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.events.EventService;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.server.MooshakServiceServlet;
import pt.up.fc.dcc.mooshak.managers.Manager;
import pt.up.fc.dcc.mooshak.shared.events.LogoutEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

/**
 * <p> Implementation of the update service.
 * Fetches the new events for the requesting recipient 
 * in given contest, i.e those posted after last event serial.
 * </p>
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class  EventServiceImpl 
		extends MooshakServiceServlet implements EventService

{
	private static final long serialVersionUID = 1L;

	EventSender eventSender = EventSender.getEventSender();
	
	protected final static Logger LOGGER = Logger.getLogger("");
	
	@Override
	public List<MooshakEvent> getEvents(int serial) {
		Session session = getSession();
		
		if(serial == -1) {
			LOGGER.log(Level.INFO,"Get events: invalid serial");
			
			return LogoutEvent.getLogoutEventAsList("Session timeout (?)");
		} else if(session == null) {
			LOGGER.log(Level.INFO,"Get events: no session available");
			
			return LogoutEvent.getLogoutEventAsList("Session timeout");
		} else {
			String contest = session.getContestId();
			Recipient recipient = null;
		
			try {
				Authenticable participant = session.getParticipant();
				
				switch(participant.getProfile().getIdName()) {
				case "admin":
				case "judge":
					// these profiles receive all events
					break;
				default:
					recipient = Manager.makeRecipient(session);
				}
				
			} catch (MooshakContentException cause) {
				LOGGER.log(Level.SEVERE,"getEvents: getting user ID",cause);
			}
		
			/* LOGGER.log(Level.INFO, 
			 * "get events:\t"+contest+"\t"+recipient+"\t"+serial);*/
			 List<MooshakEvent> events = 
					 eventSender.eventsToDeliver(contest, recipient, serial);
			 
			 scriptLog("getEvents",serial+"");
			 
			 return events;
		}
	}

	@Override
	public int getSerial(Date date) {
		Session session = getSession();
		
		if(session == null) {
			LOGGER.log(Level.INFO,"Get serial: No session available");
			
			return -1;
		} else {
			String contest = session.getContestId();
		
			// LOGGER.log(Level.INFO, "get serial:\t"+contest+"\t"+date);
			int serial =  eventSender.getSerial(contest, date);
			
			scriptLog("getSerial",date.getTime()+"");
			
			return serial;
		}
	}

	
	Session session = null; // This is an injected session for testing

	/**
	 * Get a Mooshak session recorded in the HTTP Session.
	 * May return a session injected with setSession(), 
	 * if one was provided for testing. 
	 * 
	 * @return
	 */
	private Session getSession() {
		if(session == null)
			return (Session) getThreadLocalRequest()
					.getSession()
					.getAttribute("session");
		else 
			return session; 
	}

	/**
	 * Set a session to use with this event service implementation.
	 * This is for testing purposes only. Avoid using it in production code.
	 * @param session
	 */
	public void setSession(Session session) {
		this.session = session; 
	}

}
