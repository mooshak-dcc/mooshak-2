package pt.up.fc.dcc.mooshak.rest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.rest.auth.security.AuthUserDetails;

public class Resource {

	protected static final Logger LOGGER = Logger.getLogger(Resource.class.getName());

	@Context
	protected SecurityContext securityContext;

	@Context
	protected UriInfo uriInfo;

	@Context
	protected Request request;

	public Resource() {
	}

	public Resource(SecurityContext securityContext, UriInfo uriInfo, Request request) {
		this.securityContext = securityContext;
		this.uriInfo = uriInfo;
		this.request = request;
	}

	/**
	 * Get session from current user
	 * 
	 * @return {@link Session} session from current user
	 */
	protected Session getSession() {
		AuthUserDetails userDetails = (AuthUserDetails) securityContext.getUserPrincipal();
		return userDetails.getTokenDetails().getSession();
	}

	protected static final Pattern INVALID_CHARS_IN_PATH_SEGMENT = Pattern.compile("/|\\.\\.");

	/**
	 * Remove all characters in argument that may be exploited when this string
	 * is used as a path segment such as slashes ("/") or double dots ("..")
	 * 
	 * @param pathSegment
	 * @return sanitized path segment
	 */
	protected static String sanitizePathSegment(String pathSegment) {

		return INVALID_CHARS_IN_PATH_SEGMENT.matcher(pathSegment).replaceAll("");
	}

	protected final static Pattern INVALID_CHARS_IN_ID = Pattern.compile("^(../)*/?");

	/**
	 * Remove all characters in argument that may be exploited when this is used
	 * as a relative path (id of a persistent object) such as series of leading
	 * double dots and slashes.
	 * 
	 * @param path
	 *            string including slashes, dots and double-dots
	 * @return string without leading double-dots and slashes
	 */
	protected static String sanitizePathId(String path) {
		return INVALID_CHARS_IN_ID.matcher(path).replaceAll("");
	}

	/**
	 * Read contents from an {@link InputStream} to {@code byte[]}
	 * 
	 * @param is
	 *            {@link InputStream} to read from
	 * @return {@code byte[]} result
	 * @throws IOException
	 */
	public static byte[] readInputStreamToByteArray(InputStream is) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		byte[] contents = result.toByteArray();
		result.close();
		return contents;
	}

}
