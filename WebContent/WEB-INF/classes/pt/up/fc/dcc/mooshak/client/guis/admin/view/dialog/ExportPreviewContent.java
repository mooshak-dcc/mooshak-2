package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.FileDownloader;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content of dialog box for exporting TSV file
 * 
 * @author josepaiva
 */
public class ExportPreviewContent extends Composite 
	implements DialogContent {
	
	private static ExportPreviewContentUiBinder uiBinder = 
			GWT.create(ExportPreviewContentUiBinder.class);
	
	@UiTemplate("ExportPreviewContent.ui.xml")
	interface ExportPreviewContentUiBinder extends UiBinder<Widget, ExportPreviewContent> {}
	
	@UiField
	HTMLPanel pnlContent;
	
	private byte[] content;
	
	private String filename ;
	
	private String mimeType = "text/plain";

	public ExportPreviewContent() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void setMessage(String message) {
		this.pnlContent.getElement().setInnerHTML(message);
	}

	@Override
	public MethodContext getContext() {
		downloadFile();
		return new MethodContext();
	}

	@Override
	public void setContext(MethodContext context) {
		
		filename = context.getValue("filename");
		
		String type = context.getValue("mimeType");
		switch (type) {
			
			case "text/plain":
				String message = context.getValue("header") + "\n";
			
				for (String data : context.getValues("data")) {
					message += Base64Coder.decodeString(data) + "\n";
				}
		
				content = message.getBytes();
				
				break;
			case "text/csv":
				content = Base64Coder.decode(context.getValue("data"));
				break;
			case "application/xml":
			case "application/json":
				content = Base64Coder.decodeLines(context.getValue("file"));
				break;
			case "application/zip":
				content = Base64Coder.decode(context.getValue("file"));
				break;
		}
		
		setMessage("A new window will open with the content requested. "
				+ "Do you want to proceed?");
		mimeType = type;
	}

	@Override
	public String getWidth() {
		return "500px";
	}

	@Override
	public String getHeight() {
		return "150px";
	}
	
	void downloadFile() {				
		
		if(mimeType.startsWith("text"))
			FileDownloader.downloadFile(filename == null ? "file.txt" : filename,
					new String(content));
		else if (mimeType.equals("application/xml"))
			FileDownloader.downloadFile(filename == null ? "file.xml" : filename, 
					new String(content));
		else if (mimeType.equals("application/json"))
			FileDownloader.downloadFile(filename == null ? "file.json" : filename, 
					new String(content));
		else {
			if (mimeType.equals("application/zip"))
				FileDownloader.downloadBinaryFile(filename == null ? "archive.zip" : filename, 
						content, mimeType);
		}
	}

}
