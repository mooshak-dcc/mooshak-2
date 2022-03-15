package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public interface TopLevelView extends View {
	
	public interface Presenter {		
		void onListingClicked(Kind kind);
		void onListingStatisticsClicked();
		void onContestSelectedChanged(String contest);
		void onCommentProblemClicked();
		void onLogoutClicked();
	}

	void setPresenter(Presenter presenter);
	Presenter getPresenter();
	void setContest(String name);
	void setContestSelector(List<SelectableOption> contests);
	
	CardPanel getContent();
	void setWaitingCursor();
	void unsetWaitingCursor();
	void setDates(Date start, Date end, Date current);
	
}
