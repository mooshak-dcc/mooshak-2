package pt.up.fc.dcc.mooshak.client.gadgets;

import com.google.gwt.event.shared.HandlerManager;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditorobservations.ProgramObservationsViewImpl;

/**
 * Gadget for displaying program observations
 * 
 * @author josepaiva
 */
public class ProgramEditorObservations extends Gadget {

	
	public ProgramEditorObservations(
			HandlerManager eventBus, Token token, GadgetType type) {
		super(token, type);
		
		ProgramObservationsView view = new ProgramObservationsViewImpl();
		
		ProgramObservationsPresenter presenter = 
				new ProgramObservationsPresenter(eventBus, view, token);
		presenter.go(null);
		
		setPresenter(presenter);
		setView(view);
		
	}
	
	@Override
	public String getName() {
		return CONSTANTS.observations();
	}
	
}
