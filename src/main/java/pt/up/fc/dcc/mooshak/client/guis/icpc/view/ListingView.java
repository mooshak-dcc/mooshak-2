package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.views.Listing;

public interface ListingView extends Listing, View {

	public interface Presenter {
		void getParticipantLogged();
		void onReplaceSubmission(String id, String team, String problemId);
		void getShowOwnCode();
	}
	
	void setPresenter(Presenter presenter);
	void setParticipant(String participant);
	void setShowOwnCode(boolean booleanValue);
	
}
