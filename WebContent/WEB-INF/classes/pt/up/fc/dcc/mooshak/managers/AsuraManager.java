package pt.up.fc.dcc.mooshak.managers;

import java.util.Comparator;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Match;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Tournament;
import pt.up.fc.dcc.mooshak.content.types.Tournaments;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.asura.TournamentMovie;

/**
 * Manages requests related to Asura challenges. 
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class AsuraManager extends Manager {
	
	private static AsuraManager manager = null;

	/**
	 * Get a single instance of this class
	 * 
	 * @return {@link AsuraManager} single instance of this class
	 * @throws MooshakException
	 */
	public static AsuraManager getInstance() {

		if (manager == null)
			manager = new AsuraManager();
		return manager;
	}

	private AsuraManager() {
	}

	/**
	 * Get last tournament realized for a certain problem or {@code null} if no tournament exists
	 * 
	 * @param session {@link Session} session
	 * @param problemId {@link String} ID of the problem
	 * @return {@link String} ID of the tournament or {@code null} if no tournament exists
	 */
	public String getLastTournamentForProblem(Session session, final String problemId) 
			throws MooshakException {
		
		Contest contest = session.getContest();
		
		Tournaments tournaments = contest.open("tournaments");
		
		Tournament tournament = tournaments.getContent().stream()
			.filter(t -> {
				Problem problem;
				try {
					problem = t.getProblem();
				} catch (MooshakContentException e) {
					return false;
				}
				return problem != null && problem.getIdName().equals(problemId);
			})
			.sorted(new Comparator<Tournament>() {

				@Override
				public int compare(Tournament t1, Tournament t2) {
					return t1.getDate().compareTo(t2.getDate());
				}
			}.reversed())
			.findFirst()
			.orElse(null);
		
		if (tournament == null)
			return null;
		
		return tournament.getIdName();
	}
	
	/**
	 * Get tournament as a JSON string
	 *  
	 * @param {@link Session} session
	 * @param tournamentId {@link String} ID of the tournament
	 * @return {@link String} JSON string of the tournament
	 * @throws MooshakException - if an error occurs while fetching tournament JSON
	 */
	public TournamentMovie getTournamentJson(Session session, String tournamentId) 
			throws MooshakException {
		
		Contest contest = session.getContest();
		
		Tournaments tournaments = contest.open("tournaments");
		
		Tournament tournament = tournaments.open(tournamentId);
		
		TournamentMovie tournamentMovie = new TournamentMovie();
		tournamentMovie.setJson(tournament.getTournamentJson());
		
		try {
			tournamentMovie.setPlayerId(tournament.getPlayerId(
					session.getParticipant().getIdName()));
		} catch (Exception e) {
			tournamentMovie.setPlayerId(null);
		}
		
		return tournamentMovie;
	}
	
	/**
	 * Get match movie JSON string
	 *  
	 * @param {@link Session} session
	 * @param tournamentId {@link String} ID of the tournament
	 * @param matchId {@link String} ID of the match
	 * @return {@link String} JSON string of the match movie
	 * @throws MooshakException - if an error occurs while fetching match JSON
	 */
	public String getMatchMovieJson(Session session, String tournamentId, String matchId) 
			throws MooshakException {
		
		Contest contest = session.getContest();
		
		Tournaments tournaments = contest.open("tournaments");
		
		Tournament tournament = tournaments.open(tournamentId);
		
		Match match = tournament.find(matchId);
		
		return match.readMovie();
	}
}
