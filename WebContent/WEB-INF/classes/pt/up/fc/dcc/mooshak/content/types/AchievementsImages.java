package pt.up.fc.dcc.mooshak.content.types;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * A collection of images of achievements
 * Instances of this class are persisted locally
 * 
 * @author josepaiva
 */
public class AchievementsImages extends PersistentContainer<AchievementImage> {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(
			name="Image",
			type=AttributeType.CONTENT)
	private Void image;
}
