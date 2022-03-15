package pt.up.fc.dcc.mooshak.content.types;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;

public class LDAP extends PersistentObject {
	private static final long serialVersionUID = 1L;

	private static final String[] STANDARD_ARGUMENTS =  { "-LLL", "-x"};
	
	private static final Logger LOGGER = Logger.getLogger("");
	
	@MooshakAttribute( 
			name="Fatal", 
			type=AttributeType.LABEL)
	private String fatal = null;
	
	@MooshakAttribute( 
			name="Warning",
			type=AttributeType.LABEL)
	private String warning = null;
	
	@MooshakAttribute(
			name="Command",
			tip="Command for executing LDAP queries (e.g. /usr/bin/ldapsearch)")
	private String command = null;
	
	@MooshakAttribute(
			name="Host",
			tip="Host name (or IP) of the LDAP server")
	private String host = null;
	
	@MooshakAttribute(
			name="Bind_DN",
			tip="Distinguished Name to bind LDAP directory")
	private String bindDN = null;
	
	@MooshakAttribute(
			name="Base_DN",
			tip="Starting point for the search instead of the default")
	private String baseDN = null;

	@MooshakAttribute(
			name="Login_Attribute",
			tip="LDAP attribute containing login id (default: uid)")
	private String loginAttribute = null;
	
	/*-------------------------------------------------------------------*\
	 * 		            Operations                                       *
	\*-------------------------------------------------------------------*/
		
	
	/**
	 * Tests if connection to LDAP server is up and running
	 * Searches for "nobody" with password "?" and checks if
	 * "ldap_bind:" is in error message
	 * 
	 * @return
	 */
	@MooshakOperation(
			name="Test",
			inputable=false,
			tip="Tests if connection to LDAP server is up and running")
	 CommandOutcome test() {	
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			searchHost("uid","nobody","userPassword",null);
		} catch (IOException | MooshakException | InterruptedException e) {
			if(e.getMessage().contains("ldap_bind:"))				
				outcome.setMessage("LDAP server is reachable");
			else
				outcome.setMessage("Unexpected output from LDAP server:"+
						e.getMessage());
		}
		
		return outcome;
	}
	
	 /*-------------------------------------------------------------------*\
	  * 	Setters and getters				                              *
	 \*-------------------------------------------------------------------*/
	

	/**
	 * Set LDAP command
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * get LDAP command
	 * 
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	
	/**
	 * Get LDAP host
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Set LDAP host
	 * 
	 * @param host the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Get bind distinguished name
	 * 
	 * @return the bindDN
	 */
	public String getBindDN() {
		return bindDN;
	}

	/**
	 * Set bind distinguished name
	 * 
	 * @param bindDN the bindDN to set
	 */
	public void setBindDN(String bindDN) {
		this.bindDN = bindDN;
	}

	/**
	 * Get base distinguished name
	 * 
	 * @return the baseDN
	 */
	public String getBaseDN() {
		return baseDN;
	}

	/**
	 * Set base distinguished name
	 * @param baseDN the baseDN to set
	 */
	public void setBaseDN(String baseDN) {
		this.baseDN = baseDN;
	}

	/**
	 * Get login attribute, 
	 * an LDAP attribute used for login instead of {@code uid}
	 * 
	 * @return the loginAttribute
	 */
	public String getLoginAttribute() {
		return loginAttribute;
	}

	/**
	 * Set login attribute, 
	 * an LDAP attribute used for login instead of {@code uid}
	 * 
	 * @param loginAttribute the loginAttribute to set
	 */
	public void setLoginAttribute(String loginAttribute) {
		this.loginAttribute = loginAttribute;
	}

	/* (non-Javadoc)
	 * @see pt.up.fc.dcc.mooshak.content.PersistentObject#backup()
	 */
	@Override
	protected boolean updated() throws MooshakContentException {
		super.backup();
		
		fatal = "";
		if(command != null && ! "".equals(command = command.trim())) {
		
			if(! Files.isExecutable(Paths.get(command)))
					fatal = command +" is not executable";
				
		}
		
		return false;
	}
	
		
	/**
	 * Authenticate user in this LDAP server
	 * @param user
	 * @param password
	 * @return
	 */
	public boolean authenticate(String user, String password) {
		String hash = null;
		String uid = null;
		
		if(loginAttribute != null && ! "".equals(loginAttribute)) 
			try {	
				uid = searchHost(loginAttribute,user,"uid",null);
			} catch(Exception e) {
				uid = "nobody";
			}
		else
			uid = user;
		
		try {
			hash = searchHost("uid",uid,"userPassword",password);
		} catch(Exception cause) {
			LOGGER.log(Level.INFO,"Autentication failed for user:"+user);
		}
		
		return hash != null;
	}
	
	/**
	 * Searches LDAP host for entry with attribute=value to return request
	 *  (optionally pass password for authentication)
	 *  Returns hash if user is authenticated and {@code null} otherwise
	 *   
	 * @param attribute
	 * @param value
	 * @param request
	 * @param password
	 * @return
	 * @throws IOException
	 * @throws MooshakException
	 * @throws InterruptedException
	 */
	String searchHost(
			String attribute,
			String value, 
			String request,
			String password) throws 
			IOException, MooshakException, InterruptedException {
		
		List<String> line = new ArrayList<>();
		ProcessBuilder builder = new ProcessBuilder();
		String hash = null;
		
		if(command == null || "".equals(command.trim()))
			throw new MooshakException("LDAP command undefined");
		
		for (String token: command.split("\\s+"))
			line.add(token);
		
		for(String argument: STANDARD_ARGUMENTS)
			line.add(argument);
		if("uid".equals(attribute)) {
			line.add("-D");
			line.add(String.format("uid=%s,%s",value,bindDN));
		}
		line.add("-b");		line.add(baseDN);
		line.add("-h");		line.add(host);
		if(password != null) {		line.add("-w"); 	line.add(password); }
		line.add(String.format("%s=%s",attribute,value));
		line.add(request);
		
		builder.command(line);
		
		Process process = builder.start();
		
		if((process.waitFor()) != 0) {
			List<String> message = new ArrayList<>();
			
			message.add("Error in command \n");
			message.addAll(line);
			message.add("\n");
			message.addAll(readLines(process.getErrorStream()));
			
			throw new MooshakException(String.join(" ",message));
		}
		
		try(InputStream stream = process.getInputStream()) {
			List<String> lines = readLines(stream);
			
			if(lines.size() > 1) {
				String words[] = lines.get(1).split("\\s+");
				
				if(words.length > 1)
					hash = words[1];
			}
		}
		
		return hash;
	}
	
	/**
	 * Reads lines from a stream (avoid {@code org.apache.commons.io.IOUtils)})
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 */
	private List<String> readLines(InputStream stream) throws IOException {
		List<String> lines = new ArrayList<>();
		
		try (BufferedReader reader = 
				new BufferedReader(new InputStreamReader(stream))) {
			String line;
			while((line = reader.readLine()) != null)
				lines.add(line);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lines;
	}
		
}