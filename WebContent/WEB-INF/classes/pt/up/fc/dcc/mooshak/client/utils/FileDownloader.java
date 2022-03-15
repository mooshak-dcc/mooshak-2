package pt.up.fc.dcc.mooshak.client.utils;

import com.google.gwt.typedarrays.client.ArrayBufferNative;
import com.google.gwt.typedarrays.client.Uint8ArrayNative;
import com.google.gwt.typedarrays.shared.ArrayBuffer;
import com.google.gwt.typedarrays.shared.Uint8Array;

/**
 * Utils to download files on the client-side
 * 
 * @author josepaiva
 */
public class FileDownloader {
	
	/**
	 * Issues a binary file download
	 * @param fileName
	 * @param content
	 * @param mime
	 */
	public static void downloadBinaryFile(String fileName, byte[] content, String mime) {

		ArrayBuffer buf=ArrayBufferNative.create(content.length);
		Uint8Array view=Uint8ArrayNative.create(buf);
		for (int i=0; i < content.length; i++) {
			view.set(i, content[i]);
		}
		
		downloadFile(fileName, buf, mime);
	}
	
	/**
	 * Issues a file download
	 * @param fileName
	 * @param content
	 */
	public static void downloadFile(String fileName, Object content) {
		downloadFile(fileName, content, "text/plain");
	}
	
	/**
	 * Issues a file download
	 * @param fileName
	 * @param content
	 * @param mime
	 */
	public static native void downloadFile(String fileName, Object content, String mime) /*-{
	
		var textFileAsBlob = new Blob([content], {type:mime});
		var link = document.createElement('a');
		link.download = fileName;
		if (window.webkitURL != null) {
			link.href = window.webkitURL.createObjectURL(textFileAsBlob);
		} else {
			link.href = window.URL.createObjectURL(textFileAsBlob);
		}

		// Because firefox not executing the .click() well
		// We need to create mouse event initialization.
		var clickEvent = document.createEvent("MouseEvent");
		clickEvent.initEvent("click", true, true);
		
		link.dispatchEvent(clickEvent);
		
	}-*/;

}
