package pt.up.fc.dcc.mooshak.content.types;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Group.AuthenticationType;
import pt.up.fc.dcc.mooshak.content.types.Person.Role;
import pt.up.fc.dcc.mooshak.content.types.Person.Sex;
import pt.up.fc.dcc.mooshak.content.util.Colors;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.content.util.Serialize;
import pt.up.fc.dcc.mooshak.print.PrinterController;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Container of groups of teams
 * 
 * Groups is a container of Teams rather than a container of Group to enable the
 * recursive search of Teams
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Groups extends PersistentContainer<Team> {
	private static final long serialVersionUID = 1L;
	
	public enum Language { 
		ENGLISH, PORTUGUESE
	};	

	@MooshakAttribute(name = "Fatal", 
			type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(name = "Warning", 
			type = AttributeType.LABEL)
	private String warning;

	@MooshakAttribute(name = "Printer")
	private String printer;

	@MooshakAttribute(name = "Team_template", 
			type = AttributeType.FILE, 
			complement = ".html")
	private Path teamTemplate;

	@MooshakAttribute(name = "Person_template", 
			type = AttributeType.FILE, 
			complement = ".html")
	private Path personTemplate;

	@MooshakAttribute(name = "Password_template", 
			type = AttributeType.FILE, 
			complement = ".html")
	private Path passwordTemplate;

	@MooshakAttribute(name = "Config", 
			type = AttributeType.FILE, 
			complement = ".css")
	private Path config;
	
	@MooshakAttribute(
			name="Certificate_language",
			type=AttributeType.MENU)
	private Language certLanguage = null;

	@MooshakAttribute(name = "Group", 
			type = AttributeType.CONTENT)
	private Void group;

	/*********************************************************************
	 * \ Custom serialization due to sun.nio.fs.UnixPath * \
	 *********************************************************************/

	/**
	 * Marshal PO state (Path path) which is not a serializable type.
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {
		System.out.println("Write Group start");
		Serialize.writeString(fatal, out);
		Serialize.writeString(warning, out);
		Serialize.writeString(printer, out);

		Serialize.writePath(teamTemplate, out);
		Serialize.writePath(personTemplate, out);
		Serialize.writePath(passwordTemplate, out);
		Serialize.writePath(config, out);

		System.out.println("Write Group end");
	}

	/**
	 * Unmarshal PO state (Path path) which is not a serializable type.
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException,
			ClassNotFoundException {

		System.out.println("Read Group start");

		fatal = Serialize.readString(in);
		warning = Serialize.readString(in);
		printer = Serialize.readString(in);

		teamTemplate = Serialize.readPath(in);
		personTemplate = Serialize.readPath(in);
		passwordTemplate = Serialize.readPath(in);
		config = Serialize.readPath(in);

		System.out.println("Read Group end");
	}

	/*-------------------------------------------------------------------*\
	 * 		            Operations                                    *
	\*-------------------------------------------------------------------*/

	@MooshakOperation(name = "Import Teams", 
			inputable = true, 
			tip = "Import teams TSV data file")
	private CommandOutcome importTeamsUploadForm(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();

		String content = context.getValue("content");
		
		if (content == null) {
			outcome.setMessage("Error: no data found");
			return outcome;
		}
		
		if(content.indexOf("\n") < 0) {
			outcome.setMessage("Error: no data found");
			return outcome;
		}

		boolean clear = context.getValue("clear").toLowerCase().equals("true");

		if (clear) {
			try {
				for (PersistentObject po : getChildren(false))
					po.delete();
			} catch (MooshakContentException e) {
				outcome.setMessage("Error: could not delete teams");
				return outcome;
			}
		}
		
		String lines[] = content.split("\n");
		int nlines = lines.length;
		
		String separator = "\\t";
		for (String sep : Arrays.asList("\\t", ";", ",")) {
			int cells = content.split(sep).length;
			int cols = cells / nlines + 1;
			if (cols > 1) {
				separator = sep;
				break;
			}
		}
		outcome.addPair("separator", separator);

		boolean hasId = false;
		if (lines[0].toLowerCase().indexOf("id") != -1)
			hasId = true;

		outcome.addPair("header", lines[0]);

		outcome.addPair("hasId", Boolean.toString(hasId));

		for (int i = 1; i < lines.length; i++) {
			if(lines[i].indexOf(separator.equals("\\t") ? "\t" 
					: separator) < 0)
				continue;
			outcome.addPair("dataRow", lines[i]);
		}

		outcome.setContinuation("ImportTeamsEditForm");

		return outcome;
	}

	private static final RegExp REGEX_ID_NAME = RegExp.compile(".*\\d{5,}$");
	
	@MooshakOperation(name = "ImportTeamsEditForm", 
			inputable = true, 
			show = false,
			sendEvents = true)
	private CommandOutcome importTeamsEditForm(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();

		String separator = context.getValue("separator");

		List<String> headers = context.getValues("header");

		if (!headers.contains("ID")) {
			outcome.setMessage("Error: ID column not selected");
			return outcome;
		}
		if (!headers.contains("Team")) {
			outcome.setMessage("Error: Team column not selected");
			return outcome;
		}

		Group group = null;
		if (!headers.contains("Group")) {
			try {
				for (PersistentObject po : getChildren(false)) {
					if(po.getIdName().equals("Default")) {
						group = (Group) po;
						break;
					}
				}
				if (group == null) {
					group = create("Default", Group.class);
					group.setAcronym("DFLT");
					group.setDesignation("Default");
					group.save();
				}
			} catch (MooshakContentException e) {
				outcome.setMessage("Error:"+e.getMessage());
				return outcome;
			}
		} 

		int lineNo = 1;
		List<String> dataRows = context.getValues("dataRow");
		try {
			for (String row : dataRows) {
				String values[] = row.split(separator);
				if(values[headers.indexOf("Team")].equals(""))
					continue;
				
				if (headers.contains("Group")) {
					String groupName = values[headers.indexOf("Group")];
					String school = "";
					for (char c : groupName.toCharArray()) {
					    if (Character.isUpperCase(c) || Character.isDigit(c))
					        school += c;
					}
					
					for (PersistentObject po : getChildren(false)) {
						if(po.getIdName().equals(school)) {
							group = (Group) po;
							break;
						}
					}
					if (group == null) {
						group = create(Filenames.sanitize(school), Group.class);
						group.setAcronym(headers.contains("Acronym") ? 
								values[headers.indexOf("Acronym")] : school);
						group.setAuthentication(AuthenticationType.BASIC);
						group.setColor(headers.contains("Color") ? 
								Colors.getColor(values[headers.indexOf("Color")]) 
								: Color.BLACK);
						group.setDesignation(groupName);
						try {
							group.setFlag(open(Paths
									.get(getAbsoluteFile().toString(),
											"..", "..", "..", "configs", "flags",
											headers.contains("Flag") ? 
											values[headers.indexOf("Flag")] : "00")));
						} catch(MooshakContentException e) {}
						
						group.save();
					}
				}
				
				String name = headers.contains("Name") ? 
						values[headers.indexOf("Name")] : "";
				if(headers.contains("First_Name") && headers.contains("Last_Name")
						&& name.equals(""))
					name = values[headers.indexOf("First_Name")] + " " +
							values[headers.indexOf("Last_Name")];
				
				String teamName = values[headers.indexOf("Team")];
				Team team = null;
				if ((team = group.find(Filenames.sanitize(teamName))) == null) {
					team = group.create(Filenames.sanitize(teamName),Team.class);
					team.setEmail(headers.contains("Email") ? 
							values[headers.indexOf("Email")] : "");
					team.setLocation(headers.contains("Country") ? 
							values[headers.indexOf("Country")] : "");
					team.setPassword(headers.contains("Password") ? 
							values[headers.indexOf("Password")] : "");
					if(REGEX_ID_NAME.test(teamName)) 
						team.setName(name);
					else
						team.setName(teamName);
					team.save();
				}
				
				Person person = team.create(values[headers.indexOf("ID")],
						Person.class);
				person.setName(name);
				String gender = headers.contains("Gender") ? 
						values[headers.indexOf("Gender")] : "MALE";
				if(gender.equals("FEMALE"))
					gender = "F";
				else if(gender.equals("MALE"))
					gender = "M";
				
				try {
					person.setSex(Sex.valueOf(gender.toUpperCase()));
				} catch (Exception e) {
					person.setSex(Sex.M);
				}
				
				try {
					person.setRole(headers.contains("Role") ? 
						Role.valueOf(values[headers.indexOf("Role")].toUpperCase()) :
							Role.CONTESTANT);
				} catch (Exception e) {
					person.setRole(Role.CONTESTANT);
				}
				
				person.setBorn(new Date(0));
				person.setContact(headers.contains("Email") ? 
							values[headers.indexOf("Email")] : "");
				person.save();
				
				lineNo++;
			}
		} catch (MooshakContentException e) {
			outcome.setMessage("Error:"+e.getMessage());
			return outcome;
		} catch (ArrayIndexOutOfBoundsException e) {
			outcome.setMessage("Error: Line " + lineNo + " has an unexpected format");
			return outcome;
		}

		outcome.setMessage("Import Success");
		return outcome;
	}
	
	@MooshakOperation(
			name="Default Config",
			inputable=false,
			tip="CSS File for configuring HTML Templates")
	CommandOutcome createDefaultConfig() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Path config = getAbsoluteFile("Config.css");
			
			String content = "BODY {\n"
					+ "\tfont-size: 12px;\n\ttext-align: center;\n}\n\n"
					+ "table {\n\ttext-align: center;\n\tmargin:auto;\n}\n\n"
					+ ".pagebreak { \n\tpage-break-before: always; \n}\n\n"
					+ ".h1 { \n\tdisplay: block;\n\tfont-size: 2.33em;\n\tpadding-top: 2.6em;"
					+ "\n\tpadding-bottom: 2.6em;\n\tmargin-left: 0;\n\tmargin-right: 0;"
					+ "\n\tfont-weight: bold;\n}\n\n"
					+ ".h2 { \n\tdisplay: block;\n\tfont-size: 1.83em;\n\tpadding-top: 2.77em;"
					+ "\n\tpadding-bottom: 2.77em;\n\tmargin-left: 0;\n\tmargin-right: 0;"
					+ "\n\tfont-weight: bold;\n}\n\n"
					+ ".h3 { \n\tdisplay: block;\n\tfont-size: 1.5em;\n\tpadding-top: 2.93em;"
					+ "\n\tpadding-bottom: 2.93em;\n\tmargin-left: 0;\n\tmargin-right: 0;"
					+ "\n\tfont-weight: bold;\n}\n\n"
					+ ".h4 { \n\tdisplay: block;\n\tpadding-top: 3.26em;"
					+ "\n\tpadding-bottom:3.26em;\n\tmargin-left: 0;\n\tmargin-right: 0;"
					+ "\n\tfont-weight: bold;\n}\n\n"
					+ ".h5 { \n\tdisplay: block;\n\tfont-size: 1.17em;\n\tpadding-top: 3.6em;"
					+ "\n\tpadding-bottom: 3.6em;\n\tmargin-left: 0;\n\tmargin-right: 0;"
					+ "\n\tfont-weight: bold;\n}\n\n"
					+ ".h6 { \n\tdisplay: block;\n\tfont-size: 1em;\n\tpadding-top:4.26em;"
					+ "\n\tpadding-bottom: 4.26em;\n\tmargin-left: 0;\n\tmargin-right: 0;"
					+ "\n\tfont-weight: bold;\n}";
			
			executeIgnoringFSNotifications( 
					() -> Files.write(config, content.getBytes())
			);
			
			setConfig(config);
			
			save();
			
			outcome.setMessage("Config created");
		} catch (IOException | MooshakContentException e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Default Password Template",
			inputable=false,
			tip="HTML file with password template")
	CommandOutcome createDefaultPasswordTemplate() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Path template = getAbsoluteFile("Password_template.html");
			
			String content = "<table width=\"100%\" border=\"0\">" + 
					"<tr><th colspan=\"2\" height=\"5cm\">&nbsp;</th></tr>" + 
					"<tr><th colspan=\"2\" class=\"h1\">$Contest</th></tr>" + 
					"<tr><th colspan=\"2\" height=\"50\">&nbsp;</th></tr>" + 
					"<tr><th>Team</th><td>$Team</td></tr>" + 
					"<tr><th>Group</th><td>$Group</td></tr>" +
					"<tr><th colspan=\"2\" height=\"50\">&nbsp;</th></tr>" +
					"<tr><th>Login</th><td>$Login</td></tr>" +
					"<tr><th>Password</th><td>$Password</td></tr>" +
					"</table>";
			
			executeIgnoringFSNotifications( 
					() -> Files.write(template, content.getBytes())
			);
			
			setPasswordTemplate(template);
			
			save();
			
			outcome.setMessage("Template created");
		} catch (IOException | MooshakContentException e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Default Person Template",
			inputable=false,
			tip="HTML file with person template")
	CommandOutcome createDefaultPersonTemplate() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Path template = getAbsoluteFile("Person_template.html");
			
			String content = "<table width=\"100%\" border=\"0\">"
					+ "\n\t<tr><th colspan=\"2\" height=\"5cm\">&nbsp;</th></tr>"
					+ "\n\t<tr><th class=\"h1\"><u>$Rank</u></th></tr>"
					+ "\n\t<tr><th class=\"h1\">$Name</th></tr>"
					+ "\n\t<tr><th class=\"h2\">Team \"$Team\"</th></tr>"
					+ "\n\t<tr><th class=\"h3\">$Group</th></tr>" +
					"\n</table>";
			
			executeIgnoringFSNotifications( 
					() -> Files.write(template, content.getBytes())
			);
			
			setPersonTemplate(template);
			
			save();
			
			outcome.setMessage("Template created");
		} catch (IOException | MooshakContentException e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Default Team Template",
			inputable=false,
			tip="HTML file with person template")
	CommandOutcome createDefaultTeamTemplate() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Path template = getAbsoluteFile("Team_template.html");
			
			String content = "<table width=\"100%\" border=\"0\">"
					+ "\n\t<tr><th colspan=\"2\" height=\"5cm\">&nbsp;</th></tr>"
					+ "\n\t<tr><th class=\"h1\"><u>$Rank</u></th></tr>"
					+ "\n\t<tr><th class=\"h1\">Team \"$Team\"</th></tr>"
					+ "\n\t<tr><th class=\"h2\">$Group</th></tr>"
					+ "\n\t<tr><th class=\"h3\">$Names</th></tr>" +
					"\n</table>";
			
			executeIgnoringFSNotifications( 
					() -> Files.write(template, content.getBytes())
			);
			
			setTeamTemplate(template);
			
			save();
			
			outcome.setMessage("Template created");
		} catch (IOException | MooshakContentException e) {
			outcome.setMessage("Error:"+e.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Print Certificates",
			inputable=false,
			tip="Prints certificates for all participants")
	private CommandOutcome printCertificates() {
		CommandOutcome outcome = new CommandOutcome();
		
		String content = null;
		try {
			content = getCertificatesContent();
		} catch (MooshakException e) {
			outcome.setMessage(e.getMessage());
			return outcome;
		}

		String filename = null;
		String filepath = null;
		try {	
			PrinterController printer = null;
			filename = "Mooshak-" + getParent().getIdName() + "-certificates.pdf";
			filepath = getAbsoluteFile().toString() + File.separator + filename;		
			printer = new PrinterController(content,
					new String(Files.readAllBytes(getAbsoluteFile(getConfig())), "UTF-8"));
			
			printer.printToFile(new File(filepath), false, true);
		} catch (Exception e) {
			outcome.setMessage("Could not print file");
			return outcome;
		}
		
		String service = getPrinter();
		
		String os = System.getProperty("os.name");
		if(os.toLowerCase().indexOf("linux") != -1) {
			Process pr = null;
			try {
				String printerArg = "";
				if(!service.equals(""))
					printerArg = " -P" + service;
				pr = Runtime.getRuntime().exec("lpr" + printerArg + " -J" + filename + " -p " 
						+ filepath);
			} catch (IOException e) {
				outcome.setMessage("Could not send file to printer");
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			
			String result = "";
			try {
				String s;
				while ((s = in.readLine()) != null) 
					result += s;
			} catch (IOException e) {
				outcome.setMessage(e.getMessage());
			}
			
			if (!result.equals(""))
				outcome.setMessage(result);
		}
		else {
			outcome.setMessage("Printing in windows is not yet supported");
			return outcome;
		}
		
		outcome.setMessage("Files sent to printer ...");
		
		return outcome;
	}
	
	@MooshakOperation(
			name="View Certificates",
			inputable=false,
			tip="View the certificates")
	private CommandOutcome viewCertificates() {
		CommandOutcome outcome = new CommandOutcome();
		
		String content = null;
		try {
			content = getCertificatesContent();
		} catch (MooshakException e) {
			outcome.setMessage(e.getMessage());
			return outcome;
		}

		String filename = null;
		String filepath = null;
		try {	
			PrinterController printer = null;
			filename = "Mooshak-" + getParent().getIdName() + "-certificates.pdf";
			filepath = getAbsoluteFile().toString() + File.separator + filename;		
			printer = new PrinterController(content,
					new String(Files.readAllBytes(getAbsoluteFile(getConfig())), "UTF-8"));
			
			printer.printToFile(new File(filepath), false, true);

			outcome.addPair("contestId", getParent().getIdName());
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
	 * @throws MooshakException 
	 */
	private String getCertificatesContent() throws MooshakException {
		List<Team> teams;
		
		try {
			Contest contest = getParent();
			
			teams = contest.getRankingPolicy().finalizeRankings();
			
		} catch (MooshakException cause) {
			throw new MooshakException(cause.getMessage());
		}
		
		Path template = null, teamTemplate = null, config = null, fileNamePath = null;
		if(getPersonTemplate() == null)
			createDefaultPersonTemplate();
		fileNamePath = getPersonTemplate().getFileName();
		if (fileNamePath != null)
			template = Paths.get(getAbsoluteFile().toString(),
					fileNamePath.toString());
		else
			throw new MooshakException("No person template");
			
		if(!Files.exists(template))
			createDefaultPersonTemplate();
		
		if(getTeamTemplate() == null)
			createDefaultTeamTemplate();
		fileNamePath = getTeamTemplate().getFileName();
		if (fileNamePath != null)
			teamTemplate = Paths.get(getAbsoluteFile().toString(),
				fileNamePath.toString());
		else 
			throw new MooshakException("No team template");
		
		if(!Files.exists(template))
			createDefaultTeamTemplate();
		
		if(getConfig() == null)
			createDefaultConfig();
		fileNamePath = getConfig().getFileName();
		if (fileNamePath != null)
			config = Paths.get(getAbsoluteFile().toString(),
					fileNamePath.toString());
		else 
			throw new MooshakException("No config template");
		if(!Files.exists(config)) 
			createDefaultConfig();

		String content = "";
		
		for (Team team : teams) {
			
			try {
				content += team.printCertificates(template, teamTemplate, config, true);
			} catch (Exception e) {
				throw new MooshakException("Problem printing"
						+ " certificates of " + team.getIdName());
				//outcome.setMessage(outcome.getMessage() + "\n" + "Problem printing"
				//		+ " certificates of " + team.getIdName());
			}
		}
		
		return content;
	}
	
	@MooshakOperation(
			name="Generate Passwords and Print",
			inputable=false,
			tip="Generate passwords for all teams in this group and prints them")
	private CommandOutcome generatePasswordsAndPrint() {
		CommandOutcome outcome = new CommandOutcome();
		
		Path template = null, config = null;
		if(getPasswordTemplate() == null)
			createDefaultPasswordTemplate();
		template = Paths.get(getAbsoluteFile().toString(),
				getPasswordTemplate().getFileName().toString());
		if(!Files.exists(template))
			createDefaultPasswordTemplate();
		
		if(getConfig() == null)
			createDefaultConfig();
		config = Paths.get(getAbsoluteFile().toString(),
				getConfig().getFileName().toString());
		if(!Files.exists(config)) 
			createDefaultConfig();

		String content = "";
		
		for (Team team : getContent()) {
			try {
				content += team.getPrintableTeamPassword(template, config);
				content += "<div class=\"pagebreak\"> </div>";
			} catch (Exception e) {
				outcome.setMessage(outcome.getMessage() + "\n" + "Problem printing: " 
						+team.getName());
			}
		}

		String filepath = null;
		String filename = null;
		try {
			filename = "Mooshak-" + getParent().getIdName() + "-passwords.pdf";
			filepath = getAbsoluteFile().toString() + File.separator + filename;
			
			PrinterController printer;
		
			printer = new PrinterController(content,
					new String(Files.readAllBytes(config), "UTF-8"));
			File file = new File(filepath);
			printer.printToFile(file, false, true);
		} catch (Exception e1) {
			outcome.setMessage("Error generating pdf");
			return outcome;
		}
		
		String service = getPrinter();
		
		String os = System.getProperty("os.name");
		if(os.toLowerCase().indexOf("linux") != -1) {
			Process pr = null;
			try {
				String printerArg = "";
				if(!service.equals(""))
					printerArg = " -P" + service;
				pr = Runtime.getRuntime().exec("lpr" + printerArg + " -J" + filename + " -p " 
						+ filepath);
			} catch (IOException e) {
				outcome.setMessage("Could not send file to printer");
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			
			String result = "";
			try {
				String s;
				while ((s = in.readLine()) != null) 
					result += s;
			} catch (IOException e) {
				outcome.setMessage(e.getMessage());
			}
			
			if (!result.equals(""))
				outcome.setMessage(result);
		}
		else {
			outcome.setMessage("Printing in windows is not yet supported");
			return outcome;
		}
		
		outcome.setMessage("Files sent to printer ...");
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Generate Passwords to Archive",
			inputable=false,
			tip="Generate passwords for all teams in this group, to"
					+ " an archive")
	private CommandOutcome generatePasswordsArchive() {
		CommandOutcome outcome = new CommandOutcome();
		
		Path template = null, config = null;
		if(getPasswordTemplate() == null)
			createDefaultPasswordTemplate();
		template = Paths.get(getAbsoluteFile().toString(),
				getPasswordTemplate().getFileName().toString());
		if(!Files.exists(template))
			createDefaultPasswordTemplate();
		
		if(getConfig() == null)
			createDefaultConfig();
		config = Paths.get(getAbsoluteFile().toString(),
				getConfig().getFileName().toString());
		if(!Files.exists(config)) 
			createDefaultConfig();

		Path zipPath = Paths.get(getAbsoluteFile().toString(),
				"Passwords.zip");
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipPath
					.toString()));
			out.setLevel(Deflater.DEFAULT_COMPRESSION);
		} catch (Exception cause) {
			outcome.setMessage("Cannot create zip file:" + cause
					.getLocalizedMessage());
			return outcome;
		}
		
		for (Team team : getContent()) {
			try {
				File file = team.printTeamPassword(template, config, true);
				FileInputStream in = new FileInputStream(file);
				out.putNextEntry(new ZipEntry(file.getName()));

				byte[] buffer = new byte[1024];
				int len;
				while ((len = in.read(buffer)) > 0)
					out.write(buffer, 0, len);
				out.closeEntry();
				in.close();
				file.delete();
			} catch (Exception e) {
				
			}
		}
		
		try {
			out.finish();
			out.close();
			outcome.addPair("mimeType", "application/zip");
			outcome.addPair("file", new String(Base64Coder.encode(Files.readAllBytes(zipPath))));
		} catch (Exception cause) {
			outcome.setMessage("Cannot create Zip file:" + cause
					.getLocalizedMessage());
			return outcome;
		}
		
		outcome.setContinuation("GeneratePasswordArchiveDownload");
		
		return outcome;
	}
	
	
	@MooshakOperation(
			name="GeneratePasswordArchiveDownload",
			inputable=true,
			show=false)
	private CommandOutcome generatePasswordArchiveDownload(MethodContext context) {	
		return new CommandOutcome();
	}
	

	@MooshakOperation(
			name="Generate Passwords to CSV",
			inputable=false,
			tip="Generate passwords for all teams in this group, to"
					+ " a .csv file")
	private CommandOutcome generatePasswordsCsv() {
		CommandOutcome outcome = new CommandOutcome();

		outcome.addPair("mimeType", "text/csv");

		String lines = "";
		for (Team team : getContent()) {
			try {
				lines += team.generatePasswordCSVDataLine() + "\n";
			} catch (Exception e) {
			}
		}
		
		outcome.addPair("data", Base64Coder.encodeString(lines));

		outcome.setContinuation("GeneratePasswordExcelDownload");
		
		return outcome;
	}
	
	
	@MooshakOperation(
			name="GeneratePasswordExcelDownload",
			inputable=true,
			show=false)
	private CommandOutcome generatePasswordCsvDownload(MethodContext context) {	
		return new CommandOutcome();
	}
	
	
	@MooshakOperation(
			name="Start Now",
			inputable=false,
			tip="Set Start time to now in all teams in all groups")
	private CommandOutcome startNow() {
		CommandOutcome outcome = new CommandOutcome();

		Date now = new Date();
		
		int count = 0;
		for (Team team : getContent()) {
			team.setStart(now);
			
			try {
				team.save();
				count++;
			} catch (MooshakContentException e) {	}
		}
		
		outcome.setMessage("Set start time now in " + count
				+ " teams");
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Start Never",
			inputable=false,
			tip="Set Start time to never in all teams in all groups")
	private CommandOutcome startNever() {
		CommandOutcome outcome = new CommandOutcome();

		int count = 0;
		for (Team team : getContent()) {
			team.setStart(null);
			
			try {
				team.save();
				count++;
			} catch (MooshakContentException e) {	}
		}
		
		outcome.setMessage("Set start time never in " + count
				+ " teams");
		
		return outcome;
	}
	
	

	// -------------------- Setters and getters ----------------------//

	/**
	 * Fatal errors messages of this folder
	 * 
	 * @return
	 */
	public String getFatal() {
		if (fatal == null)
			return "";
		else
			return fatal;

	}

	/**
	 * Set fatal error messages
	 * 
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
	 * Warning errors messages of this folder
	 * 
	 * @return
	 */
	public String getWarning() {
		if (warning == null)
			return "";
		else
			return warning;

	}

	/**
	 * Set warning error messages
	 * 
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * Get printer queue name (empty for default printer)
	 * 
	 * @return
	 */
	public String getPrinter() {
		if (printer == null)
			return "";
		else
			return printer;
	}

	/**
	 * Set printer queue name (empty or null for default printer)
	 * 
	 * @param printer
	 */
	public void setPrinter(String printer) {
		this.printer = printer;
	}

	/**
	 * Get HTML file with person template for certificate
	 * 
	 * @return
	 */
	public Path getPersonTemplate() {
		if (personTemplate == null)
			return null;
		else
			return getPath().resolve(personTemplate);
	}

	/**
	 * Set HTML file with person template for certificate
	 * 
	 * @return
	 */
	public void setPersonTemplate(Path template) {
		this.personTemplate = template;
	}

	/**
	 * Get HTML file with team template for certificate printing
	 * 
	 * @return
	 */
	public Path getTeamTemplate() {
		if (teamTemplate == null)
			return null;
		else
			return getPath().resolve(teamTemplate);
	}

	/**
	 * Set HTML file with team template for certificate printing
	 * 
	 * @return
	 */
	public void setTeamTemplate(Path template) {
		this.teamTemplate = template;
	}

	/**
	 * Get HTML file with password template for authentication sheet
	 * 
	 * @return
	 */
	public Path getPasswordTemplate() {
		if (passwordTemplate == null)
			return null;
		else
			return getPath().resolve(passwordTemplate);
	}

	/**
	 * Set HTML file with password template for authentication sheet
	 * 
	 * @return
	 */
	public void setPasswordTemplate(Path template) {
		this.passwordTemplate = template;
	}

	/**
	 * Get CSS file for configuring HTML template
	 * 
	 * @return
	 */
	public Path getConfig() {
		if (config == null)
			return null;
		else
			return getPath().resolve(config);
	}

	/**
	 * Set CSS file for configuring HTML template
	 * 
	 * @return
	 */
	public void setConfig(Path config) {
		this.config = config;
	}
	
	/**
	 * The language in which the certificates must be written
	 * @return
	 */
	public Language getCertLanguage() {
		if (certLanguage == null)
			return Language.ENGLISH;
		return certLanguage;
	}
	
	/**
	 * Set the language in which the certificates must be written
	 * @param certLanguage
	 */
	public void setCertLanguage(Language certLanguage) {
		this.certLanguage = certLanguage;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends PersistentObject> Class<T> getDescendantType() {
		return (Class<T>) Group.class;
	}
	
	/**
	 * Cache of teams with a maximum capacity
	 * @author José Paulo Leal zp@dcc.fc.up.pt
	 *
	 */
	private static class TeamCache	extends LinkedHashMap<String,Team> {
		
		private static final long serialVersionUID = 1L;
		private static final int INITIAL_CAPACITY = 500;
		private static final float LOAD_FACTOR = 0.75F;
		private static final boolean ACCESS_ORDER = true;
		
		private static int maxEntries = 5*INITIAL_CAPACITY;
		
		TeamCache() {
			super(INITIAL_CAPACITY, LOAD_FACTOR, ACCESS_ORDER);
		}
		
		protected boolean removeEldestEntry(
				Map.Entry<String,Team> eldest) {
			
			if(size() > maxEntries) {
				return true;
			} else {
				return false;
			}
	    }
	}
	
	private TeamCache teamCache = new TeamCache(); 
	
	/**
	 * Try to use cached team with given name before using generic find 
	 */
	@Override
	public Team find(String name) throws MooshakContentException {
		Team team = teamCache.get(name);
		
		if(team == null)
			teamCache.put(name,team = super.find(name));
		
		return team;
	}
	
	/**
	 * Remove team from cache
	 * @param team
	 */
	public void removeCached(String name) {
		teamCache.remove(name);
	}
}
