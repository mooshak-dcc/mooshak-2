package pt.up.fc.dcc.mooshak.client.guis.enki;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.CountLogMessage;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider;
import pt.up.fc.dcc.mooshak.client.data.ListingDataProvider.Kind;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.gadgets.AchievementList;
import pt.up.fc.dcc.mooshak.client.gadgets.Gadget;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetMaker;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.HasLearningTime;
import pt.up.fc.dcc.mooshak.client.gadgets.LeaderboardTable;
import pt.up.fc.dcc.mooshak.client.gadgets.MyData;
import pt.up.fc.dcc.mooshak.client.gadgets.ResourceRating;
import pt.up.fc.dcc.mooshak.client.gadgets.ResourcesTree;
import pt.up.fc.dcc.mooshak.client.gadgets.StatsChart;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.VideoViewer;
import pt.up.fc.dcc.mooshak.client.gadgets.ViewStatement;
import pt.up.fc.dcc.mooshak.client.gadgets.achievements.AchievementsView;
import pt.up.fc.dcc.mooshak.client.gadgets.gamesubmission.GameSubmissionPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.gameviewer.GameViewerPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.gameviewer.GameViewerView;
import pt.up.fc.dcc.mooshak.client.gadgets.kora.KoraPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.leaderboard.LeaderboardView;
import pt.up.fc.dcc.mooshak.client.gadgets.mydata.MyDataPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.mydata.MyDataView;
import pt.up.fc.dcc.mooshak.client.gadgets.mysubmissions.MySubmissionsView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView;
import pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.ProgramErrorListPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.ProgramErrorListView;
import pt.up.fc.dcc.mooshak.client.gadgets.programtestcases.ProgramTestCasesPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programtestcases.ProgramTestCasesView;
import pt.up.fc.dcc.mooshak.client.gadgets.quiz.QuizPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcerating.ResourceRatingView;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.ResourcesTreeView;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.SelectResourceEvent;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.SelectResourceEventHandler;
import pt.up.fc.dcc.mooshak.client.gadgets.statschart.StatsChartView;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.enki.event.ResourceOnSuccessEvent;
import pt.up.fc.dcc.mooshak.client.guis.enki.event.ResourceOnSuccessEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.enki.event.SelectTabEvent;
import pt.up.fc.dcc.mooshak.client.guis.enki.event.SelectTabEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.enki.presenter.TopLevelPresenter;
import pt.up.fc.dcc.mooshak.client.guis.enki.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.enki.view.TopLevelViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.enki.view.TopLevelViewImpl.Region;
import pt.up.fc.dcc.mooshak.client.guis.enki.view.TutorialView;
import pt.up.fc.dcc.mooshak.client.guis.enki.widgets.EvaluationAcceptedDialog;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ChangeEditorContentEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.ChangeEditorContentEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.HelpTutorialEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.HelpTutorialEventHandler;
import pt.up.fc.dcc.mooshak.client.services.AsuraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.QuizCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.asura.NewAcceptedSubmissionEvent;
import pt.up.fc.dcc.mooshak.shared.asura.TournamentMovieEvent;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.events.AchievementsUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.ContextChangedEvent;
import pt.up.fc.dcc.mooshak.shared.events.LeaderboardUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.ListingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.LogoutEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;
import pt.up.fc.dcc.mooshak.shared.events.ProblemStatisticsUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.QuestionsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.RatingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.ReportNotificationEvent;
import pt.up.fc.dcc.mooshak.shared.events.ResourceStateUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.StudentProfileUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.SubmissionsUpdate;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;
import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

public class AppController extends AbstractAppController implements ValueChangeHandler<String> {

	private EnkiConstants enkiConstants = GWT.create(EnkiConstants.class);

	private BasicCommandServiceAsync basicService;
	private ParticipantCommandServiceAsync participantService;
	private EnkiCommandServiceAsync enkiService;

	private HandlerManager eventBus;
	private HasWidgets container;

	private EventManager eventManager = EventManager.getInstance();

	static ContextInfo contextInfo = null;

	private GadgetMaker gadgetMaker;

	private TopLevelPresenter toplevelPresenter = null;

	private TopLevelView topLevelView = null;

	private TutorialView tutorialView = null;

	private Storage storage = Storage.getLocalStorageIfSupported();

	private Map<String, ProgramTestCasesPresenter> programPresenters = new HashMap<>();
	private Map<String, KoraPresenter> koraPresenters = new HashMap<>();
	private Map<String, QuizPresenter> quizPresenters = new HashMap<>();
	private Map<String, GameSubmissionPresenter> gamePresenters = new HashMap<>();
	private Map<String, GameViewerPresenter> gameViewerPresenters = new HashMap<>();

	private Map<String, EditorKind> editors = new HashMap<>();

	private Token activeToken = null;
	private Date lastChange = null;
	private Date learningTime = null;

	public AppController(
			BasicCommandServiceAsync basicService, 
			ParticipantCommandServiceAsync participantService,
			EnkiCommandServiceAsync enkiService,
			KoraCommandServiceAsync koraService, 
			AsuraCommandServiceAsync asuraService, 
			QuizCommandServiceAsync quizService,
			HandlerManager eventBus, ContextInfo contextInfo) {

		this.basicService = basicService;
		this.participantService = participantService;
		this.enkiService = enkiService;

		this.eventBus = eventBus;

		AppController.contextInfo = contextInfo;
		GadgetPresenter.setContextInfo(contextInfo);

		gadgetMaker = new GadgetMaker(eventBus, basicService, participantService, 
				enkiService, koraService, asuraService, quizService);

		bind();

		tutorialView = TutorialView.getInstance();
	}

	private void bind() {
		History.addValueChangeHandler(this);

		eventBus.addHandler(ChangeEditorContentEvent.TYPE, new ChangeEditorContentEventHandler() {
			@Override
			public void onChangeEditorContent(ChangeEditorContentEvent changeContentEvent) {
				ProgramTestCasesPresenter presenter = programPresenters.get(changeContentEvent.getProblemId());
				if (presenter == null) {

					KoraPresenter koraPresenter = koraPresenters.get(changeContentEvent.getProblemId());
					if (koraPresenter != null) {
						topLevelView.selectTab(activeToken.getId(), gadgetMaker
								.getGadget(activeToken, GadgetType.KORA, activeToken.getId()).getView().asWidget());
						koraPresenter.replaceSubmissionContent(changeContentEvent.getId(),
								changeContentEvent.getTeam());
					} else {
						QuizPresenter quizPresenter = quizPresenters.get(changeContentEvent.getProblemId());
						if (quizPresenter != null) {	
							topLevelView.selectTab(activeToken.getId(), gadgetMaker
									.getGadget(activeToken, GadgetType.QUIZ, activeToken.getId()).getView().asWidget());
							quizPresenter.replaceSubmissionContent(changeContentEvent.getId(),
									changeContentEvent.getTeam());
						} else {
							GameSubmissionPresenter gamePresenter = gamePresenters.get(changeContentEvent.getProblemId());
							if (gamePresenter != null) {
								Gadget gadget = gadgetMaker.getGadget(activeToken, GadgetType.PROGRAM_PROBLEM_EDITOR,
										activeToken.getId());
	
								topLevelView.selectTab(activeToken.getId(), gadget.getView().asWidget());
								gamePresenter.replaceSubmissionContent(changeContentEvent.getId(),
										changeContentEvent.getTeam());
							} else
								LOGGER.log(Level.SEVERE, "Unknown problem ID:" + changeContentEvent.getProblemId());
						}
					}
				} else {
					programPresenters.get(changeContentEvent.getProblemId())
							.replaceSubmissionContent(changeContentEvent.getId(),
									changeContentEvent.getTeam());

					topLevelView.selectTab(activeToken.getId(),
							gadgetMaker.getGadget(activeToken, GadgetType.PROGRAM_PROBLEM_EDITOR, activeToken.getId())
									.getView().asWidget());
				}
			}
		});

		eventBus.addHandler(SelectResourceEvent.TYPE, new SelectResourceEventHandler() {
			public void onSelectResource(SelectResourceEvent event) {

				if (activeToken != null && activeToken.getType() != null) {

					List<Gadget> currentGadgets = gadgetMaker.searchById(activeToken.getId());
					for (Gadget gadget : currentGadgets) {
						if (gadget instanceof HasLearningTime
								&& (gadget instanceof ViewStatement || gadget instanceof VideoViewer)) {
							HasLearningTime hasLearningTime = (HasLearningTime) gadget;
							if (hasLearningTime.hasExceededLearningTime()) {
								hasLearningTime.notifySeenResource();
								addResourceRatingGadget(activeToken.getId(), activeToken);
							}
						} 
						
						if (gadget instanceof HasLearningTime) {
							((HasLearningTime) gadget).stopTime();
						}
					}
					final Date learningTime = new Date(new Date().getTime() - lastChange.getTime());
					final String resourceId = activeToken.getId();
					enkiService.syncResourceLearningTime(contextInfo.getactivityId(), resourceId, learningTime,
							new AsyncCallback<Void>() {

								@Override
								public void onFailure(Throwable e) {
								}

								@Override
								public void onSuccess(Void arg0) {
								}
							});
				}

				Token token = new Token();
				token.setId(event.getResourceId());
				token.setName(event.getResourceName());
				token.setType(event.getResourceType());
				token.setLink(event.getLink());
				token.setLanguage(event.getLanguage());

				activeToken = token;
				learningTime = event.getLearningTime();
				History.newItem(token.toString());
			}
		});

		eventBus.addHandler(SelectTabEvent.TYPE, new SelectTabEventHandler() {

			@Override
			public void onSelectTab(SelectTabEvent event) {
				topLevelView.selectTab(activeToken.getId(), event.getWidget());
			}
		});

		eventBus.addHandler(ResourceOnSuccessEvent.TYPE, new ResourceOnSuccessEventHandler() {

			@Override
			public void onResourceSolvedSuccessfully(final ResourceOnSuccessEvent event) {

				ResourcesTree resourcesTree = (ResourcesTree) gadgetMaker.getGadget(null, GadgetType.RESOURCE_TREE,
						"top");

				final CourseResource resource = ((ResourcesTreeView) resourcesTree.getView())
						.getOnSuccessResource(event.getResourceId());

				if (resource != null) {
					new EvaluationAcceptedDialog(enkiConstants.congratulations(), enkiConstants.msgCongratulations()) {
					}.addDialogHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent e) {
							eventBus.fireEvent(new SelectResourceEvent(event.getCourseId(), resource.getId(),
									resource.getTitle(), resource.getType(), resource.getHref(),
									resource.getLearningTime(), resource.getLanguage()));
						}
					});
				}
			}
		});

		eventManager.addListener(ReportNotificationEvent.class, new MooshakEventListener<ReportNotificationEvent>() {

			@Override
			public void receiveEvent(ReportNotificationEvent message) {
				ProgramTestCasesPresenter presenter = programPresenters.get(message.getProblemId());
				if (presenter == null) {

					KoraPresenter koraPresenter = koraPresenters.get(message.getProblemId());
					if (koraPresenter != null)
						koraPresenter.update(message.getSubmissionId(), message.isConsider());
					else {
						QuizPresenter quizPresenter = quizPresenters.get(message.getProblemId());
						if (quizPresenter != null)		
							quizPresenter.update(message.getSubmissionId(), message.isConsider());
						else {
							
							GameSubmissionPresenter gamePresenter = gamePresenters.get(message.getProblemId());
							if (gamePresenter != null) {
								gamePresenter.update(message.getSubmissionId(), message.isConsider());

								topLevelView.selectTab(activeToken.getId(),
										gadgetMaker
												.getGadget(activeToken, GadgetType.GAME_VIEWER, activeToken.getId())
												.getView()
												.asWidget());
							} else
								LOGGER.log(Level.SEVERE, "Unknown problem ID:" + message.getProblemId());
						}
					}
				} else
					presenter.update(message.getSubmissionId(), message.isConsider());

				topLevelView.selectTab(activeToken.getId(),
						gadgetMaker
								.getGadget(activeToken, GadgetType.PROGRAM_PROBLEM_OBSERVATIONS, activeToken.getId())
								.getView()
								.asWidget());
			}
		});

		eventManager.addListener(LogoutEvent.class, new MooshakEventListener<LogoutEvent>() {

			@Override
			public void receiveEvent(LogoutEvent event) {
				AuthenticationPresenter.logout(event.getReason());
				LOGGER.log(Level.INFO, event.getReason());
			}

		});

		eventManager.addListener(ContextChangedEvent.class, new MooshakEventListener<ContextChangedEvent>() {

			@Override
			public void receiveEvent(ContextChangedEvent message) {

				updateContextInfo();
			}
		});

		eventManager.addListener(ResourceStateUpdateEvent.class, new MooshakEventListener<ResourceStateUpdateEvent>() {

			@Override
			public void receiveEvent(ResourceStateUpdateEvent event) {
				LOGGER.log(Level.INFO, "Resource state updated: " + event.getId() + " " + event.getState().toString());
				topLevelView.updateResourceState(event.getCourseId(), event.getId(), event.getState());
			}
		});

		eventBus.addHandler(HelpTutorialEvent.TYPE, new HelpTutorialEventHandler() {
			public void onHelpTutorial(HelpTutorialEvent event) {
				topLevelView.setTutorialAnchorPoints(tutorialView);
				tutorialView.showTutorial(activeToken);
			}
		});

		eventManager.addListener(LeaderboardUpdateEvent.class, new MooshakEventListener<LeaderboardUpdateEvent>() {

			@Override
			public void receiveEvent(LeaderboardUpdateEvent event) {
				LOGGER.log(Level.INFO, "Received leaderboard updated");

				Token tmp = new Token();

				String resourceId = event.getResourceId() == null ? "top" : event.getResourceId();
				tmp.setId(resourceId);

				if (!gadgetMaker.hasGadget(tmp, GadgetType.LEADERBOARD_TABLE, resourceId))
					return;

				LeaderboardTable gadget = (LeaderboardTable) gadgetMaker.getGadget(tmp, GadgetType.LEADERBOARD_TABLE,
						resourceId);
				if (gadget == null)
					return;
				((LeaderboardView) gadget.getView()).updateLeaderboard(event.getLeaderboard());
			}
		});

		eventManager.addListener(ProblemStatisticsUpdateEvent.class,
				new MooshakEventListener<ProblemStatisticsUpdateEvent>() {

					@Override
					public void receiveEvent(ProblemStatisticsUpdateEvent event) {
						LOGGER.log(Level.INFO, "Received statistics updated: " + event.getResourceId());

						Token tmp = new Token();

						String resourceId = event.getResourceId() == null ? "top" : event.getResourceId();
						tmp.setId(resourceId);

						if (!gadgetMaker.hasGadget(tmp, GadgetType.STATISTICS_CHART, resourceId))
							return;

						StatsChart gadget = (StatsChart) gadgetMaker.getGadget(tmp, GadgetType.STATISTICS_CHART,
								resourceId);
						if (gadget == null)
							return;

						ProblemStatistics stats = new ProblemStatistics();
						stats.setNumberOfStudentsWhoSolved(event.getNumberOfStudentsWhoSolved());
						stats.setNumberOfStudentsWhoTried(event.getNumberOfStudentsWhoTried());
						stats.setSolvedAtFirst(event.getSolvedAtFirst());
						stats.setSolvedAtSecond(event.getSolvedAtSecond());
						stats.setSolvedAtThird(event.getSolvedAtThird());
						stats.setSolvingAttempts(event.getSolvingAttempts());
						stats.setTotalStudents(event.getTotalStudents());

						((StatsChartView) gadget.getView()).updateStatistics(stats);
					}
				});

		eventManager.addListener(AchievementsUpdateEvent.class, new MooshakEventListener<AchievementsUpdateEvent>() {

			@Override
			public void receiveEvent(AchievementsUpdateEvent event) {
				LOGGER.log(Level.INFO, "Received " + event.getState() + " achievements updated");

				Token tmp = new Token();

				String resourceId = event.getResourceId() == null ? "top" : event.getResourceId();
				tmp.setId(resourceId);

				if (!gadgetMaker.hasGadget(tmp, GadgetType.ACHIEVEMENTS_LIST, resourceId))
					return;

				AchievementList gadget = (AchievementList) gadgetMaker.getGadget(tmp, GadgetType.ACHIEVEMENTS_LIST,
						resourceId);
				if (gadget == null)
					return;
				if (event.getState() != null && event.getState().equals("UNLOCKED")) {
					((AchievementsView) gadget.getView()).clearUnlockedAchievements();
					((AchievementsView) gadget.getView()).addAchievements(event.getAchievements().getItems());
				} else {
					((AchievementsView) gadget.getView()).clearRevealedAchievements();
					((AchievementsView) gadget.getView()).addAchievements(event.getAchievements().getItems());
				}
			}
		});

		eventManager.addListener(StudentProfileUpdateEvent.class,
				new MooshakEventListener<StudentProfileUpdateEvent>() {

					@Override
					public void receiveEvent(StudentProfileUpdateEvent event) {
						LOGGER.log(Level.INFO, "Received profile updated: " + event.getStudentName());

						Token tmp = new Token();
						tmp.setId("top");

						if (!gadgetMaker.hasGadget(tmp, GadgetType.MY_DATA, "top"))
							return;

						MyData gadget = (MyData) gadgetMaker.getGadget(tmp, GadgetType.MY_DATA, "top");
						if (gadget == null)
							return;

						StudentProfile profile = new StudentProfile();
						profile.setAcceptedSubmissions(event.getAcceptedSubmissions());
						profile.setCurrentPart(event.getCurrentPart());
						profile.setRegistrationDate(event.getRegistrationDate());
						profile.setSolvedExercises(event.getSolvedExercises());
						profile.setStaticResourcesSeen(event.getStaticResourcesSeen());
						profile.setStudentName(event.getStudentName());
						profile.setSubmissions(event.getSubmissions());
						profile.setUnsolvedExercises(event.getUnsolvedExercises());
						profile.setVideoResourcesSeen(event.getVideoResourcesSeen());

						((MyDataView) gadget.getView()).setProfile(profile);
					}
				});

		eventManager.addListener(RatingUpdateEvent.class, new MooshakEventListener<RatingUpdateEvent>() {

			@Override
			public void receiveEvent(RatingUpdateEvent event) {
				LOGGER.log(Level.INFO, "Received rating updated");

				Token tmp = new Token();

				String resourceId = event.getResourceId() == null ? "top" : event.getResourceId();
				tmp.setId(resourceId);

				if (!gadgetMaker.hasGadget(tmp, GadgetType.RESOURCE_RATING, resourceId))
					return;

				ResourceRating gadget = (ResourceRating) gadgetMaker.getGadget(tmp, GadgetType.RESOURCE_RATING,
						resourceId);
				if (gadget == null)
					return;
				((ResourceRatingView) gadget.getView()).setRating(event.getRating(), event.getComment());
			}
		});
		
		eventManager.addListener(NewAcceptedSubmissionEvent.class, 
				new MooshakEventListener<NewAcceptedSubmissionEvent>() {

			@Override
			public void receiveEvent(NewAcceptedSubmissionEvent event) {
				GameSubmissionPresenter presenter = gamePresenters.get(event.getProblemId());
				presenter.addSubmission(event.getSubmissionId(), event.getTeamId());
			}
		});
		
		eventManager.addListener(TournamentMovieEvent.class, 
				new MooshakEventListener<TournamentMovieEvent>() {

					@Override
					public void receiveEvent(TournamentMovieEvent event) {						
						GameViewerPresenter viewerPresenter = gameViewerPresenters.get(event.getProblemId());
						viewerPresenter.setTournament(event.getTournamentId());
					}
		});

		enkiService.refreshMySubmissionRows(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				LOGGER.log(Level.INFO, "My submission updates processed");
				eventManager.refresh();
			}

			@Override
			public void onFailure(Throwable caught) {
				throwing("Refreshing my submission events", caught);
			}
		});
		enkiService.refreshQuestionRows(new AsyncCallback<Void>() {

			@Override
			public void onSuccess(Void result) {
				LOGGER.log(Level.INFO, "Question updates processed");
				eventManager.refresh();
			}

			@Override
			public void onFailure(Throwable caught) {
				throwing("Refreshing question events", caught);
			}
		});
		LOGGER.log(Level.INFO, "Updates requested");

		new ListenerManager<QuestionsUpdate>().addEventListener(QuestionsUpdate.class, Kind.QUESTIONS, "question");
		new ListenerManager<SubmissionsUpdate>().addEventListener(SubmissionsUpdate.class, Kind.SUBMISSIONS,
				"submission");
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	/**
	 * Listener creator for ListingUpdateEvent. Data on the received events is
	 * forward to a ListingDataProvider of the given kind.
	 *
	 * @param <T>
	 *            a type of event extending ListingUpdateEvent
	 */
	private class ListenerManager<T extends ListingUpdateEvent> {

		void addEventListener(final Class<T> type, final Kind kind, final String label) {

			final ListingDataProvider provider = ListingDataProvider.getDataProvider(kind);

			eventManager.addListener(type, new MooshakEventListener<T>() {

				@Override
				public void receiveEvent(T event) {
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
			activeToken = token;
			History.newItem(token.toString());
		} else {
			History.fireCurrentHistoryState();
		}
		
		/*new TimeSelectionDialog("Select the time that you have available for this course")
			.addDialogHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
				}
			});*/
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {

		final Token token;
		if (activeToken != null)
			token = activeToken;
		else
			token = new Token(event.getValue());

		lastChange = new Date();

		showWaitCursor();

		if (token.getId().equals("")) {

			makeToplevel();

			topLevelView.setResourceTreeGadget(
					(ResourcesTree) gadgetMaker.getGadget(null, GadgetType.RESOURCE_TREE, "top"));
			topLevelView.setCurrentResourceId("top");

			addLeaderboardGadget("top", token);
			addAchievementsGadget("top", token);
			addMyDataGadget("top", token);

		} else if (topLevelView == null) {
			redirectToToplevel();
		} else {
			topLevelView.setCurrentResourceId(token.getId());

			Gadget gadget;
			switch (token.getType()) {
			case PROBLEM:
				// add statement gadget
				Gadget gadgetStatement = gadgetMaker.getGadget(token, GadgetType.STATEMENT, token.getId());
				topLevelView.addGadget(token.getId(), gadgetStatement, Region.CENTER);

				// add observations gadget
				final Gadget gadgetObs = gadgetMaker.getGadget(token, GadgetType.PROGRAM_PROBLEM_OBSERVATIONS,
						token.getId());
				topLevelView.addGadget(token.getId(), gadgetObs, Region.SOUTH);

				String problemId = token.getId();
				if (problemId != null && problemId.lastIndexOf("/") != -1)
					problemId = problemId.substring(problemId.lastIndexOf("/") + 1);

				EditorKind editorKind = editors.get(problemId);
				if (editorKind != null) {
					if (editorKind.equals(EditorKind.DIAGRAM))
						addKoraGadget(token, gadgetObs);
					else if (editorKind.equals(EditorKind.GAME))
						addGameEditorGadget(token, gadgetObs);
					else
						addProgramEditorGadget(token, gadgetObs);

					topLevelView.selectTab(activeToken.getId(), gadgetMaker
							.getGadget(activeToken, GadgetType.STATEMENT, activeToken.getId()).getView().asWidget());
				} else {

					getEditorKind(token, problemId, gadgetObs);
				}

				// add statistics' chart gadget
				Gadget gadgetStats = gadgetMaker.getGadget(token, GadgetType.STATISTICS_CHART, token.getId());
				topLevelView.addGadget(token.getId(), gadgetStats, Region.NORTHEAST);

				addLeaderboardGadget(token.getId(), token);
				addMyDataGadget(token.getId(), token);
				addResourceRatingGadget(token.getId(), token);
				break;

			case PDF:
				gadget = gadgetMaker.getGadget(token, GadgetType.STATEMENT, token.getId());
				topLevelView.addGadget(token.getId(), gadget, Region.CENTER);

				updateMissingLearningTime(gadget, token, learningTime);

				addLeaderboardGadget("top", token);
				addRelatedResourcesGadget(token);
				addMyDataGadget(token.getId(), token);

				break;

			case VIDEO:
				gadget = gadgetMaker.getGadget(token, GadgetType.VIDEO, token.getId());
				topLevelView.addGadget(token.getId(), gadget, Region.CENTER);

				updateMissingLearningTime(gadget, token, learningTime);

				addLeaderboardGadget("top", token);
				addRelatedResourcesGadget(token);
				addMyDataGadget(token.getId(), token);

				break;

			default:
				break;
			}
		}

		showDefaultCursor();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				if (storage.getItem("showHelp") != null) {
					if (!storage.getItem("showHelp").equals("no")) {
						topLevelView.setTutorialAnchorPoints(tutorialView);
						tutorialView.showTutorial(activeToken);
					}
				} else {
					topLevelView.setTutorialAnchorPoints(tutorialView);
					tutorialView.showTutorial(activeToken);
				}
			}
		});

	}

	private void getEditorKind(final Token token, final String problemId, final Gadget gadgetObs) {

		participantService.getEditorKind(problemId, new AsyncCallback<EditorKind>() {

			@Override
			public void onSuccess(EditorKind editorKind) {
				if (editorKind == null)
					addProgramEditorGadget(token, gadgetObs);
				else {
					switch (editorKind) {
					case DIAGRAM:
						addKoraGadget(token, gadgetObs);
						break;
					case CODE:
						addProgramEditorGadget(token, gadgetObs);
						break;
					case GAME:
						addGameEditorGadget(token, gadgetObs);
						break;
					case QUIZ:
						addQuizGadget(token, gadgetObs);
						break;

					default:
						addProgramEditorGadget(token, gadgetObs);
						break;
					}
				}

				topLevelView.selectTab(activeToken.getId(), gadgetMaker
						.getGadget(activeToken, GadgetType.STATEMENT, activeToken.getId()).getView().asWidget());

				editors.put(problemId, editorKind);
			}

			@Override
			public void onFailure(Throwable arg0) {
				addProgramEditorGadget(token, gadgetObs);
			}
		});
	}

	/**
	 * Update context info when context changes
	 */
	private void updateContextInfo() {
		basicService.context(new AsyncCallback<ContextInfo>() {

			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
			}

			@Override
			public void onSuccess(ContextInfo contextInfo) {

				long diff = new Date().getTime() - contextInfo.getCurrent().getTime();

				if (contextInfo.getStart() != null)
					contextInfo.setStart(new Date(contextInfo.getStart().getTime() + diff));
				if (contextInfo.getEnd() != null)
					contextInfo.setEnd(new Date(contextInfo.getEnd().getTime() + diff));

				AppController.contextInfo = contextInfo;
				GadgetPresenter.setContextInfo(contextInfo);

				if (toplevelPresenter != null)
					toplevelPresenter.setContext(contextInfo);
			}
		});
	}

	/**
	 * Update the learning time missing
	 * 
	 * @param gadget
	 * @param token
	 * @param learningTime
	 */
	private void updateMissingLearningTime(final Gadget gadget, final Token token, final Date learningTime) {

		enkiService.getResourceLearningTime(contextInfo.getactivityId(), token.getId(), new AsyncCallback<Date>() {

			@Override
			public void onSuccess(Date date) {
				long time = learningTime == null ? 0 : learningTime.getTime();
				if (time - date.getTime() < 0)
					addResourceRatingGadget(token.getId(), token);
				else
					((HasLearningTime) gadget).setLearningTime(new Date(time - date.getTime()));
			}

			@Override
			public void onFailure(Throwable e) {
				((HasLearningTime) gadget).setLearningTime(learningTime);
			}
		});

	}

	/**
	 * Make a a card top level
	 */
	private void makeToplevel() {

		if (topLevelView == null) {
			topLevelView = new TopLevelViewImpl();
			tutorialView.setTutorialView(topLevelView);
		}

		toplevelPresenter = new TopLevelPresenter(enkiService, basicService, eventBus, topLevelView);
		toplevelPresenter.setContext(contextInfo);

		toplevelPresenter.go(container);
	}

	/**
	 * add related resources gadget
	 * 
	 * @param token
	 */
	private void addRelatedResourcesGadget(Token token) {

		Gadget gadget = gadgetMaker.getGadget(token, GadgetType.RELATED_RESOURCES, token.getId());
		topLevelView.addGadget(token.getId(), gadget, Region.SOUTH);
	}

	/**
	 * add leaderboard gadget
	 * 
	 * @param id
	 * @param token
	 */
	private void addLeaderboardGadget(String id, Token token) {
		id = token.getId() == null || token.getId().equals("") ? id : token.getId();

		Gadget gadgetLeaderboard = gadgetMaker.getGadget(token, GadgetType.LEADERBOARD_TABLE, id);
		topLevelView.addGadget(id, gadgetLeaderboard, Region.NORTHEAST);
	}

	/**
	 * add achievements gadget
	 * 
	 * @param id
	 * @param token
	 */
	private void addAchievementsGadget(String id, Token token) {
		id = token.getId() == null || token.getId().equals("") ? id : token.getId();

		Gadget gadget = gadgetMaker.getGadget(token, GadgetType.ACHIEVEMENTS_LIST, id);
		topLevelView.addGadget(id, gadget, Region.NORTHEAST);
	}

	/**
	 * add mydata gadget
	 * 
	 * @param id
	 * @param token
	 */
	private void addMyDataGadget(String id, Token token) {
		id = token.getId() == null || token.getId().equals("") ? id : token.getId();

		Gadget gadget = gadgetMaker.getGadget(token, GadgetType.MY_DATA, id);
		((MyDataPresenter) gadget.getPresenter()).setStudentId(topLevelView.getTeam());
		topLevelView.addGadget(id, gadget, Region.NORTHEAST);
	}

	/**
	 * Add resource rating gadget
	 * 
	 * @param id
	 * @param token
	 */
	private void addResourceRatingGadget(String id, Token token) {
		id = token.getId() == null || token.getId().equals("") ? id : token.getId();

		Gadget gadget = gadgetMaker.getGadget(token, GadgetType.RESOURCE_RATING, id);
		topLevelView.addGadget(id, gadget, Region.SOUTH);
	}

	/**
	 * Add ask question gadget
	 * 
	 * @param id
	 * @param token
	 */
	private void addAskQuestionGadget(String id, Token token) {
		id = token.getId() == null || token.getId().equals("") ? id : token.getId();

		Gadget gadget = gadgetMaker.getGadget(token, GadgetType.ASK_QUESTION, id);
		topLevelView.addGadget(id, gadget, Region.CENTER);
	}

	/**
	 * Add my submissions gadget
	 * 
	 * @param id
	 * @param token
	 */
	private void addMySubmissionsGadget(String id, Token token) {
		id = token.getId() == null || token.getId().equals("") ? id : token.getId();

		Gadget gadget = gadgetMaker.getGadget(token, GadgetType.MY_SUBMISSIONS, id);
		topLevelView.addGadget(id, gadget, Region.CENTER);

		((MySubmissionsView) gadget.getView()).setFiltering();
	}

	/**
	 * Add the program editor gadget
	 * 
	 * @param token
	 * @param gadgetObs
	 */
	private void addProgramEditorGadget(Token token, Gadget gadgetObs) {

		// add error list gadget
		final Gadget gadgetErrorList = gadgetMaker.getGadget(token, GadgetType.PROGRAM_ERROR_LIST, token.getId());
		topLevelView.addGadget(token.getId(), gadgetErrorList, Region.SOUTH);
		((ProgramObservationsPresenter) gadgetObs.getPresenter())
				.setErrorListView((ProgramErrorListView) gadgetErrorList.getView());

		// add test cases gadget
		final Gadget gadgetTests = gadgetMaker.getGadget(token, GadgetType.PROGRAM_PROBLEM_TESTS, token.getId());
		((ProgramTestCasesPresenter) gadgetTests.getPresenter())
				.setObservationsView((ProgramObservationsView) gadgetObs.getView());
		topLevelView.addGadget(token.getId(), gadgetTests, Region.EAST);

		// add editor gadget
		Gadget gadgetEditor = gadgetMaker.getGadget(token, GadgetType.PROGRAM_PROBLEM_EDITOR, token.getId());
		((ProgramEditorPresenter) gadgetEditor.getPresenter())
				.setTestCasesView((ProgramTestCasesView) gadgetTests.getView());
		((ProgramEditorView) gadgetEditor.getView()).setResponsive(true);
		topLevelView.addGadget(token.getId(), gadgetEditor, Region.CENTER);

		addAskQuestionGadget(token.getId(), token);
		addMySubmissionsGadget(token.getId(), token);

		((ProgramTestCasesPresenter) gadgetTests.getPresenter())
				.setEditorView((ProgramEditorView) gadgetEditor.getView());
		((ProgramErrorListPresenter) gadgetErrorList.getPresenter())
				.setEditorView((ProgramEditorView) gadgetEditor.getView());
		((ProgramEditorPresenter) gadgetEditor.getPresenter())
				.setObservationsView((ProgramObservationsView) gadgetObs.getView());
		((ProgramObservationsPresenter) gadgetObs.getPresenter())
				.setEditorView((ProgramEditorView) gadgetEditor.getView());

		String problemId = token.getId();
		if (problemId != null && problemId.lastIndexOf("/") != -1)
			problemId = problemId.substring(problemId.lastIndexOf("/") + 1);
		programPresenters.put(problemId, ((ProgramTestCasesPresenter) gadgetTests.getPresenter()));

		updateMissingLearningTime(gadgetTests, token, learningTime);

		// inform tutorial view that it has a diagram editor
		tutorialView.setDiagramEditor(false);
	}

	/**
	 * Add the game editor gadget
	 * 
	 * @param token
	 * @param gadgetObs
	 */
	private void addGameEditorGadget(Token token, Gadget gadgetObs) {
		
		String problemId = token.getId();
		if (problemId != null && problemId.lastIndexOf("/") != -1)
			problemId = problemId.substring(problemId.lastIndexOf("/") + 1);

		// add error list gadget
		final Gadget gadgetErrorList = gadgetMaker.getGadget(token, GadgetType.PROGRAM_ERROR_LIST, token.getId());
		topLevelView.addGadget(token.getId(), gadgetErrorList, Region.SOUTH);
		((ProgramObservationsPresenter) gadgetObs.getPresenter())
				.setErrorListView((ProgramErrorListView) gadgetErrorList.getView());

		// add game viewer gadget
		final Gadget gadgetGameViewer = gadgetMaker.getGadget(token, GadgetType.GAME_VIEWER, token.getId());
		gameViewerPresenters.put(problemId, ((GameViewerPresenter) gadgetGameViewer.getPresenter()));

		// add game submission gadget
		final Gadget gadgetGameSubmission = gadgetMaker.getGadget(token, GadgetType.GAME_SUBMISSION, token.getId());
		((GameSubmissionPresenter) gadgetGameSubmission.getPresenter())
				.setObservationsView((ProgramObservationsView) gadgetObs.getView());
		((GameSubmissionPresenter) gadgetGameSubmission.getPresenter())
				.setGameViewerView((GameViewerView) gadgetGameViewer.getView());
		topLevelView.addGadget(token.getId(), gadgetGameSubmission, Region.EAST);

		// add editor gadget
		Gadget gadgetEditor = gadgetMaker.getGadget(token, GadgetType.PROGRAM_PROBLEM_EDITOR, token.getId());
		((ProgramEditorView) gadgetEditor.getView()).setResponsive(true);
		topLevelView.addGadget(token.getId(), gadgetEditor, Region.CENTER);
		topLevelView.addGadget(token.getId(), gadgetGameViewer, Region.CENTER);

		addAskQuestionGadget(token.getId(), token);
		addMySubmissionsGadget(token.getId(), token);

		((GameSubmissionPresenter) gadgetGameSubmission.getPresenter())
				.setEditorView((ProgramEditorView) gadgetEditor.getView());
		((ProgramErrorListPresenter) gadgetErrorList.getPresenter())
				.setEditorView((ProgramEditorView) gadgetEditor.getView());
		((ProgramEditorPresenter) gadgetEditor.getPresenter())
				.setObservationsView((ProgramObservationsView) gadgetObs.getView());
		((ProgramObservationsPresenter) gadgetObs.getPresenter())
				.setEditorView((ProgramEditorView) gadgetEditor.getView());

		gamePresenters.put(problemId, ((GameSubmissionPresenter) gadgetGameSubmission.getPresenter()));

		updateMissingLearningTime(gadgetGameSubmission, token, learningTime);

		// inform tutorial view that it hasn't a diagram editor
		tutorialView.setDiagramEditor(false);
	}

	/**
	 * Add the Kora gadget
	 * 
	 * @param token
	 * @param gadgetObs
	 */
	private void addKoraGadget(Token token, Gadget gadgetObs) {

		// add editor gadget
		Gadget gadgetEditor = gadgetMaker.getGadget(token, GadgetType.KORA, token.getId());
		topLevelView.addGadget(token.getId(), gadgetEditor, Region.CENTER);

		((KoraPresenter) gadgetEditor.getPresenter())
				.setObservationsView((ProgramObservationsView) gadgetObs.getView());

		String problemId = token.getId();
		if (problemId != null && problemId.lastIndexOf("/") != -1)
			problemId = problemId.substring(problemId.lastIndexOf("/") + 1);
		koraPresenters.put(problemId, ((KoraPresenter) gadgetEditor.getPresenter()));

		updateMissingLearningTime(gadgetEditor, token, learningTime);
		addAskQuestionGadget(token.getId(), token);
		addMySubmissionsGadget(token.getId(), token);

		// inform tutorial view that it has a diagram editor
		tutorialView.setDiagramEditor(true);
	}

	/**
	 * Add the Quiz gadget
	 * 
	 * @param token
	 * @param gadgetObs
	 */
	private void addQuizGadget(Token token, Gadget gadgetObs) {

		// add editor gadget
		Gadget gadgetEditor = gadgetMaker.getGadget(token, GadgetType.QUIZ, token.getId());
		topLevelView.addGadget(token.getId(), gadgetEditor, Region.CENTER);

		((QuizPresenter) gadgetEditor.getPresenter())
				.setObservationsView((ProgramObservationsView) gadgetObs.getView());

		String problemId = token.getId();
		if (problemId != null && problemId.lastIndexOf("/") != -1)
			problemId = problemId.substring(problemId.lastIndexOf("/") + 1);
		quizPresenters.put(problemId, ((QuizPresenter) gadgetEditor.getPresenter()));

		updateMissingLearningTime(gadgetEditor, token, learningTime);
		//addAskQuestionGadget(token.getId(), token);
		addMySubmissionsGadget(token.getId(), token);

		// inform tutorial view that it has a diagram editor
		tutorialView.setDiagramEditor(false);
	}

}
