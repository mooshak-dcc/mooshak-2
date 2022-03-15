package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromTrustedString;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

public class ViewCertificatesContent extends Composite implements DialogContent {
	private static final String PDF_URL_PREFIX = "certificate";

	private static ViewCertificatesContentUiBinder uiBinder = 
			GWT.create(ViewCertificatesContentUiBinder.class);
	
	@UiTemplate("ViewCertificatesContent.ui.xml")
	interface ViewCertificatesContentUiBinder extends UiBinder<Widget, ViewCertificatesContent> {}

	@UiField
	HTML pdfContent;

	public ViewCertificatesContent() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setPDFStatement(String contestId, String teamId, String personId, 
			String filename) {
		String frameStyleText = "width: 100%; height: 500px; ";
		
		String path = "/" + contestId;
		if (teamId != null)
			path += "/" + teamId;
		if (personId != null)
			path += "/" + personId;
		path += "/" + filename;

		SafeHtmlBuilder builder = new SafeHtmlBuilder().append(fromTrustedString("<iframe style=\""))
				.append(fromTrustedString(frameStyleText)).append(fromTrustedString("\" src=\""))
				.appendEscaped(PDF_URL_PREFIX)
				.append(fromTrustedString(path))
				.append(fromTrustedString("\"></iframe>"));

		pdfContent.setHTML(builder.toSafeHtml());
	}

	@Override
	public MethodContext getContext() {
		return new MethodContext();
	}

	@Override
	public void setContext(MethodContext context) {;
		String contestId = context.getValue("contestId");
		String teamId = context.getValue("teamId");
		String personId = context.getValue("personId");
		String filename = context.getValue("fileName");

		setPDFStatement(contestId, teamId, personId, filename);
	}

	@Override
	public String getWidth() {
		return "650px";
	}

	@Override
	public String getHeight() {
		return "550px";
	}

}
