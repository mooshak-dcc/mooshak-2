package pt.up.fc.dcc.mooshak.client.gadgets.mysubmissions;

import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.mysubmissions.MySubmissionsView.Presenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ChangeEditorContentEvent;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;

public class MySubmissionsPresenter extends GadgetPresenter<MySubmissionsView> implements Presenter {

	public MySubmissionsPresenter(ParticipantCommandServiceAsync participantService,
			BasicCommandServiceAsync basicService, MySubmissionsView view, Token token, HandlerManager eventBus) {
		super(eventBus, basicService, participantService, null, null, null, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	/**
	 * Set columns
	 */
	private void setDependentData() {

		basicService.getColumns(Kind.SUBMISSIONS.toString(), new AsyncCallback<List<ColumnInfo>>() {

			@Override
			public void onSuccess(List<ColumnInfo> result) {
				view.setColumns(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		getParticipantLogged();
		getProblemNameById();
	}

	/**
	 * Retrieve the problem name by id
	 */
	private void getProblemNameById() {
		basicService.getProblemNameById(problemId, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				if (result != null)
					view.setProblemName(result);
				else
					view.setProblemName("??");
			}

			@Override
			public void onFailure(Throwable caught) {
				view.setProblemName("??");
			}
		});
	}

	@Override
	public void getParticipantLogged() {
		participantService.getParticipantLogged(new AsyncCallback<String>() {

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
	public void onReplaceSubmission(String id, String team) {
		eventBus.fireEvent(new ChangeEditorContentEvent(id, team, problemId));
	}
}
