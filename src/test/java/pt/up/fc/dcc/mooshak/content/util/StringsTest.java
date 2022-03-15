package pt.up.fc.dcc.mooshak.content.util;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringsTest {

	@Test
	public void testToTitle() {
		assertEquals("",Strings.toTitle(""));
		
		assertEquals("M",Strings.toTitle("m"));
		assertEquals("M",Strings.toTitle("M"));
		assertEquals("F",Strings.toTitle("f"));
		assertEquals("F",Strings.toTitle("F"));
		
		assertEquals("Hello",Strings.toTitle("hello"));
		assertEquals("Hello",Strings.toTitle("HELLO"));
		assertEquals("Hello",Strings.toTitle("HeLlO"));
		
		assertEquals("Hello World",Strings.toTitle("hello world"));
		assertEquals("Hello World",Strings.toTitle("hello  world"));
		assertEquals("Hello World",Strings.toTitle("hello   world"));
		assertEquals("Hello World",Strings.toTitle("hello    world"));
		assertEquals("Hello World",Strings.toTitle("Hello World"));
		assertEquals("Hello World",Strings.toTitle("HELLO WORLD"));
		
		assertEquals("Snow White and the Seven Dwarfs",
				Strings.toTitle("Snow White and the Seven Dwarfs"));
		assertEquals("Snow White and the Seven Dwarfs",
				Strings.toTitle("snow white and the seven dwarfs"));
		assertEquals("Snow White and the Seven Dwarfs",
				Strings.toTitle("SNOW WHITE AND THE SEVEN DWARFS"));
		
		assertEquals("The Last of the Mohicans",
				Strings.toTitle("The Last of the Mohicans"));
		assertEquals("The Last of the Mohicans",
				Strings.toTitle("the last of the mohicans"));
		assertEquals("The Last of the Mohicans",
				Strings.toTitle("THE LAST OF THE MOHICANS"));
		
	}
	
	@Test
	public void testAcronymOf() {
		assertEquals("JPL",Strings.acronymOf("José Paulo Leal"));
		assertEquals("JPL",Strings.acronymOf("José   Paulo    Leal"));
		assertEquals("JPL",Strings.acronymOf(" José Paulo Leal "));
		assertEquals("FCUP",Strings.acronymOf("Faculdade de Ciências da Univercidade do Porto"));
		assertEquals("AA",Strings.acronymOf("ÁLA Ãlõ"));
		assertEquals("SSP2",Strings.acronymOf("Some Stuf - Part 2"));
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void testToCammelCase() {
		
		assertEquals("HelloWorld",Strings.toCamelCase("hello world"));
		assertEquals("HelloWorld",Strings.toCamelCase("hello  world"));
		assertEquals("HelloWorld",Strings.toCamelCase("   hello  world "));
		assertEquals("HelloWorld",Strings.toCamelCase("Hello World"));
		assertEquals("HelloWorld",Strings.toCamelCase("HelloWorld"));
		assertEquals("HelloWorld",Strings.toCamelCase(" Hello World "));
		assertEquals("HelloWorld",Strings.toCamelCase("  Hello  World  "));
		
		assertEquals("AdHoc",Strings.toCamelCase("ad-hoc"));
	}
	
	@Test
	public void testToJavaConstant() {
		
		assertEquals("HELLO_WORLD",Strings.toJavaConstant("hello world"));
		assertEquals("HELLO_WORLD",Strings.toJavaConstant("hello  world"));
		assertEquals("HELLO_WORLD",Strings.toJavaConstant("   hello  world "));
		assertEquals("HELLO_WORLD",Strings.toJavaConstant("Hello World"));
		assertEquals("HELLO_WORLD",Strings.toJavaConstant(" Hello World "));
		assertEquals("HELLO_WORLD",Strings.toJavaConstant("  Hello  World  "));
		
		assertEquals("AD_HOC",Strings.toJavaConstant("ad-hoc"));
	}
	
	
	@Test
	public void testListOfStrings() {
		List<String> list;
		
		list = new ArrayList<>();
		for(String value: new String[] { "a", "b", "c", "d"})
			list.add(value);
		assertEquals(list,Strings.listOfStrings("a b c d"));
		
		list = new ArrayList<>();
		for(String value: new String[] { "ola", "ole"})
			list.add(value);
		assertEquals(list,Strings.listOfStrings("ola ole"));
		
		list = new ArrayList<>();
		for(String value: new String[] { "a1", "b2"})
			list.add(value);
		assertEquals(list,Strings.listOfStrings("a1 b2"));
		
		list = new ArrayList<>();
		for(String value: new String[] { "a b", "b c", "c d", "d e"})
			list.add(value);
		assertEquals(list,Strings.listOfStrings("{a b} {b c} {c d} {d e}"));
		
		list = new ArrayList<>();
		for(String value: new String[] { "a b c", "c d e"})
			list.add(value);
		assertEquals(list,Strings.listOfStrings("{a b c} {c d e}"));
		
	}
	
	
	@Test
	public void testStringOfList() {
		List<String> list;

		list = new ArrayList<>();
		for(String value: new String[] { "a", "b", "c", "d"})
			list.add(value);
		assertEquals("a b c d",Strings.stringOfList(list));

		
		list = new ArrayList<>();
		for(String value: new String[] { "ola", "ole"})
			list.add(value);
		assertEquals("ola ole",Strings.stringOfList(list));
		
		list = new ArrayList<>();
		for(String value: new String[] { "a1", "b2"})
			list.add(value);
		assertEquals("a1 b2",Strings.stringOfList(list));		

		list = new ArrayList<>();
		for(String value: new String[] { "a b", "b c", "c d", "d e"})
			list.add(value);
		assertEquals("{a b} {b c} {c d} {d e}",Strings.stringOfList(list));
		
		list = new ArrayList<>();
		for(String value: new String[] { "a b c", "c d e"})
			list.add(value);
		assertEquals("{a b c} {c d e}",Strings.stringOfList(list));
		
		
	}
}
