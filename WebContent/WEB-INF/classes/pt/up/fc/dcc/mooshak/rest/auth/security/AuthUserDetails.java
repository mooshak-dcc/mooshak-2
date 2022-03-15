package pt.up.fc.dcc.mooshak.rest.auth.security;

import java.security.Principal;

/**
 * Implementation of the {@link Principal} with an associated {@link AuthTokenDetails}
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class AuthUserDetails implements Principal {
	
	private final String username;
	private final AuthTokenDetails tokenDetails;

	public AuthUserDetails(String username, AuthTokenDetails tokenDetails) {
		this.username = username;
		this.tokenDetails = tokenDetails;
	}

	@Override
	public String getName() {
		return username;
	}

	public AuthTokenDetails getTokenDetails() {
		return tokenDetails;
	}
}
