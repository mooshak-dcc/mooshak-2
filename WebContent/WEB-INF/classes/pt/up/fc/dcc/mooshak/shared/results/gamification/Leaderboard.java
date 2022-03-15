package pt.up.fc.dcc.mooshak.shared.results.gamification;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Leaderboard implements IsSerializable {
	
	public enum Order implements IsSerializable { ASC, DESC }

	private String leaderboardId;
	private Order scoreOrder;
	private Float scoreMin;
	private Float scoreMax;
	private long lastUpdatedTimestamp;
	
	public Leaderboard() {
		super();
	}

	public Leaderboard(String leaderboardId, Order scoreOrder, Float scoreMin, Float scoreMax) {
		super();
		this.leaderboardId = leaderboardId;
		this.scoreOrder = scoreOrder;
		this.scoreMin = scoreMin;
		this.scoreMax = scoreMax;
	}

	/**
	 * @return the leaderboardId
	 */
	public String getLeaderboardId() {
		return leaderboardId;
	}

	/**
	 * @param leaderboardId the leaderboardId to set
	 */
	public void setLeaderboardId(String leaderboardId) {
		this.leaderboardId = leaderboardId;
	}

	/**
	 * @return the scoreOrder
	 */
	public Order getScoreOrder() {
		return scoreOrder;
	}

	/**
	 * @param scoreOrder the scoreOrder to set
	 */
	public void setScoreOrder(Order scoreOrder) {
		this.scoreOrder = scoreOrder;
	}

	/**
	 * @return the scoreMin
	 */
	public Float getScoreMin() {
		return scoreMin;
	}

	/**
	 * @param scoreMin the scoreMin to set
	 */
	public void setScoreMin(Float scoreMin) {
		this.scoreMin = scoreMin;
	}

	/**
	 * @return the scoreMax
	 */
	public Float getScoreMax() {
		return scoreMax;
	}

	/**
	 * @param scoreMax the scoreMax to set
	 */
	public void setScoreMax(Float scoreMax) {
		this.scoreMax = scoreMax;
	}

	/**
	 * @return the lastUpdatedTimestamp
	 */
	public long getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}

	/**
	 * @param lastUpdatedTimestamp the lastUpdatedTimestamp to set
	 */
	public void setLastUpdatedTimestamp(long lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}
	
	
}
