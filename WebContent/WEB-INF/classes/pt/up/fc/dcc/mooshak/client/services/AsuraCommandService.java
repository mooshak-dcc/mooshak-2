package pt.up.fc.dcc.mooshak.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.asura.TournamentMovie;

/**
 * Synchronous service interface for Asura commands
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
@RemoteServiceRelativePath("asuraCommand")
public interface AsuraCommandService extends RemoteService {

	TournamentMovie getTournament(String id) throws MooshakException;
	
	String getMatch(String tournamentId, String matchId) throws MooshakException;

	String getLastTournamentForProblem(String problemId) throws MooshakException;
}
