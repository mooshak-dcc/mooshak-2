package pt.up.fc.dcc.mooshak.client.guis.admin.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Copy object with given ID
 * 
 * @author josepaiva
 */
public class CopyObjectEvent extends GwtEvent<CopyObjectEventHandler> {
	
	public static Type<CopyObjectEventHandler> TYPE = 
			new Type<CopyObjectEventHandler>();
	
	private String objectId = null;
	
	public CopyObjectEvent(String objectId) {
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
	public Type<CopyObjectEventHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(CopyObjectEventHandler handler) {
		handler.onCopyObject(this);
	}


}
