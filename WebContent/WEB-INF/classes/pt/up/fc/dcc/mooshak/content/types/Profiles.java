

package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * User profile 
 * Instances of this class are persisted locally
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 * Recoded in Java in         November  2013
 * From a Tcl module coded in April 2001
 */
public class Profiles extends PersistentContainer<Profile> {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute(
			name="Profile",
			type=AttributeType.CONTENT)
	public Void profile;
	
}