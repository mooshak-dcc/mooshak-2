package pt.up.fc.dcc.mooshak.client.gadgets;

import com.google.gwt.event.shared.HandlerManager;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetFactory.GadgetType;
import pt.up.fc.dcc.mooshak.client.gadgets.mysubmissions.MySubmissionsPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.mysubmissions.MySubmissionsView;
import pt.up.fc.dcc.mooshak.client.gadgets.mysubmissions.MySubmissionsViewImpl;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

public class MySubmissions extends Gadget {

	public MySubmissions(ParticipantCommandServiceAsync rpc, BasicCommandServiceAsync rpcBasic, HandlerManager eventBus,
			Token token, GadgetType type, int numberOfQuestions) {
		super(token, type);

		MySubmissionsView view = new MySubmissionsViewImpl(token.getId(), numberOfQuestions);

		MySubmissionsPresenter presenter = new MySubmissionsPresenter(rpc, rpcBasic, view, token, eventBus);

		presenter.go(null);

		setPresenter(presenter);
		setView(view);
	}

	@Override
	public String getName() {
		return CONSTANTS.mySubmissions();
	}
}
