package pt.up.fc.dcc.mooshak.client.guis.guest;

import java.util.logging.Level;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.guest.Token.Command;
import pt.up.fc.dcc.mooshak.client.guis.guest.presenter.ListingPresenter;
import pt.up.fc.dcc.mooshak.client.guis.guest.presenter.TopLevelPresenter;
import pt.up.fc.dcc.mooshak.client.guis.guest.view.ListingView;
import pt.up.fc.dcc.mooshak.client.guis.guest.view.ListingViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.guest.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.guest.view.TopLevelViewImpl;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.events.ListingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.SubmissionsUpdate;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController extends AbstractAppController implements
		ValueChangeHandler<String> {
	
	private static final int RANKINGS_NUMBER_OF_ROWS = 15;
	private static final int SUBMISSIONS_NUMBER_OF_ROWS = 5;

	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private HasWidgets container;

	private EventManager eventManager = null;

	private TopLevelView topLevelView = null;

	public AppController(
			BasicCommandServiceAsync rpcBasic, HandlerManager eventBus) {

		this.rpcBasic = rpcBasic;
		this.eventBus = eventBus;

		eventManager = EventManager.getInstance();

		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);

		new ListenerManager<SubmissionsUpdate>().addEventListener(
				SubmissionsUpdate.class, Kind.SUBMISSIONS, "submission");

		new ListenerManager<RankingUpdate>().addEventListener(
				RankingUpdate.class, Kind.RANKINGS, "rankings");

		rpcBasic.refreshRows(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				LOGGER.log(Level.INFO, "Updates processed");
				eventManager.refresh();
			}

			@Override
			public void onFailure(Throwable caught) {
				throwing("Refreshing events", caught);
			}
		});
		LOGGER.log(Level.INFO, "Updates requested");
	}

	/**
	 * Listener creator for ListingUpdateEvent. Data on the received events is
	 * forward to a ListingDataProvider of the given kind.
	 *
	 * @param <T>
	 *            a type of event extending ListingUpdateEvent
	 */
	private class ListenerManager<T extends ListingUpdateEvent> {

		void addEventListener(final Class<T> type, final Kind kind,
				final String label) {

			final ListingDataProvider provider = ListingDataProvider
					.getDataProvider(kind);

			eventManager.addListener(type, new MooshakEventListener<T>() {

				@Override
				public void receiveEvent(T event) {
					LOGGER.log(Level.INFO, "Processing " + label + " event");
					provider.addOrChangeRow(event.getId(), event.getRecord());
				}

			});
		}
	}

	public void go(final HasWidgets container) {
		this.container = container;

		if ("".equals(History.getToken())) {
			Token token = new Token();
			token.setCommand(Command.TOP);
			History.newItem(token.toString());
		} else {
			History.fireCurrentHistoryState();
		}
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		final Token token = new Token(event.getValue());
		Command command = token.getCommand();

		showWaitCursor();

		if (command == Command.TOP) {
			makeToplevel();
		} else if (topLevelView == null) {
			redirectToToplevel();
		}

		showDefaultCursor();

	}

	/**
	 * Make a a card top level
	 */
	private void makeToplevel() {

		if (topLevelView == null) {
			topLevelView = new TopLevelViewImpl();
		}
		new TopLevelPresenter(rpcBasic, eventBus, topLevelView).go(container);

		makeListing();

	}

	/**
	 * Make a a card for a particular king of listing
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeListing() {
		if (topLevelView != null) {
			
			CardPanel rankingPanel = topLevelView.getRankingPanel();
			ListingView rankingView = new ListingViewImpl(Kind.RANKINGS,
					RANKINGS_NUMBER_OF_ROWS);

			ListingPresenter rankingPresenter = new ListingPresenter(rpcBasic,
					rankingView, Kind.RANKINGS);

			rankingPresenter.go(rankingPanel);

			rankingPanel
					.addCard(Command.TOP.toString(), rankingView.asWidget());
			rankingPanel.showCard(Command.TOP.toString());

			CardPanel submissionPanel = topLevelView.getSubmissionPanel();
			ListingView submissionView = new ListingViewImpl(Kind.SUBMISSIONS,
					SUBMISSIONS_NUMBER_OF_ROWS);

			ListingPresenter submissionPresenter = new ListingPresenter(
					rpcBasic, submissionView, Kind.SUBMISSIONS);

			submissionPresenter.go(submissionPanel);

			submissionPanel.addCard(Command.TOP.toString(),
					submissionView.asWidget());
			submissionPanel.showCard(Command.TOP.toString());

		}
	}
}
