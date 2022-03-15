package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Problems.Presents;

public class ProblemsTest {

	Problems loaded;
	Problems created;

	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath = Paths.get(CustomData.CONTEST_PATHNAME,"SOME_PROBLEMS");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Problems.class);
		loaded = PersistentObject.openPath(CustomData.PROBLEMS_PATHNAME);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}
	
	@Test
	public void testPresents() throws MooshakContentException, IOException {
		assertEquals(Presents.RADIO,created.getPresents());
		assertEquals(Presents.RADIO,loaded.getPresents());
		
		for(Presents presents:Presents.values()) {
			created.setPresents(presents);
			created.save(); created.reopen();
			assertEquals(presents,created.getPresents());
			
			assertTrue(CustomData.checkContent(created, "Presents", 
					presents.toString().toLowerCase()));
			
			loaded.setPresents(presents);
			assertEquals(presents,loaded.getPresents());
		}
			
	}

}
