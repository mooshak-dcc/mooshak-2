package pt.up.fc.dcc.mooshak.client.gadgets.mysubmissions;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.views.Listing;

public interface MySubmissionsView extends Listing, View {

	public interface Presenter {
		void getParticipantLogged();
		void onReplaceSubmission(String id, String team);
	}
	
	void setPresenter(Presenter presenter);
	void setParticipant(String participant);
	
	void setProblemName(String name);
	
	void setFiltering();
	
}
