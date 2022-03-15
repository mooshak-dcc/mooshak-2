package pt.up.fc.dcc.mooshak.rest.auth;

import java.security.NoSuchAlgorithmException;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.rest.auth.model.AuthToken;
import pt.up.fc.dcc.mooshak.rest.auth.model.EnrollContestRequest;
import pt.up.fc.dcc.mooshak.rest.auth.model.UserCredentials;
import pt.up.fc.dcc.mooshak.rest.auth.security.AuthTokenDetails;
import pt.up.fc.dcc.mooshak.rest.auth.security.AuthUserDetails;
import pt.up.fc.dcc.mooshak.rest.auth.security.NotSecured;
import pt.up.fc.dcc.mooshak.rest.auth.utils.JWTToken;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.UnauthenticatedException;
import pt.up.fc.dcc.mooshak.rest.exception.UnprocessableEntityException;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

@Path("/auth")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
public class AuthenticationResource {

	private static final Logger LOGGER = Logger.getLogger(AuthenticationResource.class.getName());
	
	@Context
	private SecurityContext securityContext;
	
	@Context
	private UriInfo uriInfo;
	
	@NotSecured
	@POST
	@Path("/login")
	public Response login(UserCredentials userCredentials) {
		
		if (userCredentials.getUsername() == null)
			throw new UnprocessableEntityException("Username is required.");
		if (userCredentials.getPassword() == null)
			throw new UnprocessableEntityException("Password is required.");

		LOGGER.info("#### login/password : " + userCredentials.getUsername() + "/" + userCredentials.getPassword());

		// Authenticate the user using the credentials provided
		Session session;
		try {
			session = AuthManager.getInstance().authenticate(userCredentials.getContest(), userCredentials.getUsername(), 
					userCredentials.getPassword());
		} catch (MooshakException e) {
			throw new UnauthenticatedException("Authentication failed. Reason: " + e.getMessage(), e);
		}
		
		// Issue a token for the user
		String tokenStr;
		try {
			tokenStr = JWTToken.getInstance().issue(uriInfo.getAbsolutePath().toString(), session);
		} catch (MooshakContentException | NoSuchAlgorithmException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
			
		AuthToken token = new AuthToken();
		token.setToken(tokenStr);

		// Return the token on the response
		return Response.ok(token).build();
	}
	
	@NotSecured
	@POST
	@Path("/enroll")
	public Response enroll(EnrollContestRequest req) {

		if (req.getContest() == null)
			throw new UnprocessableEntityException("Domain is required.");
		if (req.getUsername() == null)
			throw new UnprocessableEntityException("Username is required.");
		if (req.getPassword() == null)
			throw new UnprocessableEntityException("Password is required.");
		if (req.getName() == null)
			throw new UnprocessableEntityException("Name is required.");
		
		try {
			AuthManager.getInstance().register(req.getContest(), req.getUsername(), req.getPassword(), req.getName());
		} catch (MooshakException e) {
			throw new UnauthenticatedException("Enrolment failed. Reason: " + e.getMessage(), e);
		}

		// Authenticate the user using the credentials provided
		Session session;
		try {
			session = AuthManager.getInstance().authenticate(req.getContest(), req.getUsername(), 
					req.getPassword());
		} catch (MooshakException e) {
			throw new UnauthenticatedException("Authentication failed. Reason: " + e.getMessage(), e);
		}
		
		// Issue a token for the user
		String tokenStr;
		try {
			tokenStr = JWTToken.getInstance().issue(uriInfo.getAbsolutePath().toString(), session);
		} catch (MooshakContentException | NoSuchAlgorithmException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
			
		AuthToken token = new AuthToken();
		token.setToken(tokenStr);

		// Return the token on the response
		return Response.ok(token).build();
	}
	
	@POST
	@Path("/refresh")
	public Response refresh() {
		
		AuthTokenDetails tokenDetails = ((AuthUserDetails) securityContext.getUserPrincipal()).getTokenDetails();
		
		LOGGER.info("#### refresh token : " + tokenDetails.getId());
		
		// Refresh token of the user
		String tokenStr;
		try {
			tokenStr = JWTToken.getInstance().refresh(tokenDetails);
		} catch (NoSuchAlgorithmException | MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}

		AuthToken token = new AuthToken();
		token.setToken(tokenStr);

		// Return the token on the response
		return Response.ok(token).build();
	}
}
