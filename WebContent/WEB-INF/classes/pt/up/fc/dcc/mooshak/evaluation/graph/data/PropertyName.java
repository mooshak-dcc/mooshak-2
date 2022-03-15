package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.Map;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public abstract class PropertyName{
	public abstract boolean isSimple();
	public abstract String toString();
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
	public abstract boolean equals(Object obj, Map<Node, Match> map);
	public abstract PropertyName deepCopy();
}
