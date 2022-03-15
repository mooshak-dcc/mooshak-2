package pt.up.fc.dcc.mooshak.client.form.admin;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.guis.creator.view.CustomLabelPath;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ContentAttributeManager extends Composite implements MooshakWidget,
	HasValueChangeHandlers<MooshakValue>, ValueChangeHandler<MooshakValue> {
	
	private static final int MAX_FILE_SIZE = 2000000;
	private static final int MESSAGE_VIEWING_TIME = 5*1000;

	private static ContentAttributeManagerUiBinder uiBinder = GWT
			.create(ContentAttributeManagerUiBinder.class);

	@UiTemplate("ContentAttributeManager.ui.xml")
	interface ContentAttributeManagerUiBinder extends
			UiBinder<Widget, ContentAttributeManager> {
	}
	
	private List<ValueChangeHandler<MooshakValue>> valueChangeHandlers = 
			new ArrayList<ValueChangeHandler<MooshakValue>>();
	
	@UiField
	HTMLPanel container;
	
	@UiField
	Button openPopup;

	@UiField
	DecoratedPopupPanel popup;
	
	@UiField
	Label message;
	
	@UiField
	Button ok;
	
	@UiField
	HTMLPanel filesList;
	
	@UiField
	FileUpload fileUpload;

	private String field = null;

	MooshakValue value;
	
	public ContentAttributeManager() {
		initWidget(uiBinder.createAndBindUi(this));
		
		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				popup.hide();
			}
		});
		
		openPopup.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Button source = (Button) event.getSource();

				if (popup.isShowing()) 
					popup.hide();
				else {
					int left = source.getAbsoluteLeft();
					int top = source.getAbsoluteTop()
							+ source.getOffsetHeight();
					popup.setPopupPosition(left, top);

					popup.show();

				} 
			}
		});
		
		popup.hide();
		container.remove(popup);
		
		
	}
	

	@Override
	public MooshakValue getValue() {
		return value;
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value, false);
	}

	@Override
	public void setValue(MooshakValue val, boolean fireEvents) {
		if(val == null) 
			return;
		
		field = val.getField();
		value = val;
		filesList.clear();
		
		for (final String file : val.getFileNames()) {
			
			final HorizontalPanel hpane = new HorizontalPanel();
			
			CustomLabelPath label = new CustomLabelPath();
			label.setValue(new MooshakValue(field, file, 
					val.getFileValue(file).getContent()));
			/*Button removeBtn = new Button("remove");
			removeBtn.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					value.removeFile(file);					
					ValueChangeEvent<MooshakValue> eventToSend = 
							new ValueChangeEvent<MooshakValue>(value) {};
							
					onValueChange(eventToSend);
					filesList.remove(hpane);
				}
			});*/
			
			hpane.add(label);
			/*hpane.add(removeBtn);*/
			
			filesList.add(hpane);
		}
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

	@Override
	public boolean isEditing() {
		return false;
	}
	
	public void setMessage(String text) {
		message.setText(text);
		
		resetMessage();
		
	}

	private Timer cleanupTimer = null;

	/**
	 * Reset message after some time
	 */
	private void resetMessage() {
		
		if(cleanupTimer != null)
			cleanupTimer.cancel();
		
		new Timer() {

			@Override
			public void run() {
				message.setText("");
				cleanupTimer = null;
			}
			
		}.schedule(MESSAGE_VIEWING_TIME);
	}

	@Override
	public void onValueChange(ValueChangeEvent<MooshakValue> event) {
		
		for (ValueChangeHandler<MooshakValue> handler : valueChangeHandlers) {
			handler.onValueChange(event);
		}
	}

	public void onFileChange(String content, String name) {
		byte[] result = Base64Coder.decode(content);
		
		if(result.length > MAX_FILE_SIZE) {
			setMessage("File size exceeded");
			return;
		}
		
		setMessage("Uploading ...");
		
		fireUploadFileEvent(name, result);
	}
	
	/**
	 * Fires an upload event
	 * @param fileName
	 * @param content
	 */
	public void fireUploadFileEvent(String fileName, byte[] content) {
		value.addFileValue(new MooshakValue(field, fileName, content));
		ValueChangeEvent<MooshakValue> event = 
				new ValueChangeEvent<MooshakValue>(value){};
				
		onValueChange(event);
		
	}
	
	/**
	 * List of objects created for this class. Used for mapping static methods
	 * to them
	 */
	private static List<ContentAttributeManager> pool = 
			new ArrayList<ContentAttributeManager>();

	/**
	 * Receive load notification to set JS event handlers
	 */
	protected void onLoad() {
		int id;

		synchronized (pool) {
			pool.add(this);
			id = pool.size() - 1;
		}

		newFileUploaderJS(id, fileUpload.getElement());

	}

	/**
	 * Read file
	 * 
	 * @param obj
	 */
	private static native void uploadFileBase64(JavaScriptObject obj, int id) /*-{
	    $wnd.onFileChange =  $entry(
	    @pt.up.fc.dcc.mooshak.client.form.admin.ContentAttributeManager::onFileChange(ILjava/lang/String;Ljava/lang/String;));
        
        var reader = new $wnd.FileReader();
		reader.onloadend = function () {
            var b64 = $wnd.base64ArrayBuffer(this.result);
            $wnd.FileChange(b64);
        };

		var file = obj.files[0];
        reader.readAsArrayBuffer(file);
		
		$wnd.FileChange = function(result) {
			$wnd.onFileChange(id, result, file.name);
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
		 
		  // Main loop deals with bytes in chunks of 3
		  for (var i = 0; i < mainLength; i = i + 3) {
		    // Combine the three bytes into a single integer
		    chunk = (bytes[i] << 16) | (bytes[i + 1] << 8) | bytes[i + 2]
		 
		    // Use bitmasks to extract 6-bit segments from the triplet
		    a = (chunk & 16515072) >> 18 // 16515072 = (2^6 - 1) << 18
		    b = (chunk & 258048)   >> 12 // 258048   = (2^6 - 1) << 12
		    c = (chunk & 4032)     >>  6 // 4032     = (2^6 - 1) << 6
		    d = chunk & 63               // 63       = 2^6 - 1
		 
		    // Convert the raw binary segments to the appropriate ASCII encoding
		    base64 += encodings[a] + encodings[b] + encodings[c] + encodings[d]
		  }
		 
		  // Deal with the remaining bytes and padding
		  if (byteRemainder == 1) {
		    chunk = bytes[mainLength]
		 
		    a = (chunk & 252) >> 2 // 252 = (2^6 - 1) << 2
		 
		    // Set the 4 least significant bits to zero
		    b = (chunk & 3)   << 4 // 3   = 2^2 - 1
		 
		    base64 += encodings[a] + encodings[b] + '=='
		  } else if (byteRemainder == 2) {
		    chunk = (bytes[mainLength] << 8) | bytes[mainLength + 1]
		 
		    a = (chunk & 64512) >> 10 // 64512 = (2^6 - 1) << 10
		    b = (chunk & 1008)  >>  4 // 1008  = (2^6 - 1) << 4
		 
		    // Set the 2 least significant bits to zero
		    c = (chunk & 15)    <<  2 // 15    = 2^4 - 1
		 
		    base64 += encodings[a] + encodings[b] + encodings[c] + '='
		  }
		  
		  return base64
		}
		
	  }-*/;

	/**
	 * Instantiate FileUploader JS counterpart
	 * 
	 * @param fileElement
	 * @param areaElement
	 */
	private static native void newFileUploaderJS(int id,
			JavaScriptObject fileElement) /*-{

		new $wnd.FileUploader(id, fileElement);
	}-*/;

	public static void onFileChange(int id, String content, 
			String name) {
		pool.get(id).onFileChange(content, name);
	}

	/**
	 * Define the JS counterpart of the FileUploader class
	 */
	public static native void defineFileUploaderJS() /*-{
		
	    $wnd.uploadFileBase64 =  function(obj,id){
	    	@pt.up.fc.dcc.mooshak.client.form.admin.ContentAttributeManager::uploadFileBase64(*)(obj,id);
	    }
		
		$wnd.FileUploader = function(id,fileElement) {
			this.id = id;
			this.fileElement = fileElement;
			
			this.fileElement.addEventListener('change',
				this.fileChangeHandler.bind(this),
				false);
		};		

		// Handle file change: read file as binary string
		$wnd.FileUploader.prototype.fileChangeHandler = function(event) {
			$wnd.uploadFileBase64(event.target,this.id);
		};
		
	}-*/;

	static {
		defineFileUploaderJS();
	}

}
