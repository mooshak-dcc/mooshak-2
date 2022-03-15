package pt.up.fc.dcc.mooshak.client.guis.enki.widgets;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class EvaluationAcceptedDialog 
	extends Composite {

	private static EvaluationAcceptedDialogUiBinder uiBinder = 
			GWT.create(EvaluationAcceptedDialogUiBinder.class);
	
	@UiTemplate("EvaluationAcceptedDialog.ui.xml")
	interface EvaluationAcceptedDialogUiBinder 
			extends UiBinder<Widget, EvaluationAcceptedDialog> {}
	
	@UiField
	DialogBox dialogBox;
	
	@UiField
	HTMLPanel basePanel;
	
	@UiField
	Button proceedButton;
	
	@UiField
	Button cancelButton;
	
	@UiField
	HTMLPanel dialogContentPanel;
	
	public EvaluationAcceptedDialog(String caption, String text) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setButtonClickHandlers();	
		
		Label message = new HTML(new SafeHtmlBuilder().appendEscapedLines(text)
				.toSafeHtml());
		dialogContentPanel.add(message);
		dialogBox.getCaption().setText(caption);
		
		enable();
		dialogBox.getElement().getStyle().setZIndex(999999);
		dialogBox.getElement().getStyle().setPosition(Position.FIXED);
	}
	
	/**
	 * Enable this dialog
	 * When created it is not enabled
	 */
	public void enable() {
		dialogBox.show();
		dialogBox.center();
	}

	/**
	 * Sets the click handlers
	 */
	private void setButtonClickHandlers() {
		proceedButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fireSubmit();
				dialogBox.hide();
			}
		});

		cancelButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
	}

	private Set<ClickHandler> handlers = 
			new HashSet<ClickHandler>();
	
	/**
	 * Add an handler of this dialog submissions.
	 * It will be notified with {@code onClick(ClickEvent)}
	 * @param handler
	 */
	public void addDialogHandler(final ClickHandler handler) {
		handlers.add(handler);	
	}

	protected void fireSubmit() {
		for(ClickHandler handler: handlers)
			handler.onClick(null);
	}
	
}
