package wrappers.java;

import java.util.Arrays;

/**
 * Commands sent to/from players to execute actions in the game
 *
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class Command {

    private String name;
    private Object[] args;

    public Command(String name, Object... args) {
        this.name = name;
        this.args = args;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the args
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @param args the args to set
     */
    public void setArgs(Object[] args) {
        this.args = args;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Command [name=" + name + ", args=" + Arrays.toString(args) + "]";
    }
}
