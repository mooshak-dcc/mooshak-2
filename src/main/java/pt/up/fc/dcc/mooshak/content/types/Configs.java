package pt.up.fc.dcc.mooshak.content.types;


import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Configs extends PersistentObject {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute(
			name="checks",
			type=AttributeType.DATA)
	private Checks checks = null;

	@MooshakAttribute(
			name="network",
			type=AttributeType.DATA)
	private Network network = null;

	@MooshakAttribute(
			name="profiles",
			type=AttributeType.DATA)
	private Profiles profiles = null;
	
	@MooshakAttribute(
			name="sessions",
			type=AttributeType.DATA)
	private Sessions sessions = null;
	@MooshakAttribute(
			name="users",
			type=AttributeType.DATA)
	private Users users = null;
	
	@MooshakAttribute(
			name="flags",
			type=AttributeType.DATA)
	private Flags flags = null;
	
	@MooshakAttribute(
			name="achievements",
			type=AttributeType.DATA)
	private AchievementsImages achievements = null;
	
	@MooshakAttribute(
			name="ldap",
			type=AttributeType.DATA)
	private LDAPs ldap = null;
	
	

	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}