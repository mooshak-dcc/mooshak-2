package pt.up.fc.dcc.mooshak.evaluation.graph;

public class GraphEvalException extends Exception {
	private static final long serialVersionUID = 1L;

	public GraphEvalException() {
		super();
	}

	public GraphEvalException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public GraphEvalException(String message, Throwable cause) {
		super(message, cause);
	}

	public GraphEvalException(String message) {
		super(message);
	}

	public GraphEvalException(Throwable cause) {
		super(cause);
	}
}
