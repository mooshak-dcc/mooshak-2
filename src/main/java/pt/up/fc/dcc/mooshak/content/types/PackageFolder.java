package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * Package folder
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class PackageFolder extends PersistentObject {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning;
	
	@MooshakAttribute(
			name="Program",
			type=AttributeType.FILE)
	protected Path program = null;

	/*-------------------------------------------------------------------*\
	 * 		            Setters and getters                              *
	\*-------------------------------------------------------------------*/

	/**
	 * Fatal errors messages of this folder
	 * 
	 * @return the fatal
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
	 * @return the warning
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
	 * Get the program file
	 * @return
	 */
	public Path getProgram() {
		if(program == null)
			return null;
		else
			return getPath().resolve(program);
	}
	
	/** 
	 * Convenience method for returning program name as a string 
	 * @return name of program or {@code null} if undefined
	 */
	public String getProgramName() {
		String name = null;
		Path resolvedProgram = getProgram(); 
		
		if(resolvedProgram  != null) {
			 Path fileName = resolvedProgram.getFileName();
			 
			 if(fileName != null)
				 name = fileName.toString();
		}
	
		return name;
	}

	/**
	 * Sets this program's file
	 * @param program
	 */
	public void setProgram(Path program) {
		if (program == null) 
			this.program = program;
		else
			this.program = program.getFileName();
	}
}
