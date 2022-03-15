package pt.up.fc.dcc.mooshak.client.gadgets.diagrameditor;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.eshu.client.eshugwt.Eshu;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.utils.FullscreenUtils;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.OkPopupOverlay;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

public class DiagramEditorViewImpl extends ResizeComposite implements DiagramEditorView {

	private static final int SECONDS_IN_MILLIS = 1000;
	private static final int MINUTES_IN_MILLIS = 60 * SECONDS_IN_MILLIS;
	private static final int HOURS_IN_MILLIS = 60 * MINUTES_IN_MILLIS;
	
	private static final int STORAGE_UPDATE_TIMER = 5 * 1000;

	private static DiagramEditorViewUiBinder uiBinder = GWT.create(DiagramEditorViewUiBinder.class);

	@UiTemplate("DiagramEditorView.ui.xml")
	interface DiagramEditorViewUiBinder extends UiBinder<Widget, DiagramEditorViewImpl> {
	}

	private ICPCConstants constants = GWT.create(ICPCConstants.class);
	private EnkiConstants enkiConstants = GWT.create(EnkiConstants.class);

	private Presenter presenter = null;

	@UiField
	ResizableHtmlPanel panel;

	@UiField
	HTMLPanel commandsPanel;

	@UiField
	Eshu editor;

	private Timer submitTimer = null;
	private long submitResetTime = -1;
	private int remainingSubmits = -1;

	private int missingObs = 0;

	private Map<String, String> availableLanguages;
	
	private Timer updateStorageTimer = null;
	
	private boolean showingFeedback = false;

	public DiagramEditorViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		configureSubmitTimer();
		//editor.hideDivNote();

		panel.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				editor.resize(event.getWidth() - 20, event.getHeight() - 20);
				editor.redraw();
			}
		});
		panel.onResize();

		Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
			public void onPreviewNativeEvent(NativePreviewEvent event) {
				NativeEvent ne = event.getNativeEvent();
				
				if (ne.getKeyCode() == 27 || ne.getKeyCode() == 122)
					exitFullscreen(null);
			}
		});
		
		updateStorageTimer = new Timer() {
			
			@Override
			public void run() {
				if (!showingFeedback)
					presenter.saveToLocalStorage(editor.exportGraphEshu());
			}
		};
		updateStorageTimer.scheduleRepeating(STORAGE_UPDATE_TIMER);
	}

	/**
	 * Resolving issues when clicking inside draggable parent
	 */
	public void setDraggableParentProperties() {
		editor.addDomHandler(new MouseMoveHandler() {

			@Override
			public void onMouseMove(MouseMoveEvent event) {
				event.stopPropagation();
			}
		}, MouseMoveEvent.getType());

		editor.addDomHandler(new MouseDownHandler() {

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
					submit.setTitle(submit.getTitle() + "\n\n" + constants.submit() + " " + tooltip);
			}
		};

		submitTimer.scheduleRepeating(SECONDS_IN_MILLIS);
	}

	/* Command buttons */

	@UiField
	CustomImageButton submit;

	@UiHandler({ "submit" })
	void submit(ClickEvent event) {
		presenter.onDiagramEvaluate(true);
	}

	@UiField
	CustomImageButton clear;

	@UiHandler({ "clear" })
	void clear(ClickEvent event) {
		new OkCancelDialog(enkiConstants.clearDiagramConfirmation()) {
		}.addDialogHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				editor.clear();
			}
		});
	}

	@UiField
	CustomImageButton copy;

	@UiHandler({ "copy" })
	void copy(ClickEvent event) {
		editor.copy();
	}

	@UiField
	CustomImageButton paste;

	@UiHandler({ "paste" })
	void paste(ClickEvent event) {
		editor.paste();
	}

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

	@Override
	public void setSubmitTooltip(int remaining, long resetTime) {
		submitResetTime = resetTime;
		remainingSubmits = remaining;
	}

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
					"An observation has been received " + 
					"and the client was not waiting for it.");
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

	@Override
	public void setLanguages(Map<String, String> languages) {
		this.availableLanguages = languages;
	}

	@Override
	public Map<String, String> getLanguages() {
		return availableLanguages;
	}

}
