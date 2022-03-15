package pt.up.fc.dcc.mooshak.server.commands;

import pt.up.fc.dcc.mooshak.client.services.SessionCommandService;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Session Management Service
 * 
 * @author josepaiva
 */
public class SessionCommandServiceImpl extends CommandService
	implements SessionCommandService {
	
	private static final long serialVersionUID = 1L;

	private int timeout;
	 
	@Override
	public Integer getUserSessionTimeout() throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID, RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID);
		
		if ((getThreadLocalRequest().getRequestedSessionId() != null
		        && !getThreadLocalRequest().isRequestedSessionIdValid()) ||
				getThreadLocalRequest().getRequestedSessionId() == null)
			throw new MooshakException("No session available!");
		
		if (session == null)
			throw new MooshakException("No session available!");
	 
		timeout = getThreadLocalRequest().getSession()
				.getMaxInactiveInterval() * 1000;
		return timeout;
	}
	 
	@Override
	public Boolean isSessionAlive() throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID, RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID);
	 
		return new Boolean((System.currentTimeMillis() - getThreadLocalRequest()
				.getSession().getLastAccessedTime()) < timeout);
	}
	 
	@Override
	public void ping() throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID, 
				CREATOR_PROFILE_ID, JUDGE_PROFILE_ID, RUNNER_PROFILE_ID,
				TEAM_PROFILE_ID);
	 
	}

}
