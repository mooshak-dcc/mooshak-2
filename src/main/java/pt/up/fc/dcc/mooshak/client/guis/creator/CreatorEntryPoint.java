package pt.up.fc.dcc.mooshak.client.guis.creator;

import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandService;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.CreatorCommandService;
import pt.up.fc.dcc.mooshak.client.services.CreatorCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandService;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandService;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

public class CreatorEntryPoint implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		AuthenticationPresenter.isSessionAlive();
		
		AdminCommandServiceAsync adminService = GWT
				.create(AdminCommandService.class);
		DataManager.setRpc(adminService);

		CreatorCommandServiceAsync rpcService = GWT
				.create(CreatorCommandService.class);
		BasicCommandServiceAsync rpcBasic = GWT
				.create(BasicCommandService.class);
		ParticipantCommandServiceAsync rpcParticipant = GWT
				.create(ParticipantCommandService.class);
		KoraCommandServiceAsync rpcKora = GWT
				.create(KoraCommandService.class);

		HandlerManager eventBus = new HandlerManager(null);
		AppController controller = new AppController(adminService, rpcBasic,
				rpcService, rpcParticipant, rpcKora, eventBus);
		controller.go(RootPanel.get());
	}

}
