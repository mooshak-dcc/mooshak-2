package pt.up.fc.dcc.mooshak.client.utils;

import com.google.gwt.user.client.Window.Navigator;

import pt.up.fc.dcc.mooshak.shared.Platform;

/**
 * Detect the platform of this client
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class PlatformDetector {
	
	public static Platform getPlatform() {

		String platform = Navigator.getPlatform();

		if(platform.startsWith("HP"))
			return Platform.HP_UX;
		else if(platform.startsWith("Lin"))
			return Platform.LINUX;
		else if(platform.startsWith("Mac"))
			return Platform.MAC;
		else if(platform.startsWith("Sun"))
			return Platform.SUN_OS;
		else if(platform.startsWith("Win"))
			return Platform.WINDOWS;
		else if(platform.startsWith("iPhone"))
			return Platform.IPHONE;
		else if(platform.startsWith("iPod"))
			return Platform.IPOD;
		else if(platform.startsWith("iPad"))
			return Platform.IPAD;
		else if(platform.startsWith("Android"))
			return Platform.ANDROID;
		else if(platform.startsWith("BlackBerry"))
			return Platform.BLACKBERRY;
		else if(platform.startsWith("Opera"))
			return Platform.OPERA;
		else 
			return Platform.UNKNOWN;			
	}

}
