package pt.up.fc.dcc.mooshak.rest.auth.filter;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import pt.up.fc.dcc.mooshak.rest.auth.security.AuthUserDetails;
import pt.up.fc.dcc.mooshak.rest.auth.security.EveryoneLocked;
import pt.up.fc.dcc.mooshak.rest.auth.security.NotSecured;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.ForbiddenException;
import pt.up.fc.dcc.mooshak.rest.exception.UnauthenticatedException;

/**
 * JWT authorization filter
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {
	public static final String PATH_PARAM_CONTEST_KEY = "contestId";

	private static final Logger LOGGER = Logger.getLogger(AuthorizationFilter.class.getName());

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext ctx) throws IOException {

		MultivaluedMap<String, String> pathParam = ctx.getUriInfo().getPathParameters();

		String domain = null;
		if (pathParam.containsKey(PATH_PARAM_CONTEST_KEY))
			domain = pathParam.get(PATH_PARAM_CONTEST_KEY).get(0);

		Method method = resourceInfo.getResourceMethod();

		// @EveryoneLocked on the method takes precedence over @Secured and
		// @NotSecured
		if (method.isAnnotationPresent(EveryoneLocked.class)) {
			throw new ForbiddenException();
		}

		// @Secured on the method takes precedence over @NotSecured
		Secured secured = method.getAnnotation(Secured.class);
		if (secured != null) {
			authorize(ctx, domain, secured.value());
			return;
		}

		// @NotSecured on the method takes precedence over @Secured on the class
		if (method.isAnnotationPresent(NotSecured.class)) {
			return;
		}

		// @EveryoneLocked can't be attached to classes

		// @Secured on the class takes precedence over @NotSecured on the class
		secured = resourceInfo.getResourceClass().getAnnotation(Secured.class);
		if (secured != null) {
			authorize(ctx, domain, secured.value());
		}

		// @NotSecured on the class
		if (resourceInfo.getResourceClass().isAnnotationPresent(NotSecured.class)) {
			return;
		}

		// Authentication is required for non-annotated methods
		if (!isAuthenticated(ctx)) {
			LOGGER.severe("#### not authenticated");
			throw new UnauthenticatedException("Authentication is required to perform this action.");
		}

		authorize(ctx, domain, Role.values());
	}

	private void authorize(ContainerRequestContext ctx, String domain, Role[] rolesAllowed) {
		authorize(ctx, domain, Arrays.asList(rolesAllowed).stream().map(Role::toString).collect(Collectors.toList())
				.toArray(new String[rolesAllowed.length]));
	}

	private void authorize(ContainerRequestContext ctx, String domain, String[] rolesAllowed) {

		if (rolesAllowed.length > 0 && !isAuthenticated(ctx))
			throw new UnauthenticatedException();
		
		if (domain != null)	// check if domain is the same as the session domain
			checkDomain(ctx, domain);

		AuthUserDetails userDetails = (AuthUserDetails) ctx.getSecurityContext().getUserPrincipal();
		for (String role : rolesAllowed) {

			if (ctx.getSecurityContext().isUserInRole(role)) {
				userDetails.getTokenDetails().getSession().setLastUsed(new Date());
				return;
			}
		}

		LOGGER.severe("#### not authorized");
		throw new ForbiddenException();
	}

	private void checkDomain(ContainerRequestContext ctx, String domain) {
		AuthUserDetails userDetails = (AuthUserDetails) ctx.getSecurityContext().getUserPrincipal();

		String sessionDomain = userDetails.getTokenDetails().getSession().getContestId();
		if (sessionDomain == null)
			return;

		if (!sessionDomain.equals(domain))
			throw new ForbiddenException();
	}

	/**
	 * Check if user is authenticated
	 * 
	 * @param ctx
	 *            {@link ContainerRequestContext} container request context
	 * @return {@code boolean} <code>true</code> if user is authenticated,
	 *         <code>false</code> otherwise
	 */
	private boolean isAuthenticated(ContainerRequestContext ctx) {
		return ctx.getSecurityContext().getUserPrincipal() != null;
	}
}
