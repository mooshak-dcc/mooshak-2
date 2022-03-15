package wrappers.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Action of the player including debugging messages
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class PlayerAction {
    private Command command = null;
    private List<String> messages = new ArrayList<>();

    public PlayerAction() {}

    public void setCommand(Command command) {
        this.command = command;
    }

    public void log(String message) {
        messages.add(message);
    }

    public Command getCommand() {
        return command;
    }

    public List<String> getMessages() {
        return messages;
    }

}
