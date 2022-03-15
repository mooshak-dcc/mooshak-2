package pt.up.fc.dcc.mooshak.server.commands;

import pt.up.fc.dcc.mooshak.client.services.AsuraCommandService;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.asura.TournamentMovie;

/**
 * Implementation of service interface for Asura commands
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AsuraCommandServiceImpl extends CommandService 
		implements AsuraCommandService {

	private static final long serialVersionUID = 1L;
	
	@Override
	public String getLastTournamentForProblem(String problemId) throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, JUDGE_PROFILE_ID, ADMIN_PROFILE_ID);
		
		String json = asuraManager.getLastTournamentForProblem(session, problemId);
		auditLog("getLastTournamentForProblem", problemId);
		
		return json;
	}

	@Override
	public TournamentMovie getTournament(String id) throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, JUDGE_PROFILE_ID, ADMIN_PROFILE_ID);
		
		TournamentMovie tournament = asuraManager.getTournamentJson(session, id);
		auditLog("getTournament", id);
		
		return tournament;
	}

	@Override
	public String getMatch(String tournamentId, String matchId) throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, JUDGE_PROFILE_ID, ADMIN_PROFILE_ID);
		
		String json = asuraManager.getMatchMovieJson(session, tournamentId, matchId);
		auditLog("getMatch", tournamentId, matchId);
		
		return json;
	}
}
