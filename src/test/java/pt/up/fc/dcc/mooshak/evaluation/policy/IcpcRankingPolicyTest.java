package pt.up.fc.dcc.mooshak.evaluation.policy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy.Policy;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo.ColumnType;

public class IcpcRankingPolicyTest {

	Team team;
	Contest contest;
	String contestName;
	Submissions submissions;
	Problem problem;
	IcpcRankingPolicy rankingPolicy;
	EventSender sender;
	
	@BeforeClass
	public static void setUpBeforeClass() throws MooshakContentException {
		PersistentObject.setHome("home");
	}
	
	@Before
	public void setUp() throws Exception {
		contest= PersistentObject.openPath(CustomData.CONTEST_PATHNAME);
		contest.reopen();
		
		submissions = PersistentObject.openPath(CustomData.SUBMISSIONS_PATHNAME);
		submissions.reopen();
		
		team = PersistentObject.openPath(CustomData.TEAM_PATHNAME);
		team.reopen();
		
		problem = PersistentObject.openPath(CustomData.PROBLEM_PATHNAME);
		problem. reopen();
		
		contestName = contest.getIdName();
		rankingPolicy = new IcpcRankingPolicy(contest);
		sender = EventSender.getEventSender();
	}
	
	
	Set<Submission> trash = new HashSet<>();
	
	@After
	public void cleanUp() throws Exception {
		for(Submission submission: trash)
			if(Files.exists(submission.getAbsoluteFile()))
				submission.delete();
	}
	
	@Test
	public void testGetColumns() throws Exception {
		
		List<ColumnInfo> expected = ColumnInfo.addColumns();
		
		expected.add(new ColumnInfo("order",0));
		expected.add(new ColumnInfo("#",12,ColumnType.RANK));
		expected.add(new ColumnInfo("flag",14,ColumnType.FLAG));
		expected.add(new ColumnInfo("group",20));
		expected.add(new ColumnInfo("team",40, ColumnType.TEAM));
		
		expected.add(new ColumnInfo("B",20, ColumnType.LABEL, "#ff0000"));
		expected.add(new ColumnInfo("J",20, ColumnType.LABEL, "#000000"));
		expected.add(new ColumnInfo("M",20, ColumnType.LABEL, "#000000"));
		expected.add(new ColumnInfo("P",20, ColumnType.LABEL, "#000000"));
		
		expected.add(new ColumnInfo("solved",17));
		expected.add(new ColumnInfo("points",20));
				
		assertEquals(expected,rankingPolicy.getColumns());
		
	}

	@Test
	public void test1Accepted() throws Exception {
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		Submission submission = makeSubmission(10 * 60 * 1000L);
		
		submission.setClassify(Classification.ACCEPTED);
		
		rankingPolicy.addSubmission(submission);
		
		events = getRankingUpdateEvents(serial);
		
		assertEquals(1,events.size());
		
		event = events.get(0);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;
				
		assertEquals("00:10:00 (0)",rankingEvent.getRecord().get("J"));
		assertEquals("1",rankingEvent.getRecord().get("solved"));
		
	}	
	
	@Test
	public void test1AcceptedAfter3Days() throws Exception {
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		Submission submission = makeSubmission(
				3 * 24 * 60 * 60 * 1000L + // 3 days 
				10 * 60 * 1000L);
		
		submission.setClassify(Classification.ACCEPTED);
		
		rankingPolicy.addSubmission(submission);
		
		events = getRankingUpdateEvents(serial);
		
		assertEquals(1,events.size());
		
		event = events.get(0);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;
				
		assertEquals("003 00:10 (0)",rankingEvent.getRecord().get("J"));
		assertEquals("1",rankingEvent.getRecord().get("solved"));
		
	}	
	
	@Test
	public void test1Acceptedtwice() throws Exception {
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		Submission submission;
		RankingUpdate rankingEvent;
		
		submission = makeSubmission(10 * 60 * 1000L);
		submission.setClassify(Classification.ACCEPTED);
		rankingPolicy.addSubmission(submission);
		
		submission = makeSubmission(15 * 60 * 1000L);
		submission.setClassify(Classification.ACCEPTED);
		rankingPolicy.addSubmission(submission);
		
		events = getRankingUpdateEvents(serial);
		
		assertEquals(2,events.size());
		
		event = events.get(0);
		
		assertTrue(event instanceof RankingUpdate);
		
		rankingEvent = (RankingUpdate) event;
				
		assertEquals("00:10:00 (0)",rankingEvent.getRecord().get("J"));
		assertEquals("1",rankingEvent.getRecord().get("solved"));
		
		event = events.get(1);
		
		assertTrue(event instanceof RankingUpdate);
		
		rankingEvent = (RankingUpdate) event;
		
		assertEquals("00:10:00 (0)",rankingEvent.getRecord().get("J"));
		assertEquals("1",rankingEvent.getRecord().get("solved"));
		
	}	
	
	
	@Test
	public void test1LongAccepted() throws Exception {
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		Submission submission = makeSubmission(70 * 60 * 1000L);
		
		submission.setClassify(Classification.ACCEPTED);
		
		rankingPolicy.addSubmission(submission);
		
		events =  getRankingUpdateEvents(serial);
		
		assertEquals(1,events.size());
		
		event = events.get(0);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;
				
		assertEquals("01:10:00 (0)",rankingEvent.getRecord().get("J"));
		
	}
	
	
	@Test
	public void test1WrongAnswer() throws Exception {
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		Submission submission = makeSubmission(10 * 60 * 1000L);
		
		submission.setClassify(Classification.WRONG_ANSWER);
		
		rankingPolicy.addSubmission(submission);
		
		events = getRankingUpdateEvents(serial); 
		
		assertEquals(1,events.size());
		
		event = events.get(0);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;
		
		assertEquals("------ (1)",rankingEvent.getRecord().get("J"));
		
	}
	

	@Test
	public void test2WrongAnswer1Accepted() throws Exception {
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		
		Submission submission;
		
		submission= makeSubmission(10 * 60 * 1000L);
		submission.setClassify(Classification.WRONG_ANSWER);
		rankingPolicy.addSubmission(submission);
		
		submission = makeSubmission(20 * 60 * 1000L);
		submission.setClassify(Classification.WRONG_ANSWER);
		rankingPolicy.addSubmission(submission);
		
		submission = makeSubmission(30 * 60 * 1000L);
		submission.setClassify(Classification.ACCEPTED);
		rankingPolicy.addSubmission(submission);
		
		events = getRankingUpdateEvents(serial);
		
		assertEquals(3,events.size());
		
		event = events.get(2);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;
		
		assertEquals("00:30:00 (2)",rankingEvent.getRecord().get("J"));
		assertEquals("01:10:00",rankingEvent.getRecord().get("points"));
		
	}	

	
	@Test
	public void testSendRankingsTo() throws MooshakException {
		
		Contest topas = PersistentObject.openPath("data/contests/ToPAS14/");
		IcpcRankingPolicy policy = RankingPolicy.getPolicy(Policy.ICPC, topas);
				
		String name = topas.getIdName();
		int serial = sender.getSerial(name, new Date());	
		List<MooshakEvent> events;
		Recipient recipient = new Recipient("admin");
		
		Map<String,Map<String,String>> ranking = new HashMap<>();
		List<String> teams;
		
		policy.sendRankingsTo(recipient);
		
		events = getRankingUpdateEvents(name,recipient,serial);
		
		for(MooshakEvent event: events) {
			if(event instanceof RankingUpdate) {
				RankingUpdate update = (RankingUpdate) event;
				Map<String,String> record = update.getRecord();
				ranking.put(record.get("team"), record);
			}
		}
		teams = new ArrayList<>(ranking.keySet());
				
		Collections.sort(teams, 
				(t1,t2) ->  
					ranking.get(t2).get("order").
						compareTo(ranking.get(t1).get("order")));
		
		
		// for(String team: teams) System.out.println(ranking.get(team));
		
		assertEquals(31,teams.size()); // added "team" to the teams  
	
		assertEquals("AEPBS", teams.get(0));
		assertEquals("AEPBS",ranking.get(teams.get(0)).get("team"));
		assertEquals("AEPBS",ranking.get(teams.get(0)).get("group"));
		assertEquals("pt",ranking.get(teams.get(0)).get("flag"));
		assertEquals("5",ranking.get(teams.get(0)).get("solved"));
		assertEquals("03:20:25",ranking.get(teams.get(0)).get("points"));
		assertEquals("00:09:57 (0)",ranking.get(teams.get(0)).get("A"));
		assertEquals("00:15:22 (0)",ranking.get(teams.get(0)).get("B"));
		assertEquals("00:33:50 (0)",ranking.get(teams.get(0)).get("C"));
		assertEquals("00:48:31 (0)",ranking.get(teams.get(0)).get("D"));
		assertEquals("01:12:45 (1)",ranking.get(teams.get(0)).get("E"));
		assertEquals("",ranking.get(teams.get(0)).get("F"));
		assertEquals("",ranking.get(teams.get(0)).get("G"));
	
		assertEquals("PVI Coders", teams.get(1));
		assertEquals("PVI Coders",ranking.get(teams.get(1)).get("team"));
		assertEquals("CPV",ranking.get(teams.get(1)).get("group"));
		assertEquals("pt",ranking.get(teams.get(1)).get("flag"));
		assertEquals("5",ranking.get(teams.get(1)).get("solved"));
		assertEquals("06:28:28",ranking.get(teams.get(1)).get("points"));
		assertEquals("02:32:28 (0)",ranking.get(teams.get(1)).get("A"));
		assertEquals("00:12:22 (0)",ranking.get(teams.get(1)).get("B"));
		assertEquals("00:23:37 (0)",ranking.get(teams.get(1)).get("C"));
		assertEquals("02:15:05 (1)",ranking.get(teams.get(1)).get("D"));
		assertEquals("00:44:56 (0)",ranking.get(teams.get(1)).get("E"));
		assertEquals("------ (1)",ranking.get(teams.get(1)).get("F"));
		assertEquals("------ (1)",ranking.get(teams.get(1)).get("G"));
		
		assertEquals("Trio fantástico", teams.get(6));
		assertEquals("Trio fantástico",ranking.get(teams.get(6)).get("team"));
		assertEquals("AEV",ranking.get(teams.get(6)).get("group"));
		assertEquals("pt",ranking.get(teams.get(6)).get("flag"));
		assertEquals("3",ranking.get(teams.get(6)).get("solved"));
		assertEquals("05:17:01",ranking.get(teams.get(6)).get("points"));
		assertEquals("01:41:18 (0)",ranking.get(teams.get(6)).get("A"));
		assertEquals("00:56:52 (1)",ranking.get(teams.get(6)).get("B"));
		assertEquals("------ (1)",ranking.get(teams.get(6)).get("C"));
		assertEquals("02:18:51 (0)",ranking.get(teams.get(6)).get("D"));
		assertEquals("",ranking.get(teams.get(6)).get("E"));
		assertEquals("",ranking.get(teams.get(6)).get("F"));
		assertEquals("",ranking.get(teams.get(6)).get("G"));
		
		assertEquals("Suricatas", teams.get(10));
		assertEquals("Suricatas",ranking.get(teams.get(10)).get("team"));
		assertEquals("EPI",ranking.get(teams.get(10)).get("group"));
		assertEquals("pt",ranking.get(teams.get(10)).get("flag"));
		assertEquals("2",ranking.get(teams.get(10)).get("solved"));
		assertEquals("03:01:46",ranking.get(teams.get(10)).get("points"));
		assertEquals("01:03:52 (0)",ranking.get(teams.get(10)).get("A"));
		assertEquals("01:17:54 (2)",ranking.get(teams.get(10)).get("B"));
		assertEquals("",ranking.get(teams.get(10)).get("C"));
		assertEquals("------ (3)",ranking.get(teams.get(10)).get("D"));
		assertEquals("------ (3)",ranking.get(teams.get(10)).get("E"));
		assertEquals("",ranking.get(teams.get(10)).get("F"));
		assertEquals("",ranking.get(teams.get(10)).get("G"));
		
		
		assertEquals("Bits Please", teams.get(14));
		assertEquals("Bits Please",ranking.get(teams.get(14)).get("team"));
		assertEquals("AEDS",ranking.get(teams.get(14)).get("group"));
		assertEquals("pt",ranking.get(teams.get(14)).get("flag"));
		assertEquals("1",ranking.get(teams.get(14)).get("solved"));
		assertEquals("00:41:02",ranking.get(teams.get(14)).get("points"));
		assertEquals("------ (12)",ranking.get(teams.get(14)).get("A"));
		assertEquals("00:41:02 (0)",ranking.get(teams.get(14)).get("B"));
		assertEquals("",ranking.get(teams.get(14)).get("C"));
		assertEquals("------ (8)",ranking.get(teams.get(14)).get("D"));
		assertEquals("",ranking.get(teams.get(14)).get("E"));
		assertEquals("",ranking.get(teams.get(14)).get("F"));
		assertEquals("",ranking.get(teams.get(14)).get("G"));
	}
	
	@Test
	public void testRemoveFailedSubmission() throws Exception {
		
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		
		Submission submission1 = makeSubmission(10 * 60 * 1000L);
		submission1.setClassify(Classification.WRONG_ANSWER);
		submission1.setMark(0);
		rankingPolicy.addSubmission(submission1);
		
		Submission submission2 = makeSubmission(20 * 60 * 1000L);
		submission2.setClassify(Classification.WRONG_ANSWER);
		submission2.setMark(22);
		rankingPolicy.addSubmission(submission2);
		
		Submission submission3 = makeSubmission(30 * 60 * 1000L);
		submission3.setClassify(Classification.ACCEPTED);
		submission3.setMark(100);
		rankingPolicy.addSubmission(submission3);
		
		rankingPolicy.removeSubmission(submission2);
		
		events = getRankingUpdateEvents(serial);
		
		assertEquals(4,events.size());
		
		event = events.get(3);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;

		assertEquals("00:30:00 (1)",rankingEvent.getRecord().get("J"));
		assertEquals("00:50:00",rankingEvent.getRecord().get("points"));
		
	}
	
	@Test
	public void testRemoveAcceptedSubmission() throws Exception {
		
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		
		Submission submission1 = makeSubmission(10 * 60 * 1000L);
		submission1.setClassify(Classification.WRONG_ANSWER);
		submission1.setMark(0);
		rankingPolicy.addSubmission(submission1);
		
		Submission submission2 = makeSubmission(20 * 60 * 1000L);
		submission2.setClassify(Classification.WRONG_ANSWER);
		submission2.setMark(22);
		rankingPolicy.addSubmission(submission2);
		
		Submission submission3 = makeSubmission(30 * 60 * 1000L);
		submission3.setClassify(Classification.ACCEPTED);
		submission3.setMark(100);
		rankingPolicy.addSubmission(submission3);
		
		rankingPolicy.removeSubmission(submission3);
		
		events = getRankingUpdateEvents(serial);
		
		assertEquals(4,events.size());
		
		event = events.get(3);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;
		
		assertEquals("------ (2)",rankingEvent.getRecord().get("J"));
		assertEquals("00:00:00",rankingEvent.getRecord().get("points"));
		
	}
	
	@Test
	public void testRemove1Accepted2AcceptedSubmission() throws Exception {
		
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		
		Submission submission1 = makeSubmission(10 * 60 * 1000L);
		submission1.setClassify(Classification.WRONG_ANSWER);
		submission1.setMark(0);
		rankingPolicy.addSubmission(submission1);
		
		Submission submission2 = makeSubmission(20 * 60 * 1000L);
		submission2.setClassify(Classification.WRONG_ANSWER);
		submission2.setMark(22);
		rankingPolicy.addSubmission(submission2);
		
		Submission submission3 = makeSubmission(30 * 60 * 1000L);
		submission3.setClassify(Classification.ACCEPTED);
		submission3.setMark(100);
		rankingPolicy.addSubmission(submission3);
		
		Submission submission4 = makeSubmission(40 * 60 * 1000L);
		submission4.setClassify(Classification.ACCEPTED);
		submission4.setMark(100);
		rankingPolicy.addSubmission(submission4);
		
		rankingPolicy.removeSubmission(submission3);
		
		events = getRankingUpdateEvents(serial);
		
		assertEquals(5,events.size());
		
		event = events.get(4);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;

		assertEquals("00:40:00 (2)",rankingEvent.getRecord().get("J"));
		assertEquals("01:20:00",rankingEvent.getRecord().get("points"));
		
	}
	
	@Test
	public void testChangeWAToAcceptedSubmission() throws Exception {
		
		List<MooshakEvent> events;
		MooshakEvent event;
		int serial = sender.getSerial(contestName, new Date());	
		
		Submission submission1 = makeSubmission(10 * 60 * 1000L);
		submission1.setClassify(Classification.WRONG_ANSWER);
		submission1.setMark(0);
		rankingPolicy.addSubmission(submission1);
		
		Submission submission2 = makeSubmission(20 * 60 * 1000L);
		submission2.setClassify(Classification.WRONG_ANSWER);
		submission2.setMark(22);
		rankingPolicy.addSubmission(submission2);
		
		Submission submission3 = makeSubmission(30 * 60 * 1000L);
		submission3.setClassify(Classification.WRONG_ANSWER);
		submission3.setMark(22);
		rankingPolicy.addSubmission(submission3);
		
		Submission submission4 = makeSubmission(40 * 60 * 1000L);
		submission4.setClassify(Classification.WRONG_ANSWER);
		submission4.setMark(22);
		rankingPolicy.addSubmission(submission4);
		
		Submission submission5 = makeSubmission(50 * 60 * 1000L);
		submission5.setClassify(Classification.WRONG_ANSWER);
		submission5.setMark(22);
		rankingPolicy.addSubmission(submission5);
		
		Submission submission6 = makeSubmission(60 * 60 * 1000L);
		submission6.setClassify(Classification.WRONG_ANSWER);
		submission6.setMark(22);
		rankingPolicy.addSubmission(submission6);
		
		Submission submission7 = makeSubmission(70 * 60 * 1000L);
		submission7.setClassify(Classification.ACCEPTED);
		submission7.setMark(100);
		rankingPolicy.addSubmission(submission7);
		
		rankingPolicy.removeSubmission(submission2);
		submission2.setClassify(Classification.ACCEPTED);
		submission2.setMark(100);
		rankingPolicy.addSubmission(submission2);
		
		events = getRankingUpdateEvents(serial);
		
		assertEquals(9,events.size());
		
		event = events.get(6);
		
		assertTrue(event instanceof RankingUpdate);
		
		RankingUpdate rankingEvent = (RankingUpdate) event;

		assertEquals("01:10:00 (6)",rankingEvent.getRecord().get("J"));
		assertEquals("03:10:00",rankingEvent.getRecord().get("points"));
		
		event = events.get(8);
		
		assertTrue(event instanceof RankingUpdate);
		
		rankingEvent = (RankingUpdate) event;

		assertEquals("00:20:00 (1)",rankingEvent.getRecord().get("J"));
		assertEquals("00:40:00",rankingEvent.getRecord().get("points"));
		
	}
	
	
	@Test
	public void testSaveRankings() {
		Map<String,Integer> ranks = new HashMap<>();
		
		for(String teamId: rankingPolicy.teams.keySet()) {
			ranks.put(teamId, rankingPolicy.teams.get(teamId).getRank());
		}
				
		rankingPolicy.finalizeRankings();
		
		for(String teamId: rankingPolicy.teams.keySet()) {
			assertEquals(ranks.get(teamId).intValue(), rankingPolicy.teams.get(teamId).getRank());
		}
	}
	
	
	private Submission makeSubmission(long time) 
			throws MooshakContentException {
	
		String id = time + contest.getTransactionId(
				CustomData.TEAM_ID,
				CustomData.PROBLEM_ID);
		
		Submission submission = submissions.create(id, Submission.class);
		
		submission.setTeam(team);
		submission.setProblem(problem);
		submission.setDate(new Date());
		submission.setTime(new Date(time));
		
		trash.add(submission);
		
		return submission;
	}
	
	private List<MooshakEvent> getRankingUpdateEvents(int serial) {
		return getRankingUpdateEvents(contestName,null,serial);
	}

	private List<MooshakEvent> getRankingUpdateEvents(
			String contest,
			Recipient recipient,
			int serial) {
		
		return  sender
				.eventsToDeliver(contest, recipient, serial)
				.stream()
				.filter(e -> e instanceof RankingUpdate)
				.collect(Collectors.toList());
	}

}
