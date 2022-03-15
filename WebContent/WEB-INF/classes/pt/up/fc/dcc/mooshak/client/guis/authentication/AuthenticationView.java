package pt.up.fc.dcc.mooshak.client.guis.authentication;

import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter.Display;
import pt.up.fc.dcc.mooshak.client.utils.HasSelectedValue;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AuthenticationView extends Composite implements Display {

	private static AuthenticationUiBinder uiBinder = GWT.create(AuthenticationUiBinder.class);

	@UiTemplate("Authentication.ui.xml")
	interface AuthenticationUiBinder extends UiBinder<Widget, AuthenticationView> {
	}

	private OptionFormatter<SelectableOption> formatter = new OptionFormatter<SelectableOption>() {
		public String getLabel(SelectableOption option) {
			return option.getLabel();
		};

		public String getValue(SelectableOption option) {
			return option.getId();
		};
	};

	@UiField
	SelectOneListBox<SelectableOption> domain;

	SelectOneListBox<SelectableOption> client = new SelectOneListBox<SelectableOption>(formatter);

	@UiField
	TextBox user;

	@UiField
	PasswordTextBox password;

	@UiField
	TextBox message;

	@UiField
	Button login;

	@UiField
	Button clear;

	@UiField
	Button register;

	@UiField
	Button guest;

	@UiField
	Label version;

	private AuthenticationPresenter presenter;

	@UiField
	BaseStyle style;

	interface BaseStyle extends CssResource {

		String globalMoveCursor();
	}

	public AuthenticationView() {
		initWidget(uiBinder.createAndBindUi(this));

		domain.ensureDebugId("loginDomain");
		user.ensureDebugId("loginUser");
		password.ensureDebugId("loginPassword");
		login.ensureDebugId("loginButton");
		register.ensureDebugId("registerButton");

		KeyDownHandler handler = new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				message.setText("");
			}
		};

		user.addKeyDownHandler(handler);
		password.addKeyDownHandler(handler);

		password.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
					presenter.login();
				}
			}
		});

		domain.setFormatter(formatter);

		domain.addValueChangeHandler(new ValueChangeHandler<SelectableOption>() {

			@Override
			public void onValueChange(ValueChangeEvent<SelectableOption> event) {
				register.setEnabled(presenter.getContestInfoRegister(event.getValue().getId()));
				guest.setEnabled(presenter.getContestInfoPublicScoreboard(event.getValue().getId()));
			}
		});
	}

	/**
	 * @return the presenter
	 */
	public AuthenticationPresenter getPresenter() {
		return presenter;
	}

	/**
	 * @param presenter
	 *            the presenter to set
	 */
	public void setPresenter(AuthenticationPresenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public HasSelectedValue<SelectableOption> getDomainHolder() {
		return domain;
	}

	@Override
	public HasSelectedValue<SelectableOption> getClientHolder() {
		return client;
	}

	@Override
	public HasValue<String> getUserHolder() {
		return user;
	}

	@Override
	public HasValue<String> getPasswordHolder() {
		return password;
	}

	@Override
	public HasValue<String> getMessageHolder() {
		return message;
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

	@Override
	public BaseStyle getStyle() {
		return style;
	}

	@Override
	public HasText getVersionHolder() {
		return version;
	}

}
