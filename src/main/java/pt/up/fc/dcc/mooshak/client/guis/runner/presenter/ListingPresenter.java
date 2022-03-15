package pt.up.fc.dcc.mooshak.client.guis.runner.presenter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ViewStatementEvent;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.RegisterDeliveryEvent;
import pt.up.fc.dcc.mooshak.client.guis.runner.view.ListingView;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class ListingPresenter implements Presenter, ListingView.Presenter {

	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private ListingView view;
	private Kind kind;

	private static final Logger LOGGER = Logger.getLogger("");

	public ListingPresenter(JudgeCommandServiceAsync rpcService,
			BasicCommandServiceAsync rpcBasic, 
			ListingView view, HandlerManager eventBus, Kind kind) {
		this.rpcBasic = rpcBasic;
		this.view = view;
		this.eventBus = eventBus;
		this.kind = kind;

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		// container.add(view.asWidget());// added in CardPanel by AppController
		view.getDetail().clear();
		setDependentData();
	}

	/**
	 * Set problem statement in this view
	 */
	private void setDependentData() {
		rpcBasic.getColumns(kind.toString(),
				new AsyncCallback<List<ColumnInfo>>() {

					@Override
					public void onSuccess(List<ColumnInfo> result) {
						view.setColumns(result);
					}

					@Override
					public void onFailure(Throwable caught) {
						LOGGER.log(Level.SEVERE, caught.getMessage());
					}
				});
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	@Override
	public void onViewStatementClicked(final String id, final String label) {
		eventBus.fireEvent(new ViewStatementEvent(id, label));
	}

	@Override
	public void onRegisterDeliveryClicked(final String id, final Kind kind) {
		eventBus.fireEvent(new RegisterDeliveryEvent(id, kind));
	}

	@Override
	public void onSelectedItemChanged() {
		Row selected = view.getSelectionModel().getSelectedObject();
		if (selected != null) {
			String id = selected.getId(); 
			
			if(id == null)
				return;
			
			Kind selectedKind;
			if (kind == Kind.PENDING) {
				selectedKind = Kind.valueOf(id.substring(id.lastIndexOf("_")+1));
				id = id.replaceAll("_" + selectedKind.toString(), "");
			} else
				selectedKind = kind;
			
			switch (selectedKind) {
			case BALLOONS:
				onRegisterDeliveryClicked(id, selectedKind);
				break;
			case PRINTOUTS:
				onRegisterDeliveryClicked(id, selectedKind);
				break;
			default:
				break;
			}
		}
	}

}
