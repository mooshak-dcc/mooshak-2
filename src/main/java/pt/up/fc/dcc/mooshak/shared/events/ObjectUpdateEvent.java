package pt.up.fc.dcc.mooshak.shared.events;

/**
 * Event for notifying of changes in a particular object.
 * The only data this event carries is the id of the updated object.
 * If changes are of interest then the object will have to be fetched.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class ObjectUpdateEvent extends MooshakEvent {

	String id;

	public ObjectUpdateEvent() {
		super();
	}
	
	public ObjectUpdateEvent(String id) {
		super();
		this.id = id;
	}

	/**
	 * Get the id of the updated object 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set the id of the updated object 
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
}
