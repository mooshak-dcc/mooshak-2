package pt.up.fc.dcc.mooshak.client.gadgets.diagrameditor;

import java.util.Map;

import pt.up.fc.dcc.mooshak.client.View;

public interface DiagramEditorView extends View {
	
	public interface Presenter {

		void onDiagramEvaluate(boolean consider);
		void getSubmissionsTransactionsData();
		
		void setObservations(String obs);
		void revertToLastSubmission();
		void saveToLocalStorage(String jsonGraph);
	}

	void setPresenter(Presenter presenter);
	
	void setSubmitTooltip(int remaining, long resetTime);

	void increaseProgramWaitingEvaluation();
	void decreaseProgramWaitingEvaluation();
	
	void importGraphAsJson(String obs);
	void importGraphDiff(String json);
	String getGraphAsJSON();
	
	void setLanguages(Map<String, String> languages);
	Map<String, String> getLanguages();


}
