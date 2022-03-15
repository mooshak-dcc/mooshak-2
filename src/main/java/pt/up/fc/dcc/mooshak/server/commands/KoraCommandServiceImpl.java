package pt.up.fc.dcc.mooshak.server.commands;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


import pt.up.fc.dcc.mooshak.client.services.KoraCommandService;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

/**
 * Kora Commands Service Implementation
 * 
 * @author Helder Correia
 */
public class KoraCommandServiceImpl extends CommandService implements KoraCommandService {
	private static final long serialVersionUID = 1L;
	//public  KoraManager koraManager ; <- isto está no CommandService !

	
	
	/**
	 * Method to get Eshu configuration in the the file xmlConfig
	 * 
	 * @return JsonObject with configuration to create eshu
	 */
	@Override
	public ConfigInfo getEshuConfig (String id) throws MooshakException {
		Session session = getSession();

		try {
			return koraManager.getEshuConfiguration(session,id);
		} catch (IOException e) {
			throw new MooshakException("getEshuConfig:" + e.getMessage());
		}
		
	}
	
	@Override
	public Map<String, String> getImageSVG(String path) throws MooshakException {
		
		Map<String, String> images = new HashMap<>();
//		try {
//			 //koraManager.getImageSVG(path);
//		} catch (IOException e) {
//			throw new MooshakException("getImageSVG:" + e.getMessage());
//		}
		return images;
	}

	


}
