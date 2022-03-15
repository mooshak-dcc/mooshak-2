package pt.up.fc.dcc.mooshak.client.gadgets;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.askquestion.AskPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.askquestion.AskView;
import pt.up.fc.dcc.mooshak.client.gadgets.askquestion.AskViewImpl;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

/**
 * Gadget for asking questions
 * 
 * @author josepaiva
 */
public class AskQuestion extends Gadget {

	private ParticipantCommandServiceAsync rpc;
	private BasicCommandServiceAsync rpcBasic;

	public AskQuestion(ParticipantCommandServiceAsync rpcService, BasicCommandServiceAsync rpcBasicService, Token token,
			GadgetType type, int numberOfQuestions) {
		super(token, type);

		this.rpc = rpcService;
		this.rpcBasic = rpcBasicService;

		AskView askView = new AskViewImpl(numberOfQuestions);
		askView.setFiltering();

		AskPresenter presenter = new AskPresenter(rpc, rpcBasic, askView, token, "");

		presenter.go(null);

		setView(askView);
		setPresenter(presenter);
	}

	@Override
	public String getName() {
		return CONSTANTS.askQuestion();
	}

}
