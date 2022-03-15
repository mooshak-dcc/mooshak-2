package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.HashSet;
import java.util.Set;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.DialogContent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog with Ok and Cancel buttons
 * 
 * @author josepaiva
 */
public abstract class OkCancelDialog 
	extends Composite {
	
	private static OkCancelDialogUiBinder uiBinder = 
			GWT.create(OkCancelDialogUiBinder.class);
	
	@UiTemplate("OkCancelDialog.ui.xml")
	interface OkCancelDialogUiBinder 
			extends UiBinder<Widget, OkCancelDialog> {}
	
	@UiField
	DialogBox dialogBox;
	
	@UiField
	HTMLPanel basePanel;
	
	@UiField
	Button okButton;
	
	@UiField
	Button cancelButton;
	
	@UiField
	HTMLPanel dialogContentPanel;
	
	public OkCancelDialog(DialogContent content) {
		initWidget(uiBinder.createAndBindUi(this));
		
		basePanel.setWidth(content.getWidth());
		basePanel.setHeight(content.getHeight());
		
		dialogContentPanel.add((Widget) content);
		
		setButtonClickHandlers();	  
		dialogBox.getCaption().setText("Type specific command");
		dialogBox.getElement().getStyle().setZIndex(99999);
	}
	
	public OkCancelDialog(DialogContent content,
			String text) {
		initWidget(uiBinder.createAndBindUi(this));
		
		basePanel.setWidth(content.getWidth());
		basePanel.setHeight(content.getHeight());
		
		dialogContentPanel.add((Widget) content);
		
		setButtonClickHandlers();	  
		dialogBox.getCaption().setText(text);

		dialogBox.getElement().getStyle().setZIndex(99999);
		enable();
	}
	
	public OkCancelDialog(String text) {
		initWidget(uiBinder.createAndBindUi(this));
		
		setButtonClickHandlers();	
		
		Label message = new Label(text);
		dialogContentPanel.add(message);
		
		enable();
		dialogBox.getCaption().setText("Confirm");
		dialogBox.getElement().getStyle().setZIndex(99999);
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
		okButton.ensureDebugId("popupOk");
		okButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				fireSubmit();
				dialogBox.hide();
			}
		});

		cancelButton.ensureDebugId("popupCancel");
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
