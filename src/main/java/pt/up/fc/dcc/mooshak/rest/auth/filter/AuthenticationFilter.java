package pt.up.fc.dcc.mooshak.rest.auth.filter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.rest.auth.security.AuthTokenDetails;
import pt.up.fc.dcc.mooshak.rest.auth.security.AuthUserDetails;
import pt.up.fc.dcc.mooshak.rest.auth.security.TokenBasedDomainSecurityContext;
import pt.up.fc.dcc.mooshak.rest.auth.utils.JWTToken;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.UnauthenticatedException;

/**
 * JWT authentication filter
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	
	private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {
		
		String authorizationHeader = ctx.getHeaderString(HttpHeaders.AUTHORIZATION);
        LOGGER.info("#### authorizationHeader : " + authorizationHeader);
        
        if (authorizationHeader != null) {
        	
        	if (authorizationHeader.startsWith(JWTToken.TOKEN_TYPE + " ")) {
				
		        String token = authorizationHeader.substring(JWTToken.TOKEN_TYPE.length()).trim();
		        
		        AuthTokenDetails tokenDetails;
		        try {
		            tokenDetails = JWTToken.getInstance().parse(token);
		            LOGGER.info("#### valid token : " + token);
		        } catch (NoSuchAlgorithmException | MooshakContentException e) {
		        	LOGGER.severe("#### invalid token : " + e.getMessage());
		        	throw new InternalServerException(e.getMessage(), e);
		        }
		            
	            boolean secure = ctx.getSecurityContext().isSecure();
	            AuthUserDetails userDetails = new AuthUserDetails(tokenDetails.getUsername(), 
	            		tokenDetails); 
	            
	            TokenBasedDomainSecurityContext securityContext = new TokenBasedDomainSecurityContext(userDetails, secure);
	            ctx.setSecurityContext(securityContext);
        		
        	} else {
	            LOGGER.severe("#### invalid authorizationHeader : " + authorizationHeader);
	            throw new UnauthenticatedException("Invalid authorization header.");
        	}
        }
	}
}
