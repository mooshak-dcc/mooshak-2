package pt.up.fc.dcc.mooshak.client.gadgets.gamesubmission;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.View;

public interface GameSubmissionView extends View {

	public interface Presenter {

		void onProgramEvaluate(boolean consider);
		
		void getSubmissionsTransactionsData();
		void getValidationsTransactionsData();
	}
	
	void setPresenter(Presenter presenter);
	
	void setSubmitTooltip(int remaining, long resetTime);
	void setValidateTooltip(int remaining, long resetTime);

	void increaseProgramWaitingEvaluation();
	void decreaseProgramWaitingEvaluation();

	List<String> getSelectedOpponents();
	void setOpponents(Map<String, String> result);
	void addOpponent(String id, String teamId);
	
}
