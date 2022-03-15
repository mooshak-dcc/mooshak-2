package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;;

/**
 * Asynchronous service interface for Kora commands
 * 
 * @author Helder Correia
 */
public interface KoraCommandServiceAsync {

	void getEshuConfig(String id, AsyncCallback<ConfigInfo> callback); //o throws aqui não é preciso, vai na callback
	void getImageSVG(String path,AsyncCallback<Map<String, String>> callback);
	

}
