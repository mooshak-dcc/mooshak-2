
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

public class UsersTest {

	Users loaded;
	Users created;
	
	static Path createdPath;
	
	Path base;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath = Paths.get(CustomData.CONTEST_PATHNAME,"SOME_USERS");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Users.class);
		loaded =  PersistentObject.openPath(CustomData.USERS_PATHNAME);
		
		base = Paths.get(CustomData.USERS_PATHNAME);
	}
	
	@After
	public void cleanUp()  throws Exception {
		loaded.reopen();
		created.delete();
	}
	
	@Test
	public void exist() {
		assertNotNull(loaded);
		assertNotNull(created);	
	}
	
}
