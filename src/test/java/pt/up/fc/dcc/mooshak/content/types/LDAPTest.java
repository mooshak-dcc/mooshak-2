package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

public class LDAPTest {
	static Path createdPath;
	
	private static final List<String> LDAP_TEST_HOSTS   = Arrays.asList(
			"khato.dcc.fc.up.pt");
	
	private static final String COMMAND 				= "/usr/bin/ldapsearch";
	private static final String HOST    				= "servicos-labcc.dcc.fc.up.pt";
	private static final String BIND_DN_TEMPLATE		= "ou=Pessoas,dc=%s,dc=dcc";
	private static final String BASE_DN_TEMPLATE		= "dc=%s,dc=dcc";
	private static final String LOGIN_ATTRIBUTE			= "employeeNumber";	
	
	private static final String STUDENT_USER 			= "testStudent";
	private static final String STUDENT_PASSWORD 		= "StudentKey";
	private static final String STUDENT_DN 				= "alunos";
	private static final String STUDENT_EMPLOYEE_NUMBER	= "123456789";
	
	
	private static final String TEACHER_USER 			= "testProf";
	private static final String TEACHER_PASSWORD 		= "ProfKey";
	private static final String TEACHER_DN 				= "docentes";
	private static final String TEACHER_EMPLOYEE_NUMBER	= "987654321"; 
	
	
	private static final String WRONG_PASSWORD			= "wrong";
	
	LDAP loaded;
	LDAP created;
	
	@BeforeClass
	public static void setUpBeforeClass() 
			throws MooshakContentException, IOException {
		PersistentObject.setHome(CustomData.HOME);
		
		createdPath =  Paths.get(CustomData.LDAP_PATHNAME,"ldaps",
				"SOME_LDAP");
	}
	
	@Before
	public void setUp() throws Exception {
		created =  PersistentObject.create(createdPath,LDAP.class);
		loaded= PersistentObject.openPath(CustomData.STUDENTS_LDAP_PATHNAME);
				
	}

	@Test
	public void testCommand() {
		assertEquals(COMMAND,loaded.getCommand());
		assertEquals(null,created.getCommand());
		
		for(String text: CustomData.TEXTS) {
			created.setCommand(text);
			assertEquals(text,created.getCommand());
		}
	}
	
	@Test
	public void testHost() {
		assertEquals(HOST,loaded.getHost());
		assertEquals(null,created.getHost());
		
		for(String text: CustomData.TEXTS) {
			created.setHost(text);
			assertEquals(text,created.getHost());
		}
	}
	
	@Test
	public void tesBindDn() {
		assertEquals(String.format(BIND_DN_TEMPLATE,STUDENT_DN),loaded.getBindDN());
		assertEquals(null,created.getBindDN());
		
		for(String text: CustomData.TEXTS) {
			created.setBindDN(text);
			assertEquals(text,created.getBindDN());
		}
	}
	
	@Test
	public void tesBaseDn() {
		assertEquals(String.format(BASE_DN_TEMPLATE,STUDENT_DN),loaded.getBaseDN());
		assertEquals(null,created.getBaseDN());
		
		for(String text: CustomData.TEXTS) {
			created.setBaseDN(text);
			assertEquals(text,created.getBaseDN());
		}
	}
	
	@Test
	public void testAutenticateStudentLoaded() {
		
		if(! canTestLDAP())
			return;
		
		assertTrue(loaded.authenticate(STUDENT_USER,STUDENT_PASSWORD));
		assertFalse(loaded.authenticate(STUDENT_USER,WRONG_PASSWORD));
	}
	
	@Test
	public void testAutenticateStudentCreated() {
		
		if(! canTestLDAP())
			return;
		
		created.setCommand(COMMAND);
		created.setHost(HOST);
		
		created.setBindDN(String.format(BIND_DN_TEMPLATE, STUDENT_DN));
		created.setBaseDN(String.format(BASE_DN_TEMPLATE, STUDENT_DN));
		
		assertTrue(created.authenticate(STUDENT_USER,STUDENT_PASSWORD));
		assertFalse(created.authenticate(STUDENT_USER,WRONG_PASSWORD));
		
		created.setLoginAttribute("");
		assertTrue(created.authenticate(STUDENT_USER,STUDENT_PASSWORD));
		assertFalse(created.authenticate(STUDENT_USER,WRONG_PASSWORD));
		
		created.setLoginAttribute(LOGIN_ATTRIBUTE);
		assertTrue(created.authenticate(STUDENT_EMPLOYEE_NUMBER,STUDENT_PASSWORD));
		assertFalse(created.authenticate(STUDENT_USER,WRONG_PASSWORD));
	}
	
	
	@Test
	public void testAutenticateTeacher() {
		
		if(! canTestLDAP())
			return;
		
		created.setCommand(COMMAND);
		created.setHost(HOST);
		
		created.setBindDN(String.format(BIND_DN_TEMPLATE, TEACHER_DN));
		created.setBaseDN(String.format(BASE_DN_TEMPLATE, TEACHER_DN));
		
		assertTrue(created.authenticate(TEACHER_USER,TEACHER_PASSWORD));
		assertFalse(created.authenticate(TEACHER_USER,WRONG_PASSWORD));
		
		created.setLoginAttribute("");
		assertTrue(created.authenticate(TEACHER_USER,TEACHER_PASSWORD));
		assertFalse(created.authenticate(TEACHER_USER,WRONG_PASSWORD));
		
		created.setLoginAttribute(LOGIN_ATTRIBUTE);
		assertTrue(created.authenticate(TEACHER_EMPLOYEE_NUMBER,TEACHER_PASSWORD));
		assertFalse(created.authenticate(TEACHER_USER,WRONG_PASSWORD));
	}
	
	@Test
	public void testTest() {
		
		if(! canTestLDAP())
			return;
		
		assertEquals("LDAP server is reachable",loaded.test().getMessage());
		assertTrue(created.test().getMessage()
				.startsWith("Unexpected output from LDAP server:"));
	}

	/**
	 * Check if this host can be used for testing LDAP
	 * @return
	 */
	private boolean canTestLDAP() {
		String localhost = null;
		
		try {
			localhost = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			System.err.append("Cannot find hostname of localhost");
			return false;
		}

		return LDAP_TEST_HOSTS.contains(localhost);
	}
}
