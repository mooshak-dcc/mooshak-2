package pt.up.fc.dcc.mooshak.client.gadgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.shared.HandlerManager;

import pt.up.fc.dcc.mooshak.client.services.AsuraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.QuizCommandServiceAsync;

/**
 * Implementation of factory of gadgets
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class GadgetMaker implements GadgetFactory {

	private Map<String, ArrayList<Gadget>> gadgets = new HashMap<String, ArrayList<Gadget>>();

	private HandlerManager eventBus;

	private BasicCommandServiceAsync basicService;
	private ParticipantCommandServiceAsync participantService;
	private EnkiCommandServiceAsync enkiService;
	private KoraCommandServiceAsync koraService;
	private AsuraCommandServiceAsync asuraService;
	private QuizCommandServiceAsync quizService;

	public GadgetMaker(
			HandlerManager eventBus,
			BasicCommandServiceAsync basicService,
			ParticipantCommandServiceAsync participantService,
			EnkiCommandServiceAsync enkiService,
			KoraCommandServiceAsync koraService, 
			AsuraCommandServiceAsync asuraService,
			QuizCommandServiceAsync quizService) {

		this.eventBus = eventBus;

		this.basicService = basicService;
		this.participantService = participantService;
		this.enkiService = enkiService;
		this.koraService = koraService;
		this.asuraService = asuraService;
		this.quizService = quizService;
	}

	@Override
	public Gadget getGadget(Token token, GadgetType type, String id) {

		Gadget gadget = null;

		if ((gadget = searchById(id, type)) != null)
			return gadget;

		if ((gadget = searchByToken(token, type)) != null)
			return gadget;

		switch (type) {
		case STATEMENT:
			gadget = new ViewStatement(participantService, enkiService, eventBus, token, type);
			break;
		case VIDEO:
			gadget = new VideoViewer(participantService, enkiService, eventBus, token, type);
			break;
		case PROGRAM_PROBLEM_EDITOR:
			gadget = new ProgramEditor(participantService, enkiService, token, type);
			break;
		case PROGRAM_PROBLEM_TESTS:
			gadget = new ProgramTestCases(participantService, enkiService, eventBus, token, type);
			break;
		case PROGRAM_ERROR_LIST:
			gadget = new ProgramErrorList(token, type);
			break;
		case PROGRAM_PROBLEM_OBSERVATIONS:
			gadget = new ProgramEditorObservations(eventBus, token, type);
			break;
		case KORA:
			gadget = new Kora(eventBus, basicService, participantService, enkiService, koraService, token, type);
			break;
		case QUIZ:
			gadget = new Quiz(eventBus, basicService, participantService, enkiService, quizService, token, type);
			break;
		case RESOURCE_TREE:
			gadget = new ResourcesTree(eventBus, enkiService, token, type);
			break;
		case RESOURCE_RATING:
			gadget = new ResourceRating(enkiService, eventBus, token, type);
			break;
		case STATISTICS_CHART:
			gadget = new StatsChart(enkiService, token, type);
			break;
		case LEADERBOARD_TABLE:
			gadget = LeaderboardTable.getInstance(enkiService, token, type);
			break;
		case ACHIEVEMENTS_LIST:
			gadget = AchievementList.getInstance(enkiService, token, type);
			break;
		case RELATED_RESOURCES:
			gadget = new RelatedResources(enkiService, eventBus, token, type);
			break;
		case MY_DATA:
			gadget = MyData.getInstance(enkiService, token, type);
			break;
		case ASK_QUESTION:
			gadget = new AskQuestion(participantService, basicService, token, type, 15);
			break;
		case MY_SUBMISSIONS:
			gadget = new MySubmissions(participantService, basicService, eventBus, token, type, 22);
			break;
		case GAME_SUBMISSION:
			gadget = new GameSubmission(participantService, enkiService, asuraService, token, type);
			break;
		case GAME_VIEWER:
			gadget = new GameViewer(participantService, enkiService, asuraService, token, type);
			break;
		default:
			break;
		}

		if (id != null) {
			if (gadgets.containsKey(id))
				gadgets.get(id).add(gadget);
			else
				gadgets.put(id, new ArrayList<Gadget>(Arrays.asList(gadget)));
		}

		return gadget;
	}

	/**
	 * Searches for a gadget with given id and type
	 * 
	 * @param id
	 * @return Gadget with given id
	 */
	private Gadget searchById(String id, GadgetType type) {
		if (id == null)
			return null;

		ArrayList<Gadget> result = gadgets.get(id);
		if (result != null) {
			for (Gadget g : result) {
				if (g.getType().equals(type))
					return g;
			}
		}

		return null;
	}

	/**
	 * Searches for a gadget with given id
	 * 
	 * @param id
	 * @return List of gadgets with given id
	 */
	public List<Gadget> searchById(String id) {
		if (id == null)
			return null;

		List<Gadget> result = gadgets.get(id);

		return result;
	}

	/**
	 * Searches for a gadget with given token
	 * 
	 * @param token
	 * @return Gadget with given token
	 */
	private Gadget searchByToken(Token token, GadgetType type) {
		if (token == null)
			return null;

		ArrayList<Gadget> result = gadgets.get(token.getId());
		if (result != null) {
			for (Gadget gadget : result) {
				if (gadget.getToken() == null && type.equals(gadget.getType()))
					return gadget;
				else if (gadget.getToken() != null && gadget.getToken().equals(token) && type.equals(gadget.getType()))
					return gadget;
			}
		}

		return null;
	}

	@Override
	public boolean hasGadget(Token token, GadgetType type, String id) {

		if (searchById(id, type) != null)
			return true;

		if (searchByToken(token, type) != null)
			return true;

		return false;
	}

}
