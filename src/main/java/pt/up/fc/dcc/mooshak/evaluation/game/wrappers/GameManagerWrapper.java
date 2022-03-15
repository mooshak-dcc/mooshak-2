package pt.up.fc.dcc.mooshak.evaluation.game.wrappers;

import java.util.Map;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * This class wraps a game manager instance, providing reflection methods that
 * invoke methods of the game manager provided by the Asura Builder framework.
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class GameManagerWrapper extends GameBaseWrapper {

	public GameManagerWrapper(Object gameManagerObj) {
		super(gameManagerObj);
	}

	/**
	 * Get name of the game
	 * 
	 * @return {@link String} name of the game
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public String getGameName() throws MooshakException {

		return (String) invoke("getGameName");
	}

	/**
	 * Get minimum number of players per match
	 * 
	 * @return minimum number of players per match
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public int getMinPlayersPerMatch() throws MooshakException {
		
		int minNumberOfPlayers = (int) invoke("getMinPlayersPerMatch");
		
		if (minNumberOfPlayers < 1)
			throw new MooshakException("Invalid minimum number of players: " 
					+ minNumberOfPlayers);

		return minNumberOfPlayers;
	}

	/**
	 * Get maximum number of players per match
	 * 
	 * @return maximum number of players per match
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public int getMaxPlayersPerMatch() throws MooshakException {
		
		int maxNumberOfPlayers = (int) invoke("getMaxPlayersPerMatch");
		
		if (maxNumberOfPlayers < 1)
			throw new MooshakException("Invalid maximum number of players: " 
					+ maxNumberOfPlayers);

		return maxNumberOfPlayers;
	}

	/**
	 * Executes a game with a list of players identified by their processes
	 * 
	 * @param players
	 *            map of players' processes (for the same game) keyed by player
	 *            id
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public void manage(Map<String, Process> players) throws MooshakException {
		
		invoke("manage", new Class<?>[]{Map.class}, players);
	}

	/**
	 * Get game status of the player wrapped in a {@link GamePlayerStatusWrapper}
	 * 
	 * @param player ID of the player
	 * @return {@link GamePlayerStatusWrapper} wrapper for game status of a player
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public GamePlayerStatusWrapper getGamePlayerStatus(String player) throws MooshakException {

		return new GamePlayerStatusWrapper(invoke("getGamePlayerStatus",
				new Class<?>[]{String.class}, player));
	}

	/**
	 * Get movie of the game execution as string
	 * 
	 * @return String with game movie
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public String getGameMovieString() throws MooshakException {

		return (String) invoke("getGameMovieString");
	}

	/**
	 * Get movie of the game execution as string
	 * 
	 * @param {@link String} compression algorithm
	 * @return String with game movie
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public String getGameMovieString(String compression) throws MooshakException {

		return (String) invoke("getGameMovieString", new Class<?>[]{String.class}, compression);
	}

	/**
	 * Export game movie to a file
	 * 
	 * @param filePath
	 *            path to file
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public void exportGameMovie(String filePath) throws MooshakException {

		invoke(filePath);
	}	
}
