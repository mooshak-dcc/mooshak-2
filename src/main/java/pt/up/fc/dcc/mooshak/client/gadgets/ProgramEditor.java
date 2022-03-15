package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorView;
import pt.up.fc.dcc.mooshak.client.gadgets.programeditor.ProgramEditorViewImpl;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

/**
 * Gadget for editing programs
 * 
 * @author josepaiva
 */
public class ProgramEditor extends Gadget {

	public ProgramEditor(ParticipantCommandServiceAsync rpcService, EnkiCommandServiceAsync rpcEnki, Token token,
			GadgetType type) {
		super(token, type);

		ProgramEditorView view = new ProgramEditorViewImpl();

		ProgramEditorPresenter presenter = new ProgramEditorPresenter(rpcService, rpcEnki, view, token);

		presenter.go(null);

		setView(view);
		setPresenter(presenter);
	}

	@Override
	public String getName() {
		return CONSTANTS.editor();
	}

}
