package pt.up.fc.dcc.mooshak.content.util;

import java.util.Enumeration;
import java.util.Hashtable;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Generic implementation of a pool of objects 
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 *
 * @param <T> Type of objects in the pool
 */
public abstract class ObjectPool<T> {
	private static final long EXPIRATION_TIME_MILLIS = 5 * 60 * 1000; // 5 minutes

	private Hashtable<T, Long> locked, unlocked;

	public ObjectPool() {
		locked = new Hashtable<T, Long>();
		unlocked = new Hashtable<T, Long>();
	}

	/**
	 * Create an object and add to the pool
	 * 
	 * @return T object created
	 * @throws MooshakException - if an exception occurs while creating an object
	 */
	protected abstract T create() throws MooshakException;

	/**
	 * Validate the object (check if it is still valid)
	 * 
	 * @param o T object to be validated
	 * @return <code>true</code> if object is valid, <code>false</code> otherwise
	 */
	public abstract boolean validate(T o);

	/**
	 * Expire an object from the pool
	 * 
	 * @param o T object to be expired
	 */
	public abstract void expire(T o);

	/**
	 * Checkout an object from the pool
	 * 
	 * @return T object checked out
	 * @throws MooshakException - if an exception occurs while creating an object
	 */
	public synchronized T checkOut() throws MooshakException {
		long now = System.currentTimeMillis();
		
		T t;
		if (unlocked.size() > 0) {
			
			Enumeration<T> e = unlocked.keys();
			while (e.hasMoreElements()) {
				
				t = e.nextElement();
				if ((now - unlocked.get(t)) > EXPIRATION_TIME_MILLIS) { // object has expired
					unlocked.remove(t);
					expire(t);
					t = null;
				} else {
					
					if (validate(t)) { // everything fine
						unlocked.remove(t);
						locked.put(t, now);
						return (t);
					} else { // object failed validation
						unlocked.remove(t);
						expire(t);
						t = null;
					}
				}
			}
		}
		
		// no objects available, create a new one
		t = create();
		locked.put(t, now);
		
		return (t);
	}

	/**
	 * Check-in object to the pool
	 * 
	 * @param o T object checked in
	 */
	public synchronized void checkIn(T o) {
		
		long now = System.currentTimeMillis();
		long expire = locked.remove(o);
		
		if ((now - expire) <= EXPIRATION_TIME_MILLIS) // object has not expired
			unlocked.put(o, now);
	}
	
	/**
	 * Set expiration time of all objects to 0
	 */
	public void setAllObjectsToExpire() {
		
		synchronized (unlocked) {
			for (T obj : unlocked.keySet())
				unlocked.put(obj, 0L);
		}
		
		synchronized (locked) {
			for (T obj : locked.keySet())
				locked.put(obj, 0L);
		}
	}
}