package pt.up.fc.dcc.mooshak.client.guis.icpc;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;

/** 
 * A token containing information on commands and their arguments.
 * Token are stringified to be saved in GWT history
 */
public class Token extends pt.up.fc.dcc.mooshak.client.gadgets.Token {
	
	public enum Command { TOP, VIEW, PROGRAM, ASK, ANSWERED,
		LISTING};
	
	private static final String SEP = ":";
	
	Command command = null;
	Kind kind = null;
	
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
	 * @return the kind of listing
	 */
	public Kind getKind() {
		return kind;
	}

	/**
	 * @param kind the kind of listing to set
	 */
	public void setKind(Kind kind) {
		this.kind = kind;
	}

	/**
	 * Stringification compatible with parse (used in constructor)
	 */
	public String toString() {
		String commandValue = "";
		String kindValue = "";

		if(command != null)
			commandValue = command.toString();

		if(kind != null)
			kindValue = kind.toString();
		
		return commandValue+SEP+kindValue+SEP+getId()+SEP+getName();
	}
	
	/**
	 * Parse a token stringification  
	 * @param token
	 */
	protected void parse(String token) {
		String[] args = token.split(SEP);	
		
		if(args.length > 0 && args[0].length() > 0)
			command = Enum.valueOf(Command.class, args[0]);
		if(args.length > 1 && args[1].length() > 0)
			kind = Enum.valueOf(Kind.class, args[1]);
		if(args.length > 2)
			setId(args[2]);
		if(args.length > 3)
			setName(args[3]);
	}

}
