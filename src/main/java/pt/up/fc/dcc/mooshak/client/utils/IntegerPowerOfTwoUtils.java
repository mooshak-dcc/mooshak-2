package pt.up.fc.dcc.mooshak.client.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Converts integers to/from string in power of two form
 * Example: 2048 -> 2K
 * 			2K -> 2048 
 * 
 * @author josepaiva
 */
public class IntegerPowerOfTwoUtils {

	private static final Pattern REGEX = Pattern
			.compile("(\\d+)([KMGkmg]?)");
	private static final String[] KMG = new String[] { "", "K", "M", "G" };
	private static final int KILO = 1 << 10;
	private static final int MEGA = 1 << 20;
	private static final int GIGA = 1 << 30;

	/**
	 * Converts integers to string in power of two form
	 * Example: 2048 -> 2K
	 * 
	 * @param d
	 * @return
	 */
	public static String formatInt(int d) {
		
		if(d % KILO != 0)
			return d + KMG[0];
		
		int i = 0;
		while (d >= KILO) {
			i++;
			d /= KILO;
			
			if(i >= 3)
				break;
		}
		
		return d + KMG[i];
	}


	/**
	 * Converts string in power of two form to int
	 * Example: 2K -> 2048
	 * 
	 * @param s
	 * @return
	 * @throws MooshakException 
	 */
	public static int parseInt(String s) throws MooshakException {
		
		if(s == null || s.equals("null"))
			throw new MooshakException("Could not parse null string as int");
		
		final Matcher m = REGEX.matcher(s);
		
		if (!m.matches()) {
			try {
				return Integer.parseInt(s);
			} catch (Exception e) {
				throw new MooshakException("Could not parse "+s+" as int");
			}
		}
		
		switch (m.group(2)) {
		case "K":
		case "k":
			return Integer.parseInt(m.group(1)) * KILO;
		case "M":
		case "m":
			return Integer.parseInt(m.group(1)) * MEGA;
		case "G":
		case "g":
			return Integer.parseInt(m.group(1)) * GIGA;

		default:
			return Integer.parseInt(s);
		}
	}

}
