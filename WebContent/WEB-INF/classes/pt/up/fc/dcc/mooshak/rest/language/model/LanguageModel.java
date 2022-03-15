package pt.up.fc.dcc.mooshak.rest.language.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.up.fc.dcc.mooshak.rest.model.PoModel;

/**
 * Model for a language
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
@XmlRootElement(name = "language")
@XmlType(name = "language")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LanguageModel extends PoModel {
	
	private String id;
	private String name;
	private String extension;
	private String compiler;
	private String version;
	private String compile;
	private String execute;
	private Long data;
	private Long fork;
	private String omit;
	private Integer uid;
	private String editableContents;

	public LanguageModel() {
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
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the extension
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @param extension the extension to set
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * @return the compiler
	 */
	public String getCompiler() {
		return compiler;
	}

	/**
	 * @param compiler the compiler to set
	 */
	public void setCompiler(String compiler) {
		this.compiler = compiler;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * @return the compile
	 */
	public String getCompile() {
		return compile;
	}

	/**
	 * @param compile the compile to set
	 */
	public void setCompile(String compile) {
		this.compile = compile;
	}

	/**
	 * @return the execute
	 */
	public String getExecute() {
		return execute;
	}

	/**
	 * @param execute the execute to set
	 */
	public void setExecute(String execute) {
		this.execute = execute;
	}

	/**
	 * @return the data
	 */
	public Long getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Long data) {
		this.data = data;
	}

	/**
	 * @return the fork
	 */
	public Long getFork() {
		return fork;
	}

	/**
	 * @param fork the fork to set
	 */
	public void setFork(Long fork) {
		this.fork = fork;
	}

	/**
	 * @return the omit
	 */
	public String getOmit() {
		return omit;
	}

	/**
	 * @param omit the omit to set
	 */
	public void setOmit(String omit) {
		this.omit = omit;
	}

	/**
	 * @return the uid
	 */
	public Integer getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(Integer uid) {
		this.uid = uid;
	}

	/**
	 * @return the editableContents
	 */
	public String getEditableContents() {
		return editableContents;
	}

	/**
	 * @param editableContents the editableContents to set
	 */
	public void setEditableContents(String editableContents) {
		this.editableContents = editableContents;
	}

}
