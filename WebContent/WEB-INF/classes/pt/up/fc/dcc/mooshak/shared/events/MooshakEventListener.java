package pt.up.fc.dcc.mooshak.shared.events;

/**
 * Interface implemented by a class subscribing Mooshak events.
 * This interface is parameterized by an event type that extends
 * <code>MooshakEvent</code> 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 * @param <T> a type extending MooshakEvent
 */
public interface MooshakEventListener<T extends MooshakEvent> {

	void receiveEvent(T event);

}
