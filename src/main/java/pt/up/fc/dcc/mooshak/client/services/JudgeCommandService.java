package pt.up.fc.dcc.mooshak.client.services;


import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("judgeCommand")
public interface JudgeCommandService extends RemoteService {

	/**
	 * Gets the program code in the path
	 * 
	 * @param id
	 * @return
	 * @throws MooshakException
	 */
	byte[] getProgramCode(String id) throws MooshakException;

	/**
	 * Reevaluates a program
	 * 
	 * @param programName
	 * @param programCode
	 * @param problemId
	 * @return
	 */
	void reevaluate(String programName, byte[] programCode, String problemId,
			String submissionId) throws MooshakException;

	/**
	 * Comment a problem
	 * 
	 * @param problemId
	 * @param subject
	 * @param comment
	 * @throws MooshakException
	 */
	void comment(String problemId, String subject, String comment)
			throws MooshakException;

	/**
	 * Return all the xml report associated to a submission identified
	 * by id
	 * 
	 * @param id
	 * @return
	 * @throws MooshakException
	 */
	Map<String, String> getSubmissionReports(String id) throws MooshakException;
	
	/**
	 * Returns the server file system separator
	 * @return
	 * @throws MooshakException
	 */
	String getFileSeparator() throws MooshakException;
	
	/**
	 * Broadcast a change in a row to all clients
	 * 
	 * @param type
	 * @param id
	 * @throws MooshakException
	 */
	void broadcastRowChange(String type, String id) 
			throws MooshakException;
	
	/**
	 * Creates a balloon for the given submission
	 * @param objectId
	 * @throws MooshakException
	 */
	void createBalloon(String objectId)
			throws MooshakException;

	/**
	 * Sends an alert to a team with message
	 * @param objectId
	 * @param message
	 * @throws MooshakException 
	 */
	void sendAlertNotificationEvent(String objectId, String message) 
			throws MooshakException;

	/**
	 * Gets a list of questions' subjects related to problem of questionId
	 * @param questionId
	 * @return
	 * @throws MooshakException
	 */
	List<SelectableOption> getQuestionsSubjectList(String questionId)
			throws MooshakException;

	/**
	 * Get problem id associated to a submissionId
	 * @param submissionId
	 * @return
	 * @throws MooshakException
	 */
	String getProblemId(String submissionId) throws MooshakException;
	
	/**
	 * Updates the result of a submission
	 * @param submissionId
	 * @param oldResult
	 * @param newResult
	 * @throws MooshakException
	 */
	void updateSubmissionResult(String submissionId, String oldResult,
			String newResult) throws MooshakException;

	/**
	 * Is printouts list-pending?
	 * @return
	 * @throws MooshakException
	 */
	boolean isPrintoutsListPending() throws MooshakException;

	/**
	 * Is balloons list-pending?
	 * @return
	 * @throws MooshakException
	 */
	boolean isBalloonsListPending() throws MooshakException;
	
	/**
	 * Get obtained output from submission
	 * 
	 * @return the list of obtained output
	 * @throws MooshakException
	 */
	List<String> getObtainedOutputs(String submissionId) throws MooshakException;
	
	/**
	 * Get expected output from submission
	 * 
	 * @return the list of expected output
	 * @throws MooshakException
	 */
	List<String> getExpectedOutputs(String submissionId) throws MooshakException;

	/**
	 * Gets the problem test cases
	 * @param problemId
	 * @return problem test cases
	 */
	Map<String, String> getProblemTestCases(String problemId) throws MooshakException;

	/**
	 * Validate submission 
	 * @param submissionId
	 * @param programCode
	 * @param problemId
	 * @param inputs 
	 * @throws MooshakException 
	 */
	void validateSubmission(String submissionId, byte[] programCode, String problemId,
			List<String> inputs) throws MooshakException;

	EvaluationSummary getValidationSummary(String submissionId) throws MooshakException;
}
