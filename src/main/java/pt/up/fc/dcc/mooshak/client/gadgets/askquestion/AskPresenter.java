package pt.up.fc.dcc.mooshak.client.gadgets.askquestion;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Row;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.askquestion.AskView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;

public class AskPresenter extends GadgetPresenter<AskView> implements Presenter {

	public AskPresenter(ParticipantCommandServiceAsync participantService, BasicCommandServiceAsync basicService,
			AskView view, Token token, String problemLabel) {
		super(null, basicService, participantService, null, null, null, null, view, token);

		this.view.setPresenter(this);
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

		new OkCancelDialog(ICPC_MESSAGES.askConfirmation(view.getSubject(), problemId)) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				view.setMessage(ICPC_CONSTANTS.processing(), true);

				participantService.ask(problemId, view.getSubject(), view.getQuestion(), new AsyncCallback<Void>() {

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
				view.setMessage(ICPC_CONSTANTS.submitted(), true);
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

		basicService.getColumns(Kind.QUESTIONS.toString(), new AsyncCallback<List<ColumnInfo>>() {

			@Override
			public void onSuccess(List<ColumnInfo> result) {
				view.setColumns(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}

	@Override
	public void onSelectedItemChanged() {
		Row selected = view.getSelectionModel().getSelectedObject();
		if (selected != null) {
			String id = selected.getId();
			if (id == null)
				return;

			participantService.getAnsweredQuestion(id, new AsyncCallback<MooshakObject>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());
				}

				@Override
				public void onSuccess(MooshakObject result) {
					view.setSubject(result.getFieldValue("Subject").getSimple());
					view.setQuestion(result.getFieldValue("Question").getSimple());
					view.setAnswer(result.getFieldValue("Answer").getSimple());
				}
			});
		}

	}

	@Override
	public void getQuestionsTransactionsData() {
		participantService.getTransactionsData("questions", new AsyncCallback<TransactionQuota>() {

			@Override
			public void onFailure(Throwable caught) {
				view.setAskTooltip(-1, -1);
			}

			@Override
			public void onSuccess(TransactionQuota result) {
				if (result == null)
					return;

				view.setAskTooltip(result.getTransactionsLimit() - result.getTransactionsUsed(),
						result.getTimeToReset());
			}
		});
	}
}
