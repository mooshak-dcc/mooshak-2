package wrappers.java;

/**
 * Updates sent by the {@link pt.up.fc.dcc.asura.builder.base.GameManager}
 * to inform the player about an update to the game state.
 *
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class StateUpdate {

    private String type;
    private Object object;

    public StateUpdate(String type, Object object) {
        this.type = type;
        this.object = object;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "StateUpdate [type=" + type + ", object=" + object + "]";
    }
}
