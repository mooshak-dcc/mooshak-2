package pt.up.fc.dcc.mooshak.client.guis.kora;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandService;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandService;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author josepaiva
 */
public class KoraEntryPoint implements EntryPoint {


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		AuthenticationPresenter.isSessionAlive();
		
		KoraCommandServiceAsync rpcKora = GWT.create(KoraCommandService.class);
		ParticipantCommandServiceAsync rpcParticipant = GWT.create(ParticipantCommandService.class);
		BasicCommandServiceAsync rpcBasic = GWT.create(BasicCommandService.class);
		HandlerManager eventBus = new HandlerManager(null);
	    AppController controller = new AppController(rpcKora, rpcParticipant, rpcBasic, eventBus);
	    controller.go(RootPanel.get());
	}
		
		
}