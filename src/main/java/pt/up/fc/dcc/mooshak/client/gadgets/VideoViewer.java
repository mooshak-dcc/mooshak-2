package pt.up.fc.dcc.mooshak.client.gadgets;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.videoviewer.VideoPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.videoviewer.VideoView;
import pt.up.fc.dcc.mooshak.client.gadgets.videoviewer.VideoViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.ResourceRatingContent;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;

/**
 * Gadget for viewing videos
 * 
 * @author josepaiva
 */
public class VideoViewer extends Gadget implements HasLearningTime {

	private ParticipantCommandServiceAsync rpc;
	private EnkiCommandServiceAsync enkiService;

	private Token token;

	private Timer timer = null;

	public VideoViewer(ParticipantCommandServiceAsync rpcService, EnkiCommandServiceAsync enkiService,
			HandlerManager eventBus, Token token, GadgetType type) {
		super(token, type);

		this.token = token;
		this.rpc = rpcService;
		this.enkiService = enkiService;

		// view and presenter
		VideoView view = new VideoViewImpl(token.getName(), token.getLink());

		VideoPresenter presenter = new VideoPresenter(rpc, enkiService, view, token);
		presenter.go(null);

		setPresenter(presenter);
		setView(view);

	}

	@Override
	public String getName() {
		return CONSTANTS.videoPlayer();
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

		enkiService.notifyResourceSeen(GadgetPresenter.getContextInfo().getactivityId(), token.getId(),
				new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void arg0) {

						final ResourceRatingContent ratingContent = new ResourceRatingContent();
						new OkCancelDialog(ratingContent, "Rate " + token.getName()) {
						}.addDialogHandler(new ClickHandler() {

							@Override
							public void onClick(ClickEvent arg0) {
								enkiService.sendFeedbackToResource(GadgetPresenter.getContextInfo().getactivityId(),
										token.getId(), ratingContent.getRating(), ratingContent.getComment(),
										new AsyncCallback<Feedback>() {

											@Override
											public void onSuccess(Feedback f) {

											}

											@Override
											public void onFailure(Throwable arg0) {

											}
										});
							}
						});
						EventManager.getInstance().refresh();
					}

					@Override
					public void onFailure(Throwable arg0) {

					}
				});
	}

	@Override
	public void stopTime() {
		if (timer != null && timer.isRunning())
			timer.cancel();
	}

}
