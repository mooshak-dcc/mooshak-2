package pt.up.fc.dcc.mooshak.client.utils;

import java.util.HashMap;
import java.util.Map;

public class BasicHasLines<K, V> implements HasLines<K, V> {

	Map<K,Map<String,V>> table = new HashMap<K,Map<String,V>>();
	
	@Override
	public Map<String,V> getLine(K key) {
		return table.get(key);
	}

	@Override
	public void setLine(K key, Map<String,V> line) {
		table.put(key,line);
	}

}
