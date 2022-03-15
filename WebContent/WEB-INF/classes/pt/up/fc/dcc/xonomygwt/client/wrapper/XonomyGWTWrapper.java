package pt.up.fc.dcc.xonomygwt.client.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.xonomygwt.client.resources.Resources;

/**
 * Main class of Xonomy GWT Wrapper
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class XonomyGWTWrapper extends ResizeComposite implements HasValueChangeHandlers<String> {
	public static final String DEFAULT_EDITOR_ID = "xonomy-editor-";

	private static final Logger LOGGER = Logger.getLogger("");

	private static XonomyGWTWrapperUiBinder uiBinder = GWT.create(XonomyGWTWrapperUiBinder.class);

	interface XonomyGWTWrapperUiBinder extends UiBinder<Widget, XonomyGWTWrapper> {
	}

	interface BaseStyle extends CssResource {
		String hide();
	}
	
	private static int count = 1;
	
	private static boolean loaded = false;

	private static Map<String, List<ValueChangeHandler<String>>> handlers = new HashMap<>();

	@UiField
	static BaseStyle style;

	@UiField
	ResizeHtmlPanel base;

	private String id;

	public XonomyGWTWrapper() {
		this(DEFAULT_EDITOR_ID + count);
	}

	public XonomyGWTWrapper(String id) {

		initWidget(uiBinder.createAndBindUi(this));

		base.add(new HTML("<div id=\"" + id + "\"></div"));

		this.id = id;
		
		count++;
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		if (!loaded) {
			injectStyles();
			injectJavaScriptFiles();
	
			init();
		}
		
		loaded = true;
	}

	/**
	 * Apply initial styles to player
	 */
	private void injectStyles() {

		for (CssResource cssResource : Arrays.asList(Resources.INSTANCE.xonomyGwtCss())) {
			cssResource.ensureInjected();
		}
	}

	/**
	 * Inject JS files stored as resources in script elements of top window
	 */
	private void injectJavaScriptFiles() {
		
		if (!isJqueryLoaded())
			ScriptInjector.fromString(Resources.INSTANCE.jqueryJs().getText())
				.setWindow(ScriptInjector.TOP_WINDOW).setRemoveTag(false).inject();

		for (TextResource textResource : Arrays.asList(Resources.INSTANCE.xonomyJs(),
				Resources.INSTANCE.helperJs())) {
			ScriptInjector.fromString(textResource.getText())
					.setWindow(ScriptInjector.TOP_WINDOW)
					.setRemoveTag(false)
					.inject();
		}
	}
	
	private native boolean isJqueryLoaded() /*-{
		if ($wnd.jQuery)
			return true;
		return false;
	}-*/;

	/**
	 * Initialize Xonomy
	 */
	private void init() {
		Element editorElement = DOM.getElementById(id);
		Event.sinkEvents(editorElement, Event.ONCHANGE);
		Event.setEventListener(editorElement, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONCHANGE == event.getTypeInt()) {
					if (!handlers.containsKey(id))
						return;
					for (ValueChangeHandler<String> handler : handlers.get(id)) {
						handler.onValueChange(new ValueChangeEvent<String>(harvest()) {
						});
					}
				}
			}
		});
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
		if (handlers.containsKey(id))
			handlers.remove(id);
		
		handlers.put(id, new ArrayList<ValueChangeHandler<String>>());
		handlers.get(id).add(handler);

		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				handlers.get(id).remove(handler);
			}
		};
	}
	
	/**
	 * Set the height
	 * 
	 * @param height height to set
	 */
	public void setHeight(String height) {
		base.setHeight(height);
		base.onResize();
	}
	
	/**
	 * Set the width
	 * 
	 * @param width width to set
	 */
	public void setWidth(String width) {
		base.setWidth(width);
		base.onResize();
	}

	/**
	 * Get the language
	 * 
	 * @return language
	 */
	public static native String getLanguage() /*-{
		return $wnd.Xonomy.lang;
	}-*/;

	/**
	 * Set the language
	 * 
	 * @param lang
	 *            language to set
	 */
	public static native void setLanguage(String lang) /*-{
		$wnd.Xonomy.lang = lang;
	}-*/;

	/**
	 * Get the mode
	 * 
	 * @return mode
	 */
	public static native String getMode() /*-{
		return $wnd.Xonomy.mode;
	}-*/;

	/**
	 * Set the mode
	 * 
	 * @param mode
	 *            mode to set
	 */
	public static native void setMode(String mode) /*-{
		$wnd.Xonomy.setMode(mode);
	}-*/;

	/**
	 * Set the data
	 * 
	 * @param data
	 *            data to set
	 */
	public native void setData(String data) /*-{
		var id = this.@pt.up.fc.dcc.xonomygwt.client.wrapper.XonomyGWTWrapper::id;
		var editor = $doc.getElementById(id);
		$wnd.Xonomy.render(data, editor, $wnd.Xonomy.docSpec);
	}-*/;

	/**
	 * Set the docSpec
	 * 
	 * @param docSpec
	 *            docSpec to set
	 */
	public native void setDocSpec(String docSpec) /*-{
		
		if (docSpec == null)
			return;
			
		$wnd.Xonomy.docSpec=JSON.parse(docSpec);
		$wnd.Xonomy.verifyDocSpec();
		
		var id = this.@pt.up.fc.dcc.xonomygwt.client.wrapper.XonomyGWTWrapper::id;
		var editor = $doc.getElementById(id);
		$wnd.Xonomy.render($wnd.Xonomy.harvest(), editor, $wnd.Xonomy.docSpec);
	}-*/;
	
	/**
	 * Get default root element based in doc spec
	 * 
	 * @return default root element
	 */
	public static native String getDefaultRootElement() /*-{
		if ($wnd.Xonomy && $wnd.Xonomy.docSpec) {
			return Object.keys($wnd.Xonomy.docSpec.elements)[0];
		}
		return "root";
	}-*/;

	/**
	 * Renders the contents of an editor
	 * 
	 * @param data
	 *            XML string
	 * @param docSpec
	 *            valid JSON string document specification
	 */
	public native void render(String data, String docSpec) /*-{
		var id = this.@pt.up.fc.dcc.xonomygwt.client.wrapper.XonomyGWTWrapper::id;
		var editor = $doc.getElementById(id);
		$wnd.Xonomy.render(data, editor, JSON.parse(docSpec));
	}-*/;

	/**
	 * Renders the contents of an editor
	 * 
	 * @param data
	 *            Xonomy-compliant JSON as string
	 * @param docSpec
	 *            valid JSON string document specification
	 */
	public native void renderJsonData(String data, String docSpec) /*-{
		var id = this.@pt.up.fc.dcc.xonomygwt.client.wrapper.XonomyGWTWrapper::id;
		var editor = $doc.getElementById(id);
		$wnd.Xonomy.render(JSON.parse(data), editor, JSON.parse(docSpec));
	}-*/;

	/**
	 * Refresh display
	 */
	public static native void refresh() /*-{
		$wnd.Xonomy.refresh();
	}-*/;

	/**
	 * Harvest the contents of an editor
	 * 
	 * @return XML string of contents
	 */
	public static native String harvest() /*-{
		return $wnd.Xonomy.harvest();
	}-*/;

	/**
	 * Get text in selected language
	 * 
	 * @param text
	 *            a string of the form "lang: text_in_lang | lang: text_in_lang
	 *            | ..."
	 * @return text in selected language
	 */
	public static native String textByLang(String text) /*-{
		return $wnd.Xonomy.textByLang(text);
	}-*/;
}
