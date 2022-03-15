package pt.up.fc.dcc.mooshak.rest.auth.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Model for the user credentials
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
@XmlRootElement
public class UserCredentials {
	
	private String contest;
	private String username;
	private String password;

	public UserCredentials() {
	}

	/**
	 * @return the contest
	 */
	public String getContest() {
		return contest;
	}

	/**
	 * @param contest the contest to set
	 */
	public void setContest(String contest) {
		this.contest = contest;
	}

	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
}
