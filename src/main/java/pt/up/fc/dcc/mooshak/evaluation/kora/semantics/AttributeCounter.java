package pt.up.fc.dcc.mooshak.evaluation.kora.semantics;

import pt.up.fc.dcc.mooshak.evaluation.graph.data.Node;

class AttributeCounter {
	Node node;
	String attributeType;
	int counter;

	AttributeCounter(Node node, String attributeType) {
		this.node = node;
		this.attributeType = attributeType;
		this.counter = 1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attributeType == null) ? 0 : attributeType.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		AttributeCounter other = (AttributeCounter) obj;
		if (attributeType == null) {
			if (other.attributeType != null)
				return false;
		} else if (!attributeType.equals(other.attributeType))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}

}