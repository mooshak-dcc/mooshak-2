package pt.up.fc.dcc.mooshak.client.gadgets.relatedresources;

import java.util.Date;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceType;

public interface RelatedResourcesView extends View {

	public interface Presenter {
		void updateRelatedResources();

		void onSelectedResourceChanged(String id, String name, ResourceType type,
				String link, Date learningTime, String language);
	}
	
	void setPresenter(Presenter presenter);

	void addRelatedResource(CourseResource resource);
	
	
}
