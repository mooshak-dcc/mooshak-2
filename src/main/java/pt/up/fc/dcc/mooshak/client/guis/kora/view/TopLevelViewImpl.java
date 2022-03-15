package pt.up.fc.dcc.mooshak.client.guis.kora.view;

import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.eshu.client.eshugwt.Eshu;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.client.widgets.OkPopupOverlay;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

public class TopLevelViewImpl extends Composite implements TopLevelView {

	private static final int SECONDS_IN_MILLIS = 1000;
	private static final int MINUTES_IN_MILLIS = 60 * SECONDS_IN_MILLIS;
	private static final int HOURS_IN_MILLIS = 60 * MINUTES_IN_MILLIS;

	private static final int STORAGE_UPDATE_TIMER = 5 * SECONDS_IN_MILLIS;

	private static final ICPCConstants CONSTANTS = GWT.create(ICPCConstants.class);
	private static final EnkiConstants ENKI_CONSTANTS = GWT.create(EnkiConstants.class);
	
	private static KoraUiBinder uiBinder = GWT.create(KoraUiBinder.class);

	@UiTemplate("TopLevelView.ui.xml")
	interface KoraUiBinder extends UiBinder<Widget, TopLevelViewImpl> {
	}

	/* UI fields */

	@UiField
	HTMLPanel panel;

	@UiField
	HTMLPanel commandsPanel;

	@UiField
	Eshu editor;
	
	@UiField
	TextArea observations;

	private Presenter presenter = null;

	/* Necessary fields to handle submissions and quota limits */
	private Timer submitTimer = null;
	private long submitResetTime = -1;
	private int remainingSubmits = -1;

	private int missingObs = 0;

	private boolean showingFeedback = false;

	/* Your fields */
	private Map<String, String> availableLanguages;

	public TopLevelViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));

		// setup timer for submission quota limits
		configureSubmitTimer();

		// schedule caching for graph
		scheduleStoreGraph();

	}

	/* Implementation of exposed methods */

	@Override
	public void setObservations(String obs) {
		observations.setText(obs);
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
					presenter.saveToLocalStorage(editor.exportGraphEshu());
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

	@UiField
	CustomImageButton clear;

	@UiHandler({ "clear" })
	void clear(ClickEvent event) {
		new OkCancelDialog(ENKI_CONSTANTS.clearDiagramConfirmation()) {
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

}
