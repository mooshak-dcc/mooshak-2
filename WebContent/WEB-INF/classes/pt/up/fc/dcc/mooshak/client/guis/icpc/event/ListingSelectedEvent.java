package pt.up.fc.dcc.mooshak.client.guis.icpc.event;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;

import com.google.gwt.event.shared.GwtEvent;

public class ListingSelectedEvent 
	extends GwtEvent<ListingSelectedEventHandler> {

	public static Type<ListingSelectedEventHandler> TYPE = 
			new Type<ListingSelectedEventHandler>();
	
	private Kind kind;
	
	public ListingSelectedEvent(Kind kind) {
		this.kind = kind;
	}
	
	/**
	 * @return the kind of listing
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * @param kind the kind of listing to set
	 */
	public void setKind(Kind kind) {
		this.kind = kind;
	}

	@Override
	public Type<ListingSelectedEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ListingSelectedEventHandler handler) {
		handler.onListingSelected(this);
	}

}
