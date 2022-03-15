package pt.up.fc.dcc.mooshak.client.guis.runner.view;

import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public interface TopLevelView extends View {
	
	public interface Presenter {		
		void onListingClicked(Kind kind);
		void onContestSelectedChanged(String contest);
		void onLogoutClicked();
	}

	void setPresenter(Presenter presenter);
	Presenter getPresenter();
	void setContest(String name);
	void setContestSelector(List<SelectableOption> contests);
	void setDates(Date start, Date end, Date current);
	
	CardPanel getContent();
	
}
