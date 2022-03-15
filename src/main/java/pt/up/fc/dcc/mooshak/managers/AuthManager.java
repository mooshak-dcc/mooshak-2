package pt.up.fc.dcc.mooshak.managers;

import java.awt.Color;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Configs;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contest.Evaluator;
import pt.up.fc.dcc.mooshak.content.types.Contest.Gui;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.content.types.Group;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.Profile;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Sessions;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.content.types.Users;
import pt.up.fc.dcc.mooshak.content.util.RandomStringGenerator;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;

/**
 * Manages all requests related to participants (teams or students).
 * This class ignores types from any particular communication layer,
 * such as GWT RPC or Jersey
 * 
 * @author José paulo Leal <code>zp@dcc.fc.up.pt</code>
 *
 */
public class AuthManager extends Manager {
	 
	private static final String USERNAME_PATTERN = "^[A-Za-z0-9_-]{1,15}$";	
	
	private Configs configs;
	private Contests contests;
	private Sessions sessions;
	
	volatile private static AuthManager manager = null;
	
	/**
	 * Get single instance of this class for testing purposes
	 * @return
	 */
	public static AuthManager getInstance() {
		if(manager==null)
			manager = new AuthManager();
		return manager;
	}	
	
	// remove expired sessions on a regular basis
	private final ScheduledExecutorService scheduler =	
			Executors.newScheduledThreadPool(1);
	private final static int DELAY_MINUTES = 15; 
	
	
	private AuthManager() {
		
		try {
			configs  = PersistentObject.openPath("data/configs");
		} catch (MooshakContentException cause) {
			LOGGER.log(Level.SEVERE,"openning configs folder",cause);
		}
		
		try {
			contests = PersistentObject.openPath("data/contests");
		} catch (MooshakContentException cause) {
			LOGGER.log(Level.SEVERE,"openning contests folder",cause);
		}

		try {
			sessions = PersistentObject.openPath("data/configs/sessions");
		} catch (MooshakContentException cause) {
			LOGGER.log(Level.SEVERE,"openning sessions folder",cause);
		}
		
		scheduler.scheduleAtFixedRate(() -> 
			{
				try {
					if(isHostLoadAcceptable()) {
						info("Starting session cleanup");
						sessions.removeExpiredSessions();
						info("Ending session cleanup");
					} else {
						warning("Postponing session cleanup due to high load"+
								OS.getSystemLoadAverage());
					}
				} catch(MooshakContentException cause) {
					severe("Error while removing expired sessions",cause);
				}
			}, DELAY_MINUTES, DELAY_MINUTES, TimeUnit.MINUTES);
		
		scheduler.submit(() -> {
			try {
				sessions.removeExpiredSessions();
			} catch (MooshakContentException cause) {
				severe("Error while removing expired sessions during initial cleanup", cause);
			}
		});
	}
	
	/**
	 * Stop session cleanup immediately
	 */
	public void stopScheduler() {
		scheduler.shutdownNow();
	}
	
	
	/**
	 * Authenticate a user in a given domain
	 * @param domain
	 * @param userId
	 * @param password
	 * @return
	 * @throws MooshakContentException 
	 * @throws MooshakException
	 */
	public Session authenticate(String domain, String userId, String password) 
			throws MooshakException {
		
		Authenticable person;
		
		if(
				(person = globalUser(userId)) == null &&
				(person = localUser(domain,userId)) == null &&
				(person = participant(domain,userId)) == null
		)
			throw new MooshakException("Unknown user");
		else if(! person.authentic(password))
			throw new MooshakException("Invalid password");

		Contest contest = null;
		Session session;
		Profile profile;
		
		try {
			if (domain != null)
				contest = contests.find(domain);
			session = sessions.newSession();
			profile = person.getProfile();
		} catch(MooshakContentException cause) {
			String message = "Error obtaining authentication data";
			severe(message,cause);
			throw new MooshakException(message,cause);
		}
		
		if (domain != null)
			session.setContest(contest);
		session.setParticipant(person);
		session.setProfile(profile);
		
		switch(profile.getIdName()) {
			case "team":
				if (contest == null)
					throw new MooshakException("No contest selected");
				
				if (contest.getGui().equals(Gui.ENKI)) {
					session.setEntryPoint("Enki");
					
					EnkiManager.getInstance().registerPlayerAtGS(session);
					EnkiManager.getInstance().registerStudentAtSequenciationService(session);
				}
				else
					session.setEntryPoint("ICPC");
				
				if(! ((PersistentObject) person).getParent().getIdName()
						.equals("users")) {
					Team team = (Team) person;
					team.recordLoginTime();
				}
				break;
			case "admin":
				session.setEntryPoint("Admin");
				break;
			case "judge":
				if (contest == null)
					throw new MooshakException("No contest selected");
				
				if (contest.getEvaluator().equals(Evaluator.TEACHER)) 
					session.setEntryPoint("Teacher");
				else
					session.setEntryPoint("Judge");
				break;
			case "creator":
				if (contest == null)
					throw new MooshakException("No contest selected");
				
				session.setEntryPoint("Creator");
				break;
			case "runner":
				if (contest == null)
					throw new MooshakException("No contest selected");
				
				session.setEntryPoint("Runner");
				break;
			case "kora":
				if (contest == null)
					throw new MooshakException("No contest selected");
				
				session.setEntryPoint("Kora");
				break;
			default:	
				session.setEntryPoint("Scoreboard");
		}

		try {
			session.save();
		} catch (MooshakContentException cause) {
			throw new MooshakException("Saving authenticated session",cause);
		}
		
		return session;
	}

	/**
	 * Authenticate an user coming from a LMS
	 * @param domain Contest in which user should be authenticated
	 * @param userId User to authenticate
	 * @return
	 * @throws MooshakException 
	 */
	public Session authenticateLtiUser(String domain, String userId, String name) 
			throws MooshakException {
		Authenticable person;
		
		if(
				(person = localUser(domain,userId)) == null &&
				(person = participant(domain,userId)) == null
		) {
			register(domain, userId, RandomStringGenerator.randomAlphanumeric(6),
					name);
			if(
					(person = localUser(domain,userId)) == null &&
					(person = participant(domain,userId)) == null
			)
				throw new MooshakException("Unknown user");
		}

		Contest contest;
		Session session;
		Profile profile;
		
		try {
			contest = contests.find(domain);
			session = sessions.newSession();
			profile = person.getProfile();
		} catch(MooshakContentException cause) {
			String message = "Error obtaining authentication data";
			severe(message,cause);
			throw new MooshakException(message,cause);
		}
			
		session.setContest(contest);
		session.setParticipant(person);
		session.setProfile(profile);
		
		switch(profile.getIdName()) {
			case "team":
				if (contest.getGui().equals(Gui.ENKI)) 
					session.setEntryPoint("Enki");
				else
					session.setEntryPoint("ICPC");
				
				if(! ((PersistentObject) person).getParent().getIdName()
						.equals("users")) {
					Team team = (Team) person;
					team.recordLoginTime();
				}
				break;
			default:	
				session.setEntryPoint("Guest");
		}

		try {
			session.save();
		} catch (MooshakContentException cause) {
			throw new MooshakException("Saving authenticated session",cause);
		}
		
		return session;
	}
	
	/**
	 * Check if a user can authenticate in a given domain with a certain profile
	 * @param domain
	 * @param userId
	 * @param profile
	 * @return {@link Boolean}
	 * @throws MooshakException
	 */
	public boolean canChangeContest(String domain, Authenticable user, String profile) throws MooshakException {
		PersistentObject userPO = (PersistentObject) user;
		
		if (userPO.getGrandParent() instanceof Configs)
			return true;
		else
			return false;
	}
	
	/**
	 * Checks if given session is available to one of the given profile name 
	 * @param session a Mooshak PersistentObject
	 * @param profileName as String
	 * @throws MooshakException
	 */
	public void autorize(Session session, String... profileNames) 
			throws MooshakException {
				
		if(session == null)
			throw new MooshakException("Session lost");
		
		session.setLastUsed(new Date());
		
		Profile profile;
		try {
			profile = session.getProfile();
		} catch (MooshakContentException cause) {
			throw new MooshakException("Obtaining profile",cause);
		}
	
		for(String profileName: profileNames)
			if(profile.getIdName().equals(profileName))
				return;
			
		throw new MooshakException("Invalid profile");
	}
	
	/**
	 * Searchers for a global user with given id 
	 * @param userId
	 * @return
	 * @throws MooshakException
	 */
	public Authenticable globalUser(String userId) throws MooshakException {
		Users users=null;
		
		try {
			users = configs.open("users");
		} catch (MooshakContentException cause) {
			error("Error reading global authentication data",cause);
		}
		return users.find(userId);
	}

	/**
	 * Searchers for a local user with given id 
	 * @param userId
	 * @return
	 * @throws MooshakException
	 */
	public Authenticable localUser(String domain, String userId)
			throws MooshakException {
		Users users = null;
		
		try {
			Contest contest = contests.open(domain);
			if(Files.exists(contest.getAbsoluteFile("users")))
				users = contest.open("users");
			
		} catch (MooshakContentException cause) {			
			error("Error reading local authentication data",cause);
		}
		
		if(users == null)
			return null;
		else
			return users.find(userId);
	}

	/**
	 * Searchers for a participant user with given id 
	 * @param userId
	 * @return
	 * @throws MooshakException
	 */
	public Authenticable participant(String domain, String userId)
			throws MooshakException {
		Groups groups = null;
		try {
			Contest contest = contests.open(domain);
			groups = contest.open("groups");
			 
		} catch (MooshakContentException cause) {
			error("Reading participants authentication data",cause);
		}
		return groups.find(userId);
	}

	/**
	 * Returns a map with available domains, as defined in contests.
	 * The map keys are domain identifiers and map values are their long names. 
	 * 
	 * @param listCreated
	 * @param listConcluded
	 * @return
	 * @throws MooshakException
	 */
	public List<ResultsContestInfo> getDomains(boolean listCreated, 
			boolean listConcluded) throws MooshakException {

		return contests.getDomains(listCreated, listConcluded);
	}	
	
	/**
	 * Registers a given userId in domain
	 * @param domain
	 * @param userId
	 * @param password
	 * @throws MooshakException
	 */
	public void register(String domain, String userId, String password, String name)
			throws MooshakException {
		
		if(!validateUsername(userId))
			throw new MooshakException("Invalid username (3 to 15 letters, digits and underscore)");
		
		if(!validatePassword(password))
			throw new MooshakException("Invalid password (4 to 20 charaters)");
		
		if(
				globalUser(userId) != null ||
				localUser(domain,userId) != null ||
				participant(domain,userId) != null
		)
			throw new MooshakException("This user already exists");
		
		Contest contest;
		Groups groups;
		try {
			contest = contests.find(domain);
			
			if(!contest.isRegister())
				throw new MooshakException("Contest not accepting"
						+ " registrations");
			
			groups = contest.open("groups");
		} catch (MooshakContentException cause) {
			String message = "Error obtaining authentication data";
			severe(message,cause);
			throw new MooshakException(message,cause);
		}
		
		try {
			Group group = null;
			for (PersistentObject po : groups.getChildren(false)) {
				if(po.getIdName().equals("Default")) {
					group = (Group) po;
					break;
				}
			}
			
			if(group == null) {
				group = groups.create("Default", Group.class);
				group.setAcronym("DFLT");
				group.setDesignation("Default");
				group.setColor(Color.BLACK);
				group.save();
			}
			
			Team team = group.create(userId, Team.class);
			team.setName(name == null ? userId : name);
			team.setPassword(password);
			team.save();
		} catch (MooshakContentException cause) {
			String message = "Error registering user";
			severe(message,cause);
			throw new MooshakException(message,cause);
		}
	}
	
	/**
	 * Verifies if a given user name is valid
	 * @param user
	 * @return
	 */
	private boolean validateUsername(String user) {
		return Pattern.matches(USERNAME_PATTERN, user);
	}
	
	/**
	 * Verifies if a given password is valid
	 * @param password
	 * @return
	 */
	private boolean validatePassword(String password) {
		return password.length() >= 4 && password.length() <= 20;
	}
	
	static private final List<String> AVAILABLE_LOCALES = 
			Arrays.asList("en","pt","es","arb");
	static private final int MAX_PREFERENCE = 100;
	static private final Pattern COMMA = Pattern.compile(",");
	static private final Pattern LANG_PREF = 
			Pattern.compile("([\\w-]+)(:?;q=([\\d.]+))?");
	
	/**
	 * Match HTTP accepted-language value against default list of locales 
	 * 
	 * @param prefered
	 * @param available
	 * @return
	 */
	public String matchPreferredLanguage(String prefered) {
		return matchPreferredLanguage(prefered,AVAILABLE_LOCALES);
	}		
			
	/**
	 * Match HTTP accepted-language value against given list of locales 
	 * 
	 * @param prefered
	 * @param available
	 * @return
	 */
	public String matchPreferredLanguage(
			String prefered, 
			List<String> available) {
		Map<String,Integer> langPreferences = new HashMap<>();
		
		for(String langPref: COMMA.split(prefered)) {
			Matcher langMatcher = LANG_PREF.matcher(langPref.trim());
			if(langMatcher.matches()) {
				String language = langMatcher.group(1).trim();
				int preference = MAX_PREFERENCE;
				
				if(langMatcher.group(2) != null) {
					try {
						float value = Float.parseFloat(langMatcher.group(2));
						preference = (int) (MAX_PREFERENCE * value);
					} catch(NumberFormatException cause) {}
				}
				
				langPreferences.put(language, preference);
			}
		}
		
		List<String> languages = new ArrayList<>(langPreferences.keySet());
		Collections.sort(languages, 
				(a,b) -> langPreferences.get(a) - langPreferences.get(b));
		
		
		for(String language: languages)
			for(String locale: available)
				if(language.startsWith(locale))
					return locale;
		
		return null;
	}
	
}
