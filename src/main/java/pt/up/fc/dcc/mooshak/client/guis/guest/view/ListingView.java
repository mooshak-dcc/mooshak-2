package pt.up.fc.dcc.mooshak.client.guis.guest.view;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.views.Listing;

public interface ListingView extends Listing, View {

	public interface Presenter {

	}
	
	void setPresenter(Presenter presenter);
	
}
