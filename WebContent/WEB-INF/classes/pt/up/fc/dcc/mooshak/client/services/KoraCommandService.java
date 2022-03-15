package pt.up.fc.dcc.mooshak.client.services;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;;

/**
 * Synchronous service interface for Kora commands
 * 
 * @author Helder Correia
 */
@RemoteServiceRelativePath("koraCommand")
public interface KoraCommandService extends RemoteService {

	/**
	 * @return String with configuration to create a new eshu
	 * @throws MooshakException
	 */
	
	public  ConfigInfo getEshuConfig(String id) throws MooshakException;
	
	
	/**
	 * @return JsonObject with configuration to create a new eshu
	 * @throws MooshakException
	 */
	
	public Map<String, String> getImageSVG(String path) throws MooshakException;
	

	
}
