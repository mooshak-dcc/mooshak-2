package pt.up.fc.dcc.mooshak.client.gadgets.achievements;

import java.util.logging.Level;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.achievements.AchievementsView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementsListResponse;

public class AchievementsPresenter extends GadgetPresenter<AchievementsView> implements Presenter {

	public AchievementsPresenter(EnkiCommandServiceAsync enkiService, AchievementsView view, Token token) {
		super(null, null, null, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		updateAchievements();
	}

	@Override
	public void updateAchievements() {
		view.clearAchievements();
		updateAchievementsUnlocked();
		updateAchievementsRevealed();
	}

	private void updateAchievementsUnlocked() {
		enkiService.getAchievementsUnlocked(contextInfo.getactivityId(), null, new AsyncCallback<AchievementsListResponse>() {

			@Override
			public void onSuccess(AchievementsListResponse result) {

				view.addAchievements(result.getItems());
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE, caught.getMessage());
			}
		});
	}

	private void updateAchievementsRevealed() {
		enkiService.getAchievementsRevealed(contextInfo.getactivityId(), null, new AsyncCallback<AchievementsListResponse>() {

			@Override
			public void onSuccess(AchievementsListResponse result) {

				view.addAchievements(result.getItems());
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE, caught.getMessage());
			}
		});
	}

}
