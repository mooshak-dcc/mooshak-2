package pt.up.fc.dcc.mooshak.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Mooshak main exception type. This type is meant to be 
 * extended for exception to be handled locally. 
 * Mooshak exceptions without a specialized type will be
 * sent to clients or recorded in logs. 
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 *
 */
public class MooshakException extends Exception implements IsSerializable {
	private static final long serialVersionUID = 1L;

	public MooshakException() {
		// TODO Auto-generated constructor stub
	}

	public MooshakException(String message) {
		super(message);
	}

	public MooshakException(Throwable cause) {
		super(cause);
	}

	public MooshakException(String message, Throwable cause) {
		super(message, cause);
	}

}
