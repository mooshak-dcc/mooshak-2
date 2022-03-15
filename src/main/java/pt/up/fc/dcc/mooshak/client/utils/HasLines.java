package pt.up.fc.dcc.mooshak.client.utils;

import java.util.Map;

public interface HasLines<K,V> {
	
	public Map<String,V> getLine(K key);
	
	public void setLine(K key, Map<String,V> line);
	
}
