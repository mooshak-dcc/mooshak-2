package pt.up.fc.dcc.mooshak.shared.events;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Event sent to clients when a submission listings has a new record.
 * Listing update events must extend this class.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public abstract class ListingUpdateEvent extends MooshakEvent {

	private Map<String,String> record = new HashMap<String,String>();
	
	private String id;
	
	public ListingUpdateEvent() {
		super();
	}
	
	public ListingUpdateEvent(Map<String,String> record) {
		super();

		this.record = record;
	}

	/**
	 * @return the text
	 */
	public Map<String,String> getRecord() {
		return record;
	}

	/**
	 * @param text the text to set
	 */
	public void setRecord(Map<String,String> record) {
		this.record = record;
	}

	/**
	 * Add a keyed value to the record.
	 * @param key
	 * @param value
	 */
	public void addValue(String key,String value) {
		record.put(key, value);
	}
	
	/** 
	 * Returns the value indexed by key in the new record
	 * @param key
	 * @return
	 */
	public String getValue(String key) {
		return record.get(key);
	}
	
	/**
	 * Returns a set with all the keys in the new records
	 * @return
	 */
	public Set<String> getKeys() {
		return record.keySet();
	}

	/**
	 * Get line id
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * Set line id
	 * @param id
	 */
	public void setId(String id) {
		this.id = id;
	}
}
