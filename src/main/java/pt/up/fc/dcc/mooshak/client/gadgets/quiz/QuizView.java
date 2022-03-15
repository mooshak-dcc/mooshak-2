package pt.up.fc.dcc.mooshak.client.gadgets.quiz;

import java.util.Date;

import pt.up.fc.dcc.mooshak.client.View;

/**
 * Interface of the view of Quiz in the Model-View Presenter architectural
 * pattern
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface QuizView extends View {

	public interface Presenter {

		/**
		 * Get limit quota for submissions
		 */
		void getSubmissionsTransactionsData();

		/**
		 * Submit a quiz for evaluation
		 * 
		 * @param json JSON String with answers
		 * @param consider Consider submission for evaluation
		 */
		void onQuizEvaluate(String json, boolean consider);

		/**
		 * Set observations in the Observations view
		 * 
		 * @param obs
		 */
		void setObservations(String obs);

		/**
		 * Save diagram to local storage
		 * 
		 * @param json JSON String with answers 
		 */
		void saveToLocalStorage(String json);

	}

	void setPresenter(Presenter presenter);

	/**
	 * Import quiz answers as JSON into view
	 * 
	 * @param json JSON String with answers
	 */
	void importAnswersAsJson(String json);

	/**
	 * Get quiz answers from the view as JSON
	 * 
	 * @return json JSON String with answers
	 */
	String getAnswersAsJSON();

	/**
	 * Set tooltip for submit button based on remaining requests and reset time
	 * 
	 * @param remaining
	 * @param resetTime
	 */
	void setSubmitTooltip(int remaining, long resetTime);

	/* Counter of submissions waiting the summary */
	void increaseQuizWaitingEvaluation();
	void decreaseQuizWaitingEvaluation();
	
	void createQuizzes(String html, String problemId);
	
	public void setDates(Date start, Date end, Date current) ;
}
