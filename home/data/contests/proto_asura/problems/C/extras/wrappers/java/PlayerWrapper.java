package wrappers.java;

import com.google.gson.GsonBuilder;

import java.util.Scanner;

/**
 * Wrapper for players in Java
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public abstract class PlayerWrapper {

    private static Scanner in = new Scanner(System.in);

    private String playerId;
    private PlayerAction action = new PlayerAction();

    private Json json = Json.get();

    /********************************************************************************************
     *                         Methods implemented by concrete players                          *
     ********************************************************************************************/

    /**
     * Get the name of this player from the concrete player
     *
     * @return {@code String} the name of this player
     */
    public abstract String getName();

    /**
     * Provide the concrete player with an opportunity to perform
     * initializations
     */
    public abstract void init();

    /**
     * Invoked on the concrete player to ask for his action
     */
    public abstract void execute();

    /********************************************************************************************
     *                         Methods implemented by abstract players                          *
     ********************************************************************************************/

    /**
     * Update state on abstract players
     *
     * @param {@link StateUpdate} the state update
     */
    public abstract void update(StateUpdate update);

    /**
     * Invoked on the abstract player to manage player lifecycle
     */
    protected abstract void run();

    /********************************************************************************************
     *                                     General Helpers                                      *
     ********************************************************************************************/

    /**
     * Set the player id
     *
     * @param playerId ID of the player
     */
    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    /**
     * Get the player id
     *
     * @return ID of the player
     */
    public String getPlayerId() {
        return playerId;
    }

    /**
     * Read a line of input and update the the state of the player with a {@link StateUpdate}.
     * It expects that the line contains the JSON string representation of a
     * {@link StateUpdate}.
     */
    public void readAndUpdate() {
        String line = in.nextLine();
        StateUpdate stateUpdate = json.objectFromString(line, StateUpdate.class);
        update(stateUpdate);
    }

    /**
     * Send the current action (command and log messages) to the {@code GameManager}
     */
    public void sendAction() {

        PlayerAction action = this.action;

        this.action = new PlayerAction();

        String actionString = json.objectToString(action);
        System.out.println(actionString);
    }

    /**
     * Do an action in the game with given name and arguments
     *
     * @param name Name of the command
     * @param args Arguments of the command
     */
    public void doAction(String name, Object...args) {
        action.setCommand(new Command(name, args));
    }

    /**
     * Log debugging message
     *
     * @param message debugging message
     */
    public void log(String message) {
        action.log(message);
    }

    /**
     * Send the name to the {@code GameManager}
     */
    private void sendName() {
        doAction("NAME", getName());
        sendAction();
    }

    /********************************************************************************************
     *                                      Static helpers                                      *
     ********************************************************************************************/

    public final static void main(String[] args) {

        if (args.length == 2) {
            String playerClassName = args[0];
            String playerId = args[1];

            PlayerWrapper player = null;
            try {
                player = initializePlayer(playerClassName);
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                System.err.println("Error initializing player class " + playerClassName);
                System.exit(9);
            }

            player.setPlayerId(playerId);

            try {
                player.init();

                player.sendName();

                player.run();
            } catch (Exception e) {
                System.err.println("Error while running player: " + e.getMessage());
                System.exit(9);
            }
        } else {
            System.err.println("usage: {player.qualified.ClassName} {player-id}");
        }
    }

    /**
     * Instantiates a {@link PlayerWrapper} from a given binary name
     *
     * @param playerClassName class name that implements {@link PlayerWrapper}
     * @return instance of playerClassName
     * @throws InstantiationException - Failed to get an instance of the player
     * @throws IllegalAccessException - Illegal access to class instance
     * @throws ClassNotFoundException - Player class was not found
     */
    private static PlayerWrapper initializePlayer(String playerClassName)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        Class<?> clazz = loader.loadClass(playerClassName);
        Object object = clazz.newInstance();

        if (object instanceof PlayerWrapper)
            return (PlayerWrapper) object;
        else
            throw new IllegalArgumentException("Not a player class:" + playerClassName);
    }
}
