package pt.up.fc.dcc.mooshak.content.types;


import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Top extends PersistentObject {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute( 
			name="Fatal", 
			type=AttributeType.LABEL)
	private String fatal = null;

	@MooshakAttribute( 
			name="Warning",
			type=AttributeType.LABEL)
	private String warning = null;


	@MooshakAttribute(
			name="configs",
			type=AttributeType.DATA)
	private Configs configs = null;

	@MooshakAttribute(
			name="contests",
			type=AttributeType.DATA)
	private Contests contests = null;

	@MooshakAttribute(
			name="trash",
			type=AttributeType.DATA)
	private Void trash = null;


	//-------------------- Setters and getters ----------------------//

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
	 * Warning errors messages of this folder
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
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}


	@Override
	protected void created() throws MooshakContentException { 
		StringBuilder builder = new StringBuilder();

		checkSafeexec(builder);

		fatal = builder.toString();
	};

	// ---------------------  Checks ------------------------------ //


	private void checkSafeexec(StringBuilder builder) {
		boolean ok = true;

		try {
			ok = SafeExecution.isSafexecOk();
		} catch(MooshakException cause) {
			builder.append(cause.getLocalizedMessage());
		}

		if(!ok)
			builder.append("Safeexec without root permission."+
					"Execute as root \n\n chmod u+s,o+x "+
					SafeExecution.getSafeexec());

	}


	//------------------------- Operations -------------------------- // 



	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}
