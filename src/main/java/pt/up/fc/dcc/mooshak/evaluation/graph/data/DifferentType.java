package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class DifferentType implements GraphDifference{
	GObject object;
	String solutionType;
	String attemptType;
	
	public DifferentType(GObject object, String solutionType, String attemptType) {
		this.object = object;
		this.solutionType = solutionType;
		this.attemptType = attemptType;
	}
	
	/**
	 * @return the solutionType
	 */
	public String getSolutionType() {
		return solutionType;
	}

	/**
	 * @param solutionType the solutionType to set
	 */
	public void setSolutionType(String solutionType) {
		this.solutionType = solutionType;
	}

	/**
	 * @return the attemptType
	 */
	public String getAttemptType() {
		return attemptType;
	}

	/**
	 * @param attemptType the attemptType to set
	 */
	public void setAttemptType(String attemptType) {
		this.attemptType = attemptType;
	}

	/**
	 * @return the object
	 */
	public GObject getObject() {
		return object;
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
		return true;
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
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((attemptType == null) ? 0 : attemptType.hashCode());
		result = prime * result
				+ ((solutionType == null) ? 0 : solutionType.hashCode());
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
		DifferentType other = (DifferentType) obj;
		if (attemptType == null) {
			if (other.attemptType != null)
				return false;
		} else if (!attemptType.equals(other.attemptType))
			return false;
		if (solutionType == null) {
			if (other.solutionType != null)
				return false;
		} else if (!solutionType.equals(other.solutionType))
			return false;
		return true;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DifferentType [object=" + object.getId() + ", solutionType="
				+ solutionType + ", attemptType=" + attemptType + "]";
	}
}
