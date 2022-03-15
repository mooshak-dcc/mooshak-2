package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CommonResources;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CustomTab extends SimplePanel {
	private static final String TAB_STYLE = "gwt-TabLayoutPanelTab";
	private static final String TAB_INNER_STYLE = "gwt-TabLayoutPanelTabInner";
	
	private ScrollableTabLayoutPanel layoutPanel;
	private Element inner;
	private boolean replacingWidget;
	private String resourceId;

	public CustomTab(Widget child, String id, ScrollableTabLayoutPanel layoutPanel) {
		super(Document.get().createDivElement());
		
		this.resourceId = id;
		
		this.layoutPanel = layoutPanel;
		
		getElement().appendChild(inner = Document.get().createDivElement());

		setWidget(child);
		setStyleName(TAB_STYLE);
		inner.setClassName(TAB_INNER_STYLE);

		getElement().addClassName(CommonResources.getInlineBlockStyle());
	}

	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public boolean remove(Widget w) {
		int index = layoutPanel.getWidgetIndex(this);
		if (replacingWidget || index < 0) {
			return super.remove(w);
		} else {
			return layoutPanel.remove(index);
		}
	}

	public void setSelected(boolean selected) {
		if (selected) {
			addStyleDependentName("selected");
		} else {
			removeStyleDependentName("selected");
		}
	}

	@Override
	public void setWidget(Widget w) {
		replacingWidget = true;
		super.setWidget(w);
		replacingWidget = false;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected com.google.gwt.user.client.Element getContainerElement() {
		return inner.cast();
	}

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}
	
	public void setDraggingTab(boolean b) {
		layoutPanel.setDraggingTab(b);
	}

	/**
	 * @return the layoutPanel
	 */
	public ScrollableTabLayoutPanel getLayoutPanel() {
		return layoutPanel;
	}

	/**
	 * @param tabLayoutPanel the layoutPanel to set
	 */
	public void setLayoutPanel(ScrollableTabLayoutPanel tabLayoutPanel) {
		this.layoutPanel = tabLayoutPanel;
	}

}
