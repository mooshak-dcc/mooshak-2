package pt.up.fc.dcc.mooshak.client.guis.creator;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;

/** 
 * A token containing information on commands and their arguments.
 * Token are stringified to be saved in GWT history
 */
class Token {
	
	public enum Command { TOP, VIEW, ADD };
	
	private static final String SEP = ":";
	
	Command command = null;
	Kind kind = null;
	String id = "";
	String name = "";
	
	Token() {}
	
	Token(String token) {
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
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
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
		
		return commandValue+SEP+kindValue+SEP+id+SEP+name;
	}
	
	/**
	 * Parse a token stringification  
	 * @param token
	 */
	private void parse(String token) {
		String[] args = token.split(SEP);	
		
		if(args.length > 0 && args[0].length() > 0)
			command = Enum.valueOf(Command.class, args[0]);
		if(args.length > 1 && args[1].length() > 0)
			kind = Enum.valueOf(Kind.class, args[1]);
		if(args.length > 2)
			id = args[2];
		if(args.length > 3)
			name = args[3];
	}

}
