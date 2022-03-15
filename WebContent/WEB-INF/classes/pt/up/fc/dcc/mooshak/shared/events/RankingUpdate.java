package pt.up.fc.dcc.mooshak.shared.events;

/**
 * Event with record to add to rankings listings. This event is sent to all
 * registered recipients and is delivered to components that subscribed it
 * on the client.
 * 
 * @author José Paulo Leal <zp.dcc.fc.up.pt>
 */
public class RankingUpdate extends ListingUpdateEvent {
	
	public RankingUpdate() {
		super();
	}
}
