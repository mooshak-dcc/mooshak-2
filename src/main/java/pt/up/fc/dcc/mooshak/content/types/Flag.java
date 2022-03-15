package pt.up.fc.dcc.mooshak.content.types;


import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

/**
 * Models a flag used as icon for a group
 * Instances of this class are persisted locally
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @since July 2013
 * @version 2.0
 *
 * Recoded in Java in         July   2013
 * From a Tcl module coded in April 2005
 */
public class Flag extends PersistentObject {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute( 
    		name="Name",
    		tip="Name of entity identified by this flag")
    private String name = null;
	
	@MooshakAttribute( 
    		name="ISO_code",
    		tip="ISO code of this flag")
	private String isoCode = null;
	
	
	@MooshakAttribute( 
    		name="Image",
    		tip="Flag in PNG format, .png extension and same name as code",
    		help = "Image file names must have the same rootname as the " +
    				"folder name and ISO code." +
    				"The file format must be PNG and its extension .png")
    Path image	= null;
	
	//-------------------- Setters and getters ----------------------//
	
    /**
     * Get the name of this flag 
     * @return
     */
    public String getName() {
    	if(name == null)
    		return "";
    	else
    		return name;
	}
    
    /**
     * Set the name of this flag; use null to revert to default
     */    
    public void setName(String name) {
    	this.name = name;
    }
    
    /**
     * Get the isoCode of this flag 
     * @return
     */
    public String getIsoCode() {
    	if(isoCode == null)
    		return "";
    	else
    		return isoCode;
	}
    
    /**
     * Set the isoCode of this flag; use null to revert to default
     * @param isoCode
     */    
    public void setIsoCode(String isoCode) {
    	this.isoCode = isoCode;
    }

    /**
     * Get PNG file containing thumb nail image of this flag 
     * @return
     */
    public Path getImage() {
    	if(image==null)
    		return null;
    	else
    		return getPath().resolve(image);
    }
    
    /**
     * Set PNG file containing thumb nail image of this flag 
     * @param image
     */
    public void setImage(Path image) {
    	this.image = image.getFileName();
    }

	/*************************************
	 *       Overriden Methods			 *
	 *************************************/
	
	@Override
	public boolean isRenameable() {
		return !getIdName().equals("00");
	}
}
