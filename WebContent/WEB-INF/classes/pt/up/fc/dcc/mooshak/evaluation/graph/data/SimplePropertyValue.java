package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public class SimplePropertyValue extends PropertyValue {
	String value;

	public SimplePropertyValue(String value) {
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public PropertyValue deepCopy(){
		return new SimplePropertyValue(this.value);
	}
	
	public String toString() {
		return " | value: " + value;
	}

	public boolean isSimple() {
		return true;
	}

	/**
	 * Compare property. If values are equals returns 100 otherwise returns 0.
	 * 
	 * @param property
	 * @return
	 */
	public GradeWithDifferences compareProperty(
			GObject object, 
			PropertyName name, 
			PropertyValue value,
			Configs configs) {
		if(this.value.equals(((SimplePropertyValue) value).value))
			return new GradeWithDifferences(configs.get(object.isNode() ?
					ConfigName.NODE_PROPERTY_WEIGHT : ConfigName.EDGE_PROPERTY_WEIGHT));
		else {
			Set<GraphDifference> diff = new HashSet<>();
			diff.add(new DifferentPropertyValue(object, name, this, value));
			return new GradeWithDifferences(diff, 0);
		}

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimplePropertyValue other = (SimplePropertyValue) obj;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}
	
	
}