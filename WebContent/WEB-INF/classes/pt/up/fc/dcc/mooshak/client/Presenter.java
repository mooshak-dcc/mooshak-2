package pt.up.fc.dcc.mooshak.client;

import com.google.gwt.user.client.ui.HasWidgets;

/**
 * Common interface to all presenters used in clients.
 * Presenters are participants of the MVP architectural pattern
 * 
 * @author José paulo Leal <zp@dcc.fc.up.pt>
 */
public abstract interface Presenter {
  public abstract void go(final HasWidgets container);
}