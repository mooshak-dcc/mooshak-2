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

public class FlagTest {

	Flag loaded;
	Flag created;

	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome("home");
		
		createdPath =  Paths.get("data","configs","flags","SOME_FLAG");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Flag.class);
		loaded  =  PersistentObject.openPath(CustomData.FLAG_PATHNAME);
		
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}

	@Test
	public void testName() {
		assertEquals("Portugal",loaded.getName());
		assertEquals("",created.getName());
		
		for(String text: CustomData.TEXTS) {
			created.setName(text);
			assertEquals(text,created.getName());
			
			loaded.setName(text);
			assertEquals(text,loaded.getName());
		}
	}

	@Test
	public void testIsoCode() {
		
		assertEquals("pt",loaded.getIsoCode());
		assertEquals("",created.getIsoCode());
		
		for(String text: CustomData.TEXTS) {
			created.setIsoCode(text);
			assertEquals(text,created.getIsoCode());
			
			loaded.setIsoCode(text);
			assertEquals(text,loaded.getIsoCode());
		}
	}

	@Test
	public void testImage() {
		assertEquals(loaded.getPath().resolve("pt.png"),loaded.getImage());
		assertEquals(null,created.getImage());
		
		for(String name: CustomData.FILENAMES) {
			Path file = loaded.getPath().resolve(name);
			
			created.setImage(file);
			assertEquals(file.getFileName(),created.getImage().getFileName());
		}
	}

}
