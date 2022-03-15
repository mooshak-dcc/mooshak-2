package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.util.Password;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * An user (other than teams or students) in a contest 
 * Instances of this class are persisted locally
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 * Recoded in Java in         July  2013
 * From a Tcl module coded in April 2001
 */
public class User extends PersistentObject implements Authenticable {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute( 
    		name="Name",
    		tip="Name of user (this is NOT the ID)")
    private String name = null;
	
	@MooshakAttribute( 
    		name="Password",
    		type=AttributeType.PASSWORD,
    		tip="Password of this user")
    private String password = null;
	
	@MooshakAttribute(
			name="Profile",
			type=AttributeType.PATH,
			refType = "Profile",
			complement="/data/configs/profiles")
	private Path profile = null;
	
	//-------------------- Setters and getters ----------------------//
	
	/**
     * Get the name of the user (this is not an ID)
     * @return
     */
    public String getName() {
    	if(name == null)
    		return "";
    	else
    		return name;
	}
    
    /**
     * Set the name of the user; use null to revert to default
     */    
    public void setName(String name) {
    	this.name = name;
    }
    
    /**
     * Get the password of this user as an hash (not as plain text)
     * Use <code>authenticate()</code> to compare a plain text with
     * saved hash.
     * @return
     */
    public String getPassword() {
    	if(password == null)
    		return "";
    	else
    		return password;
	}
    
    /**
     * Set the password of this user as plain text.
     * Password are stored as hashes.
     * Use null to revert to default
     */    
    public void setPassword(String plain) {
    	this.password = Password.crypt(plain);
    }
    
    /**
	 * Get the profile of this user
	 * @throws MooshakContentException 
	 */
    @Override
	public Profile getProfile() throws MooshakContentException {
		if(profile == null)
			return null;
		else 
			return openRelative("Profile", Profile.class);
	}
	
	/**
	 * Change the profile (a file pointer) of this user
	 * @param profile
	 */
	public void setProfile(Profile profile) {
		if(profile == null)
			this.profile = null;
		else {
			this.profile = profile.getId();
		}
	}

	/**
	 * Authenticate user by comparing given password in clear text with
	 * stored hash. 
	 * @param password in clear text
	 * @return
	 */
	@Override
	public boolean authentic(String plain) {
		
		return Password.match(password, plain);
	}
}
