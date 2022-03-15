package pt.up.fc.dcc.mooshak.client.guis.authentication;


import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.HasSelectedValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

public class AuthenticationPresenterTest extends GWTTestCase {

	AuthenticationViewMock display;
	BasicCommandServiceAsync rpcService;
	AuthenticationPresenter presenter;
	
	@Override
	public void gwtSetUp() {
		display = new AuthenticationViewMock();
		rpcService = GWT.create(BasicCommandService.class);
		presenter = new AuthenticationPresenter(display,rpcService);
		
	}

public void test() {
		
		HasSelectedValue<SelectableOption> domains = display.getDomainHolder();

		String href = Window.Location.getHref();
		System.out.println("href:"+href);
		
		assertNull(domains.getSelectedOption());
		
		
		display.getUserHolder().setValue("team");
		display.getUserHolder().setValue("team");
		
		display.pressLogin();

		delayTestFinish(20 * 1000);
		
		new Timer() {
	
			@Override
			public void run() {

				String message = display.getMessageHolder().getValue();

				System.out.println("Message:"+message);
				assertNotNull(message);
				
				String href = Window.Location.getHref();
				System.out.println("href:"+href);
				
				finishTest();
			}
		}.schedule(10 * 1000);

		
	}
	
	
	@Override
	public String getModuleName() {
		return "pt.up.fc.dcc.mooshak.AuthenticationModule";
	}
	
}
