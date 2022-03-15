package pt.up.fc.dcc.mooshak.managers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.evaluation.kora.Configuration;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

/**
 * Manages all Kora requests. This class ignores types from any particular
 * communication layer, such as GWT RPC or Jersey
 * 
 * @author Helder Correia
 */
public class KoraManager extends Manager {

	static KoraManager manager = null;

	private Map<String, ConfigInfo> configurationsMap;

	private Map<String, Map<String, String>> imagesSVG;

	/**
	 * Get a single instance of this class
	 * 
	 * @return
	 * @throws MooshakException
	 */
	public static KoraManager getInstance() {

		if (manager == null)
			manager = new KoraManager();
		return manager;
	}

	private KoraManager() {
		this.configurationsMap = new HashMap<String, ConfigInfo>();
		this.imagesSVG = new HashMap<String, Map<String, String>>();
	}

	/**
	 * @param id
	 * @return the eshuConfiguration
	 * @throws MooshakException
	 * @throws IOException
	 */
	public ConfigInfo getEshuConfiguration(Session session, String id) throws MooshakException, IOException {

			
		if (configurationsMap.containsKey(id))
			return configurationsMap.get(id);

		return getConfigurationLanguage(session, id);
	}

	/**
	 * String filename- file name configuration
	 * 
	 * @return the configurations
	 * @throws IOException
	 * @throws MooshakException
	 */
	public ConfigInfo getConfigInfo(Configuration configuration) throws IOException {

		ConfigInfo confInfo = new ConfigInfo();
		// confInfo.setTextBox(configuration.getEshu().getStyle().getTextBox().getInfo());
		confInfo.setToolbarStyle(configuration.getDL2().getStyle().getToolbarStyle().getInfo());
		// confInfo.setVertice(configuration.getEshu().getStyle().getVertice().getInfo());
		confInfo.setEditorStyle(configuration.getDL2().getStyle().getEditorStyle().getInfo());
		confInfo.setNodeTypes(configuration.getDL2().getDiagram().getNodeTypes().toString());
		confInfo.setEdgeTypes(configuration.getDL2().getDiagram().getEdgeTypes().toString());
		confInfo.setImagesSVG(configuration.getImagesSVG());
		confInfo.setSyntaxValidation(configuration.getDL2().getDiagram().getSyntaxeValidation().toString());
		return confInfo;
	}

	/**
	 * if configuration exist in configurationsMap return the value else if
	 * create a new configuration store in configurationsMap and return String
	 * path- path to configuration
	 * 
	 * @return the configurations
	 * @throws MooshakException
	 */

	public Map<String, ConfigInfo> getConfigurationsMap() {
		return configurationsMap;
	}

	public void setConfigurationsMap(Map<String, ConfigInfo> configurationsMap) {
		this.configurationsMap = configurationsMap;
	}

	public ConfigInfo getConfigurationLanguage(Session session, String id) throws IOException, MooshakException {

		Contest contest = session.getContest();
		Languages languages = contest.open("languages");
		Configuration configuration = null;
		try (POStream<Language> stream = languages.newPOStream()) {
			for (Language language : stream) {
				Path conf = language.getConfiguration();
				if (conf != null) {
					Path path = PersistentCore.getHomePath().resolve(conf);
					LOGGER.severe(path.getName(path.getNameCount() - 2).toString());
					if (path.getNameCount() >= 2 && 
							path.getName(path.getNameCount() - 2).toString().equalsIgnoreCase(id))
						configuration = new Configuration(path.toString());
				}
			}
		} catch (Exception e) {
			throw new MooshakException("Error iteration on languages: " + e.getMessage());
		}
		return getConfigInfo(configuration);
	}

	public Map<String, Map<String, String>> getImagesSVG() {
		return imagesSVG;
	}

	public void setImagesSVG(Map<String, Map<String, String>> imagesSVG) {
		this.imagesSVG = imagesSVG;
	}

}
