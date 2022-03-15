package pt.up.fc.dcc.mooshak.client.guis.enki;

import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.services.AsuraCommandService;
import pt.up.fc.dcc.mooshak.client.services.AsuraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandService;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandService;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandService;
import pt.up.fc.dcc.mooshak.client.services.KoraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandService;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.QuizCommandService;
import pt.up.fc.dcc.mooshak.client.services.QuizCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class EnkiEntryPoint implements EntryPoint {

	/**
	 * This is the entry point method.
	 */
	@Override
	public void onModuleLoad() {

		final BasicCommandServiceAsync basicService = GWT.create(BasicCommandService.class);
		final ParticipantCommandServiceAsync participantService = GWT.create(ParticipantCommandService.class);
		final EnkiCommandServiceAsync enkiService = GWT.create(EnkiCommandService.class);
		final KoraCommandServiceAsync koraService = GWT.create(KoraCommandService.class);
		final AsuraCommandServiceAsync asuraService = GWT.create(AsuraCommandService.class);
		final QuizCommandServiceAsync quizService = GWT.create(QuizCommandService.class);

		basicService.context(new AsyncCallback<ContextInfo>() {

			@Override
			public void onSuccess(ContextInfo contextInfo) {

				AppController controller = new AppController(
						basicService, participantService, enkiService, koraService, 
						asuraService, quizService, new HandlerManager(null), contextInfo);
				controller.go(RootLayoutPanel.get());
			}

			@Override
			public void onFailure(Throwable caught) {
				processfailure(caught);
			}
		});
	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

}
