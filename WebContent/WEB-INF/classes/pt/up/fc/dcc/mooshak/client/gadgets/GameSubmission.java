package pt.up.fc.dcc.mooshak.client.gadgets;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.gamesubmission.GameSubmissionPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.gamesubmission.GameSubmissionView;
import pt.up.fc.dcc.mooshak.client.gadgets.gamesubmission.GameSubmissionViewImpl;
import pt.up.fc.dcc.mooshak.client.services.AsuraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.ResourceRatingContent;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;

public class GameSubmission extends Gadget implements HasLearningTime {

	private EnkiCommandServiceAsync enkiService;

	private Token token;

	private Timer timer = null;

	public GameSubmission(ParticipantCommandServiceAsync participantService, EnkiCommandServiceAsync enkiService,
			AsuraCommandServiceAsync asuraService, Token token, GadgetType type) {
		super(token, type);

		this.enkiService = enkiService;

		this.token = token;

		GameSubmissionView view = new GameSubmissionViewImpl();

		GameSubmissionPresenter presenter = new GameSubmissionPresenter(participantService, enkiService,
				asuraService, view, token);

		presenter.go(null);

		setView(view);
		setPresenter(presenter);
	}

	@Override
	public String getName() {
		return CONSTANTS.gameSubmission();
	}

	@Override
	public void setLearningTime(final Date date) {
		timer = new Timer() {

			@Override
			public void run() {
				if (date != null && date.getTime() > 0)
					notifySeenResource();
			}
		};

		if (date != null && date.getTime() > 0)
			timer.schedule((int) date.getTime());
		else
			timer.schedule(60 * 1000);
	}

	@Override
	public boolean hasExceededLearningTime() {
		if (timer != null && !timer.isRunning())
			return true;
		return false;
	}

	@Override
	public void notifySeenResource() {

		if (timer != null)
			timer.cancel();
		timer = null;

		final ResourceRatingContent ratingContent = new ResourceRatingContent();
		new OkCancelDialog(ratingContent, "Rate " + token.getName()) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				enkiService.sendFeedbackToResource(GadgetPresenter.getContextInfo().getactivityId(), token.getId(),
						ratingContent.getRating(), ratingContent.getComment(), new AsyncCallback<Feedback>() {

							@Override
							public void onSuccess(Feedback f) {

							}

							@Override
							public void onFailure(Throwable arg0) {

							}
						});
			}
		});

	}

	@Override
	public void stopTime() {
		if (timer != null && timer.isRunning())
			timer.cancel();
	}
}
