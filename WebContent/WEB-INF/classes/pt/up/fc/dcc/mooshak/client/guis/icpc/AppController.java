package pt.up.fc.dcc.mooshak.client.guis.icpc;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.CountLogMessage;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.Token.Command;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.AskQuestionEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.AskQuestionEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ChangeEditorContentEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ChangeEditorContentEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.EditProgramEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.EditProgramEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.HelpTutorialEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.HelpTutorialEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ListingSelectedEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ListingSelectedEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ViewStatementEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ViewStatementEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.icpc.presenter.AskPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.presenter.ListingPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.presenter.ProgramPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.presenter.StatementPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.presenter.TopLevelPresenter;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.AskView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.AskViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.ListingView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.ListingViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.ProgramView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.ProgramViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TopLevelViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.TutorialView;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.events.AlertNotificationEvent;
import pt.up.fc.dcc.mooshak.shared.events.BalloonsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.ContextChangedEvent;
import pt.up.fc.dcc.mooshak.shared.events.ListingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.LogoutEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;
import pt.up.fc.dcc.mooshak.shared.events.PrintoutsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.ProblemDescriptionChangedEvent;
import pt.up.fc.dcc.mooshak.shared.events.QuestionsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.ReportNotificationEvent;
import pt.up.fc.dcc.mooshak.shared.events.SubmissionsUpdate;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController extends AbstractAppController implements
		ValueChangeHandler<String> {

	private static final int SMALL_NUMBER_OF_ROWS = 18;
	private static final int RANKINGS_NUMBER_OF_ROWS = 14;

	private ParticipantCommandServiceAsync rpc;
	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private HasWidgets container;

	private TopLevelPresenter toplevelPresenter = null;

	private Map<String, ProgramPresenter> programPresenters = new HashMap<String, ProgramPresenter>();

	private Map<String, StatementPresenter> statementPresenters = new HashMap<String, StatementPresenter>();

	private EventManager eventManager = EventManager.getInstance();

	private TopLevelView topLevelView = null;

	private Map<String, AskView> askViews = new HashMap<String, AskView>();

	private TutorialView tutorialView = null;

	private Command currentCommand = null;

	private Storage storage = Storage.getLocalStorageIfSupported();

	public AppController(ParticipantCommandServiceAsync rpcService,
			BasicCommandServiceAsync rpcBasic, HandlerManager eventBus) {

		this.rpc = rpcService;
		this.rpcBasic = rpcBasic;
		this.eventBus = eventBus;

		bind();

		tutorialView = new TutorialView();
	}

	private void bind() {
		History.addValueChangeHandler(this);

		eventBus.addHandler(ChangeEditorContentEvent.TYPE,
				new ChangeEditorContentEventHandler() {
					@Override
					public void onChangeEditorContent(
							ChangeEditorContentEvent changeContentEvent) {
						Token token = new Token();
						token.setCommand(Command.PROGRAM);
						token.setId(changeContentEvent.getProblemId());
						History.newItem(token.toString());
						
						String problemId = changeContentEvent.getProblemId();
						ProgramPresenter presenter = 
								programPresenters.get(problemId);
						
						presenter.replaceSubmissionContent(
										changeContentEvent.getId(),
										changeContentEvent.getTeam());
						topLevelView.selectProgramEditor(problemId);
					}
				});

		eventBus.addHandler(ViewStatementEvent.TYPE,
				new ViewStatementEventHandler() {
					public void onViewStatement(ViewStatementEvent event) {
						Token token = new Token();
						token.setCommand(Command.VIEW);
						token.setId(event.getProblemId());
						token.setName(event.getProblemLabel());
						History.newItem(token.toString());
					}
				});

		eventBus.addHandler(EditProgramEvent.TYPE,
				new EditProgramEventHandler() {
					public void onEditProgram(EditProgramEvent event) {
						Token token = new Token();
						token.setCommand(Command.PROGRAM);
						token.setId(event.getProblemId());
						History.newItem(token.toString());
					}
				});

		eventBus.addHandler(AskQuestionEvent.TYPE,
				new AskQuestionEventHandler() {
					public void onAskQuestion(AskQuestionEvent event) {
						Token token = new Token();
						token.setCommand(Command.ASK);
						token.setId(event.getProblemId());
						token.setName(event.getProblemName());
						History.newItem(token.toString());

						ListingDataProvider provider = ListingDataProvider
								.getDataProvider(Kind.QUESTIONS);

						provider.reapplyFilters();
						provider.setDisplayRows(AppController.SMALL_NUMBER_OF_ROWS);

						if (askViews.get(event.getProblemId()) != null)
							askViews.get(event.getProblemId()).setFiltering();
					}
				});

		eventBus.addHandler(ListingSelectedEvent.TYPE,
				new ListingSelectedEventHandler() {
					public void onListingSelected(ListingSelectedEvent event) {
						Token token = new Token();
						token.setCommand(Command.LISTING);
						token.setKind(event.getKind());
						History.newItem(token.toString());

						ListingDataProvider provider = ListingDataProvider
								.getDataProvider(event.getKind());

						provider.reapplyFilters();

						int rows = event.getKind() == Kind.RANKINGS ? RANKINGS_NUMBER_OF_ROWS
								: ListingDataProvider.ROWS;
						provider.setDisplayRows(rows);
					}
				});

		eventBus.addHandler(HelpTutorialEvent.TYPE,
				new HelpTutorialEventHandler() {
					public void onHelpTutorial(HelpTutorialEvent event) {
						tutorialView.showTutorial(currentCommand);
					}
				});

		eventManager.addListener(ReportNotificationEvent.class,
				new MooshakEventListener<ReportNotificationEvent>() {

					@Override
					public void receiveEvent(ReportNotificationEvent message) {
						ProgramPresenter presenter = programPresenters
								.get(message.getProblemId());
						if (presenter == null)
							LOGGER.log(Level.SEVERE, "Unknown problem ID:"
									+ message.getProblemId());
						else
							presenter.update(message.getSubmissionId(),
									message.isConsider());
					}
				});

		eventManager.addListener(LogoutEvent.class,
				new MooshakEventListener<LogoutEvent>() {

					@Override
					public void receiveEvent(LogoutEvent event) {
						AuthenticationPresenter.logout(event.getReason());
						LOGGER.log(Level.INFO, event.getReason());
					}

				});

		eventManager.addListener(ContextChangedEvent.class,
				new MooshakEventListener<ContextChangedEvent>() {

					@Override
					public void receiveEvent(ContextChangedEvent message) {
						if (toplevelPresenter != null) {
							toplevelPresenter.updateContext();

							for (ProgramPresenter pp : programPresenters
									.values()) {
								pp.setContestDates(
										toplevelPresenter.getStartDate(),
										toplevelPresenter.getStopDate());
							}
						}
					}
				});

		new ListenerManager<SubmissionsUpdate>().addEventListener(
				SubmissionsUpdate.class, Kind.SUBMISSIONS, "submission");

		new ListenerManager<QuestionsUpdate>().addEventListener(
				QuestionsUpdate.class, Kind.QUESTIONS, "question");

		/*
		 * eventManager.addListener(QuestionsUpdate.class, new
		 * MooshakEventListener<QuestionsUpdate>() {
		 * 
		 * @Override public void receiveEvent(QuestionsUpdate event) {
		 * if(event.getRecord().get("state").toLowerCase() .equals("answered"))
		 * topLevelView.showNotification(constants.notification() +
		 * event.getRecord().get("subject"));
		 * 
		 * } });
		 * 
		 * eventManager.addListener(SubmissionsUpdate.class, new
		 * MooshakEventListener<SubmissionsUpdate>() {
		 * 
		 * @Override public void receiveEvent(SubmissionsUpdate event) {
		 * if(event.getRecord().get("classification").toLowerCase()
		 * .equals("accepted"))
		 * topLevelView.showNotification(constants.notification() + "Problem " +
		 * event.getRecord().get("problem") + " ACCEPTED for team " +
		 * event.getRecord().get("team"));
		 * 
		 * } });
		 */

		eventManager.addListener(AlertNotificationEvent.class,
				new MooshakEventListener<AlertNotificationEvent>() {

					@Override
					public void receiveEvent(AlertNotificationEvent event) {
						topLevelView.showNotification(event.getMessage());
						LOGGER.log(Level.INFO, "received alert");
					}
				});

		eventManager.addListener(ProblemDescriptionChangedEvent.class,
				new MooshakEventListener<ProblemDescriptionChangedEvent>() {

					@Override
					public void receiveEvent(
							ProblemDescriptionChangedEvent event) {

						StatementPresenter presenter = statementPresenters
								.get(event.getProblemId());

						if (presenter == null)
							return;

						presenter.clearStatement();
						LOGGER.log(Level.INFO, "received problem");
					}
				});

		new ListenerManager<PrintoutsUpdate>().addEventListener(
				PrintoutsUpdate.class, Kind.PRINTOUTS, "printout");

		new ListenerManager<BalloonsUpdate>().addEventListener(
				BalloonsUpdate.class, Kind.BALLOONS, "balloons");

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
					// LOGGER.log(Level.INFO,);
					CountLogMessage.info("Processing " + label + " event");
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

		} else
			History.fireCurrentHistoryState();

	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		final Token token = new Token(event.getValue());
		Command command = token.getCommand();
		currentCommand = command;

		showWaitCursor();

		if (command == Command.TOP) {
			makeToplevel();
		} else if (topLevelView == null) {
			redirectToToplevel();
		} else {
			CardPanel cardPanel = topLevelView.getContent();

			switch (command) {
			case VIEW:
				if (!cardPanel.hasCard(token.toString()))
					makeViewStatement(cardPanel, token);
				if (statementPresenters.get(token.getId()).isRequestUpdate())
					statementPresenters.get(token.getId()).updateStatement();
				break;
			case PROGRAM:
				if (!cardPanel.hasCard(token.toString()))
					makeEditProgram(cardPanel, token);
				break;
			case ASK:
				if (!cardPanel.hasCard(token.toString()))
					makeAskQuestion(cardPanel, token);
				break;
			case LISTING:
				if (!cardPanel.hasCard(token.toString()))
					makeListing(cardPanel, token);
				break;
			default:
				LOGGER.log(Level.SEVERE, "AppController Invalid command: "
						+ command);
			}

			cardPanel.showCard(token.toString());
		}

		showDefaultCursor();

		if (storage.getItem("showHelp") != null) {
			if (!storage.getItem("showHelp").equals("no"))
				tutorialView.showTutorial(currentCommand);
		} else
			tutorialView.showTutorial(currentCommand);

	}

	/**
	 * Make a a card top level
	 */
	private void makeToplevel() {

		if (topLevelView == null) {
			topLevelView = new TopLevelViewImpl();
			tutorialView.setTutorialView(topLevelView);
		}

		toplevelPresenter = new TopLevelPresenter(rpc, rpcBasic, eventBus,
				topLevelView);

		toplevelPresenter.go(container);

	}

	/**
	 * Make a a card for viewing a statement for a given problem
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeViewStatement(final CardPanel cardPanel, final Token token) {

		if (topLevelView != null) {

			StatementView statementView = new StatementViewImpl();

			StatementPresenter presenter = new StatementPresenter(rpc,
					eventBus, statementView, token.getId(), token.getName());
			presenter.go(cardPanel);

			statementPresenters.put(token.getId(), presenter);

			cardPanel.addCard(token.toString(), statementView.asWidget());
		}
	}

	/**
	 * Make a a card for editing a given problem
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeEditProgram(final CardPanel cardPanel, final Token token) {
		if (topLevelView != null) {

			ProgramView programView = new ProgramViewImpl();

			ProgramPresenter presenter = new ProgramPresenter(rpc, eventBus,
					programView, toplevelPresenter.getContestId(), token.getId(), "",
					toplevelPresenter.getTeamId());

			presenter.go(cardPanel);
			programPresenters.put(token.getId(), presenter);

			cardPanel.addCard(token.toString(), programView.asWidget());
			
			presenter.setContestDates(toplevelPresenter.getStartDate(),
					toplevelPresenter.getStopDate());
		}
	}

	/**
	 * Make a a card for ask a question
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeAskQuestion(final CardPanel cardPanel, final Token token) {
		if (topLevelView != null) {

			AskView askView = new AskViewImpl(token.getId(),
					SMALL_NUMBER_OF_ROWS);
			askView.setFiltering();

			AskPresenter presenter = new AskPresenter(rpc, rpcBasic, eventBus,
					askView, token.getId(), token.getName());

			presenter.go(cardPanel);

			cardPanel.addCard(token.toString(), askView.asWidget());

			askViews.put(token.getId(), askView);
		}
	}

	/**
	 * Make a a card for a particular king of listing
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeListing(CardPanel cardPanel, final Token token) {
		if (topLevelView != null) {
			Kind kind = token.getKind();

			int rows = kind == Kind.RANKINGS ? RANKINGS_NUMBER_OF_ROWS
					: ListingDataProvider.ROWS;

			ListingView view = new ListingViewImpl(kind, rows);

			ListingPresenter presenter = new ListingPresenter(rpc, rpcBasic,
					view, kind, eventBus);

			presenter.go(cardPanel);

			cardPanel.addCard(token.toString(), view.asWidget());
		}
	}

}
