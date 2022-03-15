package pt.up.fc.dcc.mooshak.client.utils;

import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.regexp.shared.SplitResult;

/**
 * Convert strings to space separated strings
 * 
 * @author josepaiva
 */
public class StringSpaceSeparatedUtils {

	/**
	 * From camel case to space separated
	 * 
	 * @param s
	 * @return
	 */
	public static String splitCamelCase(String s) {
		return s.replaceAll("([A-Z])([A-Z][a-z])", "$1 $2")
				 .replaceAll("([a-z])([A-Z])", "$1 $2")
	   			 .replaceAll("([^0-9])([0-9])", "$1 $2")
		 		 .replaceAll("([0-9])([^0-9])", "$1 $2");
	}

	/**
	 * From underscore to space separated
	 * 
	 * @param s
	 * @return
	 */
	public static String splitUnderscore(String s) {
		return s.replaceAll("_", " ");
	}

	/**
	 * Returns a formatted string using the specified format string and arguments
	 * @param format
	 * @param args
	 * @return
	 */
	public static String format(final String format, final Object... args) {
		final RegExp regex = RegExp.compile("%[a-z]");
		final SplitResult split = regex.split(format);
		final StringBuffer msg = new StringBuffer();
		for (int pos = 0; pos < split.length() - 1; ++pos) {
			msg.append(split.get(pos));
			msg.append(args[pos].toString());
		}
		msg.append(split.get(split.length() - 1));
		return msg.toString();
	}

}
