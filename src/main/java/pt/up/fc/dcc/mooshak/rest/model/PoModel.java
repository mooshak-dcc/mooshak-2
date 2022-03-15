package pt.up.fc.dcc.mooshak.rest.model;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

import pt.up.fc.dcc.mooshak.content.Attributes;
import pt.up.fc.dcc.mooshak.content.Attributes.Attribute;
import pt.up.fc.dcc.mooshak.content.Converters;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.util.Strings;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;

public abstract class PoModel {
	private String id;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * Copy attribute values from a {@link PoModel} to a {@link MooshakObject}
	 * 
	 * @param mo {@link MooshakObject} to which attribute values will be copied
	 */
	public <T extends PoModel> void copyTo(PersistentObject po) {
		
		Class<? extends PersistentObject> objectType = po.getClass();

		for (Attribute attribute : Attributes.getAttributes(objectType)) {
			String fieldname = attribute.getName();
			Object fieldvalue = null;
			
			String getterName = "get" + ucFirst(fieldname);
			Method getterMethod;
			
			if ((getterMethod = searchMethod(getClass().getDeclaredMethods(), getterName)) == null)
				continue;
			
			try {
				if ((fieldvalue = getterMethod.invoke(this)) == null)
					continue;
				
				Attributes.setStringValue(attribute, po, Converters.getStringFromTypedValueFrom(attribute, 
						fieldvalue));
			} catch (MooshakContentException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new InternalServerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Copy attribute values from a {@link PersistentObject} to a {@link PoModel}
	 * 
	 * @param mo {@link PersistentObject} from which attribute values will be copied
	 */
	public <T extends PoModel> void copyFrom(PersistentObject po) {
		
		// set the object ID
		setId(po.getIdName());
		
		Class<? extends PersistentObject> objectType = po.getClass();

		for (Attribute attribute : Attributes.getAttributes(objectType)) {
			String fieldname = Strings.toCamelCase(attribute.getName());
			String fieldvalue;
			
			String setterName = "set" + ucFirst(fieldname);
			try {
				switch (attribute.getType()) {
				case FILE:
				case CONTENT:
				case PASSWORD:
					break;
				case DOUBLE:
					fieldvalue = Attributes.getStringValue(attribute, po);
					if (fieldvalue != null) {
						Method setterMethod = searchMethod(getClass().getDeclaredMethods(), setterName,
								Double.class);
						if (setterMethod != null)
							setterMethod.invoke(this, Double.parseDouble(fieldvalue));
					}
					break;
				case FLOAT:
					fieldvalue = Attributes.getStringValue(attribute, po);
					if (fieldvalue != null) {
						Method setterMethod = searchMethod(getClass().getDeclaredMethods(), setterName,
								Float.class);
						if (setterMethod != null)
							setterMethod.invoke(this, Float.parseFloat(fieldvalue));
					}
					break;
				case INTEGER:
					fieldvalue = Attributes.getStringValue(attribute, po);
					if (fieldvalue != null) {
						Method setterMethod = searchMethod(getClass().getDeclaredMethods(), setterName,
								Integer.class);
						if (setterMethod != null)
							setterMethod.invoke(this, Integer.parseInt(fieldvalue));
					}
					break;
				default:
					fieldvalue = Attributes.getStringValue(attribute, po);
					if (fieldvalue != null) {
						Method setterMethod = searchMethod(getClass().getDeclaredMethods(), setterName, 
								String.class);
						if (setterMethod != null)
							setterMethod.invoke(this, fieldvalue);
					}
				}
			} catch (MooshakContentException e) {
				String message = "Getting attribute " + attribute + " from "
						+ po.getIdName();
				throw new InternalServerException(message, e);
			} catch (Exception e) {
				throw new InternalServerException(e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Convert first letter of a String to uppercase
	 * 
	 * @param input {@link String} The string to transform
	 * @return {@link String} The transformed string
	 */
	private String ucFirst(String input) {
	    if (input.length() <= 1)
	        return input.toUpperCase();
	    
	    return input.substring(0, 1).toUpperCase() + input.substring(1);
	}
	
	private Method searchMethod(Method[] methods, String name, Class<?>... parameterTypes) {
		
		for (Method method : methods) {
			if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes))
				return method;
		}
		
		return null;
	}
}
