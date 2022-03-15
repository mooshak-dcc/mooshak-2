package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public interface TopLevelView extends HasTutorial, View {
	
	public interface Presenter {
		void onViewStatementClicked(String id, String label);
		void onEditProgramClicked(String id, String label);
		void onAskQuestionClicked(String id, String label);
		
		void onListingClicked(Kind kind);
		void onHelpClicked();
		void onLogoutClicked();
	}

	void setPresenter(Presenter presenter);
	void setContest(String name);
	void setTeam(String name);
	void setProblems(List<SelectableOption> problemOptions);
	CardPanel getContent();
	void showTutorial();
	void showNotification(String message);
	void setDates(Date start, Date end, Date current);
	void setVersion(String versionText);
	void selectProgramEditor(String problemId);
}
