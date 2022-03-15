package pt.up.fc.dcc.mooshak.client.guis.admin.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handle {@link ShowObjectEvent}.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface ShowObjectEventHandler extends EventHandler {

	void onShowObject(ShowObjectEvent showObjectEvent);
}
