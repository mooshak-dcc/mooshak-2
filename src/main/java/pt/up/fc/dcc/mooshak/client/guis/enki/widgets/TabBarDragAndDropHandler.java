package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import gwtquery.plugins.draggable.client.events.DragEvent;
import gwtquery.plugins.draggable.client.events.DragEvent.DragEventHandler;
import gwtquery.plugins.draggable.client.gwt.DraggableWidget;
import gwtquery.plugins.droppable.client.events.DropEvent;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent;
import gwtquery.plugins.droppable.client.events.DropEvent.DropEventHandler;
import gwtquery.plugins.droppable.client.events.OutDroppableEvent.OutDroppableEventHandler;
import gwtquery.plugins.droppable.client.events.OverDroppableEvent.OverDroppableEventHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class TabBarDragAndDropHandler implements DropEventHandler,
		OverDroppableEventHandler, OutDroppableEventHandler, DragEventHandler {

	public interface Resources extends ClientBundle {

		public interface Css extends CssResource {
			String placeHolder();
		}

		Resources INSTANCE = GWT.create(Resources.class);

		@Source("ContentPanel.css")
		Css css();
	}

	private HandlerRegistration dragHandlerRegistration;
	private ScrollableTabLayoutPanel tabLayoutPanel;
	private FlowPanel panel;
	private SimplePanel placeHolder;
	private int placeHolderIndex;

	public TabBarDragAndDropHandler(FlowPanel panel, ScrollableTabLayoutPanel tabLayoutPanel2) {
		this.panel = panel;
		placeHolderIndex = -1;
		this.tabLayoutPanel = tabLayoutPanel2;
	}

	/**
	 * When draggable is dragging inside the panel, check if the place holder
	 * has to move
	 */
	public void onDrag(DragEvent event) {
		maybeMovePlaceHolder(event.getHelper());
		//Logger.getLogger("").log(Level.SEVERE, "Dragging tab");
	}

	/**
	 * On drop, insert the draggable at the place holder index, remove handler
	 * on the {@link DragEvent} of this draggable and remove the visual place
	 * holder
	 */
	public void onDrop(DropEvent event) {
		final DraggableWidget<?> draggable = event.getDraggableWidget();
		
		int index = Math.max(0, placeHolderIndex);
		if (draggable.getOriginalWidget() instanceof CustomTab) {
			CustomTab tab = (CustomTab) draggable.getOriginalWidget();
			Widget w = tab.getLayoutPanel().getWidget(tab.getLayoutPanel()
					.getTabIndex((DraggableCustomTab) draggable));
			tab.getLayoutPanel().remove(draggable);
			((DraggableCustomTab) draggable).removeClickHandler();
			tabLayoutPanel.insert(w, (DraggableCustomTab) draggable, 
					((DraggableCustomTab) draggable).getOriginalWidget().getResourceId(), index);
			tab.setLayoutPanel(tabLayoutPanel);
			
			if(tabLayoutPanel instanceof ScrollableTabLayoutPanel)
				((ScrollableTabLayoutPanel) tabLayoutPanel).refreshHandlers();
		}

		reset();
	}

	/**
	 * When a draggable is out the panel, remove handler on the
	 * {@link DragEvent} of this draggable and remove the visual place holder
	 */
	public void onOutDroppable(OutDroppableEvent event) {

		reset();
	}

	/**
	 * When a draggable is being over the panel, listen on the {@link DragEvent}
	 * of the draggable and put a visaul place holder.
	 */
	public void onOverDroppable(OverDroppableEvent event) {
		DraggableWidget<?> draggable = event.getDraggableWidget();
		// create a place holder
		createPlaceHolder(draggable, panel.getWidgetIndex(draggable));
		// listen drag event when draggable is over the droppable
		dragHandlerRegistration = draggable.addDragHandler(this);
	}

	/**
	 * Create a visual place holder
	 * 
	 * @param draggable
	 * @param initialPosition
	 */
	private void createPlaceHolder(Widget draggable, int initialPosition) {
		placeHolder = new SimplePanel();
		placeHolder.addStyleName(Resources.INSTANCE.css().placeHolder());
		placeHolder.setHeight("30px");
		placeHolder.setWidth("30px");

		if (initialPosition != -1) {
			/*panel.insert(placeHolder, initialPosition);*/
			placeHolderIndex = initialPosition;
		}
	}

	/**
	 * Return the index before which we should insert the draggable if this one
	 * is dropped now
	 * 
	 * @param draggableHelper
	 * @return
	 */
	private int getBeforeInsertIndex(Element draggableHelper) {
		if (panel.getWidgetCount() == 0) {
			// no widget, the draggable should just be added to the panel
			return -1;
		}

		int draggableAbsoluteLeft = draggableHelper.getAbsoluteLeft();

		for (int i = 0; i < panel.getWidgetCount(); i++) {
			Widget w = panel.getWidget(i);
			int widgetAbsoluteLeft = w.getElement().getAbsoluteLeft()
					+ w.getElement().getOffsetWidth() / 2;
			if (widgetAbsoluteLeft > draggableAbsoluteLeft) {
				return i;
			}
		}

		// the draggable should just be added at the end of the panel
		return -1;
	}

	/**
	 * Check if we have to move the place holder
	 * 
	 * @param draggableHelper
	 */
	private void maybeMovePlaceHolder(Element draggableHelper) {
		int beforeInsertIndex = getBeforeInsertIndex(draggableHelper);

		if (placeHolderIndex > 0 && beforeInsertIndex == placeHolderIndex) {
			// placeHolder must not move
			return;
		}

		if (beforeInsertIndex >= 0) {
			// move the place holder and keep its position
			/*panel.insert(placeHolder, beforeInsertIndex);*/
			placeHolderIndex = beforeInsertIndex;
		} else {
			// insert the place holder at the end
			/*panel.add(placeHolder);*/
			placeHolderIndex = panel.getWidgetCount();
		}
	}

	private void reset() {

		// remove the place holder
		/*if (placeHolder != null)
			placeHolder.removeFromParent();*/
		// don't listen drag event on the draggable
		if (dragHandlerRegistration != null)
			dragHandlerRegistration.removeHandler();
		
		/*placeHolder = null;*/
		dragHandlerRegistration = null;
		placeHolderIndex = -1;
	}

}
