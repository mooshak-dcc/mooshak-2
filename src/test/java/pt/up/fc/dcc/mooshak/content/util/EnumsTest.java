package pt.up.fc.dcc.mooshak.content.util;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Vector;

import org.junit.Before;
import org.junit.Test;

public class EnumsTest {

	
	enum SimpleEnumExample { A , B, C, HelloWorld, Accepted;	}

	
	enum EnumExampleWithToString { A , B, C, HelloWorld, Accepted;
	
		public String toString() {
			return super.toString().toLowerCase();
	}
}
	
	@Before
	public void setUp() throws Exception {
	}
	

	@Test
	public void testGetNamesSimpleEnumExample() throws Exception {
		
		List<String> obtainedNames = Enums.getNames(SimpleEnumExample.class);
		
		List<String> expectedNames = new Vector<String>();
		for(SimpleEnumExample item: SimpleEnumExample.values())
			expectedNames.add(item.toString());
		
		assertEquals(expectedNames,obtainedNames);
	}
	

	@Test
	public void testGetNamesSimpleEnumExampleWithToString() throws Exception {
		
		List<String> obtainedNames = Enums.getNames(EnumExampleWithToString.class);
		
		List<String> expectedNames = new Vector<String>();
		for(EnumExampleWithToString item: EnumExampleWithToString.values())
			expectedNames.add(item.toString());
		
		assertEquals(expectedNames,obtainedNames);
	}


}
