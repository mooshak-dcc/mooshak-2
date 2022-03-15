package pt.up.fc.dcc.mooshak.content.util;

import java.awt.Color;
import java.lang.reflect.Field;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;

public class Colors {

	private static String INVALID_COLOR = "Invalid color:";
	
	public static Color getColor(String value) throws MooshakContentException {
		Color color =Color.black;
		
		value = value.trim();
		if(value.startsWith("#")) {
			int size = (value.length()-1) / 3;
			try {
			int red   = Integer.parseInt(value.substring(1,1+size),16);
			int green = Integer.parseInt(value.substring(1+size,1+2*size),16);
			int blue  = Integer.parseInt(value.substring(1+2*size),16);
			
				color = new Color(red,green,blue);
			} catch(NumberFormatException e) {
				throw new MooshakContentException(INVALID_COLOR+value,e);
			} catch (IllegalArgumentException e) {
				throw new MooshakContentException(INVALID_COLOR+value,e);
			}
		} else {
			
			value = value.toLowerCase();
			for(Field field: Color.class.getFields()) {
				if(! field.getType().equals(Color.class))
					continue;
				if(field.getName().equals(value)) {
					try {
						color = (Color) field.get(null);
					} catch (IllegalArgumentException e) {
						throw new MooshakContentException(INVALID_COLOR+value);
					} catch (IllegalAccessException e) {
						throw new MooshakContentException(INVALID_COLOR+value);
					}
					break;
				}
			}
		}
		
		return color;
	}
	
	public static String getHtmlColor(Color color) {
		String value = String.format("#%02X%02X%02X", 
				color.getRed(),
				color.getGreen(),
				color.getBlue());
		
		
		return value;
	}
	
}
