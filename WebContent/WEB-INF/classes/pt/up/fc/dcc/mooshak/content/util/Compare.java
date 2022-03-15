package pt.up.fc.dcc.mooshak.content.util;

import java.util.Locale;
import java.util.regex.Pattern;


public class Compare {
	
	private final static Pattern WHITESPACE_RE = Pattern.compile("(\\s)+"); 
	
	/**
	 * Normalizes a string by removing leading trailing and whitespace,
	 * reducing multiple whitespace to a single space, and converting
	 * characters to lower case (in the English locale).   
	 * @param value string to normalize
	 * @return normalized string
	 */
	public static String normalize(String value) {
		
		value = WHITESPACE_RE.matcher(value).replaceAll(" ");
		
		return value.toLowerCase(Locale.ENGLISH).trim();
	}

}
