package pt.up.fc.dcc.mooshak.client.gadgets.achievements;

import java.util.ArrayList;
import java.util.List;

import org.gwtbootstrap3.client.ui.Column;
import org.gwtbootstrap3.client.ui.Image;
import org.gwtbootstrap3.client.ui.Row;
import org.gwtbootstrap3.client.ui.constants.ImageType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementEntry;

public class AchievementsViewImpl extends ResizeComposite
	implements AchievementsView {
	
	public static final int ACHIEVEMENTS_TO_SHOW = 8;

	private static AchievementsUiBinder uiBinder = GWT.create(AchievementsUiBinder.class);

	@UiTemplate("AchievementsView.ui.xml")
	interface AchievementsUiBinder extends UiBinder<Widget, AchievementsViewImpl> {
	}
	
	interface BaseStyle extends CssResource {
		String imgAchievement();
	}
	
	@UiField
	static BaseStyle style; 
	
	private Presenter presenter;
	
	@UiField
	Row rowAchievements;
	
	public AchievementsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	private List<Column> unlockeds = new ArrayList<>();
	private List<Column> revealeds = new ArrayList<>();

	@Override
	public void addAchievements(List<AchievementEntry> achievements) {
		
		int i = rowAchievements.getElement().getChildCount();
		for (AchievementEntry achievement : achievements) {
			if (i >= ACHIEVEMENTS_TO_SHOW)
				break;
			Column column = new Column("XS_3 SM_3 MD_3 LG_3");
			
			String uri = "achievements/" + (achievement.getAchievementState().equals("UNLOCKED") ? 
					achievement.getAchievement().getUnlockedIconUrl() : 
					achievement.getAchievement().getRevealedIconUrl());
			SafeUri safeUri = UriUtils.fromTrustedString(uri);
			Image image = new Image(safeUri);
			image.setAltText(achievement.getAchievement().getDescription());
			image.setType(ImageType.ROUNDED);
			image.setStyleName(style.imgAchievement());
			
			column.add(image);
			if (achievement.getAchievementState().equals("UNLOCKED"))
				unlockeds.add(column);
			else
				revealeds.add(column);
			rowAchievements.add(column);
			i++;
		}
	} 

	
	@Override
	public void clearAchievements() {
		rowAchievements.getElement().removeAllChildren();
	}

	@Override
	public void clearUnlockedAchievements() {
		for (Column column : unlockeds) {
			column.removeFromParent();
		}
		unlockeds.clear();
	}

	@Override
	public void clearRevealedAchievements() {
		for (Column column : revealeds) {
			column.removeFromParent();
		}
		revealeds.clear();
	}
	
	

}
