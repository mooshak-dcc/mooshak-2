package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class DifferentConnection {
	private String type;
	private int correctDegree;
	private int wrongDegree;

	public DifferentConnection(String type, int correctDegree, int wrongDegree) {
		super();
		this.type = type;
		this.correctDegree = correctDegree;
		this.wrongDegree = wrongDegree;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getCorrectDegree() {
		return correctDegree;
	}

	public void setCorrectDegree(int correctDegree) {
		this.correctDegree = correctDegree;
	}

	public int getWrongDegree() {
		return wrongDegree;
	}

	public void setWrongDegree(int wrongDegree) {
		this.wrongDegree = wrongDegree;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DifferentConnection [type=" + type + ", correctDegree="
				+ correctDegree + ", wrongDegree=" + wrongDegree + "]";
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + correctDegree;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + wrongDegree;
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
		DifferentConnection other = (DifferentConnection) obj;
		if (correctDegree != other.correctDegree)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (wrongDegree != other.wrongDegree)
			return false;
		return true;
	}
	
	
}