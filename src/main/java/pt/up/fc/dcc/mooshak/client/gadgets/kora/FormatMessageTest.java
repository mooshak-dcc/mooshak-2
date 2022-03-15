package pt.up.fc.dcc.mooshak.client.gadgets.kora;


import com.google.gwt.junit.client.GWTTestCase;


public class FormatMessageTest extends GWTTestCase {
	
	public void test() {
		fail("Not yet implemented");
	}

	public void testGetMarkMessage() {
		FormatMessage format = new FormatMessage();
		assertEquals("Sua tentativa estÃ¡ 97% perto da soluÃ§Ã£o",format.getMarkMessage("97"));
	}

	public String getModuleName() {  return "pt.up.fc.dcc.mooshak.EnkiModule"; }

	
}
