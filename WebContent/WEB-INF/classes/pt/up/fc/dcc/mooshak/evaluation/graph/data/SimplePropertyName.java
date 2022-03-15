package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.Map;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Match;

public class SimplePropertyName extends PropertyName {
	String name;
	
	public SimplePropertyName(String name) {
		this.name = name;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public boolean isSimple(){
		return true;
	}
	
	public PropertyName deepCopy(){
		return new SimplePropertyName(this.name);
	}
	
	public String toString(){
		return "name = " + this.name;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		SimplePropertyName other = (SimplePropertyName) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	@Override
	public boolean equals(Object obj, Map<Node, Match> map) {
		// TODO Auto-generated method stub
		return false;
	}
}
