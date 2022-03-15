package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Content of dialog box for uploading TSV file with team data
 * 
 * @author josepaiva
 */
public class ImportTeamUploadFormContent extends Composite implements
		DialogContent {

	private static ImportTeamUploadFormContentUiBinder uiBinder = GWT
			.create(ImportTeamUploadFormContentUiBinder.class);

	@UiTemplate("ImportTeamUploadFormContent.ui.xml")
	interface ImportTeamUploadFormContentUiBinder extends
			UiBinder<Widget, ImportTeamUploadFormContent> {
	}
	
	@UiField
	FileUpload fileUpload;
	
	@UiField
	CheckBox clear;
	
	private String content;
	private String name;

	public ImportTeamUploadFormContent() {

		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public MethodContext getContext() {
		MethodContext context = new MethodContext();

		context.addPair("name", name);
		context.addPair("content", content);
		context.addPair("clear", clear.getValue().toString());

		return context;
	}

	@Override
	public void setContext(MethodContext context) {

	}

	@Override
	public String getWidth() {
		return "700px";
	}

	@Override
	public String getHeight() {
		return "600px";
	}
	
	/** Drag and drop **/

	public void setContent(String content) {
		this.content = content;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * List of objects created for this class. Used for mapping static methods
	 * to them
	 */
	private static List<ImportTeamUploadFormContent> pool = 
			new ArrayList<ImportTeamUploadFormContent>();

	/**
	 * Receive load notification to set JS event handlers
	 */
	protected void onLoad() {
		int id;

		synchronized (pool) {
			pool.add(this);
			id = pool.size() - 1;
		}

		newFileContentJS(id, fileUpload.getElement(), 
				RootPanel.getBodyElement());
	}

	/**
	 * Drop Handler, load file data to editor
	 * 
	 * @param obj
	 */
	private native void dropHandlerSupport(JavaScriptObject obj, int id) /*-{
	    $wnd.setName =  $entry(
	    @pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.ImportTeamUploadFormContent::setName(ILjava/lang/String;));
		$wnd.setContent =  $entry(
	    @pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.ImportTeamUploadFormContent::setContent(ILjava/lang/String;));

		var reader = new $wnd.FileReader();
		
		reader.onload = function(e) {
			$wnd.setContent(id, reader.result);
		}	
		var file = obj.files[0];
			
		reader.readAsText(file);
		$wnd.setName(id,file.name);
		
	  }-*/;

	/**
	 * Instantiate FileContent JS counterpart
	 * 
	 * @param fileElement
	 * @param areaElement
	 */
	private static native void newFileContentJS(int id,
			JavaScriptObject fileElement, 
			JavaScriptObject bodyElement) /*-{

		new $wnd.FileContent(id, fileElement, bodyElement);
	}-*/;

	public static void setName(int id, String name) {
		pool.get(id).setName(name);
	}

	public static void setContent(int id, String content) {
		pool.get(id).setContent(content);
	}

	/**
	 * Define the JS counterpart of the class
	 */
	public static native void defineFileContentJS() /*-{
		
		$wnd.setName =  $entry(
	    @pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.ImportTeamUploadFormContent::setName(ILjava/lang/String;));
		$wnd.setContent =  $entry(
	    @pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.ImportTeamUploadFormContent::setContent(ILjava/lang/String;));
		
		$wnd.FileContent = function(id,fileElement,bodyElement) {
			this.id = id;
			this.fileElement = fileElement;
			this.bodyElement = bodyElement;
			
			this.reader = new $wnd.FileReader();
			this.reader.onload = function() {
				$wnd.setContent(id,this.reader.result);
			}.bind(this);
			
			this.fileElement.addEventListener('change',
				this.fileChangeHandler.bind(this),
				false);
		};		

		// Handle file change: read file as text
		$wnd.FileContent.prototype.fileChangeHandler = function(event) {
			this.reader.readAsText(event.target.files[0]);
			$wnd.setName(this.id,event.target.files[0].name);
		};
		
		// Handle drop events: read file as text after stopping events
		$wnd.FileContent.prototype.dropHandler = function(event) {
			event.stopPropagation();
			event.preventDefault();
			
			var file = event.dataTransfer.files[0];
			
			this.reader.readAsText(file);
			$wnd.setName(this.id,file.name);
		};
		
	}-*/;

	static {
		defineFileContentJS();
	}

}
