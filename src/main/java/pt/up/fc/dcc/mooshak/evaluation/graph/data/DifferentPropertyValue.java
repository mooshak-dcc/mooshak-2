package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class DifferentPropertyValue implements GraphDifference{
	GObject object;
	PropertyName name;
	PropertyValue correctValue;
	PropertyValue wrongValue;
	
	
	public DifferentPropertyValue(GObject object, PropertyName name,
			PropertyValue correctValue, PropertyValue wrongValue) {
		this.object = object;
		this.name = name;
		this.correctValue = correctValue;
		this.wrongValue = wrongValue;
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
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((correctValue == null) ? 0 : correctValue.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result
				+ ((wrongValue == null) ? 0 : wrongValue.hashCode());
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
		DifferentPropertyValue other = (DifferentPropertyValue) obj;
		if (correctValue == null) {
			if (other.correctValue != null)
				return false;
		} else if (!correctValue.equals(other.correctValue))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!object.equals(other.object))
			return false;
		if (wrongValue == null) {
			if (other.wrongValue != null)
				return false;
		} else if (!wrongValue.equals(other.wrongValue))
			return false;
		return true;
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

	public GObject getObject() {
		return this.object;
	}
	
	public void setObject(GObject object) {
		this.object=object;;
	}

	/**
	 * @return the correctValue
	 */
	public PropertyValue getCorrectValue() {
		return correctValue;
	}
	
	/**
	 * @return String name of correctValue
	 */
	public String getCorrectValueString() {
		if(correctValue.isSimple()){
			SimplePropertyValue value1 = (SimplePropertyValue) correctValue;
			return value1.value;
		}
		CompositePropertyValue value1 = (CompositePropertyValue) correctValue;
		
		return value1.value.toString();
	}

	/**
	 * @param correctValue the correctValue to set
	 */
	public void setCorrectValue(PropertyValue correctValue) {
		this.correctValue = correctValue;
	}

	/**
	 * @return the wrongValue
	 */
	public PropertyValue getWrongValue() {
		return wrongValue;
	}

	
	/**
	 * @return the wrongValue
	 */
	public String getWrongValueString() {
		if(wrongValue.isSimple()){
			SimplePropertyValue value1 = (SimplePropertyValue) wrongValue;
			return value1.value;
		}
		CompositePropertyValue value1 = (CompositePropertyValue) wrongValue;
		
		return value1.value.toString();
	}
	/**
	 * @param wrongValue the wrongValue to set
	 */
	public void setWrongValue(PropertyValue wrongValue) {
		this.wrongValue = wrongValue;
	}
	
	

	/**
	 * @return the name
	 */
	public PropertyName getName() {
		return name;
	}
	
	public String getNameString() {
		if(name.isSimple()){
			SimplePropertyName nameS=(SimplePropertyName)name;
			return nameS.getName();
		}
		CompositePropertyName nameC=(CompositePropertyName)name;
		return nameC.getName();
	}

	/**
	 * @param name the name to set
	 */
	public void setName(PropertyName name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DifferentPropertyValue [object=" + object.getId() + ", name=" + name
				+ ", correctValue=" + correctValue + ", wrongValue="
				+ wrongValue + "]";
	}
	
	
}
