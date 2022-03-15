package pt.up.fc.dcc.mooshak.client.guis.guest.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.guest.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class TopLevelPresenter
	implements Presenter, TopLevelView.Presenter {
	
	private BasicCommandServiceAsync rpcService;
	private TopLevelView view;
	protected Map<String, ResultsContestInfo> contestsInfo;
	
	private ICPCConstants constants = GWT.create(ICPCConstants.class);
	
	public TopLevelPresenter(
			  BasicCommandServiceAsync rpcBasic, 
		      HandlerManager eventBus, 
		      TopLevelView view
			 ) {
		    this.rpcService = rpcBasic;
		    /*this.eventBus = eventBus;*/
		    this.view = view;
		    
		    this.view.setPresenter(this);
		    
		    populateContestSelector();
		  }
	
	@Override
	public void go(final HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
		setDependentData();
	}
	
	
	/**
	 * Set contest name, team name and problems in top level 
	 */
	private void setDependentData() {
		rpcService.context(new AsyncCallback<ContextInfo>() {
			
			@Override
			public void onSuccess(ContextInfo result) {
				view.setContest(result.getactivityId());
			}
			
			@Override
			public void onFailure(Throwable caught) {				
				processfailure(caught);
			}
		});
	
	}
	
	@Override
	public void onContestSelectedChanged(String contest) {
		rpcService.changeContest(contest, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				AuthenticationPresenter.redirectTo("Scoreboard.html");
			}
			
		});
		
		setDependentData();
	}
	
	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	/**
	 * Set contest selector with list of available contests 
	 * returned by RPC call
	 */
	private void populateContestSelector() {
		rpcService.getDomains(false, false,
				new AsyncCallback<Map<String, ResultsContestInfo>>() {
			
			@Override
			public void onSuccess(Map<String, ResultsContestInfo> result) {
				contestsInfo = result;
				
				List<SelectableOption> options = new ArrayList<SelectableOption>();
				for (String key : result.keySet()) {
					options.add(new SelectableOption(result.get(key).getContestId(),
							result.get(key).getContestName()));
				}
				view.setContestSelector(options);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
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
	

}
