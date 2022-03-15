package pt.up.fc.dcc.mooshak.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous service interface for Quiz commands
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface QuizCommandServiceAsync {
	
	
//	void getEshuConfig(String id, AsyncCallback<ConfigInfo> callback); //o throws aqui não é preciso, vai na callback
//	/**
//	 * The async counterpart of <code>GreetingService</code>.
//	 */
	void getQuizHTML (String input, AsyncCallback<String> callback) ;
	
	void getQuizHTMLFinal (String input, AsyncCallback<String> callback) ;
	
	

}
