package pt.up.fc.dcc.mooshak.client.gadgets.viewstatement;

import java.util.logging.Level;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.viewstatement.StatementView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class StatementPresenter extends GadgetPresenter<StatementView> implements Presenter {

	private boolean requestUpdate = false;

	public StatementPresenter(ParticipantCommandServiceAsync participantService, EnkiCommandServiceAsync enkiService,
			StatementView view, Token token) {
		super(null, null, participantService, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	/**
	 * Set problem statement in this view
	 */
	private void setDependentData() {
		if (type.equals(ResourceType.PDF)) {
			view.setExternalResource(link);
			return;
		}

		participantService.view(problemId, true, new AsyncCallback<ProblemInfo>() {

			@Override
			public void onSuccess(ProblemInfo result) {
				String html = result.getStatement();

				view.setProgramIdentification(result.getLabel(), result.getTitle());

				if (result.isPDFviewable())
					view.setPDFStatement(problemId);
				else if (html != null && !"".equals(html))
					view.setHTMLStatement(html, problemId);
				else
					view.setStatement("No PDF or HTML file available");
			}

			@Override
			public void onFailure(Throwable caught) {
				view.setStatement("Error:" + caught.getMessage());
				LOGGER.log(Level.SEVERE, caught.getMessage());
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