package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class MooshakTypeException extends MooshakException {

	private static final long serialVersionUID = 1L;

	public MooshakTypeException() {
	}

	public MooshakTypeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MooshakTypeException(String message) {
		super(message);
	}

	public MooshakTypeException(Throwable cause) {
		super(cause);
	}

}
