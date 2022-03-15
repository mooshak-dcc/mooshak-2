package pt.up.fc.dcc.mooshak.client.guis.judge.data;

/**
 * Represents a field of a test related to a problem
 * 
 * @author josepaiva
 */
public class TestField {

	private String fieldName;

	private String fieldValue;

	public TestField(String name, String value) {
		fieldName = name;
		fieldValue = value;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the fieldValue
	 */
	public String getFieldValue() {
		return fieldValue;
	}

	/**
	 * @param fieldValue
	 *            the fieldValue to set
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

}
