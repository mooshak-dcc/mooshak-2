package pt.up.fc.dcc.mooshak.client.gadgets.programeditor;

import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.client.widgets.FileContent;
import pt.up.fc.dcc.mooshak.client.widgets.HasFileContent;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;

public class ProgramEditorViewImpl extends ResizeComposite implements
		ProgramEditorView, HasFileContent {
	
	private static final int STORAGE_UPDATE_TIMER = 5 * 1000;
	
	private EnkiConstants constants = GWT.create(EnkiConstants.class);
	
	private static ProgramEditorUiBinder uiBinder = GWT
			.create(ProgramEditorUiBinder.class);

	@UiTemplate("ProgramEditorView.ui.xml")
	interface ProgramEditorUiBinder extends UiBinder<Widget, ProgramEditorViewImpl> {
	}
	
	private Presenter presenter = null;
	
	@UiField
	ResizableHtmlPanel panel;
	
	@UiField
	FileContent editor;
	
	@UiField
	SelectOneListBox<SelectableOption> skeleton;
	
	private OptionFormatter<SelectableOption> formatter = 
			new OptionFormatter<SelectableOption>() {
		 public String getLabel(SelectableOption option) { 
			 return option.getLabel(); 
			 };
         public String getValue(SelectableOption option)  { 
        	 return option.getId(); 
        	 };
	};
	
	private boolean isResponsive = false;
	
	private Timer updateStorageTimer = null;

	private String defaultLanguage;

	public ProgramEditorViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		skeleton.setFormatter(formatter);
		editor.setTheme(AceEditorTheme.ECLIPSE);
		editor.setForm(this);

		panel.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				if (isResponsive)
					resizePanel(event);
			}
		});

		setDraggableParentProperties();
		
		updateStorageTimer = new Timer() {
			
			@Override
			public void run() {
				presenter.saveToLocalStorage(editor.getProgramName(), 
						editor.getProgramCode());
			}
		};
		updateStorageTimer.scheduleRepeating(STORAGE_UPDATE_TIMER);
	}

	
	/**
	 * Resolving issues when clicking inside draggable parent
	 */
	public void setDraggableParentProperties() {
		skeleton.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				event.stopPropagation();
			}
		}, MouseMoveEvent.getType());
		
		skeleton.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
			}
		}, MouseDownEvent.getType());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setProgramCode(byte[] result) {
		editor.setFileContent(result);
	}

	@Override
	public void setProgramName(String name) {
		editor.setFilename(name);
	}

	@Override
	public void clearObservations() {
		presenter.setObservations("");
	}

	@Override
	public byte[] getProgramCode() {
		return editor.getProgramCode();
	}

	@Override
	public String getProgramName() {
		return editor.getProgramName();
	}
	
	@Override
	public AceEditor getEditor() {
		return editor.getEditor();
	}

	/**
	 * @return the isResponsive
	 */
	public boolean isResponsive() {
		return isResponsive;
	}

	/**
	 * @param isResponsive the isResponsive to set
	 */
	@Override
	public void setResponsive(boolean isResponsive) {
		this.isResponsive = isResponsive;
	}

	private void resizePanel(ResizeEvent event) {
		editor.getEditor().setWidth("100%");
		editor.getEditor().setHeight(Math.max(event.getHeight() 
				- editor.getFileHeader().getOffsetHeight() - 20, 0) + "px");
	}

	@Override
	public void setSkeletonOptions(List<SelectableOption> options) {
		options.add(0, new SelectableOption("", constants.noSkeleton()));
		skeleton.setSelections(options);
		
		if (options.size() == 2) {
			skeleton.setSelectedValue(options.get(1));
		}
	}
	
	@UiHandler("skeleton")
	void skeleton(ChangeEvent e) {
		if(presenter != null)
			presenter.onSkeletonSelectedChanged(skeleton.getSelectedOption().getId());
	}


	@Override
	public void onChangeProgramExtension(String extension, boolean askSkeletonUse) {
		
		presenter.updateEditable(extension);
	}


	@Override
	public void setLanguages(Map<String, String> languages) {
		editor.setAvailableLanguages(languages);
		
		if (defaultLanguage != null) {
			for (String lang : languages.values()) {
				
				if (lang.equalsIgnoreCase(defaultLanguage)) {
					editor.setMode(AceEditorMode.valueOf(defaultLanguage.toUpperCase()));
					editor.updateFileName();
					return;
				}
			}
		}
		
		if (editor.getProgramName().equals("") 
				|| languages.get(editor.getProgramExtension()) == null) {
			String lang = languages.values().iterator()
					.next().toUpperCase();
			editor.setMode(AceEditorMode.valueOf(lang));
			editor.updateFileName();
		}
	}


	@Override
	public void setDefaultLanguage(String language) {
		this.defaultLanguage = language;
	}


	@Override
	public void setLanguageEditable(boolean editable) {
		editor.setLanguageEditable(editable);
	}

	@Override
	public boolean isLanguageEditable() {
		return editor.isLanguageEditable();
	}

}
