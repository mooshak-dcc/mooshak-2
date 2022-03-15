package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("creatorCommand")
public interface CreatorCommandService extends RemoteService {

	Map<String, List<String>> getOptionsValues() throws MooshakException;

	/**
	 * Creates a new problem with default settings
	 * 
	 * @param objectId
	 * @return
	 * @throws MooshakException
	 */
	String createNewDefaultProblem(String objectId) throws MooshakException;

	/**
	 * Gets the problem images object's string path
	 * 
	 * @return
	 * @throws MooshakException
	 */
	String getImagesPath();

	/**
	 * Gets the problem tests object's string path
	 * 
	 * @return
	 * @throws MooshakException
	 */
	String getTestsPath();

	/**
	 * Gets the problem skeletons object's string path
	 * 
	 * @return
	 * @throws MooshakException
	 */
	String getSkeletonsPath();

	/**
	 * Gets the problem solutions object's string path
	 * 
	 * @return
	 * @throws MooshakException
	 */
	String getSolutionsPath();

	/**
	 * Gets the problem object's string path
	 * 
	 * @return
	 * @throws MooshakException
	 */
	String getProblemsPath() throws MooshakException;

	/**
	 * Uploads a file to the server
	 * 
	 * @param objectId
	 * @param content
	 * @param name
	 * @param field
	 * @return 
	 * @throws MooshakException
	 */
	MooshakValue uploadFile(String objectId, byte[] content, String name,
			String field) throws MooshakException;

	/**
	 * Deletes the problem identified by id
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	void deleteProblem(String id) throws MooshakException;
	
	/**
	 * Adds a new test to objectId
	 * 
	 * @param objectId
	 * @return
	 * @throws MooshakException
	 */
	MooshakObject addNewDefaultTest(String objectId)
			 throws MooshakException;
	
	/**
	 * Deletes the test with this id
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	void deleteTest(String id) throws MooshakException;
	
	/**
	 * Adds a new skeleton to objectId
	 * 
	 * @param objectId
	 * @return
	 * @throws MooshakException
	 */
	MooshakObject addNewDefaultSkeleton(String objectId)
			 throws MooshakException;
	
	/**
	 * Deletes the skeleton with this id
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	void deleteSkeleton(String id) throws MooshakException;
	
	/**
	 * Adds a new solution to objectId
	 * 
	 * @param objectId
	 * @return
	 * @throws MooshakException
	 */
	MooshakObject addNewDefaultSolution(String objectId)
			 throws MooshakException;
	
	/**
	 * Adds a new file to field in problemId
	 * @param field
	 * @param problemId
	 * @return the new Value
	 * @throws MooshakException
	 */
	MooshakValue addNewDefaultFile(String problemId, String field) 
			throws MooshakException;
	
	/**
	 * Runs tests against solution
	 * @param problemId
	 * @throws MooshakException
	 */
	void updateTestsResults(String problemId) 
			throws MooshakException;
	
	/**
	 * Changes the type of a program file
	 * @param problemId
	 * @param objectId
	 * @param value
	 * @param newType
	 * @return
	 * @throws MooshakException
	 */
	void changeProgramType(String problemId,String objectId, 
			MooshakValue value, String newType)	throws MooshakException;
	
	/**
	 * Remove file in object
	 * @param objectId
	 * @param name
	 * @return
	 * @throws MooshakException
	 */
	void removeFileFromObject(String problemId, String name)	
			throws MooshakException;
	
	/**
	 * Gets a MooshakValue with solutions
	 * @param problemId
	 * @return
	 * @throws MooshakException
	 */
	MooshakValue getSolutionsValue(String problemId)	
			throws MooshakException;

	/**
	 * Removes a solution from the problem
	 * @param problemId
	 * @param name
	 * @throws MooshakException
	 */
	void removeSolution(String problemId, String name) throws MooshakException;

	/**
	 * Removes a skeleton from the problem
	 * @param problemId
	 * @param name
	 * @throws MooshakException
	 */
	void removeSkeleton(String problemId, String name) throws MooshakException;

	/**
	 * Checks for errors in languages configuration
	 * @return errors found in languages configuration
	 * @throws MooshakException
	 */
	String checkLanguages() throws MooshakException;

	/**
	 * Remove file from problem
	 * @param problemId
	 * @param field
	 * @param name
	 * @throws MooshakException
	 */
	void removeFile(String problemId, String field, String name) throws MooshakException;

}
