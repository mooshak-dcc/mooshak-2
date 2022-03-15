package pt.up.fc.dcc.mooshak.content.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Simple test case for the LRUCache
 * 
 * @author josepaiva
 */
public class LRUCacheTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testInsertion() {
		LRUCache<String, String> cache = new LRUCache<>(3, -1);
		cache.put("1", "one"); // 1
		cache.put("2", "two"); // 2 1
		cache.put("3", "three"); // 3 2 1
		cache.put("4", "four"); // 4 3 2

		Assert.assertNotNull(cache.get("2"));
		Assert.assertNotNull(cache.get("3"));
		Assert.assertNotNull(cache.get("4"));
		Assert.assertNull(cache.get("1"));
		Assert.assertTrue(1 == cache.getCacheOverflows());
		Assert.assertTrue(1 == cache.getCacheMisses());
		Assert.assertTrue(3 == cache.getCacheHits());
		Assert.assertTrue(3 == cache.usedEntries());
		
		cache.removeExpired();
		Assert.assertTrue(0 == cache.getCacheExpired());
	}

	@Test
	public void testTTL() throws InterruptedException {
		LRUCache<String, String> cache = new LRUCache<>(3, 100);
		cache.put("1", "one"); // 1
		Thread.sleep(30);
		cache.put("2", "two"); // 2 1
		cache.put("3", "three"); // 3 2 1
		
		Thread.sleep(80);

		Assert.assertNull(cache.get("1"));
		Assert.assertNotNull(cache.get("2"));
		Assert.assertNotNull(cache.get("3"));
		Assert.assertTrue(0 == cache.getCacheOverflows());
		Assert.assertTrue(1 == cache.getCacheMisses());
		Assert.assertTrue(1 == cache.getCacheExpired());
		Assert.assertTrue(2 == cache.getCacheHits());
		Assert.assertTrue(2 == cache.usedEntries());
	}

}
