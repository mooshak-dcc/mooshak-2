package pt.up.fc.dcc.mooshak.client.gadgets.askquestion;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.views.Listing;

import com.google.gwt.view.client.SingleSelectionModel;

public interface AskView extends Listing, View {
	
	public interface Presenter {
		void onAsk();
		void onClear();

		void onSelectedItemChanged();
		void getQuestionsTransactionsData();
	}
	
	void setPresenter(Presenter presenter);
	
	/**
	 * Gets the question text in textarea
	 * 
	 * @return question text
	 */
	String getQuestion();
	
	/**
	 * Cleans the question textarea
	 */
	void clearQuestion();

	/**
	 * Gets the subject text in textbox
	 * 
	 * @return subject text
	 */
	String getSubject();
	
	/**
	 * Cleans the subject textbox
	 */
	void clearSubject();

	/**
	 * Cleans the answer textbox
	 */
	void clearAnswer();
	
	/**
	 * Sets the label message text
	 * 
	 * @param message
	 * @param result
	 */
	void setMessage(String message,boolean result);

	void clearSelectedItem();
	SingleSelectionModel<Row> getSelectionModel();
	void setSubject(String subject);
	void setQuestion(String question);
	void setAnswer(String answer);

	void setAskTooltip(int remaining, long resetTime);
	void setProblemId(String problemId);
	
	void setFiltering();

	
}
