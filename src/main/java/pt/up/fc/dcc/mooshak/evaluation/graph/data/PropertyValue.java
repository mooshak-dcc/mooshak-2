package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.Map;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public abstract class PropertyValue {
	final static int PENALTY_FACTOR = 10;

	public abstract String toString();
	public abstract boolean isSimple();
	public abstract PropertyValue deepCopy();
	public Map<String, String> info;
	public abstract GradeWithDifferences compareProperty(
			GObject object, 
			PropertyName name, 
			PropertyValue property,
			Configs configs);
	public abstract boolean equals(Object obj);
	public abstract int hashCode();
}
