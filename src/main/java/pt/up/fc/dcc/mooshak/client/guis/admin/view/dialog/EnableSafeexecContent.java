package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.Widget;

public class EnableSafeexecContent extends Composite implements DialogContent {
	
	private static EnableSafeexecContentUiBinder uiBinder = 
			GWT.create(EnableSafeexecContentUiBinder.class);
	
	@UiTemplate("EnableSafeexecContent.ui.xml")
	interface EnableSafeexecContentUiBinder 
			extends UiBinder<Widget, EnableSafeexecContent> {}
	
	@UiField 
	PasswordTextBox password;

	public EnableSafeexecContent() {
		
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();
				
		context.addPair("password", password.getText());
		
		return context;
	}

	@Override
	public void setContext(MethodContext context) {}

	@Override
	public String getWidth() {
		return "500px";
	}

	@Override
	public String getHeight() {
		return "100px";
	}
	
}