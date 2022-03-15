package pt.up.fc.dcc.mooshak.content.util;

import java.util.Random;

/**
 * Static methods to handle passwords, to convert a plain text key to an hash 
 * and to compare a plain text key with an hash.
 * For compatibility with the previous versions of Mooshak, it uses a
 * Java implementation of the crypt(3) function of Unix.
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class Password {

	// disable constructor; only static methods
	private Password() {} 
	
	/**
	 * Return an hash given a key in plain text
	 * @param plain
	 * @return
	 */
	public static String crypt(String plain) {
		
		 return Crypt.crypt(makeSalt(2),plain);    
	}
	
	/**
	 * Checks if hash an play version of key match
	 * @param hash
	 * @param plain
	 * @return
	 */
	public static boolean match(String hash,String plain) {
	
		return hash.compareTo(Crypt.crypt(hash,plain)) == 0;
	}
	
	/* From local_passwd.c (C) Regents of Univ. of California blah blah */
	static String itoa64 =         /* 0 ... 63 => ascii - 64 */
	        "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	static Random random = new Random();

	static String makeSalt( int n) {
	  StringBuilder text = new StringBuilder();
	  long v = random.nextLong();
	  
	  while (--n >= 0) {
	        text.append(itoa64.charAt((int) v & 0x3f));
	        v >>= 6;
	    }
	  return text.toString();
	}

}
