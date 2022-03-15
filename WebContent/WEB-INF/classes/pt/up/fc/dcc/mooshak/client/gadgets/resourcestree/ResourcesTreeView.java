package pt.up.fc.dcc.mooshak.client.gadgets.resourcestree;

import java.util.Date;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseList;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;

public interface ResourcesTreeView extends View {

	public interface Presenter {
		void onSelectedResourceChanged(String courseId, String id, String name,
				ResourceType type, String link, Date learningTime, String language);
	}

	void setPresenter(Presenter presenter);
	
	void setCourseId(String courseId);

	void setResources(CourseList resources);
	
	void updateResourceState(String courseId, String resourceId, 
			ResourceState state);
	
	CourseResource getOnSuccessResource(String id);
}
