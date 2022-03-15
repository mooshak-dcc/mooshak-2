package pt.up.fc.dcc.mooshak.content.types;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Skeletons extends PersistentContainer<Skeleton> {
	private static final long serialVersionUID = 1L;
	
	@MooshakAttribute( 
			name="Skeleton",
			type=AttributeType.CONTENT)
	private Void skeleton;
	

	@MooshakAttribute(
			name="Show",
			type=AttributeType.MENU,
			tip="Show skeletons to participants",
			help="Use this field to allow users to see the skeletons\n" +
					"while they code their solutions in Mooshak.\n"
			)
	private YesNo show = MooshakAttribute.YesNo.YES;
	
	
	//-------------------- Setters and getters ----------------------//
	


	/**
	 * Are skeletons to be shown? Defaults to {@code true} 
	 * @return
	 */
	public boolean isShowing() {
		if(YesNo.NO.equals(show))	
			return false;
		else
			return true;
	}


	/**
	 * Set skeletons to be shown. Use  {@code null} to revert to default.
	 */
	public void setShowing(boolean show) {
		if(!show)
			this.show = YesNo.NO;
		else
			this.show = YesNo.YES;
	}
	
	/**
	 * Gets the skeleton related to a language extension
	 * @param langExtension
	 * @param program Is for showing in program?
	 * @return
	 * @throws MooshakContentException
	 */
	public Skeleton getSkeleton(String langExtension, boolean program)
			throws MooshakContentException {
		
		Skeleton generalSkeleton = null;
		
		for (Skeleton skeleton : getChildren(Skeleton.class, true)) {
			
			if(program && !skeleton.isShowing())
				continue;
			
			if(skeleton.getExtension().equals(langExtension))
				return skeleton;
			else if(skeleton.getExtension().equals(""))
				generalSkeleton = skeleton;
		}
		
		return generalSkeleton;
	}
	
	/**
	 * Gets all skeletons provided
	 * @param program Is for showing in program?
	 * @return
	 * @throws MooshakContentException
	 */
	public List<Skeleton> getAllSkeletons(boolean program)
			throws MooshakContentException {
		
		List<Skeleton> skeletons = new ArrayList<>();
		
		for (Skeleton skeleton : getChildren(Skeleton.class, true)) {
			
			if (program && !skeleton.isShowing())
				continue;
			
			skeletons.add(skeleton);
		}
		
		return skeletons;
	}

	/**
	 * 
	 * @param name
	 * @param program
	 * @return
	 * @throws MooshakException
	 */
	public Skeleton getSkeletonByName(String name, boolean program)
			throws MooshakException {
		
		Skeleton generalSkeleton = null;
		
		for (Skeleton skeleton : getChildren(Skeleton.class, true)) {
			
			if(program && !skeleton.isShowing())
				continue;
			
			if(skeleton.getIdName().equals(name))
				return skeleton;
			else if(skeleton.getExtension().equals(""))
				generalSkeleton = skeleton;
		}
		
		return generalSkeleton;
	}

}
