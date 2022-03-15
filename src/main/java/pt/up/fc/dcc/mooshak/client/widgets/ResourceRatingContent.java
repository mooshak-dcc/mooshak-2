package pt.up.fc.dcc.mooshak.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog.DialogContent;
import pt.up.fc.dcc.mooshak.client.widgets.rating.FullRatingWidget;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

public class ResourceRatingContent extends Composite implements DialogContent {

	private static ResourceRatingContentUiBinder uiBinder = GWT.create(ResourceRatingContentUiBinder.class);

	@UiTemplate("ResourceRatingContent.ui.xml")
	interface ResourceRatingContentUiBinder extends UiBinder<Widget, ResourceRatingContent> {
	}
	
	@UiField
	TextArea comment;
	
	@UiField
	FullRatingWidget rating;
	
	public ResourceRatingContent() {

		initWidget(uiBinder.createAndBindUi(this));
		
		rating.setHeight("55px");
		comment.getElement().setPropertyString("placeholder", 
				"Tell others what you think about this resource. "
				+ "Would you recommend it and why?");
		
		getElement().getStyle().setZIndex(99999);
	}

	@Override
	public MethodContext getContext() {
		return null;
	}

	@Override
	public void setContext(MethodContext context) {
	}

	@Override
	public String getWidth() {
		return "500px";
	}

	@Override
	public String getHeight() {
		return "400px";
	}
	
	public int getRating() {
		return rating.getValue();
	}
	
	public String getComment() {
		return comment.getText();
	}

}
