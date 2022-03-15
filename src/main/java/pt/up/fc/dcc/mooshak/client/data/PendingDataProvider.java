package pt.up.fc.dcc.mooshak.client.data;

import pt.up.fc.dcc.mooshak.shared.events.ListingUpdateEvent;

/**
 * Data provider for Mooshak Pending lists. 
 * 
 * @author josepaiva
 */
public class PendingDataProvider extends ListingDataProvider {

	private static PendingDataProvider dataProvider = null;
	
	/**
	 * Get a Data Provider for a given problem
	 * @param problem
	 * @return
	 */
 	public static PendingDataProvider getDataProvider() {
		
		if(dataProvider == null)
			dataProvider = new PendingDataProvider();
		
		return dataProvider;
	}
	
	private PendingDataProvider() {
		super(Kind.PENDING);
	}

	/**
	 * Processed when an update is received
	 * @param event
	 * @param kind 
	 */
	public void receiveUpdateEvent(ListingUpdateEvent event, 
			Kind kind) {
		
		String state = event.getRecord().get("state");
		
		if(state == null)
			return;
		switch (state.toUpperCase()) {
		case "PENDING":
		case "UNDELIVERED":
		case "UNANSWERED":
			addOrChangeRow(event.getId() + "_" + kind.toString(), 
					event.getRecord());
			break;

		default:
			removeRow(event.getId() + "_" + kind.toString());
			break;
		}
	}

	/**
	 * Removes a row with given id 
	 * @param id
	 */
	private void removeRow(String id) {
		int index = findRowIndex(id);
		
		if(index == -1)
			return;
		
		list.remove(index);
		addFillerRows(1);
		
		flush();
		
		if (hasFilters())
			setFilters(filters);
		else 
			resetFilter();
		
		refresh();
	}
	
	
	
}
