package pt.up.fc.dcc.mooshak.content.types;


import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Checks extends PersistentObject {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute(
			name="Check",
			type=AttributeType.DATA)
	private Path check = null;
	
	
	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}