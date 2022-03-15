package pt.up.fc.dcc.mooshak.client.gadgets.gameviewer;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;

import pt.up.fc.dcc.asuraviewer.client.player.AsuraViewer.Mode;
import pt.up.fc.dcc.mooshak.client.gadgets.GadgetPresenter;
import pt.up.fc.dcc.mooshak.client.gadgets.Token;
import pt.up.fc.dcc.mooshak.client.gadgets.gameviewer.GameViewerView.Presenter;
import pt.up.fc.dcc.mooshak.client.services.AsuraCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.EnkiCommandServiceAsync;
import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.asura.TournamentMovie;

public class GameViewerPresenter extends GadgetPresenter<GameViewerView> implements Presenter {
	
	private String tournamentId;

	public GameViewerPresenter(
			ParticipantCommandServiceAsync participantService, 
			EnkiCommandServiceAsync enkiService,
			AsuraCommandServiceAsync asuraService,
			GameViewerView view, Token token) {

		super(null, null, participantService, enkiService, null, asuraService, null, view, token);

		this.view.setPresenter(this);
	}

	@Override
	public void go(HasWidgets container) {
		setDependentData();
	}

	private void setDependentData() {
		view.setProblemId(getProblemId());
		
		//getCurrentPlayerId();
		
		asuraService.getLastTournamentForProblem(getProblemId(), new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe(caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				if (result != null)
					setTournament(result);
			}
		});
	}

	public void getCurrentPlayerId() {
		participantService.getParticipantLogged(new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String playerId) {
				view.setSubmissionId(playerId);
			}
		});
	}

	@Override
	public void setTournament(String id) {
		this.tournamentId = id;
		asuraService.getTournament(id, new AsyncCallback<TournamentMovie>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe(caught.getMessage());
			}

			@Override
			public void onSuccess(TournamentMovie tournamentMovie) {
				view.setMode(Mode.TOURNAMENT);
				view.setTournament(tournamentMovie.getJson());
				
				if (tournamentMovie.getPlayerId() != null)
					view.setSubmissionId(tournamentMovie.getPlayerId());
			}
		});
	}

	@Override
	public void requestMatchMovie(String id) {
		
		asuraService.getMatch(tournamentId, id, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				LOGGER.severe(caught.getMessage());
			}

			@Override
			public void onSuccess(String json) {
				view.setMovie(json);
			}
		});
	}

}
