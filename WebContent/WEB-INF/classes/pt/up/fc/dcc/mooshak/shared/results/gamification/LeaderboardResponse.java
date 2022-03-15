package pt.up.fc.dcc.mooshak.shared.results.gamification;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class LeaderboardResponse implements IsSerializable {

	private String nextPageToken;
	private String prevPageToken;
	private int numScores;
	private List<LeaderboardEntry> items;
	private long lastUpdatedTimestamp;
	
	public LeaderboardResponse() {}

	/**
	 * @return the nextPageToken
	 */
	public String getNextPageToken() {
		return nextPageToken;
	}

	/**
	 * @param nextPageToken the nextPageToken to set
	 */
	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	/**
	 * @return the prevPageToken
	 */
	public String getPrevPageToken() {
		return prevPageToken;
	}

	/**
	 * @param prevPageToken the prevPageToken to set
	 */
	public void setPrevPageToken(String prevPageToken) {
		this.prevPageToken = prevPageToken;
	}

	/**
	 * @return the numScores
	 */
	public int getNumScores() {
		return numScores;
	}

	/**
	 * @param numScores the numScores to set
	 */
	public void setNumScores(int numScores) {
		this.numScores = numScores;
	}

	/**
	 * @return the items
	 */
	public List<LeaderboardEntry> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<LeaderboardEntry> items) {
		this.items = items;
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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LeaderboardResponse [nextPageToken=" + nextPageToken
				+ ", prevPageToken=" + prevPageToken + ", numScores="
				+ numScores + ", items=" + items + "]";
	}
	
	
}
