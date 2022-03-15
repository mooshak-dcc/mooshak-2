package pt.up.fc.dcc.mooshak.client.gadgets;

import com.google.gwt.event.shared.HandlerManager;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcerating.ResourceRatingPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcerating.ResourceRatingView;
import pt.up.fc.dcc.mooshak.client.gadgets.resourcerating.ResourceRatingViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;

public class ResourceRating extends Gadget {
	
	public ResourceRating(EnkiCommandServiceAsync rpcEnki,
			HandlerManager eventBus,
			Token token, GadgetType type) {
		super(token, type);
		
		ResourceRatingView view = new ResourceRatingViewImpl();
		
		ResourceRatingPresenter presenter = new ResourceRatingPresenter(
				rpcEnki, eventBus, view, token);
		
		presenter.go(null);
		
		setPresenter(presenter);
		setView(view);
	}
	
	@Override
	public String getName() {
		return CONSTANTS.rateResource();
	}
}
