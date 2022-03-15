package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.HashMap;
import java.util.Map;

public class Configs extends HashMap<ConfigName,Integer> {
	private static final long serialVersionUID = 1L;
	
	public static final int MAX_GRADE = 100;
	
	public Configs() {
		super();
	}
	
	public Configs(Map<ConfigName,Integer> configs) {
		super(configs);
	}
	
	public int getNodeFactor() {
		return get(ConfigName.NODE_FACTOR);
	}
	
	public void setNodeFactor(int value) {
		put(ConfigName.NODE_FACTOR,value);
	}
	
	public int getEdgeFactor() {
		return MAX_GRADE - get(ConfigName.NODE_FACTOR);
	}

	public static Configs getDefaultConfigs() {
		
		Configs configs = new Configs();
		for(ConfigName name: ConfigName.values()){
			configs.put(name, name.value);
		}
		
		return configs;
	}
	
}
