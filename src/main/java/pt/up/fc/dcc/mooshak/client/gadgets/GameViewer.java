package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.gameviewer.GameViewerPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.gameviewer.GameViewerView;
import pt.up.fc.dcc.mooshak.client.gadgets.gameviewer.GameViewerViewImpl;
import pt.up.fc.dcc.mooshak.client.services.AsuraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

public class GameViewer extends Gadget {

	public GameViewer(
			ParticipantCommandServiceAsync rpcParticipant, 
			EnkiCommandServiceAsync rpcEnki,
			AsuraCommandServiceAsync rpcAsura,
			Token token, GadgetType type) {
		super(token, type);

		GameViewerView view = new GameViewerViewImpl();

		GameViewerPresenter presenter = new GameViewerPresenter(rpcParticipant, rpcEnki,
				rpcAsura, view, token);

		presenter.go(null);

		setView(view);
		setPresenter(presenter);
	}

	@Override
	public String getName() {
		return CONSTANTS.gameViewer();
	}
}
