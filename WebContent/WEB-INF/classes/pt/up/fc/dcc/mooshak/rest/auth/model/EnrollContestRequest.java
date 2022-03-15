package pt.up.fc.dcc.mooshak.rest.auth.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Model for enrolling in a contest.
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnrollContestRequest {
	
	private String contest;
	private String username;
	private String password;
	private String name;

	public EnrollContestRequest() {
	}

	public String getContest() {
		return contest;
	}

	public void setContest(String contest) {
		this.contest = contest;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
