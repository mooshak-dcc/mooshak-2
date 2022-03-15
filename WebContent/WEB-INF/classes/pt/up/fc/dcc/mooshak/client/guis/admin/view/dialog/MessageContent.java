package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content of dialog box for message display
 * 
 * @author josepaiva
 */
public class MessageContent extends Composite implements DialogContent {
	
	private static MessageContentUiBinder uiBinder = 
			GWT.create(MessageContentUiBinder.class);
	
	@UiTemplate("MessageContent.ui.xml")
	interface MessageContentUiBinder extends UiBinder<Widget, MessageContent> {}
	
	@UiField
	Label message;

	public MessageContent() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setMessage(String message) {
		this.message.setText(message);
	}

	@Override
	public MethodContext getContext() {
		return new MethodContext();
	}

	@Override
	public void setContext(MethodContext context) {
		
	}

	@Override
	public String getWidth() {
		return "500px";
	}

	@Override
	public String getHeight() {
		return "100px";
	}

}
