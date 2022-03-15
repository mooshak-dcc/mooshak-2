package pt.up.fc.dcc.mooshak.server.commands;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.managers.ParticipantManager;

/**
 * Extension of team command servlet for testing purposes.
 * The <code>init()</code> is redefined and  <code>ServletConfig</code> is
 * ignored since GWTTestase doesn'te read <code>web.xml</code>; servlet
 * parameters host and home are hard-wired for tests. 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 *
 */
public class TeamCommandServiceImpl4Tests extends ParticipantCommandServiceImpl {
	private static final long serialVersionUID = 1L;

	public void init(ServletConfig servletConfig) throws ServletException{
				
		authManager = AuthManager.getInstance();
		participantManager = ParticipantManager.getInstance();

	}	
	
}
