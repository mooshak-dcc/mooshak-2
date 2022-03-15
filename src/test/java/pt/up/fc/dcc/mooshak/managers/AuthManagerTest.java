package pt.up.fc.dcc.mooshak.managers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.management.UnixOperatingSystemMXBean;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.content.types.Group;
import pt.up.fc.dcc.mooshak.content.types.Group.AuthenticationType;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.LDAP;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;

public class AuthManagerTest {

	static AuthManager auth;

	@BeforeClass
	public static void setUpBeforeClass() {
		PersistentObject.setHome(CustomData.HOME);
		auth = AuthManager.getInstance();
	}

	@Test
	public void testGetInstance() {
		assertNotNull(auth);
	}

	@Test
	public void testAuthenticateTeam() throws MooshakException {
		Session session = auth.authenticate("proto_icpc", "team", "team");

		assertEquals("proto_icpc", session.getContest().getIdName());
		assertEquals("team", session.getProfile().getIdName());
		assertEquals("team", session.getParticipant().getIdName());
		assertEquals("team", session.getParticipant().getName());
	}

	@Test
	public void testAuthenticateTeamFail() {
		try {
			auth.authenticate("proto_icpc", "team", "wrong password");
			fail("Exception expected");
		} catch (MooshakException cause) {
			assertNotNull(cause);
			assertEquals("Invalid password", cause.getMessage());
		}
	}

	@Test
	public void testAuthenticateAdmin() throws MooshakException {
		Session session = auth.authenticate("proto_icpc", "admin", "admin");

		assertEquals("proto_icpc", session.getContest().getIdName());
		assertEquals("admin", session.getProfile().getIdName());
		assertEquals("admin", session.getParticipant().getIdName());
		assertEquals("admin", session.getParticipant().getName());
	}

	@Test
	public void testAuthenticateAdminFail() {
		try {
			auth.authenticate("proto_icpc", "team", "wrong password");
			fail("Exception expected");
		} catch (MooshakException cause) {
			assertNotNull(cause);
			assertEquals("Invalid password", cause.getMessage());
		}
	}

	@Test
	public void testAuthenticateNoOne() throws MooshakException {
		try {
			auth.authenticate("proto_icpc", "NoOne", "wrong password");
			fail("Exception expected");
		} catch (MooshakException cause) {
			assertNotNull(cause);
			assertEquals("Unknown user", cause.getMessage());
		}
	}

	@Test
	public void testAutorizeTeam() throws MooshakException {
		Session session = auth.authenticate("proto_icpc", "team", "team");
		try {
			auth.autorize(session, "team");
		} catch (MooshakException cause) {
			fail("Unexpected exception");
		}
	}

	@Test
	public void testNotAutorizeTeam() throws MooshakException {
		Session session = auth.authenticate("proto_icpc", "team", "team");
		try {
			auth.autorize(session, "admin");
		} catch (MooshakException cause) {
			assertEquals("Invalid profile", cause.getMessage());
		}
	}

	@Test
	public void testAutorizeAdmin() throws MooshakException {
		Session session = auth.authenticate("proto_icpc", "admin", "admin");
		try {
			auth.autorize(session, "admin");
		} catch (MooshakException cause) {
			fail("Unexpected exception");
		}

	}

	@Test
	public void testNotAutorizeAdmin() throws MooshakException {
		Session session = auth.authenticate("proto_icpc", "admin", "admin");
		try {
			auth.autorize(session, "team");
		} catch (MooshakException cause) {
			assertEquals("Invalid profile", cause.getMessage());
		}
	}

	@Test
	public void testGetDomains() throws MooshakException {
		List<ResultsContestInfo> domains = auth.getDomains(false, false);
		String protoDesignation = null;

		for (ResultsContestInfo resultsContestInfo : domains) {
			if (resultsContestInfo.getContestId().equals("proto_icpc"))
				protoDesignation = resultsContestInfo.getContestName();
		}
		assertEquals("ICPC contest prototype", protoDesignation);
	}

	@Test(expected = MooshakException.class)
	public void testRegisterError() throws MooshakException {
		auth.register("proto_icpc", "admin", "admin", null);
	}

	@Test
	public void testRegister() throws MooshakException {
		try {
			auth.register("proto_icpc", "jpaiva", "jpaiva", null);
		} catch (MooshakException cause) {
			assertEquals("This user already exists", cause.getLocalizedMessage());
		}

		Session session = auth.authenticate("proto_icpc", "jpaiva", "jpaiva");

		assertEquals("proto_icpc", session.getContest().getIdName());
		assertEquals("team", session.getProfile().getIdName());
		assertEquals("jpaiva", session.getParticipant().getIdName());
		assertEquals("jpaiva", session.getParticipant().getName());
	}

	@Test
	public void testMatchPreferredLanguage() {
		List<String> available = Arrays.asList("pt", "es", "en", "arb");

		assertEquals("pt", auth.matchPreferredLanguage("pt, en-gb;q=0.8, en;q=0.7", available));
		assertEquals("en", auth.matchPreferredLanguage("fr, en-gb;q=0.8, en;q=0.7", available));
		assertEquals("en", auth.matchPreferredLanguage("fr, en-gb;q=0.8, es;q=0.7", available));
		assertEquals("es", auth.matchPreferredLanguage("fr, de;q=0.8, es;q=0.7", available));
		assertEquals("es", auth.matchPreferredLanguage("fr, de;q=0.8, es-mx;q=0.7", available));
		assertEquals(null, auth.matchPreferredLanguage("fr, de;q=0.8, ru;q=0.7", available));
	}

	private static final int AVAILABLE = Runtime.getRuntime().availableProcessors();
	private static final ExecutorService POOL = Executors.newFixedThreadPool(4);

	protected static OperatingSystemMXBean OS = ManagementFactory.getOperatingSystemMXBean();
	protected static final UnixOperatingSystemMXBean LINUX = OS instanceof UnixOperatingSystemMXBean
			? (UnixOperatingSystemMXBean) OS : null;

	@Test
	public void testHighLoadRegisterAndLogin() throws MooshakException {

		EnkiManager.setGamificationServer("http://localhost:8080/Odin/");
		EnkiManager.setSequenciationServer("http://localhost:8080/Seqins/");

		Contest contest = PersistentObject.openPath("data/contests/proto_enki");

		EnkiManager.getInstance().insertCourseIntoSequenciationService(contest.getCourse());

		final int N_TEAMS = 200;
		final String prefix = "student";

		Logger.getLogger("").info("Register!");

		List<Future<Void>> futuresRegister = new ArrayList<>();

		for (int i = 0; i < N_TEAMS; i++) {

			final String studentId = prefix + i;
			Callable<Void> task = () -> {

				// register all teams
				try {
					auth.register("proto_enki", studentId, studentId, studentId);
				} catch (MooshakException cause) {
					assertEquals("This user already exists", cause.getLocalizedMessage());
				}

				return null;
			};

			futuresRegister.add(POOL.submit(task));
		}

		for (Future<Void> future : futuresRegister) {
			try {
				future.get();
			} catch (InterruptedException e) {
				fail("Thread interrupted");
			} catch (ExecutionException e) {
				fail("Thread exception: " + e.getMessage());
			}
		}

		Logger.getLogger("").info("Login!");

		long startTime = System.currentTimeMillis();
		double maxLoadRatio = OS.getSystemLoadAverage() / OS.getAvailableProcessors();

		List<Future<Session>> futuresLogin = new ArrayList<>();

		for (int i = 0; i < N_TEAMS; i++) {

			final String studentId = prefix + i;
			Callable<Session> task = () -> {

				// login all teams
				return auth.authenticate("proto_enki", studentId, studentId);
			};

			futuresLogin.add(POOL.submit(task));
		}

		List<Session> sessions = new ArrayList<>();

		int count = 0;
		for (Future<Session> future : futuresLogin) {

			double currentLoad = OS.getSystemLoadAverage() / 4;
			if (currentLoad > maxLoadRatio)
				maxLoadRatio = currentLoad;

			try {
				Session session = future.get();
				if (session == null) {
					System.out.println("Session not found");
					continue;
				}
				assertEquals("proto_enki", session.getContest().getIdName());
				assertEquals("team", session.getProfile().getIdName());
				assertEquals(prefix + count, session.getParticipant().getIdName());
				assertEquals(prefix + count, session.getParticipant().getName());

				sessions.add(session);
			} catch (InterruptedException e) {
				fail("Thread interrupted");
			} catch (ExecutionException e) {
				fail("Thread exception: " + e.getMessage());
			}

			count++;
		}

		Logger.getLogger("").info("Get resources for student!");

		for (Session session : sessions) {
			CourseList courseList = EnkiManager.getInstance().getResourcesForStudent(session);
		}

		Logger.getLogger("").info("Deleting teams and sessions!");

		for (Session session : sessions) {
			((Team) session.getParticipant()).delete();
			session.delete();
		}

		long stopTime = System.currentTimeMillis();

		Assert.assertTrue(60 * 1000 > stopTime - startTime);
		Assert.assertTrue(maxLoadRatio < 1);

		Logger.getLogger("").severe((stopTime - startTime) + "ms");
		Logger.getLogger("").severe(maxLoadRatio + " load factor");
	}

	@Test
	public void testHighLoadRegisterAndLoginLDAP() throws MooshakException {
		
		

		EnkiManager.setGamificationServer("http://localhost:8080/Odin/");
		EnkiManager.setSequenciationServer("http://localhost:8080/Seqins/");

		Contest contest = PersistentObject.openPath("data/contests/proto_enki");
		Groups groups = contest.open("groups");
		Group group = groups.open("Default");

		EnkiManager.getInstance().insertCourseIntoSequenciationService(contest.getCourse());

		final int N_TEAMS = 200;
		final String prefix = "student";

		Logger.getLogger("").info("Register!");

		List<Future<Void>> futuresRegister = new ArrayList<>();

		for (int i = 0; i < N_TEAMS; i++) {

			final String studentId = prefix + i;
			Callable<Void> task = () -> {

				// register all teams
				try {
					auth.register("proto_enki", studentId, studentId, studentId);
				} catch (MooshakException cause) {
					assertEquals("This user already exists", cause.getLocalizedMessage());
				}

				return null;
			};

			futuresRegister.add(POOL.submit(task));
		}

		for (Future<Void> future : futuresRegister) {
			try {
				future.get();
			} catch (InterruptedException e) {
				fail("Thread interrupted");
			} catch (ExecutionException e) {
				fail("Thread exception: " + e.getMessage());
			}
		}
		
		LDAP ldap = PersistentCore.create(Paths.get("data/configs/ldap/MY_LDAP"), 
				LDAP.class);
		Path homePath= PersistentObject.getHomePath();
		ldap.setCommand(homePath.resolve("data/configs/checks/ldap/resources/ldap_sim.sh").toString() 
				+ " " + 30);
		ldap.setBaseDN("baseDN");
		ldap.setHost("localhost");
		ldap.setBindDN("bindDN");
		group.setAuthentication(AuthenticationType.LDAP);
		group.setLdap(ldap);

		Logger.getLogger("").info("Login!");

		long startTime = System.currentTimeMillis();
		double maxLoadRatio = OS.getSystemLoadAverage() / OS.getAvailableProcessors();

		List<Future<Session>> futuresLogin = new ArrayList<>();

		for (int i = 0; i < N_TEAMS; i++) {

			final String studentId = prefix + i;
			Callable<Session> task = () -> {

				// login all teams
				return auth.authenticate("proto_enki", studentId, studentId);
			};

			futuresLogin.add(POOL.submit(task));
		}

		List<Session> sessions = new ArrayList<>();

		int count = 0;
		for (Future<Session> future : futuresLogin) {

			double currentLoad = OS.getSystemLoadAverage() / 4;
			if (currentLoad > maxLoadRatio)
				maxLoadRatio = currentLoad;

			try {
				Session session = future.get();
				if (session == null) {
					System.out.println("Session not found");
					continue;
				}
				assertEquals("proto_enki", session.getContest().getIdName());
				assertEquals("team", session.getProfile().getIdName());
				assertEquals(prefix + count, session.getParticipant().getIdName());
				assertEquals(prefix + count, session.getParticipant().getName());

				sessions.add(session);
			} catch (InterruptedException e) {
				fail("Thread interrupted");
			} catch (ExecutionException e) {
				fail("Thread exception: " + e.getMessage());
			}

			count++;
		}

		Logger.getLogger("").info("Get resources for student!");

		for (Session session : sessions) {
			CourseList courseList = EnkiManager.getInstance().getResourcesForStudent(session);
		}

		Logger.getLogger("").info("Deleting teams and sessions!");

		for (Session session : sessions) {
			((Team) session.getParticipant()).delete();
			session.delete();
		}

		long stopTime = System.currentTimeMillis();

		Logger.getLogger("").severe((stopTime - startTime) + "ms");
		Logger.getLogger("").severe(maxLoadRatio + " load factor");

		Assert.assertTrue(60 * 1000 > stopTime - startTime);
		Assert.assertTrue(maxLoadRatio < 1);
	}

}
