package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.types.Submission.State;

/**
 * Interface implemented by submission containers
 *  
 * @author josepaiva
 */
public interface HasSubmissions {


	/**
	 * The default state assigned to submissions
	 * @return
	 */
	public State getDefaultState();
	
}
