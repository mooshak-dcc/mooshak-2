package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * FlowPanel with resize properties
 * 
 * @author josepaiva
 */
public class ResizableFlowPanel extends FlowPanel implements ProvidesResize,
		RequiresResize {

	public ResizableFlowPanel() {
		super();
	}

	@Override
	public void onResize() {
		for (Widget child : getChildren()) {
			if (child instanceof RequiresResize) {
				((RequiresResize) child).onResize();
			}
		}
	}

	@Override
	public void add(Widget w) {
		super.add(w);

		double height = ((double) 1 / (double) getChildren().size()) * 100;
		for (Widget child : getChildren()) {
			child.getElement().getStyle().setHeight(height, Unit.PCT);
		}
	}
	
	@Override
	public boolean remove(Widget w) {
		boolean b = super.remove(w);
		
		if (b && getWidgetCount() > 0) {
			double height = ((double) 1 / (double) getChildren().size()) * 100;
			for (Widget child : getChildren()) {
				child.getElement().getStyle().setHeight(height, Unit.PCT);
			}
		}
		
		return b;
	}
	
	@Override
	public void insert(Widget w, int beforeIndex) {
		super.insert(w, beforeIndex);

		double height = ((double) 1 / (double) getChildren().size()) * 100;
		for (Widget child : getChildren()) {
			child.getElement().getStyle().setHeight(height, Unit.PCT);
		}
	}

}
