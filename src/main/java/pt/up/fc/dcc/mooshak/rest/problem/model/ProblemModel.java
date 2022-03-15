package pt.up.fc.dcc.mooshak.rest.problem.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.up.fc.dcc.mooshak.rest.model.PoModel;

/**
 * Model for a problem
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
@XmlRootElement(name = "problem")
@XmlType(name = "problem")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemModel extends PoModel {
	
	private String name;
	private String color;
	private String title;
	private String difficulty;
	private String type;
	private Integer timeout;
	private String staticCorrector;
	private String dynamicCorrector;
	private String gameManager;
	private String originalLocation;
	private String editorKind;
	private String start;
	private String stop;

	public ProblemModel() {
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
	 * @return the color
	 */
	public String getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the difficulty
	 */
	public String getDifficulty() {
		return difficulty;
	}

	/**
	 * @param difficulty the difficulty to set
	 */
	public void setDifficulty(String difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the timeout
	 */
	public Integer getTimeout() {
		return timeout;
	}

	/**
	 * @param timeout the timeout to set
	 */
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return the staticCorrector
	 */
	public String getStaticCorrector() {
		return staticCorrector;
	}

	/**
	 * @param staticCorrector the staticCorrector to set
	 */
	public void setStaticCorrector(String staticCorrector) {
		this.staticCorrector = staticCorrector;
	}

	/**
	 * @return the dynamicCorrector
	 */
	public String getDynamicCorrector() {
		return dynamicCorrector;
	}

	/**
	 * @param dynamicCorrector the dynamicCorrector to set
	 */
	public void setDynamicCorrector(String dynamicCorrector) {
		this.dynamicCorrector = dynamicCorrector;
	}

	/**
	 * @return the gameManager
	 */
	public String getGameManager() {
		return gameManager;
	}

	/**
	 * @param gameManager the gameManager to set
	 */
	public void setGameManager(String gameManager) {
		this.gameManager = gameManager;
	}

	/**
	 * @return the originalLocation
	 */
	public String getOriginalLocation() {
		return originalLocation;
	}

	/**
	 * @param originalLocation the originalLocation to set
	 */
	public void setOriginalLocation(String originalLocation) {
		this.originalLocation = originalLocation;
	}

	/**
	 * @return the editorKind
	 */
	public String getEditorKind() {
		return editorKind;
	}

	/**
	 * @param editorKind the editorKind to set
	 */
	public void setEditorKind(String editorKind) {
		this.editorKind = editorKind;
	}

	/**
	 * @return the start
	 */
	public String getStart() {
		return start;
	}

	/**
	 * @param start the start to set
	 */
	public void setStart(String start) {
		this.start = start;
	}

	/**
	 * @return the stop
	 */
	public String getStop() {
		return stop;
	}

	/**
	 * @param stop the stop to set
	 */
	public void setStop(String stop) {
		this.stop = stop;
	}
}
