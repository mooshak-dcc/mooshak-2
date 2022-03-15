package pt.up.fc.dcc.mooshak.shared.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Data transfer object containing persistent object data
 * This object include files contained in objects
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class MooshakObject implements IsSerializable{
	
	String id;
	String type;
	Map<String,MooshakValue> values = new HashMap<String,MooshakValue>();
	List<String> children = new ArrayList<String>();
	
	public MooshakObject() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return the id
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param id the id to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return the fields
	 */
	public Map<String,MooshakValue> getValues() {
		return values;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setValues(Map<String,MooshakValue> values) {
		this.values = values;
	}
	/**
	 * @return the children
	 */
	public List<String> getChildren() {
		return children;
	}
	/**
	 * @param children the children to set
	 */
	public void setChildren(List<String> children) {
		this.children = children;
	}
	
	/**
	 * Get a single field value from this Mooshak object 
	 * @param field
	 * @return
	 */
	public MooshakValue getFieldValue(String field) {
		return values.get(field);
	}
	
	/**
	 * Get a single field value from this Mooshak object 
	 * @param field
	 * @param value
	 */
	public void setFieldValue(String field, MooshakValue value) {
		this.values.put(field, value);
	}
}
