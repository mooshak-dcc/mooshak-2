package pt.up.fc.dcc.mooshak.client.guis.icpc.presenter;

import java.util.Date;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.AskQuestionEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.EditProgramEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.HelpTutorialEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ListingSelectedEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ViewStatementEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
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

	private ParticipantCommandServiceAsync rpcService;
	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private TopLevelView view;

	private String contestId;
	private String teamId;
	
	private Date startDate = null;
	private Date stopDate = null;

	public TopLevelPresenter(
			  ParticipantCommandServiceAsync rpcService, 
		      BasicCommandServiceAsync rpcBasic, 
		      HandlerManager eventBus, 
		      TopLevelView view
			 ) {
		    this.rpcService = rpcService;
		    this.rpcBasic = rpcBasic;
		    this.eventBus = eventBus;
		    this.view = view;
		    
		    this.view.setPresenter(this); 
		  }
	
	@Override
	public void go(final HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
		setDependentData();
	
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
	 * Set contest name, team name and problems in top level 
	 */
	private void setDependentData() {
			
		updateContext();
		
		rpcService.getProblems(new AsyncCallback<List<SelectableOption>>() {
			
			@Override
			public void onSuccess(List<SelectableOption> result) {
				view.setProblems(result);
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
	
	/**
	 * @return the contestId
	 */
	public String getContestId() {
		return contestId;
	}

	/**
	 * @param contestId the contestId to set
	 */
	public void setContestId(String contestId) {
		this.contestId = contestId;
	}

	/**
	 * @return the teamId
	 */
	public String getTeamId() {
		return teamId;
	}

	/**
	 * @param teamId the teamId to set
	 */
	public void setTeamId(String teamId) {
		this.teamId = teamId;
	}

	public void updateContext() {
		rpcBasic.context(new AsyncCallback<ContextInfo>() {

			@Override
			public void onSuccess(ContextInfo result) {
				setContestId(result.getactivityId());
				setTeamId(result.getParticipantId());
				view.setContest(result.getactivityName());
				view.setTeam(result.getParticipantName());
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
	
	
	@Override
	public void onViewStatementClicked(final String id,final String label) {
		eventBus.fireEvent(new ViewStatementEvent(id,label));
	}

	@Override
	public void onEditProgramClicked(final String id,final String label) {
		eventBus.fireEvent(new EditProgramEvent(id));
	}

	@Override
	public void onAskQuestionClicked(final String id,final String label) {
		eventBus.fireEvent(new AskQuestionEvent(id,label));
	}

	@Override
	public void onListingClicked(Kind kind) {
		eventBus.fireEvent(new ListingSelectedEvent(kind));
	}

	@Override
	public void onHelpClicked() {
		eventBus.fireEvent(new HelpTutorialEvent());
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
