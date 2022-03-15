package pt.up.fc.dcc.mooshak.client.gadgets;

import com.google.gwt.event.shared.HandlerManager;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.ResourcesTreePresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.ResourcesTreeView;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcestree.ResourcesTreeViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;

public class ResourcesTree extends Gadget {

	public ResourcesTree(HandlerManager eventBus, EnkiCommandServiceAsync enkiService, Token token, GadgetType type) {
		super(token, type);

		ResourcesTreeView view = new ResourcesTreeViewImpl();

		ResourcesTreePresenter presenter = new ResourcesTreePresenter(eventBus, enkiService, view, token);
		presenter.go(null);

		setView(view);
		setPresenter(presenter);

	}

	@Override
	public String getName() {
		return CONSTANTS.resources();
	}
}
