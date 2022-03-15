package pt.up.fc.dcc.mooshak.rest.exception;

/**
 * Exception thrown when an error related to Mooshak server occurs
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class InternalServerException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InternalServerException() {
		super("Mooshak was unable to complete your request.");
	}

	public InternalServerException(String message) {
		super(message);
	}

	public InternalServerException(String message, Throwable cause) {
		super(message, cause);
	}
}
