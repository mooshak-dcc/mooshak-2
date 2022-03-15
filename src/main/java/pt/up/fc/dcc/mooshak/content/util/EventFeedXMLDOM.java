package pt.up.fc.dcc.mooshak.content.util;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.erl.ReportType;
import pt.up.fc.dcc.mooshak.content.erl.TestType;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Group;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Question;
import pt.up.fc.dcc.mooshak.content.types.Questions;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.content.types.Tests;
import pt.up.fc.dcc.mooshak.evaluation.policy.IcpcRankingPolicy;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Util for building the event feed XML document to the Resolver The Event Feed
 * is an externally-accessible XML feed of contest data supplied by a Contest
 * Control System. The event feed is the primary way that external clients can
 * get the state of a running contest.
 * 
 * @author josepaiva
 */
public class EventFeedXMLDOM {
	public static final String SCOREBOARD_FREEZE_LENGTH = "01:00:00";

	private static Map<String, Integer> teamIds = new HashMap<>();
	private static Map<String, Integer> problemIds = new HashMap<>();
	private static Map<String, Integer> languageIds = new HashMap<>();
	private static Map<String, Integer> regionIds = new HashMap<>();

	public static void get(Contest contest) throws MooshakException {

		if (contest.getStart() == null)
			throw new MooshakException("Contest has not yet started!");

		if (contest.getStop() == null)
			throw new MooshakException("Contest does not have a stop date!");

		if (contest.getStop().after(new Date()))
			throw new MooshakException("Contest has not yet finished!");

		teamIds.clear();
		problemIds.clear();
		languageIds.clear();
		regionIds.clear();

		try {
			DocumentBuilderFactory icFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder icBuilder = icFactory.newDocumentBuilder();
			Document doc = icBuilder.newDocument();
			Element mainRootElement = doc.createElement("contest");
			doc.appendChild(mainRootElement);

			// info node
			String title = contest.getDesignation() == null ? contest.getIdName() : contest.getDesignation();
			long length = contest.getStop().getTime() - contest.getStart().getTime();
			mainRootElement.appendChild(getInfo(doc, contest.getIdName().toLowerCase().replaceAll("[^a-z0-9\\-]", "-"),
					Dates.showTime(new Date(length)), SCOREBOARD_FREEZE_LENGTH, IcpcRankingPolicy.PENALTY / (60 * 1000),
					true, (double) (contest.getStart().getTime() / 1000), title, title));

			// add languages
			addLanguages(contest, doc, mainRootElement);

			// add groups
			///addRegions(contest, doc, mainRootElement);

			// add judgements (or classifications)
			addJudgements(contest, doc, mainRootElement);

			// add problems
			addProblems(contest, doc, mainRootElement);

			// add teams
			addTeams(contest, doc, mainRootElement);

			// add clarifications
			addClarifications(contest, doc, mainRootElement);

			// add runs
			addRuns(contest, doc, mainRootElement);

			// add finalized
			addFinalized(contest, doc, mainRootElement);

			// output DOM XML to file
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.ENCODING, "US-ASCII");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult console = new StreamResult(new OutputStreamWriter(new FileOutputStream(
					contest.getAbsoluteFile().resolve(Contest.RESOLVER_EVENT_FEED_NAME).toFile()), Charset.forName("UTF-8")));
			transformer.transform(source, console);
		} catch (Exception e) {
			throw new MooshakException("Error generating feed: " + e.getMessage());
		}

	}

	private static void addLanguages(Contest contest, Document doc, Element mainRootElement) throws MooshakException {

		Languages languages = contest.open("languages");

		int i = 1;
		for (PersistentObject po : languages.getChildren(true)) {
			Language language = (Language) po;
			mainRootElement.appendChild(
					getLanguage(doc, i, language.getName() == null ? language.getIdName() : language.getName()));
			languageIds.put(language.getIdName(), i);
			i++;
		}

	}

	private static void addRegions(Contest contest, Document doc, Element mainRootElement) throws MooshakException {

		Groups groups = contest.open("groups");

		int i = 1;
		for (PersistentObject po : groups.getChildren(true)) {
			Group group = (Group) po;
			mainRootElement.appendChild(
					getRegion(doc, i, group.getAcronym() == null ? group.getIdName() : group.getAcronym()));
			regionIds.put(group.getIdName(), i);
			i++;
		}
	}

	private static void addJudgements(Contest contest, Document doc, Element mainRootElement) throws MooshakException {

		for (Classification c : Classification.values()) {

			// boolean penalty = !c.equals(Classification.ACCEPTED);
			mainRootElement.appendChild(getJudgement(doc, c.getAcronym(), c.toString()));
		}

	}

	private static void addProblems(Contest contest, Document doc, Element mainRootElement) throws MooshakException {

		Problems problems = contest.open("problems");

		int i = 1;
		List<Problem> problemList = problems.getContent();
		Collections.sort(problemList, new Comparator<Problem>() {

			@Override
			public int compare(Problem o1, Problem o2) {
				String o1Name = o1.getName() == null ? o1.getIdName() : o1.getName();
				String o2Name = o2.getName() == null ? o2.getIdName() : o2.getName();
				return o1Name.compareTo(o2Name);
			}
		});
		
		for (Problem problem : problemList) {

			String color = null, rgb = null;
			if (problem.getColor() != null) {
				rgb = String.format("#%02x%02x%02x", problem.getColor().getRed(), problem.getColor().getGreen(),
						problem.getColor().getBlue());
				color = rgb;
			}

			mainRootElement
					.appendChild(getProblem(doc, i, problem.getName() == null ? problem.getIdName() : problem.getName(),
							problem.getTitle(), color, rgb));

			problemIds.put(problem.getIdName(), i);
			i++;
		}

	}

	private static void addTeams(Contest contest, Document doc, Element mainRootElement) throws MooshakException {

		Groups groups = contest.open("groups");

		int i = 1;
		for (Team team : groups.getContent()) {
			teamIds.put(team.getIdName(), i);

			Group group = ((Group) team.getParent());

			String nationality = "";
			if (group.getFlag() != null)
				nationality = group.getFlag().getIsoCode();

			String university = group.getDesignation() == null ? group.getIdName() : group.getDesignation();

			mainRootElement.appendChild(getTeam(doc, i, team.getName() == null ? team.getIdName() : team.getName(),
					nationality, university, group.getAcronym(),
					group.getAcronym() == null ? group.getIdName() : group.getAcronym(), -1));
			i++;
		}

	}

	private static void addClarifications(Contest contest, Document doc, Element mainRootElement)
			throws MooshakException {

		Questions questions = contest.open("questions");

		int i = 1;
		for (PersistentObject po : questions.getChildren(true)) {
			Question question = (Question) po;

			int teamId = 0;
			if (question.getTeam() != null)
				teamId = teamIds.get(question.getTeam().getIdName());

			int problemId = 0;
			if (question.getProblem() != null)
				problemId = problemIds.get(question.getProblem().getIdName());

			mainRootElement.appendChild(getClar(doc, question.getAnswer(), !question.getAnswer().equals(""), i,
					question.getQuestion(), teamId, problemId, (double) (question.getTime().getTime() / 1000),
					(double) (question.getDate().getTime() / 1000), teamId == 0));

			i++;
		}

	}

	private static void addRuns(Contest contest, Document doc, Element mainRootElement) throws MooshakException {

		Submissions submissions = contest.open("submissions");

		int i = 1;
		for (Submission submission : submissions.getContent()) {

			if (submission.getProblem() == null || submission.getTeam() == null)
				continue;

			int teamId = teamIds.get(submission.getTeam().getIdName());
			int problem = problemIds.get(submission.getProblem().getIdName());
			
			String lang = "";
			if (submission.getLanguage() != null)
				lang = submission.getLanguage().getName() == null ? submission.getLanguage().getIdName()
						: submission.getLanguage().getName();

			boolean penalty = !submission.getClassify().equals(Classification.ACCEPTED);
			boolean solved = submission.getClassify().equals(Classification.ACCEPTED);

			double contestTime = (double) (submission.getTime().getTime() / 1000);
			double timestamp = (double) (submission.getDate().getTime() / 1000);
			mainRootElement.appendChild(getRun(doc, i, true, lang, penalty, problem,
					submission.getClassify().getAcronym(), solved, teamId, contestTime, timestamp));

			Tests tests = submission.getProblem().open("tests");
			int ntests = tests.getChildren(true).size();

			try {
				addTestcases(doc, mainRootElement, submission, i, contestTime, timestamp, ntests);
			} catch (MooshakException e) {
				// ignore
			}

			i++;
		}
	}

	private static void addTestcases(Document doc, Element mainRootElement, Submission submission, int id,
			double contestTime, double timestamp, int ntests) throws MooshakException {

		try {

			if (submission.getAllReportTypes().isEmpty())
				throw new Exception();

			ReportType reportType = submission.getReportType(submission.getAllReportTypes().size() - 1);

			int i = 1;
			for (TestType test : reportType.getTests().getTest()) {

				mainRootElement.appendChild(getTestcase(doc, i, true, ntests, id,
						test.getClassify().equals(Classification.ACCEPTED.toString()),
						Strings.acronymOf(test.getClassify()), contestTime, timestamp));
				i++;
			}
		} catch (Exception e) {
			for (int i = 1; i <= ntests; i++) {

				mainRootElement.appendChild(getTestcase(doc, i, true, ntests, id,
						submission.getClassify().equals(Classification.ACCEPTED),
						submission.getClassify().getAcronym(), contestTime, timestamp));
			}
		}
	}

	private static void addFinalized(Contest contest, Document doc, Element mainRootElement) {

		mainRootElement.appendChild(getFinalized(doc, (double) (contest.getStop().getTime() / (60 * 1000)), 1, 2, 3,
				"Finalized by Mooshak 2.0"));
	}

	/**
	 * Info
	 * 
	 * @param doc
	 * @param contestId
	 *            Contest identifier, which is of the form [a-z0-9-]{1,36}, i.e.
	 *            lowercase letters, digits, and dashes with length at most 36
	 *            (this allows a UUID).
	 * @param length
	 *            Length of contest (HH:MM:SS)
	 * @param scoreboardFreezeLength
	 *            Length of time at the end of the contest when scoreboard is
	 *            frozen (HH:MM:SS)
	 * @param penalty
	 *            Penalty time (minutes)
	 * @param started
	 *            Started flag
	 * @param startTime
	 *            Wall-clock start time of the contest (seconds)
	 * @param title
	 *            Contest title
	 * @param shortTitle
	 *            Shortened contest title
	 * @return info XML node
	 */
	public static Node getInfo(Document doc, String contestId, String length, String scoreboardFreezeLength,
			int penalty, boolean started, double startTime, String title, String shortTitle) {
		Element info = doc.createElement("info");
		info.appendChild(getTextNode(doc, info, "contest-id", contestId));
		info.appendChild(getTextNode(doc, info, "length", length));
		info.appendChild(getTextNode(doc, info, "scoreboard-freeze-length", scoreboardFreezeLength));
		info.appendChild(getTextNode(doc, info, "penalty", Integer.toString(penalty)));
		info.appendChild(getTextNode(doc, info, "started", Boolean.toString(started)));
		info.appendChild(getTextNode(doc, info, "starttime", Double.toString(startTime)));
		info.appendChild(getTextNode(doc, info, "title", title));
		info.appendChild(getTextNode(doc, info, "short-title", shortTitle));
		return info;
	}

	/**
	 * Language
	 * 
	 * @param doc
	 * @param id
	 *            Language identifier
	 * @param name
	 *            Language name
	 * @return Language XML node
	 */
	public static Node getLanguage(Document doc, int id, String name) {
		Element language = doc.createElement("language");
		language.appendChild(getTextNode(doc, language, "id", Integer.toString(id)));
		language.appendChild(getTextNode(doc, language, "name", name));
		return language;
	}

	/**
	 * Region
	 * 
	 * @param doc
	 * @param externalId
	 *            Identidier from the registration system
	 * @param name
	 *            Region name
	 * @return region XML node
	 */
	public static Node getRegion(Document doc, int externalId, String name) {
		Element region = doc.createElement("region");
		region.appendChild(getTextNode(doc, region, "external-id", Integer.toString(externalId)));
		region.appendChild(getTextNode(doc, region, "name", name));
		return region;
	}

	/**
	 * A judgement to a submission
	 * 
	 * @param doc
	 * @param acronym
	 *            Judgement acronym
	 * @param name
	 *            Judgement full name
	 * @return judgement XML node
	 */
	public static Node getJudgement(Document doc, String acronym, String name) {
		Element judgement = doc.createElement("judgement");
		judgement.appendChild(getTextNode(doc, judgement, "acronym", acronym));
		judgement.appendChild(getTextNode(doc, judgement, "name", name));
		return judgement;
	}

	/**
	 * A problem element is identified by it's id. If a problem element with the
	 * same id as an earlier element arrives the latter overrides the former.
	 * 
	 * @param doc
	 * @param id
	 *            Problem identifier
	 * @param label
	 *            Problem label, typically a single upper case letter
	 * @param name
	 *            Problem name
	 * @param color
	 *            problem color name (optional)
	 * @param rgb
	 *            problem color RGB hex value (optional)
	 * @return problem XML node
	 */
	public static Node getProblem(Document doc, int id, String label, String name, String color, String rgb) {
		Element problem = doc.createElement("problem");
		problem.appendChild(getTextNode(doc, problem, "id", Integer.toString(id)));
		problem.appendChild(getTextNode(doc, problem, "label", label));
		problem.appendChild(getTextNode(doc, problem, "name", name));

		if (color != null)
			problem.appendChild(getTextNode(doc, problem, "color", color));
		if (rgb != null)
			problem.appendChild(getTextNode(doc, problem, "rgb", rgb));

		return problem;
	}

	/**
	 * A team element is identified by it's team number. If a team element with
	 * the same team number as an earlier element arrives the latter overwrites
	 * the former.
	 * 
	 * @param doc
	 * @param n
	 *            Team number
	 * @param name
	 *            Team name
	 * @param nationality
	 *            Country code, ISO 3166-1 alpha-3
	 * @param university
	 *            University name
	 * @param universityShortName
	 *            shortened university name
	 * @param region
	 *            region name, must match some name given in a region element
	 * @param externalId
	 *            ID from ICPC registration system. Used to match this team to
	 *            other resources, such as university logos or team pictures
	 * @return team XML node
	 */
	public static Node getTeam(Document doc, int n, String name, String nationality, String university,
			String universityShortName, String region, int externalId) {
		Element team = doc.createElement("team");
		team.appendChild(getTextNode(doc, team, "id", Integer.toString(n)));
		team.appendChild(getTextNode(doc, team, "name", name));
		team.appendChild(getTextNode(doc, team, "nationality", nationality));
		team.appendChild(getTextNode(doc, team, "university", university));
		team.appendChild(getTextNode(doc, team, "university-short-name", universityShortName));

		if (region != null)
			team.appendChild(getTextNode(doc, team, "region", region));

		if (externalId != -1)
			team.appendChild(getTextNode(doc, team, "external-id", Integer.toString(externalId)));

		return team;
	}

	/**
	 * Clarification of a problem or about the contest
	 * @param doc
	 * @param answer
	 * @param answered Answered flag
	 * @param id Clarification identifier
	 * @param question
	 * @param team Team ID
	 * @param problem Problem ID
	 * @param time
	 * @param timestamp
	 * @param toAll to-all flag
	 * @return
	 */
	public static Node getClar(Document doc, String answer, boolean answered, int id, String question, int team,
			int problem, double time, double timestamp, boolean toAll) {

		Element submission = doc.createElement("clar");
		submission.appendChild(getTextNode(doc, submission, "answer", answer));
		submission.appendChild(getTextNode(doc, submission, "answered", Boolean.toString(answered)));
		submission.appendChild(getTextNode(doc, submission, "id", Integer.toString(id)));
		submission.appendChild(getTextNode(doc, submission, "question", question));
		submission.appendChild(getTextNode(doc, submission, "team", Integer.toString(team)));
		submission.appendChild(getTextNode(doc, submission, "problem", Integer.toString(problem)));
		submission.appendChild(getTextNode(doc, submission, "time", Double.toString(time)));
		submission.appendChild(getTextNode(doc, submission, "timestamp", Double.toString(timestamp)));
		submission.appendChild(getTextNode(doc, submission, "to-all", Boolean.toString(toAll)));
		return submission;
	}

	/**
	 * An event sent whenever a team makes a submission.
	 * 
	 * @param doc
	 * @param id
	 *            Submission ID
	 * @param teamNumber
	 *            Team Number
	 * @param problemLabel
	 *            Problem label
	 * @param language
	 *            Language name
	 * @param contestTime
	 *            Contest time when the run was submitted
	 * @param timestamp
	 *            Wall-clock time when the submission was made
	 * @return run XML node
	 */
	public static Node getRun(Document doc, int id, boolean judged, String language, boolean penalty, int problem,
			String result, boolean solved, int team, double time, double timestamp) {
		Element run = doc.createElement("run");
		run.appendChild(getTextNode(doc, run, "id", Integer.toString(id)));
		run.appendChild(getTextNode(doc, run, "judged", Boolean.toString(judged)));
		run.appendChild(getTextNode(doc, run, "language", language));
		run.appendChild(getTextNode(doc, run, "penalty", Boolean.toString(penalty)));
		run.appendChild(getTextNode(doc, run, "problem", Integer.toString(problem)));
		run.appendChild(getTextNode(doc, run, "result", result));
		run.appendChild(getTextNode(doc, run, "solved", Boolean.toString(solved)));
		run.appendChild(getTextNode(doc, run, "team", Integer.toString(team)));
		run.appendChild(getTextNode(doc, run, "time", Double.toString(time)));
		run.appendChild(getTextNode(doc, run, "timestamp", Double.toString(timestamp)));
		return run;
	}

	/**
	 * A testcase event is sent when a submission is judged against an input file.
	 * 
	 * @param doc
	 * @param id
	 *            Input file index (1-based)
	 * @param judged
	 * @param n
	 *            Total number of input files
	 * @param runId
	 *            Submission ID of submission that this run is part of
	 * @param solved
	 * @param judgement
	 *            Judgement acronym. Must match an acronym given in some earlier
	 *            judgement element
	 * @param contestTime
	 *            Contest time when the test was judged
	 * @param timestamp
	 *            Wall-clock time when the test was judged
	 * @return testcase XML node
	 */
	public static Node getTestcase(Document doc, int id, boolean judged, int n, int runId, boolean solved,
			String judgement, double time, double timestamp) {
		Element run = doc.createElement("testcase");
		run.appendChild(getTextNode(doc, run, "i", Integer.toString(id)));
		run.appendChild(getTextNode(doc, run, "n", Integer.toString(n)));
		run.appendChild(getTextNode(doc, run, "run-id", Integer.toString(runId)));
		run.appendChild(getTextNode(doc, run, "judged", Boolean.toString(judged)));
		run.appendChild(getTextNode(doc, run, "solved", Boolean.toString(solved)));
		run.appendChild(getTextNode(doc, run, "judgement", judgement));
		run.appendChild(getTextNode(doc, run, "time", Double.toString(time)));
		run.appendChild(getTextNode(doc, run, "timestamp", Double.toString(timestamp)));
		return run;
	}

	/**
	 * The finalized event is sent when the contest is finalized.
	 * 
	 * @param doc
	 * @param timestamp
	 *            Wall-clock time when the contest was finalized (seconds)
	 * @param lastGold
	 *            The last place to receive a gold (should always be 4 for a
	 *            compliant system)
	 * @param lastSilver
	 *            The last place to receive a silver (should always be 8 for a
	 *            compliant system)
	 * @param lastBronze
	 *            The last place to receive a bronze (should often be 12 but
	 *            could very possibly be something else)
	 * @param comment
	 *            A comment provided during the finalization
	 * @return finalized XML node
	 */
	public static Node getFinalized(Document doc, double timestamp, int lastGold, int lastSilver, int lastBronze,
			String comment) {
		Element finalized = doc.createElement("finalized");
		finalized.appendChild(getTextNode(doc, finalized, "timestamp", Double.toString(timestamp)));
		finalized.appendChild(getTextNode(doc, finalized, "last-gold", Integer.toString(lastGold)));
		finalized.appendChild(getTextNode(doc, finalized, "last-silver", Integer.toString(lastSilver)));
		finalized.appendChild(getTextNode(doc, finalized, "last-bronze", Integer.toString(lastBronze)));
		finalized.appendChild(getTextNode(doc, finalized, "comment", comment));
		return finalized;
	}

	/**
	 * Text node
	 * 
	 * @param doc
	 * @param element
	 * @param name
	 * @param value
	 * @return text node
	 */
	private static Node getTextNode(Document doc, Element element, String name, String value) {
		Element node = doc.createElement(name);
		node.appendChild(doc.createTextNode(value));
		return node;
	}
}
