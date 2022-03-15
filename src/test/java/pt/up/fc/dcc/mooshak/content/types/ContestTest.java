package pt.up.fc.dcc.mooshak.content.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.Operations;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest.Challenge;
import pt.up.fc.dcc.mooshak.content.types.Contest.ContestType;
import pt.up.fc.dcc.mooshak.content.types.Contest.Service;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy.Policy;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo.ContestStatus;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

public class ContestTest {

	Contest loaded;
	Contest created;
	Contest copy;

	static Path createdPath;

	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException,
			IOException {
		PersistentObject.setHome("home");

		createdPath = Paths.get(CustomData.CONTESTS, "SOME_CONTEST");
	}

	@Before
	public void setUp() throws Exception {
		created = PersistentObject.create(createdPath, Contest.class);
		loaded = PersistentObject.openPath(CustomData.CONTEST_PATHNAME);

		System.out.println(PersistentObject.getHomePath().resolve(loaded.getPath()).toString());
		System.out.println(PersistentObject.getHomePath().resolve(Paths.get(CustomData.CONTESTS, "proto_icpc_copy")).toString());
		FileUtils.copyDirectory(new File(PersistentObject.getHomePath().resolve(loaded.getPath()).toString()), 
				new File(PersistentObject.getHomePath().resolve(Paths.get(CustomData.CONTESTS, "proto_icpc_copy")).toString()));
		
		copy = PersistentObject.openPath(Paths.get(CustomData.CONTESTS, "proto_icpc_copy")
				.toString());
	}

	@After
	public void cleanUp() throws MooshakContentException {
		loaded.reopen();
		created.delete();
		copy.delete();
	}

	@Test
	public void testType() throws MooshakContentException, IOException {
		assertEquals(ContestType.ICPC, created.getContestType());
		assertEquals(ContestType.ICPC, loaded.getContestType());

		for (ContestType type : ContestType.values()) {
			created.setContestType(type);
			assertEquals(type, created.getContestType());

			created.save();
			assertTrue(CustomData.checkContent(created, "Type", type.toString()));
			System.out.println(type.toString());
			
			loaded.setContestType(type);
			assertEquals(type, loaded.getContestType());
		}	
	}
	
	@Test
	public void testStatus() throws MooshakContentException {
		assertEquals(ContestStatus.CREATED, created.getContestStatus());
		assertEquals(ContestStatus.RUNNING, loaded.getContestStatus());

		for (ContestStatus status : ContestStatus.values()) {
			created.setContestStatus(status);
			assertEquals(status, created.getContestStatus());

			loaded.setContestStatus(status);
			assertEquals(status, loaded.getContestStatus());
		}
	}

	@Test
	public void testFatal() throws MooshakContentException {

		assertTrue(loaded.getFatal().startsWith("Verifique "));
		assertEquals("", created.getFatal());

		for (String text : CustomData.TEXTS) {
			loaded.setFatal(text);
			assertEquals(text, loaded.getFatal());

			created.setFatal(text);
			created.save();
			created.reopen();
			// This content is computed and should revert to the empty string
			assertEquals("", created.getFatal());
		}

		Date now = new Date();
		Date after = new Date(now.getTime() + 1000);

		created.setOpen(now);
		created.setStart(after);
		created.save();
		created.reopen();
		assertEquals("", created.getFatal());

		created.setOpen(after);
		created.setStart(now);
		created.save();
		created.reopen();
		assertEquals("open date cannot be after start.", created.getFatal());

		created.setOpen(after);
		created.setStart(after);
		created.setStop(now);
		created.save();
		created.reopen();
		assertEquals("open date cannot be after stop; "
				+ "start date cannot be after stop.", created.getFatal());
	}

	@Test
	public void testWarning() throws MooshakContentException {

		assertTrue(loaded.getWarning().startsWith("Directoria não vazia "));
		assertEquals("", created.getWarning());

		for (String text : CustomData.TEXTS) {
			loaded.setWarning(text);
			assertEquals(text, loaded.getWarning());

			created.setWarning(text);
			created.save();
			created.reopen();
			assertEquals(text, created.getWarning());
		}
	}

	@Test
	public void testDesignation() throws MooshakContentException {
		assertEquals("", created.getDesignation());
		assertEquals("ICPC contest prototype", loaded.getDesignation());

		for (String text : CustomData.TEXTS) {
			created.setDesignation(text);
			created.save();
			created.reopen();
			assertEquals(text, created.getDesignation());

			loaded.setDesignation(text);
			assertEquals(text, loaded.getDesignation());
		}
	}

	@Test
	public void testOrganizes() throws MooshakContentException {
		assertEquals("", created.getOrganizes());
		assertEquals("Mooshak distribution", loaded.getOrganizes());

		for (String text : CustomData.TEXTS) {
			created.setOrganizes(text);
			created.save();
			created.reopen();
			assertEquals(text, created.getOrganizes());

			loaded.setOrganizes(text);
			assertEquals(text, loaded.getOrganizes());
		}
	}

	@Test
	public void testEmail() throws MooshakContentException {
		assertEquals("", created.getEmail());
		assertEquals("zp@dcc.fc.up.pt", loaded.getEmail());

		for (String email : CustomData.EMAILS) {
			created.setEmail(email);
			created.save();
			created.reopen();
			assertEquals(email, created.getEmail());

			loaded.setEmail(email);
			assertEquals(email, loaded.getEmail());
		}
	}

	@Test
	public void testOpen() throws MooshakContentException {
		assertEquals(null, created.getOpen());
		assertEquals(1279062000, loaded.getOpen().getTime() / 1000);

		for (Date time : CustomData.DATES) {
			created.setOpen(time);
			created.save();
			created.reopen();
			assertEquals(time.getTime() / 1000,
					created.getOpen().getTime() / 1000);

			loaded.setOpen(time);
			assertEquals(time, loaded.getOpen());
		}
	}

	@Test
	public void testStart() throws MooshakContentException {
		assertEquals(null, created.getStart());
		assertEquals(1279062000, loaded.getStart().getTime() / 1000);

		for (Date time : CustomData.DATES) {
			created.setStart(time);
			created.save();
			created.reopen();
			assertEquals(time.getTime() / 1000,
					created.getStart().getTime() / 1000);

			loaded.setStart(time);
			assertEquals(time, loaded.getStart());
		}
	}

	@Test
	public void testStop() throws MooshakContentException {
		assertEquals(null, created.getStop());
		assertEquals(null, loaded.getStop());

		for (Date time : CustomData.DATES) {
			created.setStop(time);
			created.save();
			created.reopen();
			assertEquals(time.getTime() / 1000,
					created.getStop().getTime() / 1000);

			loaded.setStop(time);
			assertEquals(time, loaded.getStop());
		}
	}

	@Test
	public void testClose() throws MooshakContentException {
		assertEquals(null, created.getClose());
		assertEquals(null, loaded.getClose());

		for (Date time : CustomData.DATES) {
			created.setClose(time);
			created.save();
			created.reopen();
			assertEquals(time.getTime() / 1000,
					created.getClose().getTime() / 1000);

			loaded.setClose(time);
			assertEquals(time, loaded.getClose());
		}
	}

	@Test
	public void testHideListings() throws MooshakContentException {
		assertEquals(0, created.getHideListings());
		assertEquals(100, loaded.getHideListings());

		for (int value : CustomData.INTS) {
			created.setHideListings(value);
			created.save();
			created.reopen();
			assertEquals(value, created.getHideListings());

			loaded.setHideListings(value);
			assertEquals(value, loaded.getHideListings());
		}
	}

	@Test
	public void testPolicy() throws MooshakContentException {
		assertEquals(Policy.ICPC, created.getPolicy());
		assertEquals(Policy.ICPC, loaded.getPolicy());

		for (Policy policy : Policy.values()) {
			created.setPolicy(policy);
			created.save();
			created.reopen();
			assertEquals(policy, created.getPolicy());

			loaded.setPolicy(policy);
			assertEquals(policy, loaded.getPolicy());
		}

	}

	@Test
	public void testVirtual() throws MooshakContentException {
		assertEquals(false, created.isVirtual());
		assertEquals(false, loaded.isVirtual());

		for (boolean value : new boolean[] { true, false }) {
			created.setVirtual(value);
			created.save();
			created.reopen();
			assertEquals(value, created.isVirtual());

			loaded.setVirtual(value);
			assertEquals(value, loaded.isVirtual());
		}
	}

	@Test
	public void testRegister() throws MooshakContentException {
		assertEquals(false, created.isRegister());
		assertEquals(true, loaded.isRegister());

		for (boolean value : new boolean[] { true, false }) {
			created.setRegister(value);
			created.save();
			created.reopen();
			assertEquals(value, created.isRegister());

			loaded.setRegister(value);
			assertEquals(value, loaded.isRegister());
		}
	}

	@Test
	public void testService() throws MooshakContentException {
		assertEquals(Service.NONE, created.getService());
		assertEquals(Service.NONE, loaded.getService());

		for (Service service : Service.values()) {
			created.setService(service);
			created.save();
			created.reopen();
			assertEquals(service, created.getService());

			loaded.setService(service);
			assertEquals(service, loaded.getService());
		}

	}

	@Test
	public void testNotes() throws MooshakContentException {
		assertEquals("", created.getNotes());
		assertTrue(loaded.getNotes().startsWith("This a prototype"));

		for (String text : CustomData.TEXTS) {
			created.setNotes(text);
			created.save();
			created.reopen();
			assertEquals(text, created.getNotes());

			loaded.setNotes(text);
			assertEquals(text, loaded.getNotes());
		}
	}

	@Test
	public void testChallenge() throws MooshakContentException {
		assertEquals(Challenge.PROBLEMS, created.getChallenge());
		assertEquals(Challenge.PROBLEMS, loaded.getChallenge());

		for (Challenge challenge : Challenge.values()) {
			created.setChallenge(challenge);
			created.save();
			created.reopen();
			assertEquals(challenge, created.getChallenge());

			loaded.setChallenge(challenge);
			assertEquals(challenge, loaded.getChallenge());
		}

	}

	private static final String TEAM_ID = "some";
	private static final String PROBLEM_ID = "A";
	private static final String ID_PATTERN = "\\d+_\\w*_\\w*";

	@Test
	public void testGetTransaction() throws MooshakContentException {
		String id;

		id = loaded.getTransactionId(TEAM_ID, PROBLEM_ID);
		assertTrue(id.matches(ID_PATTERN));
		assertTrue(id.contains(PROBLEM_ID));
		assertTrue(id.endsWith(TEAM_ID));

		id = loaded.getTransactionId(null, PROBLEM_ID);
		assertTrue(id.matches(ID_PATTERN));
		assertTrue(id.contains(PROBLEM_ID));
		assertTrue(id.endsWith("_"));

		id = loaded.getTransactionId(TEAM_ID, null);
		assertTrue(id.matches(ID_PATTERN));
		assertTrue(id.contains("__"));
		assertTrue(id.endsWith(TEAM_ID));

		id = loaded.getTransactionId(null, null);
		assertTrue(id.matches(ID_PATTERN));
		assertTrue(id.endsWith("__"));

	}

	@Test
	public void testGetTeam() throws MooshakContentException {
		Team team = loaded.getTeam("team");

		assertEquals("team", team.getName());
	}

	@Test
	public void testGetRankingPolicy() throws MooshakException {
		RankingPolicy rankingPolicy = loaded.getRankingPolicy();
		RankingPolicy other = loaded.getRankingPolicy();

		assertNotNull(rankingPolicy);

		assertTrue(other == rankingPolicy);
	}

	@Test
	public void testChangeStatus() throws MooshakException {
		int delay = 50;

		created.setOpen(new Date(new Date().getTime() + delay));
		created.save();
		created.reopen();
		try {
			Thread.sleep(delay);
			assertEquals(ContestStatus.READY, created.getContestStatus());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		created.setStart(new Date(new Date().getTime() + delay));
		created.save();
		created.reopen();
		try {
			Thread.sleep(delay);
			assertEquals(ContestStatus.RUNNING, created.getContestStatus());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		created.setStop(new Date(new Date().getTime() + delay));
		created.save();
		created.reopen();
		try {
			Thread.sleep(delay);
			assertEquals(ContestStatus.FINISHED, created.getContestStatus());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		created.setClose(new Date(new Date().getTime() + delay));
		created.save();
		created.reopen();
		try {
			Thread.sleep(delay);
			assertEquals(ContestStatus.CONCLUDED, created.getContestStatus());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testIsFreezingListing() throws MooshakException {
		boolean isFreezing = false;
		
		if (created.getStop() != null)
			isFreezing = System.currentTimeMillis() > created.getStop()
				.getTime() - created.getHideListings() * 1000;

		assertEquals(isFreezing, created.isFreezingListing());
		
		created.setVirtual(true);
		created.save();
		created.reopen();
		assertEquals(false, created.isFreezingListing());

		if (loaded.getStop() != null)
			isFreezing = System.currentTimeMillis() > loaded.getStop()
					.getTime() - loaded.getHideListings() * 1000;
		else
			isFreezing = false;	

		assertEquals(isFreezing, loaded.isFreezingListing());
	}
	
	@Test
	public void testPrepare()  throws MooshakException {
		
		copy.setContestStatus(ContestStatus.CONCLUDED);
		copy.setStop(new Date(0));
		
		EventSender sender = EventSender.getEventSender();

		List<MooshakEvent> events;
		MooshakEvent event;
		String contestName = copy.getIdName();
		int serial = sender.getSerial(loaded.getIdName(), new Date());	
		
		Operations.getOperation(copy.getClass(), "PrepareClean").execute(copy, null);
		
		events = getRankingUpdateEvents(sender, contestName, serial);
		
		for (int i = 0; i < events.size(); i++) {
			event = events.get(i);
			
			RankingUpdate rankingEvent = (RankingUpdate) event;

			assertEquals("", rankingEvent.getValue("B"));
			assertEquals("", rankingEvent.getValue("J"));
			assertEquals("", rankingEvent.getValue("M"));
			assertEquals("", rankingEvent.getValue("P"));
			assertEquals("00:00:00", rankingEvent.getValue("points"));
		}
	}
	
	@Test
	public void testResetRankings()  throws MooshakException {
		
		Submissions submissions = copy.open("submissions");
		Submission submission = submissions.find("15709241_C_team");
		submission.delete();
		
		EventSender sender = EventSender.getEventSender();

		List<MooshakEvent> events;
		MooshakEvent event;
		String contestName = copy.getIdName();
		int serial = sender.getSerial(copy.getIdName(), new Date());	
		
		Iterator<? extends HasListingRow> it = copy.getRankingPolicy().getRows().iterator();
		
		Map<String, Map<String, String>> values = new HashMap<>();
		while (it.hasNext()) {
			HasListingRow row = it.next();
			
			String teamId = row.getTeam().getName();
			values.put(teamId, new HashMap<>());
			for (String key : row.getRow().keySet()) {
				values.get(teamId).put(key, row.getRow().get(key));
			}
		}
		
		Operations.getOperation(copy.getClass(), "Update").execute(copy, null);
		
		events = getRankingUpdateEvents(sender, contestName, serial);
		
		for (int i = 0; i < events.size(); i++) {
			event = events.get(i);
			
			RankingUpdate rankingEvent = (RankingUpdate) event;

			Map<String, String> expectedValues = values.get(rankingEvent.getValue("team"));
			if (rankingEvent.getValue("team").equals("team"))
				assertEquals("", rankingEvent.getValue("B"));
			else
				assertEquals(expectedValues.get("B"), rankingEvent.getValue("B"));
			assertEquals(expectedValues.get("J"), rankingEvent.getValue("J"));
			assertEquals(expectedValues.get("M"), rankingEvent.getValue("M"));
			assertEquals(expectedValues.get("P"), rankingEvent.getValue("P"));
			assertEquals(expectedValues.get("points"), rankingEvent.getValue("points"));
		}
	}
	
	private List<MooshakEvent> getRankingUpdateEvents(EventSender sender, String contestName,
			int serial) {
		return getRankingUpdateEvents(sender, contestName,null,serial);
	}

	private List<MooshakEvent> getRankingUpdateEvents(
			EventSender sender,
			String contest,
			Recipient recipient,
			int serial) {
		
		return  sender
				.eventsToDeliver(contest, recipient, serial)
				.stream()
				.filter(e -> e instanceof RankingUpdate)
				.collect(Collectors.toList());
	}
	
	@Test
	public void testSeparateCache() throws MooshakContentException {
		String email = "test@mooshak2.pt";
		Path loadedGroupsPath = Paths.get(loaded.getPath().toString(),"SOME_GROUPS");
		Path createdGroupsPath = Paths.get(createdPath.toString(),"SOME_GROUPS");
		
		Groups loadedGroups = PersistentObject.create(loadedGroupsPath, Groups.class);
		Groups createdGroups = PersistentObject.create(createdGroupsPath, Groups.class);
		
		Group createdGroup = PersistentObject.create(createdGroupsPath.resolve("SOME_GROUP"), Group.class);
		Group loadedGroup = PersistentObject.create(loadedGroupsPath.resolve("SOME_GROUP"), Group.class);

		Team loadedTeam = PersistentObject.create(Paths.get(loadedGroup.getPath().toString(),"SOME_TEAM"),Team.class);
		loadedTeam.setEmail(email);
		loadedTeam.save();
		loadedTeam = loadedGroups.find(loadedTeam.getIdName());
		
		assertEquals(email, loadedTeam.getEmail());
		
		Team createdTeam = PersistentObject.create(Paths.get(createdGroup.getPath().toString(),"SOME_TEAM"),Team.class);
		assertEquals("", createdGroups.find(createdTeam.getIdName()).getEmail());

		loadedGroups.delete();
		createdGroups.delete();
	}
}
