package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.achievements.AchievementsPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.achievements.AchievementsView;
import pt.up.fc.dcc.mooshak.client.gadgets.achievements.AchievementsViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;

public class AchievementList extends Gadget {

	private static AchievementList achievementList = null;

	private AchievementList(EnkiCommandServiceAsync rpcEnki, Token token, GadgetType type) {
		super(token, type);

		AchievementsView view = new AchievementsViewImpl();

		AchievementsPresenter presenter = new AchievementsPresenter(rpcEnki, view, token);

		presenter.go(null);

		setPresenter(presenter);
		setView(view);
	}

	public static AchievementList getInstance(EnkiCommandServiceAsync rpcEnki, Token token, GadgetType type) {
		if (achievementList == null)
			achievementList = new AchievementList(rpcEnki, token, type);

		return achievementList;
	}

	@Override
	public String getName() {
		return CONSTANTS.achievements();
	}
}
