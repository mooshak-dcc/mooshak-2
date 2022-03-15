package pt.up.fc.dcc.mooshak.evaluation.graph;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.CompositePropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.PropertyValue;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyName;
import pt.up.fc.dcc.mooshak.evaluation.graph.data.SimplePropertyValue;

public class PropertyValueTest {
	
	@Test
	public void testPropertiesCreation(){
		Map<PropertyName,PropertyValue> map = new HashMap<>();
		
		map.put(new SimplePropertyName("multiplicity"), new SimplePropertyValue("1"));
		map.put(new CompositePropertyName("uml:attribute", "name"), new CompositePropertyValue("type","String"));
		
		Iterator<PropertyName> mapIterator = map.keySet().iterator();
		PropertyName key;
		while(mapIterator.hasNext()){
			key = mapIterator.next();
			if(map.get(key).isSimple())
				System.out.println("key = (" + key.toString() + ") | value = " + map.get(key).toString());
			else
				System.out.println("key = (" + key.toString() + ") | value = " + map.get(key).toString());		
			if(key.equals(new SimplePropertyName("multiplicity")))
				assertEquals("Test1", "1", ((SimplePropertyValue)map.get(key)).getValue());
			else
				assertEquals("Test2", "\n\ttype : String", map.get(key).toString());
		}
	}

	
}
