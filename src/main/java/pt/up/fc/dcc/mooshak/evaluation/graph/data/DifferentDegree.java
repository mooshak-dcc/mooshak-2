package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class DifferentDegree implements GraphDifference{
	Node node;
	DifferentConnection differentConnection;
	String name;
	int correctValue;
	int wrongValue;
	
	
	public DifferentDegree(Node node, DifferentConnection differentConnection) {
		this.node = node;
		this.name = differentConnection.getType();
		this.correctValue = differentConnection.getCorrectDegree();
		this.wrongValue = differentConnection.getWrongDegree();
	}

	@Override
	public boolean isNodeInsertion() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isNodeDeletion() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isEdgeInsertion() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean isEdgeDeletion() {
		// TODO Auto-generated method stub
		return false;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + correctValue;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result + wrongValue;
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
		DifferentDegree other = (DifferentDegree) obj;
		if (correctValue != other.correctValue)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (wrongValue != other.wrongValue)
			return false;
		return true;
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

	public GObject getObject() {
		return this.node;
	}

	/**
	 * @return the correctValue
	 */
	public int getCorrectValue() {
		return correctValue;
	}

	/**
	 * @param correctValue the correctValue to set
	 */
	public void setCorrectValue(int correctValue) {
		this.correctValue = correctValue;
	}

	/**
	 * @return the wrongValue
	 */
	public int getWrongValue() {
		return wrongValue;
	}

	/**
	 * @param wrongValue the wrongValue to set
	 */
	public void setWrongValue(int wrongValue) {
		this.wrongValue = wrongValue;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DifferentPropertyValue [object=" + node.getId() + ", name=" + name
				+ ", correctValue=" + correctValue + ", wrongValue="
				+ wrongValue + "]";
	}
	
	
}
