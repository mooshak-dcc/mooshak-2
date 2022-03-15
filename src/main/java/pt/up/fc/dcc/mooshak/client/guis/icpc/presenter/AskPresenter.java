package pt.up.fc.dcc.mooshak.client.guis.icpc.presenter;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCMessages;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.AskView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class AskPresenter implements Presenter, AskView.Presenter {

	private static final Logger LOGGER = Logger.getLogger("");
	
	private ParticipantCommandServiceAsync rpcService;
	private BasicCommandServiceAsync rpcBasic;
	private AskView view;
	private String problemId;
	private String problemLabel;

	private ICPCConstants constants = GWT.create(ICPCConstants.class);
	private ICPCMessages messages = GWT.create(ICPCMessages.class);

	public AskPresenter(ParticipantCommandServiceAsync rpcService,
			BasicCommandServiceAsync rpcBasic, HandlerManager eventBus,
			AskView view, String problemId, String problemLabel) {
		this.rpcBasic = rpcBasic;
		this.rpcService = rpcService;
		this.view = view;
		this.setProblemId(problemId);
		this.problemLabel = problemLabel;
		
		this.view.setPresenter(this);
		this.view.setProblemLabel(this.problemLabel);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
		getQuestionsTransactionsData();
	
		
	}
	

	/**
	 * Send a question for remote server with data collected from the view
	 * 
	 */
	public void onAsk() {
		
		if (view.getSubject().equals("")) {
			view.setMessage("Without subject", false);
			return;
		}
		
		if (view.getQuestion().equals("")) {
			view.setMessage("Without question", false);
			return;
		}

		new OkCancelDialog(messages.askConfirmation(view.getSubject(), 
				problemId)) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				view.setMessage(constants.processing(), true);

				rpcService.ask(problemId, view.getSubject(),
						view.getQuestion(), new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								view.setMessage(caught.getMessage(), false);
								getQuestionsTransactionsData();
							}

							@Override
							public void onSuccess(Void result) {
								EventManager.getInstance().refresh();
								getQuestionsTransactionsData();
							}

						});
				view.setMessage(constants.submitted(), true);
			}
		});
	}

	/**
	 * Clean the form from the view
	 */
	public void onClear() {
		view.clearSubject();
		view.clearQuestion();
		view.clearAnswer();
	}

	/**
	 * Set columns
	 */
	private void setDependentData() {
		
		rpcService.view(problemId, false, new AsyncCallback<ProblemInfo>() {
			
			@Override
			public void onSuccess(ProblemInfo result) {

				view.setProgramIdentification(
						result.getLabel(), 
						result.getTitle());
			}
			
			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE,caught.getMessage());
			}
		});
		
		rpcBasic.getColumns(Kind.QUESTIONS.toString(),
				new AsyncCallback<List<ColumnInfo>>() {

					@Override
					public void onSuccess(List<ColumnInfo> result) {
						view.setColumns(result);
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
	}

	/**
	 * @return the problemId
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param problemId
	 *            the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	@Override
	public void onSelectedItemChanged() {
		Row selected = view.getSelectionModel().getSelectedObject();
		if (selected != null) {
			String id = selected.getId();
			if (id == null)
				return;

			rpcService.getAnsweredQuestion(id,
					new AsyncCallback<MooshakObject>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(MooshakObject result) {
							view.setSubject(result.getFieldValue("Subject")
									.getSimple());
							view.setQuestion(result.getFieldValue("Question")
									.getSimple());
							view.setAnswer(result.getFieldValue("Answer")
									.getSimple());
						}
					});
		}

	}

	@Override
	public void getQuestionsTransactionsData() {
		rpcService.getTransactionsData("questions",
				new AsyncCallback<TransactionQuota>() {

					@Override
					public void onFailure(Throwable caught) {
						view.setAskTooltip(-1, -1);
					}

					@Override
					public void onSuccess(TransactionQuota result) {
						if (result == null)
							return;

						view.setAskTooltip(result.getTransactionsLimit()
								- result.getTransactionsUsed(),
								result.getTimeToReset());
					}
				});
	}
}
