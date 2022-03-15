package pt.up.fc.dcc.mooshak.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Platforms where client may run
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public enum Platform implements IsSerializable {

	HP_UX, 
	LINUX, 
	MAC, 
	SUN_OS, 
	WINDOWS, 
	IPHONE, 
	IPAD, 
	IPOD, 
	ANDROID, 
	BLACKBERRY, 
	OPERA,
	UNKNOWN
}
