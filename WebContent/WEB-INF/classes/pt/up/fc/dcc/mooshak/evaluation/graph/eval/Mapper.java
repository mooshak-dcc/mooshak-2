package pt.up.fc.dcc.mooshak.evaluation.graph.eval;

import java.util.Map;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

public interface Mapper extends Iterable<Map<Node, Match>>{
	
	public void setRunning(boolean running);
	
	public boolean isRunning();

}
