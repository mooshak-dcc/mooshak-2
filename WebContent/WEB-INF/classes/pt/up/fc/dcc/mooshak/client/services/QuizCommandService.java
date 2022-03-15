package pt.up.fc.dcc.mooshak.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

/**
 * Synchronous service interface for Quiz commands
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RemoteServiceRelativePath("quizCommand")
public interface QuizCommandService extends RemoteService {

	/**
	 * The client-side stub for the RPC service.
	 */

	public  String getQuizHTML (String name) throws MooshakException;
	
	public  String getQuizHTMLFinal (String name) throws MooshakException;
	

	
}
