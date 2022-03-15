package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

public class DraggableCustomTab extends DraggableWidget<CustomTab> {

	interface DraggableCustomTabBinder extends
			UiBinder<Widget, DraggableCustomTab> {
	}

	private class DraggablePositionHandler implements
			BeforeDragStartEventHandler, DragStopEventHandler {

		public void onBeforeDragStart(BeforeDragStartEvent event) {
			tab.setDraggingTab(true);
		}

		public void onDragStop(DragStopEvent event) {
			tab.setDraggingTab(false);
		}
	}

	private static UiBinder<Widget, DraggableCustomTab> binder = GWT
			.create(DraggableCustomTabBinder.class);

	private DraggablePositionHandler HANDLER = new DraggablePositionHandler();
	
	private HandlerRegistration handlers;
	
	@UiField(provided=true)
	CustomTab tab;
	
	public DraggableCustomTab(Widget tab, String id, ScrollableTabLayoutPanel scrollableTabLayoutPanel) {
		this.tab = new CustomTab(tab, id, scrollableTabLayoutPanel);
		initWidget(binder.createAndBindUi(this));
		setup();
	}

	private void setup() {
		DraggableOptions options = new DraggableOptions();
		options.setOpacity(new Float(0.8));
		options.setZIndex(99999);
		addBeforeDragHandler(HANDLER);
		addDragStopHandler(HANDLER);
		options.setAppendTo("body");
		options.setContainment("#splitPanelPetcha");
		options.setScope("TabBar");
		options.setRevert(RevertOption.ON_INVALID_DROP);
		options.setRevertDuration(1000);
		options.setCursor(Cursor.MOVE);
		options.setHelper(HelperType.CLONE);
		options.setCursorAt(new CursorAt(0, 0, null, null));
		setDraggableOptions(options);
	}
	
	public void setSelected(boolean b) {
		tab.setSelected(b);
	}
	
	public void setWidget(Widget w) {
		tab.setWidget(w);
	}

	public HandlerRegistration addClickHandler(ClickHandler clickHandler) {
		handlers = tab.addClickHandler(clickHandler);
		return handlers;
	}

	public void removeClickHandler() {
		if (handlers == null)
			return;
		handlers.removeHandler();
	}

	public Widget getTabWidget() {
		return this;
	}

	public CustomTab getTab() {
		return tab;
	}
}
