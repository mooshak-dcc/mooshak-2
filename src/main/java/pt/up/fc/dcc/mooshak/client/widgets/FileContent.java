package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.DropEvent;
import com.google.gwt.event.dom.client.DropHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceCommandDescription;
import edu.ycp.cs.dh.acegwt.client.ace.AceDefaultCommandLine;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;
import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.Filenames;

/**
 * A composite widget to collect a local file content. Files can be selected
 * using a file selector or drag-and drop.
 * 
 * @author josepaiva
 * @since 2014-07-15
 *
 */
public class FileContent extends DropFileSupportHandler {

	private static FileContentUiBinder uiBinder = GWT
			.create(FileContentUiBinder.class);

	@UiTemplate("FileContent.ui.xml")
	interface FileContentUiBinder extends UiBinder<Widget, FileContent> {
	}

	@UiField
	TextBox filename;

	@UiField
	AceEditor editor;

	@UiField
	HTMLPanel fileHeader;

	@UiField
	FileUpload fileUpload;
	
	private boolean editable;
	private byte[] content;
	
	private Map<String, String> availableLanguages;
	
	private TextBox commandLine = new TextBox(); 
	
	private HasFileContent form;
	
	private AceEditorMode languageMode;

	public FileContent() {
		initWidget(uiBinder.createAndBindUi(this));
		configureAceEditor();
		
		filename.ensureDebugId("fileName");
		editor.ensureDebugId("editor");
		fileUpload.ensureDebugId("fileUpload");
		
		final Element ace = editor.getElement().getElementsByTagName("textarea")
				.getItem(0);
		Event.sinkEvents(ace, Event.ONFOCUS | Event.ONBLUR);
	    Event.setEventListener(ace, new EventListener() {

	        @Override
	        public void onBrowserEvent(Event event) {
	             if(Event.ONBLUR == event.getTypeInt()) {
	            	 if (!filename.getText().equals(""))
	            		 updateFileName();
	             }
	        }
	    });
	}

	public void setForm(HasFileContent form) {
		this.form = form;
	}

	private void configureAceEditor() {
		editor.startEditor();
		editor.setMode(AceEditorMode.JAVA);
		languageMode = AceEditorMode.JAVA;
		editor.setTheme(AceEditorTheme.ECLIPSE);
		editor.getElement().setId("fileContent");
		
		editor.initializeCommandLine(new AceDefaultCommandLine(commandLine));
		editor.addCommand(new AceCommandDescription("increaseFontSize", 
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(AceEditor editor) {
				int fontSize = editor.getFontSize();
				editor.setFontSize(fontSize + 1);
				return null;
			}
		}).withBindKey("Ctrl-=|Ctrl-+"));
		editor.addCommand(new AceCommandDescription("decreaseFontSize", 
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(AceEditor editor) {
				int fontSize = editor.getFontSize();
				fontSize = Math.max(fontSize - 1, 1);
				editor.setFontSize(fontSize);
				return null;
			}
		}).withBindKey("Ctrl+-|Ctrl-_"));
		editor.addCommand(new AceCommandDescription("resetFontSize", 
				new AceCommandDescription.ExecAction() {
			@Override
			public Object exec(AceEditor editor) {
				editor.setFontSize(12);
				return null;
			}
		}).withBindKey("Ctrl+0|Ctrl-Numpad0"));

		editor.setAutocompleteEnabled(true);
		editor.setUseSoftTabs(true);
		editor.setUseWrapMode(true);
		
		editor.setLiveAutocompleteEnabled(true);
		editor.setSnippetsEnabled(true);
	    
	   // updateFileName();
	}

	/**
	 * @return the editor
	 */
	public AceEditor getEditor() {
		return editor;
	}

	/**
	 * @return the editor
	 */
	public HTMLPanel getFileHeader() {
		return fileHeader;
	}
	
	/**
	 * Sets Ace Editor theme
	 * @param theme
	 */
	public void setTheme(AceEditorTheme theme) {
		editor.setTheme(theme);
	}
	
	/**
	 * Sets Ace Editor mode
	 * @param theme
	 */
	public void setMode(AceEditorMode mode) {
		editor.setMode(mode);
		languageMode = mode;
	}

	/**
	 * Changes the editor language based in file name
	 * @param askSkeletonUse
	 */
	public void onChangeExtension(boolean askSkeletonUse) {
		String languageExtension = getProgramExtension();
		
		if (languageExtension.equals(""))
			return;
		
		if (availableLanguages != null &&
				availableLanguages.get(languageExtension) == null) {
			Window.alert("This file extension is not available.");
			return;
		}

		switch (languageExtension.toLowerCase()) {
		case "ada":
			setMode(AceEditorMode.ADA);
			break;
		case "c":
		case "cpp":
			setMode(AceEditorMode.C_CPP);
			break;
		case "clj":
			setMode(AceEditorMode.CLOJURE);
			break;
		case "cbl":
			setMode(AceEditorMode.COBOL);
			break;
		case "coffee":
			setMode(AceEditorMode.COFFEE);
			break;
		case "cfm":
			setMode(AceEditorMode.COLDFUSION);
			break;
		case "cs":
			setMode(AceEditorMode.CSHARP);
			break;
		case "css":
			setMode(AceEditorMode.CSS);
			break;
		case "d":
			setMode(AceEditorMode.D);
			break;
		case "erl":
			setMode(AceEditorMode.ERLANG);
			break;
		case "forth":
			setMode(AceEditorMode.FORTH);
			break;
		case "go":
			setMode(AceEditorMode.GOLANG);
			break;
		case "groovy":
			setMode(AceEditorMode.GROOVY);
			break;
		case "hs":
			setMode(AceEditorMode.HASKELL);
			break;
		case "html":
			setMode(AceEditorMode.HTML);
			break;
		case "java":
			setMode(AceEditorMode.JAVA);
			break;
		case "js":
			setMode(AceEditorMode.JAVASCRIPT);
			break;
		case "json":
			setMode(AceEditorMode.JSON);
			break;
		case "jl":
			setMode(AceEditorMode.JULIA);
			break;
		case "lisp":
		case "clisp":
			setMode(AceEditorMode.LISP);
			break;
		case "lua":
			setMode(AceEditorMode.LUA);
			break;
		case "m":
			setMode(AceEditorMode.MATLAB);
			break;
		case "ml":
			setMode(AceEditorMode.OCAML);
			break;
		case "pas":
		case "p":
		case "pp":
		case "pascal":
			setMode(AceEditorMode.PASCAL);
			break;
		case "pl":
			if (availableLanguages != null && availableLanguages.get(languageExtension) != null
					 && availableLanguages.get(languageExtension).equalsIgnoreCase("prolog")) {
				setMode(AceEditorMode.PROLOG);
				break;
			}
			setMode(AceEditorMode.PERL);
			break;
		case "php":
			setMode(AceEditorMode.PHP);
			break;
		case "prolog":
		case "yap":
		case "pro":
			setMode(AceEditorMode.PROLOG);
			break;
		case "py":
			setMode(AceEditorMode.PYTHON);
			break;
		case "r":
			setMode(AceEditorMode.R);
			break;
		case "rb":
			setMode(AceEditorMode.RUBY);
			break;
		case "rs":
			setMode(AceEditorMode.RUST);
			break;
		case "sc":
		case "scala":
			setMode(AceEditorMode.SCALA);
			break;
		case "sql":
			setMode(AceEditorMode.SQL);
			break;
		case "tcl":
			setMode(AceEditorMode.TCL);
			break;
		case "txt":
			setMode(AceEditorMode.TEXT);
			break;
		case "vb":
			setMode(AceEditorMode.VBSCRIPT);
			break;
		case "xml":
			setMode(AceEditorMode.XML);
			break;

		default:
			setMode(AceEditorMode.JAVA);
			break;
		}
		
		form.onChangeProgramExtension(languageExtension, askSkeletonUse);
	}
	
	public boolean isLanguageEditable() {
		return editable;
	}

	public byte[] getProgramCode() {
		if (editable)
			return editor.getText().getBytes();
		else
			return content;
	}

	public String getProgramName() {
		return filename.getText();
	}
	
	/**
	 * Get the extension of the file name
	 * @return
	 */
	public String getProgramExtension() {
		return Filenames.getExtension(filename.getText());
	}

	@UiHandler({ "filename" })
	void lostFocus(BlurEvent event) {
		onChangeExtension(true);
	}
	
	public void setLanguageEditable(boolean editable) {
		
		if (this.editable == editable)
			return;
		
		if (editable) 
			try {
				if (content != null)
					editor.setText(new String(content));
				else
					editor.setText("");
			} catch (IllegalArgumentException e) {
				Logger.getLogger("").severe(e.getMessage());
				editor.setText("");
			}
		else
			editor.setText("");
	
		editor.setReadOnly(!editable);
		
		this.editable = editable;
	}

	/** Drag and drop **/

	public void setFileContent(byte[] content) {
		
		this.content = content;
		
		if (editable) 
			try {
				editor.setText(new String(content));
			} catch (IllegalArgumentException e) {
				Logger.getLogger("").severe(e.getMessage());
				editor.setText("");
			}
		else
			editor.setText("");
		
		form.clearObservations();
	}

	public void setFilename(String filename) {
		this.filename.setText(filename);
		onChangeExtension(false);
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

		newFileContentJS(id, fileUpload.getElement(), filename.getElement(),
				editor.getElement(), RootPanel.getBodyElement());

		final int idf = id;
		RootPanel.get().addDomHandler(new DropHandler() {
			@Override
			public void onDrop(DropEvent event) {
				// stop default behaviour
				event.preventDefault();
				event.stopPropagation();

				// starts the fetching, reading and callbacks
				dropHandlerSupport(event.getDataTransfer(), idf);
			}
		}, DropEvent.getType());
	}

	@Override
	public void onFileDropped(String content, String name) {
		byte[] result = Base64Coder.decode(content);

		if (availableLanguages != null &&
				availableLanguages.get(Filenames.getExtension(name)) == null) {
			Window.alert("This file extension is not available.");
			return;
		}
		
		setFilename(name);
		setFileContent(result);
		
		onChangeExtension(false);
	}

	private static final RegExp REGEX_JAVA_CLASSNAME = RegExp
			.compile("class\\s*([^\\{\\s]*)[^\\{]*\\{.*","m");

	private static final RegExp REGEX_MAIN_METHOD = RegExp
			.compile("(public\\s+)?static\\s*(public\\s+)?void\\s*main\\s*\\(\\s*String[^\\)]*.*","m");

	/**
	 * Updates the file name
	 */
	public void updateFileName() {
		
		String extension = getProgramExtension();
		String modeExtension = getExtensionForMode();
		
		if (modeExtension == null) {
		
			if (availableLanguages != null && availableLanguages.size() > 1)
				return;
			
			if (filename.getText().equals(""))
				setFilename("program");
			return;
		}
		
		if (modeExtension.equals("c") && extension.equalsIgnoreCase("cpp"))
			return;
		
		if (extension != null && !extension.isEmpty() && !extension.equalsIgnoreCase("java"))
			return;
		
		switch (modeExtension.toLowerCase()) {
		case "java":
			String filename = getJavaMainClassNameFromSourceCode(editor.getText());
			if (filename == null)
				filename = getProgramName() == null || getProgramName().isEmpty() ?
						"program.java" : getProgramName();
			setFilename(filename);
			break;

		default:
			setFilename("program." + modeExtension);
			break;
		}
	}
	
	public String getJavaMainClassNameFromSourceCode(String source) {
		
		int openBr = 0;
		int start = source.indexOf("{");
		int lastPos = 0;
		
		while (start >= 0) {
			start = start + lastPos;
			int pos = start;
			do {
				char c = source.charAt(pos);
				if (c == '{')
					openBr++;
				else if (c == '}')
					openBr--;
				
				pos++;
			} while (openBr > 0 && pos < source.length());
			
			if (pos == source.length() && openBr > 0)
				return null;
			String s = source.substring(start, pos);
			if (REGEX_MAIN_METHOD.test(s)) {
				MatchResult result = REGEX_JAVA_CLASSNAME.exec(source.substring(lastPos,
						start+1));
				if (result != null) {
					return result.getGroup(1) + ".java";
				}
			}
			
			lastPos = pos;
			start = source.substring(lastPos).indexOf("{");
		}

		return null;
	}

	/**
	 * Get the extension for the current editor mode
	 */
	private String getExtensionForMode() {
	 
		switch (languageMode) {
		case ADA:
			return "ada";
		case C_CPP:
			return "c";
		case CLOJURE:
			return "clj";
		case COBOL:
			return "cbl";
		case COFFEE:
			return "coffee";
		case COLDFUSION:
			return "cfm";
		case CSHARP:
			return "cs";
		case CSS:
			return "css";
		case D:
			return "d";
		case ERLANG:
			return "erl";
		case FORTH:
			return "forth";
		case GOLANG:
			return "go";
		case GROOVY:
			return "groovy";
		case HASKELL:
			return "hs";
		case HTML:
			return "html";
		case JAVA:
			return "java";
		case JAVASCRIPT:
			return "js";
		case JSON:
			return "json";
		case JULIA:
			return "jl";
		case LISP:
			return "lisp";
		case LUA:
			return "lua";
		case MATLAB:
			return "m";
		case MYSQL:
			return "sql";
		case OCAML:
			return "ml";
		case PASCAL:
			return "pp";
		case PERL:
			return "pl";
		case PGSQL:
			return "sql";
		case PHP:
			return "php";
		case PLAIN_TEXT:
			return "txt";
		case PROLOG:
			if (availableLanguages == null)
				return "pro";
			return availableLanguages.get("yap") != null ? "yap" : (availableLanguages.get("pro") == null ? "pl" : "pro");
		case PYTHON:
			return "py";
		case R:
			return "r";
		case RUBY:
			return "rb";
		case RUST:
			return "rs";
		case SCALA:
			return "sc";
		case SQL:
			return "sql";
		case TCL:
			return "tcl";
		case TEXT:
			return "txt";
		case VBSCRIPT:
			return "vb";
		case XML:
			return "xml";
		default:
			return null;
		}
	}

	/**
	 * @return the availableLanguages
	 */
	public Map<String, String> getAvailableLanguages() {
		return availableLanguages;
	}

	/**
	 * @param availableLanguages the availableLanguages to set
	 */
	public void setAvailableLanguages(Map<String, String> availableLanguages) {
		this.availableLanguages = availableLanguages;
	}

}