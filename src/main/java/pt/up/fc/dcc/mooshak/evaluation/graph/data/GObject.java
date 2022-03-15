package pt.up.fc.dcc.mooshak.evaluation.graph.data;

import static pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName.EDGE_PROPERTY_WEIGHT;
import static pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName.EDGE_TYPE_WEIGHT;
import static pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName.NODE_PROPERTY_WEIGHT;
import static pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import pt.up.fc.dcc.mooshak.evaluation.graph.eval.ConfigName;
import pt.up.fc.dcc.mooshak.evaluation.graph.eval.Configs;

/**
 * GObject class represents any graphic element of a diagram (node or edge). It
 * groups all the features that is common to both of them.
 * 
 * @author Ruben
 *
 */
public class GObject {

	protected String id;
	protected String type;
	protected Map<PropertyName, PropertyValue> properties;
	protected int value;
	protected int indistincts;
	protected String name;

	public GObject() {
		this.id = null;
		this.type = null;
		this.properties = new HashMap<>();
		this.value = 0;
		this.indistincts = 1;
		this.name = "";
	}

	public GObject(String id) {
		this.id = id;
		this.properties = new HashMap<>();
		this.value = 0;
		this.indistincts = 1;
		this.name = "";
	}

	public GObject(String id, String type) {
		this.id = id;
		this.type = type;
		this.properties = new HashMap<>();
		this.value = 0;
		this.indistincts = 1;
		this.name = "";
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the indistincts
	 */
	public int getIndistincts() {
		return indistincts;
	}

	/**
	 * @param indistincts
	 *            the indistincts to set
	 */
	public void setIndistincts(int indistincts) {
		this.indistincts = indistincts;
	}

	/**
	 * @return the properties
	 */
	public Map<PropertyName, PropertyValue> getProperties() {
		return properties;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(Map<PropertyName, PropertyValue> properties) {
		this.properties = properties;
	}
	
	public void addProperties(Map<PropertyName, PropertyValue> properties) {
		for(Entry<PropertyName, PropertyValue> prop:properties.entrySet())
			this.properties.put(prop.getKey(), prop.getValue());
	}

	/**
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Adds a property the properties' list
	 * 
	 * @param propertyName
	 * @param propertyValue
	 */
	public void addProperty(PropertyName propertyName, PropertyValue propertyValue) {
//		if(this.properties==null)
//			this.properties = new HashMap<>();
//		if(propertyName!=null && propertyValue!=null) 
			this.properties.put(propertyName, propertyValue);
	}

	/**
	 * Compute the max value this object can get
	 * @param configs
	 * @return
	 */
	public int computeMaxValue(Configs configs) {
		value = 0;
		ConfigName propertyWeight = isNode() ? NODE_PROPERTY_WEIGHT : EDGE_PROPERTY_WEIGHT;

		if (type != null)
			value += configs.get(isNode() ? NODE_TYPE_WEIGHT : EDGE_TYPE_WEIGHT);

		for (PropertyName property : properties.keySet()) {
			if (property.isSimple())
				value += configs.get(propertyWeight);

			else {
				
				System.out.println(properties.get(property));
				CompositePropertyValue propertyValue = (CompositePropertyValue) properties.get(property);
				
				for (@SuppressWarnings("unused")
				String key : propertyValue.value.keySet())
					value += configs.get(propertyWeight);
				
			}
		}
		if (isNode()) {
			Node node = (Node) this;
			int degreeCounter = 0;
			for (String type : node.getInDegree().keySet())
				degreeCounter += node.getInDegree().get(type);
			for (String type : node.getOutDegree().keySet())
				degreeCounter += node.getOutDegree().get(type);
			value += configs.get(NODE_DEGREE_WEIGHT) * degreeCounter;
		}

		value *= this.indistincts;
		return value;
	}

	public boolean isNode() {
		return false;
	}

	/**
	 * Checks if this node as the same id as the @param id
	 * 
	 * @param id
	 * @return
	 */
	boolean hasId(String id) {
		return this.id.equals(id);
	}

	/**
	 * Verifies if this object is an blank object
	 * 
	 * @return
	 */
	boolean isBlank() {
		if (this.id.contains("###blank_") && this.type == null)
			return true;
		return false;
	}

	/**
	 * Creates a copy of this object's properties
	 * @param object
	 * @param properties
	 */
	void propertiesDeepCopy(GObject object, Map<PropertyName, PropertyValue> properties) {
		for (PropertyName propertyName : properties.keySet()) {
			PropertyName name = propertyName.deepCopy();
			PropertyValue value = properties.get(name).deepCopy();
			object.addProperty(name, value);
		}
	}
	

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((properties == null) ? 0 : properties.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
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
		GObject other = (GObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (properties == null) {
			if (other.properties != null)
				return false;
		} else if (!properties.equals(other.properties))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

}
