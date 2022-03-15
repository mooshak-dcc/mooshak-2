package pt.up.fc.dcc.mooshak.client.gadgets.relatedresources;

import java.util.Date;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.relatedresources.RelatedResourcesView.Presenter;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.SelectResourceEvent;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.Resources;

public class RelatedResourcesPresenter extends GadgetPresenter<RelatedResourcesView> implements Presenter {

	public RelatedResourcesPresenter(EnkiCommandServiceAsync enkiService, HandlerManager eventBus,
			RelatedResourcesView view, Token token) {
		super(eventBus, null, null, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		updateRelatedResources();
	}

	@Override
	public void updateRelatedResources() {
		enkiService.getRelatedResources(contextInfo.getactivityId(), resourceId, new AsyncCallback<Resources>() {

			@Override
			public void onSuccess(Resources related) {
				for (CourseResource resource : related.getResource()) {
					view.addRelatedResource(resource);
				}
			}

			@Override
			public void onFailure(Throwable e) {
				System.out.println(e.getMessage());
			}
		});
	}

	@Override
	public void onSelectedResourceChanged(String id, String name, ResourceType type, String link, Date learningTime,
			String language) {
		eventBus.fireEvent(new SelectResourceEvent(contextInfo.getactivityId(), id, name, type, link, learningTime, language));
	}

}
