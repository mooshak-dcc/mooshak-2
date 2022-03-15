package pt.up.fc.dcc.mooshak.server.commands;


import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.servlet.ServletException;

import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.managers.AsuraManager;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.managers.EnkiManager;
import pt.up.fc.dcc.mooshak.managers.KoraManager;
import pt.up.fc.dcc.mooshak.managers.ParticipantManager;
import pt.up.fc.dcc.mooshak.managers.QuizManager;
import pt.up.fc.dcc.mooshak.managers.ReviewerManager;
import pt.up.fc.dcc.mooshak.managers.RowManager;
import pt.up.fc.dcc.mooshak.server.MooshakServiceServlet;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Abstract class common to all servlets implementing service commands.
 * Reads global configurations, initializes managers and handles 
 * sessions using HTTP sessions
 */
public abstract class CommandService extends MooshakServiceServlet {
	private static final long serialVersionUID = 1L;

	protected final static Logger LOGGER = Logger.getLogger("");
	
	protected static final String ADMIN_PROFILE_ID = "admin";
	protected static final String CREATOR_PROFILE_ID = "creator";
	protected static final String JUDGE_PROFILE_ID = "judge";
	protected static final String RUNNER_PROFILE_ID = "runner";
	protected static final String KORA_PROFILE_ID = "kora";
	protected static final String TEAM_PROFILE_ID = "team";
	protected static final String PUBLIC_PROFILE_ID = "public";
	
	protected AuthManager authManager = null;
	protected AdministratorManager administratorManager = null;
	protected ParticipantManager participantManager = null;
	protected ReviewerManager reviewerManager = null;
	protected EnkiManager enkiManager = null;
	protected KoraManager koraManager = null;
	protected QuizManager quizManager = null;
	protected RowManager rowManager = null;
	protected AsuraManager asuraManager = null;
	
	/**
	 * Initialize this servlet using configurations
	 */
	@Override
	public void init() throws ServletException{
		
		authManager = AuthManager.getInstance();
		administratorManager = AdministratorManager.getInstance();
		participantManager = ParticipantManager.getInstance();
		reviewerManager = ReviewerManager.getInstance();
		enkiManager = EnkiManager.getInstance();
		koraManager = KoraManager.getInstance();
		quizManager = QuizManager.getInstance();
		rowManager = RowManager.getInstance();
		asuraManager = AsuraManager.getInstance();
	}

	/**
	 * Checks if this session has required authentication
	 * @param profile
	 * @throws MooshakException if session doesn't have authentication
	 */
	protected void authorized(String... profile) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session,profile);
	}
	
	/**
	 * Get a Mooshak session recorded in the HTTP Session 
	 * @return
	 */
	protected Session getSession() {

		return (Session) 
				getThreadLocalRequest().getSession().getAttribute("session");
	}
	
	
	final static Pattern invalidCharsInPathSegment = Pattern.compile("/|\\.\\.");
	
	/**
	 * Remove all characters in argument that may be exploited when this
	 * string is used as a path segment such as slashes ("/") or
	 * double dots  ("..")
	 * 
	 * @param pathSegment
	 * @return	sanitized path segment
	 */
	public static String sanitizePathSegment(String pathSegment) {
		
		return invalidCharsInPathSegment.matcher(pathSegment).replaceAll("");
	}
	
	
	final static Pattern invalidCharsInPath = Pattern.compile("^(../)*/?");
	
	/**
	 * Remove all characters in argument that may be exploited when this
	 * is used as a relative path (id of a persistent object) such as
	 * series of leading double dots and slashes.
	 * 
	 * @param path	string including slashes, dots and double-dots
	 * @return string without leading double-dots and slashes
	 */
	public static String sanitizePathId(String path) {
		return invalidCharsInPath.matcher(path).replaceAll("");
	}
	
	
}
