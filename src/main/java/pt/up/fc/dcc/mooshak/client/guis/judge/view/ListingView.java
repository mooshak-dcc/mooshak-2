package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.views.Listing;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;

import com.google.gwt.view.client.SingleSelectionModel;

public interface ListingView extends Listing, View {

	public interface Presenter {
		void onSelectedItemChanged();
		void onViewStatementClicked(String id, String label);
		void onInfoSubmissionClicked(String id, String label, 
				String problemId);
		void onDetailQuestionClicked(String id, String label);
		void onRegisterDeliveryClicked(String id, Kind kind);
	}

	SingleSelectionModel<Row> getSelectionModel();

	void setPresenter(Presenter presenter);
	
	CardPanel getDetail();

	Kind getKind();

}
