package pt.up.fc.dcc.mooshak.client.gadgets.resourcestree;

import java.util.Date;
import java.util.logging.Level;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.ResourcesTreeView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;

public class ResourcesTreePresenter extends GadgetPresenter<ResourcesTreeView> implements Presenter {
	
	public ResourcesTreePresenter(
			  HandlerManager eventBus,
			  EnkiCommandServiceAsync enkiService, ResourcesTreeView view, Token token) {
		
		super(eventBus, null, null, enkiService, null, null, null, view, null);
	    
	    this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		
		view.setCourseId(contextInfo.getactivityId());
		
		enkiService.getResources(new AsyncCallback<CourseList>() {

			@Override
			public void onSuccess(CourseList result) {
				if (result != null)
					view.setResources(result);
			}

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.log(Level.SEVERE, caught.getMessage());
			}
		});
	}

	public void onSelectedResourceChanged(String courseId, String id, String name,
			ResourceType type, String link, Date learningTime, String language) {
		eventBus.fireEvent(new SelectResourceEvent(courseId, id, name, type,
				link, learningTime, language));
	}

}
