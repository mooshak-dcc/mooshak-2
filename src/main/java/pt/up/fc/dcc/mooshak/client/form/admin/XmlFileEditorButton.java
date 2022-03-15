package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.FileDownloader;
import pt.up.fc.dcc.mooshak.client.widgets.DropFileSupportHandler;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.client.widgets.WindowBox;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

public class XmlFileEditorButton extends DropFileSupportHandler
		implements MooshakWidget, HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue> {

	private static XmlFileEditorButtonUiBinder uiBinder = GWT.create(XmlFileEditorButtonUiBinder.class);

	@UiTemplate("XmlFileEditorButton.ui.xml")
	interface XmlFileEditorButtonUiBinder extends UiBinder<Widget, XmlFileEditorButton> {
	}

	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers = 
			new ArrayList<ValueChangeHandler<MooshakValue>>();

	private String field = null;

	AdminCommandServiceAsync rpc;

	@UiField(provided = true)
	WindowBox popup;

	@UiField
	CustomTextBox filename;

	@UiField(provided=true)
	CustomXmlEditor editor;

	@UiField
	FileUpload fileUpload;

	@UiField
	Button ok;

	@UiField
	Button download;

	@UiField
	Button cancel;

	@UiField
	Button remove;

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

	private MooshakValue lastValue;

	@SuppressWarnings("deprecation")
	XmlFileEditorButton(AdminCommandServiceAsync rpc, final String docSpec) {
		
		popup = new WindowBox(false, false, false, true, true, true) {
			@Override
			public void hide() {
				if (isEditing()) {
					Logger.getLogger("").severe("editing");
					editorContent = editor.getValue().getContent();
					filenameValue = filename.getValue().getSimple();
					MooshakValue mValue = new MooshakValue(field, filenameValue, editorContent);
					for (ValueChangeHandler<MooshakValue> handler : valueChangeHandlers) {
						handler.onValueChange(new ValueChangeEvent<MooshakValue>(mValue) {
						});
					}
					lastValue = mValue;
				}
				super.hide();
			}
		};
		
		editor = new CustomXmlEditor();
		editor.setDocSpec(docSpec);

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
					editor.setDocSpec(docSpec);
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

		popup.hide();
		container.remove(popup);

		linkFieldsToHandler();
	}

	private void linkFieldsToHandler() {
		filename.addValueChangeHandler(this);
		editor.addValueChangeHandler(this);
	}

	@Override
	public MooshakValue getValue() {
		return new MooshakValue(field, filename.getValue().getSimple(), editor.getValue().getContent());
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
		
		field = value.getField();
		
		if (value.getName() == null)
			value = new MooshakValue(field, field + ".xml", value.getContent());

		this.value = value;
		this.lastValue = value;

		openPopup.setText(value.getName());
		filename.setValue(new MooshakValue(field, value.getName()));
		editor.setValue(new MooshakValue(field, value.getName(), value.getContent()));
		
		this.filenameValue = value.getName();
		this.editorContent = value.getContent();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<MooshakValue> handler) {

		valueChangeHandlers.add(handler);

		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				valueChangeHandlers.remove(handler);
			}
		};
	}

	@Override
	public boolean isEditing() {
		return isEditing || filename.isEditing() || editor.isEditing();
	}

	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		MooshakValue value = event.getValue();
		if (value.isSimpleValue()) {
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
		/*ValueChangeEvent<MooshakValue> event = new ValueChangeEvent<MooshakValue>(
				) {
		};

		for (ValueChangeHandler<MooshakValue> handler : valueChangeHandlers) {
			handler.onValueChange(event);
		}*/

		filename.setValue(new MooshakValue(field, fileName));
		editor.setValue(new MooshakValue(field, fileName, content));
		value = new MooshakValue(field, fileName, content);
		this.filenameValue = fileName;
		this.editorContent = content;
	}

	/**
	 * Gets the file extension
	 * 
	 * @param file
	 * @return file extension
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

	@UiHandler({ "download" })
	void download(ClickEvent event) {
		FileDownloader.downloadFile(filename.getValue().getSimple(), editor.getXML());
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
}
