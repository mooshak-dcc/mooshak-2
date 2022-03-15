package pt.up.fc.dcc.mooshak.client.gadgets.quiz;

import static com.google.gwt.safehtml.shared.SafeHtmlUtils.fromTrustedString;

import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.resources.client.TextResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.gadgets.quiz.resources.Resources;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.utils.FullscreenUtils;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.client.widgets.TimeBar;

/**
 * View of Quiz in the Model-View Presenter architectural pattern
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class QuizViewImpl extends ResizeComposite implements QuizView {

	private static final int SECONDS_IN_MILLIS = 1000;
	private static final int MINUTES_IN_MILLIS = 60 * SECONDS_IN_MILLIS;
	private static final int HOURS_IN_MILLIS = 60 * MINUTES_IN_MILLIS;

	private static final int STORAGE_UPDATE_TIMER = 5 * SECONDS_IN_MILLIS;

	private static final ICPCConstants CONSTANTS = GWT.create(ICPCConstants.class);

	private static QuizViewUiBinder uiBinder = GWT.create(QuizViewUiBinder.class);
	
	static RegExp imgRegExp = RegExp.compile("<img([^>]*)src=(\"|')([^\"']*)(\"|')([^>]*)>", "gi");
	
	private static final String IMG_URL_PREFIX = "image";

	@UiTemplate("QuizView.ui.xml")
	interface QuizViewUiBinder extends UiBinder<Widget, QuizViewImpl> {
	}

	/* UI fields */

	@UiField
	ResizableHtmlPanel panel;

	@UiField
	HTMLPanel commandsPanel;

	@UiField
	HTML quiz;
	
	@UiField
	TimeBar timeBar;

	private Presenter presenter = null;

	/* Necessary fields to handle submissions and quota limits */
	private Timer submitTimer = null;
	private long submitResetTime = -1;
	private int remainingSubmits = -1;

	private int missingObs = 0;

	/* Your fields */

	public QuizViewImpl() {

		initWidget(uiBinder.createAndBindUi(this));

		injectJavaScriptFiles();
		// setup timer for submission quota limits
		configureSubmitTimer();

		// schedule caching for graph
		scheduleStoreAnswers();

		// handle resize
		panel.addResizeHandler(new ResizeHandler() {

			@Override
			public void onResize(ResizeEvent event) {
				// do some resizing
			}
		});
		panel.onResize();
	}

	/* Implementation of exposed methods */

	@Override
	public void increaseQuizWaitingEvaluation() {
		missingObs++;
		submit.setEnabled(false);
	}

	@Override
	public void decreaseQuizWaitingEvaluation() {
		if (missingObs > 0) {
			missingObs--;
			submit.setEnabled(true);
		} else
			Logger.getLogger("").log(Level.SEVERE,
					"An observation has been received " + "and the client was not waiting for it.");
	}

	@Override
	public String getAnswersAsJSON() {
		String json = exportQuiz(); // get answers as JSON
		return json;
	}

	@Override
	public void importAnswersAsJson(String json) {
		// import answers to quiz
		// editor.importAnswers(json);
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

	/* HELPERS */

	/**
	 * Schedule updates to local storage of the graph
	 */
	private void scheduleStoreAnswers() {

		new Timer() {

			@Override
			public void run() {
				String json = null; // get answers as JSON
				if (json != null)
					presenter.saveToLocalStorage(json);
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
		String json = exportQuiz(); // get answers as JSON
		presenter.onQuizEvaluate(json, true);
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

	/**
	 * Inject JS files stored as resources in script elements of top window
	 */
	private void injectJavaScriptFiles() {
		
		
		// CSS resources
		for (TextResource cssResource : Arrays.asList(Resources.INSTANCE.quizCSS())) {
			StyleInjector.inject(cssResource.getText());
		}
		
		StyleInjector.inject(Resources.INSTANCE.assetsCss().getText());
		
		// JS resources
		for (TextResource jsResource : Arrays.asList(Resources.INSTANCE.quizJS())) {
			ScriptInjector.fromString(jsResource.getText())
				.setWindow(ScriptInjector.TOP_WINDOW).inject();
		}
		
		//  correct resources
//		for (TextResource jsResource : Arrays.asList(Resources.INSTANCE.correctIMG())) {
//			ScriptInjector.fromString(jsResource.getText())
//				.setWindow(ScriptInjector.TOP_WINDOW).inject();
//		}
//				

		
		
	}

	public native void start() /*-{
		$wnd.onload()

	}-*/;
	
	public native String exportQuiz()/*-{
		console.log($wnd.getQuizzes());
		return $wnd.getQuizzes();

	}-*/;

	public void createQuizzes(String html, String problemId) {
		
		html = imgRegExp.replace(html, "<img$1src=$2" + IMG_URL_PREFIX + "/" + problemId + "/$3$4$5>");
		
		
		SafeHtmlBuilder builder = new SafeHtmlBuilder()
				.append(fromTrustedString(html));
			
		quiz.setHTML(builder.toSafeHtml());
		start();
		Date current = new Date();
		setDates(current, new Date(current.getTime() + 60*1000), current);
	}
	
//	public void createQuizzesFinal(String html) {
//		SafeHtmlBuilder builder = new SafeHtmlBuilder()
//				.append(fromTrustedString(html));
//		
//		quiz.
//		quiz.setHTML(builder.toSafeHtml());
//		start();
//		Date current = new Date();
//		setDates(current, new Date(current.getTime() + 60*1000), current);
//	}
	
	public void setDates(Date start, Date end, Date current) {
		timeBar.setTimerScheduleRepeating(1);
		timeBar.setWidth("500px");
		timeBar.initValues(start, end, current);
		
	}
	
	

}
