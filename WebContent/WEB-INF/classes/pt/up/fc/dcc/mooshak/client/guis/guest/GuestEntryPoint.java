package pt.up.fc.dcc.mooshak.client.guis.guest;

import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GuestEntryPoint implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		final BasicCommandServiceAsync rpcBasic = GWT
				.create(BasicCommandService.class);

		rpcBasic.initSession(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not initialize session");
			}

			@Override
			public void onSuccess(Void result) {
				HandlerManager eventBus = new HandlerManager(null);
				AppController controller = new AppController(
						rpcBasic,
						eventBus);
				controller.go(RootPanel.get());
			}
		});

	}

}
