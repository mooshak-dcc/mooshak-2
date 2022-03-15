package pt.up.fc.dcc.mooshak.client.guis.enki.event;

import com.google.gwt.event.shared.EventHandler;

public interface ResourceOnSuccessEventHandler extends EventHandler {

	void onResourceSolvedSuccessfully(ResourceOnSuccessEvent event);
}