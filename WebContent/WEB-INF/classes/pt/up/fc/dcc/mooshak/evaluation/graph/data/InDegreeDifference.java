package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import java.util.Set;

public class InDegreeDifference implements GraphDifference {
	Node node;
	Set<DifferentConnection> differences;

	
	public InDegreeDifference(Node node, Set<DifferentConnection> differences) {
		this.node = node;
		this.differences = differences;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @return the differences
	 */
	public Set<DifferentConnection> getDifferences() {
		return differences;
	}

	/**
	 * @param differences the differences to set
	 */
	public void setDifferences(Set<DifferentConnection> differences) {
		this.differences = differences;
	}

	/**
	 * @param node the node to set
	 */
	public void setNode(Node node) {
		this.node = node;
	}
	

	public boolean isNodeInsertion() {
		return false;
	}

	public boolean isNodeDeletion() {
		return false;
	}

	public boolean isEdgeInsertion() {
		return false;
	}

	public boolean isEdgeDeletion() {
		return false;
	}


	@Override
	public boolean isPropertyInsertion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPropertyDeletion() {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isDifferentPropertyValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDifferentSubPropertyValue() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubPropertyInsertion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isSubPropertyDeletion() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDifferentType() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isInDegreeDifference() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isOutDegreeDifference() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "InDegreeDifference [node=" + node.getId() + ", differences="
				+ differences + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((differences == null) ? 0 : differences.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		InDegreeDifference other = (InDegreeDifference) obj;
		if (differences == null) {
			if (other.differences != null)
				return false;
		} else if (!differences.equals(other.differences))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}
	
}