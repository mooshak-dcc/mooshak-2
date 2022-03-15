package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class EdgeDeletion implements GraphDifference {
	Edge deletion;
	int quantity;
	
	public EdgeDeletion(Edge deletion) {
		super();
		this.deletion = deletion;
		this.quantity = 1;
	}
	
	public EdgeDeletion(Edge deletion, int quantity) {
		this.deletion = deletion;
		this.quantity = quantity;
	}

	/**
	 * @return the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * @param quantity the quantity to set
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
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
		return true;
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
		result = prime * result
				+ ((deletion == null) ? 0 : deletion.hashCode());
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
		EdgeDeletion other = (EdgeDeletion) obj;
		if (deletion == null) {
			if (other.deletion != null)
				return false;
		} else if (!deletion.equals(other.deletion))
			return false;
		return true;
	}

	@Override
	public boolean isDifferentType() {
		// TODO Auto-generated method stub
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EdgeDeletion [source=" + deletion.getSource().getId()
				+ "|target=" + deletion.getTarget().getId() + "|quantity=" + quantity + "]";
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
	public boolean isInDegreeDifference() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOutDegreeDifference() {
		// TODO Auto-generated method stub
		return false;
	}

	public Edge getDeletion() {
		// TODO Auto-generated method stub
		return deletion;
	}

}