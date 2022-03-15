package pt.up.fc.dcc.mooshak.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Publish/subscribe mechanism for events on persistent objects
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class PersistentObjectSender {

	Map<
		Class<? extends PersistentObject>,
		List<PersistentObjectListener<? extends PersistentObject>>> listeners 
			= new HashMap<>();

	
	public void addPersistentObjectListener(
			Class<? extends PersistentObject> type,
			PersistentObjectListener<? extends PersistentObject> listener) {
		List<PersistentObjectListener<? extends PersistentObject>> list;
		
		if(listeners.containsKey(type)) 
			list = listeners.get(type);
		else {
			 list = new ArrayList<>();
			 listeners.put(type,list);
		}
			
		list.add(listener);
	}
	
	public void removePersistentObjectListener(
			Class<? extends PersistentObject> type,
			PersistentObjectListener<?> listener) {
		
		if(listeners.containsKey(type)) {
			List<PersistentObjectListener<?>> list = listeners.get(type);
			
			list.remove(listener);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	public <T extends PersistentObject> void broadcat(T persistent) 
			throws MooshakContentException {
		
		Class<?> type = persistent.getClass();
		
		while(! Object.class.equals(type)) {
			if(listeners.containsKey((type))) {
				
				Object[] typeListeners = listeners.get(type).toArray(); // fix ConcurrentModification
				for(Object obj: typeListeners) {
					PersistentObjectListener<? extends PersistentObject> listener =
							(PersistentObjectListener<? extends PersistentObject>) obj;
					
					PersistentObjectListener<T> safe = 
						(PersistentObjectListener<T>) listener;

					safe.receivedPersistentObject(persistent);
				}
			}
			type = type.getSuperclass();
		}
	}

	 
}
