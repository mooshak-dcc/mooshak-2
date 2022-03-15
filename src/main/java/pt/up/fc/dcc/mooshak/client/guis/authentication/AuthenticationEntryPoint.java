package pt.up.fc.dcc.mooshak.client.guis.authentication;

import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class AuthenticationEntryPoint implements EntryPoint,
	ValueChangeHandler<String> {

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		History.addValueChangeHandler(this);
		History.newItem("login");
		History.fireCurrentHistoryState();
		
		final BasicCommandServiceAsync rpcService = 
				GWT.create(BasicCommandService.class);

		rpcService.initSession(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				String message = "Could not initialize session";
				
				Window.alert(message+": "+caught.getMessage());
				Logger.getLogger("").log(Level.SEVERE,message,caught);
			}

			@Override
			public void onSuccess(Void result) {

			    AuthenticationView view = new AuthenticationView();
			    AuthenticationPresenter presenter = new AuthenticationPresenter(view,rpcService);
			    view.setPresenter(presenter);
			    
			    RootPanel.get().add(view);
			}
		});
		//HandlerManager eventBus = new HandlerManager(null);
	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		String historyToken = event.getValue();
		if (!historyToken.equals("login")) {
		  History.newItem("login", true);
		  History.replaceItem("login", true);
		}
	}
		
		
}
