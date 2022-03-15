package pt.up.fc.dcc.mooshak.content.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * An LRU cache, based on <code>LinkedHashMap</code>.
 *
 * This cache has a fixed maximum number of elements (<code>cacheSize</code>).
 * If the cache is full and another entry is added, the LRU (least recently
 * used) entry is dropped.
 *
 * This class is thread-safe. Author: Christian d'Heureuse, Inventec Informatik
 * AG, Zurich, Switzerland<br>
 * Multi-licensed: EPL / LGPL / GPL / AL / BSD.
 * 
 * Extended and adapted by:
 * 
 * @author josepaiva
 */
public class LRUCache<K, V> {

	private static final float HASH_TABLE_LOAD_FACTOR = 0.75f;

	private LinkedHashMap<K, LRUCacheItem> map;
	private int cacheSize;
	private long ttl;

	// Cache statistics
	private long cacheHits = 0;
	private long cacheMisses = 0;
	private long cacheExpired = 0;
	private long cacheOverflows = 0;

	/**
	 * Creates an LRUCache without size limits or time expiration for entries
	 */
	public LRUCache() {
		this(0, -1);
	}

	/**
	 * Creates an LRUCache without time expiration for entries
	 * 
	 * @param cacheSize
	 *            the maximum number of entries that will be kept in this cache.
	 */
	public LRUCache(int cacheSize) {
		this(cacheSize, -1);
	}

	/**
	 * @param cacheSize
	 *            the maximum number of entries that will be kept in this cache.
	 * @param ttl
	 *            the maximum time (in ms) that an entry remains valid
	 */
	public LRUCache(int cacheSize, long ttl) {
		if (cacheSize < 0)
			throw new IllegalArgumentException("Cannot pass a negative value to cacheSize");

		this.cacheSize = cacheSize;
		this.ttl = ttl;
		int hashTableCapacity = (int) Math.ceil(cacheSize / HASH_TABLE_LOAD_FACTOR) + 1;
		map = new LinkedHashMap<K, LRUCacheItem>(hashTableCapacity, HASH_TABLE_LOAD_FACTOR, true) {
			// (an anonymous inner class)
			private static final long serialVersionUID = 1;

			@Override
			protected boolean removeEldestEntry(Map.Entry<K, LRUCacheItem> eldest) {
				if (LRUCache.this.cacheSize <= 0)
					return false;
				if (size() > LRUCache.this.cacheSize) {
					LRUCache.this.cacheOverflows++;
					return true;
				}

				return false;
			}
		};
	}

	/**
	 * Retrieves an entry from the cache.<br>
	 * The retrieved entry becomes the MRU (most recently used) entry.
	 * 
	 * @param key
	 *            the key whose associated value is to be returned.
	 * @return the value associated to this key, or null if no value with this
	 *         key exists in the cache.
	 */
	public synchronized V get(K key) {
		LRUCacheItem item = (LRUCacheItem) map.get(key);

		if (item == null) {
			cacheMisses++;
			return null;
		}

		if (item.hasExpired()) {
			cacheExpired++;
			cacheMisses++;
			map.remove(key);
			return null;
		}

		cacheHits++;
		return item.value;
	}

	/**
	 * Adds an entry to this cache. The new entry becomes the MRU (most recently
	 * used) entry. If an entry with the specified key already exists in the
	 * cache, it is replaced by the new entry. If the cache is full, the LRU
	 * (least recently used) entry is removed from the cache.
	 * 
	 * @param key
	 *            the key with which the specified value is to be associated.
	 * @param value
	 *            a value to be associated with the specified key.
	 */
	public synchronized void put(K key, V value) {
		map.put(key, new LRUCacheItem(value));
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		map.clear();
		cacheHits = 0;
		cacheExpired = 0;
		cacheMisses = 0;
		cacheOverflows = 0;
	}

	/**
	 * Destroys the cache.
	 */
	public void destroy() {
		clear();
		map = null;
	}

	/**
	 * Returns the number of used entries in the cache.
	 * 
	 * @return the number of entries currently in the cache.
	 */
	public synchronized int usedEntries() {
		return map.size();
	}

	/**
	 * Returns a <code>Collection</code> that contains a copy of all cache
	 * entries.
	 * 
	 * @return a <code>Collection</code> with a copy of the cache content.
	 */
	public synchronized Collection<Map.Entry<K, V>> getAll() {
		LinkedHashMap<K, V> entries = new LinkedHashMap<>();

		for (Entry<K, LRUCache<K, V>.LRUCacheItem> entry : map.entrySet()) {
			entries.put(entry.getKey(), entry.getValue().value);
		}

		return new ArrayList<Map.Entry<K, V>>(entries.entrySet());
	}

	/**
	 * Goes through every item in the cache, and removes it if it has expired.
	 */
	public synchronized void removeExpired() {
		LinkedHashMap<K, LRUCacheItem> mapCopy = new LinkedHashMap<>(map);
		for (Iterator<K> it = mapCopy.keySet().iterator(); it.hasNext();) {
			Object key = it.next();
			LRUCacheItem item = (LRUCacheItem) mapCopy.get(key);

			if (item.hasExpired()) {
				cacheExpired++;
				map.remove(key);
			}
		}
	}

	/**
	 * @return the cacheHits
	 */
	public long getCacheHits() {
		return cacheHits;
	}

	/**
	 * @param cacheHits
	 *            the cacheHits to set
	 */
	public void setCacheHits(long cacheHits) {
		this.cacheHits = cacheHits;
	}

	/**
	 * @return the cacheMisses
	 */
	public long getCacheMisses() {
		return cacheMisses;
	}

	/**
	 * @param cacheMisses
	 *            the cacheMisses to set
	 */
	public void setCacheMisses(long cacheMisses) {
		this.cacheMisses = cacheMisses;
	}

	/**
	 * @return the cacheExpired
	 */
	public long getCacheExpired() {
		return cacheExpired;
	}

	/**
	 * @param cacheExpired
	 *            the cacheExpired to set
	 */
	public void setCacheExpired(long cacheExpired) {
		this.cacheExpired = cacheExpired;
	}

	/**
	 * @return the cacheOverflows
	 */
	public long getCacheOverflows() {
		return cacheOverflows;
	}

	/**
	 * @param cacheOverflows
	 *            the cacheOverflows to set
	 */
	public void setCacheOverflows(long cacheOverflows) {
		this.cacheOverflows = cacheOverflows;
	}

	/**
	 * Calculates the hit rate of the cache
	 * 
	 * @return
	 */
	public int getHitRate() {
		if (cacheHits + cacheMisses + cacheExpired == 0) {
			return 0;
		} else {
			double result = cacheHits;
			result = result / (cacheHits + cacheMisses + cacheExpired) * 100;
			return (int) result;
		}
	}

	/**
	 * Wraps an item of the LRUCache to add time-based expiration
	 * 
	 * @author josepaiva
	 */
	private class LRUCacheItem {
		public V value;
		public long creationTime;

		public LRUCacheItem(V value) {
			this.creationTime = System.currentTimeMillis();
			this.value = value;
		}

		/**
		 * Checks if the item is older than the TTL
		 * 
		 * @return true if the item is older than the TTL, false otherwise
		 */
		public boolean hasExpired() {
			if (ttl <= 0) { // if time to live is less or equal to zero, then
							// expiration is disabled
				return false;
			} else {
				return System.currentTimeMillis() - creationTime > ttl;
			}
		}
	}

}