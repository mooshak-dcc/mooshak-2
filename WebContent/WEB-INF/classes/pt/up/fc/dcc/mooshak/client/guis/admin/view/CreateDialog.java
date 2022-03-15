package pt.up.fc.dcc.mooshak.client.guis.admin.view;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.ObjectEditorView.Presenter;

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

/**
 * Dialog box for entering name of content to be created
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class CreateDialog  extends Composite {

	private static CreateDialogUiBinder uiBinder = 
			GWT.create(CreateDialogUiBinder.class);
		
	@UiTemplate("CreateDialog.ui.xml")
	interface CreateDialogUiBinder 
			extends UiBinder<Widget, CreateDialog> {}
	
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
	
	CreateDialog() {

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
				presenter.onCreate(id, nameTextBox.getValue());
				dialogBox.hide();
			}
		};
		
		dialogBox.center();
	}
}
