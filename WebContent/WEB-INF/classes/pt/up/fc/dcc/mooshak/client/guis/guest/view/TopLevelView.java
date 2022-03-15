package pt.up.fc.dcc.mooshak.client.guis.guest.view;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public interface TopLevelView extends View {

	
	public interface Presenter {
		void onContestSelectedChanged(String contest);
		void onLogoutClicked();
	}

	void setPresenter(Presenter presenter);
	void setContestSelector(List<SelectableOption> contests);
	void setContest(String name);
	CardPanel getRankingPanel();
	CardPanel getSubmissionPanel();
	
}
