package pt.up.fc.dcc.mooshak.rest.problem.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.up.fc.dcc.mooshak.rest.model.PoModel;

/**
 * Model for a skeleton.
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
@XmlRootElement(name = "skeleton")
@XmlType(name = "skeleton")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkeletonModel extends PoModel {
	private String id;
	private String extension;
	private String code;
	
	public SkeletonModel() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
}
