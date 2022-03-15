package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ImportRemoteProblemContent extends Composite implements
		DialogContent {
	
	private static ImportRemoteProblemContentUiBinder uiBinder = GWT
		.create(ImportRemoteProblemContentUiBinder.class);

	@UiTemplate("ImportRemoteProblemContent.ui.xml")
	interface ImportRemoteProblemContentUiBinder extends
			UiBinder<Widget, ImportRemoteProblemContent> {
	}
	
	@UiField
	TextBox url;
	
	public ImportRemoteProblemContent() {

		initWidget(uiBinder.createAndBindUi(this));
		url.getElement().getStyle().setWidth(100, Unit.PCT);
	}

	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();

		context.addPair("url", url.getValue());

		return context;
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
		return "150px";
	}

}
