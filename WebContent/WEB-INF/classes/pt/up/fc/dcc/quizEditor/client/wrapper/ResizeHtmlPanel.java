package pt.up.fc.dcc.quizEditor.client.wrapper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.HasResizeHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

public class ResizeHtmlPanel extends HTMLPanel implements RequiresResize, ProvidesResize, HasResizeHandlers {

	private final ScheduledCommand resizeCmd = new ScheduledCommand() {
		
		@Override
		public void execute() {
			resizeCmdScheduled = false;
			handleResize();
		}
	};
	private boolean resizeCmdScheduled = false;

	public ResizeHtmlPanel(SafeHtml safeHtml) {
		super(safeHtml);
	}

	public ResizeHtmlPanel(String tag, String html) {
		super(tag, html);
	}

	public ResizeHtmlPanel(String html) {
		super(html);
	}

	public HandlerRegistration addResizeHandler(ResizeHandler handler) {
		return addHandler(handler, ResizeEvent.getType());
	}

	@Override
	protected void onAttach() {
		super.onAttach();

		scheduleResize();
	}

	@Override
	protected void onDetach() {
		super.onDetach();
	}

	private void handleResize() {
		if (!isAttached()) {
			return;
		}

		for (Widget widget : getChildren()) {

			// Provide resize to child.
			if (widget instanceof RequiresResize) {
				((RequiresResize) widget).onResize();
			}
		}

		// Fire resize event.
		ResizeEvent.fire(this, getOffsetWidth(), getOffsetHeight());
	}

	/**
	 * Schedule a resize handler. We schedule the event so the DOM has time to
	 * update the offset sizes, and to avoid duplicate resize events from both a
	 * height and width resize.
	 */
	private void scheduleResize() {
		if (isAttached() && !resizeCmdScheduled) {
			resizeCmdScheduled = true;
			Scheduler.get().scheduleDeferred(resizeCmd);
		}
	}

	@Override
	public void onResize() {
		scheduleResize();
	}
}

