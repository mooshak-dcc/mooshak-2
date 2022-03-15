package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class DroppableTabBar extends DroppableWidget<FlowPanel>
		implements HasWidgets, ProvidesResize, RequiresResize {

	public interface Resources extends ClientBundle {

		public interface Css extends CssResource {
			String sortablePanel();
		}

		Resources INSTANCE = GWT.create(Resources.class);

		@Source("ContentPanel.css")
		Css css();
	}

	interface DroppableTabBarBinder extends UiBinder<Widget, DroppableTabBar> {
	}

	private static UiBinder<Widget, DroppableTabBar> binder = GWT
			.create(DroppableTabBarBinder.class);

	@UiField
	FlowPanel tabBar;
	
	private ScrollableTabLayoutPanel tabLayoutPanel;

	public DroppableTabBar(ScrollableTabLayoutPanel scrollableTabLayoutPanel) {
		tabLayoutPanel = scrollableTabLayoutPanel;
		initWidget(binder.createAndBindUi(this));
		tabBar.setStyleName(Resources.INSTANCE.css().sortablePanel());
		setupDrop();
	}

	@Override
	public void clear() {
		tabBar.clear();
	}

	@Override
	public boolean remove(Widget w) {
		return tabBar.remove(w);
	}

	@Override
	public Iterator<Widget> iterator() {
		return tabBar.iterator();
	}

	@Override
	public void add(Widget w) {
		tabBar.add(w);
	}

	public void remove(int index) {
		tabBar.remove(index);
	}

	public void insert(DraggableCustomTab tab, int beforeIndex) {
		tabBar.insert(tab, beforeIndex);
	}
	
	public int getWidgetIndex(Widget w) {
		return tabBar.getWidgetIndex(w);
	}
	
	@Override
	public FlowPanel getOriginalWidget() {
		return tabBar;
	}

	/**
	 * Register drop handler
	 */
	private void setupDrop() {
		TabBarDragAndDropHandler sortableHandler = new TabBarDragAndDropHandler(
				tabBar, tabLayoutPanel);
		addDropHandler(sortableHandler);
		addOutDroppableHandler(sortableHandler);
		addOverDroppableHandler(sortableHandler);
		setTolerance(DroppableTolerance.POINTER);
		setDragAndDropScope("TabBar");
		setGreedy(true);
	}

	@Override
	public void onResize() {
		Widget child;
		for (int i = 0; i < getOriginalWidget().getWidgetCount(); i++) {
			if ((child = getOriginalWidget().getWidget(i)) instanceof RequiresResize) {
				((RequiresResize) child).onResize();
			}
		}
	}

}
