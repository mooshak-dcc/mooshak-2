package pt.up.fc.dcc.mooshak.client;

import com.google.gwt.user.client.ui.Widget;

/**
 * Common interface to all views used in clients.
 * Views are participants of the MVP architectural pattern
 * 
 * @author josepaiva
 */
public abstract interface View {

	Widget asWidget();
}
