package pt.up.fc.dcc.mooshak.server.commands;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contest.Gui;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;

/**
 * Basic Commands Service Implementation
 * 
 * @author josepaiva
 */
public class BasicCommandServiceImpl extends CommandService implements
		BasicCommandService {

	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#context()
	 */
	@Override
	public ContextInfo context() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID, RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID, KORA_PROFILE_ID, PUBLIC_PROFILE_ID);

		Authenticable participant = session.getParticipant();
		Contest contest = session.getContest();

		ContextInfo info = new ContextInfo();
		info.setactivityId(session.getContest().getGui().equals(Gui.ENKI) ?
				enkiManager.getCourseId(session) : session.getContestId());
		info.setactivityName(contest.getDesignation());
		switch(contest.getContestStatus()) {
		case RUNNING_VIRTUALLY:
			Date start;
			Date stop;
			
			if(participant instanceof Team) {
				start = ((Team) participant).getStart();
			} else {
				start = new Date();
			}
			stop = new Date(
					start.getTime() + (
							contest.getStop().getTime() - 
							contest.getStart().getTime())
							);
			
			info.setStart(start);
			info.setEnd(stop);
			
			break;
		default:
			info.setStart(contest.getStart());
			info.setEnd(contest.getStop());
			break;
		}
		info.setParticipantId(participant.getIdName());
		info.setParticipantName(participant.getName());
		info.setVersion("2.1.0.1");

		auditLog("context");
		return info;
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#getContestId()
	 */
	@Override
	public String getContestId() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID,RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID, KORA_PROFILE_ID, PUBLIC_PROFILE_ID);
		
		auditLog("getContestId");
		return session.getContest().getPath().toString();
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#getDomains(boolean, boolean)
	 */
	@Override
	public Map<String, ResultsContestInfo> getDomains(boolean listCreated, 
			boolean listConcluded)	throws MooshakException {
		
		List<ResultsContestInfo> results = authManager.getDomains(listCreated, 
				listConcluded);
		
		Map<String, ResultsContestInfo> resultsMap = 
				new HashMap<String, ResultsContestInfo>();
		
		for (ResultsContestInfo resultsContestInfo : results) {
			resultsMap.put(resultsContestInfo.getContestId(), resultsContestInfo);
		}
		
		auditLog("getDomains", Boolean.toString(listCreated), Boolean.toString(listConcluded));
		return resultsMap;
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#initSession()
	 */
	@Override
	public void initSession() throws MooshakException {
		Session session = getSession();
		
		if(session != null)
			return;
		
		session = AuthManager.getInstance()
			.authenticate(null, PUBLIC_PROFILE_ID, "");
	
		HttpSession httpSession = getThreadLocalRequest().getSession();
		httpSession.setAttribute("session",session);
		
		auditLog("initSession");
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#login(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String login(String domain, String user, String password)
			throws MooshakException {
		
		Session session = authManager.authenticate(
				domain != null ? sanitizePathSegment(domain) : null,
				sanitizePathSegment(user),
				password);
		
		HttpSession httpSession = getThreadLocalRequest().getSession();
		httpSession.setAttribute("session",session);
		
		justAuditLog("login",domain,user);	     // don't log passwords !
		scriptLog("login",domain,user,password); // unless for a script
		
		return session.getEntryPoint();
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#register(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String register(String domain, String user, String password)
			throws MooshakException {
		
		authManager.register(sanitizePathSegment(domain), 
				sanitizePathSegment(user), password, null);
		
		String message = login(domain, user, password); 
		auditLog("register",domain,user); // don't log  password !
		return message;
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#logout()
	 */
	@Override
	public void logout() throws MooshakException {
		
		HttpSession httpSession = getThreadLocalRequest().getSession();
		httpSession.removeAttribute("session");
		httpSession.invalidate();
		// Mooshak session will be deleted when notification is received
		auditLog("logout");
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#isSessionAlive()
	 */
	@Override
	public boolean isSessionAlive() throws MooshakException {
		
		Session session = (Session) getThreadLocalRequest().getSession()
				.getAttribute("session");
		
		boolean alive = true;
		
		if (getThreadLocalRequest().getRequestedSessionId() != null
		        && !getThreadLocalRequest().isRequestedSessionIdValid()) {
		    alive = false;
		}
		else {
			alive = session != null
					&& !session.getProfile().getIdName().equalsIgnoreCase("public");	
		}
		
		auditLog("isSessionAlive");
		return alive;
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#getColumns(java.lang.String)
	 */
	@Override
	public List<ColumnInfo> getColumns(String kind) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID,RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID, PUBLIC_PROFILE_ID);

		List<ColumnInfo> infos = participantManager.getColumns(session, kind);
		
		auditLog("getColumns",kind);
		return infos;
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#refreshRows()
	 */
	@Override
	public void refreshRows() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID,RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID, PUBLIC_PROFILE_ID);

		rowManager.refreshRows(session);
		auditLog("refreshRows");
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.client.services.BasicCommandService#changeContest(java.lang.String)
	 */
	@Override
	public void changeContest(String contest) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID,RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID, PUBLIC_PROFILE_ID);

		reviewerManager.changeContest(session, contest);
		auditLog("changeContest",contest);
	}

	@Override
	public boolean validateCaptcha(String challenge) throws MooshakException{
		boolean valid = false;
		HttpSession httpSession = getThreadLocalRequest().getSession();
		String captcha = (String) httpSession.getAttribute("captcha");
				
		if(captcha == null)
			throw new MooshakException("No captcha found in this session");
		else
			valid = captcha.equals(challenge);
		
		auditLog("validateCaptcha",challenge);
		return valid;
	}

	@Override
	public boolean isLoggedInAsAdmin() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID, JUDGE_PROFILE_ID);
		
		auditLog("isLoggedInAsAdmin");
		return session.getParticipant().getIdName().equals("admin");
	}

	@Override
	public void switchProfileBackToAdmin() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID, JUDGE_PROFILE_ID);
		
		administratorManager.switchProfileBackToAdmin(session);
		
		auditLog("switchProfileBackToAdmin");
	}

	@Override
	public String getPreferredLocale() throws MooshakException {
		
		String prefered = getThreadLocalRequest().getHeader("Accept-Language");
		
		String locale = authManager.matchPreferredLanguage(prefered);
		
		auditLog("getPreferredLocales");
		
		return locale;
	}
	
	@Override
	public String getPreferredLocale(List<String> locales)
			throws MooshakException {
		
		String prefered = getThreadLocalRequest().getHeader("Accept-Language");
		
		String locale = authManager.matchPreferredLanguage(prefered,locales);
		
		auditLog("getPreferredLocales");
		
		return locale;
	}

	@Override
	public String getVersion() {
		
		ServletContext context = getServletContext();
		
		Properties properties = (Properties) context.getAttribute("properties");
		
		if(properties == null)
			return "<properties not found>";
		else 
			return properties.getProperty("mooshakVersion","<version not found>");
	}
	
	@Override
	public String getProblemNameById(String problemId) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID, RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID, PUBLIC_PROFILE_ID);
		
		String name = participantManager.getProblemNameById(session, problemId);
		
		auditLog("getProblemNameById", problemId);
		
		return name;
	}
	
	@Override
	public String diffStrings(String expected, String obtained)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID, RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID, PUBLIC_PROFILE_ID);
		
		String diff = administratorManager.diffStrings(obtained, expected);
		
		auditLog("diffStrings", expected, obtained);
		
		return diff;
	}

	@Override
	public String getScriptFile(String path) throws MooshakException {

		String script = administratorManager.getScriptFile(path);
		
		auditLog("getScriptFile", path);
		
		return script;
	}
	
	@Override
	public List<List<String>> getTaskList(String script) throws MooshakException {
		List<List<String>> result = administratorManager.getTaskList(script);
		
		auditLog("getTaskList", script);
		
		return result;
	}

}
