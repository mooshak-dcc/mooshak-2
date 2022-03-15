package pt.up.fc.dcc.mooshak.shared.commands;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * The outcome of executing a Mooshak method/operation,
 * including type GUI update and message to display 
 *
 * This class must be also Serializable (instead of just IsSerializable)
 * since it is used both by RPC and RMI (and IsSerializable is
 * not serializable...)
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class CommandOutcome implements IsSerializable {
	
	String message = null;
	String continuation = null;
	MethodContext context = null;
	
	/**
	 * An entry in a method context
	 *
	 * @author José Paulo Leal <zp@dcc.fc.up.pt>
	 */
	public static class Pair implements IsSerializable {

		String name;
		String value;
		
		public Pair() {};
		
		public Pair(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}
	}
	
	public CommandOutcome() {}

	public CommandOutcome(String message) {
		this.message = message;
	}


	/**
	 * Get message to display after command execution
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * Set message to display after command execution
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	
	/**
	 * Get name continuation method (next in wizard) for this outcome
	 * @return the continuation
	 */
	public String getContinuation() {
		return continuation;
	}

	/**
	 * Set continuation method (next in wizard) for this outcome
	 * @param continuation the continuation to set
	 */
	public void setContinuation(String continuation) {
		this.continuation = continuation;
	}

	/**
	 * Add a name value pair to the context of the continuation
	 * @param name
	 * @param value
	 */
	public void addPair(String name,String value) {
		if(context == null)
			context = new MethodContext();
		
		context.addPair(name,value);
	}

	/**
	 * Return continuation method context, or {@code null} if none defined
	 * @return
	 */
	public MethodContext getContext() {
		return context;
	}
}
