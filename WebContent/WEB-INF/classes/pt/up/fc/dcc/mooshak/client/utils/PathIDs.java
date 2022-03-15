package pt.up.fc.dcc.mooshak.client.utils;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Utilities for pathname IDs
 *  
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class PathIDs {
	
	private static RegExp backTurn = RegExp.compile("/\\w*/\\.\\./"); 

	/**
	 * Normalize a path by replacing sequences of the form /name/../ by /
	 * @param path
	 * @return
	 */
	public static String normalize(String path) {
		
		while(backTurn.test(path))
			path = backTurn.replace(path, "/");
		
		return path;
	}
	
	public static String getIdName(String path) {
		int pos = path.lastIndexOf("/");
		
		return path.substring(pos+1);
	}
}
