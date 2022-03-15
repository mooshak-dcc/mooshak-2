package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class PropertyDeletion implements GraphDifference {
	GObject object;
    PropertyName propertyName;
    PropertyValue propertyValue;
	
    public PropertyDeletion(GObject object, PropertyName propertyName,
			PropertyValue propertyValue) {
		super();
		this.object = object;
		this.propertyName = propertyName;
		this.propertyValue = propertyValue;
	}

    /**
	 * @return the object
	 */
	public GObject getObject() {
		return object;
	}

	/**
	 * @return the propertyName
	 */
	public PropertyName getPropertyName() {
		return propertyName;
	}

	/**
	 * @return the propertyValue
	 */
	public PropertyValue getPropertyValue() {
		return propertyValue;
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
		PropertyDeletion other = (PropertyDeletion) obj;
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
		return "PropertyDeletion [object=" + object.getId() + ", propertyName="
				+ propertyName + ", propertyValue=" + propertyValue + "]";
	}
	
	
}