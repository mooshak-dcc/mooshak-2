package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Solutions extends PersistentObject {
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
			name="Solution",
			type=AttributeType.CONTENT)
	private Void solution;
	


	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}
