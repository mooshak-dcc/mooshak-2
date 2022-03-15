package pt.up.fc.dcc.mooshak.client.gadgets.resourcerating;

import org.gwtbootstrap3.client.ui.Button;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.guis.enki.i18n.EnkiConstants;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.client.widgets.rating.FullRatingWidget;

public class ResourceRatingViewImpl extends ResizeComposite 
	implements ResourceRatingView {
	
	public static final int UPDATE_TIME = 30000;
	private static final int MESSAGE_VIEWING_TIME = 5*1000;

	private static ResourceRatingViewUiBinder uiBinder = GWT.create(ResourceRatingViewUiBinder.class);

	@UiTemplate("ResourceRatingView.ui.xml")
	interface ResourceRatingViewUiBinder extends UiBinder<Widget, ResourceRatingViewImpl> {
	}
	
	EnkiConstants constants = GWT.create(EnkiConstants.class);
	
	private Presenter presenter;
	
	@UiField
	ResizableHtmlPanel container;
	
	@UiField
	TextArea comment;
	
	@UiField
	FullRatingWidget rating;
	
	@UiField
	Button submit;

	@UiField
	Label message;
	
	public ResourceRatingViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		rating.setHeight("55px");
		comment.getElement().setPropertyString("placeholder",constants.commentPlaceholder());
		container.addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				comment.setHeight(Math.max(0, event.getHeight() - rating.getOffsetHeight()) 
						+ "px");
			}
		});
		submit.setText(constants.rate());
		
		setDraggableParentProperties();
	}

	
	/**
	 * Resolving issues when clicking inside draggable parent
	 */
	public void setDraggableParentProperties() {
		comment.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				event.stopPropagation();
			}
		}, MouseMoveEvent.getType());
		
		comment.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
			}
		}, MouseDownEvent.getType());
		
		
		rating.addDomHandler(new MouseMoveHandler() {
			
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				event.stopPropagation();
			}
		}, MouseMoveEvent.getType());
		
		rating.addDomHandler(new MouseDownHandler() {
			
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.stopPropagation();
			}
		}, MouseDownEvent.getType());
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
	@UiHandler({"submit"})
	void submit(ClickEvent event) {
		presenter.onSubmit(rating.getValue(), comment.getText());
	}

	@Override
	public void setRating(int rating, String comment) {
		this.rating.setValue(rating, true);
		this.comment.setValue(comment);
	}
	
	@Override
	public void setMessage(String text) {
		message.setText(text);
		
		resetMessage();
		
	}

	private Timer cleanupTimer = null;

	/**
	 * Reset message after some time
	 */
	private void resetMessage() {
		
		if(cleanupTimer != null)
			cleanupTimer.cancel();
		
		new Timer() {

			@Override
			public void run() {
				message.setText("");
				cleanupTimer = null;
			}
			
		}.schedule(MESSAGE_VIEWING_TIME);
	}

	
}
