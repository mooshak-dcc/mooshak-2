package pt.up.fc.dcc.mooshak.content;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class MooshakContentException extends MooshakException {

	private static final long serialVersionUID = 1L;

	public MooshakContentException() {
		super();
	}

	public MooshakContentException(String message, Throwable cause) {
		super(message, cause);
	}

	public MooshakContentException(String message) {
		super(message);
	}

	public MooshakContentException(Throwable cause) {
		super(cause);
	}

}
