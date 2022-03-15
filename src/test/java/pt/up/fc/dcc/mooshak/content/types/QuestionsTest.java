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

public class QuestionsTest {

	Questions loaded;
	Questions created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath = Paths.get(CustomData.CONTEST_PATHNAME,"SOME_QUESTIONS");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Questions.class);
		loaded = PersistentObject.openPath(CustomData.QUESTIONS_PATHNAME);
	}
	
	@After
	public void cleanUp()  throws Exception {
		loaded.reopen();
		created.delete();
	}

	@Test
	public void testActive() throws MooshakContentException {
		assertEquals(false,loaded.isActive());
		loaded.setActive(true);
		assertEquals(true,loaded.isActive());
		
		assertEquals(false,created.isActive());
		created.setActive(true);
		created.save(); created.reopen();
		assertEquals(true,created.isActive());
		
	}
	
	@Test
	public void testForward() throws MooshakContentException {
		assertEquals(false,loaded.isForward());
		loaded.setForward(true);
		assertEquals(true,loaded.isForward());
		
		assertEquals(false,created.isForward());
		created.setForward(true);
		created.save(); created.reopen();
		assertEquals(true,created.isForward());
		
	}
	
}
