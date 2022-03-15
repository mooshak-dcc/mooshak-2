package pt.up.fc.dcc.mooshak.client.events;

import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Event API. 
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
@RemoteServiceRelativePath("event")
public interface EventService extends RemoteService {

	/**
	 * Gets the serial number of the last event sent before the given data
	 * @param date		after which you want the serial
	 * @return
	 */
	int getSerial(Date date);
	
	/**
	 * Get a list of events sent after given serial 
	 * @param serial	of last event received
	 * @return
	 */
	List<MooshakEvent> getEvents(int serial);
	
	
}
