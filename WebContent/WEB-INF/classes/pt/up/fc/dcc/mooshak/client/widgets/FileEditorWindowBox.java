package pt.up.fc.dcc.mooshak.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorCallback;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.FileDownloader;

public abstract class FileEditorWindowBox extends WindowBox {
	public final static String DEFAULT_PLACEHOLDER = "Write the content here or drag and drop a file"
			+ " from your system";

	private static FileEditorWindowBoxUiBinder uiBinder = GWT
			.create(FileEditorWindowBoxUiBinder.class);

	@UiTemplate("FileEditorWindowBox.ui.xml")
	interface FileEditorWindowBoxUiBinder extends
			UiBinder<Widget, FileEditorWindowBox> {
	}
	
	/*@UiField
	TextBox fileName;*/
	
	@UiField
	AceEditor editor;

	@UiField
	FileUpload fileUpload;

	@UiField
	Button ok;

	@UiField
	Button cancel;

	@UiField
	Button download;

	@UiField
	ResizableHtmlPanel popupContainer;
	
	private DropFileSupportHandler dfsHandler = null;
	
	private boolean readOnly = false;

	public FileEditorWindowBox() {
		setWidget(uiBinder.createAndBindUi(this));
        setAutoHideEnabled(false);
        setGlassEnabled(false);
        setModal(true);
        setMinimizeIconVisible(false);
        setCloseIconVisible(true);
        setDraggable(true);
        setResizable(true);
        
        getElement().getStyle().setZIndex(1000);

		setMinWidth(600);
		setMinHeight(490);
		setWidth("600px");
		setHeight("490px");
		editor.setHeight("200px");
		
		addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				editor.setWidth((popupContainer.getOffsetWidth()-20)+"px");
				editor.setHeight((popupContainer.getOffsetHeight()-80)+"px");
			}
		});

		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				save();
				hide();
			}
		});

		cancel.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});

		dfsHandler = new DropFileSupportHandler() {
			
			@Override
			public void onFileDropped(String content, String name) {
				
				if (readOnly)
					return;
				byte[] result = Base64Coder.decode(content);
				editor.setValue(new String(result));
			}
		};
		
		configureAceEditor();
		
		center();
	}

	/** AceEditor methods  **/
	private void configureAceEditor() {
		editor.startEditor();
		editor.setMode(AceEditorMode.TEXT);
		editor.setTheme(AceEditorTheme.ECLIPSE);
		editor.getElement().getStyle().setHeight(418, Unit.PX);
		editor.focus();
		
		editor.addOnChangeHandler(new AceEditorCallback() {
			
			@Override
			public void invokeAceCallback(JavaScriptObject obj) {
				updatePlaceholder(getPlaceHolder());
			}
		});
		
		new Timer() {
			
			@Override
			public void run() {
				updatePlaceholder(getPlaceHolder());
			}
		}.scheduleRepeating(100);
		
	}

	/**
	 * Receive load notification to set JS event handlers
	 */
	protected void onLoad() {
		int id;

		synchronized (DropFileSupportHandler.pool) {
			DropFileSupportHandler.pool.add(dfsHandler);
			id = DropFileSupportHandler.pool.size() - 1;
		}

		DropFileSupportHandler.newFileContentJS(id, fileUpload.getElement(), null, 
				editor.getElement(), popupContainer.getElement());

	}
	
	public String getFileName() {
		return "file.txt";
	}

	private String placeHolder = DEFAULT_PLACEHOLDER;
	public String getPlaceHolder() {
		return placeHolder;
	}

	public String getEditorContent() {
		return editor.getValue();
	}
	
	public void setEditorContent(String content) {
		editor.setValue(content);
	}
	
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
		editor.setReadOnly(readOnly);
		
		if (readOnly) {
			fileUpload.getElement().getStyle().setDisplay(Display.NONE);
			placeHolder = "";
		}
		else {
			fileUpload.getElement().getStyle().setDisplay(Display.BLOCK);
			placeHolder = DEFAULT_PLACEHOLDER;
		}
	}
	
	
	@UiHandler({"download"})
	void download(ClickEvent event) {
		FileDownloader.downloadFile(getFileName(), editor.getValue());
	}
	
	
	public void updatePlaceholder(String placeHolder) {
		boolean shouldShow = editor.getValue().isEmpty();
		boolean hasNode = false;
		Element node = null;
		Element scroller = null;
		NodeList<Element> nodes = editor.getElement().getElementsByTagName("div");
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.getItem(i).hasClassName("ace_emptyMessage")) {
				hasNode = true;
				node = nodes.getItem(i);
				break;
			} else if (nodes.getItem(i).hasClassName("ace_scroller")) {
				scroller = nodes.getItem(i);
			}
		}
		
		if (!shouldShow && hasNode) {
			scroller.removeChild(node);
		} else if (shouldShow && !hasNode) {
			node = DOM.createElement("div");
			node.setInnerText(getPlaceHolder());
			node.addClassName("ace_invisible ace_emptyMessage");
			node.getStyle().setProperty("padding", "0 9px");
			node.getStyle().setProperty("zIndex", "99999");
			node.getStyle().setProperty("color", "#aaa");
			node.getStyle().setProperty("position", "absolute");
			scroller.appendChild(node);
		}
	}

	public abstract void save();
}