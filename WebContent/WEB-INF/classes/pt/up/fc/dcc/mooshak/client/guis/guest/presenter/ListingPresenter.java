package pt.up.fc.dcc.mooshak.client.guis.guest.presenter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.guis.guest.view.ListingView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;


public class ListingPresenter implements Presenter, ListingView.Presenter {

	private BasicCommandServiceAsync rpcService;
	private ListingView view;
	
	private static Logger logger = Logger.getLogger("");
	
	private Kind kind;

	public ListingPresenter(
			BasicCommandServiceAsync rpcBasic, 
			ListingView view,
			Kind kind) {
		this.rpcService = rpcBasic;
		this.view = view;
		this.kind = kind;
		
		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		//container.add(view.asWidget());// added in CardPanel by AppController
		setDependentData();
	}

	/**
	 * Set problem statement in this view
	 */
	private void setDependentData() {
		rpcService.getColumns(kind.toString(), 
				new AsyncCallback<List<ColumnInfo>>() {
			
			@Override
			public void onSuccess(List<ColumnInfo> result) {
				view.setColumns(result);				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				logger.log(Level.SEVERE,caught.getMessage());
			}
		});
	}
	
}

