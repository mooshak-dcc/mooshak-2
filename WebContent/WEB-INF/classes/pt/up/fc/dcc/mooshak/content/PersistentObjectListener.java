package pt.up.fc.dcc.mooshak.content;

/**
 * Listener of events on persistent objects
 * 
 * @param <T> extends PersistentObject
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface PersistentObjectListener<T extends PersistentObject> {
	
	public void receivedPersistentObject(T persistent) throws MooshakContentException;
	

}
