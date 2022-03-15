package pt.up.fc.dcc.mooshak.rest.filter;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.ext.Provider;

@Provider
@PreMatching
public class OverrideHttpMethodFilter implements ContainerRequestFilter {

	private static final String HEADER_X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";
	private static final String HEADER_X_HTTP_METHOD = "X-HTTP-Method";
	private static final String HEADER_X_METHOD_OVERRIDE = "X-Method-Override";

	private static final String METHOD_GET = "GET";
	private static final String METHOD_POST = "POST";
	private static final String METHOD_PUT = "PUT";
	private static final String METHOD_DELETE = "DELETE";
	private static final String METHOD_PATCH = "PATCH";
	private static final String METHOD_OPTIONS = "OPTIONS";
	private static final String METHOD_HEAD = "HEAD";

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		String receivedMethod = requestContext.getMethod();
		String newMethod = requestContext
				.getHeaderString(HEADER_X_HTTP_METHOD_OVERRIDE);
		
		if (newMethod == null)
			newMethod = requestContext.getHeaderString(HEADER_X_HTTP_METHOD);
		
		if (newMethod == null)
			newMethod = requestContext.getHeaderString(HEADER_X_METHOD_OVERRIDE);
		
		if (receivedMethod != null && newMethod != null
				&& !receivedMethod.equals(newMethod))
			overrideHttpMethodIfPossible(requestContext, receivedMethod, newMethod);
	}

	/**
	 * Override HTTP method, if possible
	 * 
	 * @param requestContext {@link ContainerRequestContext} context of the request
	 * @param oldMethod {@link String} received HTTP method
	 * @param newMethod {@link String} override HTTP method
	 */
	private void overrideHttpMethodIfPossible(ContainerRequestContext requestContext,
			String oldMethod, String newMethod) {
		
		switch (oldMethod.toUpperCase()) {
		case METHOD_POST:
			
			if (
					newMethod.equalsIgnoreCase(METHOD_DELETE) || 
					newMethod.equalsIgnoreCase(METHOD_PUT) || 
					newMethod.equalsIgnoreCase(METHOD_PATCH))
				requestContext.setMethod(newMethod);
			
			break;

		default:
			break;
		}
	}
}