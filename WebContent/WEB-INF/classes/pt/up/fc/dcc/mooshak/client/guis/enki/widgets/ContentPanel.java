package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import gwtquery.plugins.draggable.client.DraggableOptions;
import gwtquery.plugins.draggable.client.DraggableOptions.CursorAt;
import gwtquery.plugins.draggable.client.DraggableOptions.HelperType;
import gwtquery.plugins.draggable.client.DraggableOptions.RevertOption;
import gwtquery.plugins.draggable.client.StopDragException;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent;
import gwtquery.plugins.draggable.client.events.BeforeDragStartEvent.BeforeDragStartEventHandler;
import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.events.DragStopEvent;
import gwtquery.plugins.draggable.client.events.DragStopEvent.DragStopEventHandler;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class ContentPanel extends DraggableWidget<ContentPanel> implements
		CloseHandler<ClosePanel>, ProvidesResize, RequiresResize {

	interface ContentPanelBinder extends UiBinder<Widget, ContentPanel> {
	}

	private class DraggablePositionHandler implements DragEventHandler,
			BeforeDragStartEventHandler, DragStopEventHandler {

		public void onBeforeDragStart(BeforeDragStartEvent event) {
			if(tabPanel.isDraggingTab())
				return;
			
			height = event.getDraggable().getOffsetHeight();
			event.getDraggable().getStyle().setProperty("width", "100px");
			event.getDraggable().getStyle().setProperty("height", "50px");/**/
		}

		public void onDragStop(DragStopEvent event) {
			
			event.getDraggable().getStyle().setProperty("width", "100%");
			if(event.getDraggable().getOffsetHeight() == 50)
				event.getDraggable().getStyle().setProperty("height", height +"px");
		}

		@Override
		public void onDrag(DragEvent event) {
			if(tabPanel.isDraggingTab())
				throw new StopDragException();
			
			event.getHelper().getStyle().setProperty("width", "100px");
			event.getHelper().getStyle().setProperty("height", "50px");
		}
	}

	private static UiBinder<Widget, ContentPanel> binder = GWT
			.create(ContentPanelBinder.class);

	private DraggablePositionHandler HANDLER = new DraggablePositionHandler();

	@UiField(provided = true)
	ScrollableTabLayoutPanel tabPanel;
	
	private String id;
	
	private int height;

	public ContentPanel(String id) {
		tabPanel = new ScrollableTabLayoutPanel(id);
		initWidget(binder.createAndBindUi(this));
		getElement().setId(id);
		this.id = id;
		tabPanel.setHeight("100%");
		tabPanel.setWidth("100%");
		setup();
	}

	public void addTab(String text, Widget content, String id) {
		
		content.addDomHandler(new MouseDownHandler() {

			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
			}
		}, MouseDownEvent.getType());
		
		ClosePanel closePanel = new ClosePanel();
		closePanel.setText(text);
		closePanel.addCloseHandler(this);
		tabPanel.add(content, closePanel, id);
	}

	@Override
	public void onClose(CloseEvent<ClosePanel> event) {
		if (tabPanel.getWidgetCount() > 1) {
			event.getTarget().removeFromParent();
		}
	}

	private void setup() {
		DraggableOptions options = new DraggableOptions();
		options.setOpacity(new Float(0.8));
		options.setZIndex(99999);
		/*options.setSnap(true);
		options.setSnapMode(SnapMode.INNER);
		options.setSnapTolerance(50);*/
		addBeforeDragHandler(HANDLER);
		addDragStopHandler(HANDLER);
		addDragHandler(HANDLER);
		options.setAppendTo("body");
		options.setHandle("#"+this.id);
		options.setContainment("#splitPanelPetcha");
		options.setRevert(RevertOption.ON_INVALID_DROP);
		options.setRevertDuration(0);
		options.setScope("TabPanel");
		options.setCursor(Cursor.MOVE);
		options.setCursorAt(new CursorAt(0, 0, null, null));
		options.setHelper(HelperType.CLONE);
		setDraggableOptions(options);
	}

	@Override
	public void onResize() {
		tabPanel.onResize();
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void refreshScrollHandlers() {
		tabPanel.refreshHandlers();
	}
	
	/**
	 * Remove tabs related to a resource id
	 * 
	 * @param resourceId
	 */
	public void removeTabs(String resourceId) {
		tabPanel.removeTabs(resourceId);
	}
	
	/**
	 * Select tab in position
	 * 
	 * @param position
	 */
	public void selectTab(int position) {
		tabPanel.selectTab(position);
	}
	
	/**
	 * Select tab with widget
	 * @param id
	 * @param widget
	 * @return boolean true if the tab was selected
	 */
	public boolean selectTab(String id, Widget widget) {
		return tabPanel.selectTab(id, widget);
	}
}
