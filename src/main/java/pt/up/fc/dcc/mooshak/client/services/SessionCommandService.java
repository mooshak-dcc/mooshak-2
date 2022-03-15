package pt.up.fc.dcc.mooshak.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

@RemoteServiceRelativePath("sessionCommand")
public interface SessionCommandService extends RemoteService {

	/**
	 * Get timeout time
	 * @return timeout
	 * @throws MooshakException if no session
	 */
	Integer getUserSessionTimeout() throws MooshakException;
	
	/**
	 * Check if session is alive
	 * @return boolean
	 * @throws MooshakException 
	 */
	Boolean isSessionAlive() throws MooshakException;
	
	/**
	 * Check if server responds
	 * @throws MooshakException 
	 */
	void ping() throws MooshakException;
}
