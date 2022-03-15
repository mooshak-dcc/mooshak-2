package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * A collection of flags 
 * Instances of this class are persisted locally
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 * Recoded in Java in         July  2013
 * From a Tcl module coded in April 2005
 */
public class Flags extends PersistentContainer<Flag> {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(
			name="Flag",
			type=AttributeType.CONTENT)
	private Void flag;
	
}
