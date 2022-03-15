package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TabBar.Tab;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import edu.ycp.cs.dh.acegwt.client.ace.AceEditor;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorMode;
import edu.ycp.cs.dh.acegwt.client.ace.AceEditorTheme;
import pt.up.fc.dcc.eshu.client.eshugwt.Eshu;
import pt.up.fc.dcc.mooshak.client.form.admin.MooshakWidget;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

/**
 * Editor for diagram edition via code/diagram
 * 
 * @author josepaiva
 */
public class TabbedMultipleEditor extends Composite implements MooshakWidget {

	public enum EditorMode {
		CODE, DIAGRAM, PDF, HTML, QUIZ
	};

	private static EshuMultipleEditorUiBinder uiBinder = GWT.create(EshuMultipleEditorUiBinder.class);

	@UiTemplate("TabbedMultipleEditor.ui.xml")
	interface EshuMultipleEditorUiBinder extends UiBinder<Widget, TabbedMultipleEditor> {
	}

	interface BaseStyle extends CssResource {
		String codeEditor();

		String diagramEditor();
	}

	private List<ValueChangeHandler<MooshakValue>> handlers = new ArrayList<>();

	private boolean isEditing = false;
	private String field = null;
	private EditorMode mode = EditorMode.CODE;
	private AceEditorMode aceMode = AceEditorMode.JAVA;
	private String problemId = null;
	private boolean enabled = false;
	
	private boolean languageEditable = true;
	private byte[] content;

	private int widgetWidth = 500;
	private int widgetHeight = 500;

	@UiField
	BaseStyle style;

	@UiField
	TabPanel panel;

	@UiField
	AceEditor codeEditor;
	
	@UiField
	ResizableHtmlPanel diagramEditorTab;

	@UiField
	public SelectOneListBox<SelectableOption> diagramLanguageSelector;

	@UiField
	Eshu diagramEditor;
	
	@UiField
	StatementViewer statementViewer;

	private OptionFormatter<SelectableOption> formatter = new OptionFormatter<SelectableOption>() {
		public String getLabel(SelectableOption option) {
			return option.getLabel();
		};

		public String getValue(SelectableOption option) {
			return option.getId();
		};
	};

	public TabbedMultipleEditor() {
		initWidget(uiBinder.createAndBindUi(this));
		configureAceEditor();

		final Element ace = codeEditor.getElement().getElementsByTagName("textarea").getItem(0);
		Event.sinkEvents(ace, Event.ONFOCUS | Event.ONBLUR);
		Event.setEventListener(ace, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONFOCUS == event.getTypeInt()) {
					isEditing = true;
				} else if (Event.ONBLUR == event.getTypeInt()) {
					isEditing = false;
					if (enabled)
						scheduleFire(false);
				}
			}
		});

		diagramEditor.initEshu();
		diagramLanguageSelector.setFormatter(formatter);

		final Element eshu = (Element) diagramEditor.getElement().getChild(1).getFirstChild();
		Event.sinkEvents(eshu, Event.ONMOUSEUP);
		Event.setEventListener(eshu, new EventListener() {

			@Override
			public void onBrowserEvent(Event event) {
				if (Event.ONMOUSEUP == event.getTypeInt()) {
					if (enabled)
						scheduleFire(true);
				}
			}
		});

		
		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
            public void onPreviewNativeEvent(NativePreviewEvent pEvent) {
            	if (enabled)
            		return;
            	
                final com.google.gwt.dom.client.Element target = pEvent.getNativeEvent().getEventTarget().cast();

                // block all events targetted at the children of the composite.
                if (DOM.isOrHasChild(diagramEditor.getElement(), target)) {
                    pEvent.cancel();
                }
            }
        });

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				diagramEditorTab.getElement().getStyle().setHeight(widgetHeight, Unit.PX);
				diagramEditorTab.getElement().getStyle().setWidth(widgetWidth, Unit.PX);
				diagramEditor.resize(widgetWidth, widgetHeight);
				statementViewer.getElement().getStyle().setHeight(widgetHeight, Unit.PX);
				statementViewer.getElement().getStyle().setWidth(widgetWidth, Unit.PX);
			}
		});
		
		statementViewer.setHeight(350);

		setTabVisible(panel.getWidgetIndex(diagramEditorTab), false);
		setTabVisible(panel.getWidgetIndex(statementViewer), false);
		panel.selectTab(0);
	}

	/** AceEditor methods **/
	private void configureAceEditor() {
		codeEditor.startEditor();
		codeEditor.setMode(aceMode);
		codeEditor.setTheme(AceEditorTheme.ECLIPSE);
		codeEditor.getElement().getStyle().setHeight(widgetHeight, Unit.PX);
		codeEditor.getElement().getStyle().setWidth(widgetWidth, Unit.PX);
		codeEditor.getElement().setId("fileContent");
	}

	/**
	 * @return the codeEditor
	 */
	public AceEditor getCodeEditor() {
		return codeEditor;
	}

	/**
	 * @return the diagramEditor
	 */
	public Eshu getDiagramEditor() {
		return diagramEditor;
	}

	/**
	 * @return the widgetHeight
	 */
	public int getWidgetHeight() {
		return widgetHeight;
	}

	/**
	 * @param widgetHeight
	 *            the widgetHeight to set
	 */
	public void setWidgetHeight(int widgetHeight) {
		this.widgetHeight = widgetHeight;

		diagramEditorTab.getElement().getStyle().setHeight(widgetHeight, Unit.PX);
		diagramEditorTab.getElement().getStyle().setWidth(widgetWidth, Unit.PX);
		diagramEditor.resize(widgetWidth, widgetHeight);
		codeEditor.setHeight(widgetHeight + "px");
		codeEditor.onResize();
	}

	/**
	 * @return the widgetWidth
	 */
	public int getWidgetWidth() {
		return widgetWidth;
	}

	/**
	 * @param widgetWidth the widgetWidth to set
	 */
	public void setWidgetWidth(int widgetWidth) {
		this.widgetWidth = widgetWidth;

		diagramEditorTab.getElement().getStyle().setHeight(widgetHeight, Unit.PX);
		diagramEditorTab.getElement().getStyle().setWidth(widgetWidth, Unit.PX);
		diagramEditor.resize(widgetWidth, widgetHeight);
		codeEditor.setWidth(widgetWidth + "px");
		codeEditor.onResize();
	}

	public void setMode(EditorMode mode) {
		this.mode = mode;
		
		switch (this.mode) {
		case DIAGRAM:
			setTabVisible(panel.getWidgetIndex(codeEditor), true);
			setTabVisible(panel.getWidgetIndex(diagramEditorTab), true);
			setTabVisible(panel.getWidgetIndex(statementViewer), false);
			panel.selectTab(panel.getWidgetIndex(codeEditor));
			break;
		case PDF:
			setTabVisible(panel.getWidgetIndex(codeEditor), false);
			setTabVisible(panel.getWidgetIndex(diagramEditorTab), false);
			setTabVisible(panel.getWidgetIndex(statementViewer), true);
			panel.selectTab(panel.getWidgetIndex(statementViewer));
			break;
		case HTML:
			setTabVisible(panel.getWidgetIndex(codeEditor), true);
			setTabVisible(panel.getWidgetIndex(diagramEditorTab), false);
			setTabVisible(panel.getWidgetIndex(statementViewer), true);
			panel.selectTab(panel.getWidgetIndex(codeEditor));
			break;
		
		default:
			setTabVisible(panel.getWidgetIndex(codeEditor), true);
			setTabVisible(panel.getWidgetIndex(diagramEditorTab), false);
			setTabVisible(panel.getWidgetIndex(statementViewer), false);
			panel.selectTab(panel.getWidgetIndex(codeEditor));
			break;
		}
		
	}

	/**
	 * @return the mode
	 */
	public EditorMode getMode() {
		return mode;
	}

	public void setMode(AceEditorMode mode) {
		codeEditor.setMode(mode);
		aceMode = mode;
	}

	/**
	 * @param problemId the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	@Override
	public void setValue(MooshakValue value) {
		setValue(value, false);
	}

	/**
	 * @return the languageEditable
	 */
	public boolean isLanguageEditable() {
		return languageEditable;
	}

	/**
	 * @param languageEditable the languageEditable to set
	 */
	public void setLanguageEditable(boolean languageEditable) {
		if (this.languageEditable == languageEditable)
			return;
		
		if (languageEditable) 
			try {
				codeEditor.setText(new String(content));
			} catch (IllegalArgumentException e) {
				Logger.getLogger("").severe(e.getMessage());
				codeEditor.setText("");
			}
		else
			codeEditor.setText("");
	
		codeEditor.setReadOnly(!languageEditable);
		
		this.languageEditable = languageEditable;
	}

	@Override
	public void setValue(MooshakValue value, boolean fireEvents) {
		enabled = true;
		field = value.getField();
		content = value.getContent();

		if (field == null || !field.equalsIgnoreCase("pdf")) {
			try {
				if (languageEditable)
					codeEditor.setValue(new String(value.getContent()));
				else
					codeEditor.setValue("");
				
				if (enabled)
					enableEditing();
			} catch (Exception e) {
				codeEditor.setValue("Content preview not available");
				disableEditing();
			}
		} else
			disableEditing();

		if (mode.equals(EditorMode.DIAGRAM)) {
			if (codeEditor.getText() != null && !codeEditor.getText().isEmpty()) {
				diagramEditor.importGraphEshu(codeEditor.getText());
				diagramEditor.redraw();
			}
		} else if (mode.equals(EditorMode.HTML) && problemId != null)
			statementViewer.setHTMLStatement(new String(value.getContent()), problemId);
		else if (mode.equals(EditorMode.PDF) && problemId != null)
			statementViewer.setPDFStatement(problemId);
		else 
			diagramEditor.clear();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(final ValueChangeHandler<MooshakValue> handler) {

		final int size = handlers.size();
		handlers.add(handler);
		return new HandlerRegistration() {

			@Override
			public void removeHandler() {
				handlers.remove(size);
			}
		};
	}

	public void fireEvent(ValueChangeEvent<MooshakValue> event) {
		for (ValueChangeHandler<MooshakValue> valueChangeHandler : handlers) {
			valueChangeHandler.onValueChange(event);
		}
	}

	public boolean isEditing() {
		return isEditing;
	}

	@Override
	public MooshakValue getValue() {
		if (languageEditable)
			return new MooshakValue(field, "toBeSetAfter", codeEditor.getValue().getBytes());
		else
			return new MooshakValue(field, "toBeSetAfter", content);
	}

	public void disableEditing() {
		enabled=false;
		codeEditor.setReadOnly(!enabled);
		showDiagramOptions(enabled);
	}

	public void enableEditing() {
		enabled=true;
		
		if (mode == EditorMode.CODE)
			codeEditor.setReadOnly(!enabled);
		else if (mode == EditorMode.DIAGRAM)
			showDiagramOptions(enabled);
	}

	public String getText() {
		return codeEditor.getText();
	}

	public void setField(String field) {
		this.field = field;
	}

	public void setTabVisible(int tabIndex, boolean showTab) {
		Tab tabObject = panel.getTabBar().getTab(tabIndex);
		if (tabObject == null) {
			return;
		}

		if (tabObject instanceof Composite) {
			((Composite) tabObject).setVisible(showTab);
		}
	}
	
	private Timer fireTimer = null;

	/**
	 * Schedule fire events
	 */
	protected void scheduleFire(final boolean diagram) {
		
		if (fireTimer != null)
			return;
		fireTimer = new Timer() {
			
			@Override
			public void run() {
				if (diagram) {
					String graph = diagramEditor.exportGraphEshu();
					codeEditor.setText(graph);
					codeEditor.setValue(graph);
				} else {
					if (mode.equals(EditorMode.DIAGRAM)) {
						diagramEditor.importGraphEshu(codeEditor.getText());
						diagramEditor.redraw();
					} else if (mode.equals(EditorMode.HTML)) {
						statementViewer.setHTMLStatement(codeEditor.getText(), problemId);
					}
				}
				fireEvent(new ValueChangeEvent<MooshakValue>(getValue()) {
				});
				fireTimer = null;
			}
		};
		fireTimer.schedule(500);
	}

	public void showDiagramOptions(boolean b) {
		diagramEditor.showToolbar(b);
	}

	public void redisplay() {
		codeEditor.redisplay();
		diagramEditor.redraw();
	}

	public void createEshu(ConfigInfo eshuConfig) {
		this.diagramEditor.resetEshu();
		Map<String, String> images = eshuConfig.getImagesSVG();
		for (Entry<String, String> image : images.entrySet()) {
			this.diagramEditor.setImageSVG(image.getKey(), image.getValue());
		}

		this.diagramEditor.createNodeTypeArrowToolbar();

		/*int width = Integer.parseInt(eshuConfig.getEditorStyle().get("width"));
		int height = Integer.parseInt(eshuConfig.getEditorStyle().get("height"));
		this.diagramEditor.resize(width, height);*/

		boolean bool = Boolean.parseBoolean(eshuConfig.getEditorStyle().get("gridVisible"));
		this.diagramEditor.setGridVisible(bool);
		this.diagramEditor.setGridLineColor(eshuConfig.getEditorStyle().get("gridColorLine"));
		this.diagramEditor.setToolbarBorderWidth(eshuConfig.getToolbarStyle().get("borderWidth"));
		this.diagramEditor.setToolbarBorderColor(eshuConfig.getToolbarStyle().get("borderColor"));
		this.diagramEditor.setToolbarBackgroundColor(eshuConfig.getToolbarStyle().get("background"));
		this.diagramEditor.createNodeTypes(eshuConfig.getNodeTypes());
		this.diagramEditor.createEdgeType(eshuConfig.getEdgeTypes());
		this.diagramEditor.setPosition(eshuConfig.getToolbarStyle().get("position"));
		this.diagramEditor.setSyntaxValidation(eshuConfig.getSyntaxValidation());
		this.diagramEditor.showToolbar(true);
		
		if (codeEditor.getText() != null && !codeEditor.getText().isEmpty()) {
			diagramEditor.importGraphEshu(codeEditor.getText());
			diagramEditor.redraw();
		}
	}

	public void setDiagramLanguageOptions(List<SelectableOption> options) {
		diagramLanguageSelector.setSelections(options);

		if (options.size() > 0) {
			diagramLanguageSelector.setSelectedValue(options.get(0));
		}
	}
}
