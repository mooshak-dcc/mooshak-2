package pt.up.fc.dcc.mooshak.client.gadgets.leaderboard;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.leaderboard.LeaderboardView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;

public class LeaderboardPresenter extends GadgetPresenter<LeaderboardView> implements Presenter {

	public LeaderboardPresenter(EnkiCommandServiceAsync enkiService,
			LeaderboardView view, Token token) {
		
		super(null, null, null, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		updateLeaderboard();
	}

	@Override
	public void updateLeaderboard() {
		enkiService.getLeaderboard(contextInfo.getactivityId(), null,
				new AsyncCallback<LeaderboardResponse>() {
			
			@Override
			public void onSuccess(LeaderboardResponse result) {
				view.updateLeaderboard(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				//Logger.getLogger("").log(Level.SEVERE, caught.getMessage());
			}
		});
	}

	
}
