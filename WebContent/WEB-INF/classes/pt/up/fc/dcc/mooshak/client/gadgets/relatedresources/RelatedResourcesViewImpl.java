package pt.up.fc.dcc.mooshak.client.gadgets.relatedresources;

import org.gwtbootstrap3.client.ui.Anchor;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.widgets.ResizableHtmlPanel;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;

public class RelatedResourcesViewImpl extends ResizeComposite implements
		RelatedResourcesView {

	private static RelatedResourcesUiBinder uiBinder = GWT.create(RelatedResourcesUiBinder.class);

	@UiTemplate("RelatedResourcesView.ui.xml")
	interface RelatedResourcesUiBinder extends UiBinder<Widget, RelatedResourcesViewImpl> {
	}
	
	private Presenter presenter;
	
	@UiField
	ResizableHtmlPanel container;
	
	public RelatedResourcesViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public Presenter getPresenter() {
		return presenter;
	}

	@Override
	public void addRelatedResource(final CourseResource resource) {
		Anchor anchor = new Anchor();
		anchor.setHTML(resource.getTitle());
		
		anchor.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				
				if (resource.getState().equals(ResourceState.UNAVAILABLE)) {
					Window.alert("Resource Unavailable! Please finish previous"
							+ " resources first");
					return;
				}
				
				presenter.onSelectedResourceChanged(resource
						.getId(), resource.getTitle(), resource.getType(), 
						resource.getHref(), resource.getLearningTime(),
						resource.getLanguage());
			}
		});
		
		container.add(anchor);
	}
	
	
	
}
