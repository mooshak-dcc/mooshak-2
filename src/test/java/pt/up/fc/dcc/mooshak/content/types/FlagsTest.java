package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class FlagsTest {

	Flags loaded;
	Flags created;
	Path base;
	
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get("data","configs","SOME_FLAGS");
	}
	@Before
	public void setUp() throws Exception {
		loaded =  PersistentObject.openPath(CustomData.FLAGS_PATHNAME);
		created =  PersistentObject.create(createdPath,Flags.class);
		
		base = Paths.get(CustomData.FLAG_PATHNAME);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}
	
	@Test
	public void exist() {
		assertNotNull(loaded);
		assertNotNull(created);
		
	}
	
}
