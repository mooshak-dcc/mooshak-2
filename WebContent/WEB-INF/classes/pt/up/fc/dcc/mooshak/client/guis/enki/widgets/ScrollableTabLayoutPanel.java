package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.gadgets.diagrameditor.DiagramEditorView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorView;
import pt.up.fc.dcc.mooshak.client.guis.enki.resources.EnkiResources;
import pt.up.fc.dcc.mooshak.client.guis.enki.view.TutorialView;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.HasBeforeSelectionHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.layout.client.Layout.Alignment;
import com.google.gwt.layout.client.Layout.AnimationCallback;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AnimatedLayout;
import com.google.gwt.user.client.ui.AttachDetachException;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class ScrollableTabLayoutPanel extends ResizeComposite implements
	HasWidgets, ProvidesResize, IndexedPanel.ForIsWidget, AnimatedLayout,
	HasBeforeSelectionHandlers<Integer>, HasSelectionHandlers<Integer> {
	
	private static final String SCROLL_BUTTON_STYLE = "gwt-TabLayoutPanelScrollButton";
	private static final String SCROLL_PANEL_STYLE = "gwt-TabLayoutPanelScrollPanel";

	private final double barHeight;
	private final Unit barUnit;
	private final EnkiResources resources;

	private LayoutPanel panel;
	private FlowPanel tabBarPanel;
	private HorizontalPanel scrollPanel;

	private static EnkiResources DEFAULT_RESOURCES;

	public ScrollableTabLayoutPanel(String id) {
		this(id, 30, Unit.PX);
	}

	public ScrollableTabLayoutPanel(String id, double barHeight, Unit barUnit) {
		this(id, barHeight, barUnit, getDefaultResources());
	}
	
	private HandlerRegistration handlers;
	private Image scrollLeftButtonImage;
	private Image scrollRightButtonImage;
	private Image menuButtonImage;

	public ScrollableTabLayoutPanel(String id, double barHeight, Unit barUnit,
			EnkiResources resources) {
		LayoutPanel panel = new LayoutPanel();
		initWidget(panel);

		// Add the tab bar to the panel.
		panel.add(tabBar);
		panel.setWidgetLeftRight(tabBar, 0, Unit.PX, 0, Unit.PX);
		panel.setWidgetTopHeight(tabBar, 0, Unit.PX, barHeight, barUnit);
		panel.setWidgetVerticalPosition(tabBar, Alignment.END);

		// Add the deck panel to the panel.
		deckPanel.addStyleName(CONTENT_CONTAINER_STYLE);
		panel.add(deckPanel);
		panel.setWidgetLeftRight(deckPanel, 0, Unit.PX, 0, Unit.PX);
		panel.setWidgetTopBottom(deckPanel, barHeight, barUnit, 0, Unit.PX);

		tabBar.getElement().getStyle().setHeight(100, Unit.PCT);
		tabBar.getOriginalWidget().getElement().getStyle()
				.setWidth(BIG_ENOUGH_TO_NOT_WRAP, Unit.PX);

		tabBar.getOriginalWidget().setStyleName("gwt-TabLayoutPanelTabs");
		tabBar.getOriginalWidget().getElement().getStyle().setPosition(Position.RELATIVE);
		setStyleName("gwt-TabLayoutPanel");
		
		tabBar.getOriginalWidget().getElement().getParentElement()
			.getStyle().setPosition(Position.RELATIVE);
		tabBar.getElement()
			.getStyle().setOverflow(Overflow.HIDDEN);

		this.barUnit = barUnit;
		this.barHeight = barHeight;
		this.resources = resources;

		this.panel = (LayoutPanel) getWidget();

		for (int i = 0; i < panel.getWidgetCount(); i++) {
			Widget widget = panel.getWidget(i);
			if (widget instanceof DroppableWidget<?>) {
				if (((DroppableWidget<?>) widget).getOriginalWidget() instanceof FlowPanel) {
					tabBarPanel = ((DroppableTabBar) widget).getOriginalWidget();
					break;
				}
			}
		}

		initScrollButtons();
	}

	@Override
	public void onResize() {
		super.onResize();
		
		deckPanel.onResize();
		
		showScrollButtonsIfNecessary();
	}

	public void insert(Widget child, Widget tab, String id, int beforeIndex) {
		deckPanel.forceLayout();
		DraggableCustomTab dragTab;
		if (tab instanceof DraggableCustomTab)
			dragTab = (DraggableCustomTab) tab;
		else
			dragTab = new DraggableCustomTab(tab, id, this);
		
		insert(child, dragTab, beforeIndex);
		showScrollButtonsIfNecessary();
		
		if (id != null) {
			if (gadgets.containsKey(id)) {
				
				if (gadgets.get(id).contains(child))
					return;
				
				gadgets.get(id).ensureCapacity(gadgets.size()+1);
				gadgets.get(id).add(getWidgetIndex(child), child);

			}
			else
				gadgets.put(id, new ArrayList<Widget>(Arrays.asList(child)));
		}
	}

	public boolean remove(int index) {

		if ((index < 0) || (index >= getWidgetCount())) {
			return false;
		}

		Widget child = getWidget(index);
		tabBar.remove(index);
		deckPanel.removeProtected(child);
		child.removeStyleName(CONTENT_STYLE);

		DraggableCustomTab tab = tabs.remove(index);
		tab.getTabWidget().removeFromParent();
		
		String resId = tab.getOriginalWidget().getResourceId();
		if (resId != null) {
			gadgets.get(resId).remove(child);
		}

		if (index == selectedIndex) {
			// If the selected tab is being removed, select the first tab (if
			// there
			// is one).
			selectedIndex = -1;
			if (getWidgetCount() > 0) {
				selectTab(0);
			}
		} else if (index < selectedIndex) {
			// If the selectedIndex is greater than the one being removed, it
			// needs
			// to be adjusted.
			--selectedIndex;
		}
		showScrollButtonsIfNecessary();
		return true;
	}

	public void selectTab(int index, boolean fireEvents) {
		deckPanel.forceLayout();
		checkIndex(index);
		if (index == selectedIndex) {
			return;
		}

		// Fire the before selection event, giving the recipients a chance to
		// cancel the selection.
		if (fireEvents) {
			BeforeSelectionEvent<Integer> event = BeforeSelectionEvent.fire(
					this, index);
			if ((event != null) && event.isCanceled()) {
				return;
			}
		}

		// Update the tabs being selected and unselected.
		if (selectedIndex != -1) {
			tabs.get(selectedIndex).setSelected(false);
		}

		deckPanel.showWidget(index);
		tabs.get(index).setSelected(true);
		selectedIndex = index;
		
		Widget w = deckPanel.getWidget(index);
		if (w instanceof DiagramEditorView || w instanceof ProgramEditorView)
			TutorialView.getInstance().setEditorSelected(true);
		else
			TutorialView.getInstance().setEditorSelected(false);
		
		if (w instanceof RequiresResize)
			((RequiresResize) w).onResize();
		// Fire the selection event.
		if (fireEvents) {
			SelectionEvent.fire(this, index);
		}

		// all the code below is for automatic scrolling if selected tab is out
		// of visible area
		Widget selectedTab = tabBarPanel.getWidget(getSelectedIndex());

		int visibleAreaLeftBorder = Math.abs(getCurrentShift());
		int visibleAreaRightBorder = visibleAreaLeftBorder + getTabBarWidth();
		int halfVisibleAreaWidth = getTabBarWidth() / 2;
		int halfTabWidth = (getRightPosition(selectedTab) - getLeftPosition(selectedTab)) / 2;

		if (getLeftPosition(selectedTab) < visibleAreaLeftBorder) {
			// GWT.log("Need scroll to the right");
			int scrollValue = visibleAreaLeftBorder
					- getLeftPosition(selectedTab) + halfVisibleAreaWidth
					- halfTabWidth;
			adjustScroll(scrollValue);
		} else if (getRightPosition(selectedTab) > visibleAreaRightBorder) {
			// GWT.log("Need scroll to the left");
			int scrollValue = getRightPosition(selectedTab)
					- visibleAreaRightBorder + halfVisibleAreaWidth
					- halfTabWidth;
			adjustScroll(-scrollValue);
		}
	}

	private void showScrollButtonsIfNecessary() {
		// Defer size calculations until sizes are available.
		// When calculating immediately after add(), all size methods return
		// zero.
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				boolean scrollingNecessary = isScrollingNecessary();
				if (scrollPanel.isVisible()) {
					if (!scrollingNecessary) {
						// The scroll buttons are being hidden, reset the scroll
						// position to zero to avoid
						// having tabs starting in the middle of the window!
						scrollTo(0);
					} else {
						// Resizing or adding / removing tabs, recompute the
						// scroll
						adjustScroll(0);
					}
				}

				scrollPanel.setVisible(scrollingNecessary);
				// setting margin for tab bar to free space for scroll panel
				int marginRight = scrollingNecessary ? getScrollPanelWidth()
						: 0;
				tabBar.getElement().getParentElement().getStyle()
						.setMarginRight(marginRight, Unit.PX);
			}
		});
	}

	/**
	 * Create and attach the scroll button images with a click handler
	 */
	private void initScrollButtons() {
		scrollLeftButtonImage = new Image(resources.back());
		scrollRightButtonImage = new Image(resources.next());
		menuButtonImage = new Image(resources.menu());

		int leftArrowWidth = scrollLeftButtonImage.getWidth();
		int rightArrowWidth = scrollRightButtonImage.getWidth();
		int menuWidth = menuButtonImage.getWidth();

		// panel for scroll buttons
		scrollPanel = new HorizontalPanel();
		panel.insert(scrollPanel, 0);
		panel.setWidgetTopHeight(scrollPanel, 0, Unit.PX, barHeight, barUnit);

		// placing scroll panel in the top right corner
		panel.setWidgetRightWidth(scrollPanel, 0, Unit.PX, leftArrowWidth
				+ rightArrowWidth + menuWidth, Unit.PX);
		scrollPanel.setHeight("100%");
		scrollPanel.setWidth("100%");
		scrollPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		scrollPanel.setStyleName(SCROLL_PANEL_STYLE);

		SimplePanel scrollLeftButton = new SimplePanel(scrollLeftButtonImage);
		scrollLeftButton.setStyleName(SCROLL_BUTTON_STYLE);
		scrollLeftButtonImage.addClickHandler(createScrollLeftClickHandler());

		SimplePanel scrollRightButton = new SimplePanel(scrollRightButtonImage);
		scrollRightButton.setStyleName(SCROLL_BUTTON_STYLE);
		scrollRightButtonImage.addClickHandler(createScrollRightClickHandler());

		SimplePanel menuButton = new SimplePanel(menuButtonImage);
		menuButton.setStyleName(SCROLL_BUTTON_STYLE);
		handlers = menuButtonImage
				.addClickHandler(createShowMenuClickHandler(menuButtonImage));

		scrollPanel.add(scrollLeftButton);
		scrollPanel.add(scrollRightButton);
		scrollPanel.add(menuButton);
		scrollPanel.setVisible(false);
	}
	
	public void refreshHandlers() {
		handlers.removeHandler();
		handlers = menuButtonImage
				.addClickHandler(createShowMenuClickHandler(menuButtonImage));
	}

	private ClickHandler createShowMenuClickHandler(final Image buttonImage) {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				final MyPopup popup = new MyPopup();

				Scheduler.get().scheduleDeferred(
						new Scheduler.ScheduledCommand() {
							@Override
							public void execute() {
								popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
									public void setPosition(int offsetWidth,
											int offsetHeight) {
										final int SCROLL_BUFFER = 20;
										// coordinates of menu button for
										// showing popup next to it
										int left = buttonImage.getElement()
												.getAbsoluteLeft();
										int top = buttonImage.getElement()
												.getAbsoluteBottom();

										if (offsetHeight > Window
												.getClientHeight()) {

											int diff = offsetHeight
													- popup.getWidget()
															.getOffsetHeight();

											popup.setHeight(Window
													.getClientHeight()
													- diff
													+ "px");
											top = 0;

											popup.setWidth((popup.getWidget()
													.getOffsetWidth() + SCROLL_BUFFER)
													+ "px");
											offsetWidth = offsetWidth
													+ SCROLL_BUFFER;

										} else if (Window.getClientHeight() < (top + offsetHeight)) {
											top = Window.getClientHeight()
													- offsetHeight;
										}

										// the same stuff but for the width
										if (offsetWidth > Window
												.getClientWidth()) { 
											int diff = offsetWidth
													- popup.getWidget()
															.getOffsetWidth();
											popup.setWidth(Window
													.getClientWidth()
													- diff
													+ "px");
											left = 0;
										} else if (Window.getClientWidth() < (left + offsetWidth)) {
											left = Window.getClientWidth()
													- offsetWidth;
										}

										popup.setPopupPosition(left, top);
									}
								});
							}
						});
			}
		};
	}

	private ClickHandler createScrollRightClickHandler() {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int visibleAreaLeftBorder = Math.abs(getCurrentShift());
				int visibleAreaRightBorder = visibleAreaLeftBorder
						+ getTabBarWidth();

				for (int i = 0; i < tabBarPanel.getWidgetCount(); i++) {
					int tabRightBorder = getRightPosition(tabBarPanel.getWidget(i));

					if (tabRightBorder > visibleAreaRightBorder) {
						int diff = tabRightBorder - visibleAreaRightBorder;
						adjustScroll(-diff);
						return;
					}
				}
			}
		};
	}

	private ClickHandler createScrollLeftClickHandler() {
		return new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				int visibleAreaLeftBorder = Math.abs(getCurrentShift());

				for (int i = tabBarPanel.getWidgetCount() - 1; i >= 0; i--) {
					int tabLeftBorder = getLeftPosition(tabBarPanel.getWidget(i));

					if (tabLeftBorder < visibleAreaLeftBorder) {
						if (i == 0) { // to show margin
							scrollTo(0);
							return;
						}

						int diff = visibleAreaLeftBorder - tabLeftBorder;
						adjustScroll(diff);
						return;
					}
				}
			}
		};
	}

	private void adjustScroll(int diff) {
		Widget lastTab = getLastTab();
		if (lastTab == null)
			return;

		int newLeft = getCurrentShift() + diff;
		int rightOfLastTab = getRightPosition(lastTab);

		// Don't scroll for a positive newLeft
		if (newLeft <= 0) {
			// If we are about to scroll too far away from the right border,
			// adjust back
			int gap = rightOfLastTab - getTabBarWidth();
			if (gap < -newLeft) {
				newLeft += -newLeft - gap;
			}
			scrollTo(newLeft);
		} else {
			scrollTo(0);
		}
	}

	private void scrollTo(int pos) {
		final int currentPos = getCurrentShift();
		final int diff = pos - currentPos;

		new Animation() {
			@Override
			protected void onUpdate(double progress) {
				tabBarPanel.getElement().getStyle()
						.setLeft(currentPos + diff * progress, Unit.PX);
			}
		}.run(600);
	}

	private boolean isScrollingNecessary() {
		Widget lastTab = getLastTab();
		return lastTab != null && getRightPosition(lastTab) > getTabBarWidth();
	}

	private int getRightPosition(Widget widget) {
		return widget.getElement().getOffsetLeft()
				+ widget.getElement().getOffsetWidth();
	}

	private int getLeftPosition(Widget widget) {
		return widget.getElement().getOffsetLeft();
	}

	private int getCurrentShift() {
		return parsePosition(tabBarPanel.getElement().getStyle().getLeft());
	}

	private int getTabBarWidth() {
		return tabBarPanel.getElement().getParentElement().getClientWidth();
	}

	private int getScrollPanelWidth() {
		return scrollPanel.getElement().getParentElement().getClientWidth();
	}

	private Widget getLastTab() {
		if (tabBarPanel.getWidgetCount() == 0)
			return null;

		return tabBarPanel.getWidget(tabBarPanel.getWidgetCount() - 1);
	}

	/**
	 * get the int value from string, particularly css attribute value. For
	 * example, for "-25px" returns -25
	 *
	 * @param positionString
	 *            string to be parsed
	 * @return parsed int
	 */
	private static int parsePosition(String positionString) {
		if (positionString == null || positionString.isEmpty())
			return 0;

		int position = 0;
		int sign = 1;
		int i = 0;
		if (positionString.charAt(0) == '-') {
			sign = -1;
			i++;
		}
		for (; i < positionString.length(); i++) {
			char c = positionString.charAt(i);
			if (c < '0' || c > '9')
				break;
			position = 10 * position + c - '0';
		}

		return sign * position;
	}

	private static EnkiResources getDefaultResources() {
		if (DEFAULT_RESOURCES == null) {
			DEFAULT_RESOURCES = GWT.create(EnkiResources.class);
		}
		return DEFAULT_RESOURCES;
	}

	/**
	 * Popup with separators chooser
	 * 
	 * @author josepaiva
	 */
	private class MyPopup extends PopupPanel {

		public MyPopup() {
			super(true);

			List<Widget> tabs = new ArrayList<Widget>();
			for (int i = 0; i < tabBarPanel.getWidgetCount(); i++) {
					tabs.add(tabBarPanel.getWidget(i));
			}

			CellTable<Widget> table = new CellTable<Widget>();
			table.setRowData(tabs);
			table.getElement().getStyle()
					.setWhiteSpace(Style.WhiteSpace.NOWRAP);

			final SingleSelectionModel<Widget> selectionModel = new SingleSelectionModel<Widget>();

			// todo find a way to set selected but not fire event
			// selectionModel.setSelected(tabBar.getWidget(ScrollableTabLayoutPanel.this.getSelectedIndex()),
			// true);
			table.setSelectionModel(selectionModel);

			selectionModel
					.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
						@Override
						public void onSelectionChange(SelectionChangeEvent event) {
							Widget selected = selectionModel
									.getSelectedObject();
							/*Logger.getLogger("").log(Level.SEVERE, tabBarPanel
									.getWidgetIndex(selected)+"");*/
							ScrollableTabLayoutPanel.this.selectTab(tabBarPanel
									.getWidgetIndex(selected));
							hide();
						}
					});

			Column<Widget, SafeHtml> column = new Column<Widget, SafeHtml>(
					new SafeHtmlCell()) {
				@Override
				public SafeHtml getValue(Widget object) {
					object = ((DraggableCustomTab) object).getOriginalWidget();
					if(((SimplePanel) object).getWidget() instanceof ProvidesHtmlElement)
						return SafeHtmlUtils.fromSafeConstant((
								(ProvidesHtmlElement)((SimplePanel) object).getWidget()).getSpecificHtml());
					return SafeHtmlUtils.fromSafeConstant(object.getElement()
							.getInnerHTML());
				}
			};

			table.addColumn(column);

			// put the table into the scroll panel for the case if it height
			// exceeds the window height
			setWidget(new ScrollPanel(table));
		}
	}
	


	/**
	 * This extension of DeckLayoutPanel overrides the public mutator methods to
	 * prevent external callers from adding to the state of the DeckPanel.
	 * <p>
	 * Removal of Widgets is supported so that WidgetCollection.WidgetIterator
	 * operates as expected.
	 * </p>
	 * <p>
	 * We ensure that the DeckLayoutPanel cannot become of of sync with its
	 * associated TabBar by delegating all mutations to the TabBar to this
	 * implementation of DeckLayoutPanel.
	 * </p>
	 */
	private class TabbedDeckLayoutPanel extends DeckLayoutPanel {
		
		int count = 0;

		@Override
		public void add(Widget w) {
			throw new UnsupportedOperationException(
					"Use TabLayoutPanel.add() to alter the DeckLayoutPanel");
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException(
					"Use TabLayoutPanel.clear() to alter the DeckLayoutPanel");
		}

		@Override
		public void insert(Widget w, int beforeIndex) {
			throw new UnsupportedOperationException(
					"Use TabLayoutPanel.insert() to alter the DeckLayoutPanel");
		}

		@Override
		public boolean remove(Widget w) {
			/*
			 * Removal of items from the DeckLayoutPanel is delegated to the
			 * TabLayoutPanel to ensure consistency.
			 */
			return ScrollableTabLayoutPanel.this.remove(w);
		}

		protected void insertProtected(Widget w, int beforeIndex) {
			super.insert(w, beforeIndex);
			count++;
		}

		protected void removeProtected(Widget w) {
			try {
				super.remove(w);
				count--;
			} catch (AttachDetachException e) {
				w.removeFromParent();
				count--;
			}
		}
		
		@Override
		public int getWidgetCount() {
			return count;
		}
	}

	private static final String CONTENT_CONTAINER_STYLE = "gwt-TabLayoutPanelContentContainer";
	private static final String CONTENT_STYLE = "gwt-TabLayoutPanelContent";

	private static final int BIG_ENOUGH_TO_NOT_WRAP = 16384;

	private final TabbedDeckLayoutPanel deckPanel = new TabbedDeckLayoutPanel();
	private final DroppableTabBar tabBar = new DroppableTabBar(this);
	private final ArrayList<DraggableCustomTab> tabs = new ArrayList<DraggableCustomTab>();
	private int selectedIndex = -1;
	private boolean draggingTab = false;
	

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void add(IsWidget w) {
		add(asWidgetOrNull(w));
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void add(IsWidget w, IsWidget tab) {
		add(asWidgetOrNull(w), asWidgetOrNull(tab));
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void add(IsWidget w, String text) {
		add(asWidgetOrNull(w), text);
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void add(IsWidget w, String text, boolean asHtml) {
		add(asWidgetOrNull(w), text, asHtml);
	}

	public void add(Widget w) {
		insert(w, getWidgetCount());
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 */
	public void add(Widget child, String text) {
		insert(child, text, getWidgetCount());
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param html
	 *            the html to be shown on its tab
	 */
	public void add(Widget child, SafeHtml html) {
		add(child, html.asString(), true);
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 * @param asHtml
	 *            <code>true</code> to treat the specified text as HTML
	 */
	public void add(Widget child, String text, boolean asHtml) {
		insert(child, text, asHtml, getWidgetCount());
	}

	/**
	 * Adds a widget to the panel. If the Widget is already attached, it will be
	 * moved to the right-most index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param tab
	 *            the widget to be placed in the associated tab
	 */
	public void add(Widget child, Widget tab, String id) {
		insert(child, tab, id, getWidgetCount());
	}

	public HandlerRegistration addBeforeSelectionHandler(
			BeforeSelectionHandler<Integer> handler) {
		return addHandler(handler, BeforeSelectionEvent.getType());
	}

	public HandlerRegistration addSelectionHandler(
			SelectionHandler<Integer> handler) {
		return addHandler(handler, SelectionEvent.getType());
	}

	public void animate(int duration) {
		animate(duration, null);
	}

	public void animate(int duration, AnimationCallback callback) {
		deckPanel.animate(duration, callback);
	}

	public void clear() {
		Iterator<Widget> it = iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}

	public void forceLayout() {
		deckPanel.forceLayout();
	}

	/**
	 * Get the duration of the animated transition between tabs.
	 * 
	 * @return the duration in milliseconds
	 */
	public int getAnimationDuration() {
		return deckPanel.getAnimationDuration();
	}

	/**
	 * Gets the index of the currently-selected tab.
	 *
	 * @return the selected index, or <code>-1</code> if none is selected.
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Gets the widget in the tab at the given index.
	 *
	 * @param index
	 *            the index of the tab to be retrieved
	 * @return the tab's widget
	 */
	public Widget getTabWidget(int index) {
		checkIndex(index);
		return tabs.get(index).getTabWidget();
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public Widget getTabWidget(IsWidget child) {
		return getTabWidget(asWidgetOrNull(child));
	}

	/**
	 * Gets the widget in the tab associated with the given child widget.
	 *
	 * @param child
	 *            the child whose tab is to be retrieved
	 * @return the tab's widget
	 */
	public Widget getTabWidget(Widget child) {
		checkChild(child);
		return getTabWidget(getWidgetIndex(child));
	}

	/**
	 * Returns the widget at the given index.
	 */
	public Widget getWidget(int index) {
		return deckPanel.getWidget(index);
	}

	/**
	 * Returns the number of tabs and widgets.
	 */
	public int getWidgetCount() {
		return deckPanel.getWidgetCount();
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public int getWidgetIndex(IsWidget child) {
		return getWidgetIndex(asWidgetOrNull(child));
	}

	/**
	 * Returns the index of the given child, or -1 if it is not a child.
	 */
	public int getWidgetIndex(Widget child) {
		return deckPanel.getWidgetIndex(child);
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void insert(IsWidget child, int beforeIndex) {
		insert(asWidgetOrNull(child), beforeIndex);
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void insert(IsWidget child, IsWidget tab, int beforeIndex) {
		insert(asWidgetOrNull(child), asWidgetOrNull(tab), beforeIndex);
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void insert(IsWidget child, String text, boolean asHtml,
			int beforeIndex) {
		insert(asWidgetOrNull(child), text, asHtml, beforeIndex);
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void insert(IsWidget child, String text, int beforeIndex) {
		insert(asWidgetOrNull(child), text, beforeIndex);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, int beforeIndex) {
		insert(child, "", beforeIndex);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param html
	 *            the html to be shown on its tab
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, SafeHtml html, int beforeIndex) {
		insert(child, html.asString(), true, beforeIndex);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 * @param asHtml
	 *            <code>true</code> to treat the specified text as HTML
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, String text, boolean asHtml,
			int beforeIndex) {
		Widget contents;
		if (asHtml) {
			contents = new HTML(text);
		} else {
			contents = new Label(text);
		}
		insert(child, contents, beforeIndex);
	}

	/**
	 * Inserts a widget into the panel. If the Widget is already attached, it
	 * will be moved to the requested index.
	 *
	 * @param child
	 *            the widget to be added
	 * @param text
	 *            the text to be shown on its tab
	 * @param beforeIndex
	 *            the index before which it will be inserted
	 */
	public void insert(Widget child, String text, int beforeIndex) {
		insert(child, text, false, beforeIndex);
	}

	/**
	 * Check whether or not transitions slide in vertically or horizontally.
	 * Defaults to horizontally.
	 * 
	 * @return true for vertical transitions, false for horizontal
	 */
	public boolean isAnimationVertical() {
		return deckPanel.isAnimationVertical();
	}

	public Iterator<Widget> iterator() {
		return deckPanel.iterator();
	}

	public boolean remove(Widget w) {
		int index = getWidgetIndex(w);
		if (index == -1) {
			return false;
		}

		return remove(index);
	}

	/**
	 * Programmatically selects the specified tab and fires events.
	 *
	 * @param index
	 *            the index of the tab to be selected
	 */
	public void selectTab(int index) {
		selectTab(index, true);
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void selectTab(IsWidget child) {
		selectTab(asWidgetOrNull(child));
	}

	/**
	 * Convenience overload to allow {@link IsWidget} to be used directly.
	 */
	public void selectTab(IsWidget child, boolean fireEvents) {
		selectTab(asWidgetOrNull(child), fireEvents);
	}

	/**
	 * Programmatically selects the specified tab and fires events.
	 *
	 * @param child
	 *            the child whose tab is to be selected
	 */
	public void selectTab(Widget child) {
		selectTab(getWidgetIndex(child));
	}

	/**
	 * Programmatically selects the specified tab.
	 *
	 * @param child
	 *            the child whose tab is to be selected
	 * @param fireEvents
	 *            true to fire events, false not to
	 */
	public void selectTab(Widget child, boolean fireEvents) {
		selectTab(getWidgetIndex(child), fireEvents);
	}

	/**
	 * Set the duration of the animated transition between tabs.
	 * 
	 * @param duration
	 *            the duration in milliseconds.
	 */
	public void setAnimationDuration(int duration) {
		deckPanel.setAnimationDuration(duration);
	}

	/**
	 * Set whether or not transitions slide in vertically or horizontally.
	 * 
	 * @param isVertical
	 *            true for vertical transitions, false for horizontal
	 */
	public void setAnimationVertical(boolean isVertical) {
		deckPanel.setAnimationVertical(isVertical);
	}

	/**
	 * Sets a tab's HTML contents.
	 *
	 * Use care when setting an object's HTML; it is an easy way to expose
	 * script-based security problems. Consider using
	 * {@link #setTabHTML(int, SafeHtml)} or {@link #setTabText(int, String)}
	 * whenever possible.
	 *
	 * @param index
	 *            the index of the tab whose HTML is to be set
	 * @param html
	 *            the tab's new HTML contents
	 */
	public void setTabHTML(int index, String html) {
		checkIndex(index);
		tabs.get(index).setWidget(new HTML(html));
	}

	/**
	 * Sets a tab's HTML contents.
	 *
	 * @param index
	 *            the index of the tab whose HTML is to be set
	 * @param html
	 *            the tab's new HTML contents
	 */
	public void setTabHTML(int index, SafeHtml html) {
		setTabHTML(index, html.asString());
	}

	/**
	 * Sets a tab's text contents.
	 *
	 * @param index
	 *            the index of the tab whose text is to be set
	 * @param text
	 *            the object's new text
	 */
	public void setTabText(int index, String text) {
		checkIndex(index);
		tabs.get(index).setWidget(new Label(text));
	}

	private void checkChild(Widget child) {
		assert getWidgetIndex(child) >= 0 : "Child is not a part of this panel";
	}

	private void checkIndex(int index) {
		assert (index >= 0) && (index < getWidgetCount()) : "Index out of bounds";
	}

	private void insert(final Widget child, Widget tab, int beforeIndex) {
		assert (beforeIndex >= 0) && (beforeIndex <= getWidgetCount()) : "beforeIndex out of bounds";

		// Check to see if the TabPanel already contains the Widget. If so,
		// remove it and see if we need to shift the position to the left.
		int idx = getWidgetIndex(child);
		if (idx != -1) {
			remove(child);
			if (idx < beforeIndex) {
				beforeIndex--;
			}
		}

		deckPanel.insertProtected(child, beforeIndex);
		tabs.add(beforeIndex, (DraggableCustomTab) tab);

		tabBar.insert((DraggableCustomTab) tab, beforeIndex);
		((DraggableCustomTab) tab).addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				selectTab(child);
			}
		});

		child.addStyleName(CONTENT_STYLE);

		if (selectedIndex == -1) {
			selectTab(0);
		} else if (selectedIndex >= beforeIndex) {
			// If we inserted before the currently selected tab, its index has
			// just
			// increased.
			selectedIndex++;
		}
		
		showScrollButtonsIfNecessary();
		scrollTo(getLeftPosition(child));
		selectTab(getWidgetIndex(child));
	}

	/**
	 * @return the draggingTab
	 */
	public boolean isDraggingTab() {
		return draggingTab;
	}

	/**
	 * @param draggingTab the draggingTab to set
	 */
	public void setDraggingTab(boolean draggingTab) {
		this.draggingTab = draggingTab;
	}
	
	public int getTabIndex(Widget tab) {
		return tabBar.getWidgetIndex(tab);
	}
	
	
	
	private Map<String, ArrayList<Widget>> gadgets = new HashMap<String, ArrayList<Widget>>();
	
	/**
	 * Select Tab containing widget for the resource identified by id
	 * Ignores if it does not contain the widget
	 * @param id
	 * @param widget
	 * @return boolean true if the tab was selected
	 */
	public boolean selectTab(String id, Widget widget) {
		if (gadgets.get(id) == null) 
			return false;
		ArrayList<Widget> widgets = new ArrayList<Widget>(gadgets.get(id));
		
		if (!widgets.contains(widget)) 
			return false;
		
		selectTab(widgets.indexOf(widget));
		
		return true;
	}
	
	/**
	 * Remove tabs related to a resource id
	 * 
	 * @param resourceId
	 */
	public void removeTabs(String resourceId) {
		if (gadgets.get(resourceId) == null) 
			return;
		ArrayList<Widget> widgets = new ArrayList<Widget>();
		for (Widget widget : gadgets.get(resourceId)) {
			widgets.add(widget);
		}

		for (Widget widget : widgets) {
			try {
				remove(widget);
			} catch (Exception e) {
				// ignore silently
			}
		}
		
		gadgets.remove(resourceId);
	}
}
