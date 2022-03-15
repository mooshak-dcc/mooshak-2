package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditorCallback;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import pt.up.fc.dcc.mooshak.client.data.admin.HasFormData;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.FileDownloader;
import pt.up.fc.dcc.mooshak.client.widgets.DropFileSupportHandler;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.client.widgets.WindowBox;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

class FileEditorButton extends DropFileSupportHandler implements MooshakWidget,
		HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue>,
		HasFormData {
	private static final String DEFAULT_MIME_TYPE = "text/plain";

	public final static String DEFAULT_PLACEHOLDER = "Write the content here or drag and drop a file"
			+ " from your system";
	
	private static final List<String> ALLOWED_MIME_PARTS = Arrays.asList("text/",
			"json", "xml", "javascript");

	private static FileEditorButtonUiBinder uiBinder = GWT
			.create(FileEditorButtonUiBinder.class);

	@UiTemplate("FileEditorButton.ui.xml")
	interface FileEditorButtonUiBinder extends
			UiBinder<Widget, FileEditorButton> {
	}

	private Map<String, MooshakWidget> fields = new HashMap<String, MooshakWidget>();
	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers =
			new ArrayList<ValueChangeHandler<MooshakValue>>();

	private String field = null;

	AdminCommandServiceAsync rpc;

	@UiField(provided = true)
	WindowBox popup;

	@UiField
	CustomTextBox filename;

	@UiField
	CustomAceEditor editor;

	@UiField
	FileUpload fileUpload;

	@UiField
	Button ok;

	@UiField
	Button download;

	@UiField
	Button remove;

	@UiField
	Button cancel;

	@UiField
	Button openPopup;

	@UiField
	ResizableHtmlPanel container;

	@UiField
	ResizableHtmlPanel popupContainer;

	@UiField
	HtmlFreeLabel content;

	MooshakValue value;
	
	private boolean isEditing = false;
	
	private byte[] editorContent = null;
	private String filenameValue = null;
	
	private boolean readOnly = false;
	private String mime;

	private MooshakValue lastValue;

	@SuppressWarnings("deprecation")
	FileEditorButton(AdminCommandServiceAsync rpc) {
		popup = new WindowBox(false, true, false, true, true, true) {
			@Override
			public void hide() {
				if (isEditing()) {
					if (editorContent == null)
						editorContent = editor.getValue().getContent();
					if (filenameValue == null)
						filenameValue = filename.getValue().getName();
					MooshakValue mValue = new MooshakValue(field, filenameValue, 
							editorContent);
					for (ValueChangeHandler<MooshakValue> handler : valueChangeHandlers) {
						handler.onValueChange(new ValueChangeEvent<MooshakValue>(mValue) {});
					}
					lastValue = mValue;
				}
				super.hide();
			}
		};

		this.rpc = rpc;
		initWidget(uiBinder.createAndBindUi(this));
		
		editor.setValue(new MooshakValue("", "", "".getBytes()));

		popup.setMinWidth(600);
		popup.setWidth(600 + "px");
		popup.setMinHeight(490);
		popup.setHeight(490 + "px");
		editor.setHeight(400);

		popup.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				editor.setWidth((popup.getOffsetWidth() - 35) + "px");
				editor.setHeight((popup.getOffsetHeight() - 125) + "px");
			}
		});

		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				isEditing = true;
				popup.hide();
				isEditing = false;
			}
		});

		openPopup.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!popup.isShowing()) {
					popup.center();

					popup.show();

				}
			}
		});
		
		cancel.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				isEditing = false;
				popup.hide();
				setValue(lastValue);
			}
		});

		popupContainer.addAndReplaceElement(editor, content.getElement());
		container.remove(editor);
		
		popup.hide();
		container.remove(popup);

		linkFieldsToHandler();

		filename.addHandler(new BlurHandler() {

			@Override
			public void onBlur(BlurEvent event) {
				changeLanguageAceEditor();
			}
		}, BlurEvent.getType());
		

		
		editor.getEditor().addOnChangeHandler(new AceEditorCallback() {
			
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

	private void linkFieldsToHandler() {
		filename.addValueChangeHandler(this);
		editor.addValueChangeHandler(this);
	}

	@Override
	public MooshakValue getValue() {
		return new MooshakValue(field, filename.getValue().getSimple(),
				readOnly ? value.getContent() : editor.getValue().getContent());
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value, false);
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		
		if (fireEvents) {
			ValueChangeEvent<MooshakValue> event = new ValueChangeEvent<MooshakValue>(
					value) {
			};

			for (ValueChangeHandler<MooshakValue> handler : valueChangeHandlers) {
				handler.onValueChange(event);
			}
		}
		
		this.value = value;
		this.lastValue = value;
		field = value.getField();
		
		if (value.getContent() != null)
			editor.setValue(value);
		
		filename.setValue(new MooshakValue(field, value.getName()));

		changeLanguageAceEditor();
	
		if (value.getName() != null)
			openPopup.setText(value.getName());
		else
			openPopup.setText("Edit ...");
			
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<MooshakValue> handler) {

		valueChangeHandlers.add(handler);

		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				valueChangeHandlers.remove(handler);
			}
		};
	}

	/**
	 * Populate to form's field values with a map indexed by field names
	 * 
	 * @param data
	 *            a map with the field values indexed by field names
	 */
	@Override
	public void setFieldValues(Map<String, MooshakValue> data) {

		for (String fieldName : data.keySet()) {
			if (fields.containsKey(fieldName))
				if (!isEditing() && fieldName.equals(field))
					fields.get(fieldName).setValue(data.get(fieldName));
		}
	}

	/**
	 * Get field values as map indexed by field names
	 * 
	 * @param data
	 */
	@Override
	public Map<String, MooshakValue> getFieldValues() {
		Map<String, MooshakValue> data = new HashMap<String, MooshakValue>();

		for (String fieldName : fields.keySet()) {
			if (fieldName.equals(field))
				data.put(fieldName, fields.get(fieldName).getValue());
		}

		return data;
	}

	@Override
	public boolean isEditing() {
		return isEditing || filename.isEditing() || editor.isEditing();
	}

	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		MooshakValue value = event.getValue();
		if (readOnly) {
			filenameValue = filename.getValue().getSimple();
			editorContent = this.value.getContent();
		} else if (value.isSimpleValue()) {
			filenameValue = filename.getValue().getSimple();
			editorContent = editor.getValue().getContent();
		} else {
			filenameValue = filename.getValue().getSimple();
			editorContent = editor.getValue().getContent();
		}
		
		this.value = new MooshakValue(field, filenameValue, editorContent);
	}

	/**
	 * Fires an upload event
	 * 
	 * @param fileName
	 * @param content
	 */
	public void fireUploadFileEvent(String fileName, byte[] content) {

		isEditing = true;
		MooshakValue value = new MooshakValue(field, fileName, content);
		filename.setValue(new MooshakValue(field, fileName), false);
		editor.setValue(value, false);
		
		this.filenameValue = fileName;
		this.editorContent = content;
		this.value = value;
		
		changeLanguageAceEditor();
	}

	/**
	 * Changes the editor language based in file name
	 */
	public void changeLanguageAceEditor() {

		String extension = getFileExtension(value.getName());

		switch (extension) {
		case "c":
		case "cpp":
			editor.setMode(AceEditorMode.C_CPP);
			break;
		case "pl":
			editor.setMode(AceEditorMode.PERL);
			break;
		case "py":
			editor.setMode(AceEditorMode.PYTHON);
			break;
		case "pas":
		case "p":
		case "pp":
		case "pascal":
			editor.setMode(AceEditorMode.PASCAL);
			break;
		case "java":
			editor.setMode(AceEditorMode.JAVA);
			break;
		case "js":
			editor.setMode(AceEditorMode.JAVASCRIPT);
			break;
		case "html":
			editor.setMode(AceEditorMode.HTML);
			break;
		case "xml":
			editor.setMode(AceEditorMode.XML);
			break;
		case "json":
			editor.setMode(AceEditorMode.JSON);
			break;
		case "sql":
			editor.setMode(AceEditorMode.SQL);
			break;

		default:
			editor.setMode(AceEditorMode.TEXT);
			break;
		}

		rpc.getMimeType(extension, new AsyncCallback<String>() {

			@Override
			public void onSuccess(String mime) {
				setMimeContentPreview(mime);
			}

			@Override
			public void onFailure(Throwable caught) {
				setMimeContentPreview(DEFAULT_MIME_TYPE);
			}
		});
	}

	/**
	 * gets the file extension
	 * 
	 * @param file
	 * @return
	 */
	public String getFileExtension(String file) {
		String extension = "";

		try {
			int i = file.lastIndexOf('.');
			if (i > 0)
				extension = file.substring(i + 1);
		} catch (Exception e) {
		}

		return extension.toLowerCase();
	}

	/**
	 * Changes the preview based on mime type
	 * 
	 * @param mime
	 */
	public void setMimeContentPreview(String mime) {
		
		this.mime = mime;
		
		if (mime == null) {
			editor.enableEditing();
			return;
		}
		
		for (String part : ALLOWED_MIME_PARTS) {
			if (mime.indexOf(part) != -1) {
				editor.enableEditing();
				readOnly = false;
				return;
			}
		}
		
		filenameValue = value.getName();
		editorContent = value.getContent();
		editor.setValue(new MooshakValue("", value.getName(),
						"Content preview not available".getBytes()));
		editor.disableEditing();
		readOnly = true;
	}

	public void onFileDropped(String content, String name) {
		byte[] result = Base64Coder.decode(content);

		fireUploadFileEvent(name, result);
	}

	/**
	 * Receive load notification to set JS event handlers
	 */
	protected void onLoad() {
		int id;

		synchronized (pool) {
			pool.add(this);
			id = pool.size() - 1;
		}

		newFileContentJS(id, fileUpload.getElement(), null, null, null);

	}
	
	
	@UiHandler({"download"})
	void download(ClickEvent event) {
		if (readOnly)
			FileDownloader.downloadBinaryFile(filename.getValue().getSimple(), value.getContent(), mime);
		else
			FileDownloader.downloadFile(filename.getValue().getSimple(), editor.getText());
		
	}
	
	@UiHandler({ "remove" })
	void remove(ClickEvent event) {
		if (value.getName() == null)
			return;
		
		new OkCancelDialog("Are you sure that you want to remove this file?") {
		}.addDialogHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setValue(new MooshakValue(field, null, null), true);
				isEditing = false;
				popup.hide();
			}
		});
	}

	private String placeHolder = DEFAULT_PLACEHOLDER;
	public String getPlaceHolder() {
		return placeHolder;
	}
	
	public void updatePlaceholder(String placeHolder) {
		boolean shouldShow = editor.getText().isEmpty();
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

}
