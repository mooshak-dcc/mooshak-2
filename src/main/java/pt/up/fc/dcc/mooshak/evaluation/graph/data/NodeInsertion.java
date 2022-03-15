package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class NodeInsertion implements GraphDifference {
    Node insertion;
    int quantity;
    

	public NodeInsertion(Node insertion) {
		super();
		this.insertion = insertion;
		this.quantity = 1;
	}
	
	
	public NodeInsertion(Node insertion, int quantity) {
		this.insertion = insertion;
		this.quantity = quantity;
	}


	/**
	 * @return the insertion
	 */
	public Node getInsertion() {
		return insertion;
	}



	/**
	 * @param insertion the insertion to set
	 */
	public void setInsertion(Node insertion) {
		this.insertion = insertion;
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
		return true;
	}
	
	public boolean isNodeDeletion(){
		return false;
	}
	
	public boolean isEdgeInsertion(){
		return false;
	}
	
	public boolean isEdgeDeletion(){
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((insertion == null) ? 0 : insertion.hashCode());
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
		NodeInsertion other = (NodeInsertion) obj;
		if (insertion == null) {
			if (other.insertion != null)
				return false;
		} else if (!insertion.equals(other.insertion))
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NodeInsertion [insertion=" + insertion.getId()+insertion.getName() + "|quantity=" + quantity + "]";
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