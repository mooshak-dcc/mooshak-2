package pt.up.fc.dcc.mooshak.client.utils;

import static com.google.gwt.query.client.GQuery.$;

import java.util.Arrays;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.query.client.GQuery;

/**
 * Fullscreen commands
 * 
 * @author josepaiva
 */
public class FullscreenUtils {
	
	/**
	 * Request browser to enter fullscreen
	 */
	public static native void requestFullscreen() /*-{
		var docElement = $doc.documentElement;
		if (docElement.requestFullscreen) {
			docElement.requestFullscreen();
		} else if (docElement.msRequestFullscreen) {
			docElement.msRequestFullscreen();
		} else if (docElement.mozRequestFullScreen) {
			docElement.mozRequestFullScreen();
		} else if (docElement.webkitRequestFullScreen) {
			docElement.webkitRequestFullScreen();
		}
	}-*/;
	
	/**
	 * Request browser to enter fullscreen explicitly with or without 
	 * input
	 * 
	 * @param input
	 */
	public static void requestFullscreen(boolean input) {
		if (input) {
			requestFullscreenWithInput();
		}
		requestFullscreen();
	}
	
	/**
	 * Request browser to enter fullscreen explicitly with input
	 */
	public static native void requestFullscreenWithInput() /*-{
		var docElement = $doc.documentElement;
		if (docElement.webkitRequestFullScreen) {
			docElement.webkitRequestFullScreen(Element.ALLOW_KEYBOARD_INPUT);
		} else if (docElement.requestFullscreen) {
			docElement.requestFullscreen();
		} else if (docElement.msRequestFullscreen) {
			docElement.msRequestFullscreen();
		} else if (docElement.mozRequestFullScreen) {
			docElement.mozRequestFullScreen();
		}
	}-*/;
	
	/**
	 * Request browser to exit fullscreen
	 */
	public static native void exitFullscreen() /*-{
		if ($doc.exitFullscreen) {
			$doc.exitFullscreen();
		} else if ($doc.msExitFullscreen) {
			$doc.msExitFullscreen();
		} else if ($doc.mozCancelFullScreen) {
			$doc.mozCancelFullScreen();
		} else if ($doc.webkitCancelFullScreen) {
			$doc.webkitCancelFullScreen();
		}
	}-*/;

	/**
	 * Check if fullscreen is activated
	 */
	public static boolean isFullscreen() {
		GQuery document = $(Document.get());
		return GWT.isClient()
				&& Arrays.asList((Boolean) document.prop("fullscreen"),
						(Boolean) document.prop("mozFullScreen"), 
						(Boolean) document.prop("webkitIsFullScreen"),
						(Boolean) document.prop("msFullscreenElement"))
						.contains(Boolean.TRUE);
	}
}
