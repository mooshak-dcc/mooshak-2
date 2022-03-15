package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

import gwtquery.plugins.droppable.client.DroppableOptions.DroppableTolerance;
import gwtquery.plugins.droppable.client.gwt.DroppableWidget;

public class DroppablePanel extends DroppableWidget<ResizableFlowPanel>
	implements HasWidgets, ProvidesResize, RequiresResize {

	public interface Resources extends ClientBundle {

		public interface Css extends CssResource {
			String sortablePanel();
		}

		Resources INSTANCE = GWT.create(Resources.class);

		@Source("ContentPanel.css")
		Css css();
	}

	interface DroppablePanelBinder extends UiBinder<Widget, DroppablePanel> {
	}

	private static UiBinder<Widget, DroppablePanel> binder = GWT
			.create(DroppablePanelBinder.class);

	@UiField
	ResizableFlowPanel panel;

	public DroppablePanel() {
		initWidget(binder.createAndBindUi(this));
		panel.setStyleName(Resources.INSTANCE.css().sortablePanel());
		setupDrop();
	}
	
	@Override
	public void clear() {
		panel.clear();
	}
	
	@Override
	public boolean remove(Widget w) {
		return panel.remove(w);
	}
	
	@Override
	public Iterator<Widget> iterator() {
		return panel.iterator();
	}
	
	@Override
	public void add(Widget w) {
		panel.add(w);
	}

	/**
	 * Register drop handler
	 */
	private void setupDrop() {
		ContentPanelDragAndDropHandler sortableHandler = new ContentPanelDragAndDropHandler(
				this.getOriginalWidget());
		addDropHandler(sortableHandler);
		addOutDroppableHandler(sortableHandler);
		addOverDroppableHandler(sortableHandler);
		setAccept(".gwt-TabLayoutPanel");
		setTolerance(DroppableTolerance.POINTER);
		setDragAndDropScope("TabPanel");
		setGreedy(true);
	}

	@Override
	public void onResize() {
		panel.onResize();
	}

}
