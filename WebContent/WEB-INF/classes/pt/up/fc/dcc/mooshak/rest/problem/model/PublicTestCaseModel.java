package pt.up.fc.dcc.mooshak.rest.problem.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.up.fc.dcc.mooshak.rest.model.PoModel;

/**
 * Model for a test case.
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
@XmlRootElement(name = "testcase")
@XmlType(name = "testcase")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PublicTestCaseModel extends PoModel {
	private String input;
	private String output;
	
	public PublicTestCaseModel() {
		super();
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}
}
