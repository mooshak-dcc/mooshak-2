package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class TeamTest {

	Team created;
	Team loaded;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath = Paths.get(CustomData.GROUP_PATHNAME,"SOME_TEAM");
		
	}
		
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Team.class);
		loaded = PersistentObject.openPath(CustomData.TEAM_PATHNAME);
			
	}
	
	@After
	public void cleanUp() throws Exception {
		loaded.reopen();
		created.delete();
	}

	@Test
	public void testName() throws MooshakContentException {
		assertEquals("",created.getName());
		assertEquals("team",loaded.getName());
		
		for(String text: CustomData.TEXTS) {
			created.setName(text);
			created.save(); created.reopen();
			assertEquals(text,created.getName());
		}
	}
	
	@Test
	public void testPassword() throws MooshakException {
		assertEquals("",created.getPassword());
		assertEquals("Go5vbBcXiyvLs",loaded.getPassword());
		
		for(String text: CustomData.TEXTS) {
			created.setPassword(text);
			created.save(); created.reopen();
			assertTrue(created.authentic(text));
		}
	}
	
	@Test
	public void testEmail() throws MooshakContentException {
		assertEquals("",created.getEmail());
		assertEquals("",loaded.getEmail());
		
		for(String text: CustomData.TEXTS) {
			created.setEmail(text);
			created.save(); created.reopen();
			assertEquals(text,created.getEmail());
		}
	}
	
	@Test
	public void testLocation() throws MooshakContentException {
		assertEquals("",created.getLocation());
		assertEquals("",loaded.getLocation());
		
		for(String text: CustomData.TEXTS) {
			created.setEmail(text);
			created.save(); created.reopen();
			assertEquals(text,created.getEmail());
		}
	}

	@Test
	public void testQualifies() throws MooshakContentException {
		assertEquals(true, created.isQualifies());
		assertEquals(true, loaded.isQualifies());

		created.setQualifies(false);
		created.save(); created.reopen();
		assertEquals(false, created.isQualifies());
	}
	
	
	@Test
	public void testGetProfile() throws MooshakContentException {
		Profile profile = loaded.getProfile();
		assertEquals(profile,created.getProfile());
		assertEquals("team",profile.getIdName());
	}

}
