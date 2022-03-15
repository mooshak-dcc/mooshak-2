package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;

import com.google.gwt.user.client.rpc.IsSerializable;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Handles authentication. Authenticates given a password and returns a profile.
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public interface Authenticable extends IsSerializable {

	/**
	 * Checks if this password authenticates the object
	 * @param password
	 * @return
	 * @throws MooshakContentException
	 * @throws MooshakException 
	 */
	public boolean authentic(String password) throws MooshakContentException, MooshakException;
	
	/**
	 * Returns ID of who was authenticated (login)
	 * @return
	 * @throws MooshakContentException
	 */
	public String getIdName() throws MooshakContentException ;
	
	/**
	 * Return name of who was authenticated
	 * @return
	 * @throws MooshakContentException
	 */
	public String getName() throws MooshakContentException ;
	
	/**
	 * Returns a profile for this object
	 * @return
	 * @throws MooshakContentException
	 */
	public Profile getProfile() throws MooshakContentException ;

	/**
	 * Get path of authenticable PO
	 * @return
	 * @throws MooshakContentException
	 */
	public Path getPath();
}
