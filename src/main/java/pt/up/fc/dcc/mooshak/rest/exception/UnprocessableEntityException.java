package pt.up.fc.dcc.mooshak.rest.exception;

/**
 * Exception thrown when the entity passed as input is incorrect
 * 
 * @author José Carlos Paiva <josepaiva94@gmail.com>
 */
public class UnprocessableEntityException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public UnprocessableEntityException() {
		super("The entity passed as input is invalid.");
	}

	public UnprocessableEntityException(String message) {
		super(message);
	}

	public UnprocessableEntityException(String message, Throwable cause) {
		super(message, cause);
	}
}
