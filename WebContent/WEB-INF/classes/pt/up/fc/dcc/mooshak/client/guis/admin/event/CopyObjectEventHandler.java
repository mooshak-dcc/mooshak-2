package pt.up.fc.dcc.mooshak.client.guis.admin.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handle {@link CopyObjectEvent}
 * 
 * @author josepaiva
 */
public interface CopyObjectEventHandler extends EventHandler {

	void onCopyObject(CopyObjectEvent copyObjectEvent);
}
