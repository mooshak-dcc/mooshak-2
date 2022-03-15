package pt.up.fc.dcc.mooshak.content.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class CompareTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNormalize() {
		
		assertEquals("hello world", Compare.normalize("hello world"));
		assertEquals("hello world", Compare.normalize("hello world "));
		assertEquals("hello world", Compare.normalize(" hello world"));
		assertEquals("hello world", Compare.normalize(" hello world "));
		assertEquals("hello world", Compare.normalize("  hello world  "));
		assertEquals("hello world", Compare.normalize("  hello   world  "));
		assertEquals("hello world", Compare.normalize("\thello\tworld\t"));
		assertEquals("hello world", Compare.normalize("\nhello\nworld\n"));
		assertEquals("hello world", Compare.normalize("\n\thello\t\nworld\t\n"));
		assertEquals("hello world", Compare.normalize("\n\rhello\n\rworld\n\r"));
		assertEquals("hello world", Compare.normalize("\r\nhello\r\nworld\r\n"));

		assertEquals("hello world", Compare.normalize("HELLO WORLD"));
		assertEquals("hello world", Compare.normalize("HELLO WORLD "));
		assertEquals("hello world", Compare.normalize(" HELLO WORLD"));
		assertEquals("hello world", Compare.normalize(" HELLO WORLD "));
		assertEquals("hello world", Compare.normalize("  HELLO WORLD  "));
		assertEquals("hello world", Compare.normalize("  HELLO  WORLD  "));
		assertEquals("hello world", Compare.normalize("\tHELLO\tWORLD\t"));
		assertEquals("hello world", Compare.normalize("\nHELLO\nWORLD\n"));
		assertEquals("hello world", Compare.normalize("\n\tHELLO\t\nWORLD\t\n"));
		assertEquals("hello world", Compare.normalize("\n\rHELLO\n\rWORLD\n\r"));
		assertEquals("hello world", Compare.normalize("\r\nHELLO\r\nWORLD\r\n"));

		assertEquals("hello world", Compare.normalize("Hello World"));
		assertEquals("hello world", Compare.normalize("Hello World "));
		assertEquals("hello world", Compare.normalize(" Hello World"));
		assertEquals("hello world", Compare.normalize(" Hello World "));
		assertEquals("hello world", Compare.normalize("  Hello World  "));
		assertEquals("hello world", Compare.normalize("  Hello  World  "));
		assertEquals("hello world", Compare.normalize("\tHello\tWorld\t"));
		assertEquals("hello world", Compare.normalize("\nHello\nWorld\n"));
		assertEquals("hello world", Compare.normalize("\n\tHello\t\nWorld\t\n"));
		assertEquals("hello world", Compare.normalize("\n\rHello\n\rWorld\n\r"));
		assertEquals("hello world", Compare.normalize("\r\nHello\r\nWorld\r\n"));		
	
		assertEquals("hello world 123", Compare.normalize("Hello World 123"));
		assertEquals("hello world 123", Compare.normalize("HELLO WORLD 123"));
	}

}
