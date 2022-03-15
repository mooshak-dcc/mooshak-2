package pt.up.fc.dcc.mooshak.client.data.admin;

import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

/**
 * Form containing data accessible using a map where field values are
 * indexed by field names (keys).
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface HasFormData {
	
	/**
	 * Use map to set field values using keys as field names
	 * @param data map containing field names and values
	 */
	public void setFieldValues(Map<String,MooshakValue> data);
	
	/**
	 * Get a map with field values using keys as field names
	 * @return map containing field names and values
	 */
	public Map<String,MooshakValue> getFieldValues();

}
