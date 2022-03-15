
/**
 * <p> 
 * Server part of the mooshak event system. Server components can send
 * events trough the <code>EventSender</code> singleton. The
 * <code>EventServiceImplementation</code> collects these events and
 * sends them to the each requesting client where they are broadcast
 * to subscribing components. Event and event listener definitions are
 * available in the <code>shared.events</code> package.
 * 
 * </p>
 * 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
package pt.up.fc.dcc.mooshak.server.events;