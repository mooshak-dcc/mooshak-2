package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * Holds the compilation result of a submission {@link Submission} that will run
 * against the current {@link Submission}. 
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Opponent extends PersistentObject {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning;

	@MooshakAttribute(
			name = "Submission", 
			type = AttributeType.PATH, 
			refType = "Submission", 
			complement = "../../../../submissions")
	private Path submission = null;

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
	 * Gets the {@link Submission} submission that this {@link Opponent} refers to
	 * 
	 * @return submission
	 * @throws MooshakContentException
	 */
	public Submission getSubmission() throws MooshakContentException {
		if(submission == null)
			return null;
		else 
			return openRelative("Submission", Submission.class);
	}
	
	/**
	 * Change the {@link Submission} submission that this {@link Opponent} refers to
	 * 
	 * @param submission
	 */
	public void setSubmission(Submission submission) {
		if(submission == null)
			this.submission = null;
		else
			this.submission = submission.getId();
	}
}
