package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class ClosePanel extends Composite implements
		HasCloseHandlers<ClosePanel>, ProvidesHtmlElement {

	interface ClosePanelBinder extends UiBinder<Widget, ClosePanel> {
	}

	private static UiBinder<Widget, ClosePanel> binder = GWT
			.create(ClosePanelBinder.class);

	@UiField
	HTML html;

	/*@UiField
	Image close;*/

	public ClosePanel() {
		initWidget(binder.createAndBindUi(this));
	}

	public void setText(String text) {
		html.setText(text);
	}

	@Override
	public HandlerRegistration addCloseHandler(CloseHandler<ClosePanel> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	/*@UiHandler("close")
	void handleClick(ClickEvent event) {
		CloseEvent.fire(this, this);
	}*/

	@Override
	public String getSpecificHtml() {
		return html.getHTML();
	}

}
