package pt.up.fc.dcc.mooshak.client.guis.creator;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.client.data.admin.DataObject.Processor;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.creator.Token.Command;
import pt.up.fc.dcc.mooshak.client.guis.creator.event.ViewProblemEvent;
import pt.up.fc.dcc.mooshak.client.guis.creator.event.ViewProblemEventHandler;
import pt.up.fc.dcc.mooshak.client.guis.creator.presenter.ProblemPresenter;
import pt.up.fc.dcc.mooshak.client.guis.creator.presenter.TopLevelPresenter;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.ProblemView;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.ProblemViewImpl;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.creator.view.TopLevelViewImpl;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.CreatorCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.widgets.CardPanel;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;
import pt.up.fc.dcc.mooshak.shared.events.ObjectUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.ProblemTestSummary;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

public class AppController extends AbstractAppController implements
		ValueChangeHandler<String> {

	private CreatorCommandServiceAsync rpc;
	private BasicCommandServiceAsync rpcBasic;
	private ParticipantCommandServiceAsync rpcParticipant;
	private KoraCommandServiceAsync rpcKora;
	private HandlerManager eventBus;
	private HasWidgets container;

	private Map<String, ProblemPresenter> problemPresenters = 
			new HashMap<String, ProblemPresenter>();

	private DataManager dataManager = DataManager.getInstance();
	
	private EventManager eventManager = EventManager.getInstance();

	private TopLevelView topLevelView = null;

	private String currentObjectId = null;
	private Token currentToken = null;
	private String problems = null;
	private String solutions = null;
	private String tests = null;
	private String skeletons = null;
	private String images = null;

	public AppController(
		AdminCommandServiceAsync adminService,
		BasicCommandServiceAsync rpcBasic, 
		CreatorCommandServiceAsync rpcService, 
		ParticipantCommandServiceAsync rpcParticipant, 
		KoraCommandServiceAsync rpcKora,
		HandlerManager eventBus
	) {
		this.rpcBasic = rpcBasic;
		this.rpc = rpcService;
		this.rpcParticipant = rpcParticipant;
		this.rpcKora = rpcKora;
		this.eventBus = eventBus;

		rpc.getProblemsPath(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String result) {
				problems = result;
				if(topLevelView != null)
					topLevelView.setProblemsObjectId(result);
			}
		});
		rpc.getTestsPath(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				tests = result;
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		rpc.getSkeletonsPath(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				skeletons = result;
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		rpc.getImagesPath(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				images = result;
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});
		rpc.getSolutionsPath(new AsyncCallback<String>() {

			@Override
			public void onSuccess(String result) {
				solutions = result;
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});

		bind();
	}
	
	private RegExp problemObjId = RegExp.compile(".*/problems/([A-Za-z0-9_]+)(/.*)?$");

	private void bind() {
		History.addValueChangeHandler(this);

		eventBus.addHandler(ViewProblemEvent.TYPE,
				new ViewProblemEventHandler() {
					public void onViewStatement(ViewProblemEvent event) {
						
						if (event.getProblemId() == null || event.getProblemId().isEmpty()) {
							redirectToToplevel();
						} else {
							Token token = new Token();
							token.setCommand(Command.VIEW);
							token.setId(event.getProblemId());
							token.setName(event.getProblemLabel());
							History.newItem(token.toString());
						}
					}
				});

		eventManager.addListener(ProblemTestSummary.class,
				new MooshakEventListener<ProblemTestSummary>() {

					@Override
					public void receiveEvent(ProblemTestSummary event) {
						ProblemPresenter pp = problemPresenters.get(event.getProblemId());
						if(pp != null)
							pp.addObservation(event.getText());
					}
				});

		
			eventManager.addListener(ObjectUpdateEvent.class,
				new MooshakEventListener<ObjectUpdateEvent>() {

					@Override
					public void receiveEvent(ObjectUpdateEvent event) {
						final String id = event.getId();
						
						MatchResult m = problemObjId.exec(id);
						if (m == null)
							return;
							
						final String problemId = m.getGroup(1);
						
						if (!id.equals(currentObjectId)) {
							if (!problemId.trim().equals(""))
								dataManager.getMooshakObject(id);
						}
						
						LOGGER.log(Level.INFO,"Object updated:"+id);
						dataManager.updateObject(event.getId(),new Processor() {

							@Override
							public void process(DataObject dataObject) {
								if(id != null) {
									if(id.equals(currentObjectId)) {
										updateObject(dataObject);
										updateSolutions(currentToken.getId(), id);
									}
									else if(id.lastIndexOf(tests + "/") != -1) {
										String updated = id.substring(0, id
												.lastIndexOf(tests + "/"));
										if(currentObjectId.equals(updated)) {
											final ProblemPresenter presenter 
												= problemPresenters.get(problemId);

											loadTest(presenter, dataObject.getId());
										}
									}
									else if(id.lastIndexOf(tests) != -1) {
										String updated = id.substring(0, id
												.lastIndexOf(tests));
										if(currentObjectId.equals(updated)) {
											final ProblemPresenter presenter 
												= problemPresenters.get(problemId);

											for (String test : dataObject
													.getChildrenProvider().getList())
												loadTest(presenter, test);
										}
										
									}
									else if(id.lastIndexOf(skeletons + "/") != -1) {
										String updated = id.substring(0, id
												.lastIndexOf(skeletons + "/"));
										if(currentObjectId.equals(updated)) {
											final ProblemPresenter presenter 
												= problemPresenters.get(problemId);

											loadProgramFile(presenter, dataObject.getId());
										}
									}
									else if(id.lastIndexOf(skeletons) != -1) {
										String updated = id.substring(0, id
												.lastIndexOf(skeletons));
										if(currentObjectId.equals(updated)) {
											final ProblemPresenter presenter 
												= problemPresenters.get(problemId);

											clearProgramDataProvider(presenter, "Skeleton");
											for (String skeleton : dataObject
													.getChildrenProvider().getList())
												loadProgramFile(presenter, skeleton);
										}
										
									}
									else if(id.lastIndexOf(images) != -1) {
										String updated = id.substring(0, id
												.lastIndexOf(images));
										if(currentObjectId.equals(updated)) {
											final ProblemPresenter presenter 
												= problemPresenters.get(problemId);

											presenter.setImagesDataProvider(dataObject
													.getFormDataProvider());
											dataObject.getFormDataProvider().refresh();
										}
										
									}
									else if(id.lastIndexOf(solutions) != -1) {
										String updated = id.substring(0, id
												.lastIndexOf(solutions));
										if(currentObjectId.equals(updated)) {
											final ProblemPresenter presenter 
												= problemPresenters.get(problemId);
											clearProgramDataProvider(presenter, "Solution");
											presenter.setSolutions(dataObject.getData()
													.getFieldValue("Solution"), id);
											dataObject.getFormDataProvider().refresh();
										}
										
									} else {	
										MatchResult m = problemObjId.exec(id);
										if (m != null) {
											String problemId = m.getGroup(1);
											if (problemId != null && !problemId.trim().equals(""))
												topLevelView.addProblemId(problemId);
										}
									}
								}
							}
							
						});
					}
			});
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

		if (command == Command.TOP) {
			makeToplevel();
		} else if (topLevelView == null) {
			redirectToToplevel();
		} else {
			final CardPanel cardPanel = topLevelView.getContent();

			showWaitCursor();
			String objectId = problems + token.getId();

			if (!cardPanel.hasCard(token.toString()))
				makeViewProblem(cardPanel, token);
			
			cardPanel.showCard(token.toString());
			
			currentObjectId = objectId;
			currentToken = token;
			
			for (ProblemPresenter pp : problemPresenters.values()) {
				pp.setShowing(false);
			}
			problemPresenters.get(token.getId()).setShowing(true);
			
			dataManager.getMooshakObject(objectId, new Processor() {

				// process this when data is available
				public void process(DataObject dataObject) {
					final String objectId = dataObject.getId();
					
					MatchResult m = problemObjId.exec(dataObject.getId());
					final String problemId;
					if (m == null)
						return;
						
					problemId = m.getGroup(1);

					final ProblemPresenter presenter = problemPresenters
							.get(problemId);
					
					FormDataProvider provider = dataObject
							.getFormDataProvider();

					dataManager.getMooshakObject(objectId + tests,
							new Processor() {

								@Override
								public void process(DataObject dataObject) {
									clearTestProvider(presenter);
									for (String test : dataObject
											.getChildrenProvider().getList())
										loadTest(presenter, test);
								}
							});

					dataManager.getMooshakObject(objectId + skeletons,
							new Processor() {

								@Override
								public void process(DataObject dataObject) {
									clearProgramDataProvider(presenter, "Skeleton");
									for (String skeleton : dataObject
											.getChildrenProvider().getList())
										loadProgramFile(presenter, skeleton);
								}
							});
					dataManager.getMooshakObject(objectId + images,
							new Processor() {

								@Override
								public void process(DataObject dataObject) {
									presenter.setImagesDataProvider(dataObject
											.getFormDataProvider());
								}
							});
					
					updateSolutions(problemId, objectId);
					
					dataManager.getMooshakObject(objectId + solutions,
							new Processor() {

								@Override
								public void process(DataObject dataObject) {
									clearProgramDataProvider(presenter, "Solution");
									presenter.setSolutions(dataObject.getData()
											.getFieldValue("Solution"), objectId
											+ solutions);
								}
							});

					presenter.setObjectId(objectId);
					presenter.setDataProvider(provider);
					provider.refresh();
				}
			});

			showDefaultCursor();
			topLevelView.setProblemId(objectId);
		}
	}

	/**
	 * @param token
	 * @param objectId
	 */
	private void updateSolutions(final String problemId, final String objectId) {
		
		rpc.getSolutionsValue(problemId, 
				new AsyncCallback<MooshakValue>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE, caught.getLocalizedMessage());
			}

			@Override
			public void onSuccess(MooshakValue result) {
				if(result.getField().equals("Program")) {
					problemPresenters.get(problemId).setSolutions(result,
							objectId);
				}
				else {
					problemPresenters.get(problemId).setSolutions(result,
							objectId + solutions);
				}
			}
		});
	}

	/**
	 * Clear test provider
	 * @param presenter
	 * @param test
	 */
	private void clearTestProvider(final ProblemPresenter presenter) {
		presenter.clearTestProvider();
	}

	/**
	 * Loads a single test to provider
	 * @param presenter
	 * @param test
	 */
	private void loadTest(final ProblemPresenter presenter, final String test) {
		dataManager.getMooshakObject(test, new Processor() {

			@Override
			public void process(DataObject dataObject) {
				presenter.addTestToList(test, dataObject.getFormDataProvider());
			}
		});

	}

	/**
	 * Clear program file provider
	 * @param presenter
	 * @param program
	 */
	private void clearProgramDataProvider(final ProblemPresenter presenter,
			String field) {
		presenter.clearProgramDataProvider(field);
	}

	/**
	 * Loads a program file
	 * @param presenter
	 * @param program
	 */
	private void loadProgramFile(final ProblemPresenter presenter, final String program) {
		dataManager.getMooshakObject(program, new Processor() {

			@Override
			public void process(DataObject dataObject) {
				presenter.addProgramDataToList(program, dataObject.getData());
			}
		});

	}

	/**
	 * Make a a card top level
	 */
	private void makeToplevel() {

		if (topLevelView == null) {
			topLevelView = new TopLevelViewImpl();

		}
		new TopLevelPresenter(rpc, rpcBasic, rpcParticipant,
				eventBus, topLevelView, 
				problems).go(container);
	}

	/**
	 * Make a a card for viewing a given problem
	 * 
	 * @param cardPanel
	 * @param token
	 */
	private void makeViewProblem(final CardPanel cardPanel, final Token token) {

		if (topLevelView != null) {
			
			ProblemView problemView = new ProblemViewImpl();

			ProblemPresenter problemPresenter = new ProblemPresenter(rpc,
					rpcKora, rpcParticipant, eventBus, problemView, token.getId(),
					token.getName());
			problemPresenters.put(token.getId(), problemPresenter);
			cardPanel.addCard(token.toString(), problemView.asWidget());
			problemPresenter.go(cardPanel);
		}
	}
	
	/**
	 * @param dataObject
	 * @param problemId
	 * @param type
	 */
	private void updateObject(DataObject dataObject) {
	
		String objectId = dataObject.getId();
		//String type = dataObject.getData().getType();
		
		MatchResult m = problemObjId.exec(objectId);
		final String problemId;
		if (m == null)
			return;
			
		problemId = m.getGroup(1);
		
		ProblemPresenter presenter = problemPresenters.get(problemId);
		FormDataProvider provider = dataObject.getFormDataProvider();
		
		String tabName = dataObject.getData()
				.getFieldValue("Name").getSimple();
		if (tabName != null && !tabName.trim().equals(""))
			topLevelView.updateTabName(objectId, dataObject.getData()
					.getFieldValue("Name").getSimple());
		
		presenter.setObjectId(objectId);
		presenter.setDataProvider(provider);
		
		clearProgramDataProvider(presenter, "Program");
		presenter.setSolutions(dataObject.getData()
				.getFieldValue("Program"), objectId);
		provider.refresh();
	}

}