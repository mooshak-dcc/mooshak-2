package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class EdgeInsertion implements GraphDifference {
    Edge insertion;
    int quantity;

	public EdgeInsertion(Edge insertion) {
		super();
		this.insertion = insertion;
		this.quantity = 1;
	}
	
	public EdgeInsertion(Edge insertion, int quantity) {
		this.insertion = insertion;
		this.quantity = quantity;
	}

	/**
	 * @return the insertion
	 */
	public Edge getInsertion() {
		return insertion;
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
		return false;
	}
	
	public boolean isEdgeInsertion(){
		return true;
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
		EdgeInsertion other = (EdgeInsertion) obj;
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
	
	@Override
	public String toString() {
		return "EdgeInsertion [source=" + insertion.getSource().getId()
				+ "|target=" + insertion.getTarget().getId() + "|quantity=" + quantity + "]";}

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