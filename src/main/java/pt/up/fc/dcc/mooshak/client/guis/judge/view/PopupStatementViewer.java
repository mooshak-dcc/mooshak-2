package pt.up.fc.dcc.mooshak.client.guis.judge.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.guis.icpc.view.StatementView;
import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.client.widgets.WindowBox;

public class PopupStatementViewer extends WindowBox {

	private static PopupStatementViewerUiBinder uiBinder = GWT
			.create(PopupStatementViewerUiBinder.class);

	@UiTemplate("PopupStatementViewer.ui.xml")
	interface PopupStatementViewerUiBinder extends
			UiBinder<Widget, PopupStatementViewer> {
	}

	
	@UiField
	HTMLPanel statement;

	@UiField
	Button ok;

	@UiField
	ResizableHtmlPanel popupContainer;
	
	public PopupStatementViewer() {
		setWidget(uiBinder.createAndBindUi(this));
        setAutoHideEnabled(false);
        setGlassEnabled(false);
        setModal(true);
        setMinimizeIconVisible(false);
        setCloseIconVisible(true);
        setDraggable(true);
        setResizable(true);
        
        getElement().getStyle().setZIndex(1000);

		setMinWidth(600);
		setMinHeight(490);
		setWidth("600px");
		setHeight("600px");
        
		addResizeHandler(new ResizeHandler() {
			
			@Override
			public void onResize(ResizeEvent event) {
				statement.setWidth(Math.max(popupContainer.getOffsetWidth()-20,0)+"px");
				statement.setHeight(Math.max(popupContainer.getOffsetHeight()-60,0)+"px");
			}
		});

		ok.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				hide();
			}
		});
		
		center();
		hide();
	}

	public void setStatement(StatementView statementView) {
		statement.clear();
		statement.add(statementView.getContentAsWidget());
		
		onResize();
	}
	
}
