package pt.up.fc.dcc.mooshak.client.gadgets;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.viewstatement.StatementPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.viewstatement.StatementView;
import pt.up.fc.dcc.mooshak.client.gadgets.viewstatement.StatementViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.ResourceRatingContent;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Feedback;

/**
 * Gadget to view problems statements
 * 
 * @author josepaiva
 */
public class ViewStatement extends Gadget implements HasLearningTime {
	public static final String STATEMENT_NAME = "Statement";
	public static final String RESOURCE_NAME = "PDF Resource";

	private ParticipantCommandServiceAsync rpc;
	private EnkiCommandServiceAsync enkiService;

	private Token token;

	private Timer timer = null;

	public ViewStatement(ParticipantCommandServiceAsync rpcService, EnkiCommandServiceAsync enkiService,
			HandlerManager eventBus, Token token, GadgetType type) {
		super(token, type);

		this.token = token;
		this.rpc = rpcService;
		this.enkiService = enkiService;

		StatementView statementView = new StatementViewImpl();

		StatementPresenter presenter = new StatementPresenter(rpc, enkiService, statementView, token);

		presenter.go(null);

		setView(statementView);
		setPresenter(presenter);
	}

	@Override
	public String getName() {
		if (token.getType().equals(ResourceType.PDF))
			return CONSTANTS.pdfResource() + ": " + token.getName();
		else
			return CONSTANTS.statement() + ": " + token.getName();
	}

	@Override
	public void setLearningTime(final Date date) {
		if (!token.getType().equals(ResourceType.PDF))
			return;

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
					public void onFailure(Throwable t) {

					}
				});
	}

	@Override
	public void stopTime() {
		if (timer != null && timer.isRunning())
			timer.cancel();
	}

}
