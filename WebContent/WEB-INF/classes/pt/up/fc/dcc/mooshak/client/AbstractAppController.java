package pt.up.fc.dcc.mooshak.client;

import static com.google.gwt.dom.client.Style.Cursor.DEFAULT;
import static com.google.gwt.dom.client.Style.Cursor.WAIT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Common type to all application controllers following the MVP pattern.
 * If provides a set of common features, such as
 * <ul>
 * 	<li> logger initialization</li>
 *  <li> error handling methods </li>
 * 	<li> cursor handling </li>
 * </ul>
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class AbstractAppController {
	
	protected static Logger LOGGER = Logger.getLogger("");
	
	/**
	 * Utility method for logging on the JavaScript console
	 * (useful for debugging)
	 * @param message to log on JS console
	 */
	public static native void log(String message) /*-{
		$wnd.console.log(message);
	}-*/;
	
	
	/**
	 * Forces application to start from top level
	 * loosing all state information
	 */
	protected void redirectToToplevel() {
		String href = Window.Location.getHref();
		int pos = href.indexOf('#');
	
		if(pos > -1)
			href = href.substring(0, pos);

		Window.Location.replace(href);
	}

	
	protected void throwing(String context,Throwable caught) {
		LOGGER.log(Level.SEVERE,context+":"+caught.getMessage());
	}
	
	protected static void showWaitCursor() {
		RootPanel.getBodyElement().getStyle().setCursor(WAIT); 
	}
	 
	protected static void showDefaultCursor() {

		RootPanel.getBodyElement().getStyle().setCursor(DEFAULT); 
	}
	
	
	/**
	 * change all cursors to wait and return a map of relevant styles 
	 * with their previous cursors. This map is intended to use with 
	 * {@code resetCursors()}
	 * @return map of cursors to reset
	 */
	public static Map<Style,Cursor> setCursorsToWait() {
		Map<Style,Cursor> cursors = new HashMap<Style,Cursor>();		
		RootPanel root = RootPanel.get();
		
		setCursorsToWait(cursors,root);
		
		return cursors;
	}
	
	/**
	 * Reset cursors to their previous values, using the map provided by 
	 * setCursorToWait()
	 * @param cursors
	 */
	public static void resetCursors(Map<Style,Cursor> cursors) {
		
		for(Style style: cursors.keySet())
			style.setCursor(cursors.get(style));
	}
	
	private static void setCursorsToWait(Map<Style,Cursor> cursors,Panel panel){
		Style style = panel.getElement().getStyle();
		Cursor cursor = getSafeCursor(style);
		Iterator<Widget> iterator = panel.iterator();
		
		style.setCursor(Cursor.WAIT);
		cursors.put(style, cursor);
		while(iterator.hasNext()) {
			Widget widget = iterator.next();
			
			if(widget instanceof Panel) {
				setCursorsToWait(cursors,(Panel) widget);
			}
		}
	}
	
	
	
	/**
	 * Get a cursor enumerated value using its name. If undefined return default
	 * @param style
	 * @return
	 */
	private static Cursor getSafeCursor(Style style) {
		String cursorName = style.getCursor().toUpperCase();
		Cursor prefered = null;
		
		if(cursorName != null) 
			try {
				prefered = Cursor.valueOf(Cursor.class,cursorName);	
			} catch(IllegalArgumentException cause) {
				// ignore undefined cursor names
			}
		
		return prefered == null ? Cursor.DEFAULT : prefered;
		
	}
	
}
