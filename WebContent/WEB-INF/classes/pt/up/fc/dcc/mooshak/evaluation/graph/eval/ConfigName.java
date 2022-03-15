package pt.up.fc.dcc.mooshak.evaluation.graph.eval;


public enum ConfigName { NODE_FACTOR(66), 
	NODE_TYPE_WEIGHT(4), //2 
	NODE_PROPERTY_WEIGHT(3),
	NODE_PROPERTY_PENALTY(1),
	NODE_DEGREE_WEIGHT(0), //2
	
	EDGE_TYPE_WEIGHT(18), //10
	EDGE_PROPERTY_WEIGHT(4),
	EDGE_PROPERTY_PENALTY(4);

	int value = 0;

	ConfigName(int value) {
		this.value = value;
	}
	
};