package pt.up.fc.dcc.mooshak.content.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PasswordTest {


	@Test
	public void testCryptMatch() {
		
		for(String plain: new String [] { 
		
			"Password", 
			"1q2w3e", 
			"Open Sesame", 
			"12345", 
			"****", 
			"2Saf32F4!l" 
			
		}) {
			String hash = Password.crypt(plain);
			String fake;
			
			assertTrue(Password.match(hash, plain));
			
			fake = deleteChar(plain);
			assertFalse(Password.match(hash, fake));
			
			fake = replaceChar(plain);
			assertFalse(Password.match(hash, fake));
			
			fake = translocateChars(plain);
			assertFalse(Password.match(hash, fake));
		}
		
	}
	
	private String deleteChar(String plain) {
		StringBuilder buffer = new StringBuilder(plain);
		int size = Math.min(8,plain.length());
		int pos = Password.random.nextInt(size);
	
		buffer.deleteCharAt(pos);
		
		return buffer.toString();
	}
	
	private String replaceChar(String plain) {
		StringBuilder buffer = new StringBuilder(plain);
		int size = Math.min(8,plain.length());
		int pos = Password.random.nextInt(size);
		char aChar;
		
		do {
			aChar = Password.itoa64.charAt(
				Password.random.nextInt(Password.itoa64.length()));
		} while(aChar == buffer.charAt(pos));
		
		buffer.setCharAt(pos, aChar);
		
		return buffer.toString();
	}
	
	private String translocateChars(String plain) {
		StringBuilder buffer = new StringBuilder(plain);
		int size = Math.min(8,plain.length());
		int a = Password.random.nextInt(size);
		int b;
		int count = 0;
		do {
			b = Password.random.nextInt(size);
			if(count++ == 5)
				break;
		} while(a==b || buffer.charAt(a) == buffer.charAt(b));

		if(count < 5) {
			char t = buffer.charAt(a);
			buffer.setCharAt(a,plain.charAt(b));
			buffer.setCharAt(b,t);
		} else {
			buffer.setCharAt(0, '!');
		}
		
		return buffer.toString();
	}

}
