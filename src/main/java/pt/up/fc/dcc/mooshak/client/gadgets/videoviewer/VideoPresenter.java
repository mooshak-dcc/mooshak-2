package pt.up.fc.dcc.mooshak.client.gadgets.videoviewer;

import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.videoviewer.VideoView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;

import com.google.gwt.user.client.ui.HasWidgets;

public class VideoPresenter extends GadgetPresenter<VideoView> implements Presenter {

	public VideoPresenter(ParticipantCommandServiceAsync rpcService, EnkiCommandServiceAsync enkiService,
			VideoView view, Token token) {

		super(null, null, rpcService, enkiService, null, null, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
	}
}
