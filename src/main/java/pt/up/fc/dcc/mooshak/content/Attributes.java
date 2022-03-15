package pt.up.fc.dcc.mooshak.content;

/**
 * Static methods for handling attribute values and their types 
 */
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.content.util.Enums;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Attributes {
	
	/*----------------------------------------------------------------------*
	 *  Indexing fields by attribute names (labels) and classes             *
	 *----------------------------------------------------------------------*/
	
	 /** 
	  * An attribute of Mooshak, a field marked with @MooshakAttribute
	  * An instance of this class relates the field with the annotation data 
	  */
	 public static class Attribute {
		 Field field;
		 MooshakAttribute annot;
		 int order;
		 
		Attribute(Field field, MooshakAttribute annot, int order ) {
			super();
			this.field = field;
			this.annot = annot;
			this.order = order;
		}	
		
		/**
		 * Get name of this attribute, as declared in annotation
		 */
		public String getName() {
			return annot.name();
		}
		
		/**
		 * Show name of this attribute, as declared in annotation
		 */
		public String toString() {
			return annot.name();
		}
		
		/**
		 * Get type of Mooshak Attribute (not a Java type)
		 * @return
		 */
		public AttributeType getType() {
			return annot.type();
		}
		
		/**
		 * Get Java class of field used by this Mooshak Attribute
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <T extends PersistentObject> Class<T> getFieldType() {
			return (Class<T>) field.getType();
		}
		
		/**
		 * Get possible values of menu attributes attributes 
		 * and {@code null} otherwise
		 * @return list of strings
		 */
		public <T extends Enum<T>> List<String> getPossibleValues() {
			List<String> values = null;
			
			switch(annot.type()) {
			case LIST:
				Class<T> setEnumType = getEnumSetContainedType(field);
				values = Enums.getNames(setEnumType);
				break;
			case MENU:
				Class<?> type = field.getType();
				if(type.isEnum()) {
					@SuppressWarnings("unchecked")
					Class<T> enumType = (Class<T>) type;
					values = Enums.getNames(enumType);
				} 
				
				break;
				
			case PATH:
				values = new ArrayList<>();
				PersistentObject po;
				try {
					// relative complements are resolved on the client 
					if(annot.complement().startsWith("/")) {
						po = PersistentObject.openPath(annot.complement());
					
						for(PersistentObject child: po.getChildren(false))
							values.add(child.getIdName());
					}
				} catch (MooshakContentException cause) {
					Logger.getLogger("").log(Level.SEVERE,
							"Populating from:"+annot.complement(),cause);
				}
				break;
				
			default:
				// ignore;
			
			}
			return values;
		}
		
		/**
		 * Get the contained type of an EnumSet
		 * @param field
		 * @return
		 */
		private <T extends Enum<T>> 
		Class<T> getEnumSetContainedType(Field field) {
			 
			if(EnumSet.class.isAssignableFrom(field.getType())) {
				
				Type genericType = field.getGenericType();
			
				if(genericType instanceof ParameterizedType) {
					ParameterizedType paramType = (ParameterizedType) genericType;
					
					Type[] param = paramType.getActualTypeArguments();
				
					if(param.length == 1) {
						@SuppressWarnings("unchecked")
						Class<T> type = (Class<T>) param[0];
						return type;
					} else
						return null;
				} else
					return null;
			} else
				return null;
		}
		
		
		/**
		 * Get a tip, a small string that may be shown on a tool tip 
		 * @return
		 */
		public String getTip() {
			return annot.tip();
		}
		
		/**
		 * Get help, a long text that may be shown on a help window
		 * @return
		 */
		public String getHelp() {
			return annot.help();
		}
		
		/**
		 * Get document specification for XML, if any
		 * @return
		 */
		public String getDocumentSpecification() {
			return annot.docSpec() == null || annot.docSpec().equals("") ? null : annot.docSpec();
		}
		
		/**
		 * Is quiz editor?
		 * @return
		 */
		public boolean isQuizEditor() {
			return annot.quizEditor();
		}
		
		/**
		 * Get max length of text field
		 * @return
		 */
		public int getMaxLength() {
			return annot.maxLength();
		}

		/**
		 * Get complement of a PATH attribute (e.g. "../../languages")
		 * @return
		 */
		public String getComplement() {
			return annot.complement();
		}
		
		private static final Pattern END_STAR = Pattern.compile("/\\*$");
		/**
		 * Get complement of a PATH attribute without trailing star
		 * (e.g. "../../groups" instead of "../../groups/*")
		 * @return
		 */
		public String getComplementBase() {
			String complement = annot.complement();
			Matcher matcher;
			
			if((matcher = END_STAR.matcher(complement)).find())
				return matcher.replaceFirst("");
			else
				return complement;
		}
	}
	
	private static Map<Class<? extends PersistentObject>,Map<String,Attribute>> 
		labeledFieldbyClass = new HashMap<>();
	
	private static Map<String,Attribute> 
			getLabeledFields(Class<? extends PersistentObject> type) {
		Map<String,Attribute> labeledField;
		
		if(labeledFieldbyClass.containsKey(type))
			labeledField = labeledFieldbyClass.get(type);
		else {			
			labeledField = new HashMap<String,Attribute>();  
		
			MooshakAttribute annot;
			int order = 1;
			for(Field field: type.getDeclaredFields()) {
				field.setAccessible(true);
				if((annot=field.getAnnotation(MooshakAttribute.class)) != null) 
					labeledField.put(annot.name(),
							new Attribute(field,annot,order++));
				field.setAccessible(false);
			}
			labeledFieldbyClass.put(type,labeledField);
		}
		return labeledField;
	}

	/**
	 * Return attributes of an extension of PersistentObject
	 * sorted by the order they were declared
	 * @param type
	 * @return
	 */
	public static Collection<Attribute> getAttributes(
			Class<? extends PersistentObject> type) {
		List<Attribute> values = new ArrayList<>(getLabeledFields(type).values());
		
		Collections.sort(values, new Comparator<Attribute>() {

			@Override
			public int compare(Attribute a, Attribute b) {
				return a.order - b.order;
			}
			
		});
		return values;
	}
	
	/**
	 * Return named Mooshak attribute in given type
	 * @param type Mooshak class
	 * @param name of attribute
	 * @return Attribute object
	 * @throws MooshakContentException
	 */
	static Attribute getAttribute(
				Class<? extends PersistentObject> type,
				String name) throws MooshakContentException {
		
		Map<String, Attribute> attributes = getLabeledFields(type);
		
		if(attributes.containsKey(name))
			return attributes.get(name);
		else {
			String message = "unknown attribute " + name +
						" in class "+type.getName();
			throw new MooshakContentException(message);
		}
	}
	
	/**
	 * Get value from attribute field
	 * @throws MooshakContentException 
	 */
	static Object getValue(Attribute attribute,	PersistentObject persistent) 
			throws MooshakContentException {
		
		Object object;
		try {
			synchronized (attribute.field) {
				
				attribute.field.setAccessible(true);
				object = attribute.field.get(persistent);
				attribute.field.setAccessible(false);
			}
			
		} catch(IllegalAccessException e) {
			throw new MooshakContentException("Illegal access to attribute "+attribute.getName(),e);
		}
		return object;
	}
	
	/**
	 * Set a value to an attribute field
	 * @param attribute
	 * @param persistent
	 * @param object
	 * @throws MooshakContentException
	 */
	static void setValue(
			Attribute attribute, 
			PersistentObject persistent,
			Object object) throws MooshakContentException {
		Field field = attribute.field;
		
		try {
			synchronized (field) {
				field.setAccessible(true);
				field.set(persistent,object);
				field.setAccessible(false);
			}
		} catch (IllegalArgumentException e) {
			String message = "illegal value '"+object+"' "+
					"for attribute "+attribute+" in "+persistent.path;
			throw new MooshakContentException(message,e);
		} catch (IllegalAccessException e) {
			String message = "illegal access to attribute "+attribute
					+" in "+persistent.path;
			throw new MooshakContentException(message,e);
		}
	}
	
	
	/**
	 * Set a value to a attribute field from a string value
	 * @param attribute
	 * @param persistent
	 * @param value
	 * @throws MooshakContentException
	 */
	public static void setStringValue(
			Attribute attribute, 
			PersistentObject persistent,
			String value) 
		throws MooshakContentException {
		
		setValue(attribute,persistent,
				Converters.getTypedValueFromString(persistent.path, attribute, value));
	}
	
	/**
	 * Get the value of a attribute field as a string
	 * @param attribute
	 * @param persistent
	 * @return
	 * @throws MooshakContentException
	 */
	public static String getStringValue(Attribute attribute, PersistentObject persistent) 
		throws MooshakContentException{
		
		Object object = getValue(attribute,persistent);
		
		if(object == null)
			return null;
		else
			return Converters.getStringFromTypedValueFrom(attribute,object);
	}
	
	/**
	 * Convenience method for extracting an attribute value as Path.
	 * Returns null if object is not a Path
	 * @param attribute
	 * @param persistent
	 * @return
	 */
	public static Path getValueAsPath(
			Attribute attribute, 
			PersistentObject persistent) throws MooshakContentException {
		Object object = getValue(attribute,persistent);
		
		if(object instanceof Path)		
			return (Path) object;
		else
			return null;
	}
	
	/**
	 * Convenience method for inserting an attribute value as Path
	 * @param attribute
	 * @param persistent
	 * @param path
	 * @throws MooshakContentException
	 */
	public static void setValueAsPath(
			Attribute attribute,
			PersistentObject persistent,
			Path path) throws MooshakContentException {
		
		setValue(attribute,persistent,path);
	}
	
}
