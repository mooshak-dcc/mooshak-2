package pt.up.fc.dcc.mooshak.shared.asura;

import pt.up.fc.dcc.mooshak.shared.events.MooshakEvent;

/**
 * Event to notify students of a new tournament movie
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class TournamentMovieEvent extends MooshakEvent {

	private String problemId;
	private String tournamentId;
	
	public TournamentMovieEvent() {
		super();
	}

	/**
	 * @return the problemId
	 */
	public String getProblemId() {
		return problemId;
	}

	/**
	 * @param problemId the problemId to set
	 */
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}

	/**
	 * @return the tournamentId
	 */
	public String getTournamentId() {
		return tournamentId;
	}

	/**
	 * @param tournamentId the tournamentId to set
	 */
	public void setTournamentId(String tournamentId) {
		this.tournamentId = tournamentId;
	}
}
