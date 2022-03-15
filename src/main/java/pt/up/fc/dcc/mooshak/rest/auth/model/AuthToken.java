package pt.up.fc.dcc.mooshak.rest.auth.model;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Model for the authentication token
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
@XmlRootElement
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthToken {
	
	private String token;

	public AuthToken() {
	}

	/**
	 * @return the token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * @param token the token to set
	 */
	public void setToken(String token) {
		this.token = token;
	}
}
