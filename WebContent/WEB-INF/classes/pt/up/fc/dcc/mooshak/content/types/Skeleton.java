package pt.up.fc.dcc.mooshak.content.types;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * A skeleton of the solution used as part of the program
 * submitted by teams. This skeleton can be given to teams
 * or used automatically in compilation.
 * 
 * @author josepaiva
 */
public class Skeleton extends PersistentObject {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(
			name="Skeleton",
			type=AttributeType.FILE)
	private Path skeleton = null;
	

	@MooshakAttribute(
			name="Show",
			type=AttributeType.MENU,
			tip="Show skeleton to participants",
			help="Use this field to allow users to see this skeleton\n" +
					"while they code their solutions in Mooshak.\n"
			)
	private YesNo show = MooshakAttribute.YesNo.YES;

	@MooshakAttribute(
			name = "Extension", 
			tip = "File extension associated with the skeleton (ex: java")
	private String extension = null;
	
	
	//-------------------- Setters and getters ----------------------//
	
	/**
	 * Is skeleton to be shown? Defaults to {@code true} 
	 * @return
	 */
	public boolean isShowing() {
		if(YesNo.NO.equals(show))	
			return false;
		else
			return true;
	}


	/**
	 * Set skeleton to be shown. Use  {@code null} to revert to default.
	 */
	public void setShowing(boolean show) {
		if(!show)
			this.show = YesNo.NO;
		else
			this.show = YesNo.YES;
	}

	/**
	 * Get extension associated with the skeleton (ex: java") 
	 * @return
	 */
	public String getExtension() {
		if(extension == null)
			return "";
		else
			return extension;
	}

	/**
	 * Set extension associated with the skeleton (ex: java) 
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}
	
	/**
	 * Set path to skeleton file
	 */
	public void setSkeleton(Path skeleton) {
		if(skeleton != null)
			this.skeleton = skeleton.getFileName();
		else 
			this.skeleton = skeleton;
	}
	
	public String getSkeletonCode() {
		if (skeleton == null)
			return "";
		String code;
		try {
			code = new String(
						Files.readAllBytes(
								getAbsoluteFile().resolve(skeleton.getFileName())
										));
		} catch (IOException e) {
			code = "";
		}
		
		return code;
	}
	
}
