package pt.up.fc.dcc.mooshak.content.types;


import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Network extends PersistentObject {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(name="Name")
	private String name = null;
	
	@MooshakAttribute(
			name="Definition",
			type=AttributeType.FILE)
	private Path definition = null;
	
	@MooshakAttribute(
			name="reports",
			type=AttributeType.DATA)
	private Void reports = null;
	
	@MooshakAttribute(
			name="resources",
			type=AttributeType.DATA)
	private Void resources = null;
	
	@MooshakAttribute(
			name="Server",
			type=AttributeType.DATA)
	private Path server = null;
	
	
	


	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}