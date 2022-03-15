package pt.up.fc.dcc.quizEditor.client.wrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.quizEditor.client.resources.Resources; 

/**
 * Main class of QuizEditor GWT Wrapper
 * 
 * @author Helder Correia
 */
public class QuizEditor extends ResizeComposite implements HasValueChangeHandlers<String> {
	public static final String DEFAULT_EDITOR_ID = "editor";

	private static QuizEditorUiBinder uiBinder = GWT.create(QuizEditorUiBinder.class);

	interface QuizEditorUiBinder extends UiBinder<Widget, QuizEditor> {
	}

	interface BaseStyle extends CssResource {
		String hide();
		String base();
	}
	
	private static QuizEditor instance = null;

	private static Map<String, List<ValueChangeHandler<String>>> handlers = new HashMap<>();

	private static final Logger LOGGER = Logger.getLogger("");

	@UiField
	static BaseStyle style;

	@UiField
	ResizeHtmlPanel base;

	private String id;
	
	private String xml;
	
	private boolean loaded = false;

	private QuizEditor() {
		this(DEFAULT_EDITOR_ID);
	}

	private QuizEditor(String id) {

		initWidget(uiBinder.createAndBindUi(this));

		base.add(new HTML("<div id=\"" + id + "\"></div"));

		this.id = id;
	}
	
	public static QuizEditor getInstance() {
		if (instance == null)
			instance = new QuizEditor();
		return instance;
	}
	
	public static QuizEditor getInstance(String id) {
		if (instance == null)
			instance = new QuizEditor(id);
		return instance;
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		if (!loaded) {
			injectStyles();
			injectJavaScriptFiles();
	
			init();
			
			start(id);
		}
		
		loaded = true;
	}

	/**
	 * Apply initial styles to player
	 */
	private void injectStyles() {

		for (CssResource cssResource : Arrays.asList(Resources.INSTANCE.styleCss(),
				Resources.INSTANCE.styleCss())) {
			cssResource.ensureInjected();
		}
	}

	/**
	 * Inject JS files stored as resources in script elements of top window
	 */
	private void injectJavaScriptFiles() {
		


		for (TextResource textResource : Arrays.asList(Resources.INSTANCE.EditorJs(),
				Resources.INSTANCE.importEditorJs(), Resources.INSTANCE.XMLParserJs())) {
			ScriptInjector.fromString(textResource.getText()).inject();
		}
	}
	
	

	/**
	 * Initialize QuizEditor
	 */
	private void init() {
		
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<String> handler) {
		if (!handlers.containsKey(id))
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

	
	
	private native void start(String id) /*-{
		var el = $doc.getElementById(id);
		var that = this;
		var init = function(){
			if(Editor) {
				Editor.init(el);
			} else
				setTimeout(init, 50);
		};
		
		Editor.prototype.onChange = this.@pt.up.fc.dcc.quizEditor.client.wrapper.QuizEditor::onChange(Ljava/lang/String;).bind(this);
		init();

	}-*/;
	
	/**
	 * Export contents of editor as a JSON string
	 * 
	 * @return contents of editor as a JSON string
	 */
	public String exportJson() {
		return exportQuiz(id);
	}
	
	/**
	 * Import an XML string to the editor
	 * 
	 * @param xml XML string to import
	 */
	public void importXML(String xml) {
		importXML(id, xml);
	}
	
	/**
	 * Get the Json of language
	 * 
	 * @return language
	 */
	public static native String exportQuiz(String id) /*-{
		var el = $doc.getElementById(id);
		return JSON.stringify(el.editor.exportJSON());
	}-*/;

	/**
	 * Harvest the contents of an editor
	 * 
	 * @return XML string of contents
	 */
	public static native String harvest(String id) /*-{
		var el = $doc.getElementById(id);
		return el.editor.exportJSON();
	}-*/;

	public static native String importXML(String id, String xml) /*-{
		var el = $doc.getElementById(id);
		return el.editor.importXML(xml);
	}-*/;


	
	protected void onChange(final String json) {

		if (!handlers.containsKey(id))
			return;

		for (ValueChangeHandler<String> handler : handlers.get(id)) {
			handler.onValueChange(new ValueChangeEvent<String>(json) {
			});
		}
	}
		
}
