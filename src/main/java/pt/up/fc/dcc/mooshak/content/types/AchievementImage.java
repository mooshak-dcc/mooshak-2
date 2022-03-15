package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentObject;

/**
 * Models an image of an achievement
 * Instances of this class are persisted locally
 * 
 * @author josepaiva
 */
public class AchievementImage extends PersistentObject {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute( 
    		name="Name",
    		tip="Name of achievement identified by this image")
    private String name = null;
	
	@MooshakAttribute( 
    		name="Image",
    		tip="Image in PNG format, .png extension",
    		help = "Image file names must have the same rootname as the " +
    				"folder name." +
    				"The file format must be PNG and its extension .png")
    Path image	= null;
	
	//-------------------- Setters and getters ----------------------//
	
    /**
     * Get the name of this achievement 
     * @return
     */
    public String getName() {
    	if(name == null)
    		return "";
    	else
    		return name;
	}
    
    /**
     * Set the name of this achievement; use null to revert to default
     */    
    public void setName(String name) {
    	this.name = name;
    }

    /**
     * Get PNG file containing thumb nail image of this achievement 
     * @return
     */
    public Path getImage() {
    	if(image==null)
    		return null;
    	else
    		return getPath().resolve(image);
    }
    
    /**
     * Set PNG file containing thumb nail image of this achievement 
     * @param image
     */
    public void setImage(Path image) {
    	this.image = image.getFileName();
    }

}
