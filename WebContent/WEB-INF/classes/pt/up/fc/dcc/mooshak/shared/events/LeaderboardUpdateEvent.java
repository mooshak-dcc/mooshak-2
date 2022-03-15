package pt.up.fc.dcc.mooshak.shared.events;

import pt.up.fc.dcc.mooshak.shared.results.gamification.LeaderboardResponse;

/**
 * Event to notify students of changes in the leaderboard
 * 
 * @author josepaiva
 */
public class LeaderboardUpdateEvent extends MooshakEvent {
	
	private LeaderboardResponse leaderboard = null;
	private String resourceId = null;
	
	public LeaderboardUpdateEvent() {
		super();
	}

	/**
	 * @return the leaderboard
	 */
	public LeaderboardResponse getLeaderboard() {
		return leaderboard;
	}

	/**
	 * @param leaderboard the leaderboard to set
	 */
	public void setLeaderboard(LeaderboardResponse leaderboard) {
		this.leaderboard = leaderboard;
	}

	/**
	 * @return the resourceId
	 */
	public String getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId the resourceId to set
	 */
	public void setResourceId(String resourceId) {
		this.resourceId = resourceId;
	}
	
	
}
