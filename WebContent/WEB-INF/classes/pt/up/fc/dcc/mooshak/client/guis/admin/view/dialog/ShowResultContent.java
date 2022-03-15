package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content of dialog box for operation results display
 * 
 * @author josepaiva
 */
public class ShowResultContent extends Composite 
	implements DialogContent {

	private static ShowResultContentUiBinder uiBinder = 
			GWT.create(ShowResultContentUiBinder.class);
	
	@UiTemplate("MessageContent.ui.xml")
	interface ShowResultContentUiBinder extends UiBinder<Widget, ShowResultContent> {}
	
	@UiField
	Label message;

	public ShowResultContent() {
		initWidget(uiBinder.createAndBindUi(this));
		Style style = message.getElement().getStyle();
		style.setWidth(100, Unit.PCT);
		style.setHeight(500, Unit.PX);
		style.setOverflowY(Overflow.AUTO);
		style.setWhiteSpace(WhiteSpace.PRE_WRAP);
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
		String message = "";
		for (String result : context.getValues("result")) {
			message += result + "\n\n";
		}

		setMessage(message);
	}

	@Override
	public String getWidth() {
		return "500px";
	}

	@Override
	public String getHeight() {
		return "550px";
	}

}
