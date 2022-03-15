package pt.up.fc.dcc.mooshak.rest.exception;

/**
 * Exception thrown when user is not authorized to access the resource
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class ForbiddenException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ForbiddenException() {
		super("You don't have enough permissions to perform this action.");
	}

	public ForbiddenException(String message, Throwable cause) {
		super(message, cause);
	}

	public ForbiddenException(String message) {
		super(message);
	}
}
