package pt.up.fc.dcc.mooshak.content.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Static methods on enumerated types. Enumeration types are used to 
 * to convert names to enumeration and to get list item names.
 *   
 * @author José Paulo Leal zp@dcc.fc.up.pt
 *
 */
public class Enums {
	
	public static final Logger LOGGER = Logger.getLogger("");
	
	/**
	 * A list of names (strings) in enumerated type
	 * @param <T> extension of an enumeration
	 * @param type type of enumeration (class) 
	 * @return a list of strings with names of 
	 */
	public static <T extends Enum<T>> List<String> getNames(Class<T> type) {
		List<String> names = new Vector<String>();
		Method toStringMethod = null;
		
		try {
			toStringMethod = type.getMethod("toString");
		} catch (NoSuchMethodException | SecurityException cause) {
			LOGGER.log(Level.SEVERE,"Security excepti",cause);
		}
		
		for(Field field: type.getDeclaredFields())
			if(field.getType().equals(type)) {
				String name = field.getName();
				T value = Enum.valueOf(type,name);
				
				// try to use the toString method of the enum
				// there should always be a toString() method, but jut in case
				if(toStringMethod != null)
					try {
						name = (String) toStringMethod.invoke(value);
					} catch (IllegalAccessException 
							| IllegalArgumentException
							| InvocationTargetException cause) {
						LOGGER.log(Level.SEVERE,"Invoking toString on enum",cause);
					}
					
				names.add(name);
				
			}
					
					
		return names;
	}
	

}
