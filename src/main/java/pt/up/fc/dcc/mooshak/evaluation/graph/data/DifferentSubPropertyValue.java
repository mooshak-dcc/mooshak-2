package pt.up.fc.dcc.mooshak.evaluation.graph.data;

public class DifferentSubPropertyValue implements GraphDifference{
	GObject object;
	PropertyName name;
	PropertyValue value;
	String key;
	String correctValue;
	String wrongValue;
		
	public DifferentSubPropertyValue(GObject object, PropertyName name,
			PropertyValue value, String key, String correctValue,
			String wrongValue) {
		super();
		this.object = object;
		this.name = name;
		this.value = value;
		this.key = key;
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
		return false;
	}

	@Override
	public boolean isDifferentSubPropertyValue() {
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
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		DifferentSubPropertyValue other = (DifferentSubPropertyValue) obj;
		if (correctValue == null) {
			if (other.correctValue != null)
				return false;
		} else if (!correctValue.equals(other.correctValue))
			return false;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
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
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		if (wrongValue == null) {
			if (other.wrongValue != null)
				return false;
		} else if (!wrongValue.equals(other.wrongValue))
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

	@Override
	public String toString() {
		return "DifferentSubPropertyValue [ name=" + name + "key=" + key
				+ ", correctValue=" + correctValue + ", wrongValue=" + wrongValue + "]";
	}

	/**
	 * @return the object
	 */
	public GObject getObject() {
		return object;
	}

	/**
	 * @param object the object to set
	 */
	public void setObject(GObject object) {
		this.object = object;
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

	/**
	 * @return the value
	 */
	public PropertyValue getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(PropertyValue value) {
		this.value = value;
	}

	/**
	 * @return the correctValue
	 */
	public String getCorrectValue() {
		return correctValue;
	}

	/**
	 * @param correctValue the correctValue to set
	 */
	public void setCorrectValue(String correctValue) {
		this.correctValue = correctValue;
	}

	/**
	 * @return the wrongValue
	 */
	public String getWrongValue() {
		return wrongValue;
	}

	/**
	 * @param wrongValue the wrongValue to set
	 */
	public void setWrongValue(String wrongValue) {
		this.wrongValue = wrongValue;
	}

	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @param key the key to set
	 */
	public void setKey(String key) {
		this.key = key;
	}

	
	
	
}
