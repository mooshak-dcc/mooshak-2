package pt.up.fc.dcc.mooshak.content.util;

/**
 * Escapes <code>String</code>s for HTML and possible others
 * 
 * @author josepaiva
 */
public class StringEscapeUtils {
	// any Unicode character, excluding the surrogate blocks, FFFE, and FFFF
	private static final String INVALID_CHARS = "[^"
            + "\u0009\r\n"
            + "\u0020-\uD7FF"
            + "\uE000-\uFFFD"
            + "\ud800\udc00-\udbff\udfff"
            + "]";

	/**
	 * Escapes a  <code>String</code> for HTML
	 * @param toEscape
	 * @return
	 */
	public static String escapeHtml(String toEscape) {
		return toEscape.replaceAll("&", "&amp;")
				.replaceAll("\"", "&quot;")
				.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
	}

	/**
	 * Remove characters that are illegal in XML 1.0
	 * @param toEscape
	 * @return
	 */
	public static String removeInvalidChars(String toEscape) {
		return toEscape.replaceAll(INVALID_CHARS, "");
	}
}
