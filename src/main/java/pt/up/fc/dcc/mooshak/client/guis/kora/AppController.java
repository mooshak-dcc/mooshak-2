package pt.up.fc.dcc.mooshak.client.guis.kora;

import java.util.logging.Level;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.AbstractAppController;
import pt.up.fc.dcc.mooshak.client.events.EventManager;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.kora.Token.Command;
import pt.up.fc.dcc.mooshak.client.guis.kora.presenter.TopLevelPresenter;
import pt.up.fc.dcc.mooshak.client.guis.kora.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.kora.view.TopLevelViewImpl;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.events.LogoutEvent;
import pt.up.fc.dcc.mooshak.shared.events.MooshakEventListener;
import pt.up.fc.dcc.mooshak.shared.events.ReportNotificationEvent;

public class AppController extends AbstractAppController implements ValueChangeHandler<String> {

	private KoraCommandServiceAsync rpcKora;
	private ParticipantCommandServiceAsync rpcParticipant;
	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private HasWidgets container;

	private TopLevelPresenter toplevelPresenter = null;

	private EventManager eventManager = EventManager.getInstance();

	private TopLevelView topLevelView = null;

	private Command currentCommand = null;

	public AppController(KoraCommandServiceAsync rpcKora, ParticipantCommandServiceAsync rpcParticipant, 
			BasicCommandServiceAsync rpcBasic, HandlerManager eventBus) {

		this.rpcKora = rpcKora;
		this.rpcParticipant = rpcParticipant;
		this.rpcBasic = rpcBasic;
		this.eventBus = eventBus;

		bind();
	}

	private void bind() {
		History.addValueChangeHandler(this);

		eventManager.addListener(ReportNotificationEvent.class,
				new MooshakEventListener<ReportNotificationEvent>() {

					@Override
					public void receiveEvent(ReportNotificationEvent message) {

						if (toplevelPresenter == null)
							LOGGER.log(Level.SEVERE, "Unknown problem ID:"
									+ message.getProblemId());
						else
							toplevelPresenter.update(message.getSubmissionId(),
									message.isConsider());
					}
				});

		eventManager.addListener(LogoutEvent.class,
				new MooshakEventListener<LogoutEvent>() {

					@Override
					public void receiveEvent(LogoutEvent event) {
						AuthenticationPresenter.logout(event.getReason());
						LOGGER.log(Level.INFO, event.getReason());
					}

				});
	}

	public void go(final HasWidgets container) {
		this.container = container;

		if ("".equals(History.getToken())) {

			Token token = new Token();
			token.setCommand(Command.TOP);
			History.newItem(token.toString());

		} else
			History.fireCurrentHistoryState();

	}

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		final Token token = new Token(event.getValue());
		Command command = token.getCommand();
		currentCommand = command;

		showWaitCursor();

		if (command == Command.TOP) {
			makeToplevel();
		} else if (topLevelView == null) {
			redirectToToplevel();
		} else {
			//  NoOp
		}

		showDefaultCursor();
	}

	/**
	 * Make a a card top level
	 */
	private void makeToplevel() {

		if (topLevelView == null) {
			topLevelView = new TopLevelViewImpl();
		}

		toplevelPresenter = new TopLevelPresenter(rpcKora, rpcParticipant, rpcBasic, 
				eventBus, topLevelView);

		toplevelPresenter.go(container);

	}
	
}
