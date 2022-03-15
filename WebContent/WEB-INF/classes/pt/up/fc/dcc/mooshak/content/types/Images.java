package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Images extends PersistentObject {
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
			name="Image",
			type=AttributeType.CONTENT)
	private Void image;


	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}
