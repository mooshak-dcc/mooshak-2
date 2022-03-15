package pt.up.fc.dcc.mooshak.client.guis.admin.view;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.ObjectEditorView.Presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * Dialog box for confirming content destruction 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class DestroyDialog  extends Composite {

	private static DestroyDialogUiBinder uiBinder = 
			GWT.create(DestroyDialogUiBinder.class);
		
	@UiTemplate("DestroyDialog.ui.xml")
	interface DestroyDialogUiBinder 
			extends UiBinder<Widget, DestroyDialog> {}
	
	@UiField
	DialogBox dialogBox;
	
	@UiField
	Label idLabel;
	
	@UiField
	Button okButton;
	
	@UiField
	Button cancelButton;
	
	private Runnable action = new Runnable() { public void run() {} };
	
	DestroyDialog() {

		initWidget(uiBinder.createAndBindUi(this));
				
		okButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				action.run();
			}
		});
		
		cancelButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});
	}
	
	public void start(final String id,final Presenter presenter) {
		
		idLabel.setText(id);

		action = new Runnable() {
			@Override
			public void run() {
				presenter.onDestroy(id);
				dialogBox.hide();
			}
		};
		dialogBox.center();
	}
}
