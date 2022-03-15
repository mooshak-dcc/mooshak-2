package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class GroupsTest {

	Groups loaded;
	Groups created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get(CustomData.CONTEST_PATHNAME,"SOME_GROUPS");
	}
	
	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath,Groups.class);
		loaded= PersistentObject.openPath(CustomData.GROUPS_PATHNAME);
	}

	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
	}
	
	@Test
	public void testFatal() throws MooshakContentException {

		assertEquals("",loaded.getFatal());
		assertEquals("",created.getFatal());

		for(String text: CustomData.TEXTS) { 
			loaded.setFatal(text);
			assertEquals(text,loaded.getFatal());

			created.setFatal(text);
			created.save(); created.reopen();
			assertEquals(text,created.getFatal());
		}
	}

	@Test
	public void testWarning() throws MooshakContentException {

		assertEquals("Apenas um grupo definido<br>",loaded.getWarning());
		assertEquals("",created.getWarning());

		for(String text: CustomData.TEXTS) {
			loaded.setWarning(text);
			assertEquals(text,loaded.getWarning());

			created.setWarning(text);
			created.save(); created.reopen();
			assertEquals(text,created.getWarning());
		}
	}

	@Test
	public void testPrinter() throws MooshakContentException {
		assertEquals("",created.getPrinter());
		assertEquals("xerox",loaded.getPrinter());
		
		for(String text: CustomData.TEXTS) {
			created.setPrinter(text);
			created.save(); created.reopen();
			assertEquals(text,created.getPrinter());
			
			loaded.setPrinter(text);
			assertEquals(text,loaded.getPrinter());
		}
	}
	
	@Test
	public void testTeamTemplate() throws MooshakContentException {
		Path template = loaded.getPath().resolve("Team_template.html");
		
		assertEquals(null,created.getTeamTemplate());
		assertEquals(template,loaded.getTeamTemplate());
		
		created.setTeamTemplate(template);
		created.save(); created.reopen();
		assertEquals(template.getFileName(),
				created.getTeamTemplate().getFileName());
		
	}
	
	@Test
	public void testPersonTemplate() throws MooshakContentException {
		Path template = loaded.getPath().resolve("Person_template.html");
		
		assertEquals(null,created.getPersonTemplate());
		assertEquals(template,loaded.getPersonTemplate());
		
		created.setPersonTemplate(template);
		created.save(); created.reopen();
		assertEquals(template.getFileName(),
				created.getPersonTemplate().getFileName());
		
	}
	
	@Test
	public void testPasswordTemplate() throws MooshakContentException {
		Path template = loaded.getPath().resolve("Password_template.html");
		
		assertEquals(null,created.getPasswordTemplate());
		assertEquals(template,loaded.getPasswordTemplate());
		
		created.setPasswordTemplate(template);
		created.save(); 
		created.reopen();
		assertEquals(
				template.getFileName(),
				created.getPasswordTemplate().getFileName());
		
	}
	
	@Test
	public void testConfig() throws MooshakContentException {
		Path template = loaded.getPath().resolve("Config.css");
		
		assertEquals(null,created.getConfig());
		assertEquals(loaded.getPath().resolve("Config.css"),loaded.getConfig());
		
		created.setConfig(template);
		created.save(); created.reopen();
		assertEquals(template.getFileName(),
				created.getConfig().getFileName());	
	}
	
	@Test
	public void testFind() throws MooshakContentException {
		Team team = loaded.find("team");
		assertNotNull(team);
		assertEquals("team",team.getIdName());
	}
	
	@Test
	public void testInvalidateTeamCacheOnRemove() throws MooshakContentException {
		Team team = PersistentObject.create(Paths.get(CustomData.GROUP_PATHNAME,"SOME_TEAM"),Team.class);
		team = loaded.find(team.getIdName());
		team.delete();
		team = loaded.find(team.getIdName());
		assertNull(team);
	}
	
	@Test
	public void testInvalidateGroupCacheOnRemove() throws MooshakContentException {
		/*Group group = PersistentObject.create(Paths.get(TestData.GROUP_PATHNAME + "2"),Group.class);
		Team team = PersistentObject.create(Paths.get(TestData.GROUP_PATHNAME + "2","SOME_TEAM"+2),Team.class);
		team = loaded.find(team.getIdName());
		group.delete();
		team = loaded.find(team.getIdName());
		assertNull(team);*/
	}
	
	@Test
	public void testInvalidateTeamCacheOnUpdate() throws MooshakContentException {
		String email = "test@mooshak2.pt";
		
		Team team = PersistentObject.create(Paths.get(CustomData.GROUP_PATHNAME,"SOME_TEAM"),Team.class);
		loaded.find(team.getIdName());
		team.setEmail(email);
		team.save();
		team = loaded.find(team.getIdName());
		assertEquals(email, team.getEmail());
		team.delete();
	}
}
