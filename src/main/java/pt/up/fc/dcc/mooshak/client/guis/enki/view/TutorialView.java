package pt.up.fc.dcc.mooshak.client.guis.enki.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseWheelEvent;
import com.google.gwt.event.dom.client.MouseWheelHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.hydro4ge.raphaelgwt.client.PathBuilder;
import com.hydro4ge.raphaelgwt.client.Raphael;
import com.hydro4ge.raphaelgwt.client.Raphael.Circle;
import com.hydro4ge.raphaelgwt.client.Raphael.Ellipse;
import com.hydro4ge.raphaelgwt.client.Raphael.Path;
import com.hydro4ge.raphaelgwt.client.Raphael.Rect;
import com.hydro4ge.raphaelgwt.client.Raphael.Set;
import com.hydro4ge.raphaelgwt.client.Raphael.Shape;
import com.hydro4ge.raphaelgwt.client.Raphael.Text;

import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.enki.resources.EnkiResources;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.HasTutorial;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;

/**
 * Manages the tutorials to show
 * 
 * @author josepaiva
 */
public class TutorialView {
	
	public enum Direction {
		UP, DOWN, LEFT, RIGHT
	}

	private HasTutorial view;
	private Raphael tutorialPanel = null;

	private List<Set> tutorialParts = new ArrayList<Raphael.Set>();
	private Set currentTutView;
	
	private EnkiConstants constants =
			GWT.create(EnkiConstants.class);

	private Storage storage = Storage.getLocalStorageIfSupported();
	
	private int westContentPanelTopX, westContentPanelTopY, westContentPanelWidth,
		westContentPanelHeigth;
	private int centerContentPanelTopX, centerContentPanelTopY, centerContentPanelWidth,
		centerContentPanelHeigth;
	private int southContentPanelTopX, southContentPanelTopY, southContentPanelWidth,
		southContentPanelHeigth;
	private int eastContentPanelTopX, eastContentPanelTopY, eastContentPanelWidth,
		eastContentPanelHeigth;
	private int eastContentPanelBottomTopX, eastContentPanelBottomTopY, eastContentPanelBottomWidth,
		eastContentPanelBottomHeigth;
	
	private boolean diagramEditor = false;
	private boolean editorSelected = false;
	
	private int imgSlide = -1;
	private Image image = null;
	
	private static TutorialView tutorialView = null;

	private TutorialView() {
	}
	
	public static TutorialView getInstance() {
		if (tutorialView == null)
			tutorialView = new TutorialView();
		return tutorialView;
	}

	public void setTutorialView(HasTutorial view) {
		this.view = view;
	}

	/**
	 * @param diagramEditor the diagramEditor to set
	 */
	public void setDiagramEditor(boolean diagramEditor) {
		this.diagramEditor = diagramEditor;
	}

	/**
	 * @return the editorSelected
	 */
	public boolean isEditorSelected() {
		return editorSelected;
	}

	/**
	 * @param editorSelected the editorSelected to set
	 */
	public void setEditorSelected(boolean editorSelected) {
		this.editorSelected = editorSelected;
	}

	public void showTutorial(Token token) {
		if (tutorialPanel == null) {
			tutorialPanel = view.showTutorialPanel();
			addPanelNavigation();
		} else
			view.showTutorialPanel();
		tutorialParts.clear();
		currentTutView = null;
		
		if (token == null || token.getType() == null) {
			showTutorialTop();
			return;
		}

		switch (token.getType()) {
		case PDF:
			makePdfTutorial();
			break;
		case VIDEO:
			makeVideoTutorial();
			break;
		case PROBLEM:
			makeProblemTutorial();
			break;
		default:
			break;
		}
	}
	
	/**
	 * Updates position of anchor points
	 * @param westPanel
	 * @param centerPanel
	 * @param bottomPanel
	 * @param eastPanel
	 * @param eastBottomPanel
	 */
	public void updateAnchorPoints(Widget westPanel, Widget centerPanel,
			Widget bottomPanel, Widget eastPanel, Widget eastBottomPanel) {
		
		westContentPanelTopX = westPanel.getElement().getAbsoluteLeft();
		westContentPanelTopY = westPanel.getElement().getAbsoluteTop();
		westContentPanelHeigth = westPanel.getOffsetHeight();
		westContentPanelWidth = westPanel.getOffsetWidth();
		
		centerContentPanelTopX = centerPanel.getElement().getAbsoluteLeft();
		centerContentPanelTopY = centerPanel.getElement().getAbsoluteTop();
		centerContentPanelHeigth = centerPanel.getOffsetHeight();
		centerContentPanelWidth = centerPanel.getOffsetWidth();
		
		southContentPanelTopX = bottomPanel.getElement().getAbsoluteLeft();
		southContentPanelTopY = bottomPanel.getElement().getAbsoluteTop();
		southContentPanelHeigth = bottomPanel.getOffsetHeight();
		southContentPanelWidth = bottomPanel.getOffsetWidth();
		
		eastContentPanelTopX = eastPanel.getElement().getAbsoluteLeft();
		eastContentPanelTopY = eastPanel.getElement().getAbsoluteTop();
		eastContentPanelHeigth = eastPanel.getOffsetHeight();
		eastContentPanelWidth = eastPanel.getOffsetWidth();
		
		eastContentPanelBottomTopX = eastBottomPanel.getElement().getAbsoluteLeft();
		eastContentPanelBottomTopY = eastBottomPanel.getElement().getAbsoluteTop();
		eastContentPanelBottomHeigth = eastBottomPanel.getOffsetHeight();
		eastContentPanelBottomWidth = eastBottomPanel.getOffsetWidth();
	
	}

	/**
	 * Binds the mouse wheel and click to navigate in tutorial and adds a timer
	 * to skip tutorial parts while user don't use the mouse
	 */
	private void addPanelNavigation() {

		// high opacity
		String highStr = "{\"opacity\":\"1\"}";
		final JSONObject highObj = (JSONObject) JSONParser.parseStrict(highStr);

		// low opacity
		String lowStr = "{\"opacity\":\"0\"}";
		final JSONObject lowObj = (JSONObject) JSONParser.parseStrict(lowStr);

		// Automatic slide
		final Timer t = new Timer() {

			@Override
			public void run() {
				int currentIndex = tutorialParts.indexOf(currentTutView);

				int index = (currentIndex + 1) % tutorialParts.size();
				currentTutView.animate(lowObj, 500);
				tutorialParts.get(index).animate(highObj, 500);
				currentTutView = tutorialParts.get(index);
			}
		};
		t.scheduleRepeating(10000);

		// Wheel Control
		tutorialPanel.addDomHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent e) {
				int currentIndex = tutorialParts.indexOf(currentTutView);
				t.cancel();
				
				int index = -1;
				if (e.isNorth()) {
					index = Math.max(0, currentIndex - 1);
					currentTutView.animate(lowObj, 500);
					tutorialParts.get(index).animate(highObj, 500);
					currentTutView = tutorialParts.get(index);
				} else {
					index = Math.min(currentIndex + 1,
							tutorialParts.size() - 1);
					currentTutView.animate(lowObj, 500);
					tutorialParts.get(index).animate(highObj, 500);
					currentTutView = tutorialParts.get(index);
				}
				
				if (image != null && imgSlide == currentIndex) {
					if (RootPanel.get().getWidgetIndex(image) == -1)
						RootPanel.get().add(image);
					image.getElement().getStyle().setOpacity(0);
					image.getElement().getStyle().setZIndex(-1);
				}
				else if (image != null && imgSlide == index) {
					if (RootPanel.get().getWidgetIndex(image) == -1)
						RootPanel.get().add(image);
					image.getElement().getStyle().setOpacity(1);
					image.getElement().getStyle().setZIndex(99999);
				}
			}
		}, MouseWheelEvent.getType());

		// Mouse Click Control
		tutorialPanel.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				int currentIndex = tutorialParts.indexOf(currentTutView);
				t.cancel();

				int index = -1;
				if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					index = Math.min(currentIndex + 1,
							tutorialParts.size() - 1);
					currentTutView.animate(lowObj, 500);
					tutorialParts.get(index).animate(highObj, 500);
					currentTutView = tutorialParts.get(index);
				} else if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
					event.preventDefault();
					index = Math.max(0, currentIndex - 1);
					currentTutView.animate(lowObj, 500);
					tutorialParts.get(index).animate(highObj, 500);
					currentTutView = tutorialParts.get(index);
				}
				
				if (image != null && imgSlide == currentIndex) {
					if (RootPanel.get().getWidgetIndex(image) == -1)
						RootPanel.get().add(image);
					image.getElement().getStyle().setOpacity(0);
					image.getElement().getStyle().setZIndex(-1);
				}
				else if (image != null && imgSlide == index) {
					if (RootPanel.get().getWidgetIndex(image) == -1)
						RootPanel.get().add(image);
					image.getElement().getStyle().setOpacity(1);
					image.getElement().getStyle().setZIndex(99999);
				}
			}
		}, MouseDownEvent.getType());
		tutorialPanel.addDomHandler(new ContextMenuHandler() {

			@Override
			public void onContextMenu(ContextMenuEvent event) {
				event.preventDefault();
			}
		}, ContextMenuEvent.getType());
	}

	/**
	 * Places the common widgets to all tutorials
	 */
	private void placeCommonTutorialParts() {

		// overlay
		final Rect background = tutorialPanel.new Rect(0, 0, Window.getClientWidth(), Window.getClientHeight());
		background.attr("fill", "grey");
		background.attr("opacity", 0.3);

		// Quit help
		final Circle circle = tutorialPanel.new Circle(70, Window.getClientHeight()-70, 55);
		final Text leaveMsg = tutorialPanel.new Text(70, Window.getClientHeight()-70,
				constants.leaveTutorial().replaceAll("\n", "\u200E\n"));
		circle.ensureDebugId("leaveTutorial");

		circle.attr("stroke-width", "6px");
		circle.attr("stroke", "orange");

		setTextProperties(leaveMsg, "", "20px", "white", "fantasy", false);
		leaveMsg.attr("cursor", "pointer");

		circle.attr("fill", "#bbbbcc");
		circle.attr("cursor", "pointer");

		circle.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				view.hideTutorialPanel();
				if (image != null)
					RootPanel.get().remove(image);

				if (storage.getItem("showHelp") == null) {
					new OkCancelDialog(constants.hidePermanently()) {
					}.addDialogHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							storage.setItem("showHelp", "no");
						}
					});
				} else if (storage.getItem("showHelp").equals("yes"))
					new OkCancelDialog(constants.hidePermanently()) {
					}.addDialogHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							storage.setItem("showHelp", "no");
						}
					});
			}
		}, ClickEvent.getType());

		leaveMsg.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				view.hideTutorialPanel();
				if (image != null)
					RootPanel.get().remove(image);

				if (storage.getItem("showHelp") == null) {
					if (Window.confirm(constants.hidePermanently()))
						storage.setItem("showHelp", "no");
				} else if (storage.getItem("showHelp").equals("yes"))
					if (Window.confirm(constants.hidePermanently()))
						storage.setItem("showHelp", "no");

			}
		}, ClickEvent.getType());
	}

	/**
	 * Shows Tutorial of Enki Top level
	 */
	private void showTutorialTop() {
		tutorialPanel.clear();

		// tutorial parts
		viewTutorialInterfaceIntro(tutorialPanel);
		viewTutorialResources(tutorialPanel);
		viewTutorialEastTopPanel(tutorialPanel);

		placeCommonTutorialParts();

		// Tip for tutorial navigation
		final Text tip = tutorialPanel.new Text(Window.getClientWidth()/2, Window.getClientHeight() -100, 
				constants.navTip().replaceAll("\n", "\u200E\n"));
		setTextProperties(tip, "orange", "42px", "white", "fantasy", false);
		tip.attr("opacity", 1);

		final Timer tipTimer = new Timer() {

			@Override
			public void run() {
				double opacity = Double.parseDouble(tip.getElement().getStyle()
						.getOpacity());
				if (opacity < 0.25) {
					String highStr = "{\"opacity\":\"1\"}";
					JSONObject highObj = (JSONObject) JSONParser
							.parseStrict(highStr);
					tip.animate(highObj, 1000);
				} else {
					String lowStr = "{\"opacity\":\"0\"}";
					JSONObject lowObj = (JSONObject) JSONParser
							.parseStrict(lowStr);
					tip.animate(lowObj, 1000);
				}
			}
		};
		tipTimer.scheduleRepeating(1000);

		// Remove tip
		tutorialPanel.addDomHandler(new MouseWheelHandler() {
			public void onMouseWheel(MouseWheelEvent e) {

				tipTimer.cancel();
				tip.hide();
			}
		}, MouseWheelEvent.getType());
		tutorialPanel.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				tipTimer.cancel();
				tip.hide();
			}
		}, ClickEvent.getType());
	}

	/**
	 * View interface intro
	 */
	private void viewTutorialInterfaceIntro(Raphael film) {
		final Set set = film.new Set();
		
		final Text actionsText = film.new Text(
				Window.getClientWidth()/2, 
				Window.getClientHeight()/2, 
				constants.interfaceIntroText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);
		actionsText.rotate(-10);

		set.push(actionsText);
		set.attr("opacity", 1);
		tutorialParts.add(set);
		currentTutView = set;
	}

	/**
	 * Select a resource part of tutorial
	 */
	private void viewTutorialResources(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, westContentPanelTopX, westContentPanelTopY,
				westContentPanelWidth, westContentPanelHeigth);
		makeArrow(film, set, westContentPanelTopX, westContentPanelTopY, 
				westContentPanelWidth, westContentPanelHeigth, 100, Direction.LEFT);
		
		final Text actionsText = film.new Text(
				westContentPanelTopX + westContentPanelWidth + 100 + 350, 
				westContentPanelTopY + westContentPanelHeigth/2, 
				constants.resourcesText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);
		actionsText.rotate(-10);

		set.push(actionsText);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	/**
	 * East Top Panel part of tutorial
	 */
	private void viewTutorialEastTopPanel(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, eastContentPanelTopX, eastContentPanelTopY,
				eastContentPanelWidth, eastContentPanelHeigth);
		makeArrow(film, set, eastContentPanelTopX, eastContentPanelTopY, 
				eastContentPanelWidth, eastContentPanelHeigth, 100, Direction.RIGHT);
		
		final Text actionsText = film.new Text(
				eastContentPanelTopX - 100 - 350, 
				eastContentPanelTopY + eastContentPanelHeigth/2, 
				constants.eastTopPanelText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);
		actionsText.rotate(-10);

		set.push(actionsText);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}
	
	/**
	 * Makes the tutorial for the Program View
	 */
	private void makeProgramTutorial() {
		tutorialPanel.clear();

		placeCommonTutorialParts();

		viewTutorialFileUpload(tutorialPanel);
		viewTutorialFileName(tutorialPanel);
		viewTutorialEditor(tutorialPanel);
		viewTutorialObservations(tutorialPanel);
		viewTutorialInputOutput(tutorialPanel);
		viewTutorialProgramButtons(tutorialPanel);
	}

	private void viewTutorialFileUpload(Raphael film) {
		final Set set = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(252, 552)
				.s(100, -300, 0, -360).l(0, 25, 0, -25, 40, 10));
		final Ellipse shadowEllipse = film.new Ellipse(257, 167, 100, 20);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		set.push(shadowPath);
		set.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(250, 550)
				.s(100, -300, 0, -360).l(0, 25, 0, -25, 40, 10));
		final Text text = film.new Text(350, 650, ""
				.replaceAll("\n", "\u200E\n"));
		final Ellipse ellipse = film.new Ellipse(255, 165, 100, 20);

		for (Shape shape : Arrays.asList(path, ellipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy", true);

		text.rotate(10);

		set.push(path);
		set.push(text);
		set.push(ellipse);
		set.attr("opacity", 1);
		tutorialParts.add(set);
		currentTutView = set;
	}

	private void viewTutorialFileName(Raphael film) {
		final Set set = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(402, 567)
				.s(100, -200, 50, -345).l(-20, 20, 20, -20, 35, 15));
		final Ellipse shadowEllipse = film.new Ellipse(457, 192, 300, 25);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		set.push(shadowPath);
		set.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(400, 565)
				.s(100, -200, 50, -345).l(-20, 20, 20, -20, 35, 15));
		final Text text = film.new Text(450, 680, ""
				.replaceAll("\n", "\u200E\n"));
		final Ellipse ellipse = film.new Ellipse(455, 190, 300, 25);

		for (Shape shape : Arrays.asList(path, ellipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy", true);

		text.rotate(5);

		set.push(path);
		set.push(text);
		set.push(ellipse);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	private void viewTutorialEditor(Raphael film) {
		final Set set = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(853, 602)
				.s(-50, -50, -95, -55).l(40, -20, -40, 20, 20, 30));
		final Rect shadowEllipse = film.new Rect(192, 202, 560, 435);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		set.push(shadowPath);
		set.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(851, 600)
				.s(-50, -50, -95, -55).l(40, -20, -40, 20, 20, 30));
		final Text text = film.new Text(780, 740, ""
				.replaceAll("\n", "\u200E\n"));
		final Rect rect = film.new Rect(190, 200, 560, 435);

		for (Shape shape : Arrays.asList(path, rect)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy", true);

		text.rotate(-10);

		set.push(path);
		set.push(text);
		set.push(rect);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	private void viewTutorialObservations(Raphael film) {
		final Set set = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(432, 322)
				.s(100, 200, 50, 335).l(-20, -20, 20, 20, 35, -15));
		final Ellipse shadowEllipse = film.new Ellipse(457, 727, 300, 65);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		set.push(shadowPath);
		set.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(430, 320)
				.s(100, 200, 50, 335).l(-20, -20, 20, 20, 35, -15));
		final Text text = film.new Text(450, 250, ""
				.replaceAll("\n", "\u200E\n"));
		final Ellipse ellipse = film.new Ellipse(455, 725, 300, 65);

		for (Shape shape : Arrays.asList(path, ellipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy", true);

		text.rotate(-5);

		set.push(path);
		set.push(text);
		set.push(ellipse);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	private void viewTutorialProgramButtons(Raphael film) {
		final Set set = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(932, 357)
				.s(100, 200, 50, 325).l(-20, -20, 20, 20, 35, -15));
		final Ellipse shadowEllipse = film.new Ellipse(902, 732, 240, 45);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		set.push(shadowPath);
		set.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(930, 355)
				.s(100, 200, 50, 325).l(-20, -20, 20, 20, 35, -15));
		final Text text = film.new Text(850, 260, ""
				.replaceAll("\n", "\u200E\n"));
		final Ellipse ellipse = film.new Ellipse(900, 730, 240, 45);

		for (Shape shape : Arrays.asList(path, ellipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy", true);

		text.rotate(-5);

		set.push(path);
		set.push(text);
		set.push(ellipse);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	private void viewTutorialInputOutput(Raphael film) {
		final Set set = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(662, 627)
				.s(50, -70, 95, -75).l(-40, -20, 40, 20, -20, 30));
		final Rect shadowEllipse = film.new Rect(762, 152, 330, 545);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		set.push(shadowPath);
		set.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(660, 625).s(50, -70, 95, -75)
				.l(-40, -20, 40, 20, -20, 30));
		final Text text = film.new Text(510, 720, ""
				.replaceAll("\n", "\u200E\n"));
		final Rect rect = film.new Rect(760, 150, 330, 545);

		for (Shape shape : Arrays.asList(path, rect)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy", 
				true);

		text.rotate(5);

		set.push(path);
		set.push(text);
		set.push(rect);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	/**
	 * Makes the tutorial for the Video View
	 */
	private void makeVideoTutorial() {
		tutorialPanel.clear();

		placeCommonTutorialParts();

		viewTutorialVideoGadget(tutorialPanel);
		viewTutorialBottomExpositiveResource(tutorialPanel);
		
	}

	/**
	 * Video gadget part of tutorial
	 */
	private void viewTutorialVideoGadget(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, centerContentPanelTopX, centerContentPanelTopY,
				centerContentPanelWidth, centerContentPanelHeigth);
		makeArrow(film, set, centerContentPanelTopX, centerContentPanelTopY, 
				centerContentPanelWidth, centerContentPanelHeigth, 60, Direction.UP);
		
		final Text actionsText = film.new Text(
				centerContentPanelTopX + centerContentPanelWidth/2, 
				centerContentPanelTopY + centerContentPanelHeigth + 60 + 32 + 16, 
				constants.videoGadgetText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);

		set.push(actionsText);
		set.attr("opacity", 1);
		tutorialParts.add(set);
		currentTutView = set;
	}

	/**
	 * Tutorial of bottom panel of a static resource
	 */
	private void viewTutorialBottomExpositiveResource(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, southContentPanelTopX, southContentPanelTopY,
				southContentPanelWidth, southContentPanelHeigth);
		makeArrow(film, set, southContentPanelTopX, southContentPanelTopY, 
				southContentPanelWidth, southContentPanelHeigth, 100, Direction.DOWN);
		
		final Text actionsText = film.new Text(
				southContentPanelTopX + southContentPanelWidth/2, 
				southContentPanelTopY - 100 - 50, 
				constants.bottomPanelExpResourcesText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);
		actionsText.rotate(-10);

		set.push(actionsText);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	/**
	 * Makes the tutorial for the Pdf View
	 */
	private void makePdfTutorial() {
		tutorialPanel.clear();

		placeCommonTutorialParts();

		viewTutorialPdfGadget(tutorialPanel);
		viewTutorialBottomExpositiveResource(tutorialPanel);
		
	}

	/**
	 * Pdf gadget part of tutorial
	 */
	private void viewTutorialPdfGadget(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, centerContentPanelTopX, centerContentPanelTopY,
				centerContentPanelWidth, centerContentPanelHeigth);
		makeArrow(film, set, centerContentPanelTopX, centerContentPanelTopY, 
				centerContentPanelWidth, centerContentPanelHeigth, 60, Direction.UP);
		
		final Text actionsText = film.new Text(
				centerContentPanelTopX + centerContentPanelWidth/2, 
				centerContentPanelTopY + centerContentPanelHeigth + 60 + 32 + 16, 
				constants.pdfGadgetText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);

		set.push(actionsText);
		set.attr("opacity", 1);
		tutorialParts.add(set);
		currentTutView = set;
	}

	/**
	 * Makes the tutorial for the Problem solving View
	 */
	private void makeProblemTutorial() {
		tutorialPanel.clear();

		placeCommonTutorialParts();

		viewTutorialProblemCenterPanel(tutorialPanel, diagramEditor);
		
		if (!diagramEditor) {
			viewTutorialProblemEastBottomPanel(tutorialPanel);
		}
		
		viewTutorialProblemBottomPanel(tutorialPanel);
		viewTutorialProblemEastTopPanel(tutorialPanel);
	}

	/**
	 * Problem center panel part of tutorial
	 * @param diagramEditor whether the problem is of type diagram
	 */
	private void viewTutorialProblemCenterPanel(Raphael film, boolean diagramEditor) {
		
		if (diagramEditor)
			viewTutorialProblemDiagramEditor(film);
		else
			viewTutorialProblemCodeEditor(film);
	}
	
	
	/**
	 * Builds the center part of the tutorial of a code editor problem
	 * @param film
	 */
	private void viewTutorialProblemCodeEditor(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, centerContentPanelTopX, centerContentPanelTopY,
				centerContentPanelWidth, centerContentPanelHeigth);
		makeArrow(film, set, centerContentPanelTopX, centerContentPanelTopY, 
				centerContentPanelWidth, centerContentPanelHeigth, 60, Direction.UP);
		
		final Text actionsText = film.new Text(
				centerContentPanelTopX + centerContentPanelWidth/2, 
				centerContentPanelTopY + centerContentPanelHeigth + 60 + 32 + 16, 
				constants.problemCenterPanelText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);

		set.push(actionsText);
		set.attr("opacity", 1);
		tutorialParts.add(set);
		currentTutView = set;
	}
	
	/**
	 * Builds the center part of the tutorial of a diagram editor problem
	 * @param film
	 */
	private void viewTutorialProblemDiagramEditor(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, centerContentPanelTopX, centerContentPanelTopY,
				centerContentPanelWidth, centerContentPanelHeigth);
		makeArrow(film, set, centerContentPanelTopX, centerContentPanelTopY, 
				centerContentPanelWidth, centerContentPanelHeigth, 60, Direction.UP);
		
		Text actionsText = film.new Text(
				centerContentPanelTopX + centerContentPanelWidth/2, 
				centerContentPanelTopY + centerContentPanelHeigth + 70 + 32 + 16, 
				constants.diagramProblemCenterPanelText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "28px", "orange", "fantasy", 
				true);

		set.push(actionsText);
		set.attr("opacity", 1);
		tutorialParts.add(set);
		currentTutView = set;
		
		if (editorSelected) {
			final Set set2 = film.new Set();
			makeRect(film, set2, centerContentPanelTopX + 10, centerContentPanelTopY + 35,
					centerContentPanelWidth - 20, 60);
			makeArrow(film, set2, centerContentPanelTopX + 10, centerContentPanelTopY + 60, 
					centerContentPanelWidth-20, 50, 60, Direction.UP);
			
			final Text actionsText2 = film.new Text(
					centerContentPanelTopX + centerContentPanelWidth/2, 
					centerContentPanelTopY + 160 + 70 + 32 + 16, 
					constants.diagramEditorButtonsText().replaceAll("\n", "\u200E\n"));
			setTextProperties(actionsText2, "", "28px", "orange", "fantasy", 
					true);
			
			set2.push(actionsText2);
			set2.attr("opacity", 0);
			tutorialParts.add(set2);
			
			final Set set3 = film.new Set();
			makeRect(film, set3, centerContentPanelTopX + centerContentPanelWidth/2 - 104, 
					centerContentPanelTopY + centerContentPanelHeigth/2	- 154, 220, 310);
			makeArrow(film, set3, centerContentPanelTopX + 10, 
					centerContentPanelTopY + centerContentPanelHeigth/2	- 150, 
					centerContentPanelWidth-20, 50, 60, Direction.UP);
			
			final Text actionsText3 = film.new Text(
					centerContentPanelTopX + centerContentPanelWidth/2, 
					centerContentPanelTopY + centerContentPanelHeigth/2	+ 150 + 70, 
					constants.diagramEditorPropertiesText().replaceAll("\n", "\u200E\n"));
			setTextProperties(actionsText3, "", "28px", "orange", "fantasy", 
					true);
			
			image = new Image(EnkiResources.INSTANCE.eshuProperties());
			Style imgStyle = image.getElement().getStyle();
			imgStyle.setZIndex(99999);
			imgStyle.setPosition(Position.FIXED);
			imgStyle.setLeft(centerContentPanelTopX + centerContentPanelWidth/2 
					- 100, Unit.PX);
			imgStyle.setTop(centerContentPanelTopY + centerContentPanelHeigth/2
					- 150, Unit.PX);
			RootPanel.get().add(image);
			imgStyle.setOpacity(0);
			imgStyle.setZIndex(-1);
			
			set3.push(actionsText3);
			set3.attr("opacity", 0);
			
			imgSlide = tutorialParts.size();
			tutorialParts.add(set3);
		}
	}

	/**
	 * Problem bottom panel part of tutorial
	 */
	private void viewTutorialProblemBottomPanel(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, southContentPanelTopX, southContentPanelTopY,
				southContentPanelWidth, southContentPanelHeigth);
		makeArrow(film, set, southContentPanelTopX, southContentPanelTopY, 
				southContentPanelWidth, southContentPanelHeigth, 100, Direction.DOWN);
		
		final Text actionsText = film.new Text(
				southContentPanelTopX + southContentPanelWidth/2, 
				southContentPanelTopY - 100 - 50, 
				constants.problemBottomPanelText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);
		actionsText.rotate(-10);

		set.push(actionsText);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	/**
	 * Problem east top panel part of tutorial
	 */
	private void viewTutorialProblemEastTopPanel(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, eastContentPanelTopX, eastContentPanelTopY,
				eastContentPanelWidth, eastContentPanelHeigth);
		makeArrow(film, set, eastContentPanelTopX, eastContentPanelTopY, 
				eastContentPanelWidth, eastContentPanelHeigth, 100, Direction.RIGHT);
		
		final Text actionsText = film.new Text(
				eastContentPanelTopX - 100 - 350, 
				eastContentPanelTopY + eastContentPanelHeigth/2, 
				constants.problemEastTopPanelText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);
		actionsText.rotate(-10);

		set.push(actionsText);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	/**
	 * Problem east bottom panel part of tutorial
	 */
	private void viewTutorialProblemEastBottomPanel(Raphael film) {
		final Set set = film.new Set();

		makeRect(film, set, eastContentPanelBottomTopX, eastContentPanelBottomTopY,
				eastContentPanelBottomWidth, eastContentPanelBottomHeigth);
		makeArrow(film, set, eastContentPanelBottomTopX, eastContentPanelBottomTopY, 
				eastContentPanelBottomWidth, eastContentPanelBottomHeigth, 100, Direction.RIGHT);
		
		final Text actionsText = film.new Text(
				eastContentPanelBottomTopX - 100 - 350, 
				eastContentPanelBottomTopY + eastContentPanelBottomHeigth/2, 
				constants.problemEastBottomPanelText().replaceAll("\n", "\u200E\n"));
		setTextProperties(actionsText, "", "32px", "orange", "fantasy", 
				true);
		actionsText.rotate(-10);

		set.push(actionsText);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}
	
	/**
	 * Make an arrow
	 * @param film
	 * @param set
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param length
	 * @param dir
	 */
	private void makeArrow(Raphael film, Set set, int x, int y, int width, int height, int length,
			Direction dir) {
		
		PathBuilder shadowBuilder = new PathBuilder();
		PathBuilder actionsBuilder = new PathBuilder();
		Path shadowPath = null;
		Path actionsPath = null;
		switch (dir) {
		case UP:
			shadowPath = film.new Path(shadowBuilder.m(
					(x + width/2) + 2, y + height + length)
					.s(0, 0, 0, -length).l(-20, 40, 20, -40, 20, 40));
			actionsPath = film.new Path(actionsBuilder.m(
					(x + width/2), y + height + length)
					.s(0, 0, 0, -length).l(-20, 40, 20, -40, 20, 40));
			break;
		case DOWN:
			shadowPath = film.new Path(shadowBuilder.m(
					(x + width/2) + 2, y - length)
					.s(0, 0, 0, length).l(-20, -40, 20, 40, 20, -40));
			actionsPath = film.new Path(actionsBuilder.m(
					(x + width/2), y - length)
					.s(0, 0, 0, length).l(-20, -40, 20, 40, 20, -40));
			break;
		case LEFT:
			shadowPath = film.new Path(shadowBuilder.m(
					(x + width) + 2 + length, y + height/2)
					.s(0, 0, -length, 0).l(40, -20, -40, 20, 40, 20));
			actionsPath = film.new Path(actionsBuilder.m(
					(x + width) + length, y + height/2)
					.s(0, 0, -length, 0).l(40, -20, -40, 20, 40, 20));
			break;
		case RIGHT:
			shadowPath = film.new Path(shadowBuilder.m(
					x + 2 - length, y + height/2)
					.s(0, 0, length, 0).l(-40, -20, 40, 20, -40, 20));
			actionsPath = film.new Path(actionsBuilder.m(
					x - length, y + height/2)
					.s(0, 0, length, 0).l(-40, -20, 40, 20, -40, 20));
			break;

		}
		
		shadowPath.attr("stroke-width", "5px");
		shadowPath.attr("stroke", "rgba(0, 0, 0, 0.6)");

		actionsPath.attr("stroke-width", "6px");
		actionsPath.attr("stroke", "orange");
		
		set.push(shadowPath);
		set.push(actionsPath);
	}
	
	/**
	 * Draw an ellipse with top (x, y) and radius (rx, ry)
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void makeEllipse(Raphael film, Set set, double x, double y, double width, double height) {
		
		final Ellipse shadowEllipse = film.new Ellipse(x + width/2 + 2, y + height/2 + 2, width/2, height/2);
		final Ellipse actionsEllipse = film.new Ellipse(x + width/2, y + height/2, width/2, height/2);
		
		shadowEllipse.attr("stroke-width", "5px");
		shadowEllipse.attr("stroke", "rgba(0, 0, 0, 0.6)");
		
		actionsEllipse.attr("stroke-width", "6px");
		actionsEllipse.attr("stroke", "orange");
		
		set.push(shadowEllipse);
		set.push(actionsEllipse);
	}
	
	/**
	 * Draw a rectangle with top (x, y) and radius (rx, ry)
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	private void makeRect(Raphael film, Set set, double x, double y, double width, double height) {
		
		final Rect shadowRect = film.new Rect(x + 2, y + 2, width, height);
		final Rect actionsRect = film.new Rect(x, y, width, height);
		
		shadowRect.attr("stroke-width", "5px");
		shadowRect.attr("stroke", "rgba(0, 0, 0, 0.6)");
		
		actionsRect.attr("stroke-width", "6px");
		actionsRect.attr("stroke", "orange");
		
		set.push(shadowRect);
		set.push(actionsRect);
	}

	/**
	 * Sets text properties in a RaphaelGwt widget
	 * 
	 * @param text
	 * @param stroke
	 * @param size
	 * @param fill
	 * @param font
	 */
	private void setTextProperties(Text text, String stroke, String size,
			String fill, String font, boolean shadow) {
		text.attr("stroke", stroke);
		text.attr("font-size", size);
		text.attr("fill", fill);
		text.attr("font-family", font);
		
		if(shadow) {
			text.getElement().getStyle().setProperty("textShadow", 
					"0px 2px 2px rgba(0, 0, 0, 0.8)");
		}
	}
}
