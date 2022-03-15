package pt.up.fc.dcc.mooshak.client.gadgets;

import com.google.gwt.event.shared.HandlerManager;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.relatedresources.RelatedResourcesPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.relatedresources.RelatedResourcesView;
import pt.up.fc.dcc.mooshak.client.gadgets.relatedresources.RelatedResourcesViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;

public class RelatedResources extends Gadget {
	
	public RelatedResources(EnkiCommandServiceAsync rpcEnki,
			HandlerManager eventBus,
			Token token, GadgetType type) {
		super(token, type);
		
		RelatedResourcesView view = new RelatedResourcesViewImpl();
		
		RelatedResourcesPresenter presenter = new RelatedResourcesPresenter(
				rpcEnki, eventBus, view, token);
		
		presenter.go(null);
		
		setPresenter(presenter);
		setView(view);
	}
	
	@Override
	public String getName() {
		return CONSTANTS.relatedResources();
	}
}
