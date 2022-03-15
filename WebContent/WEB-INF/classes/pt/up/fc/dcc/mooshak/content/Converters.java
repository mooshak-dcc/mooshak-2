package pt.up.fc.dcc.mooshak.content;

import java.awt.Color;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.Attributes.Attribute;
import pt.up.fc.dcc.mooshak.content.util.Colors;
import pt.up.fc.dcc.mooshak.content.util.Strings;


/**
 * Static method for converting between String values and objects.
 * Simple cases where conversion depends only on the object type type
 * are handled by converters stored in a static map. This way a converter
 * is efficiently located using the type as key. 
 * In some cases the selection of a conversion depends on more complex tests
 * and must be handled ah hoc. 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class Converters {

	/*------------------------------------------------------------*\
	 * map with converters for simple types                       *
	\*------------------------------------------------------------*/
	
	private static final int MILLIS_IN_A_SEC = 1000;

	private interface  TypeConverter<T>  {
		T fromString(String value) throws MooshakContentException;
		String toString(Object value) throws MooshakContentException;
	}

	private static Map<Class<?>,TypeConverter<?>> converters = new
		HashMap<Class<?>,TypeConverter<?>>();
	
	static {
		
		converters.put(String.class,new TypeConverter<String>() {
			
			@Override
			public String fromString(String value) {
				return value;
			}

			@Override
			public String toString(Object value) throws MooshakContentException {
				if(value instanceof String)
					return (String) value;
				else
					throw new MooshakContentException("Not a string "+value);
			}
		});
		
		converters.put(Integer.class, new TypeConverter<Integer>() {
			
			@Override
			public Integer fromString(String value) {
			
				return new Integer(value);
			}
			
			@Override
			public String toString(Object value) throws MooshakContentException {
				if(value instanceof Integer)
					return Integer.toString((Integer) value);
				else
					throw new MooshakContentException("Not an integer "+value);
			}
		});
		
		converters.put(Long.class, new TypeConverter<Long>() {

			@Override
			public Long fromString(String value) {
			
				return new Long(value);
			}

			@Override
			public String toString(Object value) throws MooshakContentException {
				if(value instanceof Long)
					return Long.toString((Long) value);
				else
					throw new MooshakContentException("Not an long "+value);
			}
		});
		
		converters.put(Float.class, new TypeConverter<Float>() {

			@Override
			public Float fromString(String value) {
				
				return new Float(value);
			}

			@Override
			public String toString(Object value) throws MooshakContentException {
				if(value instanceof Float)
					return Float.toString((Float) value);
				else
					throw new MooshakContentException("Not an float "+value);
			}
		});
		
		converters.put(Double.class, new TypeConverter<Double>() {

			@Override
			public Double fromString(String value) {
				
				return new Double(value);
			}

			@Override
			public String toString(Object value) throws MooshakContentException {
				if(value instanceof Double)
					return Double.toString((Double) value);
				else
					throw new MooshakContentException("Not an double "+value);
			}
		});
		
		// Dates are serialized in seconds but processed in milliseconds
		converters.put(Date.class, new TypeConverter<Date>() {

			@Override
			public Date fromString(String value) {
				
				return new Date(Long.parseLong(value) * MILLIS_IN_A_SEC);
			}

			@Override
			public String toString(Object value) throws MooshakContentException {
				if(value instanceof Date)
					return Long.toString(((Date) value).getTime()/MILLIS_IN_A_SEC);
				else
					throw new MooshakContentException("Not a date "+value);
			
			}
		});
		
		converters.put(Color.class, new TypeConverter<Color>() {

			@Override
			public Color fromString(String value) throws MooshakContentException  {
				
				return Colors.getColor(value);
			}

			@Override
			public String toString(Object value) throws MooshakContentException {
				if(value instanceof Color)
					return Colors.getHtmlColor((Color) value);
				else
					throw new MooshakContentException("Not a color "+value);
			}
		});
		
	}
	
	/**
	 * Convert an object into a string according to the type of its attribute
	 * @param attribute
	 * @param value
	 * @return
	 * @throws MooshakContentException
	 */
	public static String getStringFromTypedValueFrom(
			Attribute attribute, 
			Object value) throws MooshakContentException {
		String text = "";
		Class<?> type = attribute.field.getType();
		
		if(value == null) {
			// Ignorable cases:
			// 	1) null values (empty and null are the same)
		} else if(converters.containsKey(type)) {
				text = converters.get(type).toString(value);
		} else  if(type.equals(Path.class)) { 
				text = getPathStringValue(attribute, value);
		} else if(type.isAssignableFrom(EnumSet.class)) { 
				text = getEnumSetStringValue(attribute, value);
		} else if(type.isEnum()) {
				text = getEnumStringValue(value, type);
		} else
			throw new MooshakContentException("Unsupported type:"+type.getName());
			
		return text;
	}
	

	/**
	 * Convert a string in an object of the correct type
	 * for this attribute in the context of a PersistentObject 
	 * with the given path 
	 * @param path of PersistentObject
	 * @param attribute where the value will be stored
	 * @param value a string representation of the attribute content
	 * @return an object of the appropriate type as Object
	 * @throws MooshakContentException
	 */
	public static Object getTypedValueFromString(
			Path path, 
			Attribute attribute, 
			String value) 
				throws MooshakContentException{
		Object object = null;
		Class<?> type = attribute.field.getType();

		// Ignorable cases:
		if(value == null || "".equals(value)//  1) null values (empty or null) 
				|| Void.class.equals(type) 	//  2) Void type is unassignable 
											//  3) is a sort of PersistentObject
				|| PersistentObject.class.isAssignableFrom(type))  {
			// do nothing
		} else if(converters.containsKey(type)) 
			object = converters.get(type).fromString(value);
		else  if(type.equals(Path.class)) 
			object = getPathTypedValue(path, attribute, value);
		 else if(type.isAssignableFrom(EnumSet.class)) 
			object = getEnumSetTypedValue(attribute, value);
		 else if(type.isEnum())
			object = getEnumTypedValue(value, type);
		else 
			throw new MooshakContentException("Unsupported type:"+type.getName());
		
		
		return object;
	}

	/**
	 * Get a string value from an enumerated value
	 * Assumes that it was checked that the type is Enum
	 * this the explicit cast can regarded as safe
	 * @param value
	 * @param type
	 * @return
	 */
	private static <T extends Enum<T>> 
		String getEnumStringValue(Object value, Class<?> type) {
		
		@SuppressWarnings("unchecked")
		Enum<T> enumValue = (Enum<T>) value;
		
		return enumValue.toString();
	}

	
	/**
	 * Get a typed value for a string assigned to a enumerated field
	 * Assumes that it was checked that type is  a Enum
	 * thus the explicit cast can be regarded as safe.
	 * @param value
	 * @param type
	 * @return
	 */
	private static <T extends Enum<T>> Object getEnumTypedValue(String value,
			Class<?> type) {
		Object object;
		
		@SuppressWarnings("unchecked")
		Class<T> enumType = (Class<T>) type;
		object = Enum.valueOf(enumType , Strings.toJavaConstant(value)); 
			
		return object;
	}
	
	private static <T extends Enum<T>> String getEnumSetStringValue(Attribute attribute,
			Object value) throws MooshakContentException {
		List<String> texts = new ArrayList<>();

		Type genericType = attribute.field.getGenericType();
		if(! (genericType instanceof ParameterizedType))
			throw new MooshakContentException("EnumSet with wrong type parameter");

		ParameterizedType aType = (ParameterizedType) genericType;
		Type[] fieldArgTypes = aType.getActualTypeArguments();
		@SuppressWarnings("unchecked")
		Class<T> enumType = (Class<T>) fieldArgTypes[0];

		@SuppressWarnings("unchecked")
		EnumSet<T> set = (EnumSet<T>) value;

		for(Enum<T> item: set)
			texts.add(getEnumStringValue(item, enumType));
		
		return Strings.stringOfList(texts);
	}
	
	/**
	 * Get a typed value for a string assigned to a enumerated set field
	 * Assumes that its was checked that type is assignable from an EnumSet
	 * @param attribute
	 * @param value
	 * @return
	 * @throws MooshakContentException if field doesn't have a parametric type
	 */
	private static <T extends Enum<T>> Object getEnumSetTypedValue(
			Attribute attribute, String value) throws MooshakContentException {
		Object object;
		Type genericType = attribute.field.getGenericType();
		if(! (genericType instanceof ParameterizedType))
			throw new MooshakContentException("EnumSet with wrong type parameter");
			
		ParameterizedType aType = (ParameterizedType) genericType;
		Type[] fieldArgTypes = aType.getActualTypeArguments();
		@SuppressWarnings("unchecked")
		Class<T> enumType = (Class<T>) fieldArgTypes[0];

		EnumSet<T> set = EnumSet.noneOf(enumType);
		
		for(String item: Strings.listOfStrings(value)){
			T enumValue;
			try {
			   enumValue = Enum.valueOf(enumType , Strings.toJavaConstant(item));
			} catch(IllegalArgumentException cause) {
				String message = "Illegal value in set: "+item;
				throw new MooshakContentException(message,cause);
			}
			set.add(enumValue);
		}
		object = set;
		return object;
	}

	/**
	 * Gets a string from a value assigned to a Path field
	 * @param attribute
	 * @param value
	 * @return
	 * @throws MooshakContentException 
	 */
	private static String getPathStringValue(Attribute attribute,Object value)
			throws MooshakContentException {
		if(value instanceof Path)
			return ((Path) value).toString();
		else
			throw new MooshakContentException("Path value expected");
	}
	
	/**
	 * Gets a typed value for a string assigned to a Path field.
	 * Can be either a simple File or a Path (file to a PersistentObject) 
	 * @param path
	 * @param attribute
	 * @param value
	 * @return
	 */
	private static <T extends PersistentObject> 
	Object getPathTypedValue(Path path, Attribute attribute,String value) 
			throws MooshakContentException {
			
		Path object = Paths.get(value);
		if(! attribute.annot.complement().contains("/"))	
			object = object.getFileName();

		return object;
	}

}
