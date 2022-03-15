package pt.up.fc.dcc.mooshak.client.guis.judge.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a test related to a problem
 * 
 * @author josepaiva
 */
public class Test {

	private String name;

	private List<TestField> fields;

	public Test(String name) {
		super();
		this.name = name;
		fields = new ArrayList<TestField>();
	}

	public Test(String name, List<TestField> fields) {
		super();
		this.name = name;
		this.fields = fields;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the fields
	 */
	public List<TestField> getFields() {
		return fields;
	}

	public void addField(TestField field) {
		fields.add(field);
	}

	public void addField(String name, String value) {
		fields.add(new TestField(name, value));
	}

}
