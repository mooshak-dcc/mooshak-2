package pt.up.fc.dcc.mooshak.client.guis.creator.presenter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.creator.event.ViewProblemEvent;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.CreatorCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;

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
	
	private static final int MAX_FILE_SIZE = 5 * 1024 * 1024;
	
	private ICPCConstants constants = GWT.create(ICPCConstants.class);

	private CreatorCommandServiceAsync rpcService;
	private BasicCommandServiceAsync rpcBasic;
	private ParticipantCommandServiceAsync rpcParticipant;
	private HandlerManager eventBus;
	private TopLevelView view;

	protected Map<String, ResultsContestInfo> contestsInfo;

	public TopLevelPresenter(
			CreatorCommandServiceAsync rpcService, 
		      BasicCommandServiceAsync rpcBasic, 
		      ParticipantCommandServiceAsync rpcParticipant,
		      HandlerManager eventBus, 
		      TopLevelView view,
		      String objectId
			 ) {
		    this.rpcService = rpcService;
		    this.rpcBasic = rpcBasic;
		    this.rpcParticipant = rpcParticipant;
		    this.eventBus = eventBus;
		    this.view = view;
		    
		    this.view.setPresenter(this);
		    
		    this.view.setProblemsObjectId(objectId);
		    
		    populateContestSelector();
		  }
	
	@Override
	public void go(final HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
		setDependentData();
		setDependentData("contest");
	}
	
	
	/**
	 * Set contest name and problems in top level 
	 */
	private void setDependentData() {
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
		
		getProblems();		
	}

	/**
	 * Get available programs
	 */
	private void getProblems() {
		rpcParticipant.getProblems(new AsyncCallback<List<SelectableOption>>() {
			
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
	
	
	/**
	 * Set contest name and problems in top level 
	 */
	private void setDependentData(String contest) {
		rpcBasic.context(new AsyncCallback<ContextInfo>() {
			
			@Override
			public void onSuccess(ContextInfo result) {
				view.setContest(result.getactivityName());
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
	public void onContestSelectedChanged(String contest) {
		
		if(contest.equals(""))
			return;
		
		view.setWaitingCursor();
		
		rpcBasic.changeContest(contest, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
				view.unsetWaitingCursor();
			}

			@Override
			public void onSuccess(Void result) {
				AuthenticationPresenter.redirectTo("Creator.html");
				view.unsetWaitingCursor();
			}
			
		});
		
		setDependentData(contest);
	}

	/**
	 * Set contest selector with list of available contests 
	 * returned by RPC call
	 */
	private void populateContestSelector() {
		rpcBasic.getDomains(true, true, 
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
	public void onAddProblemClicked(String objectId) {
		rpcService.createNewDefaultProblem(objectId, 
				new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
			}

			@Override
			public void onSuccess(String result) {
				getProblems();
				onViewProblemClicked(result, result);
			}
		});
		
	}

	@Override
	public void onDeleteProblemClicked(String id) {
		
		rpcService.deleteProblem(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				AuthenticationPresenter.redirectTo("Creator.html");
			}
		});
	}

	@Override
	public void onViewProblemClicked(String id, String text) {
		eventBus.fireEvent(new ViewProblemEvent(id, text));
	}

	@Override
	public void onLogoutClicked() {
		new OkCancelDialog(constants.logoutConfirmation()) {
		}.addDialogHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				rpcBasic.isLoggedInAsAdmin(new AsyncCallback<Boolean>() {
					
					@Override
					public void onSuccess(Boolean isAdmin) {
						if (!isAdmin)
							AuthenticationPresenter.logout();
						else {
							rpcBasic.switchProfileBackToAdmin(new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable arg0) {
									AuthenticationPresenter.logout();
								}

								@Override
								public void onSuccess(Void arg0) {
									AuthenticationPresenter.redirectTo("Admin.html");
								}
							});
						}
							
					}
					
					@Override
					public void onFailure(Throwable arg0) {
						AuthenticationPresenter.logout();
					}
				});
			}
		});
	}

	private boolean uploading = false;
	
	@Override
	public void uploadFile(String name, byte[] content) {
		if(content.length > MAX_FILE_SIZE) {
			view.setMessage("File size exceeded", true);
			return;
		}
		
		if (uploading) {
			Logger.getLogger("").log(Level.SEVERE, "Already Uploading");
			return;
		}
		
		final Map<Style, Cursor> cursors = AbstractAppController.setCursorsToWait();
		
		view.setMessage("Uploading " + name + " ...", false);
		uploading = true;
		rpcService.uploadFile(view.getProblemsObjectId(), content, name, null, 
				new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setMessage(caught.getMessage(), true);
				
				AbstractAppController.resetCursors(cursors);
				uploading = false;
			}
	
			@Override
			public void onSuccess(MooshakValue result) {
				view.setMessage("Upload Completed!", true);
				EventManager.getInstance().refresh();
				
				AbstractAppController.resetCursors(cursors);
				uploading = false;
			}
		});
		
		
	}
	
}
