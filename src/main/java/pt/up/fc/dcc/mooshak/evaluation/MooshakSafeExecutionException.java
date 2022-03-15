package pt.up.fc.dcc.mooshak.evaluation;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * An exception raised during a safe execution, i.e. an execution
 * within a sand box 
 * 
 * * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * 
 *          				 June  2012
 */
public class MooshakSafeExecutionException extends MooshakException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MooshakSafeExecutionException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public MooshakSafeExecutionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public MooshakSafeExecutionException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public MooshakSafeExecutionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
