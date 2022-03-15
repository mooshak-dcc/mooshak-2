package pt.up.fc.dcc.mooshak.client.guis.judge;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject.Processor;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.judge.Token;
import pt.up.fc.dcc.mooshak.client.guis.judge.Token.Command;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.CommentProblemEvent;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.CommentProblemEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.DetailQuestionEvent;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.DetailQuestionEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.InfoSubmissionEvent;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.InfoSubmissionEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.ListingSelectedEvent;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.ListingSelectedEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.RegisterDeliveryEvent;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.RegisterDeliveryEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.StatisticsSelectedEvent;
import pt.up.fc.dcc.mooshak.client.guis.judge.event.StatisticsSelectedEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.judge.presenter.CommentProblemPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.presenter.DetailQuestionPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.presenter.InfoSubmissionPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.presenter.ListingPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.presenter.RegisterDeliveryPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.presenter.StatisticsPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.presenter.TopLevelPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.CommentProblemView;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.CommentProblemViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.DetailQuestionView;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.DetailQuestionViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.InfoSubmissionView;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.InfoSubmissionViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.ListingView;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.ListingViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.RegisterDeliveryView;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.RegisterDeliveryViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.StatisticsView;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.StatisticsViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.judge.view.TopLevelViewImpl;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.data.PendingDataProvider;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.events.BalloonsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.ListingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;
import pt.up.fc.dcc.mooshak.shared.events.ObjectUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.PrintoutsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.QuestionsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.ReportNotificationEvent;
import pt.up.fc.dcc.mooshak.shared.events.SubmissionsUpdate;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController extends AbstractAppController implements
		ValueChangeHandler<String> {
	
	private static final int NUMBER_OF_ROWS = 10;
	private static final int NUMBER_OF_RANKINGS_ROWS = 14;

	private JudgeCommandServiceAsync rpc;
	private BasicCommandServiceAsync rpcBasic;
	private ParticipantCommandServiceAsync rpcParticipant;
	private HandlerManager eventBus;
	private HasWidgets container;

	private DataManager dataManager = DataManager.getInstance();
	
	private EventManager eventManager = EventManager.getInstance();

	private TopLevelView topLevelView = null;
	
	private Map<Kind, ListingView> listViews = new HashMap<>();
	private Kind currentKind = null;
	
	private String currentObjectId = null;
	
	private Map<String, DetailQuestionPresenter> questionPresenters = new HashMap<>();
	private Map<String, InfoSubmissionPresenter> submissionPresenters = new HashMap<>();
	private Map<String, RegisterDeliveryPresenter> deliveryPresenters = new HashMap<>();
	
	private String contestId = "";
	private String separator = "";

	private boolean printoutsListPending = true;
	private boolean balloonsListPending = true;

	public AppController(BasicCommandServiceAsync rpcBasic, 
			ParticipantCommandServiceAsync rpcParticipant, 
			JudgeCommandServiceAsync rpcService,
			HandlerManager eventBus) {

		this.rpc = rpcService;
		this.rpcBasic = rpcBasic;
		this.rpcParticipant = rpcParticipant;
		this.eventBus = eventBus;
		
		rpcBasic.getContestId(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				contestId = result;
			}
		});
		
		rpcService.getFileSeparator(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				separator = result;
			}
		});
		
		rpcService.isBalloonsListPending(new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				balloonsListPending = result.booleanValue();
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		rpcService.isPrintoutsListPending(new AsyncCallback<Boolean>() {
			
			@Override
			public void onSuccess(Boolean result) {
				printoutsListPending = result.booleanValue();
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
		
		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);

		eventBus.addHandler(InfoSubmissionEvent.TYPE,
				new InfoSubmissionEventHandler() {
					public void onInfoSubmission(InfoSubmissionEvent event) {
						Token token = new Token();
						token.setCommand(Command.INFO);
						token.setId(event.getSubmissionId());
						token.setName(event.getProblemId());
						History.newItem(token.toString());
					}
				});

		eventBus.addHandler(DetailQuestionEvent.TYPE,
				new DetailQuestionEventHandler() {
					public void onDetailQuestion(DetailQuestionEvent event) {
						Token token = new Token();
						token.setCommand(Command.DETAIL);
						token.setId(event.getQuestionId());
						History.newItem(token.toString());
					}
				});

		eventBus.addHandler(CommentProblemEvent.TYPE,
				new CommentProblemEventHandler() {
					public void onCommentProblem(CommentProblemEvent event) {
						Token token = new Token();
						token.setCommand(Command.COMMENT);
						History.newItem(token.toString());
					}
				});
		
		eventBus.addHandler(RegisterDeliveryEvent.TYPE,
				new RegisterDeliveryEventHandler() {
					public void onRegisterDelivery(RegisterDeliveryEvent event) {
						Token token = new Token();
						token.setCommand(Command.DELIVERY);
						token.setKind(event.getKind());
						token.setId(event.getId());
						History.newItem(token.toString());
					}
				});

		eventBus.addHandler(ListingSelectedEvent.TYPE,
				new ListingSelectedEventHandler() {
					public void onListingSelected(ListingSelectedEvent event) {
						Token token = new Token();
						token.setCommand(Command.LISTING);
						token.setKind(event.getKind());
						History.newItem(token.toString());
						
						ListingDataProvider.getDataProvider(event.getKind())
							.reapplyFilters();
					}
				});
		
		eventBus.addHandler(StatisticsSelectedEvent.TYPE, 
				new StatisticsSelectedEventHandler() {
			
			@Override
			public void onStatisticsSelected(StatisticsSelectedEvent event) {
				Token token = new Token();
				token.setCommand(Command.STATISTICS);
				History.newItem(token.toString());
				
				ListingDataProvider.getDataProvider(Kind.SUBMISSIONS)
					.reapplyFilters();
			}
		});

		
		eventManager.addListener(ObjectUpdateEvent.class,
				new MooshakEventListener<ObjectUpdateEvent>() {

					@Override
					public void receiveEvent(ObjectUpdateEvent event) {
						final String id = event.getId();
						
						LOGGER.log(Level.INFO,"Object updated:"+id);
						dataManager.updateObject(event.getId(),new Processor() {

							@Override
							public void process(DataObject dataObject) {
								if(id != null && id.equals(currentObjectId))
									updateObjectEditor(dataObject);
							}
							
						});
					}
			});

		eventManager.addListener(ReportNotificationEvent.class,
				new MooshakEventListener<ReportNotificationEvent>() {

					@Override
					public void receiveEvent(ReportNotificationEvent message) {
						InfoSubmissionPresenter presenter = submissionPresenters
								.get(message.getRecipient().getUserId());
						if (presenter == null)
							LOGGER.log(Level.SEVERE, "Unknown problem ID:"
									+ message.getProblemId());
						else
							presenter.update(message.getSubmissionId());
					}
				});
		
		new ListenerManager<SubmissionsUpdate>().addEventListener(
				SubmissionsUpdate.class, Kind.SUBMISSIONS, "submission");

		new ListenerManager<QuestionsUpdate>().addEventListener(
				QuestionsUpdate.class, Kind.QUESTIONS, "question");

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
					LOGGER.log(Level.INFO, "Processing " + label + " event");
					provider.addOrChangeRow(event.getId(), event.getRecord());
					
					switch (kind) {
					case SUBMISSIONS:
					case QUESTIONS:
						PendingDataProvider.getDataProvider()
							.receiveUpdateEvent(event, kind);
						break;
					case PRINTOUTS:
						if (printoutsListPending)
							PendingDataProvider.getDataProvider()
								.receiveUpdateEvent(event, kind);
						break;
					case BALLOONS:
						if (balloonsListPending)
							PendingDataProvider.getDataProvider()
								.receiveUpdateEvent(event, kind);
						break;

					default:
						break;
					}
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
		} else {
			CardPanel cardPanel = topLevelView.getContent();
			currentObjectId = contestId;
			switch (command) {
			case INFO:
				cardPanel = listViews.get(currentKind).getDetail();
				if (!cardPanel.hasCard(token.toString()))
					makeInfoSubmission(cardPanel, token);
				currentObjectId += separator + "submissions";
				break;
			case DETAIL:
				cardPanel = listViews.get(currentKind).getDetail();
				if (!cardPanel.hasCard(token.toString()))
					makeDetailQuestion(cardPanel, token);
				currentObjectId += separator + "questions";
				break;
			case COMMENT:
				cardPanel = listViews.get(currentKind).getDetail();
				if (!cardPanel.hasCard(token.toString()))
					makeCommentProblem(cardPanel, token);
				break;
			case DELIVERY:
				cardPanel = listViews.get(currentKind).getDetail();
				if (!cardPanel.hasCard(token.toString()))
					makeRegisterDelivery(cardPanel, token);
				if(token.getKind() == Kind.PRINTOUTS)
					currentObjectId += separator + "printouts";
				else
					currentObjectId += separator + "balloons";
				break;
			case LISTING:
				if (!cardPanel.hasCard(token.toString()))
					makeListing(cardPanel, token);
				currentKind = token.getKind();
				break;
			case STATISTICS:
				if (!cardPanel.hasCard(token.toString()))
					makeStatistics(cardPanel, token);
				break;
			default:
				LOGGER.log(Level.SEVERE, "AppController Invalid command: "
						+ command);
			}
			
			
			currentObjectId += separator + token.getId();
			
			if(!command.equals(Command.LISTING) && !command.equals(Command.STATISTICS)) {
				dataManager.getMooshakObject(currentObjectId, new Processor() {
	
					// process this when data is available
					public void process(DataObject dataObject) {
						updateObjectEditor(dataObject);			
						listViews.get(currentKind).getDetail()
							.showCard(token.toString());		
					}
	
				});
			}
			else
				cardPanel.showCard(token.toString());
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
		new TopLevelPresenter(rpc, rpcBasic, eventBus, topLevelView).go(container);

	}

	/**
	 * Make a card for viewing a submission information
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeInfoSubmission(final CardPanel cardPanel, final Token token) {
		if (topLevelView != null) {

			InfoSubmissionView infoView = new InfoSubmissionViewImpl();

			InfoSubmissionPresenter presenter = new InfoSubmissionPresenter(
					rpc, rpcBasic, rpcParticipant, eventBus, infoView, token.getId(), 
					token.getName());
			
			submissionPresenters.put(token.getId(), presenter);
			
			presenter.go(cardPanel);

			cardPanel.addCard(token.toString(), infoView.asWidget());
		}
	}

	/**
	 * Make a a card for viewing question detail and answer
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeDetailQuestion(final CardPanel cardPanel, final Token token) {
		if (topLevelView != null) {

			DetailQuestionView detailView = new DetailQuestionViewImpl();

			DetailQuestionPresenter presenter = new DetailQuestionPresenter(
					rpc, eventBus, detailView, token.getId());

			questionPresenters.put(token.getId(), presenter);

			presenter.go(cardPanel);

			cardPanel.addCard(token.toString(), detailView.asWidget());
		}
	}

	/**
	 * Make a a card to register a delivery
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeRegisterDelivery(final CardPanel cardPanel,
			final Token token) {
		if (topLevelView != null) {

			RegisterDeliveryView deliveryView = new RegisterDeliveryViewImpl();

			RegisterDeliveryPresenter presenter = new RegisterDeliveryPresenter(
					rpc, eventBus, deliveryView, token.getId(), token.getKind());

			deliveryPresenters.put(token.getId(), presenter);

			presenter.go(cardPanel);

			cardPanel.addCard(token.toString(), deliveryView.asWidget());
		}
	}

	/**
	 * Make a a card for commenting a problem
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeCommentProblem(final CardPanel cardPanel, final Token token) {
		if (topLevelView != null) {

			CommentProblemView commentView = new CommentProblemViewImpl();

			CommentProblemPresenter presenter = new CommentProblemPresenter(
					rpc, rpcParticipant, eventBus, commentView);

			presenter.go(cardPanel);

			cardPanel.addCard(token.toString(), commentView.asWidget());
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
			
			ListingView view = null;
			switch(kind) {
			case RANKINGS:
				 view = new ListingViewImpl(kind, NUMBER_OF_RANKINGS_ROWS,
							(TopLevelPresenter) topLevelView.getPresenter());
				break;
			default:
				 view = new ListingViewImpl(kind, NUMBER_OF_ROWS,
							(TopLevelPresenter) topLevelView.getPresenter());
			}

			listViews.put(kind, view);
			ListingPresenter presenter = new ListingPresenter(rpc, 
					rpcBasic, view,	eventBus, kind);

			presenter.go(cardPanel);

			cardPanel.addCard(token.toString(), view.asWidget());
			
			view.setFiltersData();
		}
	}

	/**
	 * Make a a card for statistics
	 * 
	 * @param cardPanel
	 * @param token
	 *            containing command parameters
	 */
	private void makeStatistics(CardPanel cardPanel, Token token) {
		if (topLevelView != null) {

			StatisticsView view = new StatisticsViewImpl();

			StatisticsPresenter presenter = new StatisticsPresenter( 
					rpcBasic, view,	eventBus);

			presenter.go(cardPanel);

			cardPanel.addCard(token.toString(), view.asWidget());
			
		}
	}
	
	/**
	 * @param objectId
	 * @param dataObject
	 * @param type
	 */
	private void updateObjectEditor(DataObject dataObject) {
	
		String objectId = dataObject.getId();
		String type = dataObject.getData().getType();
		FormDataProvider provider = dataObject.getFormDataProvider();
		
		String tokenId = objectId;
		if (objectId.lastIndexOf("/") > 0)
			tokenId = objectId.substring(objectId.lastIndexOf("/") + 1);
		
		switch (type.toLowerCase()) {
		case "submission":
			submissionPresenters.get(tokenId).setObjectId(objectId);
			submissionPresenters.get(tokenId).setDataProvider(provider);
			break;
		case "question":
			questionPresenters.get(tokenId).setObjectId(objectId);
			questionPresenters.get(tokenId).setDataProvider(provider);
			break;
		case "printout":
		case "balloon":
			deliveryPresenters.get(tokenId).setObjectId(objectId);
			deliveryPresenters.get(tokenId).setDataProvider(provider);
			break;

		default:
			break;
		}
		
		provider.refresh();
	}
	

}