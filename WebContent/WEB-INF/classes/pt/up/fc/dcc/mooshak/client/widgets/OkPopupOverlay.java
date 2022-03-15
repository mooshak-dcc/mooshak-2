package pt.up.fc.dcc.mooshak.client.widgets;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup box that locks the content on the back until user clicks Ok
 * 
 * @author josepaiva
 */
public class OkPopupOverlay
	extends Composite {
	
	private static OkPopupOverlayUiBinder uiBinder = 
			GWT.create(OkPopupOverlayUiBinder.class);
	
	@UiTemplate("OkPopupOverlay.ui.xml")
	interface OkPopupOverlayUiBinder 
			extends UiBinder<Widget, OkPopupOverlay> {}
	
	@UiField
	HTMLPanel base;
	
	@UiField
	Button okButton;
	
	public OkPopupOverlay() {
		initWidget(uiBinder.createAndBindUi(this));
		
		setButtonClickHandlers();	  
		
		//stop bubble
		base.addDomHandler(new DoubleClickHandler() {
			
			@Override
			public void onDoubleClick(DoubleClickEvent event) {
				event.stopPropagation();
			}
		}, DoubleClickEvent.getType());
		base.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
			}
		}, MouseDownEvent.getType());
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
				getElement().removeFromParent();
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
