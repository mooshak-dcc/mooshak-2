/**
 * 
 */
package pt.up.fc.dcc.mooshak.client.guis.judge;

import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.judge.AppController;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandService;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandService;
import pt.up.fc.dcc.mooshak.client.services.JudgeCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandService;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class JudgeEntryPoint implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		AuthenticationPresenter.isSessionAlive();

		AdminCommandServiceAsync adminService = GWT
				.create(AdminCommandService.class);
		DataManager.setRpc(adminService);

		BasicCommandServiceAsync rpcBasic = GWT
				.create(BasicCommandService.class);
		ParticipantCommandServiceAsync rpcParticipant = GWT
				.create(ParticipantCommandService.class);
		JudgeCommandServiceAsync rpcService = GWT
				.create(JudgeCommandService.class);
		HandlerManager eventBus = new HandlerManager(null);
		AppController controller = new AppController(rpcBasic, rpcParticipant,
				rpcService, eventBus);
		controller.go(RootPanel.get());
	}

}
