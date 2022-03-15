package pt.up.fc.dcc.mooshak.client.guis.admin.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Show object with given ID on the object editor
 * 
 * @author José Paulo Leal <zp@dcc.c.up.pt>
 */
public class ShowObjectEvent  extends GwtEvent<ShowObjectEventHandler> {
	
	public static Type<ShowObjectEventHandler> TYPE = 
			new Type<ShowObjectEventHandler>();
	
	private String objectId = null;
	
	public ShowObjectEvent(String objectId) {
		super();
		this.objectId = objectId;
	}

	/**
	 * @return the objectId
	 */
	public String getObjectId() {
		return objectId;
	}

	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}



	@Override
	public Type<ShowObjectEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ShowObjectEventHandler handler) {
		handler.onShowObject(this);
	}


}
