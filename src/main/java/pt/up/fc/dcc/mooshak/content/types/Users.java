package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * A collection of users (other than teams or students) in contest
 * Instances of this class are persisted locally
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 * Recoded in Java in         July  2013
 * From a Tcl module coded in April 2001
 */
public class Users extends PersistentContainer<User> {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute(
			name="User",
			type=AttributeType.CONTENT)
	private Void content;
	
}
