package pt.up.fc.dcc.mooshak.rest.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Model that displays a simple result of an operation
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@XmlRootElement(name = "result")
@XmlType(name = "result")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SimpleResult<T> {
	
	private T result;

	public SimpleResult() {
	}

	public SimpleResult(T result) {
		this.result = result;
	}

	/**
	 * @return the result
	 */
	public T getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(T result) {
		this.result = result;
	}
}
