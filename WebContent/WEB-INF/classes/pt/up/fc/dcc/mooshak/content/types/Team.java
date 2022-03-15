package pt.up.fc.dcc.mooshak.content.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Group.AuthenticationType;
import pt.up.fc.dcc.mooshak.content.util.ClassificationStringUtils;
import pt.up.fc.dcc.mooshak.content.util.Password;
import pt.up.fc.dcc.mooshak.content.util.RandomStringGenerator;
import pt.up.fc.dcc.mooshak.content.util.StringEscapeUtils;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy;
import pt.up.fc.dcc.mooshak.print.PrinterController;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Modeling teams (or students). Teams may aggregate several persons
 * Instances of this class are persisted locally
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 * Recoded in Java in         June  2012
 * From a Tcl module coded in April 2001
 */
public class Team extends PersistentContainer<Person> implements Authenticable,
	UseTransactions {
	private static final long serialVersionUID = 1L;

	private static final String PROFILES = "data/configs/profiles";
	
	@MooshakAttribute( 
			name="Fatal", 
			type=AttributeType.LABEL)
	private String fatal = null;
	
	@MooshakAttribute( 
			name="Warning",
			type=AttributeType.LABEL)
	private String warning = null;
	
	
    @MooshakAttribute( 
    		name="Name",
    		tip="Team long name (this is NOT the team ID)")
    private String name = null;
    
    @MooshakAttribute( 
    		name="Password", 
    		type=AttributeType.PASSWORD,
    		tip="Password for authentication (with team ID: folder name)")
    private String password = null;
    
    @MooshakAttribute( 
    		name="Email",
    		tip="Email address for contact")
    private String email = null;
    
    @MooshakAttribute( 
    		name="Location",
    		tip="Location in lab where team is competing")
    private String location = null;
    
    @MooshakAttribute( 
    		name="Qualifies", 
    		type=AttributeType.MENU,
    		tip="Team qualifies for this contest or is in a public contest?")
    private MooshakAttribute.YesNo qualifies = MooshakAttribute.YesNo.YES;
    
    @MooshakAttribute( 
    		name="Start", 
    		type=AttributeType.DATE,
    		tip="Date / time when team logged in")
    private Date start = null;
    
    @MooshakAttribute( 
    		name="Rank", 
    		type=AttributeType.INTEGER,
    		tip="Team position in final ranking")
    private Integer rank = null;
    
    @MooshakAttribute( 
    		name="Profile",
    		type=AttributeType.HIDDEN)
    private String profile = "team";
    
    @MooshakAttribute( 
    		name="PrintoutsTransactions", 
    		type=AttributeType.INTEGER,
    		tip="Number of printouts transactions used")
    private Integer printoutsTransactions = 0;
    
    @MooshakAttribute( 
    		name="SubmissionsTransactions", 
    		type=AttributeType.INTEGER,
    		tip="Number of submissions transactions used")
    private Integer submissionsTransactions = 0;
    
    @MooshakAttribute( 
    		name="ValidationsTransactions", 
    		type=AttributeType.INTEGER,
    		tip="Number of validations transactions used")
    private Integer validationsTransactions = 0;
    
    @MooshakAttribute( 
    		name="BalloonsTransactions", 
    		type=AttributeType.INTEGER,
    		tip="Number of balloons transactions used")
    private Integer balloonsTransactions = 0;
    
    @MooshakAttribute( 
    		name="QuestionsTransactions", 
    		type=AttributeType.INTEGER,
    		tip="Number of questions transactions used")
    private Integer questionsTransactions = 0;

    
    @MooshakAttribute(
			name="Person",
			type=AttributeType.CONTENT)
	private Void person;	


	 /*-------------------------------------------------------------------*\
     *		            life cycle events                                 *
	 \*-------------------------------------------------------------------*/
	 
	 @Override
	 protected void created() {
		 updateRankings();
	 }
	 
	 @Override
	 protected boolean updated() {

		 try {
			((Groups) getGrandParent()).removeCached(getIdName());
		} catch (MooshakContentException e) {
			Logger.getLogger("").log(Level.SEVERE,
					"Error while invalidating cache",e);
		}
		 
		 updateRankings();
		 
		 checkValues();
		 
		 return false;
	 }
	 
	 private void updateRankings() {
		 try {
			Group  group = getParent();
			Groups groups = group.getParent();
			Contest contest = groups.getParent();
			RankingPolicy ranking = contest.getSharedRankingPolicy();
			
			if(ranking != null)
				ranking.update();
			
		} catch (MooshakException cause) {
			Logger.getLogger("").log(Level.SEVERE,
					"Error while preparing to update rankings",cause);
		}
	 }

	/*-------------------------------------------------------------------*\
	 * 		            Operations                                    *
	\*-------------------------------------------------------------------*/

	@MooshakOperation(
			name="Generate Passwords",
			inputable=false,
			tip="Generate passwords for this team")
	private CommandOutcome generatePasswords() {
		CommandOutcome outcome = new CommandOutcome();
    	
    	try {
			Groups groups = getParent().getParent();
			if(groups.getPasswordTemplate() == null)
				groups.createDefaultPasswordTemplate();
			Path template = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getPasswordTemplate().getFileName().toString());
			if(!Files.exists(template))
				groups.createDefaultPasswordTemplate();
			
			if(groups.getConfig() == null)
				groups.createDefaultConfig();
			Path config = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getConfig().getFileName().toString());
			if(!Files.exists(config)) 
				groups.createDefaultConfig();
			
			printTeamPassword(template, config, false);
			
			outcome.setMessage("Printing ...");
		} catch (Exception e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}

		return outcome;
	}

	@MooshakOperation(
			name="Generate Certificates",
			inputable=false,
			tip="Generate certificates for this team")
	private CommandOutcome generateCertificates() {
		CommandOutcome outcome = new CommandOutcome();
    	
    	try {
			Groups groups = getParent().getParent();
			
			if(groups.getTeamTemplate() == null)
				groups.createDefaultTeamTemplate();
			Path teamTemplate = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getTeamTemplate().getFileName().toString());
			if(!Files.exists(teamTemplate))
				groups.createDefaultTeamTemplate();
			
			if(groups.getPersonTemplate() == null)
				groups.createDefaultPersonTemplate();
			Path template = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getPersonTemplate().getFileName().toString());
			if(!Files.exists(template))
				groups.createDefaultPersonTemplate();
			
			if(groups.getConfig() == null)
				groups.createDefaultConfig();
			Path config = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getConfig().getFileName().toString());
			if(!Files.exists(config)) 
				groups.createDefaultConfig();
			
			printCertificates(template, teamTemplate, config, false);
			
			outcome.setMessage("Printing ...");
		} catch (Exception e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}

		return outcome;
	}
	
	@MooshakOperation(
			name="View Certificates",
			inputable=false,
			tip="View the certificates")
	private CommandOutcome viewCertificates() {
		CommandOutcome outcome = new CommandOutcome();
		
		Groups groups = null; ;
		Path teamTemplate = null, template = null, config = null;
    	
    	try {
			groups = getParent().getParent();
			if(groups.getTeamTemplate() == null)
				groups.createDefaultTeamTemplate();
			teamTemplate = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getTeamTemplate().getFileName().toString());
			if(!Files.exists(teamTemplate))
				groups.createDefaultTeamTemplate();
			
			if(groups.getPersonTemplate() == null)
				groups.createDefaultPersonTemplate();
			template = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getPersonTemplate().getFileName().toString());
			if(!Files.exists(template))
				groups.createDefaultPersonTemplate();
			
			if(groups.getConfig() == null)
				groups.createDefaultConfig();
			config = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getConfig().getFileName().toString());
			if(!Files.exists(config)) 
				groups.createDefaultConfig();
		} catch (Exception e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}
		
		String content = null;
		try {
			content = printCertificates(template, teamTemplate, config, true);
		} catch (MooshakException e) {
			outcome.setMessage(e.getMessage());
			return outcome;
		}

		String filename = null;
		String filepath = null;
		try {	
			PrinterController printer = null;
			filename = "Mooshak-" + groups.getParent().getIdName() + "-certificates-" +
					getIdName() + ".pdf";
			filepath = getAbsoluteFile().toString() + File.separator + filename;		
			printer = new PrinterController(content,
					new String(Files.readAllBytes(config), "UTF-8"));
			
			printer.printToFile(new File(filepath), false, true);

			outcome.addPair("contestId", groups.getParent().getIdName());
			outcome.addPair("teamId", getIdName());
			outcome.addPair("fileName", filename);
		} catch (Exception e) {
			outcome.setMessage("Could not print file: " + e.getMessage());
			return outcome;
		}
		
		outcome.setContinuation("ViewCertificatesView");
		
		return outcome;
	}
	
	@MooshakOperation(
			name="ViewCertificatesView",
			inputable=true,
			show=false)
	private CommandOutcome viewCertificatesView(MethodContext context) {	
		return new CommandOutcome();
	}
	
	/**
	 * Print certificates of this team
	 * 
	 * @param template
	 * @param teamTemplate
	 * @param config 
	 * @return 
	 * @throws MooshakException 
	 */
	public String printCertificates(Path template, Path teamTemplate, 
			Path config, boolean toFile) throws MooshakException {
		
		String certificatesContent = "";
		for (Person person : getContent()) {
			certificatesContent += person.printCertificate(template, config, true);
			certificatesContent += "<div class=\"pagebreak\"> </div>";
		}
		
		try {
			String content = new String(Files.readAllBytes(teamTemplate));
			content = setTeamCertificateTemplateValues(content);
			
			certificatesContent += content;
			certificatesContent += "<div class=\"pagebreak\"> </div>";
		} catch (Exception e) {
			throw new MooshakException("Could NOT print certificate for team: " + getName());
		}
		
		if (toFile) 
			return certificatesContent;
		
		PrinterController printer = null;
		String filename = getAbsoluteFile().toString() + File.separator + 
				"Mooshak-" + getParent().getParent().getParent().getIdName() + "-certificates-" +
				getIdName() + ".pdf";
		try {			
			printer = new PrinterController(certificatesContent,
					new String(Files.readAllBytes(config), "UTF-8"));
			
			printer.printToFile(new File(filename), false, true);
		} catch (Exception e) {
			throw new MooshakException("Could NOT print: " + e.getMessage());
		}

		String service = ((Groups) getParent().getParent()).getPrinter();
		
		String os = System.getProperty("os.name");
		if(os.toLowerCase().indexOf("linux") != -1) {
			Process pr = null;
			try {
				String printerArg = "";
				if(!service.equals(""))
					printerArg = " -P" + service;
				pr = Runtime.getRuntime().exec("lpr" + printerArg + " -J" 
					+ "Mooshak-" + getParent().getParent().getParent().getIdName() + "-certificates-" +
						getIdName() + ".pdf" + " -p " + filename);
			} catch (IOException e) {
				throw new MooshakException("Could not send file to printer");
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			
			String result = "";
			try {
				String s;
				while ((s = in.readLine()) != null) 
					result += s;
			} catch (IOException e) {
				throw new MooshakException(e.getMessage());
			}
			
			if (!result.equals(""))
				throw new MooshakException(result);
		}
		else
			throw new MooshakException("Printing in windows is not yet supported");
		
		
		return null;
	}
	
	/**
	 * Sets the certificate template values and returns the resulting
	 * template
	 * 
	 * @param template
	 * @return
	 */
	private String setTeamCertificateTemplateValues(String template) {
		String temp;
		
		try {
			Groups groups = getParent().getParent();
			temp = template.replaceAll("\\$Rank", StringEscapeUtils.escapeHtml(
					ClassificationStringUtils.toString(groups.getCertLanguage(), getRank())));
		} catch (MooshakContentException e1) {
			temp = template.replaceAll("\\$Rank", StringEscapeUtils.escapeHtml(
					ClassificationStringUtils.toEnglishString(getRank())));
		}
		
		temp = temp.replaceAll("\\$Team", 
				StringEscapeUtils.escapeHtml(getName() != null ? getName() : getIdName()));
		
		String names = "";
		for (Person person : getContent()) {
			names += StringEscapeUtils.escapeHtml(person.getName()) + "<br/>";
		}
		temp = temp.replaceAll("\\$Names", names);
		
		try {
			Group group = ((Group) getParent());
			temp = temp.replaceAll("\\$Group", StringEscapeUtils.escapeHtml(
					group.getDesignation() != null ? group.getDesignation()
					: group.getIdName()));
		} catch (MooshakContentException e) {
			temp = temp.replaceAll("\\$Group", "?");
		}
		
		return temp;
	}
	
	/**
	 * Print password of this team
	 * 
	 * @param template
	 * @param config
	 * @return 
	 * @throws MooshakException 
	 */
	public File printTeamPassword(Path template, Path config, 
			boolean toFile) throws MooshakException {
		
		PrinterController printer = null;
		try {
			String content = getPrintableTeamPassword(template, config);
			
			printer = new PrinterController(content,
					new String(Files.readAllBytes(config), "UTF-8"));
		
			if(toFile) {
				File file = new File(getAbsoluteFile().toString() + File.separator +
					"Mooshak-" + getIdName() + "-password.pdf");
				printer.printToFile(file, false, true);
				
				return file;
			}
			else {
				File file = new File(getAbsoluteFile().toString() + File.separator +
						"Mooshak-" + getIdName() + "-password.pdf");
				printer.printToFile(file, false, true);
				
				String service = ((Groups) getParent().getParent()).getPrinter();
				
				String os = System.getProperty("os.name");
				if(os.toLowerCase().indexOf("linux") != -1) {
					Process pr = null;
					try {
						String printerArg = "";
						if(!service.equals(""))
							printerArg = " -P" + service;
						pr = Runtime.getRuntime().exec("lpr" + printerArg + " -J" + "Mooshak-" 
							+ getIdName() + "-password.pdf -p " + file.getAbsolutePath());
					} catch (IOException e) {
						throw new MooshakException("Could not send file to printer");
					}
					
					BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
					
					String result = "";
					try {
						String s;
						while ((s = in.readLine()) != null) 
							result += s;
					} catch (IOException e) {
						throw new MooshakException(e.getMessage());
					}
					
					if (!result.equals(""))
						throw new MooshakException(result);
				}
				else
					throw new MooshakException();
			}
		} catch (Exception e) {
			throw new MooshakException("Could NOT print: " + e.getMessage());
		}
		
		return null;
	}
	
	public String getPrintableTeamPassword(Path template, Path config) throws MooshakException {
		String password = null;
    	try {
    		password = RandomStringGenerator.randomAlphanumeric(6);
        	setPassword(password);
			save();
		} catch (MooshakContentException e) {
			throw new MooshakException("Could NOT set password");
		}
    	
		String content = null;
		try {
			content = new String(Files.readAllBytes(template));
			content = setPasswordTemplateValues(content, password);
		} catch (Exception e) {
			throw new MooshakException("Could NOT print: " + e.getMessage());
		}
		
		return content;
	}

	
	/**
	 * Generate a password and returns a csv line with data to login
	 * 
	 * @return 
	 * @throws MooshakException 
	 */
	public String generatePasswordCSVDataLine() throws MooshakException {
		String password = null;
    	try {
    		password = RandomStringGenerator.randomAlphanumeric(6);
        	setPassword(password);
			save();
		} catch (Exception e) {
			throw new MooshakException("Could NOT set password");
		}
    	
		return getIdName() + ";" + password;
	}
	
	/**
	 * Sets the password template values and returns the resulting
	 * template
	 * 
	 * @param template
	 * @return
	 */
	private String setPasswordTemplateValues(String template, String password) {
		String temp;
		
		try {
			temp = template.replaceAll("\\$Contest", getParent()
					.getParent().getParent().getIdName());
		} catch (MooshakContentException e) {
			temp = template.replaceAll("\\$Contest", "");
		}
		
		temp = temp.replaceAll("\\$Team", getName());
		
		try {
			temp = temp.replaceAll("\\$Group", ((Group) getParent()).getDesignation());
		} catch (MooshakContentException e) {
			temp = template.replaceAll("\\$Group", "");
		}
		
		temp = temp.replaceAll("\\$Login", getIdName());
		temp = temp.replaceAll("\\$Password", password);
		
		return temp;
	}

    
	
	//-------------------- Setters and getters ----------------------//
    
    /**
     * Get the long name of the team (this is not an ID)
     * @return
     */
    public String getName() {
    	if(name == null)
    		return "";
    	else
    		return name;
	}
    
    /**
     * Set the long name of the team; use null to revert to default
     */    
    public void setName(String name) {
    	this.name = name;
    }
    
    
    /**
	 * Fatal errors messages of this folder
	 * @return
	 */
	public String getFatal() {
		if(fatal == null)
			return "";
		else
			return fatal;

	}

	/**
	 * Set fatal error messages
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
     * Get the password of this user as an hash (not as plain text)
     * Use <code>authenticate()</code> to compare a plain text with
     * saved hash.
     * @return
     */
    public String getPassword() {
    	if(password == null)
    		return "";
    	else
    		return password;
	}
    
    /**
     * Set the password of this user as plain text.
     * Password are stored as hashes.
     * Use null to revert to default
     */    
    public void setPassword(String plain) {
    	this.password = Password.crypt(plain);
    }
    
    /**
     * Checks if teams qualifies
     * @return
     */
    public boolean isQualifies() {
    	if(qualifies == null)
    		return true;
    	else if(YesNo.YES.equals(qualifies))
    		return true;
    	else
    		return false;
    }
    
    /**
     * Set if this team qualifies
     * @param qualifies
     */
    public void setQualifies(boolean qualifies) {
    	if(qualifies)
    		this.qualifies = YesNo.YES;
    	else
    		this.qualifies = YesNo.NO;
    }
    
	/**
	 * Get date / time when team logged in
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Set date / time when team logged in
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		
		return rank == null ? 0 : rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
	}

	/**
	 * Returns time passed from the moment the team 
	 * logged for the first time
	 * 
	 * @return
	 */
	public int getSecondsFromLogin() {
		Date now = new Date();
		
		if(start != null)
			return (int) (now.getTime() - start.getTime()) / 1000; 
		else
			return (int) now.getTime() / 1000;
	}

	/**
	 * Sets login time, used in virtual contests
	 */
	public void recordLoginTime() {
		if(start == null) {
			setStart(new Date());
			
			try {
				save();
			} catch (MooshakContentException e) {}
		}
	}

	/**
	 * Authenticate user by comparing given password in clear text with
	 * stored hash. 
	 * @param password in clear text
	 * @return
	 * @throws MooshakException 
	 */
	@Override
	public boolean authentic(String plain) throws MooshakException {
		Group group = null;
		boolean authentic = false;
		
		try {
			group = getParent();
			AuthenticationType type = group.getAuthentication();
			
			switch(type) {
			case BASIC:
				if (password == null)
					throw new MooshakException("The password for this team is not set");
				authentic = Password.match(password, plain);
				break;
			case LDAP:
				LDAP ldap = group.getLdap();
				authentic = ldap.authenticate(getIdName(), plain);
				break;
			}
			
		} catch (MooshakContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return authentic;
	}

	Profile teamProfile = null;
	
	@Override
	public Profile getProfile() throws MooshakContentException {
		if(teamProfile == null) {
			Profiles profiles = PersistentObject.openPath(PROFILES);
			teamProfile = profiles.find(profile);
		}
		return teamProfile;
	}

	
    /**
     * Get the contact email
     * @return
     */
    public String getEmail() {
    	if(email == null)
    		return "";
    	else
    		return email;
	}
    
    /**
     * Set the contact email; use null to revert to default
     */    
    public void setEmail(String email) {
    	this.email = email;
    }
    
    /**
     * Get the team's physical location
     * @return
     */
    public String getLocation() {
    	if(location == null)
    		return "";
    	else
    		return location;
	}
    
    /**
     * Set the team's physical location; use null to revert to default
     */    
    public void setLocation(String location) {
    	this.location = location;
    }
	
	/**
	 * @return the printoutsTransactions
	 */
	public Integer getPrintoutsTransactions() {
		if(printoutsTransactions == null)
			return 0;
		return printoutsTransactions;
	}

	/**
	 * @param printoutsTransactions the printoutsTransactions to set
	 */
	public void setPrintoutsTransactions(Integer printoutsTransactions) {
		this.printoutsTransactions = printoutsTransactions;
	}

	/**
	 * @return the submissionsTransactions
	 */
	public Integer getSubmissionsTransactions() {
		if(submissionsTransactions == null)
			return 0;
		return submissionsTransactions;
	}

	/**
	 * @param submissionsTransactions the submissionsTransactions to set
	 */
	public void setSubmissionsTransactions(Integer submissionsTransactions) {
		this.submissionsTransactions = submissionsTransactions;
	}

	/**
	 * @return the validationsTransactions
	 */
	public Integer getValidationsTransactions() {
		if(validationsTransactions == null)
			return 0;
		return validationsTransactions;
	}

	/**
	 * @param validationsTransactions the validationsTransactions to set
	 */
	public void setValidationsTransactions(Integer validationsTransactions) {
		this.validationsTransactions = validationsTransactions;
	}

	/**
	 * @return the balloonsTransactions
	 */
	public Integer getBalloonsTransactions() {
		if(balloonsTransactions == null)
			return 0;
		return balloonsTransactions;
	}

	/**
	 * @param balloonsTransactions the balloonsTransactions to set
	 */
	public void setBalloonsTransactions(Integer balloonsTransactions) {
		this.balloonsTransactions = balloonsTransactions;
	}

	/**
	 * @return the questionsTransactions
	 */
	public Integer getQuestionsTransactions() {
		if(questionsTransactions == null)
			return 0;
		return questionsTransactions;
	}

	/**
	 * @param questionsTransactions the questionsTransactions to set
	 */
	public void setQuestionsTransactions(Integer questionsTransactions) {
		this.questionsTransactions = questionsTransactions;
	}

	@Override
	public int getTransactions(String type) {
		switch (type.toLowerCase()) {
		case "printouts":
			return getPrintoutsTransactions();
		case "submissions":
			return getSubmissionsTransactions();
		case "validations":
			return getValidationsTransactions();
		case "questions":
			return getQuestionsTransactions();
		case "balloons":
			return getBalloonsTransactions();
		default:
			return 0;
		}
	}

	@Override
	public void resetTransactions(String type) 
			throws MooshakException {
		
		switch (type.toLowerCase()) {
		case "printouts":
			setPrintoutsTransactions(0);
			break;
		case "submissions":
			setSubmissionsTransactions(0);
			break;
		case "validations":
			setValidationsTransactions(0);
			break;
		case "balloons":
			setBalloonsTransactions(0);
			break;
		case "questions":
			setQuestionsTransactions(0);
			break;

		default:
			break;
		}
		
		save();
	}

	@Override
	public void makeTransaction(String type) 
			throws MooshakException {
		
		switch (type.toLowerCase()) {
		case "printouts":
			if(printoutsTransactions == null)
				printoutsTransactions = 1;
			else
				printoutsTransactions++;
			break;
		case "submissions":
			if(submissionsTransactions == null)
				submissionsTransactions = 1;
			else
				submissionsTransactions++;
			break;
		case "validations":
			if(validationsTransactions == null)
				validationsTransactions = 1;
			else
				validationsTransactions++;
			break;
		case "balloons":
			if(balloonsTransactions == null)
				balloonsTransactions = 1;
			else
				balloonsTransactions++;
			break;
		case "questions":
			if(questionsTransactions == null)
				questionsTransactions = 1;
			else
				questionsTransactions++;
			break;

		default:
			break;
		}

		save();
	}

	
	private void checkValues() {
		StringBuilder builder = new StringBuilder();

		try {
			Group group = getParent();
			AuthenticationType type = group.getAuthentication();
			
			switch(type) {
			case BASIC:
				if (password == null)
					builder.append("Set a password");
				break;
			case LDAP:
				break;
			}
			
		} catch (MooshakContentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (builder.length() > 0)
			builder.append('.');
		fatal = builder.toString();
	}
	

	
	@Override
	protected void destroyed() throws MooshakContentException {
		((Groups) getGrandParent()).removeCached(getIdName());
		super.destroyed();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Team other = (Team) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
}
