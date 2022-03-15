package pt.up.fc.dcc.mooshak.client.guis.admin.presenter;

/**
 * Top level presenter
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.guis.admin.event.ShowObjectEvent;
import pt.up.fc.dcc.mooshak.client.guis.admin.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class TopLevelPresenter 
	implements Presenter, TopLevelView.Presenter {
	
	private static final String CREATOR = "creator";
	private static final String JUDGE = "judge";
	
	private ICPCConstants constants = GWT.create(ICPCConstants.class);

	private AdminCommandServiceAsync rpcService;
	private HandlerManager eventBus;
	private TopLevelView view;
	
	// private static final Logger LOGGER = Logger.getLogger("");

	public TopLevelPresenter(
			  AdminCommandServiceAsync rpcService, 
		      HandlerManager eventBus, 
		      TopLevelView view
			 ) {
		    this.rpcService = rpcService;
		    this.eventBus = eventBus;
		    this.view = view;
		    
		    this.view.setPresenter(this);
		  }
	
	@Override
	public void go(final HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
		
		setVersion();
	}
	
	


	private void setVersion() {
		BasicCommandServiceAsync basic = GWT.create(BasicCommandService.class);
		
		basic.getVersion(new AsyncCallback<String>() {
			
			@Override
			public void onSuccess(String version) {
				view.setVersion(version);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}
		});
	}

	@Override
	public void onSelectedObject(String id) {
		eventBus.fireEvent(new ShowObjectEvent(id));
		view.setSelectedObjectId(id);
	}

	@Override
	public void onCommand(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFind(String term,boolean nameNotContent) {
		final Map<Style, Cursor> cursors = 
				AbstractAppController.setCursorsToWait();
		
		rpcService.findMooshakObjectIds(term, nameNotContent, 
				new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						AbstractAppController.resetCursors(cursors);
						
					}

					@Override
					public void onSuccess(List<String> result) {
						view.setFoundList(result);
						AbstractAppController.resetCursors(cursors);
					}
		});
	}
	
	@Override
	public void onLogoutClicked() {
		new OkCancelDialog(constants.logoutConfirmation()) {
		}.addDialogHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				AuthenticationPresenter.logout();
			}
		});
	}

	@Override
	public void onGoToJudgeClicked(String contest) {
		rpcService.switchProfile(contest, JUDGE, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void arg0) {
				AuthenticationPresenter.redirectTo("Judge.html");
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				Window.alert("Couldn't load judge profile.");
			}
		});
	}

	@Override
	public void onGoToCreatorClicked(String contest) {
		rpcService.switchProfile(contest, CREATOR, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void arg0) {
				AuthenticationPresenter.redirectTo("Creator.html");
			}
			
			@Override
			public void onFailure(Throwable arg0) {
				Window.alert("Couldn't load creator profile.");
			}
		});
	}
	
}
