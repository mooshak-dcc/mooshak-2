package pt.up.fc.dcc.mooshak.client.guis.admin.view;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.ObjectEditorView.Presenter;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ImportDialog extends Composite {

	private static ImportDialogUiBinder uiBinder = 
			GWT.create(ImportDialogUiBinder.class);
		
	@UiTemplate("ImportDialog.ui.xml")
	interface ImportDialogUiBinder 
			extends UiBinder<Widget, ImportDialog> {}
	
	@UiField
	DialogBox dialogBox;
	
	@UiField
	Label typeLabel;
	
	@UiField
	FileUpload fileUpload;
	
	@UiField
	Button okButton;
	
	@UiField
	Button cancelButton;
	
	private byte[] fileContent;
	private String fileName;
	
	private Runnable action = new Runnable() { public void run() {} };
	
	/**
	 * List of objects created for this class. Used for mapping static methods
	 * to them
	 */
	private static List<ImportDialog> pool = new ArrayList<ImportDialog>();
	
	ImportDialog() {

		initWidget(uiBinder.createAndBindUi(this));
		
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				action.run();
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
		
		final int id;

		synchronized (pool) {
			pool.add(this);
			id = pool.size() - 1;
		}

		fileUpload.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				readFileToByteString(event.getNativeEvent().getEventTarget(), id);
			}
		});
	}
	
	public void start(
			final String type, 
			final String id,
			final Presenter presenter) {
		
		if(typeLabel != null && type != null)
			typeLabel.setText(type.toLowerCase());

		action = new Runnable() {
			@Override
			public void run() {
				presenter.onImport(id, fileName, fileContent);
				dialogBox.hide();
			}
		};
		
		dialogBox.center();
	}

	/**
	 * Read file to byte string
	 * 
	 * @param obj
	 */
	private static native void readFileToByteString(JavaScriptObject obj, int id) /*-{
	    $wnd.onFileChanged =  $entry(
	    @pt.up.fc.dcc.mooshak.client.guis.admin.view.ImportDialog::onFileChanged(ILjava/lang/String;Ljava/lang/String;));
        
        var reader = new $wnd.FileReader();
		reader.onloadend = function () {
            var b64 = $wnd.base64ArrayBuffer(this.result);
            $wnd.FileChanged(b64);
        };

		var file = obj.files[0];
        reader.readAsArrayBuffer(file);
		
		$wnd.FileChanged = function(result) {
			$wnd.onFileChanged(id, result, file.name);
		}	
		
		$wnd.base64ArrayBuffer = function(arrayBuffer) {
		  var base64    = ''
		  var encodings = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'
		 
		  var bytes         = new Uint8Array(arrayBuffer)
		  var byteLength    = bytes.byteLength
		  var byteRemainder = byteLength % 3
		  var mainLength    = byteLength - byteRemainder
		 
		  var a, b, c, d
		  var chunk
		 
		  for (var i = 0; i < mainLength; i = i + 3) {
		    chunk = (bytes[i] << 16) | (bytes[i + 1] << 8) | bytes[i + 2]
		 
		    a = (chunk & 16515072) >> 18
		    b = (chunk & 258048)   >> 12
		    c = (chunk & 4032)     >>  6 
		    d = chunk & 63         
		 
		    base64 += encodings[a] + encodings[b] + encodings[c] + encodings[d]
		  }
		 
		  if (byteRemainder == 1) {
		    chunk = bytes[mainLength]
		 
		    a = (chunk & 252) >> 2
		    b = (chunk & 3)   << 4
		    base64 += encodings[a] + encodings[b] + '=='
		  } else if (byteRemainder == 2) {
		    chunk = (bytes[mainLength] << 8) | bytes[mainLength + 1]
		 
		    a = (chunk & 64512) >> 10 
		    b = (chunk & 1008)  >>  4 
		    c = (chunk & 15)    <<  2 
		    base64 += encodings[a] + encodings[b] + encodings[c] + '='
		  }
		  
		  return base64
		}
		
	  }-*/;

	public static void onFileChanged(int id, String content, 
			String name) {
		pool.get(id).onFileChanged(content, name);
	}

	public static void setDisableButton(int id) {
		pool.get(id).setDisableButton();
	}

	private void setDisableButton() {
		okButton.setEnabled(false);
	}

	public void onFileChanged(String content, String name) {
		this.fileContent = Base64Coder.decode(content);
		this.fileName = name;
		okButton.setEnabled(true);
	}
}
