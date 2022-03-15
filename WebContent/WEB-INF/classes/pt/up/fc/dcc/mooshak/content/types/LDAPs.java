package pt.up.fc.dcc.mooshak.content.types;


import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class LDAPs extends PersistentContainer<LDAP> {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(
			name="LDAP",
			type=AttributeType.CONTENT)
	private Void ldap = null;
	
}