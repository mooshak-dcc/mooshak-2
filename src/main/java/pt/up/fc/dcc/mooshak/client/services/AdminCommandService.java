package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;
import pt.up.fc.dcc.mooshak.shared.results.ServerStatus;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * 
 * Synchronous service interface for administration commands
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
@RemoteServiceRelativePath("adminCommand")
public interface AdminCommandService extends RemoteService {
	
	/**
	 * Request a type of Mooshak object, its fields/attributes and method/operation 
	 * @param type	name of requested type
	 * @return
	 * @throws MooshakException
	 */
	public MooshakType getMooshakType(String type) throws MooshakException;
	
	/**
	 * Get a persistent object given its id-path 
	 * @param id	path to persistent object
	 * @return
	 * @throws MooshakException
	 */
	public MooshakObject getMooshakObject(String id) throws MooshakException;
	
	/**
	 * Change a persistent object
	 * @param data
	 * @throws MooshakException
	 */
	public void setMooshakObject(MooshakObject data) throws MooshakException;
	
	/**
	 * Verifies if it is possible to recover this MooshakObject
	 * @param id
	 * @param redo
	 * @return
	 * @throws MooshakException
	 */
	public boolean canRecover(String id, boolean redo) throws MooshakException;
	
	/**
	 * Create a new descendant with given name on the container with given id 
	 * @param id	of container where new PO is created 
	 * @param name  of new PO to create
	 * @throws MooshakException
	 */
	public void createMooshakObject(String id,String name) throws MooshakException;
	
	/**
	 * Destroy persistent object with given id
	 * @param id
	 * @throws MooshakException
	 */
	public void destroyMooshakObject(String id) throws MooshakException;

	/**
	 * Rename a persistent object with given id
	 * @param id	of the PO to rename
	 * @param name  to give to PO
	 * @throws MooshakException
	 */
	public void renameMooshakObject(String id, String name) throws MooshakException;
	
	/**
	 * Paste persistent object with given copiedId in object with given id
	 * @param id
	 * @throws MooshakException
	 */
	public void pasteMooshakObject(String id, String copiedId) 
			throws MooshakException;
	
	/**
	 * Replaces this version of this MooshakObject by another one
	 * 
	 * @param id
	 * @param redo
	 * @return 
	 * @throws MooshakException
	 */
	public MooshakObject recover(String id, boolean redo) throws MooshakException;
	
	/**
	 * Freezes the object with given id
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	public void freeze(String id) throws MooshakException;
	
	/**
	 * Unfreezes the object with given id
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	public void unfreeze(String id) throws MooshakException;
	
	/**
	 * Verifies if the object with given id is frozen or not
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	public boolean isFrozen(String id) throws MooshakException;
	
	/**
	 * Execute a mooshak method/operation on the server
	 * @param id			path to object 
	 * @param method		method identification
	 * @param context 	    context with name/value pairs
	 * @return				the outcome of this execution
	 * @throws MooshakException
	 */
	public CommandOutcome execute(String id,
			MooshakMethod method,
			MethodContext context) throws MooshakException;
	
	/**
	 * Gets the mime type for the given extension
	 * @param extension
	 * @return
	 * @throws MooshakException
	 */
	public String getMimeType(String extension) 
			throws MooshakException;

	/**
	 * Imports a Mooshak formatted object from url to id
	 * @param id
	 * @param name
	 * @param content
	 * @throws MooshakException
	 */
	public void importMooshakObject(String id, String name, byte[] content) 
			throws MooshakException;

	/**
	 * Exports a Mooshak formatted object from id
	 * @param id
	 * @param url
	 * @return zip file encoded in base64
	 * @throws MooshakException
	 */
	public String exportMooshakObject(String id) throws MooshakException;

	/**
	 * Check if this PO is renameable
	 * @param id
	 * @return
	 * @throws MooshakException
	 */
	boolean isRenameable(String id) throws MooshakException;
	
	/**
	 * Switch to another profile
	 * @param contest
	 * @param profile
	 * @throws MooshakException
	 */
	public void switchProfile(String contest, String profile) throws MooshakException;
	
	/**
	 * Search for Mooshak Object with given term either in name or in content
	 * @param term
	 * @param nameNotContent
	 * @return
	 * @throws MooshakException
	 */
	public List<String> findMooshakObjectIds(
			String term,boolean nameNotContent) throws MooshakException;
	
	/**
	 * Get current status of the server
	 * @return
	 */
	public ServerStatus getServerStatus() throws MooshakException;

	/**
	 * Remove file from object
	 * @param objectId
	 * @param field
	 * @param fileName
	 * @throws MooshakException
	 */
	void removeFile(String objectId, String field, String fileName)
			throws MooshakException;
}