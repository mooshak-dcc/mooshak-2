package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.NativeEvent;
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
import com.hydro4ge.raphaelgwt.client.PathBuilder;
import com.hydro4ge.raphaelgwt.client.Raphael;
import com.hydro4ge.raphaelgwt.client.Raphael.Circle;
import com.hydro4ge.raphaelgwt.client.Raphael.Ellipse;
import com.hydro4ge.raphaelgwt.client.Raphael.Path;
import com.hydro4ge.raphaelgwt.client.Raphael.Rect;
import com.hydro4ge.raphaelgwt.client.Raphael.Set;
import com.hydro4ge.raphaelgwt.client.Raphael.Shape;
import com.hydro4ge.raphaelgwt.client.Raphael.Text;

import pt.up.fc.dcc.mooshak.client.guis.icpc.Token.Command;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;

/**
 * Manages the tutorials to show
 * 
 * @author josepaiva
 */
public class TutorialView {

	private HasTutorial view;
	private Raphael tutorialPanel = null;

	private List<Set> tutorialParts = new ArrayList<Raphael.Set>();
	private Set currentTutView;
	
	private ICPCConstants constants =
			GWT.create(ICPCConstants.class);

	private Storage storage = Storage.getLocalStorageIfSupported();

	public TutorialView() {
	}

	public void setTutorialView(HasTutorial view) {
		this.view = view;
	}

	public void showTutorial(Command command) {
		if (tutorialPanel == null) {
			tutorialPanel = view.showTutorialPanel();
			addPanelNavigation();
		} else
			view.showTutorialPanel();
		tutorialParts.clear();
		currentTutView = null;

		switch (command) {
		case ASK:
			makeAskTutorial();
			break;
		case VIEW:
			makeViewTutorial();
			break;
		case TOP:
			showTutorialTop();
			break;
		case PROGRAM:
			makeProgramTutorial();
			break;
		case LISTING:
			makeListingTutorial();
			break;
		default:
			break;
		}
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

				if (e.isNorth()) {
					int index = Math.max(0, currentIndex - 1);
					currentTutView.animate(lowObj, 500);
					tutorialParts.get(index).animate(highObj, 500);
					currentTutView = tutorialParts.get(index);
				} else {
					int index = Math.min(currentIndex + 1,
							tutorialParts.size() - 1);
					currentTutView.animate(lowObj, 500);
					tutorialParts.get(index).animate(highObj, 500);
					currentTutView = tutorialParts.get(index);
				}
			}
		}, MouseWheelEvent.getType());

		// Mouse Click Control
		tutorialPanel.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				int currentIndex = tutorialParts.indexOf(currentTutView);
				t.cancel();

				if (event.getNativeButton() == NativeEvent.BUTTON_LEFT) {
					int index = Math.min(currentIndex + 1,
							tutorialParts.size() - 1);
					currentTutView.animate(lowObj, 500);
					tutorialParts.get(index).animate(highObj, 500);
					currentTutView = tutorialParts.get(index);
				} else if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
					event.preventDefault();
					int index = Math.max(0, currentIndex - 1);
					currentTutView.animate(lowObj, 500);
					tutorialParts.get(index).animate(highObj, 500);
					currentTutView = tutorialParts.get(index);
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
		Rect background = tutorialPanel.new Rect(0, 0, 1200, 900);
		background.attr("fill", "grey");
		background.attr("opacity", 0.3);

		// Quit help
		final Circle circle = tutorialPanel.new Circle(100, 100, 55);
		final Text leaveMsg = tutorialPanel.new Text(100, 100,
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
	 * Shows Tutorial of ICPC Top level
	 */
	private void showTutorialTop() {
		tutorialPanel.clear();

		placeCommonTutorialParts();

		// tutorial parts
		viewTutorialProblem(tutorialPanel);
		viewTutorialAction(tutorialPanel);
		viewTutorialStatistic(tutorialPanel);

		// Tip for tutorial navigation
		final Text tip = tutorialPanel.new Text(620, 740, constants.navTip().replaceAll("\n", "\u200E\n"));
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
	 * Select a statistic part of tutorial
	 */
	private void viewTutorialStatistic(Raphael film) {
		final Set listing = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(562, 362)
				.s(-100, 110, 40, 382).l(-40, -20, 40, 20, 20, -30));
		final Ellipse shadowEllipse = film.new Ellipse(617, 802, 440, 55);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}

		PathBuilder listingsBuilder = new PathBuilder();
		final Path listingsPath = film.new Path(listingsBuilder.m(560, 360)
				.s(-100, 110, 40, 382).l(-40, -20, 40, 20, 20, -30));

		final Text listingsText = film.new Text(650, 350,
				constants.selectStat().replaceAll("\n", "\u200E\n"));
		final Ellipse listingsEllipse = film.new Ellipse(615, 800, 440, 55);

		for (Shape shape : Arrays.asList(listingsPath, listingsEllipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(listingsText, "", "42px", "orange", "fantasy", 
				true);

		listingsText.rotate(10);

		listing.push(shadowPath);
		listing.push(shadowEllipse);
		listing.push(listingsPath);
		listing.push(listingsText);
		listing.push(listingsEllipse);
		listing.attr("opacity", 0);
		tutorialParts.add(listing);
	}

	/**
	 * Select an action part of tutorial
	 */
	private void viewTutorialAction(Raphael film) {
		final Set action = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(502, 452)
				.s(-50, -50, -335, -135).l(40, -20, -40, 20, 20, 30));
		final Ellipse shadowEllipse = film.new Ellipse(92, 322, 70, 165);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		
		PathBuilder actionsBuilder = new PathBuilder();
		final Path actionsPath = film.new Path(actionsBuilder.m(500, 450)
				.s(-50, -50, -335, -135).l(40, -20, -40, 20, 20, 30));
		final Text actionsText = film.new Text(550, 570, 
				constants.actionsText().replaceAll("\n", "\u200E\n"));
		final Ellipse actionsEllipse = film.new Ellipse(90, 320, 70, 165);

		for (Shape shape : Arrays.asList(actionsPath, actionsEllipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(actionsText, "", "42px", "orange", "fantasy", 
				true);

		actionsText.rotate(-10);

		action.push(shadowPath);
		action.push(shadowEllipse);
		action.push(actionsPath);
		action.push(actionsText);
		action.push(actionsEllipse);
		action.attr("opacity", 0);
		tutorialParts.add(action);
	}

	/**
	 * Select a problem part of tutorial
	 */
	private void viewTutorialProblem(Raphael film) {
		final Set problem = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(502, 502)
				.s(100, -200, 50, -400).l(40, 20, -40, -20, -20, 30));
		final Ellipse shadowEllipse = film.new Ellipse(752, 62, 400, 40);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(500, 500)
				.s(100, -200, 50, -400).l(40, 20, -40, -20, -20, 30));
		final Text text = film.new Text(600, 500, constants.selectProblem()
				.replaceAll("\n", "\u200E\n"));
		final Ellipse ellipse = film.new Ellipse(750, 60, 400, 40);

		for (Shape shape : Arrays.asList(path, ellipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy",
				true);

		text.rotate(-20);

		problem.push(shadowPath);
		problem.push(shadowEllipse);
		problem.push(path);
		problem.push(text);
		problem.push(ellipse);
		tutorialParts.add(problem);
		currentTutView = problem;
	}

	/**
	 * Makes the tutorial for the Listing View
	 */
	private void makeListingTutorial() {
		tutorialPanel.clear();

		placeCommonTutorialParts();

		viewTutorialSubmissions(tutorialPanel);
		viewTutorialQuestions(tutorialPanel);
		viewTutorialPrintouts(tutorialPanel);
		viewTutorialBalloons(tutorialPanel);
		viewTutorialRankings(tutorialPanel);
		viewTutorialGridHeader(tutorialPanel);
	}

	private void viewTutorialGridHeader(Raphael film) {
		final Set header = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(237, 532)
				.s(100, -300, 0, -360).l(0, 25, 0, -25, 40, 10));
		final Ellipse shadowEllipse = film.new Ellipse(222, 147, 40, 20);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		header.push(shadowPath);
		header.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(235, 530)
				.s(100, -300, 0, -360).l(0, 25, 0, -25, 40, 10));
		final Text text = film.new Text(355, 600, constants.gridHeader()
				.replaceAll("\n", "\u200E\n"));
		final Ellipse ellipse = film.new Ellipse(220, 145, 40, 20);

		for (Shape shape : Arrays.asList(path, ellipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy", 
				true);

		text.rotate(10);

		header.push(path);
		header.push(text);
		header.push(ellipse);
		header.attr("opacity", 0);
		tutorialParts.add(header);
	}

	private void viewTutorialSubmissions(Raphael film) {
		final Set listing = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(562, 362)
				.s(0, 110, -250, 390).l(-18, -30, 18, 30, 45, -10));
		final Ellipse shadowEllipse = film.new Ellipse(262, 802, 100, 55);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		listing.push(shadowPath);
		listing.push(shadowEllipse);

		PathBuilder listingsBuilder = new PathBuilder();
		final Path listingsPath = film.new Path(listingsBuilder.m(560, 360)
				.s(0, 110, -250, 390).l(-18, -30, 18, 30, 45, -10));

		final Text listingsText = film.new Text(650, 350,
				constants.submissionsText().replaceAll("\n", "\u200E\n"));
		final Ellipse listingsEllipse = film.new Ellipse(260, 800, 100, 55);

		for (Shape shape : Arrays.asList(listingsPath, listingsEllipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(listingsText, "", "42px", "orange", "fantasy", 
				true);

		listingsText.rotate(10);

		listing.push(listingsPath);
		listing.push(listingsText);
		listing.push(listingsEllipse);
		listing.attr("opacity", 1);
		tutorialParts.add(listing);
		currentTutView = listing;
	}

	private void viewTutorialQuestions(Raphael film) {
		final Set listing = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(562, 362)
				.s(0, 110, -105, 380).l(-18, -30, 18, 30, 35, -18));
		final Ellipse shadowEllipse = film.new Ellipse(442, 802, 100, 55);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		listing.push(shadowPath);
		listing.push(shadowEllipse);

		PathBuilder listingsBuilder = new PathBuilder();
		final Path listingsPath = film.new Path(listingsBuilder.m(560, 360)
				.s(0, 110, -105, 380).l(-18, -30, 18, 30, 35, -18));

		final Text listingsText = film.new Text(650, 350,
				constants.questionsText().replaceAll("\n", "\u200E\n"));
		final Ellipse listingsEllipse = film.new Ellipse(440, 800, 100, 55);

		for (Shape shape : Arrays.asList(listingsPath, listingsEllipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(listingsText, "", "42px", "orange", "fantasy", 
				true);

		listingsText.rotate(5);

		listing.push(listingsPath);
		listing.push(listingsText);
		listing.push(listingsEllipse);
		listing.attr("opacity", 0);
		tutorialParts.add(listing);
	}

	private void viewTutorialPrintouts(Raphael film) {
		final Set listing = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(612, 372)
				.s(0, 110, 0, 370).l(-30, -20, 30, 20, 30, -20));
		final Ellipse shadowEllipse = film.new Ellipse(617, 802, 100, 55);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		listing.push(shadowPath);
		listing.push(shadowEllipse);

		PathBuilder listingsBuilder = new PathBuilder();
		final Path listingsPath = film.new Path(listingsBuilder.m(610, 370)
				.s(0, 110, 0, 370).l(-30, -20, 30, 20, 30, -20));

		final Text listingsText = film.new Text(650, 350,
				constants.printoutsText().replaceAll("\n", "\u200E\n"));
		final Ellipse listingsEllipse = film.new Ellipse(615, 800, 100, 55);

		for (Shape shape : Arrays.asList(listingsPath, listingsEllipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(listingsText, "", "42px", "orange", "fantasy", 
				true);

		listing.push(listingsPath);
		listing.push(listingsText);
		listing.push(listingsEllipse);
		listing.attr("opacity", 0);
		tutorialParts.add(listing);
	}

	private void viewTutorialBalloons(Raphael film) {
		final Set listing = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(572, 382)
				.s(0, 110, 155, 370).l(-35, -18, 35, 18, 18, -30));
		final Ellipse shadowEllipse = film.new Ellipse(787, 802, 100, 55);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		listing.push(shadowPath);
		listing.push(shadowEllipse);

		PathBuilder listingsBuilder = new PathBuilder();
		final Path listingsPath = film.new Path(listingsBuilder.m(570, 380)
				.s(0, 110, 155, 370).l(-35, -18, 35, 18, 18, -30));

		final Text listingsText = film.new Text(650, 350,
				constants.balloonsText().replaceAll("\n", "\u200E\n"));
		final Ellipse listingsEllipse = film.new Ellipse(785, 800, 100, 55);

		for (Shape shape : Arrays.asList(listingsPath, listingsEllipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(listingsText, "", "42px", "orange", "fantasy", 
				true);

		listingsText.rotate(-5);

		listing.push(listingsPath);
		listing.push(listingsText);
		listing.push(listingsEllipse);
		listing.attr("opacity", 0);
		tutorialParts.add(listing);
	}

	private void viewTutorialRankings(Raphael film) {
		final Set listing = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(592, 392)
				.s(0, 110, 305, 363).l(-45, 0, 45, 0, 5, -35));
		final Ellipse shadowEllipse = film.new Ellipse(962, 802, 100, 55);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		listing.push(shadowPath);
		listing.push(shadowEllipse);

		PathBuilder listingsBuilder = new PathBuilder();
		final Path listingsPath = film.new Path(listingsBuilder.m(590, 390)
				.s(0, 110, 305, 363).l(-45, 0, 45, 0, 5, -35));

		final Text listingsText = film.new Text(650, 350,
				constants.rankingsText().replaceAll("\n", "\u200E\n"));
		final Ellipse listingsEllipse = film.new Ellipse(960, 800, 100, 55);

		for (Shape shape : Arrays.asList(listingsPath, listingsEllipse)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(listingsText, "", "42px", "orange", "fantasy", 
				true);

		listingsText.rotate(-10);

		listing.push(listingsPath);
		listing.push(listingsText);
		listing.push(listingsEllipse);
		listing.attr("opacity", 0);
		tutorialParts.add(listing);
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
		final Text text = film.new Text(350, 650, constants.fileUploadText()
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
		final Text text = film.new Text(450, 680, constants.fileNameText()
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
		final Text text = film.new Text(780, 740, constants.fileEditorText()
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
		final Text text = film.new Text(450, 250, constants.observationsText()
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
		final Text text = film.new Text(850, 260, constants.programButtonsText()
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
		final Text text = film.new Text(510, 720, constants.inputOutputText()
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
	 * Makes the tutorial for the Statement View
	 */
	private void makeViewTutorial() {
		tutorialPanel.clear();

		placeCommonTutorialParts();

		final Set listing = tutorialPanel.new Set();

		final Text listingsText = tutorialPanel.new Text(650, 350,
				constants.viewText().replaceAll("\n", "\u200E\n"));

		setTextProperties(listingsText, "", "42px", "orange", "fantasy", 
				true);

		listingsText.rotate(-10);

		listing.push(listingsText);
		listing.attr("opacity", 1);
		tutorialParts.add(listing);
		currentTutView = listing;
	}

	/**
	 * Makes the tutorial for the Ask a Question View
	 */
	private void makeAskTutorial() {
		tutorialPanel.clear();

		placeCommonTutorialParts();

		viewTutorialAskForm(tutorialPanel);
		viewTutorialAnsweredGridClick(tutorialPanel);
		viewTutorialAnsweredAnswerDetail(tutorialPanel);
		
	}

	private void viewTutorialAskForm(Raphael tutorialPanel2) {
		final Set listing = tutorialPanel.new Set();

		final Text listingsText = tutorialPanel.new Text(650, 350,
				constants.askText().replaceAll("\n", "\u200E\n"));

		setTextProperties(listingsText, "", "42px", "orange", "fantasy", 
				true);

		listingsText.rotate(-10);

		listing.push(listingsText);
		listing.attr("opacity", 1);
		tutorialParts.add(listing);
		currentTutView = listing;
	}

	private void viewTutorialAnsweredGridClick(Raphael film) {
		final Set set = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(853, 667)
				.s(50, 20, 0, -80).l(-20, 20, 20, -20, 35, 15));
		final Rect shadowEllipse = film.new Rect(182, 157, 905, 425);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		set.push(shadowPath);
		set.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(851, 665)
				.s(50, 20, 0, -80).l(-20, 20, 20, -20, 35, 15));
		final Text text = film.new Text(650, 710, constants.answeredGridClick()
				.replaceAll("\n", "\u200E\n"));
		final Rect rect = film.new Rect(180, 155, 905, 425);

		for (Shape shape : Arrays.asList(path, rect)) {
			shape.attr("stroke-width", "6px");
			shape.attr("stroke", "orange");
		}

		setTextProperties(text, "", "42px", "orange", "fantasy", true);

		text.rotate(-5);

		set.push(path);
		set.push(text);
		set.push(rect);
		set.attr("opacity", 0);
		tutorialParts.add(set);
	}

	private void viewTutorialAnsweredAnswerDetail(Raphael film) {
		final Set set = film.new Set();

		PathBuilder shadowBuilder = new PathBuilder();
		final Path shadowPath = film.new Path(shadowBuilder.m(432, 367)
				.s(100, 100, 50, 250).l(-20, -20, 20, 20, 35, -15));
		final Rect shadowEllipse = film.new Rect(182, 622, 905, 130);
		for (Shape shape : Arrays.asList(shadowPath, shadowEllipse)) {
			shape.attr("stroke-width", "5px");
			shape.attr("stroke", "rgba(0, 0, 0, 0.6)");
		}
		set.push(shadowPath);
		set.push(shadowEllipse);
		
		PathBuilder builder = new PathBuilder();
		final Path path = film.new Path(builder.m(430, 365)
				.s(100, 100, 50, 250).l(-20, -20, 20, 20, 35, -15));
		final Text text = film.new Text(450, 260, constants.answeredDetail()
				.replaceAll("\n", "\u200E\n"));
		final Rect rect = film.new Rect(180, 620, 905, 130);

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
