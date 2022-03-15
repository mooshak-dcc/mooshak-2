package pt.up.fc.dcc.mooshak.content.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Static methods for random string generation
 * 
 * @author josepaiva
 */
public class RandomStringGenerator {

	/**
	 * Generates a random alphanumeric string with length characters
	 * @param length
	 * @return
	 */
	public static String randomAlphanumeric(int length) {
		char[] charset = ("ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkm"
				+ "npqrstuvwxyz123456789")
				.toCharArray();
		return randomString(charset, length);
	}

	/**
	 * Generates a String with length character from the given set
	 * 
	 * @param characterSet
	 * @param length
	 * @return
	 */
	public static String randomString(char[] characterSet, int length) {
		Random random = new SecureRandom();
		char[] result = new char[length];
		for (int i = 0; i < result.length; i++) {
			// picks a random index out of character set > random character
			int randomCharIndex = random.nextInt(characterSet.length);
			result[i] = characterSet[randomCharIndex];
		}
		return new String(result);
	}
}
