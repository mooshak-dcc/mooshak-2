package pt.up.fc.dcc.mooshak.client.guis.kora;

/** 
 * A token containing information on commands and their arguments.
 * Token are stringified to be saved in GWT history
 */
public class Token extends pt.up.fc.dcc.mooshak.client.gadgets.Token {
	
	public enum Command { TOP };
	
	private static final String SEP = ":";
	
	Command command = null;
	
	public Token() {}
	
	public Token(String token) {
		if(token != null) 
			parse(token);
	}
	

	/**
	 * @return the command
	 */
	public Command getCommand() {
		return command;
	}

	/**
	 * @param command the command to set
	 */
	public void setCommand(Command command) {
		this.command = command;
	}

	/**
	 * Stringification compatible with parse (used in constructor)
	 */
	public String toString() {
		String commandValue = "";

		if(command != null)
			commandValue = command.toString();
		
		return commandValue+SEP+SEP+getId()+SEP+getName();
	}
	
	/**
	 * Parse a token stringification  
	 * @param token
	 */
	protected void parse(String token) {
		String[] args = token.split(SEP);	
		
		if(args.length > 0 && args[0].length() > 0)
			command = Enum.valueOf(Command.class, args[0]);
		if(args.length > 1)
			setId(args[1]);
		if(args.length > 2)
			setName(args[2]);
	}

}
