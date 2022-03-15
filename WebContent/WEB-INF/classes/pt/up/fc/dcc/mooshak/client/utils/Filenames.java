package pt.up.fc.dcc.mooshak.client.utils;

/**
 * This class contains utilities to manage filenames
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Filenames {

	/**
	 * Get extension from filename
	 * 
	 * @param filename Name of the file
	 * @return extension
	 */
	public static String getExtension(String filename) {
		
		int i = filename.lastIndexOf('.');
		if (i > 0)
			return filename.substring(i + 1);
		return "";
	}

	/**
	 * Get last pathname from a path.
	 * 
	 * @param path Path
	 * @return the last pathname
	 */
	public static String extractLastPathname(String path) {
		
		int i = path.lastIndexOf('/');
		if (i > 0)
			return path.substring(i + 1);
		return "";
	}
}
