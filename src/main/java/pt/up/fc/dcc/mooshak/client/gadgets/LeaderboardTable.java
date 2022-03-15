package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.leaderboard.LeaderboardPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.leaderboard.LeaderboardView;
import pt.up.fc.dcc.mooshak.client.gadgets.leaderboard.LeaderboardViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;

/**
 * Gadget for leaderboard representation
 * 
 * @author josepaiva
 */
public class LeaderboardTable extends Gadget {

	private static LeaderboardTable leaderboard = null;

	private LeaderboardTable(EnkiCommandServiceAsync rpc, Token token, GadgetType type) {
		super(token, type);

		LeaderboardView view = new LeaderboardViewImpl();

		LeaderboardPresenter presenter = new LeaderboardPresenter(rpc, view, token);

		presenter.go(null);

		setView(view);
		setPresenter(presenter);
	}

	public static LeaderboardTable getInstance(EnkiCommandServiceAsync rpc, Token token, GadgetType type) {
		if (leaderboard == null)
			leaderboard = new LeaderboardTable(rpc, token, type);
		return leaderboard;
	}

	@Override
	public String getName() {
		return CONSTANTS.leaderboard();
	}

}
