package pt.up.fc.dcc.mooshak.message;


import java.io.Serializable;
import java.util.LinkedHashMap;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * A collection of field values associated with a path
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
@XmlRootElement
public class MooshakInstanceValues implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String path = null; 
	public LinkedHashMap<String,String> values = new LinkedHashMap<>();

	public String put(String key, String value) {
		return values.put(key, value);
	}

	public int size() {
		return values.size();
	}

	public String get(Object key) {
		return values.get(key);
	}

	public String remove(Object key) {
		return values.remove(key);
	}
	
	
}
