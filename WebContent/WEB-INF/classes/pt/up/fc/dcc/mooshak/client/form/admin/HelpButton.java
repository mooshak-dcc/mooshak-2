package pt.up.fc.dcc.mooshak.client.form.admin;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;

public class HelpButton extends PushButton {
	DecoratedPopupPanel popup = new DecoratedPopupPanel(true);
	
	private FormStyle style = FormResources.INSTANCE.style();
	
	HelpButton(String text) {
		//super(new Image("images/help.png"));
		super("?");
		
		Label label= new Label(text);
		
		style.ensureInjected();
		getElement().setClassName(style.helpButton());
		label.getElement().setClassName(style.helpPanel());
		
		popup.add(label);
	}

	@Override
	protected void onLoad() {

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				PushButton source = (PushButton) event.getSource();

				if (popup.isShowing()) 
					popup.hide();
				else {
					int left = source.getAbsoluteLeft();
					int top = source.getAbsoluteTop()
							+ source.getOffsetHeight();
					popup.setPopupPosition(left, top);

					popup.show();

				} 
			}
		});
	}

}


