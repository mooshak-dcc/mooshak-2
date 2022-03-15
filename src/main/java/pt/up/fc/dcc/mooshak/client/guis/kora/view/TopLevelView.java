package pt.up.fc.dcc.mooshak.client.guis.kora.view;

import java.util.Map;

import pt.up.fc.dcc.mooshak.client.View;

/**
 * Interface of the view of Kora in the Model-View Presenter architectural
 * pattern
 * 
 * @author josepaiva
 */
public interface TopLevelView extends View {
	
	public interface Presenter {
		
		/**
		 * Get limit quota for submissions
		 */
		void getSubmissionsTransactionsData();

		/**
		 * Submit diagram for evaluation
		 * @param consider for evaluation?
		 */
		void onDiagramEvaluate(boolean consider);
		
		/**
		 * Save diagram to local storage
		 * @param jsonGraph
		 */
		void saveToLocalStorage(String jsonGraph);
		
		/**
		 * Revert to last submission made
		 */
		void revertToLastSubmission();
			
	}

	void setPresenter(Presenter presenter);
		
	/**
	 * Set observations in the Observations view
	 * @param obs
	 */
	void setObservations(String obs);
	
	/**
	 * Import graph as JSON into view
	 * @param json
	 */
	void importGraphAsJson(String json);
	
	/**
	 * Import differences as JSON into view
	 * @param json
	 */
	void importGraphDiff(String json);
	
	/**
	 * Get graph from the view as JSON
	 * @return graph as JSON string
	 */
	String getGraphAsJSON();

	/**
	 * Set tooltip for submit button based on remaining requests and reset time
	 * @param remaining
	 * @param resetTime
	 */
	void setSubmitTooltip(int remaining, long resetTime);

	/* Counter of submissions waiting the summary */
	void increaseProgramWaitingEvaluation();
	void decreaseProgramWaitingEvaluation();
	
	/**
	 * Set languages and extensions allowed in the contest
	 * @param languages
	 */
	void setLanguages(Map<String, String> languages);

}
