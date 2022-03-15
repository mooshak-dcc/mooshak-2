package pt.up.fc.dcc.mooshak.client.gadgets.kora;

import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

/**
 * Interface of the view of Kora in the Model-View Presenter architectural
 * pattern
 * 
 * @author josepaiva
 */
public interface KoraView extends View {

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
		 * Set observations in the Observations view
		 * @param obs
		 */
		void setObservations(String obs);
		
		/**
		 * Save diagram to local storage
		 * @param jsonGraph
		 * @param lang
		 */
		void saveToLocalStorage(String jsonGraph, String lang);
		
		/**
		 * Revert to last submission made
		 */
		void revertToLastSubmission();
		
		
		
		void onSkeletonSelectedChanged(String id);
	}

	void setPresenter(Presenter presenter);
	
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
	
	
	/**
	 * config and create a editor 
	 * @param languages
	 */
	 void createEshu(ConfigInfo eshuConfig);
	 
	 /**
	  * set option in skeleton (seleclabelOption)
	  * @param options
	  */
	 void setSkeletonOptions(List<SelectableOption> options);

	void setDefaultLanguage(String language);
	
	boolean setSelectedLanguage(String id);

	String getValueSkeleton();

}
