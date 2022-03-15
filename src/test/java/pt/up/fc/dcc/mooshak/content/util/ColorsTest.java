package pt.up.fc.dcc.mooshak.content.util;


import java.awt.Color;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorsTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetColor() throws MooshakContentException {
		
		assertEquals(Color.black,Colors.getColor("black"));
		assertEquals(Color.black,Colors.getColor("Black"));
		assertEquals(Color.black,Colors.getColor("BLACK"));
		
		assertEquals(Color.red,Colors.getColor("red"));
		assertEquals(Color.green,Colors.getColor("green"));
		assertEquals(Color.blue,Colors.getColor("blue"));
		
		assertEquals(Color.red,Colors.getColor("Red"));
		assertEquals(Color.green,Colors.getColor("Green"));
		assertEquals(Color.blue,Colors.getColor("Blue"));
		
		assertEquals(Color.red,Colors.getColor("RED"));
		assertEquals(Color.green,Colors.getColor("GREEN"));
		assertEquals(Color.blue,Colors.getColor("BLUE"));
		
		assertEquals(Color.yellow,Colors.getColor("yellow"));
		assertEquals(Color.magenta,Colors.getColor("magenta"));
		assertEquals(Color.cyan,Colors.getColor("cyan"));
		
		assertEquals(Color.pink,Colors.getColor("pink"));
		assertEquals(Color.orange,Colors.getColor("orange"));
		
		assertEquals(Color.red,Colors.getColor("#FF0000"));
		assertEquals(Color.green,Colors.getColor("#00FF00"));
		assertEquals(Color.blue,Colors.getColor("#0000FF"));
	}
	
	@Test
	public void testGetHtmlColor() {
		assertEquals("#FF0000",Colors.getHtmlColor(Color.red));
		assertEquals("#00FF00",Colors.getHtmlColor(Color.green));
		assertEquals("#0000FF",Colors.getHtmlColor(Color.blue));
		assertEquals("#FFFF00",Colors.getHtmlColor(Color.yellow));
		assertEquals("#FF00FF",Colors.getHtmlColor(Color.magenta));
		assertEquals("#00FFFF",Colors.getHtmlColor(Color.cyan));
		assertEquals("#000000",Colors.getHtmlColor(Color.black));
		assertEquals("#FFFFFF",Colors.getHtmlColor(Color.white));
	}
}
