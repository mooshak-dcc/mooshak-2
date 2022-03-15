package pt.up.fc.dcc.mooshak.client.gadgets.kora;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox;
import pt.up.fc.dcc.mooshak.client.utils.SelectOneListBox.OptionFormatter;

import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.kora.ConfigInfo;

import pt.up.fc.dcc.eshu.client.eshugwt.Eshu;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.utils.FullscreenUtils;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.OkPopupOverlay;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

/**
 * View of Kora in the Model-View Presenter architectural pattern
 * 
 * @author heldercorreia
 */
public class KoraViewImpl extends ResizeComposite implements KoraView {

	private static final int SECONDS_IN_MILLIS = 1000;
	private static final int MINUTES_IN_MILLIS = 60 * SECONDS_IN_MILLIS;
	private static final int HOURS_IN_MILLIS = 60 * MINUTES_IN_MILLIS;

	private static final int STORAGE_UPDATE_TIMER = 5 * SECONDS_IN_MILLIS;

	private static final ICPCConstants CONSTANTS = GWT.create(ICPCConstants.class);
	private static final EnkiConstants ENKI_CONSTANTS = GWT.create(EnkiConstants.class);

	private static DiagramEditorViewUiBinder uiBinder = GWT.create(DiagramEditorViewUiBinder.class);

	private EnkiConstants constants = GWT.create(EnkiConstants.class); // ***********attention

	@UiTemplate("KoraView.ui.xml")
	interface DiagramEditorViewUiBinder extends UiBinder<Widget, KoraViewImpl> {
	}

	/* UI fields */

	@UiField
	ResizableHtmlPanel panel;

	@UiField
	HTMLPanel commandsPanel;

	@UiField
	Eshu editor;

	@UiField
	SelectOneListBox<SelectableOption> skeleton;

	private Presenter presenter = null;

	/* Necessary fields to handle submissions and quota limits */
	private Timer submitTimer = null;
	private long submitResetTime = -1;
	private int remainingSubmits = -1;

	private int missingObs = 0;

	private boolean showingFeedback = false;

	/* Your fields */
	public Map<String, String> availableLanguages;

	private String defaultLanguage;

	public KoraViewImpl() {

		initWidget(uiBinder.createAndBindUi(this));

		// setup timer for submission quota limits
		configureSubmitTimer();

		// schedule caching for graph
		scheduleStoreGraph();

		this.editor.initEshu();

		skeleton.setFormatter(formatter);
		// handle resize
		panel.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				editor.resize(event.getWidth() - 20, event.getHeight() - 20);
				editor.redraw();
			}
		});
		panel.onResize();

	}

	private OptionFormatter<SelectableOption> formatter = new OptionFormatter<SelectableOption>() {
		public String getLabel(SelectableOption option) {
			return option.getLabel();
		};

		public String getValue(SelectableOption option) {
			return option.getId();
		};
	};

	/* Implementation of exposed methods */

	@Override
	public void increaseProgramWaitingEvaluation() {
		missingObs++;
		submit.setEnabled(false);
	}

	@Override
	public void decreaseProgramWaitingEvaluation() {
		if (missingObs > 0) {
			missingObs--;
			submit.setEnabled(true);
		} else
			Logger.getLogger("").log(Level.SEVERE,
					"An observation has been received " + "and the client was not waiting for it.");
	}

	@Override
	public String getGraphAsJSON() {
		return editor.exportGraphEshu();
	}

	@Override
	public void importGraphAsJson(String json) {
		editor.importGraphEshu(json);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				editor.redraw();
			}
		});

		showingFeedback = false;
	}

	@Override
	public void importGraphDiff(String json) {

		OkPopupOverlay popup = new OkPopupOverlay();
		popup.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				presenter.revertToLastSubmission();
			}
		});

		panel.add(popup);
		editor.importDiff(json);
		showingFeedback = true;
	}

	/* GETTERS & SETTERS */

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public void setSubmitTooltip(int remaining, long resetTime) {
		submitResetTime = resetTime;
		remainingSubmits = remaining;
	}

	@Override
	public void setLanguages(Map<String, String> languages) {
		this.availableLanguages = languages;

	}

	/* HELPERS */

	/**
	 * Schedule updates to local storage of the graph
	 */
	private void scheduleStoreGraph() {

		new Timer() {

			@Override
			public void run() {
				if (!showingFeedback)
					presenter.saveToLocalStorage(editor.exportGraphEshu(), skeleton.getSelectedOption().getId());
			}
		}.scheduleRepeating(STORAGE_UPDATE_TIMER);
	}

	/**
	 * Calculates a string in countdown timer form for a given diff
	 * 
	 * @param diff
	 * @return
	 */
	private String getCountdownLabel(long diff) {
		String label = "";

		int hours = (int) (diff / HOURS_IN_MILLIS);
		diff = diff % HOURS_IN_MILLIS;
		int minutes = (int) (diff / MINUTES_IN_MILLIS);
		diff = diff % MINUTES_IN_MILLIS;
		int seconds = (int) (diff / SECONDS_IN_MILLIS);

		if (hours < 10)
			label += "0" + hours;
		else
			label += hours;
		label += ":";
		if (minutes < 10)
			label += "0" + minutes;
		else
			label += minutes;
		label += ":";
		if (seconds < 10)
			label += "0" + seconds;
		else
			label += seconds;

		return label;
	}

	/**
	 * Configures the timer to update the submit tooltip
	 */
	private void configureSubmitTimer() {
		submitTimer = new Timer() {

			@Override
			public void run() {
				String tooltip = "";
				if (remainingSubmits >= 0) {
					tooltip += remainingSubmits;
				}

				if (submitResetTime >= 0) {
					tooltip += "/";
					long diff = submitResetTime - new Date().getTime();

					if (diff <= 0) {
						tooltip += "00:00:00";
						submit.setTitle(tooltip);
						presenter.getSubmissionsTransactionsData();
						return;
					}

					tooltip += getCountdownLabel(diff);
				}

				if (tooltip.equals(""))
					submit.setTitle(submit.getTitle());
				else
					submit.setTitle(submit.getTitle() + "\n\n" + CONSTANTS.submit() + " " + tooltip);
			}
		};

		submitTimer.scheduleRepeating(SECONDS_IN_MILLIS);
	}

	/* COMMAND BUTTONS */

	@UiField
	CustomImageButton submit;

	@UiHandler({ "submit" })
	void submit(ClickEvent event) {
		presenter.onDiagramEvaluate(true);
	}

	/*
	 * @UiField CustomImageButton clear;
	 * 
	 * @UiHandler({ "clear" }) void clear(ClickEvent event) { new
	 * OkCancelDialog(ENKI_CONSTANTS.clearDiagramConfirmation()) {
	 * }.addDialogHandler(new ClickHandler() {
	 * 
	 * @Override public void onClick(ClickEvent event) { editor.clear(); } }); }
	 * 
	 * @UiField CustomImageButton copy;
	 * 
	 * @UiHandler({ "copy" }) void copy(ClickEvent event) { editor.copy(); }
	 * 
	 * @UiField CustomImageButton paste;
	 * 
	 * @UiHandler({ "paste" }) void paste(ClickEvent event) { editor.paste(); }
	 */

	@UiField
	CustomImageButton fullscreen;

	@UiHandler({ "fullscreen" })
	void fullscreen(ClickEvent event) {
		Style style = panel.getElement().getStyle();

		style.setPosition(Position.FIXED);
		style.setZIndex(20);
		style.setHeight(100, Unit.PCT);
		style.setWidth(100, Unit.PCT);
		style.setTop(0, Unit.PX);
		style.setRight(0, Unit.PX);
		style.setBackgroundColor("#CCC");
		FullscreenUtils.requestFullscreenWithInput();

		exitFullscreen.getElement().getStyle().setDisplay(Display.BLOCK);
		fullscreen.getElement().getStyle().setDisplay(Display.NONE);

		panel.onResize();

		Timer checkFullscreen = new Timer() {

			@Override
			public void run() {
				if (!FullscreenUtils.isFullscreen()) {
					exitFullscreen(null);
					cancel();
				}
			}
		};
		checkFullscreen.scheduleRepeating(300);
	}

	@UiField
	CustomImageButton exitFullscreen;

	@UiHandler({ "exitFullscreen" })
	void exitFullscreen(ClickEvent event) {
		Style style = panel.getElement().getStyle();

		style.setPosition(Position.RELATIVE);
		style.setBackgroundColor("transparent");
		FullscreenUtils.exitFullscreen();

		exitFullscreen.getElement().getStyle().setDisplay(Display.NONE);
		fullscreen.getElement().getStyle().setDisplay(Display.BLOCK);

		panel.onResize();
	}

	public void createEshu(ConfigInfo eshuConfig) {
//		 Window.alert("000 "+eshuConfig.getNodeTypes());
		this.editor.resetEshu();
		Map<String, String> images = eshuConfig.getImagesSVG();
		for (Entry<String, String> image : images.entrySet()) {
			this.editor.setImageSVG(image.getKey(), image.getValue());
//			 Window.alert(image.getValue());
//			 System.out.println(image.getValue());
//			 System.out.println();
//			 System.out.println("34555555555555555555555555555555555555555");
		}

		this.editor.createNodeTypeArrowToolbar();

		int width = Integer.parseInt(eshuConfig.getEditorStyle().get("width"));
		int height = Integer.parseInt(eshuConfig.getEditorStyle().get("height"));
		this.editor.resize(width, height);

		boolean bool = Boolean.parseBoolean(eshuConfig.getEditorStyle().get("gridVisible"));
		this.editor.setGridVisible(bool);

		this.editor.setGridLineColor(eshuConfig.getEditorStyle().get("gridColorLine"));

		this.editor.setToolbarBorderWidth(eshuConfig.getToolbarStyle().get("borderWidth"));

		this.editor.setToolbarBorderColor(eshuConfig.getToolbarStyle().get("borderColor"));

		this.editor.setToolbarBackgroundColor(eshuConfig.getToolbarStyle().get("background"));

		this.editor.createNodeTypes(eshuConfig.getNodeTypes());

		this.editor.createEdgeType(eshuConfig.getEdgeTypes());

		this.editor.setPosition(eshuConfig.getToolbarStyle().get("position"));

		this.editor.setSyntaxValidation(eshuConfig.getSyntaxValidation());

		this.editor.showToolbar(true);

		panel.onResize();
	}

	private void setImagesEshu(Map<String, String> images) {
		for (Entry<String, String> image : images.entrySet()) {
			this.editor.setImageSVG(image.getKey(), image.getValue());
		}
	}

	@Override
	public void setSkeletonOptions(List<SelectableOption> options) {
		// options.add(0, new SelectableOption("", constants.noSkeleton()));
		skeleton.setSelections(options);

		if (options.size() > 0) {
			skeleton.setSelectedValue(options.get(0));
		}

	}

	@UiHandler("skeleton")
	void skeleton(ChangeEvent e) {
		if (presenter != null)
			presenter.onSkeletonSelectedChanged(skeleton.getSelectedOption().getId());
	}

	public String getValueSkeleton() {
		return skeleton.getSelectedOption().getId();
	}

	/**
	 * @return the defaultLanguage
	 */
	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	/**
	 * @param defaultLanguage the defaultLanguage to set
	 */
	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}

	@Override
	public boolean setSelectedLanguage(String id) {

		for (SelectableOption option : skeleton.getSelections()) {
			if (option.getId().equals(id)) {
				skeleton.setSelectedValue(option);
				return true;
			}
		}
		return false;
	}

}
