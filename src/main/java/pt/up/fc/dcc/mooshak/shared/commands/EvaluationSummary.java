package pt.up.fc.dcc.mooshak.shared.commands;

import java.util.Date;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Simple report with a summary of submission evaluation
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class EvaluationSummary implements IsSerializable {

	String id = null;
	Date evaluatedAt = null;

	String state = null;
	String language = null;

	String status = null;
	int mark = 0;

	String observations = null;
	String feedback = null;

	EvaluationMetrics metrics = new EvaluationMetrics();

	// validations
	Map<Integer, String> outputs = null;
	Map<Integer, String> userExecutionTimes = null;

	public EvaluationSummary() {
		// TODO Auto-generated constructor stub
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getEvaluatedAt() {
		return evaluatedAt;
	}

	public void setEvaluatedAt(Date evaluatedAt) {
		this.evaluatedAt = evaluatedAt;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getMark() {
		return mark;
	}

	public void setMark(int mark) {
		this.mark = mark;
	}

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	public EvaluationMetrics getMetrics() {
		return metrics;
	}
	
	public void setMetrics(EvaluationMetrics metrics) {
		this.metrics = metrics;
	}

	public void setExecutionTime(double executionTime) {
		metrics.setExecutionTime(executionTime);
	}

	public void setCpuUsage(double cpuUsage) {
		metrics.setCpuUsage(cpuUsage);
	}

	public void setMemoryUsage(double memoryUsage) {
		metrics.setMemoryUsage(memoryUsage);
	}

	public void setProgramSize(long size) {
		metrics.setProgramSize(size);
	}

	public void setLinesOfCode(int linesOfCode) {
		metrics.setLinesOfCode(linesOfCode);
	}

	public Map<Integer, String> getOutputs() {
		return outputs;
	}

	public void setOutputs(Map<Integer, String> outputs) {
		this.outputs = outputs;
	}

	public Map<Integer, String> getUserExecutionTimes() {
		return userExecutionTimes;
	}

	public void setUserExecutionTimes(Map<Integer, String> userExecutionTimes) {
		this.userExecutionTimes = userExecutionTimes;
	}

}
