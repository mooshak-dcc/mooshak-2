package pt.up.fc.dcc.mooshak.rest.problem.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonInclude;

import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;

@XmlRootElement(name = "problem-info")
@XmlType(name = "problem-info")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProblemInfoModel extends ProblemInfo {

	public ProblemInfoModel() {
	}

}
