package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.ProgramErrorListPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.ProgramErrorListView;
import pt.up.fc.dcc.mooshak.client.gadgets.programerrorlist.ProgramErrorListViewImpl;

public class ProgramErrorList extends Gadget {

	public ProgramErrorList(Token token, GadgetType type) {
		super(token, type);

		ProgramErrorListView view = new ProgramErrorListViewImpl();

		ProgramErrorListPresenter presenter = new ProgramErrorListPresenter(view, token);
		presenter.go(null);

		setPresenter(presenter);
		setView(view);

	}

	@Override
	public String getName() {
		return CONSTANTS.errorList();
	}
}
