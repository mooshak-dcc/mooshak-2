package pt.up.fc.dcc.mooshak.client.guis.authentication;

import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter.Display;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationView.BaseStyle;
import pt.up.fc.dcc.mooshak.client.utils.BasicHasClickHandlers;
import pt.up.fc.dcc.mooshak.client.utils.BasicHasSelectedValue;
import pt.up.fc.dcc.mooshak.client.utils.BasicHasValue;
import pt.up.fc.dcc.mooshak.client.utils.HasSelectedValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Mock class for testing AutenticationPresenter without its View
 *  
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class AuthenticationViewMock implements Display {
	
	// these classes are defined in the ...client.util package
	
	BasicHasSelectedValue domainHolder = new BasicHasSelectedValue();
	BasicHasSelectedValue clientHolder = new BasicHasSelectedValue();
	
	BasicHasValue<String> userHolder = new BasicHasValue<String>();
	BasicHasValue<String> passwordHolder = new BasicHasValue<String>();
	BasicHasValue<String> messageHolder  = new BasicHasValue<String>();
	
	BasicHasClickHandlers login = new BasicHasClickHandlers();
	BasicHasClickHandlers clear = new BasicHasClickHandlers();
	BasicHasClickHandlers guest = new BasicHasClickHandlers();
	BasicHasClickHandlers register = new BasicHasClickHandlers();
	
	@Override
	public HasSelectedValue<SelectableOption> getDomainHolder() {
		return domainHolder;
	}

	@Override
	public HasSelectedValue<SelectableOption> getClientHolder() {
		return clientHolder;
	}

	@Override
	public HasValue<String> getUserHolder() {
		return userHolder;
	}

	@Override
	public HasValue<String> getPasswordHolder() {
		return passwordHolder;
	}

	@Override
	public HasValue<String> getMessageHolder() {
		return messageHolder;
	}

	@Override
	public HasClickHandlers getLogin() {
		return login;
	}

	@Override
	public HasClickHandlers getClear() {
		return clear;
	}

	@Override
	public HasClickHandlers getGuest() {
		return guest;
	}

	@Override
	public HasClickHandlers getRegister() {
		return register;
	}
	
	/**
	 * Simulate pressing the submit button
	 */
	public void pressLogin() {
		login.press();
	}

	
	/**
	 * Simulate pressing the register button
	 */
	public void pressRegister() {
		register.press();
	}

	@Override
	public BaseStyle getStyle() {
		return null;
	}

	@Override
	public HasText getVersionHolder() {
		return null;
	}
}
