package pt.up.fc.dcc.mooshak.content.util;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Static methods on filenames (strings)
 * @author José Paulo Leal zp@dcc.fc.up.pt
 *
 */
public class Filenames {

	final private static Pattern filenamePattern = 
		Pattern.compile("(^|/)([^/]*)\\.([^/]*)$");
	
	public static String rootName(String name) {
		String extension = null;
		
		Matcher matcher = filenamePattern.matcher(name);
		if(matcher.find()) {
			extension = matcher.group(2);
		}
		
		return extension;
	}
	
	public static String extension(String name) {
		String extension = null;
		
		Matcher matcher = filenamePattern.matcher(name);
		if(matcher.find()) {
			extension = matcher.group(3);
		}
		
		return extension;
	}
	
	public static String getFileNameWithoutExtension(String name) {
	    name = Paths.get(name).getFileName().toString();
	    int pos = name.lastIndexOf('.');
	    if (pos > 0 && pos < (name.length() - 1)) {
	        // there is a '.' and it's not the first, or last character.
	        return name.substring(0,  pos);
	    }
	    return name;
	}
	
	private static final Pattern SPACE = Pattern.compile(" ");
	private static final Pattern SLASH = Pattern.compile("/");
	private static final Pattern OTHER = Pattern.compile("[^a-zA-Z0-9_:]");
	
	private static final String STANDARD_NAME_BASE = "funny_name_";
	private static final Pattern STANDARD_NAME = 
			Pattern.compile( STANDARD_NAME_BASE+"(\\d+)");
	private volatile static Integer standardNameCount = 0;

	/**
	 * Return a sanitized version of a name that can be used 
	 * as a file or directory name 
	 * 
	 * @param name	to be sanitized
	 * @return	sanitized name
	 */
	public static String sanitize(String name) {
		String sanitized = name;
		
		sanitized = SPACE.matcher(sanitized).replaceAll("_");
		sanitized = SLASH.matcher(sanitized).replaceAll(":");
		sanitized = OTHER.matcher(sanitized).replaceAll("");
		
		if("".equals(sanitized)) // empty names get standard names
			
			sanitized = STANDARD_NAME_BASE + (++standardNameCount);
			
		else { // if a name is a standard name by change increment counter
		
			Matcher matcher = STANDARD_NAME.matcher(sanitized);
			
			if(matcher.matches())
				standardNameCount = Integer.parseInt(matcher.group(1));
			
		}
		
		return sanitized;
	}
	
	/**
	 * Set number used in next standard name (for testing purposes)
	 * @return the standardNameCount
	 */
	public static Integer getStandardNameCount() {
		return standardNameCount;
	}

	/**
	 * Get number used in next standard name (for testing purposes)
	 * @param standardNameCount the standardNameCount to set
	 */
	public static void setStandardNameCount(Integer standardNameCount) {
		Filenames.standardNameCount = standardNameCount;
	}
	
	
	/**
	 * Return name of file (last part)
	 * @param path
	 * @return
	 */
	public static String getSafeFileName(Path path) {
		Path fileName = null;
		
		if(path == null)
			return null;
		else if ((fileName = path.getFileName()) == null)
			return null;
		else
			return fileName.toString();
			
	}
}
