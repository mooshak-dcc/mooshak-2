package pt.up.fc.dcc.mooshak.content.util;

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class Strings {
	
	static Pattern somePuncts = Pattern.compile("'-/:");
	static Pattern nonLeterOrDigit = Pattern.compile("[^ a-zA-Z0-9]");
	static Pattern blancs = Pattern.compile("\\s+");
	static Pattern oneOrTwoLetters = Pattern.compile(" [a-zA-Z][a-zA-Z]? ");

	/**
	 * Capitalize the first letter of each word and make the rest lower case.
	 * Words with less than 4 letters are in lower case.
	 * Words in title are separated by a single space
	 * 
	 * @param text to convert in title
	 * @return text in title case
	 */
	public static String toTitle(String text) {
		StringBuilder builder = new StringBuilder(text.length());
		boolean next = false;
		for(String word: blancs.split(text)) {
			int length = word.length();
			
			if(next)
				builder.append(" ");
			
			if(length < 4 && next)
				builder.append(word.toLowerCase());
			else {
				if(length > 0)
					builder.append(Character.toUpperCase(word.charAt(0)));
				if(length > 1)
					builder.append(word.substring(1).toLowerCase());		
			}
			
			next = true;
		}
		return builder.toString();
	}
	
	
	/**
	 * Create an acronym from a text
	 * @param text
	 * @return
	 */
	public static String acronymOf(String text) {
		
		String buffer = // replace diacritics
				Normalizer.normalize(text.toUpperCase(Locale.ENGLISH),Form.NFD).
				replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		
		buffer = somePuncts.matcher(buffer).replaceAll(" ");
		buffer = nonLeterOrDigit.matcher(buffer).replaceAll("");
		buffer = oneOrTwoLetters.matcher(buffer).replaceAll(" ");
		
		StringBuilder acronym = new StringBuilder();
		for(String word :blancs.split(buffer)) 
			if(word.length()>0)
				acronym.append(word.charAt(0));
		
		
		return acronym.toString();
	}

	
	/**
	 * Converts a string with spaces, hyphen or underscores to camel case.
	 * (was previously used with enum labels but was replaced by java constants) 
	 * @param withSpaces
	 * @return
	 */
	public static String toCamelCase(String withSpaces) {
		StringBuilder buffer = new StringBuilder(withSpaces.length());
		for(String word: withSpaces.split("[\\s-_]+")) {
			if(word.length() == 0)
				continue;
			buffer.append(Character.toUpperCase(word.charAt(0)));
			buffer.append(word.substring(1));
		}
		return buffer.toString();
	}
	
	/**
	 * Converts a string with spaces, hyphen or underscores to camel case.
	 * (this is currently just used with enum labels) 
	 * @param withSpaces
	 * @return
	 */
	public static String toJavaConstant(String withSpaces) {
		String trimmed = withSpaces.trim();
		int len = trimmed.length();
		StringBuilder buffer = new StringBuilder(len);
		boolean rep = true;
		for(int i=0; i<len; i++) {
			char c = trimmed.charAt(i);
			switch(c) {
			case '_':
			case ' ':
			case '\t':
			case '-':
				if(rep)
					continue;
				buffer.append('_');
				rep = true;
				break;
			default:
				buffer.append(Character.toUpperCase(c));
				rep = false;
				break;
			}
		}
		return buffer.toString();
	}
	
	
	/**
	 * Converts a TCL list to a list of strings.
	 * An element of the list may contain string delimited by brackets
	 * @param text text to convert
	 * @return
	 */
	public static List<String> listOfStrings(String text) {
		
		List<String> list = new ArrayList<>();
		StringBuilder buffer = new StringBuilder();
		boolean inside = false;
		for(int i=0; i<text.length(); i++) {
			if(inside)
				switch(text.charAt(i)) {
					case '}':
						inside = false;
					break;
					default:
						buffer.append(text.charAt(i));
				}
				else
			switch(text.charAt(i)) {
				case ' ':
					list.add(buffer.toString());
					buffer = new StringBuilder();
					break;
				case '{':
					inside = true;
					break;
				default:
					buffer.append(text.charAt(i));
			}
		}
		list.add(buffer.toString());
		return list;
	}
	
	/**
	 * Converts a a list of strings to a TCL list as a string.
	 * Strings are delimited by braces
	 * @param list to convert
	 * @return
	 */
	public static String stringOfList(List<String> list) {
		StringBuilder text = new StringBuilder();
		boolean first = true;
		for(String item: list) {
			if(first)
				first = false;
			else
				text.append(' ');
			
			if(blancs.matcher(item).find()) {
				text.append('{');
				text.append(item);
				text.append('}');
			} else 
				text.append(item);
		}
			
		return text.toString();
	}
	
	/**
	 * Separate words by sep
	 * 
	 * @param str
	 * @param sep
	 * @return {@link String} String of words from str separated by sep
	 */
	public static String separateWords(String str, String sep) {
		return str.replaceAll("([A-Z])([A-Z][a-z])", "$1 $2")
				.replaceAll("([a-z])([A-Z])", "$1 $2")
				.replaceAll("([^0-9])([0-9])", "$1 $2")
				.replaceAll("([0-9])([^0-9])", "$1 $2");
	}
	
	/**
	 * Check if string is a valid path
	 * @param path {@link String} to check
	 * @return <code>true</code> if valid path, <code>false</code> otherwise
	 */
	public static boolean isValidPath(String path) {
	    try {
	        Paths.get(path);
	    } catch (InvalidPathException | NullPointerException ex) {
	        return false;
	    }
	    return true;
	}
}
