package pt.up.fc.dcc.mooshak.content.util;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class FilenamesTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@Test
	public void rootNameTeste() {
		
		assertEquals("Hello",Filenames.rootName("Hello.java"));
		assertEquals("Hello",Filenames.rootName("Hello.class"));
		assertEquals("Hello",Filenames.rootName("some/dir/Hello.java"));
		assertEquals("Hello",Filenames.rootName("wrongdir/../Hello.java"));
		assertEquals("Hello",Filenames.rootName("/absolute/dir/Hello.class"));
	}
	
	@Test
	public void extensionTeste() {
		
		assertEquals("java",Filenames.extension("Hello.java"));
		assertEquals("class",Filenames.extension("Hello.class"));
		assertEquals("java",Filenames.extension("some/dir/Hello.java"));
		assertEquals("class",Filenames.extension("/absolute/dir/Hello.class"));
	}
	
	@Test
	public void sanitizeTeste() {
		
		Filenames.setStandardNameCount(0);
		
		assertEquals("zp",Filenames.sanitize("zp"));
		assertEquals("Z_Paulo",Filenames.sanitize("Zé Paulo"));
		assertEquals("Jos_Paulo_Leal",Filenames.sanitize("José Paulo Leal"));
		assertEquals("FCUP:1",Filenames.sanitize("FCUP/1"));
		assertEquals("team_1",Filenames.sanitize("team 1"));
		assertEquals("_:_",Filenames.sanitize("_/..\\_"));
		assertEquals("funny_name_1",Filenames.sanitize("(..)"));
		assertEquals("funny_name_2",Filenames.sanitize("[..]"));
		assertEquals("funny_name_3",Filenames.sanitize("funny_name_3"));
		assertEquals("funny_name_4",Filenames.sanitize("[(..)]"));
		assertEquals("funny_name_77",Filenames.sanitize("funny_name_#77"));
		assertEquals("funny_name_78",Filenames.sanitize("([(..)])"));
		
		assertEquals(78,Filenames.getStandardNameCount().intValue());
	}
}
