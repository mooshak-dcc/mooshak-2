package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * Container of {@link Opponent}s.
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Opponents extends PersistentContainer<Opponent> {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning;

	@MooshakAttribute(name = "opponent", type = AttributeType.CONTENT)
	private Void opponent;

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
}
