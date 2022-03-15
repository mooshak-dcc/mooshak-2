package pt.up.fc.dcc.mooshak.rest.problem.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;

@XmlRootElement(name = "evaluation-summary")
@XmlType(name = "evaluation-summary")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EvaluationSummaryModel extends EvaluationSummary {
	
	public EvaluationSummaryModel() {
	}
	
	public EvaluationSummaryModel(EvaluationSummary summary) {
		setId(summary.getId());
		setEvaluatedAt(summary.getEvaluatedAt());
		setLanguage(summary.getLanguage());
		setState(summary.getState());
		setStatus(summary.getStatus());
		setObservations(summary.getObservations());
		setFeedback(summary.getFeedback());
		setMark(summary.getMark());
		setProgramSize(summary.getMetrics().getProgramSize());
		setLinesOfCode(summary.getMetrics().getLinesOfCode());
		setExecutionTime(summary.getMetrics().getExecutionTime());
		setCpuUsage(summary.getMetrics().getCpuUsage());
		setMemoryUsage(summary.getMetrics().getMemoryUsage());
		setOutputs(summary.getOutputs());
		setUserExecutionTimes(summary.getUserExecutionTimes());
	}
	
}
