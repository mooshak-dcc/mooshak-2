package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.PersistentObject;

/**
 * Folder containing removed persistent objects
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Trash extends PersistentObject {
	private static final long serialVersionUID = 1L;


	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return false;
	}
}
