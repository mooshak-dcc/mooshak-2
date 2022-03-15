package pt.up.fc.dcc.mooshak.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.shared.asura.TournamentMovie;

/**
 * Asynchronous service interface for Asura commands
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public interface AsuraCommandServiceAsync {

	void getTournament(String id, AsyncCallback<TournamentMovie> callback);

	void getMatch(String tournamentId, String matchId, AsyncCallback<String> callback);

	void getLastTournamentForProblem(String problemId, AsyncCallback<String> callback);

}
