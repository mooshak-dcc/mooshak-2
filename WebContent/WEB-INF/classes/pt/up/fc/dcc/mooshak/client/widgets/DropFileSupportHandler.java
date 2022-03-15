package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.Composite;

/**
 * Defines methods to handle file dropping to UI
 * @author josepaiva
 */
public abstract class DropFileSupportHandler extends Composite {

	/**
	 * Method called to handle a file drop
	 * @param content of the file encoded in Base64
	 * @param name of the file
	 */
	public abstract void onFileDropped(String content, String name);
	
	/**
	 * List of objects created for this class. Used for mapping static methods
	 * to them
	 */
	public static List<DropFileSupportHandler> pool = new ArrayList<DropFileSupportHandler>();

	/**
	 * Drop Handler, load file data to editor
	 * 
	 * @param obj
	 */
	public static native void dropHandlerSupport(JavaScriptObject obj, int id) /*-{
	    $wnd.onFileDropped =  $entry(
	    @pt.up.fc.dcc.mooshak.client.widgets.DropFileSupportHandler::onFileDropped(ILjava/lang/String;Ljava/lang/String;));
        
		var file = obj.files[0];
        if (file == undefined) {
        	 console.log("Invalid file!");
        	 return;
        }
        	
        
        var reader = new $wnd.FileReader();
		reader.onloadend = function () {
            var b64 = $wnd.base64ArrayBuffer(this.result);
            $wnd.FileDropped(b64);
        };

        reader.readAsArrayBuffer(file);
		
		$wnd.FileDropped = function(result) {
			$wnd.onFileDropped(id, result, file.name);
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
	 * Instantiate FileContent JS counterpart
	 * 
	 * @param fileElement
	 * @param areaElement
	 */
	public static native void newFileContentJS(int id,
			JavaScriptObject fileElement, JavaScriptObject nameElement,
			JavaScriptObject areaElement, JavaScriptObject bodyElement) /*-{

		new $wnd.FileContent(id, fileElement, nameElement, areaElement,
				bodyElement);
	}-*/;

	public static void onFileDropped(int id, String content, 
			String name) {
		pool.get(id).onFileDropped(content, name);
	}

	/**
	 * Define the JS counterpart of the FileContent class
	 */
	public static native void defineFileContentJS() /*-{
		
	    $wnd.dropHandlerSupport =  function(obj,id){
	    	@pt.up.fc.dcc.mooshak.client.widgets.DropFileSupportHandler::dropHandlerSupport(*)(obj,id);
	    }
		
		$wnd.FileContent = function(id,fileElement,nameElement,areaElement,bodyElement) {
			this.id = id;
			this.fileElement = fileElement;
			this.nameElement = nameElement;
			this.areaElement = areaElement;
			this.bodyElement = bodyElement;
			
			if (this.fileElement) {
				this.fileElement.addEventListener('change',
					this.fileChangeHandler.bind(this),
					false);
			}
			if (this.areaElement) {
				this.areaElement.addEventListener('drop',
					this.dropHandler.bind(this), 
					false);
			}
			if (this.nameElement) {
				this.nameElement.addEventListener('drop',
					this.dropHandler.bind(this), 
					false);
			}
			
			if (this.bodyElement) {
				this.bodyElement.ondragover = function(event){
				    event.preventDefault();
				}
			}
			document.ondragover = function(event){
			    event.preventDefault();
			}
		};		

		// Handle file change: read file as binary string
		$wnd.FileContent.prototype.fileChangeHandler = function(event) {
			$wnd.dropHandlerSupport(event.target,this.id);
		};
		
		// Handle drop events: read file as binary string after stopping events
		$wnd.FileContent.prototype.dropHandler = function(event) {
			event.stopPropagation();
			event.preventDefault();
			$wnd.dropHandlerSupport(event.dataTransfer,this.id);
			
		};
		
	}-*/;

	static {
		defineFileContentJS();
	}
}
