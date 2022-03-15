package pt.up.fc.dcc.mooshak.rest.exception;

/**
 * Exception thrown when no user is authenticated
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class UnauthenticatedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnauthenticatedException() {
		super("You must be authenticated to perform this action.");
	}

	public UnauthenticatedException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnauthenticatedException(String message) {
		super(message);
	}
}
