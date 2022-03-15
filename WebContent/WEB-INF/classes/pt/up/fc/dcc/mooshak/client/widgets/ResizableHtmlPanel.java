package pt.up.fc.dcc.mooshak.client.widgets;

import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class ResizableHtmlPanel extends HTMLPanel implements RequiresResize, 
	HasResizeHandlers {
	
	public ResizableHtmlPanel(SafeHtml safeHtml) {
		super(safeHtml);
	}
	
	public ResizableHtmlPanel(String html) {
		super(html);
	}
	
	public ResizableHtmlPanel(String tag, String html) {
		super(tag, html);
	}

	@Override
	public void onResize() {
		for (Widget child : getChildren()) {
			if (child instanceof RequiresResize) {
				((RequiresResize) child).onResize();
			}
		}
		ResizeEvent.fire(this, getOffsetWidth(), getOffsetHeight());
	}

	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}
	
	

}
