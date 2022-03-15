package pt.up.fc.dcc.mooshak.client.guis.admin.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.ObjectEditorView.Presenter;

/**
 * Dialog box for entering new name of the object
 * 
 * @author josepaiva
 */
public class RenameDialog  extends Composite {

	private static RenameDialogUiBinder uiBinder = 
			GWT.create(RenameDialogUiBinder.class);
		
	@UiTemplate("RenameDialog.ui.xml")
	interface RenameDialogUiBinder 
			extends UiBinder<Widget, RenameDialog> {}
	
	@UiField
	DialogBox dialogBox;
	
	@UiField
	Label typeLabel;
	
	@UiField
	TextBox nameTextBox;
	
	@UiField
	Button okButton;
	
	@UiField
	Button cancelButton;
	
	private Runnable action = new Runnable() { public void run() {} };
	
	RenameDialog() {

		initWidget(uiBinder.createAndBindUi(this));
				
		nameTextBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				action.run();
			}
		});
		
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
	
	public void start(
			final String type, 
			final String id,
			final Presenter presenter) {
		
		if(typeLabel != null && type != null)
			typeLabel.setText(type.toLowerCase());

		action = new Runnable() {
			@Override
			public void run() {
				presenter.onRename(id, nameTextBox.getText());
				dialogBox.hide();
			}
		};
		
		dialogBox.center();
	}
}
