package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Group.AuthenticationType;

public class GroupTest {

	Group loaded;
	Group created;
	
	static Path createdPath;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get(CustomData.CONTEST_PATHNAME,"groups",
				"SOME_GROUP");
	}
	
	@Before
	public void setUp() throws Exception {
		created =  PersistentObject.create(createdPath,Group.class);
		loaded= PersistentObject.openPath(CustomData.GROUP_PATHNAME);
	}
	
	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
	}
	
	@Test
	public void testFatal() {

		assertEquals("",loaded.getFatal());
		assertEquals("",created.getFatal());

		for(String text: CustomData.TEXTS) { 
			loaded.setFatal(text);
			assertEquals(text,loaded.getFatal());

			created.setFatal(text);
			assertEquals(text,created.getFatal());
		}
	}

	@Test
	public void testWarning() {

		assertEquals("",loaded.getWarning());
		assertEquals("",created.getWarning());

		for(String text: CustomData.TEXTS) {
			loaded.setWarning(text);
			assertEquals(text,loaded.getWarning());

			created.setWarning(text);
			assertEquals(text,created.getWarning());
		}
	}
	
	@Test
	public void testDesignation() {
		assertEquals("myGroup",loaded.getDesignation());
		assertEquals("",created.getDesignation());
		
		for(String text: CustomData.TEXTS) {
			created.setDesignation(text);
			assertEquals(text,created.getDesignation());
		
			loaded.setDesignation(text);
			assertEquals(text,loaded.getDesignation());
		}
	}

	@Test
	public void testAcronym() {
		assertEquals("myGroup",loaded.getAcronym());
		assertEquals("",created.getAcronym());
		
		created.setDesignation("José Paulo Leal");
		assertEquals("JPL",created.getAcronym());
		
		for(String text: CustomData.TEXTS) {
			created.setAcronym(text);
			assertEquals(text,created.getAcronym());
		
			loaded.setAcronym(text);
			assertEquals(text,loaded.getAcronym());
		}
	}
	
	@Test
	public void testColor() {
		assertEquals(Color.black,loaded.getColor());
		assertEquals(Color.black,created.getColor());
		
		for(Color color: CustomData.COLORS) {
			created.setColor(color);
			assertEquals(color,created.getColor());
			
			loaded.setColor(color);
			assertEquals(color,loaded.getColor());
		}
	}
	
	@Test
	public void testFlag() throws MooshakContentException {
		assertEquals("Missing flag",loaded.getFlag().getName());
		assertEquals(null,created.getFlag());
		
	}
	
	@Test
	public void testLDAP() throws MooshakContentException {
		assertEquals(null,loaded.getLdap());
		assertEquals(null,created.getLdap());
		
		LDAP ldap = PersistentObject.openPath(CustomData.STUDENTS_LDAP_PATHNAME);
		
		created.setLdap(ldap);
		
		assertEquals(ldap, created.getLdap());
	}
	
	
	@Test
	public void testAuthentication() {
		assertEquals(AuthenticationType.BASIC,loaded.getAuthentication());
		assertEquals(AuthenticationType.BASIC,created.getAuthentication());
	
		for(AuthenticationType authentication: AuthenticationType.values()) {
			created.setAuthentication(authentication);
			assertEquals(authentication,created.getAuthentication());
			
			loaded.setAuthentication(authentication);
			assertEquals(authentication,loaded.getAuthentication());
		}
	}
	
	@Test
	public void testFind() throws MooshakContentException {
		Team team = loaded.find("team");
		assertNotNull(team);
		assertEquals("team",team.getIdName());
	}
	
}
