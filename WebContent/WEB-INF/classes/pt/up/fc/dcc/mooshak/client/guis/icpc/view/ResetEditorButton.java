package pt.up.fc.dcc.mooshak.client.guis.icpc.view;

import pt.up.fc.dcc.mooshak.client.widgets.CustomImageButton;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ResetEditorButton extends Composite {

	private static ResetEditorButtonUiBinder uiBinder = 
			GWT.create(ResetEditorButtonUiBinder.class);

	@UiTemplate("ResetEditorButton.ui.xml")
	interface ResetEditorButtonUiBinder extends UiBinder<Widget, ResetEditorButton> {
	}
	
	@UiField
	CustomImageButton reset;
	
	public ResetEditorButton() {
		initWidget(uiBinder.createAndBindUi(this));
		reset.getElement().getStyle().setMarginLeft(-10, Unit.PX);
	}
	
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return reset.addClickHandler(handler);
	}
}
