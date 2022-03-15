package pt.up.fc.dcc.mooshak.client.gadgets;

/**
 * Interface that defines the factory method for Gadgets, allowing
 * multiple implementations
 * 
 * @author josepaiva
 */
public interface GadgetFactory {
	
	public enum GadgetType { 
		ASK_QUESTION,
		PROGRAM_PROBLEM_EDITOR, 
		PROGRAM_PROBLEM_OBSERVATIONS, 
		PROGRAM_ERROR_LIST, 
		PROGRAM_PROBLEM_TESTS, 
		KORA, 
		QUIZ, 
		RESOURCE_RATING,
		VIDEO, 
		STATEMENT,
		RESOURCE_TREE, 
		STATISTICS_CHART, 
		LEADERBOARD_TABLE, 
		ACHIEVEMENTS_LIST,
		RELATED_RESOURCES,
		MY_DATA,
		MY_SUBMISSIONS,
		GAME_SUBMISSION,
		GAME_VIEWER
	}
	
	public Gadget getGadget(Token token, GadgetType type, String id);
	public boolean hasGadget(Token token, GadgetType type, String id);

}
