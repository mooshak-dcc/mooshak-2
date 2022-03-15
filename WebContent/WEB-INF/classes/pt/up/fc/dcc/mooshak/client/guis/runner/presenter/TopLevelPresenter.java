package pt.up.fc.dcc.mooshak.client.guis.runner.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.ListingSelectedEvent;
import pt.up.fc.dcc.mooshak.client.guis.runner.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
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
	
	private ICPCConstants constants = GWT.create(ICPCConstants.class);

	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private TopLevelView view;

	protected Map<String, ResultsContestInfo> contestsInfo;
	
	private Date startDate = null;
	private Date stopDate = null;

	public TopLevelPresenter(
			  JudgeCommandServiceAsync rpcService, 
		      BasicCommandServiceAsync rpcBasic, HandlerManager eventBus, 
		      TopLevelView view
			 ) {
		    this.rpcBasic = rpcBasic;
		    this.eventBus = eventBus;
		    this.view = view;
		    
		    this.view.setPresenter(this);
		    
		    populateContestSelector();
		  }
	
	@Override
	public void go(final HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
		setDependentData();

		onListingClicked(Kind.PENDING);
	}
	
	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @return the stopDate
	 */
	public Date getStopDate() {
		return stopDate;
	}
	
	
	/**
	 * Set contest name and problems in top level 
	 */
	private void setDependentData() {
		updateContext();
		
		rpcBasic.context(new AsyncCallback<ContextInfo>() {
			
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
	
	public void updateContext() {
		rpcBasic.context(new AsyncCallback<ContextInfo>() {
			
			@Override
			public void onSuccess(ContextInfo result) {
				view.setDates(result.getStart(),result.getEnd(),
						result.getCurrent());
				
				if (result.getCurrent() != null) {
					long diff = new Date().getTime() - result.getCurrent().getTime();
					
					if (result.getStart() != null)
						startDate = new Date(result.getStart().getTime() + diff);
					if (result.getEnd() != null)
						stopDate = new Date(result.getEnd().getTime() + diff);
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {				
				processfailure(caught);
			}
		});

		
	}
	
	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	@Override
	public void onListingClicked(Kind kind) {
		eventBus.fireEvent(new ListingSelectedEvent(kind));
	}

	@Override
	public void onContestSelectedChanged(String contest) {
		rpcBasic.changeContest(contest, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				AuthenticationPresenter.redirectTo("Runner.html");
			}
			
		});
		
		setDependentData();
	}

	/**
	 * Set contest selector with list of available contests 
	 * returned by RPC call
	 */
	private void populateContestSelector() {
		rpcBasic.getDomains(false, true,
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
