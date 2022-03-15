package pt.up.fc.dcc.mooshak.rest.exception;

/**
 * Exception thrown when something is bad with the request
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public BadRequestException() {
		super("Something was wrong with your request.");
	}

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
