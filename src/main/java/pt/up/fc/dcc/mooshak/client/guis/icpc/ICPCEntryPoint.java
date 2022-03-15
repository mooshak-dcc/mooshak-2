package pt.up.fc.dcc.mooshak.client.guis.icpc;

import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandService;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ICPCEntryPoint implements EntryPoint {


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		AuthenticationPresenter.isSessionAlive();
		
		ParticipantCommandServiceAsync rpcService = GWT.create(ParticipantCommandService.class);
		BasicCommandServiceAsync rpcBasic = GWT.create(BasicCommandService.class);
		HandlerManager eventBus = new HandlerManager(null);
	    AppController controller = new AppController(rpcService, rpcBasic, eventBus);
	    controller.go(RootPanel.get());
	}
		
		
}
