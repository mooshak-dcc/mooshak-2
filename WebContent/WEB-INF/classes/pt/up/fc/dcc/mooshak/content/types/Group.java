package pt.up.fc.dcc.mooshak.content.types;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.util.Serialize;
import pt.up.fc.dcc.mooshak.content.util.Strings;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Group of teams
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @version 2.0
 * @since June 2012
 * 
 *        Recoded in Java in June 2012 From a Tcl module coded in April 2001
 */
public class Group extends PersistentContainer<Team> {
	private static final long serialVersionUID = 1L;

	@Deprecated
	// iso3166-countrycodes
	String[] countryCodes = { "00", "ad", "be", "cl", "ec", "gu", "ir", "lb", "mx", "pe", "sa", "tw", "za", "ae", "bh",
			"cn", "ee", "hk", "is", "lk", "my", "ph", "sb", "ua", "zm", "ag", "bm", "co", "eg", "hn", "it", "lt", "mz",
			"pk", "se", "ug", "zw", "am", "bn", "cr", "es", "hr", "jm", "lv", "na", "pl", "sg", "uk", "ao", "bo", "cu",
			"fi", "hu", "jo", "ma", "ng", "pr", "si", "us", "ar", "br", "cy", "fo", "id", "jp", "mo", "ni", "pt", "sk",
			"uy", "at", "bs", "cz", "fr", "ie", "ke", "mp", "nl", "py", "sr", "vc", "au", "by", "de", "gb", "il", "kr",
			"mt", "no", "qa", "th", "ve", "bb", "ca", "dk", "gr", "in", "kw", "mu", "nz", "ro", "tr", "vi", "bd", "ch",
			"do", "gt", "iq", "kz", "mw", "pa", "ru", "tt", };

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal = null;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning = null;

	@MooshakAttribute(name = "Designation", tip = "Group name")
	private String designation = null;;

	@MooshakAttribute(name = "Acronym", tip = "Group Acronym")
	private String acronym = null;;

	@MooshakAttribute(name = "Color", type = AttributeType.COLOR)
	private Color color = null;

	@MooshakAttribute(name = "Flag", type = AttributeType.PATH, complement = "/data/configs/flags")
	private Path flag = null;

	public enum AuthenticationType {
		BASIC, LDAP
	};

	@MooshakAttribute(name = "Authentication", type = AttributeType.MENU)
	private AuthenticationType authentication = AuthenticationType.BASIC;

	@MooshakAttribute(name = "LDAP", type = AttributeType.PATH, complement = "/data/configs/ldap", tip = "Use LDAP for authentication", conditionalField = "authentication", conditionalValue = "LDAP")
	private Path ldap = null;

	@MooshakAttribute(name = "Basic", type = AttributeType.HIDDEN)
	private Void basic;

	@MooshakAttribute(name = "Team", type = AttributeType.CONTENT)
	private Void team;

	/*-------------------------------------------------------------------*\
	 * Custom serialization due to sun.nio.fs.UnixPath					 *
	\*-------------------------------------------------------------------*/

	/**
	 * Marshal PO state (Path path) which is not a serializable type.
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(ObjectOutputStream out) throws IOException {

		Serialize.writeString(fatal, out);
		Serialize.writeString(warning, out);
		Serialize.writeString(designation, out);
		Serialize.writeString(acronym, out);

		out.writeInt(color.getRGB());

		Serialize.writePath(flag, out);

		Serialize.writeString(authentication.toString(), out);

		Serialize.writePath(ldap, out);
	}

	/**
	 * Unmarshal PO state (Path path) which is not a serializable type.
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {

		fatal = Serialize.readString(in);
		warning = Serialize.readString(in);
		designation = Serialize.readString(in);
		acronym = Serialize.readString(in);

		color = new Color(in.readInt());

		flag = Serialize.readPath(in);

		authentication = AuthenticationType.valueOf(Serialize.readString(in));

		ldap = Serialize.readPath(in);

	}

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
			for (Team team : getContent()) {
				((Groups) getParent()).removeCached(team.getIdName());
			}
		} catch (MooshakContentException e) {
			Logger.getLogger("").log(Level.SEVERE,
					"Error while invalidating cache",e);
		}

		updateRankings();

		return false;
	}

	private void updateRankings() {
		try {
			Groups groups = getParent();
			Contest contest = groups.getParent();
			RankingPolicy ranking = contest.getSharedRankingPolicy();

			if (ranking != null)
				ranking.update();

		} catch (MooshakException cause) {
			Logger.getLogger("").log(Level.SEVERE, "Error while preparing to update rankings", cause);
		}
	}

	/*-------------------------------------------------------------------*\
	 * 		            Operations                                       *
	\*-------------------------------------------------------------------*/

	@MooshakOperation(name = "Generate Passwords to Archive", inputable = false, tip = "Generate passwords for all teams in this group, to"
			+ " an archive")
	private CommandOutcome generatePasswordsArchive() {
		CommandOutcome outcome = new CommandOutcome();

		Path template = null, config = null;
		try {
			Groups groups = getParent();

			if (groups.getPasswordTemplate() == null)
				groups.createDefaultPasswordTemplate();
			template = Paths.get(groups.getAbsoluteFile().toString(),
					groups.getPasswordTemplate().getFileName().toString());
			if (!Files.exists(template))
				groups.createDefaultPasswordTemplate();

			if (groups.getConfig() == null)
				groups.createDefaultConfig();
			config = Paths.get(groups.getAbsoluteFile().toString(), groups.getConfig().getFileName().toString());
			if (!Files.exists(config))
				groups.createDefaultConfig();

		} catch (MooshakException e) {
			outcome.setMessage("Error:" + e.getLocalizedMessage());
			return outcome;
		}

		Path zipPath = Paths.get(getAbsoluteFile().toString(), "Passwords.zip");
		ZipOutputStream out = null;
		try {
			out = new ZipOutputStream(new FileOutputStream(zipPath.toString()));
			out.setLevel(Deflater.DEFAULT_COMPRESSION);
		} catch (Exception cause) {
			outcome.setMessage("Cannot create Zip file:" + cause.getLocalizedMessage());
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
			outcome.setMessage("Cannot create Zip file:" + cause.getLocalizedMessage());
			return outcome;
		}

		outcome.setContinuation("GeneratePasswordArchiveDownload");

		return outcome;
	}

	@MooshakOperation(name = "GeneratePasswordArchiveDownload", inputable = true, show = false)
	private CommandOutcome generatePasswordArchiveDownload(MethodContext context) {
		return new CommandOutcome();
	}

	@MooshakOperation(name = "Generate Passwords to CSV", inputable = false, tip = "Generate passwords for all teams in this group, to"
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

	@MooshakOperation(name = "GeneratePasswordExcelDownload", inputable = true, show = false)
	private CommandOutcome generatePasswordCsvDownload(MethodContext context) {
		return new CommandOutcome();
	}

	@MooshakOperation(name = "Start Now", inputable = false, tip = "Set Start time to now in all teams in all groups")
	private CommandOutcome startNow() {
		CommandOutcome outcome = new CommandOutcome();

		Date now = new Date();

		int count = 0;
		for (Team team : getContent()) {
			team.setStart(now);

			try {
				team.save();
				count++;
			} catch (MooshakContentException e) {
			}
		}

		outcome.setMessage("Set start time now in " + count + " teams");

		return outcome;
	}

	@MooshakOperation(name = "Start Never", inputable = false, tip = "Set Start time to never in all teams in all groups")
	private CommandOutcome startNever() {
		CommandOutcome outcome = new CommandOutcome();

		int count = 0;
		for (Team team : getContent()) {
			team.setStart(null);

			try {
				team.save();
				count++;
			} catch (MooshakContentException e) {
			}
		}

		outcome.setMessage("Set start time never in " + count + " teams");

		return outcome;
	}

	/*-------------------------------------------------------------------*\
	 * 	Setters and getters				                              *
	\*-------------------------------------------------------------------*/

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
	 * Get group's designation
	 * 
	 * @return
	 */
	public String getDesignation() {
		if (designation == null)
			return "";
		else
			return designation;
	}

	/**
	 * Set groups designation (empty or null for default value)
	 * 
	 * @param designation
	 */
	public void setDesignation(String designation) {
		this.designation = designation;
	}

	/**
	 * Gets path to LDAP configuration
	 * 
	 * @return ldap object
	 * @throws MooshakContentException
	 */
	public LDAP getLdap() throws MooshakContentException {
		if (ldap == null)
			return null;
		else
			return openRelative("LDAP", LDAP.class);
	}

	/**
	 * Sets path to LDAP configuration
	 * 
	 * @param ldap
	 *            the ldap object to set
	 */
	public void setLdap(LDAP ldap) {
		if (ldap == null)
			this.ldap = null;
		else
			this.ldap = ldap.getId();
	}

	/**
	 * Get group's acronym
	 * 
	 * @return
	 */
	public String getAcronym() {
		if (acronym == null)
			if (designation == null)
				return "";
			else
				return Strings.acronymOf(designation);
		else
			return acronym;
	}

	/**
	 * Set groups acronym (empty or null for default value)
	 * 
	 * @param acronym
	 */
	public void setAcronym(String acronym) {
		this.acronym = acronym;
	}

	/**
	 * Get the color of this group
	 * 
	 * @return
	 */
	public Color getColor() {
		if (color == null)
			return Color.black;
		else
			return color;
	}

	/**
	 * Set the color of this group (null for default color)
	 * 
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Get flag of this group
	 * 
	 * @throws MooshakContentException
	 */
	public Flag getFlag() throws MooshakContentException {
		if (flag == null)
			return null;
		else
			return openRelative("Flag", Flag.class);
	}

	/**
	 * Change this submission's team (a file pointer) from the team object
	 * 
	 * @param team
	 */
	public void setFlag(Flag flag) {
		if (flag == null)
			this.flag = null;
		else {
			this.flag = flag.getId();
		}
	}

	/**
	 * Set authentication schema for this group
	 * 
	 * @return
	 */
	public AuthenticationType getAuthentication() {
		if (authentication == null)
			return AuthenticationType.BASIC;
		else
			return authentication;
	}

	/**
	 * Set authentication schema for this group
	 * 
	 * @param authetication
	 *            schema
	 */
	public void setAuthentication(AuthenticationType authetication) {
		this.authentication = authetication;
	}

	@Override
	protected void destroyed() throws MooshakContentException {

		try {
			for (Team team : getContent()) {
				((Groups) getParent()).removeCached(team.getIdName());
			}
		} catch (Exception e) {
			Logger.getLogger("").severe(e.getMessage());
		}

		super.destroyed();
	}

}