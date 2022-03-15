package pt.up.fc.dcc.mooshak.client.guis.judge.event;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;

import com.google.gwt.event.shared.GwtEvent;

public class RegisterDeliveryEvent extends
		GwtEvent<RegisterDeliveryEventHandler> {
	
	public static Type<RegisterDeliveryEventHandler> TYPE = 
			new Type<RegisterDeliveryEventHandler>();
	
	private String id;
	private Kind kind;
	
	public RegisterDeliveryEvent(String id, Kind kind) {
		this.id = id;
		this.kind = kind;
	}
	
	@Override
	public Type<RegisterDeliveryEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(RegisterDeliveryEventHandler handler) {
		handler.onRegisterDelivery(this);
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the kind
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(Kind kind) {
		this.kind = kind;
	}


}
