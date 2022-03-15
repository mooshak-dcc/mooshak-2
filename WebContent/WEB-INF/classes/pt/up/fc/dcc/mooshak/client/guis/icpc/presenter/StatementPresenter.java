package pt.up.fc.dcc.mooshak.client.guis.icpc.presenter;

import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementView;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class StatementPresenter implements Presenter, 
	StatementView.Presenter {

	private ParticipantCommandServiceAsync rpcService;
	//private HandlerManager eventBus;
	private StatementView view;
	private String problemId;
	//private String problemLabel;
	private boolean requestUpdate = false;

	private static Logger logger = Logger.getLogger("");
	
	public StatementPresenter(
			  ParticipantCommandServiceAsync rpcService, 
		      HandlerManager eventBus, 
		      StatementView view,
		      String problemId,
		      String problemLabel
			 ) {
		    this.rpcService = rpcService;
		    //this.eventBus = eventBus;
		    this.view = view;
		    this.problemId = problemId;
		    //this.problemLabel = problemLabel;
		    
		    this.view.setPresenter(this);
		  }
	
	@Override
	public void go(HasWidgets container) {
		// container.add(view.asWidget());// added in CardPanel by AppController
		setDependentData();
	}

	/**
	 * Set problem statement in this view
	 */
	private void setDependentData() {
		rpcService.view(problemId, true, new AsyncCallback<ProblemInfo>() {
			
			@Override
			public void onSuccess(ProblemInfo result) {
				if (result == null) {
					view.setStatement("Problem description " + problemId
							+ ": Problem description not available");
					return;
				}
				
				String html = result.getStatement();
				
				view.setProgramIdentification(
						result.getLabel(), 
						result.getTitle());
				
				if(result.isPDFviewable())
					view.setPDFStatement(problemId);
				else if(html != null && ! "".equals(html))
					view.setHTMLStatement(html,problemId);
				else 
					view.setStatement("No PDF or HTML file available");
			}
			
			@Override
			public void onFailure(Throwable caught) {
				view.setStatement("Error:"+caught.getMessage());
				logger.log(Level.SEVERE,caught.getMessage());
			}
		});
	}
	
	/**
	 * Updates the state to the given one
	 */
	public void updateStatement() {
		setDependentData();
		requestUpdate = false;
	}

	/**
	 * Clears the statement of the problem
	 */
	public void clearStatement() {
		view.setHTMLStatement("", problemId);
		requestUpdate = true;
	}
	
	/**
	 * @return the requestUpdate
	 */
	public boolean isRequestUpdate() {
		return requestUpdate;
	}
}