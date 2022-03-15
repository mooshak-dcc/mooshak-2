package pt.up.fc.dcc.mooshak.client.gadgets.leaderboard;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;

public interface LeaderboardView extends View {

	public interface Presenter {
		
		void updateLeaderboard();
	}
	
	void setPresenter(Presenter presenter);
	
	void updateLeaderboard(LeaderboardResponse resp);

}
