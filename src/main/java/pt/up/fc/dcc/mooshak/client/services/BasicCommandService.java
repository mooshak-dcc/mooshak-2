package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * Synchronous service interface for basic service commands that
 * does not require authentication
 * 
 * @author josepaiva
 */
@RemoteServiceRelativePath("basicCommand")
public interface BasicCommandService extends RemoteService {

	/**
	 * Get context information on this activity
	 * @return
	 * @throws MooshakException
	 */
	ContextInfo context() throws MooshakException;
	
	/**
	 * Returns the current contest id path
	 * @return
	 * @throws MooshakException
	 */
	String getContestId() throws MooshakException;

	/**
	 * Return a list of available domains
	 * 
	 * @param listCreated
	 * @param listConcluded
	 * @return
	 * @throws MooshakException
	 */
	public Map<String, ResultsContestInfo> getDomains(boolean listCreated, 
			boolean listConcluded) throws MooshakException;

	/**
	 * Initializes a public session
	 * 
	 * @throws MooshakException
	 */
	void initSession() throws MooshakException;
	
	/**
	 * Authenticate in a given domain (say a contest or a test)
	 * and start a session
	 * @param domain
	 * @param user
	 * @param password
	 * @return url of the continuation screen
	 * @throws MooshakException
	 */
	public String login(String domain, String user, String password) 
			throws MooshakException;
	
	/**
	 * Registers in a given domain (say a contest or a test)
	 * and start a session
	 * @param domain
	 * @param user
	 * @param password
	 * @return url of the continuation screen
	 * @throws MooshakException
	 */
	public String register(String domain, String user, String password) 
			throws MooshakException;
	
	/**
	 * Logout from current session
	 * @throws MooshakException
	 */
	public void logout() throws MooshakException;

	/**
	 * Return name of columns for a given kind of listing 
	 * (e.g. "submission", "rankings")
	 * @param Kind
	 * @return
	 * @throws MooshakException
	 */
	List<ColumnInfo> getColumns(String Kind) throws MooshakException;
	
	/**
	 * Send all events missed since the activity started
	 * @throws MooshakException
	 */
	void refreshRows()  throws MooshakException;

	/**
	 * Changes the session contest to the given contest
	 * 
	 * @param contest
	 * @throws MooshakException
	 */
	void changeContest(String contest) throws MooshakException;

	/**
	 * Check this string with challenge shown as image
	 * @param challenge
	 * @throws MooshakException
	 */
	boolean validateCaptcha(String challenge)throws MooshakException;

	/**
	 * Check if this session is validated
	 * @throws MooshakException
	 */
	boolean isSessionAlive() throws MooshakException;

	/**
	 * Check if this session belongs to an Admin
	 * @throws MooshakException
	 */
	boolean isLoggedInAsAdmin() throws MooshakException;


	/**
	 * Switch profile back to admin
	 * @throws MooshakException
	 */
	void switchProfileBackToAdmin() throws MooshakException;
	
	/**
	 * Get preferred locale from a default list
	 * @param locales
	 * @return
	 * @throws MooshakException
	 */
	String getPreferredLocale() throws MooshakException;
	
	/**
	 * Get preferred locale from this list
	 * @param locales
	 * @return
	 * @throws MooshakException
	 */
	String getPreferredLocale(List<String> locales) throws MooshakException;
	
	/**
	 * Get version of Mooshak
	 * @return
	 */
	String getVersion();

	/**
	 * Get the problem name by its id
	 * @param problemId
	 * @return
	 * @throws MooshakException
	 */
	String getProblemNameById(String problemId) throws MooshakException;

	/**
	 * Get the diff between two strings
	 * @param expected
	 * @param obtained
	 * @return the diff between two strings
	 * @throws MooshakException 
	 */
	String diffStrings(String expected, String obtained) throws MooshakException;
	
	/**
	 * Get the script file to replay
	 * @param path
	 * @return
	 * @throws MooshakException
	 */
	String getScriptFile(String path) throws MooshakException;

	List<List<String>> getTaskList(String script) throws MooshakException;
}
