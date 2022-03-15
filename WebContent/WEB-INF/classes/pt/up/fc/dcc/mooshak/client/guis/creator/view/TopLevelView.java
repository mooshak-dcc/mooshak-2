package pt.up.fc.dcc.mooshak.client.guis.creator.view;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public interface TopLevelView extends View {

	public interface Presenter {
		void onAddProblemClicked(String objectId);

		void onDeleteProblemClicked(String id);

		void onContestSelectedChanged(String id);

		void onViewProblemClicked(String id, String text);
		void onLogoutClicked();
		void uploadFile(String name, byte[] content);
	}

	void setPresenter(Presenter presenter);
	Presenter getPresenter();
	
	void setProblemsObjectId(String id);
	String getProblemsObjectId();
	
	void setProblemId(String id);
	String getProblemId();

	void setContest(String name);

	void setProblems(List<SelectableOption> result);
	void selectProblem(String problem);

	void setContestSelector(List<SelectableOption> contests);

	CardPanel getContent();
	void updateTabName(String id, String name);
	void setWaitingCursor();
	void unsetWaitingCursor();
	
	void addProblemId(String problemId);
	
	void setMessage(String msg, boolean expires);

}
