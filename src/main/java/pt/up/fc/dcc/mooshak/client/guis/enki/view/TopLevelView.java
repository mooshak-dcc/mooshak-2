package pt.up.fc.dcc.mooshak.client.guis.enki.view;

import java.util.Date;

import com.google.gwt.user.client.ui.Widget;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.gadgets.Gadget;
import pt.up.fc.dcc.mooshak.client.gadgets.ResourcesTree;
import pt.up.fc.dcc.mooshak.client.guis.enki.view.TopLevelViewImpl.Region;
import pt.up.fc.dcc.mooshak.client.guis.icpc.view.HasTutorial;
import pt.up.fc.dcc.mooshak.shared.results.sequencing.CourseResource.ResourceState;

public interface TopLevelView extends View, HasTutorial {
	
	public interface Presenter {
		void onHelpClicked();
		void onLogoutClicked();
	}

	void setPresenter(Presenter presenter);
	Presenter getPresenter();

	void setCurrentResourceId(String resId);
	String getCurrentResourceId();
	
	void setResourceTreeGadget(ResourcesTree gadget);
	void addGadget(String id, Gadget gadget, Region region);
	void selectTab(Region region, int tab);
	void selectTab(String id, Widget widget);
	
	void setContest(String name);
	String getTeam();
	void setTeam(String name);
	void setDates(Date start, Date end, Date current);
	void updateResourceState(String courseId, String id, 
			ResourceState state);
	
	void setTutorialAnchorPoints(TutorialView tutorialView);
	
}
