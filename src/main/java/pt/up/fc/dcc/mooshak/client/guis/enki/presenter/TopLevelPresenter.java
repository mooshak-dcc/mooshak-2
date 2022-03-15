package pt.up.fc.dcc.mooshak.client.guis.enki.presenter;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.mooshak.client.Presenter;
import pt.up.fc.dcc.mooshak.client.services.BasicCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.guis.authentication.AuthenticationPresenter;
import pt.up.fc.dcc.mooshak.client.guis.enki.view.TopLevelView;
import pt.up.fc.dcc.mooshak.client.guis.icpc.event.HelpTutorialEvent;
import pt.up.fc.dcc.mooshak.client.guis.icpc.i18n.ICPCConstants;
import pt.up.fc.dcc.mooshak.shared.results.ContextInfo;

public class TopLevelPresenter implements Presenter, TopLevelView.Presenter {

	private ICPCConstants constants = GWT.create(ICPCConstants.class);

	private EnkiCommandServiceAsync rpc;
	private BasicCommandServiceAsync rpcBasic;
	private HandlerManager eventBus;
	private TopLevelView view;

	public TopLevelPresenter(EnkiCommandServiceAsync rpc, BasicCommandServiceAsync rpcBasic, HandlerManager eventBus,
			TopLevelView view) {
		this.rpc = rpc;
		this.rpcBasic = rpcBasic;
		this.eventBus = eventBus;
		this.view = view;

		this.view.setPresenter(this);

	}

	@Override
	public void go(HasWidgets container) {
		container.clear();
		container.add(view.asWidget());
		setDependantData();
	}

	public void setDependantData() {

	}

	void processfailure(Throwable caught) {
		Window.alert(caught.getMessage());
		AuthenticationPresenter.logout(caught);
	}

	public void setContext(ContextInfo info) {
		view.setContest(info.getactivityName());
		view.setTeam(info.getParticipantName());
		view.setDates(info.getStart(), info.getEnd(), info.getCurrent());
	}

	@Override
	public void onHelpClicked() {
		eventBus.fireEvent(new HelpTutorialEvent());
	}

	@Override
	public void onLogoutClicked() {
		if (Window.confirm(constants.logoutConfirmation()))
			AuthenticationPresenter.logout();
	}

}
