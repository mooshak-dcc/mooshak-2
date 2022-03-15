package pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations;

import java.util.ArrayList;
import java.util.List;

import pt.up.fc.dcc.mooshak.client.utils.FullscreenUtils;
import pt.up.fc.dcc.mooshak.client.utils.ProgramError;
import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class ProgramObservationsViewImpl extends ResizeComposite
	implements ProgramObservationsView {

	private static ProgramObservationsViewUiBinder uiBinder = GWT
			.create(ProgramObservationsViewUiBinder.class);

	@UiTemplate("ProgramObservationsView.ui.xml")
	interface ProgramObservationsViewUiBinder extends
			UiBinder<Widget, ProgramObservationsViewImpl> {
	}
	
	private Presenter presenter = null;

	@UiField
	ResizableHtmlPanel panel;
	
	@UiField
	HTML observations;
	
	public ProgramObservationsViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		setDraggableParentProperties();
	}
	
	/**
	 * Resolving issues when clicking inside draggable parent
	 */
	public void setDraggableParentProperties() {
		observations.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				event.stopPropagation();
			}
		}, MouseMoveEvent.getType());
		
		observations.addDomHandler(new MouseDownHandler() {
			
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

	private static RegExp errorPattern = RegExp.compile(":(\\d+):(.*)$","gm");
	private static RegExp csErrorPattern = RegExp.compile("\\((\\d+)[^\\)]+\\):(.*)$","gm");
	private static RegExp filePattern = RegExp.compile("(.*/)(?:$|(.+?)(?:(\\.[a-zA-Z0-9]*)))","gm");
	
	public void setObservations(String text) {
		clearObservations();
		addObservations(text);
	}

	@Override
	public void addObservations(String text) {
		MatchResult result = errorPattern.exec(text);
		if (result == null)
			result = csErrorPattern.exec(text);
		
		List<ProgramError> errors = new ArrayList<ProgramError>();
		
		while(result != null) {
			int row = Integer.parseInt(result.getGroup(1))-1;
			int column = 0;
			String error = result.getGroup(2);
			
			errors.add(new ProgramError(error, row, column));
			
			result = errorPattern.exec(text);
		} 
		
		if (!errors.isEmpty()) {
		
			text = "";
			for (ProgramError error : errors) {
				result = filePattern.exec(error.getDescription());
				
				while(result != null) {
					String path = result.getGroup(1);
					error.setDescription(error.getDescription().replace(path, ""));
					result = filePattern.exec(error.getDescription());
				}
				
				text += error.getDescription() + "\n";
			}
		}

		presenter.setErrorList(errors);
		
		String formattedText;
		formattedText = "<pre>"+text+"</pre>";

		observations.setHTML(observations.getHTML()+formattedText);
		
		presenter.onSetObservations();
	}

	@Override
	public void addStatus(String status) {
		String color;
		
		log("status="+status);
		if(status.toUpperCase().indexOf("ACCEPTED") != -1) 
			color="green";
		else
			color = "red";
		
		String formattedStatus = "<font color=\""+color+"\">"+status+"</font>";
		log("1"+observations.getHTML());
		observations.setHTML(observations.getHTML() + formattedStatus);
	}
	
	public native void log(String message) /*-{
		$wnd.console.log(message);
	}-*/;
	
	@Override
	public void addFeedback(String feedback) {
		observations.setHTML(observations.getHTML() + feedback);
	}

	String observationsHtml = ""; 
	
	@Override
	public void clearObservations() {
		observations.setText("");
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
	
}
