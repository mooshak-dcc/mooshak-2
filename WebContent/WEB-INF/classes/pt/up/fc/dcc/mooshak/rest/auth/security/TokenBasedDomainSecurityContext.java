package pt.up.fc.dcc.mooshak.rest.auth.security;

import java.security.Principal;

import javax.ws.rs.core.SecurityContext;

import pt.up.fc.dcc.mooshak.rest.auth.utils.JWTToken;

/**
 * Implementation of {@link SecurityContext} for token-based domain-related auth
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TokenBasedDomainSecurityContext implements SecurityContext {
	private final AuthUserDetails userDetails;
	private final boolean secure;

	public TokenBasedDomainSecurityContext(AuthUserDetails userDetails, boolean secure) {
		this.userDetails = userDetails;
		this.secure = secure;
	}

	@Override
	public Principal getUserPrincipal() {
		return userDetails;
	}

	@Override
	public boolean isUserInRole(String role) {
		return role.equalsIgnoreCase(userDetails.getTokenDetails().getRole());
	}
	
	/**
	 * Check if user has a role in a certain domain
	 * 
	 * @param domain {@link String} Domain to check role
	 * @param role {@link String} role
	 * @return {@code boolean} <code>true</code> if user has role, <code>false</code> otherwise
	
	public boolean hasRole(String domain, String role) {
		
		if (tokenDetails.getDomain() == null || (domain != null && domain.equals(tokenDetails.getDomain()))) 
			return role.equals(tokenDetails.getRole());
		return false;
	} */

	@Override
	public boolean isSecure() {
		return secure;
	}

	@Override
	public String getAuthenticationScheme() {
		return JWTToken.TOKEN_TYPE;
	}
}
