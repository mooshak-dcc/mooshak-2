package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class PrintoutsTest {

	
	private static Path base = Paths.get(CustomData.PRINTOUTS_PATHNAME);
	private static Path TEMPLATE_FILE = base.resolve("some-template.html");
	private static Path CONFIG_FILE = base.resolve("some-config.css");
	
	Printouts loaded;
	Printouts created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get(CustomData.CONTEST_PATHNAME,"SOME_PRINTOUTS");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Printouts.class);
		loaded = PersistentObject.openPath(CustomData.PRINTOUTS_PATHNAME);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}
	

	@Test
	public void testActive() throws MooshakContentException {
		assertEquals(true,loaded.isActive());
		loaded.setActive(false);
		assertEquals(false,loaded.isActive());
		
		assertEquals(false,created.isActive());
		created.setActive(true);
		created.save(); created.reopen();
		assertEquals(true,created.isActive());
		
	}

	@Test
	public void testPrinter() throws MooshakContentException {
		assertEquals("",created.getPrinter());
		assertEquals("hp_cracs",loaded.getPrinter());
		
		for(String text: CustomData.TEXTS) {
			created.setPrinter(text);
			created.save(); created.reopen();
			assertEquals(text,created.getPrinter());
			
			loaded.setPrinter(text);
			assertEquals(text,loaded.getPrinter());
		}
	}

	
	@Test
	public void testTemplate() throws MooshakContentException {
		assertEquals(null,created.getTemplate());
		assertEquals(base.resolve("Template.html"),loaded.getTemplate());
		
		created.setTemplate(TEMPLATE_FILE);
		created.save(); created.reopen();
		assertEquals(TEMPLATE_FILE.getFileName(),
				created.getTemplate().getFileName());
		
	}
	
	@Test
	public void testConfig() throws MooshakContentException {
		assertEquals(null,created.getConfig());
		assertEquals(base.resolve("Config.css"),loaded.getConfig());
		
		created.setConfig(CONFIG_FILE);
		created.save(); created.reopen();
		assertEquals(CONFIG_FILE.getFileName(),
				created.getConfig().getFileName());	
	}
		
	@Test
	public void testListPending() throws MooshakContentException {
		assertEquals(false,loaded.isListPending());
		loaded.setListPending(true);
		assertEquals(true,loaded.isListPending());
		
		assertEquals(false,created.isListPending());
		created.setListPending(true);
		created.save(); created.reopen();
		assertEquals(true,created.isListPending());	
	}

	
}
