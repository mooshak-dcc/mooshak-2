package pt.up.fc.dcc.mooshak.content.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.util.ClassificationStringUtils;
import pt.up.fc.dcc.mooshak.content.util.StringEscapeUtils;
import pt.up.fc.dcc.mooshak.content.util.Strings;
import pt.up.fc.dcc.mooshak.print.PrinterController;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * A person in a team 
 * @author José Paulo Leal <zp2dcc.fc.up.pt>
 * @version 2.0
 * @since July 2013
 * 
 * 
 *        Recoded in Java in July 2013 
 *        From a Tcl module coded in April 2001
 */
public class Person extends PersistentObject {
	private static final long serialVersionUID = 1L;

	public enum Role { COACH,	CONTESTANT;
		
		public String toString() {
			return Strings.toTitle(super.toString());
		}
	};
	
	public enum Sex { M, F;
		
		public String toString() {
			return Strings.toTitle(super.toString());
		}
	};
	
	@MooshakAttribute( 
			name="Fatal", 
			type=AttributeType.LABEL)
	private String fatal = null;
	
	@MooshakAttribute( 
			name="Warning",
			type=AttributeType.LABEL)
	private String warning = null;

	@MooshakAttribute(name="Name")
	private String name = null;
	
	@MooshakAttribute(
				name="Role",
				type=AttributeType.MENU)
	private Role role = null;
	
	@MooshakAttribute(
			name="Sex",
			type=AttributeType.MENU)
	private Sex sex = null;
	
	@MooshakAttribute(
			name="Born",
			type=AttributeType.DATE)
	private Date born = null;
	
	@MooshakAttribute(name="Contact")
	private String contact = null;
	
	//-------------------- Setters and getters ----------------------//
	
	/**
	 * Get fatal errors messages
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
	 * get warning errors messages
	 * @return
	 */
	public String getWarning() {
		if(warning == null)
			return "";
		else
			return warning;					
	}
	
	/**
	 * Set warning error messages
	 * @param warning
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * Get name of person
	 * @return
	 */
	public String getName() {
		if(name == null)
			return "";
		else
			return name;
	}

	/**
	 * Set name of person, or null to revert to default
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get role of this person in team (defaults to contestant)
	 * @return
	 */
	public Role getRole() {
		if(role == null)
			return Role.CONTESTANT;
		else
			return role;
	}

	/**
	 * Set role of this person in team, or null to revert to default
	 * @param role
	 */
	public void setRole(Role role) {
		this.role = role;
	}

	/**
	 * Get gender of this person. Male is the default
	 * @return
	 */
	public Sex getSex() {
		if(sex == null)
			return Sex.M;
		return sex;
	}

	/** 
	 * Set gender of this person, or null to revert to default
	 * @param sex
	 */
	public void setSex(Sex sex) {
		this.sex = sex;
	}

	/** 
	 * Get date when this person was born
	 * @return
	 */
	public Date getBorn() {
		return born;
	}

	/**
	 * Set date when this person was born
	 * @param born
	 */
	public void setBorn(Date born) {
		this.born = born;
	}

	/**
	 * Get a contact (email, celphone, etc) for  this person 
	 * @return
	 */
	public String getContact() {
		if(contact == null)
			return "";
		else
			return contact;
	}

	/**
	 * Set a contact for this person
	 * @param contact
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	@MooshakOperation(
			name="Generate Certificates",
			inputable=false,
			tip="Generate certificates for this person")
	private CommandOutcome generateCertificates() {
		CommandOutcome outcome = new CommandOutcome();
    	
    	try {
			Groups groups = getParent().getParent().getParent();
			
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
			
			printCertificate(template, config, false);
			
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
		Path template = null, config = null;
    	
    	try {
			groups = getParent().getParent().getParent();
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
			content = printCertificate(template, config, true);
		} catch (MooshakException e) {
			outcome.setMessage(e.getMessage());
			return outcome;
		}

		String filename = null;
		String filepath = null;
		try {	
			PrinterController printer = null;
			filename = "Mooshak-" + groups.getParent().getIdName() + "-" +
					getIdName() + "-certificate.pdf";
			filepath = getAbsoluteFile().toString() + File.separator + filename;		
			printer = new PrinterController(content,
					new String(Files.readAllBytes(config), "UTF-8"));
			
			printer.printToFile(new File(filepath), false, true);

			outcome.addPair("contestId", groups.getParent().getIdName());
			outcome.addPair("teamId", getParent().getIdName());
			outcome.addPair("personId", getIdName());
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
	 * Print certificates of this person
	 * 
	 * @param template
	 * @param config
	 * @return 
	 * @throws MooshakException 
	 */
	public String printCertificate(Path template, Path config, 
			boolean toFile) throws MooshakException {
		
		PrinterController printer = null;
		try {
			String content = new String(Files.readAllBytes(template));
			content = setCertificateTemplateValues(content);
			
			if(toFile)
				return content;
			
			printer = new PrinterController(content,
					new String(Files.readAllBytes(config), "UTF-8"));
			
			File file = new File(getAbsoluteFile().toString() + File.separator +
					"Mooshak-" + getParent().getParent().getParent().getParent().getIdName()
					+  "-" + getIdName() + "-certificate.pdf");
			printer.printToFile(file, false, true);
		
				
			String service = ((Groups)getParent().getParent().getParent()).getPrinter();
			
			String os = System.getProperty("os.name");
			if(os.toLowerCase().indexOf("linux") != -1) {
				Process pr = null;
				try {
					String printerArg = "";
					if(!service.equals(""))
						printerArg = " -P" + service;
					pr = Runtime.getRuntime().exec("lpr" + printerArg + " -J" + file.getName() + " " 
							+ file.getAbsolutePath());
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
		} catch (Exception e) {
			throw new MooshakException("Could NOT print: " + e.getMessage());
		}
		
		return null;
	}
	
	/**
	 * Sets the certificate template values and returns the resulting
	 * template
	 * 
	 * @param template
	 * @return
	 */
	private String setCertificateTemplateValues(String template) {
		String temp;

		Integer rank = null;
		try {
			rank = ((Team) getParent()).getRank();
		} catch (MooshakContentException e) {}
		
		if (rank != null) {
			
			try {
				Groups groups = getParent().getParent().getParent();
				temp = template.replaceAll("\\$Rank", StringEscapeUtils.escapeHtml(
						ClassificationStringUtils.toString(groups.getCertLanguage(), rank.intValue())));
			} catch (MooshakContentException e1) {
				temp = template.replaceAll("\\$Rank", StringEscapeUtils.escapeHtml(
						ClassificationStringUtils.toEnglishString(rank.intValue())));
			}
		} else {
			temp = template.replaceAll("\\$Rank", "Rank not specified");
		}
		
		try {
			Team team = ((Team) getParent());
			temp = temp.replaceAll("\\$Team", StringEscapeUtils.escapeHtml(
					team.getName() != null ? team.getName()	: team.getIdName()));
		} catch (MooshakContentException e) {
			temp = temp.replaceAll("\\$Team", "");
		}
		
		temp = temp.replaceAll("\\$Name", StringEscapeUtils.escapeHtml(getName()));
		temp = temp.replaceAll("\\$Role", StringEscapeUtils.escapeHtml(getRole().toString()));
		

		try {
			Group group = ((Group) getParent().getParent());
			temp = temp.replaceAll("\\$Group", StringEscapeUtils.escapeHtml(
					group.getDesignation() != null ? group.getDesignation()
					: group.getIdName()));
		} catch (MooshakContentException e) {
			temp = temp.replaceAll("\\$Group", "?");
		}
		
		return temp;
	}

}
