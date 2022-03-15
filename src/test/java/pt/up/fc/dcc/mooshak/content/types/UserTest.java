package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class UserTest {
	
	User created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get(CustomData.USERS_PATHNAME,"SOME_USER");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,User.class);
	}

	@Test
	public void testName() throws MooshakContentException {
		assertEquals("",created.getName());
		
		for(String text: CustomData.TEXTS) {
			created.setName(text);
			created.save(); created.reopen();
			assertEquals(text, created.getName());
		}
	}
	
	@Test
	public void testPassword() throws MooshakContentException {
		assertEquals("",created.getPassword());
		
		for(String plain: CustomData.TEXTS) {
			created.setPassword(plain);
			created.save(); created.reopen();
			assertNotEquals(plain, created.getPassword());
			assertTrue(created.authentic(plain));
		}
	}

}
