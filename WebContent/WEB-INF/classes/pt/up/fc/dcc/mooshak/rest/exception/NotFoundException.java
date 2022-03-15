package pt.up.fc.dcc.mooshak.rest.exception;

/**
 * Exception thrown when an object does not exists
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(String message) {
		super(message);
	}
}
