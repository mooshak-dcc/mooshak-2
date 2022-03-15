package pt.up.fc.dcc.mooshak.client.guis.enki.view;

import java.util.Date;

import org.gwtbootstrap3.client.ui.Anchor;
import org.gwtbootstrap3.client.ui.Container;
import org.gwtbootstrap3.client.ui.html.Span;

import pt.up.fc.dcc.mooshak.client.gadgets.Gadget;
import pt.up.fc.dcc.mooshak.client.gadgets.ResourcesTree;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.ResourcesTreeView;
import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.guis.enki.widgets.ContentPanel;
import pt.up.fc.dcc.mooshak.client.guis.enki.widgets.DroppablePanel;
import pt.up.fc.dcc.mooshak.client.widgets.ColorTweaker;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.storage.client.StorageMap;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.hydro4ge.raphaelgwt.client.Raphael;


public class TopLevelViewImpl extends Composite implements TopLevelView {

	public enum Region {
		NORTHWEST, NORTHEAST, CENTER, SOUTH, SOUTHWEST, SOUTHEAST, EAST, WEST
	}

	private static TeachingUiBinder uiBinder = GWT
			.create(TeachingUiBinder.class);

	@UiTemplate("TopLevelView.ui.xml")
	interface TeachingUiBinder extends UiBinder<Widget, TopLevelViewImpl> {
	}

	private EnkiConstants constants = GWT.create(EnkiConstants.class);
	
	private String teamId;

	@UiField
	Container headerContainer;

	@UiField
	HTMLPanel basePanel;

	@UiField
	BaseStyle style;

	interface BaseStyle extends CssResource {

		String film();
	}
	
	private Raphael film;

	@UiField
	HTMLPanel content;

	@UiField
	Container footerContainer;
	
	@UiField
	Span copyrightYear;

	/*@UiField
	InputGroupAddon searchBtn;

	@UiField
	TextBox searchTxt;*/

	@UiField
	Anchor help;

	@UiField
	Anchor theming;

	@UiField
	Anchor langPt;

	@UiField
	Anchor langEn;

	@UiField
	Anchor logout;

	@UiField
	Anchor user;

	@UiField(provided = true)
	ContentPanel eastContentPanelTop;
	@UiField(provided = true)
	ContentPanel eastContentPanelBottom;
	@UiField(provided = true)
	ContentPanel westContentPanel;
	@UiField(provided = true)
	ContentPanel southContentPanel;
	@UiField(provided = true)
	ContentPanel centerContentPanel;

	@UiField(provided = true)
	SplitLayoutPanel splitPanel;

	@UiField(provided = true)
	SplitLayoutPanel splitEastPanel;

	@UiField
	DroppablePanel westWidget;
	@UiField
	DroppablePanel eastTopWidget;
	@UiField
	DroppablePanel eastBottomWidget;
	@UiField
	DroppablePanel centerWidget;
	@UiField
	DroppablePanel southWidget;

	private Presenter presenter = null;

	private String currentResId;

	private double westPanelWidthPct = 0.15;
	private double southPanelHeightPct = 0.15;
	private double eastPanelWidthPct = 0.20;

	private ResourcesTree resourcesTree;

	private Storage storage = null;
	private StorageMap storageMap = null;
	
	private ColorTweaker colorTweaker = null;

	public TopLevelViewImpl() {
		splitPanel = new SplitLayoutPanel(2) {
			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					resizeMainSplitPanelChildSizes();
				}
			};

			@Override
			public void onResize() {
				super.onResize();
				resizeTimer.cancel();
				resizeTimer.schedule(50);
			}
		};

		splitEastPanel = new SplitLayoutPanel(2);
		eastContentPanelTop = new ContentPanel("eastTopPanelPetcha");
		eastContentPanelBottom = new ContentPanel("eastBottomPanelPetcha");
		westContentPanel = new ContentPanel("westPanelPetcha");
		southContentPanel = new ContentPanel("southPanelPetcha");
		centerContentPanel = new ContentPanel("centerPanelPetcha");

		initWidget(uiBinder.createAndBindUi(this));

		logout.setText(constants.logout());
		help.setText(constants.help());
		theming.setText(constants.customize());
		splitPanel.getElement().setId("splitPanelPetcha");
		
		copyrightYear.setHTML(DateTimeFormat.getFormat( "d-M-yyyy" )
				.format( new Date() ).split( "-")[2]);

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				content.setPixelSize(
						Window.getClientWidth() - 30,
						Window.getClientHeight()
								- headerContainer.getOffsetHeight()
								- footerContainer.getOffsetHeight() - 20);
				splitPanel.setPixelSize(Window.getClientWidth() - 30,
						content.getOffsetHeight());
				splitEastPanel.setPixelSize(
						300,
						content.getOffsetHeight());
				splitPanel.onResize();
			}
		});

		// Window Resize
		Window.addResizeHandler(new ResizeHandler() {
			Timer resizeTimer = new Timer() {
				@Override
				public void run() {
					resizeMainSplitPanel();
				}
			};

			@Override
			public void onResize(ResizeEvent event) {
				resizeTimer.cancel();
				resizeTimer.schedule(250);
			}
		});
		

		/*searchBtn.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				searchTxt.setFocus(true);
			}
		}, ClickEvent.getType());*/

		film = new Raphael(Window.getClientWidth(), Window.getClientHeight());
		film.getElement().setClassName(style.film());
		
		Window.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent arg0) {
				film.setHeight(Window.getClientHeight() + "px");
				film.setWidth(Window.getClientWidth() + "px");
			}
		});
		
		storage = Storage.getLocalStorageIfSupported();
		if (storage != null)
			storageMap = new StorageMap(storage);
		
		setColors();
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public Presenter getPresenter() {
		return this.presenter;
	}

	@Override
	public String getCurrentResourceId() {
		return currentResId;
	}

	@Override
	public void setCurrentResourceId(String currentResId) {
		clearResourcesContentTabs();
		this.currentResId = currentResId;
	}

	/**
	 * @param currentResId
	 */
	private void clearResourcesContentTabs() {
		if (currentResId != null) {
			westContentPanel.removeTabs(currentResId);
			eastContentPanelTop.removeTabs(currentResId);
			eastContentPanelBottom.removeTabs(currentResId);
			centerContentPanel.removeTabs(currentResId);
			southContentPanel.removeTabs(currentResId);
		}
	}

	@Override
	public void addGadget(String id, Gadget gadget, Region region) {
		String tabName = gadget.getName();

		switch (region) {
		case NORTHEAST:
			eastContentPanelTop
					.addTab(tabName, gadget.getView().asWidget(), id);
			break;
		case WEST:
			westContentPanel.addTab(tabName, gadget.getView().asWidget(), id);
			break;
		case CENTER:
			centerContentPanel.addTab(tabName, gadget.getView().asWidget(), id);
			break;
		case EAST:
			eastContentPanelBottom.addTab(tabName, gadget.getView().asWidget(),
					id);
			break;
		case SOUTH:
			southContentPanel.addTab(tabName, gadget.getView().asWidget(), id);
			break;
		default:
			break;
		}
		splitPanel.onResize();
	}
	
	@Override
	public void selectTab(Region region, int tab) {
		switch (region) {
		case NORTHEAST:
			eastContentPanelTop.selectTab(tab);
			break;
		case WEST:
			westContentPanel.selectTab(tab);
			break;
		case CENTER:
			centerContentPanel.selectTab(tab);
			break;
		case EAST:
			eastContentPanelBottom.selectTab(tab);
			break;
		case SOUTH:
			southContentPanel.selectTab(tab);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void selectTab(String id, Widget widget) {
		if (eastContentPanelTop.selectTab(id, widget))
			return;
		if (westContentPanel.selectTab(id, widget))
			return;
		if (centerContentPanel.selectTab(id, widget))
			return;
		if (eastContentPanelBottom.selectTab(id, widget))
			return;
		if (southContentPanel.selectTab(id, widget))
			return;
	}

	/**
	 * Method called when splitPanel resizes
	 */
	private void resizeMainSplitPanelChildSizes() {

		westPanelWidthPct = (double) splitPanel.getWidgetContainerElement(
				westWidget).getOffsetWidth()
				/ (double) splitPanel.getOffsetWidth();
		eastPanelWidthPct = (double) splitPanel.getWidgetContainerElement(
				splitEastPanel).getOffsetWidth()
				/ (double) splitPanel.getOffsetWidth();
		southPanelHeightPct = (double) splitPanel.getWidgetContainerElement(
				southWidget).getOffsetHeight()
				/ (double) splitPanel.getOffsetHeight();

		splitEastPanel.setPixelSize(
				splitPanel.getWidgetContainerElement(splitEastPanel)
						.getOffsetWidth(), splitPanel.getOffsetHeight());
	}

	/**
	 * Method to resize splitPanel called when window resizes
	 */
	private void resizeMainSplitPanel() {

		content.setPixelSize(Window.getClientWidth() - 30,
				Window.getClientHeight() - headerContainer.getOffsetHeight()
						- footerContainer.getOffsetHeight() - 20);
		splitPanel.setPixelSize(content.getOffsetWidth(),
				content.getOffsetHeight());

		/*splitPanel.setWidgetSize(westWidget,
				westPanelWidthPct * splitPanel.getOffsetWidth());
		westWidget.onResize();

		splitPanel.setWidgetSize(splitEastPanel,
				eastPanelWidthPct * splitPanel.getOffsetWidth());
		splitEastPanel.onResize();
		
		splitPanel.setWidgetSize(southWidget,
				southPanelHeightPct * splitPanel.getOffsetHeight());
		southWidget.onResize();*/

		splitPanel.onResize();
	}

	@UiHandler("help")
	void help(ClickEvent e) {
		if (presenter != null)
			presenter.onHelpClicked();
	}

	@UiHandler("theming")
	void theming(ClickEvent e) {
		colorTweaker.center();
	}

	@UiHandler("logout")
	void logout(ClickEvent e) {
		if (presenter != null)
			presenter.onLogoutClicked();
	}

	@UiHandler("langPt")
	void changeLanguageToPT(ClickEvent e) {
		changeLanguage("pt");
	}

	@UiHandler("langEn")
	void changeLanguageToEN(ClickEvent e) {
		changeLanguage("en");
	}

	@Override
	public void setResourceTreeGadget(ResourcesTree gadget) {
		this.resourcesTree = gadget;
		addGadget(null, resourcesTree, Region.WEST);
	}

	@Override
	public void setContest(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTeam(String name) {
		user.setText(name);
		teamId = name;
	}

	/**
	 * @return the teamId
	 */
	@Override
	public String getTeam() {
		return teamId;
	}

	@Override
	public void setDates(Date start, Date end, Date current) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void updateResourceState(String courseId, String id,
			ResourceState state) {
		((ResourcesTreeView) resourcesTree.getView()).updateResourceState(courseId, 
				id, state);
	}
	
	public void changeLanguage(String lang) {

		UrlBuilder builder = Location.createUrlBuilder().setParameter("locale",
				lang);
		Window.Location
				.replace(builder.buildString().replaceAll("%3A9997", ""));
	}

	@Override
	public Raphael showTutorialPanel() {
		if(basePanel.getWidgetIndex(film) == -1)
			basePanel.add(film);
		return film;
	}

	@Override
	public void hideTutorialPanel() {
		basePanel.remove(film);
	}

	@Override
	public void setTutorialAnchorPoints(TutorialView tutorialView) {
		tutorialView.updateAnchorPoints(westWidget, 
				centerWidget, southWidget, eastTopWidget, 
				eastBottomWidget);
	}

	
	/* THEME CUSTOMIZATION */
	
	private void setColors() {

		final String prefix = teamId + ".theming.";
		String bgColor1 = "#BBC", bgColor2 = "#FFF", panelColor = "#DDD", fontColor = "#000";
		if (storageMap.containsKey(prefix + ".bgColor1")) {
			bgColor1 = storageMap.get(prefix + ".bgColor1");
		}
		if (storageMap.containsKey(prefix + ".bgColor2")) {
			bgColor2 = storageMap.get(prefix + ".bgColor2");
		}
		if (storageMap.containsKey(prefix + ".panelColor")) {
			panelColor = storageMap.get(prefix + ".panelColor");
		}
		if (storageMap.containsKey(prefix + ".fontColor")) {
			fontColor = storageMap.get(prefix + ".fontColor");
		}
		
		colorTweaker = new ColorTweaker();
		colorTweaker.addGradientColorSelector("bgColor", "Background Color: ", bgColor1, bgColor2);
		//colorTweaker.addColorSelector("panelColor", "Panel Color: ", panelColor);
		//colorTweaker.addColorSelector("fontColor", "Font Color: ", fontColor);
		
		colorTweaker.addColorChangeHandler("bgColor", new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				String[] colors = event.getValue().split(",");
				storageMap.put(prefix + ".bgColor1", colors[0]);
				storageMap.put(prefix + ".bgColor2", colors[1]);
				
				basePanel.getElement().getStyle().setProperty("background", "linear-gradient(" + event.getValue() + ")");
			}
		});
		
		/*colorTweaker.addColorChangeHandler("panelColor", new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				storageMap.put(prefix + ".panelColor", event.getValue());
				
				$(".gwt-TabLayoutPanel .gwt-TabLayoutPanelContentContainer, "
						+ ".gwt-TabLayoutPanel .gwt-TabLayoutPanelTab,"
						+ ".gwt-TabBarItem, .gwt-TabLayoutPanelTab")
					.css("background", event.getValue());
			}
		});*/
		
		// set defaults
		basePanel.getElement().getStyle().setProperty("background", 
				"linear-gradient(" + bgColor1 + "," + bgColor2 + ")");
		
		/*Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				$(".gwt-TabLayoutPanel .gwt-TabLayoutPanelContentContainer, "
						+ ".gwt-TabLayoutPanel .gwt-TabLayoutPanelTab,"
						+ ".gwt-TabBarItem, .gwt-TabLayoutPanelTab")
					.css("background", storageMap.get(prefix + ".panelColor"));
			}
		});*/
		
		
	}
}
