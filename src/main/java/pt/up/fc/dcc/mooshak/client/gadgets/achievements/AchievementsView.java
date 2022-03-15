package pt.up.fc.dcc.mooshak.client.gadgets.achievements;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementEntry;

public interface AchievementsView extends View {
	
	public interface Presenter {
		void updateAchievements();
	}
	
	void setPresenter(Presenter presenter);

	void addAchievements(List<AchievementEntry> achievements);
	
	void clearAchievements();
	void clearUnlockedAchievements();
	void clearRevealedAchievements();

}
