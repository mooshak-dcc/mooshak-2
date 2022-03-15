package pt.up.fc.dcc.mooshak.client.gadgets.resourcerating;

import pt.up.fc.dcc.mooshak.client.View;

public interface ResourceRatingView extends View {
	
	public interface Presenter {
		void onSubmit(int rating, String comment);
		void updateResourcesFeedback();
	}
	
	void setPresenter(Presenter presenter);
	
	void setRating(int rating, String comment);

	void setMessage(String text);

}
