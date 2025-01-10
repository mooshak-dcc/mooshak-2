package pt.up.fc.dcc.mooshak.content.types;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.Operations;
import pt.up.fc.dcc.mooshak.content.Operations.Operation;
import pt.up.fc.dcc.mooshak.content.PathManager;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submission.State;
import pt.up.fc.dcc.mooshak.content.types.Submissions.FeedbackCategories;
import pt.up.fc.dcc.mooshak.content.util.EventFeedXMLDOM;
import pt.up.fc.dcc.mooshak.content.util.JAXBContextFactory;
import pt.up.fc.dcc.mooshak.evaluation.policy.IcpcRankingPolicy;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy.Policy;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.managers.EnkiManager;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo.ContestStatus;
import pt.up.fc.dcc.mooshak.shared.events.ContestUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.ContextChangedEvent;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Course;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CoursePart;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;

/**
 * Modeling contests. A contest is a event where automatic evaluation is used.
 * It may be an exam or an assignment. Instances of this class are persisted
 * locally
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 *         Recoded in Java in May 2013 From a Tcl module coded in April 2001
 */
public class Contest extends PersistentObject {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(Contest.class.getSimpleName());

	public static final String RESOLVER_EVENT_FEED_NAME = "contest-feed.xml";

	public enum ContestType {
		DEFAULT(Gui.ICPC, Policy.ICPC, null, YesNo.NO, Service.NONE,
				Challenge.PROBLEMS, YesNo.YES, YesNo.YES, "", State.PENDING,
				"C+R", YesNo.NO, null, Evaluator.JUDGE),
		ICPC(Gui.ICPC, Policy.ICPC, 60, YesNo.NO, Service.NONE,
				Challenge.PROBLEMS, YesNo.NO, YesNo.NO, "", State.PENDING, "",
				YesNo.NO, null, Evaluator.JUDGE),
		IOI(Gui.ICPC, Policy.IOI, 300, YesNo.NO, Service.NONE,
				Challenge.PROBLEMS, YesNo.NO, YesNo.NO, "none", State.FINAL, "C",
				YesNo.YES, null, Evaluator.JUDGE),
		EXAM(Gui.ICPC, Policy.EXAM, null, YesNo.YES, Service.NONE,
				Challenge.PROBLEMS, YesNo.NO, YesNo.NO, "", State.FINAL, "all",
				YesNo.NO, null, Evaluator.JUDGE),
		ASSIGN(Gui.ICPC, 	Policy.EXAM, null, YesNo.NO, Service.NONE,
				Challenge.PROBLEMS, YesNo.NO, YesNo.NO, "none", State.FINAL, "all",
				YesNo.YES, (long) 9000000, Evaluator.JUDGE),
		QUIZ(Gui.ICPC, Policy.QUIZ, null, YesNo.YES, Service.NONE,
				Challenge.QUIZ, YesNo.NO, YesNo.NO, "", State.FINAL, "",
				YesNo.NO, null, Evaluator.JUDGE),
		SHORT(Gui.ICPC, Policy.SHORT, null, YesNo.NO, Service.NONE,
				Challenge.PROBLEMS, YesNo.NO, YesNo.NO,	"", State.PENDING, "C+R",
				YesNo.YES, null, Evaluator.JUDGE),
		SERVICE(Gui.ICPC, Policy.ICPC, null, YesNo.NO, Service.BOTH,
				Challenge.PROBLEMS, YesNo.NO, YesNo.NO, "", State.FINAL, "all",
				YesNo.YES, null, Evaluator.JUDGE),
		ENKI(Gui.ENKI, Policy.EXAM, 60, YesNo.NO, Service.BOTH, Challenge.PROBLEMS,
				YesNo.NO, YesNo.NO, "", State.PENDING, "", YesNo.YES, null,
				Evaluator.TEACHER);

		Gui gui;
		Policy policy;
		Integer hideListings;
		YesNo virtual;
		Service service;
		Challenge challenge;
		YesNo printouts_ListPending;
		YesNo ballons_ListPending;
		String submissions_ShowError;
		State submissions_defaultState;
		String submissions_showErrors;
		YesNo submissions_multipleAccepts;
		Long languages_maxProg;
		Evaluator evaluator;

		ContestType(Gui gui, Policy policy, Integer hideListings, YesNo virtual,
				Service service, Challenge challenge,
				YesNo printouts_ListPending, YesNo ballons_ListPending,
				String submissions_ShowError, State submissions_defaultState,
				String submissions_showErrors,
				YesNo submissions_multipleAccepts, Long languages_maxProg,
				Evaluator evaluator) {
			this.gui = gui;
			this.policy = policy;
			this.hideListings = hideListings;
			this.virtual = virtual;
			this.service = service;
			this.challenge = challenge;
			this.printouts_ListPending = printouts_ListPending;
			this.ballons_ListPending = ballons_ListPending;
			this.submissions_ShowError = submissions_ShowError;
			this.submissions_defaultState = submissions_defaultState;
			this.submissions_showErrors = submissions_showErrors;
			this.submissions_multipleAccepts = submissions_multipleAccepts;
			this.languages_maxProg = languages_maxProg;
			this.evaluator = evaluator;
		}

		/**
		 * Some contest types have special names
		 */
		public String toString() {
			switch(this) {
			case DEFAULT:
				return "Default";
			case ICPC:
				return "ICPC";
			case IOI:
				return "IOI";
			case SHORT:
				return "Short";
			case EXAM:
				return "Exam";
			case ASSIGN:
				return "Assign";
			case QUIZ:
				return "Quiz";
			case SERVICE:
				return "Service";
			default:
				return super.toString();
			}
		}

	}

	public enum Challenge {
		PROBLEMS, QUIZ
	};

	public enum Service {
		NONE, EVALUATION, CONTENT, BOTH
	}

	public enum Gui {
		ICPC, ENKI
	}

	public enum Evaluator {
		JUDGE, TEACHER
	}

	@MooshakAttribute(
			name = "Type",
			type = AttributeType.LABEL)
	private ContestType contestType;

	@MooshakAttribute(
			name = "Status",
			type = AttributeType.MENU)
	private ContestStatus contestStatus;

	@MooshakAttribute(
			name = "Fatal",
			type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(
			name = "Warning",
			type = AttributeType.LABEL)
	private String warning;

	@MooshakAttribute(
			name = "Designation",
			tip = "Event name")
	private String designation;

	@MooshakAttribute(
			name = "Organizes",
			tip = "Name of person or institution organizing the event")
	private String organizes;

	@MooshakAttribute(
			name = "Email",
			tip = "Mail address for sending messages to teams")
	private String email;

	@MooshakAttribute(
			name = "Open",
			type = AttributeType.DATE,
			tip = "Date/time to open the contest")
	private Date open;

	@MooshakAttribute(
			name = "Start",
			type = AttributeType.DATE,
			tip = "Date/time to start the contest")
	private Date start;

	@MooshakAttribute(
			name = "Stop",
			type = AttributeType.DATE,
			tip = "Date/time to stop the contest")
	private Date stop;

	@MooshakAttribute(
			name = "Close",
			type = AttributeType.DATE,
			tip = "Date/time to close the contest")
	private Date close;

	@MooshakAttribute(
			name = "HideListings",
			type = AttributeType.INTEGER,
			tip = "Minutes before end of contest to hide results")
	private Integer hideListings;

	@MooshakAttribute(
			name = "Policy",
			type = AttributeType.MENU,
			tip = "Evaluation and classification policy",
			help = "The grading of submissions and ranking of teams depends on \n"
					+ "policies. Different types of contests will use different \n"
					+ "policies, such as ICPC and IOI.\n\n"
					+ "The EXAM policy was designed for using Mooshak as a pedagogical\n"
					+ "tool.\n\n"
					+ "New policies can be easily added to Mooshak (EXPLAIN HOW).\n"
					+ "Policies are automatically set when a contest type is defined\n"
					+ "using menu command Contest | Type ...")
	private Policy policy;

	@MooshakAttribute(
			name = "Virtual",
			type = AttributeType.MENU,
			tip = "Is being used as a virtual contest",
			help = "In a virtual contest teams start competing in the moment\n"
					+ "they first login, independently of Start and End times.\n"
					+ " The team's time is automatically adjusted in relation\n"
					+ "to the contest's start time. Submissions, questions,\n"
					+ "etc, will use this  adjusted time. Listings will hide\n"
					+ " entries that occured after the ajusted time.")
	private YesNo virtual = MooshakAttribute.YesNo.NO;

	@MooshakAttribute(
			name = "PublicScoreboard",
			type = AttributeType.MENU,
			tip = "Show public scoreboard",
			help = "")
	private YesNo publicScoreboard = MooshakAttribute.YesNo.YES;

	@MooshakAttribute(
			name = "Register",
			type = AttributeType.MENU,
			tip = "Accept guest registration and send authentication by email",
			help = "Use this field to allow users to register themselves as\n"
					+ "teams using the Register button in the login dialog\n"
					+ "of Mooshak. Users will be able to choose the name and\n"
					+ "group of their teams and will receive the\n"
					+ "authentication data by email.")
	private YesNo register = MooshakAttribute.YesNo.NO;

	@MooshakAttribute(
			name = "TransactionLimit",
			type = AttributeType.INTEGER,
			tip = "Maximum number of transactions of each type to each user")
	private Integer transactionLimit;

	@MooshakAttribute(
			name = "TransactionLimitTime",
			type = AttributeType.DOUBLE,
			tip = "Time to reset the number of transactions of each\n"
					+ " user (hours ex.: 1.5)")
	private Double transactionLimitTime;

	@MooshakAttribute(
			name = "Service",
			type = AttributeType.MENU,
			tip = "What kind of services are exposed to remote programs")
	private Service service = Service.NONE;

	@MooshakAttribute(
			name = "Incremental",
			type = AttributeType.MENU,
			tip = "Supports incremental listings (For compability with 1.6)",
			help = "FOR VERSON 1.6 ONLY\n"
					+ "Use incremental listings. This will speed up large contests "
					+ "with any teams by avoiding to recompute listings that were "
					+ "recently generated. Listings are kept in a cache and reused "
					+ "to reply to requests with the same arguments received "
					+ "within a certain period. However, some people reported that "
					+ "occasionally incremental listings are inconsistent.")
	private YesNo incremental = MooshakAttribute.YesNo.NO;

	@MooshakAttribute(
			name = "Resources",
			type = AttributeType.FILE,
			tip = "File with resources configuration for Enki type contest",
			docSpec = "course-resources-docspec.json")
	private Path resources = null;

	@MooshakAttribute(
			name = "Notes",
			type = AttributeType.LONG_TEXT,
			tip = "")
	private String notes;

	@MooshakAttribute(
			name = "groups",
			type = AttributeType.DATA,
			tip = "")
	private Groups groups;

	@MooshakAttribute(
			name = "submissions",
			type = AttributeType.DATA,
			tip = "")
	private Submissions submissions;

	@MooshakAttribute(
			name = "validations",
			type = AttributeType.DATA,
			tip = "")
	private Submissions validations;

	@MooshakAttribute(
			name = "challenge",
			type = AttributeType.MENU,
			tip = "Regular contest with problems or quiz")
	private Challenge challenge;

	@MooshakAttribute(
			name = "gui",
			type = AttributeType.MENU,
			tip = "GUI to use in this contest")
	private Gui gui;

	@MooshakAttribute(
			name = "evaluator",
			type = AttributeType.MENU,
			tip = "Evaluator type at this contest")
	private Evaluator evaluator;

	@MooshakAttribute(
			name = "problems",
			type = AttributeType.DATA)
	private Problems problems;

	@MooshakAttribute(
			name = "quiz",
			type = AttributeType.HIDDEN)
	private Void quiz;

	@MooshakAttribute(
			name = "questions",
			type = AttributeType.DATA)
	private Questions questions;

	@MooshakAttribute(
			name = "printouts",
			type = AttributeType.DATA)
	private Printouts printouts;

	@MooshakAttribute(
			name = "balloons",
			type = AttributeType.DATA)
	private Balloons ballons;

	@MooshakAttribute(
			name = "languages",
			type = AttributeType.DATA)
	private Languages languages;

	@MooshakAttribute(
			name = "users",
			type = AttributeType.DATA)
	private Users users;

	@MooshakAttribute(name = "tournaments", type = AttributeType.DATA)
	private Tournaments tournaments;

	/****************************************************************
	 * \ Operations *
	 ****************************************************************/


	@MooshakOperation(
			name = "Prepare",
			inputable = true,
			tip = "Prepare contest to start")
	private CommandOutcome prepare(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();

		try {
			if (isRunning()) {
				outcome.setMessage("Error: The context is currently running,"
						+ " nothing done");
				return outcome;
			}
		} catch (MooshakException e) {
			outcome.setMessage("Error:" + e.getLocalizedMessage());
		}

		outcome.setContinuation("PrepareClean");

		return outcome;
	}

	@MooshakOperation(
			name = "PrepareClean",
			inputable = false,
			show = false)
	private CommandOutcome prepareClean() {
		CommandOutcome outcome = new CommandOutcome();

		try {
			for (String name : Arrays.asList("submissions", "balloons",
					"printouts", "questions", "validations")) {
				Preparable container = open(name);

				container.prepare();

			}
			if(getRankingPolicy() != null) {
				getRankingPolicy().reset();
				getRankingPolicy().sendRankingsTo(null);
			}

			outcome.setMessage("Contest was restarted");
		} catch (MooshakException cause) {
			outcome.setMessage("Error:" + cause.getLocalizedMessage());
		}

		return outcome;
	}

	@MooshakOperation(
			name = "Type",
			inputable = false,
			tip = "Configures several fields in this folder and sub-folders "
					+ "with pre-defined values")
	private CommandOutcome type() {
		CommandOutcome outcome = new CommandOutcome();

		for (ContestType contestType : ContestType.values()) {
			outcome.addPair("type", contestType.name());

			String properties = "";
			properties += contestType.policy.name() + ",";
			properties += contestType.hideListings != null ? contestType.hideListings
					+ ","
					: " ,";
			properties += contestType.virtual.name() + ",";
			properties += contestType.service.name() + ",";
			properties += contestType.challenge.name() + ",";
			properties += contestType.printouts_ListPending.name() + ",";
			properties += contestType.ballons_ListPending.name() + ",";
			properties += contestType.submissions_ShowError + ",";
			properties += contestType.submissions_showErrors + ",";
			properties += contestType.submissions_defaultState + ",";
			properties += contestType.submissions_multipleAccepts.name() + ",";
			properties += contestType.languages_maxProg != null ? contestType.languages_maxProg
					: "-";
			outcome.addPair(contestType.name(), properties);
		}

		if (contestType != null)
			outcome.addPair("currentType", contestType.name());
		else
			outcome.addPair("currentType", "default");

		outcome.setContinuation("TypeForm");

		return outcome;
	}

	@MooshakOperation(
			name = "TypeForm",
			inputable = true,
			show = false)
	private CommandOutcome typeForm(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();

		try {
			ContestType contestType = ContestType.valueOf(context
					.getValue("type"));

			setContestType(contestType);
			setPolicy(contestType.policy);
			setHideListings(contestType.hideListings);
			setVirtual(contestType.virtual == YesNo.YES ? true : false);
			setService(contestType.service);
			setChallenge(contestType.challenge);
			save();

			// ----------------- Printouts ----------------

			Path printoutsPath = getAbsoluteFile("printouts");
			if (!Files.exists(printoutsPath))
				create(printoutsPath, Printouts.class);

			Printouts printouts = open("printouts");

			printouts
					.setListPending(contestType.printouts_ListPending == YesNo.YES ? true
							: false);

			printouts.save();

			Path balloonsPath = getAbsoluteFile("balloons");
			if (!Files.exists(balloonsPath))
				create(balloonsPath, Balloons.class);

			Balloons balloons = open("balloons");

			balloons.setListPending(contestType.ballons_ListPending == YesNo.YES ? true
					: false);

			balloons.save();

			// ----------------- Languages ----------------

			if (contestType.languages_maxProg != null) {
				Path langPath = getAbsoluteFile("languages");
				if (!Files.exists(langPath))
					create(langPath, Languages.class);

				Languages languages = open("languages");
				languages.setMaxProg(contestType.languages_maxProg.intValue());
				languages.save();
			}

			// ----------------- Submissions ----------------

			Path submissionsPath = getAbsoluteFile("submissions");
			if (!Files.exists(submissionsPath))
				create(submissionsPath, Submissions.class);

			Path validationsPath = getAbsoluteFile("validations");
			if (!Files.exists(validationsPath))
				create(validationsPath, Submissions.class);

			Submissions submissions = open("submissions");
			Submissions validations = open("validations");

			FeedbackCategories category = null;
			switch (contestType.submissions_ShowError.toUpperCase()) {
			case "NONE":
				category = FeedbackCategories.NONE;
				break;
			case "C":
				category = FeedbackCategories.CLASSIFICATION;
				break;
			case "R":
				category = FeedbackCategories.REPORT;
				break;
			case "ALL":
				category = FeedbackCategories.ALL;
				break;
			default:
				break;
			}
			submissions.setGiveFeedback(category);
			if (category != null)
				validations.setGiveFeedback(Submissions.FeedbackCategories
						.valueOf(category.toString()));

			Set<Classification> showErrors = EnumSet
					.noneOf(Classification.class);
			switch (contestType.submissions_showErrors.toUpperCase()) {
			case "C":
				showErrors.add(Classification.COMPILE_TIME_ERROR);
				break;
			case "R":
				showErrors.add(Classification.RUNTIME_ERROR);
				break;
			case "C+R":
				showErrors.add(Classification.RUNTIME_ERROR);
				showErrors.add(Classification.COMPILE_TIME_ERROR);
				break;
			case "ALL":
				showErrors = EnumSet.allOf(Classification.class);
				break;
			default:
				break;
			}
			submissions.setShowErrors((EnumSet<Classification>) showErrors);
			validations.setShowErrors((EnumSet<Classification>) showErrors);

			submissions.setDefaultState(contestType.submissions_defaultState);
			validations.setDefaultState(contestType.submissions_defaultState);
			submissions
					.setMultipleAccepts(contestType.submissions_multipleAccepts == YesNo.YES ? true
							: false);
			validations
					.setMultipleAccepts(contestType.submissions_multipleAccepts == YesNo.YES ? true
							: false);

			submissions.save();
			validations.save();
		} catch (MooshakContentException e) {
			outcome.setMessage("Error:" + e.getMessage());
		}

		outcome.setMessage("Type Set");

		return outcome;
	}


	@MooshakOperation(
			name = "Update",
			inputable = false,
			tip = "Update rankings")
	private CommandOutcome refreshRanking() {
		CommandOutcome outcome = new CommandOutcome();

		try {
			if(getRankingPolicy() == null)
				outcome.setMessage("No ranking policy - nothing done");
			else {
				getRankingPolicy().reset();
				getRankingPolicy().sendRankingsTo(null);
				outcome.setMessage("Ranking policies updated");
			}
		} catch (MooshakException e) {
			outcome.setMessage("No ranking policy - nothing done");
		}

		return outcome;
	}


	@MooshakOperation(
			name = "Finalize Rankings",
			inputable = false,
			tip = "Finalize Rankings")
	private CommandOutcome finalizeRanking() {
		CommandOutcome outcome = new CommandOutcome();

		try {
			if(getRankingPolicy() == null)
				outcome.setMessage("No ranking policy - nothing done");
			else {
				getRankingPolicy().finalizeRankings();
				outcome.setMessage("Rankings finalized");
			}
		} catch (MooshakException e) {
			outcome.setMessage("No ranking policy - nothing done");
		}

		return outcome;
	}


	@MooshakOperation(
			name = "Export Resolver Feed",
			inputable = false,
			tip = "Export Resolver XML Feed")
	private CommandOutcome exportResolverFeed() {
		CommandOutcome outcome = new CommandOutcome();

		try {
			if (!(getRankingPolicy() instanceof IcpcRankingPolicy))
				throw new Exception("Resolver XML Feed is just available for ICPC contests!");
			EventFeedXMLDOM.get(this);
			outcome.setMessage("Resolver XML Feed exported!");
		} catch (Exception e) {
			outcome.setMessage("Error: " + e.getMessage());
			return outcome;
		}

		try {
			outcome.addPair("mimeType", "application/xml");
			outcome.addPair("file", Base64Coder.encodeLines(PersistentCore
					.getAbsoluteFileContentGuessingCharset(getAbsoluteFile()
							.resolve(RESOLVER_EVENT_FEED_NAME)).getBytes()));
		} catch (Exception cause) {
			outcome.setMessage("Cannot download feed file:" + cause
					.getLocalizedMessage());
			return outcome;
		}

		outcome.setContinuation("ResolverFeedDownload");

		return outcome;
	}

	@MooshakOperation(
			name = "Initialize Tournament",
			inputable = false,
			tip = "Initialize Tournament")
	private CommandOutcome initTournament() {

		CommandOutcome outcome = new CommandOutcome();
		try {
			Tournaments tournamentsPO = open("tournaments");
			Operation operation = Operations.getOperation(Tournaments.class, "Initialize Tournament");
			return operation.execute(tournamentsPO, null);
		} catch (MooshakException e) {
			outcome.setMessage("Error: " + e.getMessage());
		}

		return outcome;
	}

	@MooshakOperation(
			name="ResolverFeedDownload",
			inputable=true,
			show=false)
	private CommandOutcome generateResolverFeedDownload(MethodContext context) {
		return new CommandOutcome();
	}


	@MooshakOperation(
			name = "Cleanup",
			inputable = false,
			tip = "Cleanup previous compilations")
	private CommandOutcome cleanup() {
		CommandOutcome outcome = new CommandOutcome();

		try {
			((Submissions) open("submissions")).cleanup();
			((Submissions) open("validations")).cleanup();
			outcome.setMessage("Cleaned!");
		} catch (MooshakException e) {
			outcome.setMessage("Error cleaning compilations: " + e.getMessage());
			e.printStackTrace();
		}

		return outcome;
	}

	@MooshakOperation(
			name = "Create Tournament",
			inputable = true,
			show = false)
	private CommandOutcome createTournament(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();
		try {
			Tournaments tournamentsPO = open("tournaments");
			Operation operation = Operations.getOperation(Tournaments.class, "Create Tournament");
			return operation.execute(tournamentsPO, context);
		} catch (MooshakException e) {
			outcome.setMessage("Error: " + e.getMessage());
		}

		return outcome;
	}

	@MooshakOperation(
			name = "Set Up Tournament",
			inputable = true,
			show = false)
	private CommandOutcome setUpTournament(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();

		try {
			Tournaments tournamentsPO = open("tournaments");
			Operation operation = Operations.getOperation(Tournaments.class, "Set Up Tournament");
			return operation.execute(tournamentsPO, context);
		} catch (MooshakException e) {
			outcome.setMessage("Error: " + e.getMessage());
		}

		return outcome;
	}

	// -------------------- Setters and getters ----------------------//

	/**
	 * The type (ICPC, IOI, etc) of this contest. Default is ICPC.
	 *
	 * @return
	 */
	public ContestType getContestType() {
		if (contestType == null)
			return ContestType.ICPC;
		else
			return contestType;
	}

	/**
	 * Change the type (ICPC, IOI, etc) of this contest
	 *
	 * @param contestType
	 */
	public void setContestType(ContestType contestType) {
		this.contestType = contestType;
	}

	/**
	 * Status of this contest. Default is created.
	 *
	 * @return
	 */
	public ContestStatus getContestStatus() {
		if (contestStatus == null)
			return contestStatus = ContestStatus.CREATED;
		else
			return contestStatus;
	}

	/**
	 * Change status of this contest. Use {@code null} to revert to default.
	 *
	 * @param contestStatus
	 */
	public void setContestStatus(ContestStatus contestStatus) {
		this.contestStatus = contestStatus;
	}

	/**
	 * Fatal errors messages of this folder
	 *
	 * @return
	 */
	public String getFatal() {
		if (fatal == null)
			return "";
		else
			return fatal;

	}

	/**
	 * Set fatal error messages
	 *
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
	 * Warning errors messages of this folder
	 *
	 * @return
	 */
	public String getWarning() {
		if (warning == null)
			return "";
		else
			return warning;
	}

	/**
	 * Set warning error messages
	 *
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * Designation of this contest. Defaults to the empty string.
	 *
	 * @return
	 */
	public String getDesignation() {
		if (designation == null)
			return "";
		else
			return designation;
	}

	/**
	 * Set designation of this contest. Use {@code null} to revert to default.
	 *
	 * @param designation
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * Organizer's name. Defaults to the empty string
	 *
	 * @return
	 */
	public String getOrganizes() {
		if (organizes == null)
			return "";
		else
			return organizes;
	}

	/**
	 * Changes the organizer's name. Use {@code null} to revert to default.
	 *
	 * @param organizes
	 */
	public void setOrganizes(String organizes) {
		this.organizes = organizes;
	}

	/**
	 * Organizer's email. Defaults to the empty string
	 *
	 * @return
	 */
	public String getEmail() {
		if (email == null)
			return "";
		else
			return email;
	}

	/**
	 * Changes the organizer's email. Set {@code null} to revert to default.
	 *
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	public Date getOpen() {
		return open;
	}

	public void setOpen(Date open) {
		this.open = open;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStop() {
		return stop;
	}

	public void setStop(Date stop) {
		this.stop = stop;
	}

	public Date getClose() {
		return close;
	}

	public void setClose(Date close) {
		this.close = close;
	}

	/**
	 * Seconds before stopping when listings freeze. Defaults to 0
	 *
	 * @return
	 */
	public int getHideListings() {
		if (hideListings == null)
			return 0;
		else
			return hideListings;
	}

	/**
	 * Change seconds before stopping when listings freeze. Use {@code null} to
	 * revert to default
	 *
	 * @param hideListings
	 */
	public void setHideListings(Integer hideListings) {
		this.hideListings = hideListings;
	}

	/**
	 * Contest ranking policy, Defaults to ICPC
	 *
	 * @return
	 */
	public Policy getPolicy() {
		if (policy == null)
			return Policy.ICPC;
		else
			return policy;
	}

	/**
	 * Set contest ranking policy. Set {@code null} to revert to default.
	 */
	public void setPolicy(Policy policy) {
		this.policy = policy;
	}

	/**
	 * Is contest virtual? Defaults to {@code false}
	 *
	 * @return
	 */
	public boolean isVirtual() {
		if (YesNo.YES.equals(virtual))
			return true;
		else
			return false;
	}

	/**
	 * Set contest as virtual. Set {@code null} to revert to default.
	 *
	 * @param virtual
	 */
	public void setVirtual(boolean virtual) {
		if (virtual)
			this.virtual = YesNo.YES;
		else
			this.virtual = YesNo.NO;
	}

	/**
	 * Show scoreboard publicly? Defaults to {@code true}
	 *
	 * @return {@code false} if scoreboard is not publicly available, {@code true} otherwise
	 */
	public boolean isPublicScoreboard() {
		if (YesNo.NO.equals(publicScoreboard))
			return false;
		else
			return true;
	}

	/**
	 * Set show scoreboard publicly.
	 *
	 * @param publicScoreboard
	 */
	public void setPublicScoreboard(boolean publicScoreboard) {
		if (publicScoreboard)
			this.publicScoreboard = YesNo.YES;
		else
			this.publicScoreboard = YesNo.NO;
	}

	/**
	 * Is contest accepting registration? Defaults to {@code false}
	 *
	 * @return
	 */
	public boolean isRegister() {
		if (YesNo.YES.equals(register))
			return true;
		else
			return false;
	}

	/**
	 * Set contest to accept registrations. Use {@code null} to revert to
	 * default.
	 */
	public void setRegister(boolean register) {
		if (register)
			this.register = YesNo.YES;
		else
			this.register = YesNo.NO;
	}

	/**
	 * Kind of service provided by this contest. Defaults to {@code None}
	 *
	 * @return
	 */
	public Service getService() {
		if (service == null)
			return Service.NONE;
		else
			return service;
	}

	/**
	 * Changes the kind of service provided by this contest. Use {@code null} to
	 * revert to default.
	 *
	 * @param service
	 */
	public void setService(Service service) {
		this.service = service;
	}

	/**
	 * @return the transactionLimit
	 */
	public Integer getTransactionLimit() {
		return transactionLimit;
	}

	/**
	 * @param transactionLimit
	 *            the transactionLimit to set
	 */
	public void setTransactionLimit(Integer transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	/**
	 * @return the transactionLimitTime
	 */
	public Double getTransactionLimitTime() {
		return transactionLimitTime;
	}

	/**
	 * @param transactionLimitTime
	 *            the transactionLimitTime to set
	 */
	public void setTransactionLimitTime(Double transactionLimitTime) {
		this.transactionLimitTime = transactionLimitTime;
	}

	/**
	 * Notes on this contest. Defaults to the empty string
	 *
	 * @return
	 */
	public String getNotes() {
		if (notes == null)
			return "";
		else
			return notes;
	}

	/**
	 * Changes notes on this contest. Use {@code null} to revert to default.
	 *
	 * @param notes
	 */
	public void setNotes(String notes) {
		this.notes = notes;
	}

	/**
	 * Kind of challenge provided by this contest: problems or quiz. Defaults no
	 * problems
	 *
	 * @return
	 */
	public Challenge getChallenge() {
		if (challenge == null)
			return Challenge.PROBLEMS;
		else
			return challenge;
	}

	/**
	 * Changes Kind of challenge provided by this contest: problems or quiz. Use
	 * {@code null} to revert to default.
	 *
	 * @param challenge
	 */
	public void setChallenge(Challenge challenge) {
		this.challenge = challenge;
	}

	/**
	 * Kind of GUI provided by this contest Defaults ICPC
	 *
	 * @return
	 */
	public Gui getGui() {
		if (gui == null)
			return Gui.ICPC;
		else
			return gui;
	}

	/**
	 * Changes Kind of gui provided by this contest Use {@code null} to revert
	 * to default.
	 *
	 * @param gui
	 */
	public void setGui(Gui gui) {
		this.gui = gui;
	}

	/**
	 * Kind of evaluator used by this contest Defaults JUDGE
	 *
	 * @return
	 */
	public Evaluator getEvaluator() {
		if (evaluator == null)
			return Evaluator.JUDGE;
		else
			return evaluator;
	}

	/**
	 * Changes Kind of evaluator used by this contest Use {@code null} to revert
	 * to default.
	 *
	 * @param evaluator
	 */
	public void setEvaluator(Evaluator evaluator) {
		this.evaluator = evaluator;
	}

	public Path getResources() {
		if (resources == null)
			return null;
		else
			return getPath().resolve(resources);
	}

	public void setResources(Path resources) {
		this.resources = resources.getFileName();
	}

	// -------------------- Other methods ----------------------//

	/**
	 * Return a transaction identifier based on team and problem IDs. The prefix
	 * of the identifier is the time passed since the contest start.
	 *
	 * @param teamId
	 * @param problemId
	 * @return
	 * @throws MooshakContentException
	 */
	public synchronized String getTransactionId(String teamId, String problemId)
			throws MooshakContentException {

		return getTransactionId(teamId, problemId, true);
	}

	/**
	 * Return a transaction identifier based on team and problem IDs. The prefix
	 * of the identifier is the time passed since the contest start.
	 *
	 * @param teamId
	 * @param problemId
	 * @param timestamp prefix with timestamp?
	 * @return
	 * @throws MooshakContentException
	 */
	public synchronized String getTransactionId(String teamId, String problemId,
			boolean timestamp) throws MooshakContentException {

		if (teamId == null)
			teamId = "";
		if (problemId == null)
			problemId = "";

		if (timestamp)
			return String.format("%08d_%s_%s", getSecondsPassed(teamId), problemId,
					teamId);
		else
			return String.format("%s_%s", problemId, teamId);
	}

	/**
	 * Number of seconds since the contest started
	 * @return
	 * @throws MooshakContentException
	 */
	public long getSecondsPassed(String teamName) throws MooshakContentException {


		Date now = new Date();
		if(YesNo.YES.equals(virtual)) {
			Team team = getTeam(teamName);
			if (team == null) {
				if (start != null)
					return (now.getTime() - start.getTime()) / 1000;
				if (open != null)
					return (now.getTime() - open.getTime()) / 1000;
				return now.getTime() / 1000;
			} else
				return team.getSecondsFromLogin();
		} else if(start != null)
			return (now.getTime() - start.getTime()) / 1000;
		else if(open != null)
			return (now.getTime() - open.getTime()) / 1000;
		else
			return now.getTime() / 1000;
	}

	/**
	 * Get duration of this contest in seconds.
	 * It returns a large value if start or stop are undefined
	 * @return
	 */
	private long getDurationInSeconds() {

		if(start != null && stop != null)
			return (stop.getTime() - start.getTime()) / 1000;
		else
			return Long.MAX_VALUE;
	}

	/**
	 * Get Team in this contest with given id
	 *
	 * @param teamId
	 * @return
	 * @throws MooshakContentException
	 */
	Team getTeam(String teamId) throws MooshakContentException {
		Groups groups = open("groups");
		return groups.find(teamId);
	}

	/**
	 * Get User in this contest with given id
	 *
	 * @param userId
	 * @return
	 * @throws MooshakContentException
	 */
	User getUser(String userId) throws MooshakContentException {
		Users users = open("users");
		return users.find(userId);
	}

	private RankingPolicy rankingPolicy = null;

	/**
	 * Get ranking policy shared by all teams, if one exist.
	 * May be null if none was created yet
	 * @return
	 */
	public RankingPolicy getSharedRankingPolicy() {
		return rankingPolicy;
	}

	/**
	 * Get common ranking policy, share by all team in non virtual contests
	 * @return
	 * @throws MooshakException
	 */
	public synchronized RankingPolicy getRankingPolicy() throws MooshakException {

		if (rankingPolicy == null)
			rankingPolicy = getFreshRankingPolicy();

		return rankingPolicy;
	}

	/**
	 * Get a fresh (unused) ranking policy.
	 * Teams in virtual contests need their own contest policy
	 * @return
	 * @throws MooshakException
	 */
	public RankingPolicy getFreshRankingPolicy() throws MooshakException {
		return  RankingPolicy.getPolicy(getPolicy(), this);
	}

	/**
	 * Is this contest currently running?
	 * @return
	 * @throws MooshakException
	 */
	public boolean isRunning() throws MooshakException {
		if (getStart() == null)
			return false;

		Date now = new Date();
		if (getStop() == null && getStart().before(now))
			return true;

		if (getStart().before(now))
			if (getStop().after(now))
				return true;

		return false;
	}


	public boolean isRunning(String teamId) throws MooshakException {
		switch(contestStatus) {
		case RUNNING:
		case RUNNING_VIRTUALLY:
			return getSecondsPassed(teamId) < getDurationInSeconds();
		default:
			return false;
		}
	}




	/**
	 * Return true if contest listings are frozen; false otherwise
	 * @return
	 */
	public boolean isFreezingListing() {
		Date stop = getStop();

		if(stop == null || isVirtual())
			return false;

		long timeToHide = stop.getTime() - (long) getHideListings() * 60L * 1000L;

		return System.currentTimeMillis() > timeToHide;

	}

	@Override
	protected void created() throws MooshakContentException {
		// setContestStatus(computeStatus());
		updateStatus();
		broadcastContestStatusChange();

		attachCourseFileWatcher();
	}

	@Override
	protected boolean updated() throws MooshakContentException {
		// setContestStatus(computeStatus());
		updateStatus();
		sendProblemsToListeners();

		checkValues();

		EventSender.getEventSender().send(new ContextChangedEvent());
		broadcastContestStatusChange();

		attachCourseFileWatcher();

		return false;
	}

	@Override
	protected void destroyed() throws MooshakContentException {

		detachCourseFileWatcher();

		super.destroyed();
	}

	private Course course = null;

	/**
	 * Read course file
	 *
	 * @return {@link Course} course
	 */
	private Course readCourseFile() {

		Path resourcesPath = getResources();

		if (resourcesPath == null) {
			LOGGER.info("Could not read resources: path not set.");
			return course;
		}

		Path filePath = getAbsoluteFile().resolve(resourcesPath.getFileName());

		if (!Files.exists(filePath)) {
			LOGGER.info("Could not read resources: file does not exist.");
			return course;
		}

		try {

			Unmarshaller unmarshaller = null;
			try {
				JAXBContext jc = JAXBContextFactory.getInstance().getJaxBContext(Course.class);
				unmarshaller = jc.createUnmarshaller();
			} catch (JAXBException e1) {
				throw new MooshakException("Creating unmarshaler!");
			}

			InputStream is = new FileInputStream(filePath.toFile());
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			course = (Course) unmarshaller.unmarshal(reader);

		    reader.close();
		} catch (MooshakException | IOException | JAXBException e) {
			LOGGER.info("Could not read resources. Error: " + e.getMessage());
		}

		return course;
	}

	/**
	 * @return the course
	 */
	public Course getCourse() {
		return course;
	}

	private String courseXmlAttached = null;
	private Runnable courseXmlCbDetachModification = null;
	private Runnable courseXmlCbDetachDeletion = null;

	/**
	 * Attach course file watcher
	 */
	public synchronized void attachCourseFileWatcher() {

		if (getResources() == null) {
			detachCourseFileWatcher();
			return;
		}

		Path courseFileName = getResources().getFileName();

		if (courseFileName != null && courseXmlAttached != null
				&& courseFileName.toString().equals(courseXmlAttached)) {
			return;
		} else if (courseFileName != null) {

			detachCourseFileWatcher();

			Executors.newSingleThreadExecutor().submit(() -> {
				readCourseFile();
				updateResources();
			});

			Path courseFilePath = getAbsoluteFile(courseFileName.toString());

			courseXmlCbDetachModification = PathManager.getInstance()
				.watchRegularFileForModification(courseFilePath.toString(), new Runnable() {

				@Override
				public void run() {
					LOGGER.severe("Reading course");
					readCourseFile();
					updateResources();
				}
			});

			courseXmlCbDetachDeletion = PathManager.getInstance()
				.watchRegularFileForDeletion(courseFilePath.toString(), new Runnable() {

				@Override
				public void run() {
					course = null;
				}
			});

			courseXmlAttached = courseFileName.toString();
		}
	}

	/**
	 * Detach course file watcher
	 */
	public void detachCourseFileWatcher() {

		if (courseXmlCbDetachModification != null)
			courseXmlCbDetachModification.run();

		if (courseXmlCbDetachDeletion != null)
			courseXmlCbDetachDeletion.run();

		courseXmlCbDetachModification = null;
		courseXmlCbDetachDeletion = null;
		courseXmlAttached = null;
	}

	/**
	 * Updates resources of the course if it is a Enki contest
	 */
	private void updateResources() {

		if (course == null)
			return;

		try {

			setOpen(course.getCreationDate());
			setStart(course.getStartDate());
			setStop(course.getStopDate());
			setClose(course.getEndDate());

			boolean partsChanged = false;
			for (CoursePart coursePart : course.getChildren()) {

				partsChanged = partsChanged || updateResources(coursePart);
			}

			EnkiManager.getInstance().updateGamificationServiceResources(course);
			EnkiManager.getInstance().insertCourseIntoSequenciationService(course);

			save();

	        /*Marshaller marshaller = null;
			try {
				JAXBContext jc = JAXBContextFactory.getInstance().getJaxBContext(Course.class);
				marshaller = jc.createMarshaller();
			} catch (JAXBException e1) {
				throw new MooshakException("Creating marshaler!");
			}

			marshaller.marshal(course, getAbsoluteFile(getResources().getFileName().toString())
					.toFile());*/
		} catch (Exception e) {
			LOGGER.severe("Could not update resources. Error: " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * Update resources from a {@link CoursePart}
	 *
	 * @param coursePart {@link CoursePart} sub-tree
	 * @return {@link Boolean} <code>true</code> if xml changed, <code>false</code> otherwise
	 * @throws MooshakException
	 */
	private boolean updateResources(CoursePart coursePart) throws MooshakException {

		boolean xmlChanged = false;

		for (CourseResource resource : coursePart.getResources()) {

			ResourceType type = resource.getType();
			if (type != ResourceType.PROBLEM)
				continue;

			if (resource.getId() == null)
				continue;

			String problemId = resource.getId();
			String problemName = "";
			if (resource.getTitle() != null)
				problemName = resource.getTitle();

			String url = resource.getHref();

			if (url == null || url.startsWith("http://" + getIdName()))
				continue;

			if (!Pattern.matches(AdministratorManager.REGEX_URL, url))
				continue;
			else if (!url.endsWith(".zip"))
				continue;

			Problems problemsPO = open("problems");
			Problem problem = problemsPO.find(problemId);

			if (problem == null) {
				AdministratorManager.getInstance()
					.importMooshakObject(problemsPO.getPath().toString(), problemName,
							readRemoteURL(url));
				problem = problemsPO.find(problemId);
			}
			else {
				/*URLConnection connection = new URL(url)
					.openConnection();
				long lastModified = connection.getLastModified();
				Date date = new Date(lastModified);

				if (date.after(new Date(new File(getAbsoluteFile().toString() + File.separator
						+ "problems" + File.separator + problemId).lastModified()))) {*/
					AdministratorManager.getInstance().destroyMooshakObject(problem.getPath()
							.toString());
					AdministratorManager.getInstance()
						.importMooshakObject(problemsPO.getPath().toString(), problemId,
								readRemoteURL(url));
					problem = problemsPO.find(problemId);
				/*}*/

			}

			xmlChanged = true;
			resource.setId("http://" + getIdName() + "/" + problem.getIdName());
			resource.setHref("http://" + getIdName() + "/" + problem.getIdName());

			problem.setName(problemName);
			problem.setOriginalLocation(url);

		}

		for (CoursePart childPart : coursePart.getChildren()) {

			xmlChanged = xmlChanged || updateResources(childPart);
		}

		return xmlChanged;
	}

	/**
	 * Extract id from URL string
	 *
	 * @param url Url string to extract id from
	 * @return id
	 */
	private String extractIdFromURL(String url) {

		String file = url.substring(url.lastIndexOf("/") + 1, url.length());

		return file.substring(0, file.lastIndexOf("."));
	}

	/**
	 * Reads a file from a remote URL to a byte array
	 *
	 * @param href
	 * @throws MooshakException
	 */
	private byte[] readRemoteURL(String href) throws MooshakException {
		byte[] data = null;
		try {
			URL url = new URL(href);
			InputStream is = url.openStream();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			byte[] buf = new byte[4096];
			int n;
			while ((n = is.read(buf)) >= 0)
				os.write(buf, 0, n);
			os.close();
			is.close();
			data = os.toByteArray();
		} catch (MalformedURLException e) {
			throw new MooshakException("Could not read resource");
		} catch (IOException e) {
			throw new MooshakException("Could not read resource");
		}

		return data;
	}

	@Override
	public void setFrozen(boolean frozen) throws MooshakException {
		super.setFrozen(frozen);

		EventSender.getEventSender().send(new ContextChangedEvent());
		broadcastContestStatusChange();
	}

	private void checkValues() {
		List<String> names = Arrays.asList("open", "start", "stop", "close");
		List<Date> dates = Arrays.asList(open, start, stop, close);
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < dates.size(); i++)
			for (int j = i + 1; j < dates.size(); j++) {
				Date first = dates.get(i);
				Date then = dates.get(j);

				if (first == null || then == null)
					continue;

				if (first.after(then)) {
					if (builder.length() > 0)
						builder.append("; ");
					builder.append(names.get(i));
					builder.append(" date cannot be after ");
					builder.append(names.get(j));
				}
			}
		if (builder.length() > 0)
			builder.append('.');
		fatal = builder.toString();
	}

	/**
	 * Computes the contest status based on contest dates
	 *
	 * @return
	 */
	private ContestStatus computeStatus() {

		Date now = new Date();

		ContestStatus status = ContestStatus.CREATED;

		if (isVirtual())
			return ContestStatus.RUNNING_VIRTUALLY;

		if (open != null && open.before(now))
			status = ContestStatus.READY;
		if (start != null && start.before(now))
			status = ContestStatus.RUNNING;
		if (stop != null && stop.before(now))
			status = ContestStatus.FINISHED;
		if (close != null && close.before(now))
			status = ContestStatus.CONCLUDED;

		return status;

	}

	/**
	 * Set timers to automatically change the contest status
	 */
	void updateStatus() {

		if (isVirtual()) {
			updateStatusImmediately(ContestStatus.RUNNING_VIRTUALLY);
			return;
		}

		updateStatusImmediately(ContestStatus.CREATED);
		updateStatusTimers(ContestStatus.READY, getOpen());
		updateStatusTimers(ContestStatus.RUNNING, getStart());
		updateStatusTimers(ContestStatus.FINISHED, getStop());
		updateStatusTimers(ContestStatus.CONCLUDED, getClose());
	}

	Map<ContestStatus, Timer> updateStatusTimers = new HashMap<>();

	private void updateStatusTimers(final ContestStatus status, Date date) {

		if (updateStatusTimers.containsKey(status))
			updateStatusTimers.get(status).cancel();

		Date now = new Date();

		if (date == null)
			return;

		if (date.before(now)) {
			updateStatusImmediately(status);
			return;
		}

		Timer timer = new Timer("update "+status+" timer in "+getDesignation());
		long delay = date.getTime() - now.getTime();

		// verify time overflow
		if (now.getTime() + delay >= Long.MAX_VALUE)
			return;

		updateStatusTimers.put(status, timer);

		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				updateStatusImmediately(status);
			}

		}, delay + status.ordinal()*2); // add slack so that greater status overwrite lower

	}

	private Timer immediateUpdateTimer = null;

	private synchronized void updateStatusImmediately(final ContestStatus status) {

		if (immediateUpdateTimer != null)
			immediateUpdateTimer.cancel();

		immediateUpdateTimer = new Timer();

		immediateUpdateTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				setContestStatus(status);
				sendProblemsToListeners();
				broadcastContestStatusChange();
				if (immediateUpdateTimer == null)
					immediateUpdateTimer.cancel();
				immediateUpdateTimer = null;
			}
		}, 500);
	}

	/**
	 * When contest status is running, sends the problems in the contest to all
	 * listeners
	 */
	public void sendProblemsToListeners() {

		if (getContestStatus().equals(ContestStatus.RUNNING)) {
			try {
				Problems problems = open("problems");
				try(POStream<Problem> stream = problems.newPOStream()) {
					for (Problem problem : stream)
						problem.broadcastProblemChanges();
				}
			} catch (Exception e) {
				LOGGER.severe("Could not update problems in contest " + getIdName() + ": " + e.getMessage());
			}
		}

	}

	/**
	 * Informs all listeners that this contest changed state
	 */
	public void broadcastContestStatusChange() {
		ContestUpdateEvent event = new ContestUpdateEvent();
		event.setContest(getDesignation());
		event.setContestId(getIdName());
		event.setState(getContestStatus());
		event.setFrozen(isFrozen());
		EventSender.getEventSender().send(event);
	}

	/**
	 * Get time since start of contest; it may depend on team if this is a
	 * virtual contest
	 *
	 * @param teamId
	 * @return
	 * @throws MooshakContentException
	 */
	public Date transactionTime(String teamId) throws MooshakContentException {

		if (teamId == null)
			return new Date(0);

		Team team = getTeam(teamId);

		if (team == null) {
			if (getStart() != null)
				return new Date(new Date().getTime() - getStart().getTime());
			else
				return new Date(0);
		}

		if (isVirtual())
			return new Date(team.getSecondsFromLogin() * 1000L);
		else
			return new Date(new Date().getTime() - getStart().getTime());
	}
}
