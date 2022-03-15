package pt.up.fc.dcc.mooshak.content.util;

/**
 * Byte array handling.
 * 
 * @author José Carlos Paiva
 */
public class Bytes {

	public static int lineCount(byte[] bs) {
		return lineCount(bs, false);
	}

	public static int lineCount(byte[] bs, boolean empty) {

		int count = 0;

		boolean hasContent = false;
		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == '\n') {
				if (hasContent || empty) {
					++count;
					hasContent = false;
				}
			} else if (!hasContent && !Character.isWhitespace(bs[i])) {
				hasContent = true;
			}
		}

		return count == 0 && (empty || hasContent) ? 1 : count;
	}
}
