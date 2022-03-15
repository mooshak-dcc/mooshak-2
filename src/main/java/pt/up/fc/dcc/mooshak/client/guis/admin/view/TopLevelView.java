package pt.up.fc.dcc.mooshak.client.guis.admin.view;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.results.ServerStatus;

/**
 * Top level view of admin app 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface TopLevelView extends View {
	
	public interface Presenter {
		void onSelectedObject(String id);
		void onGoToJudgeClicked(String contest);
		void onGoToCreatorClicked(String contest);
		void onCommand(String id);
		void onFind(String term,boolean nameNotcontent);
		void onLogoutClicked();
	}

	void setPresenter(Presenter presenter);
	void updateCellBrowser(String id);
	CardPanel getContent();

	void setSelectedObjectId(String id);
	void setServerStatus(ServerStatus status);
	void setFoundList(List<String> found);
	void setVersion(String version);
}
