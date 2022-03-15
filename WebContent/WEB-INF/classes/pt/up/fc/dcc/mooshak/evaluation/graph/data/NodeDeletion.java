package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class NodeDeletion implements GraphDifference {
    Node deletion;
    int quantity;

	public NodeDeletion(Node deletion) {
		super();
		this.deletion = deletion;
		this.quantity = 1;
	}

	public NodeDeletion(Node deletion, int quantity) {
		this.deletion = deletion;
		this.quantity = quantity;
	}

	/**
	 * @return the deletion
	 */
	public Node getDeletion() {
		return deletion;
	}

	/**
	 * @param deletion the deletion to set
	 */
	public void setDeletion(Node deletion) {
		this.deletion = deletion;
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

	public boolean isNodeInsertion(){
		return false;
	}
	
	public boolean isNodeDeletion(){
		return true;
	}
	
	public boolean isEdgeInsertion(){
		return false;
	}
	
	public boolean isEdgeDeletion(){
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeDeletion [deletion=" + deletion.getId() + "|quantity=" + quantity + "]";
	}

	/* (non-Javadoc)
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
		NodeDeletion other = (NodeDeletion) obj;
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
    
}
