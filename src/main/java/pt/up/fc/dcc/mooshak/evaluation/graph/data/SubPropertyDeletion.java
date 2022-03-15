package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class SubPropertyDeletion implements GraphDifference {
    GObject object;
	PropertyName propertyName;
	PropertyValue propertyValue;
	String key;
	String value;
	
	public SubPropertyDeletion(GObject object, PropertyName propertyName,
			PropertyValue propertyValue, String key, String correctValue) {
		this.object = object;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
		this.key = key;
		this.value = correctValue;
	}

	public boolean isNodeInsertion(){
		return false;
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
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result
				+ ((propertyName == null) ? 0 : propertyName.hashCode());
		result = prime * result
				+ ((propertyValue == null) ? 0 : propertyValue.hashCode());
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
		SubPropertyDeletion other = (SubPropertyDeletion) obj;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (propertyName == null) {
			if (other.propertyName != null)
				return false;
		} else if (!propertyName.equals(other.propertyName))
			return false;
		if (propertyValue == null) {
			if (other.propertyValue != null)
				return false;
		} else if (!propertyValue.equals(other.propertyValue))
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
		return true;
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

