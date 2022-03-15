package pt.up.fc.dcc.mooshak.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilesWhiteSpacesUtils {
	
	/*private static final Pattern REGEX = 
			Pattern.compile("(.*?)(\\s)(\\s+)$", Pattern.DOTALL);*/

	/**
	 * Gets a line delimiter for a given str
	 * @param str
	 * @return
	 */
	public static String getLineDelimiter(String str) {
		
		if (str.matches("(?s).*(\\r\\n).*")) //Windows
			return "\r\n";
		else if (str.matches("(?s).*(\\n).*")) //Unix/Linux
			return "\n";
		else if (str.matches("(?s).*(\\r).*")) //Legacy mac os 9
			return "\r";
		else
			return "\n";
	}
	
	/**
	 * Remove duplicated line breaks in the end
	 * @param content
	 * @return
	 */
	public static String removeDuplicatedLineBreaksAtEnd(byte[] content) {
		return removeDuplicatedLineBreaksAtEnd(new String(content));
	}

	/**
	 * Remove duplicated line breaks in the end of string
	 * @param string
	 * @return
	 */
	public static String removeDuplicatedLineBreaksAtEnd(String string) {
		String delimiter = getLineDelimiter(string);
		string += delimiter;
		Pattern regex = null;
		if (delimiter.equals("\r\n"))
			regex = Pattern.compile("(.*?)([\\r\\n]+)$", Pattern.DOTALL);
		else
			regex = Pattern.compile("(.*?)([\\" + delimiter + "]+)$", Pattern.DOTALL);
		Matcher regexMatcher = regex.matcher(string);
		if (regexMatcher.find())
			string = string.replace(regexMatcher.group(2), delimiter);
		return string;
	}
	
	/**
	 * Checks if the string uses OS line separator (sep)
	 * @param content
	 * @param sep
	 * @return
	 */
	public static boolean isOSLineSeparator(String sep, byte[] content) {
		return getLineDelimiter(new String(content)).equals(sep);
	}
	
	/**
	 * Checks if the string uses OS line separator (sep)
	 * @param content
	 * @param sep
	 * @return
	 */
	public static boolean isOSLineSeparator(String sep, String content) {
		return getLineDelimiter(content).equals(sep);
	}

	/**
	 * Replaces line delimiter in content by sep
	 * @param property
	 * @param content
	 * @return
	 */
	public static byte[] replaceLineDelimiter(String sep, byte[] content) {
		String string = new String(content);
		String delimiter = getLineDelimiter(string);
		
		return 	string.replaceAll(delimiter, sep).getBytes();
	}
}
