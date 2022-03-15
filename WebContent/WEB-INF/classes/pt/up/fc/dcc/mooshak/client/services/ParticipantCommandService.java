package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("participantCommand")
public interface ParticipantCommandService extends RemoteService {

	/**
	 * Return a list of available problems
	 * 
	 * @return
	 * @throws MooshakException
	 */
	public List<SelectableOption> getProblems() throws MooshakException;

	/**
	 * View problem description in current contest
	 * 
	 * @param problem
	 *            id
	 * @param complete
	 *            description including descriptions
	 * @return
	 */
	ProblemInfo view(String problem, boolean description) throws MooshakException;
	
	/**
	 * Submit a program for evaluation
	 * 
	 * @param programName
	 * @param programCode
	 * @param problemId
	 * @param inputs
	 *            test data or code (in games)
	 * @param consider
	 *            submission counts for classification?
	 * @return
	 */
	void evaluate(String programName, byte[] programCode,
				String contestId, List<String> inputs,boolean consider)
					 throws MooshakException;
	
	
	/**
	 * Ask a question on a problem
	 * 
	 * @param contest
	 * @param team
	 * @param problem
	 * @param question
	 * @param subject
	 * @throws MooshakException
	 */
	void ask(String problem, String subject, String question) throws MooshakException;

	/**
	 * Submit a printout
	 * 
	 * @param problem
	 * @param content
	 * @param fileName
	 * @throws MooshakException
	 */
	void print(String problem, String content, String fileName) 
			throws MooshakException;
	

	/**
	 * Get an evaluation report summary given an ID
	 * 
	 * @param id
	 *            of submission
	 * @param consider
	 *            true if standard submission; false if validation
	 * @return
	 */
	EvaluationSummary getEvaluationSummary(String submission,boolean consider)
			 throws MooshakException;

	/**
	 * Get an object associated with a question with given id
	 * 
	 * @param id
	 * @return
	 * @throws MooshakException
	 */
	MooshakObject getAnsweredQuestion(String id) throws MooshakException;
	
	/**
	 * Gets a transaction quota of the user in the given type
	 * 
	 * @param type
	 * @return
	 * @throws MooshakException
	 */
	TransactionQuota getTransactionsData(String type) throws MooshakException;

	/**
	 * Get skeleton for problemId with extension extension
	 * 
	 * @param problemId
	 * @param extension
	 * @return
	 * @throws MooshakException
	 */
	String getProgramSkeleton(String problemId, String extension) throws MooshakException;

	/**
	 * Get participant session
	 * 
	 * @return
	 * @throws MooshakException
	 */
	String getParticipantLogged() throws MooshakException;

	/**
	 * Get Show Own code
	 * 
	 * @return
	 * @throws MooshakException
	 */
	boolean getShowOwnCode() throws MooshakException;

	/**
	 * Get the submission content
	 * 
	 * @param id
	 * @param team
	 * @return
	 * @throws MooshakException
	 */
	MooshakValue getSubmissionContent(String id, String team)	throws MooshakException;

	/**
	 * Get the languages available in a contest
	 * 
	 * @return the languages available in a contest
	 * @throws MooshakException
	 */
	Map<String, String> getAvailableLanguages() throws MooshakException;

	/**
	 * Get the kind of editor to show
	 * 
	 * @param problemId
	 * @return the kind of editor to show
	 * @throws MooshakException
	 */
	public EditorKind getEditorKind(String problemId) throws MooshakException;

	String getSubmissionContentsWithoutId(String teamId, String problemId, long evalTime, String programName)
			throws MooshakException;

	/**
	 * Check if files uploaded by participants must be editable
	 * 
	 * @param extension Extension of the file
	 * @return <code>false</code> only if explicitly stated; 
	 * 		<code>true</code> otherwise 
	 * @throws MooshakException
	 */
	boolean isEditableContents(String extension) throws MooshakException;
	

	/**
	 * Return a list of possible opponents, including those provided by the 
	 * game author, and previous submissions 
	 * 
	 * @param problemId
	 * @return opponents
	 * @throws MooshakException
	 */
	Map<String, String> getOpponents(String problemId) throws MooshakException;

}
