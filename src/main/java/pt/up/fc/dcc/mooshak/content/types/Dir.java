package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * A generic directory containing resources used in checks (tests)
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @since July 2013
 * @version 2.0
 *
 * Recoded in Java in         Octorber   2014
 * From a Tcl module coded in April 2005
 */
public class Dir extends PersistentObject {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute(
			name="Directory",
			type=AttributeType.CONTENT)
	private Void directory;
	
	@MooshakAttribute(
			name="File",
			type=AttributeType.CONTENT)
	private Void file;

}
