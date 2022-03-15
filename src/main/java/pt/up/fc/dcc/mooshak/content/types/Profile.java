package pt.up.fc.dcc.mooshak.content.types;

import java.util.EnumSet;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * User profile. Declares commands the users of this profile can execute,
 * and their default screen (not being used)
 * 
 * Instances of this class are persisted locally
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 * Recoded in Java in         July  2013
 * From a Tcl module coded in April 2001
 */
public class Profile extends PersistentObject {
	private static final long serialVersionUID = 1L;
	
	public enum Screen {  ADMIN, JUDGE, TEACHER, TEAM, EXAM, RUNNER, GUEST;
	
		public String toString() {
			return super.toString().toLowerCase();
		}
	};
	
	public enum Command { 
		ADMIN,ANALYZE,ANSWER,ASK,ASKED,
		BANNER,
		CHECK,CLONE,CODE,CONFIG,CONTENT,COPY,CUT,
		DATA,DESCRIPTION,
		EDIT,EMPTY,EXPORT,EXPORTING,
		FAQ,FEEDBACK,FILE,FLAG,FORM,FREEZE,
		GUEST,GRADE,HTOOLS,HELP,IMAGE,IMPORT,
		IMPORTING,INSPECT,
		JUDGE,
		LISTING,LOGIN,LINK,LOGOUT,MESSAGE,
		OPERATION,
		PASTE,PRINT,
		REGISTER,REMOVE,RESET,REPORT,RUNNER,
		SMS,
		TARGET,TEAM,TOP,
		UNDO,UNFREEZE,VIEW,VTOOLS,
		REDO,RELOGIN,
		SPLIT,
		WARN, WARNED;
		
		public String toString() {
			return super.toString().toLowerCase();
		}
	}; 
	
	@MooshakAttribute(
			name="Screen",
			type=AttributeType.MENU)
	private Screen screen = null;
	
	@MooshakAttribute(
			name="Authorized",
			type= AttributeType.LIST,
			tip ="list of authorized commands")
	private EnumSet<Command> authorized = null;
	// TODO review authorization mechanism

	

	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		
		for (Screen screen : Screen.values()) {
			if (screen.name().equalsIgnoreCase(getIdName()))
				return false;
		}
		return true;
	}
}
