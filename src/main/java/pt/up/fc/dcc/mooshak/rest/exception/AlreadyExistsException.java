package pt.up.fc.dcc.mooshak.rest.exception;

/**
 * Exception thrown when an object already exists
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class AlreadyExistsException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public AlreadyExistsException() {
		super();
	}

	public AlreadyExistsException(String message, Throwable cause) {
		super(message, cause);
	}

	public AlreadyExistsException(String message) {
		super(message);
	}
}
