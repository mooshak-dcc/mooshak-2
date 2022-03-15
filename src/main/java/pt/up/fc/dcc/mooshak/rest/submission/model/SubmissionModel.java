package pt.up.fc.dcc.mooshak.rest.submission.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.up.fc.dcc.mooshak.rest.model.PoModel;

@XmlRootElement(name = "submission")
@XmlType(name = "submission")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubmissionModel extends PoModel {
	
	private String consider;
	private Date date;
	private Date time;
	private String problemId;
	private String teamId;
	private String classify;
	private Integer mark;
	private Long size;
	private String observations;
	private String state;
	//private Language language;
	//private Path program;
	//private UserTestData userTestData;	
	//private Path reportPath;
	//private Reports reports;
	private Double elapsed;
	private Double cpu;
	private Double memory;
	private Integer signals;
	private String feedback;
	private String reviewerObservations;

	public SubmissionModel() {
	}

	/**
	 * @return the consider
	 */
	public String getConsider() {
		return consider;
	}

	/**
	 * @param consider the consider to set
	 */
	public void setConsider(String consider) {
		this.consider = consider;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @param date the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the time
	 */
	public Date getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * @return the problemId
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param problemId the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * @return the teamId
	 */
	public String getTeamId() {
		return teamId;
	}

	/**
	 * @param teamId the teamId to set
	 */
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	/**
	 * @return the classify
	 */
	public String getClassify() {
		return classify;
	}

	/**
	 * @param classify the classify to set
	 */
	public void setClassify(String classify) {
		this.classify = classify;
	}

	/**
	 * @return the mark
	 */
	public Integer getMark() {
		return mark;
	}

	/**
	 * @param mark the mark to set
	 */
	public void setMark(Integer mark) {
		this.mark = mark;
	}

	/**
	 * @return the size
	 */
	public Long getSize() {
		return size;
	}

	/**
	 * @param size the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}

	/**
	 * @return the observations
	 */
	public String getObservations() {
		return observations;
	}

	/**
	 * @param observations the observations to set
	 */
	public void setObservations(String observations) {
		this.observations = observations;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * @return the elapsed
	 */
	public Double getElapsed() {
		return elapsed;
	}

	/**
	 * @param elapsed the elapsed to set
	 */
	public void setElapsed(Double elapsed) {
		this.elapsed = elapsed;
	}

	/**
	 * @return the cpu
	 */
	public Double getCpu() {
		return cpu;
	}

	/**
	 * @param cpu the cpu to set
	 */
	public void setCpu(Double cpu) {
		this.cpu = cpu;
	}

	/**
	 * @return the memory
	 */
	public Double getMemory() {
		return memory;
	}

	/**
	 * @param memory the memory to set
	 */
	public void setMemory(Double memory) {
		this.memory = memory;
	}

	/**
	 * @return the signals
	 */
	public Integer getSignals() {
		return signals;
	}

	/**
	 * @param signals the signals to set
	 */
	public void setSignals(Integer signals) {
		this.signals = signals;
	}

	/**
	 * @return the feedback
	 */
	public String getFeedback() {
		return feedback;
	}

	/**
	 * @param feedback the feedback to set
	 */
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	/**
	 * @return the reviewerObservations
	 */
	public String getReviewerObservations() {
		return reviewerObservations;
	}

	/**
	 * @param reviewerObservations the reviewerObservations to set
	 */
	public void setReviewerObservations(String reviewerObservations) {
		this.reviewerObservations = reviewerObservations;
	}

}
