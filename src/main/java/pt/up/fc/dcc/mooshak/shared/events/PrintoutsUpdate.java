package pt.up.fc.dcc.mooshak.shared.events;

/**
 * Event with record to add to printouts listings. This event is sent to all
 * registered recipients and is delivered to components that subscribed it
 * on the client.
 * 
 * @author José Paulo Leal <zp.dcc.fc.up.pt>
 */
public class PrintoutsUpdate extends ListingUpdateEvent {
	
	public PrintoutsUpdate() {
		super();
	}
}
