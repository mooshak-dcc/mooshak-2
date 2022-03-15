package pt.up.fc.dcc.xonomygwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

import pt.up.fc.dcc.xonomygwt.client.resources.Resources;
import pt.up.fc.dcc.xonomygwt.client.wrapper.XonomyGWTWrapper;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class XonomyGWTWrapperTest implements EntryPoint {
	
	private static final String DOC_SPEC = Resources.INSTANCE.resourcesDocspecJson().getText();
	private static final String ROOT_ELEMENT = "course";

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		final XonomyGWTWrapper xonomyGWTWrapper = new XonomyGWTWrapper();

		RootPanel.get("wrapper").add(xonomyGWTWrapper);

		xonomyGWTWrapper.render("<" + ROOT_ELEMENT + "></" + ROOT_ELEMENT + ">", DOC_SPEC);
		
		xonomyGWTWrapper.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
			}
		});
		
		Button downloadBtn = new Button("Download File");
		downloadBtn.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				FileDownloader.downloadFile("content.xml", XonomyGWTWrapper.harvest());
			}
		});
		downloadBtn.getElement().getStyle().setPosition(Position.ABSOLUTE);
		downloadBtn.getElement().getStyle().setTop(20, Unit.PX);
		downloadBtn.getElement().getStyle().setRight(20, Unit.PX);
		
		RootPanel.get("wrapper").add(downloadBtn);
	}
}
