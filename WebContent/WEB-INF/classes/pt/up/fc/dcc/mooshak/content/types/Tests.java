package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * A container of tests for validating a submission
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * 
 * Recoded in Java in         June  2012
 * From a Tcl module coded in April 2001
 */
public class Tests extends PersistentContainer<Test> {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute( 
			name="Fatal", 
			type=AttributeType.LABEL)
	public String fatal = null;
	
	@MooshakAttribute( 
			name="Warning",
			type=AttributeType.LABEL)
	public String warning = null;

	@MooshakAttribute( 
			name="Definition")
	public Void definition = null;
	
	@MooshakAttribute(
			name="Test",
			type=AttributeType.CONTENT)
	public Void test;

}
