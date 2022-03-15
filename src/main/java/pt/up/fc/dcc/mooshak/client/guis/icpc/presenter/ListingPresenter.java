package pt.up.fc.dcc.mooshak.client.guis.icpc.presenter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ChangeEditorContentEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.ListingView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class ListingPresenter implements Presenter, ListingView.Presenter {

	private ParticipantCommandServiceAsync rpc;
	private BasicCommandServiceAsync rpcBasic;
	private ListingView view;
	private Kind kind;
	private HandlerManager eventBus;
	
	private static Logger logger = Logger.getLogger("");

	public ListingPresenter(
			ParticipantCommandServiceAsync rpc, 
			BasicCommandServiceAsync rpcBasic, ListingView view, 
			Kind kind, HandlerManager eventBus) {
		this.rpc = rpc;
		this.rpcBasic = rpcBasic;
		this.view = view;
		this.kind = kind;
		this.eventBus = eventBus;
		
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
		rpcBasic.getColumns(kind.toString(), new AsyncCallback<List<ColumnInfo>>() {
			
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

	@Override
	public void getParticipantLogged() {
		rpc.getParticipantLogged(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(String result) {
				view.setParticipant(result);
			}
		});
	}

	@Override
	public void getShowOwnCode() {
		rpc.getShowOwnCode(new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(Boolean result) {
				view.setShowOwnCode(result.booleanValue());
			}
		});
	}

	@Override
	public void onReplaceSubmission(String id, String team, String problemId) {
		eventBus.fireEvent(new ChangeEditorContentEvent(id, team, problemId));
	}
	
}
