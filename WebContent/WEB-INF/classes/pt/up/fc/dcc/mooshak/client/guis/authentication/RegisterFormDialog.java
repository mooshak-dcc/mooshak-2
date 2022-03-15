package pt.up.fc.dcc.mooshak.client.guis.authentication;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.DialogContent;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class RegisterFormDialog extends Composite implements DialogContent {
	
	private static RegisterFormDialogUiBinder uiBinder = 
			GWT.create(RegisterFormDialogUiBinder.class);
	
	@UiTemplate("RegisterFormDialog.ui.xml")
	interface RegisterFormDialogUiBinder 
						extends UiBinder<Widget, RegisterFormDialog> {}

	@UiField
	Label domain;
	
	@UiField
	TextBox user;
	
	@UiField
	PasswordTextBox password;
	
	@UiField
	PasswordTextBox confirmPassword;
	
	@UiField
	Image captcha;

	@UiField
	CustomImageButton reload;
	
	@UiField
	TextBox captchaResponse;
	
	public RegisterFormDialog(String domain) {
		initWidget(uiBinder.createAndBindUi(this));
	
		final String captchaUrl = AuthenticationPresenter.getUrlInApp("captcha");
		captcha.setUrl(captchaUrl);
		
		reload.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		reload.getElement().getStyle().setVerticalAlign(VerticalAlign.TOP);
		reload.getElement().getStyle().setMarginTop(15, Unit.PX);
		captcha.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		
		reload.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) { // force new request
				captcha.setUrl(captchaUrl+"?"+ System.currentTimeMillis()); 
			}
		});
		
		this.domain.setText(domain);
	}
	
	@Override
	public MethodContext getContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setContext(MethodContext context) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getWidth() {
		return "550px";
	}

	@Override
	public String getHeight() {
		return "400px";
	}
	
	public String getResponse() {
		return captchaResponse.getText();
	}
	
	public String getUser() {
		return user.getText();
	}
	
	public String getPassword() {
		return password.getText();
	}
	
	public String getConfirmPassword() {
		return confirmPassword.getText();
	}

}
