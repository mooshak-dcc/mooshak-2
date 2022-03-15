package pt.up.fc.dcc.mooshak.client.guis.admin;


import pt.up.fc.dcc.mooshak.client.data.admin.DataManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandService;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;
/**
 * Entry point classes for Admin app
 * 
 * @author José paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class AdminEntryPoint implements EntryPoint {


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		AuthenticationPresenter.isSessionAlive();
		
		AdminCommandServiceAsync rpcService = GWT.create(AdminCommandService.class);
		DataManager.setRpc(rpcService);

		HandlerManager eventBus = new HandlerManager(null);
		AppController controller = new AppController(rpcService, eventBus);
		
		controller.go(RootPanel.get());
	}


}