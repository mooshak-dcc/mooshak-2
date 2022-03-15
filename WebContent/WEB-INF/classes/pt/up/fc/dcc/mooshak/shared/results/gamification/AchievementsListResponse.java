package pt.up.fc.dcc.mooshak.shared.results.gamification;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Response to listAchievements() given by the gamification service
 * 
 * @author josepaiva
 */
public class AchievementsListResponse implements IsSerializable {
	
	private String nextPageToken;
	private String prevPageToken;
	private List<AchievementEntry> items;
	private long lastUpdatedTimestamp;
	
	public AchievementsListResponse() {
		super();
	}

	public AchievementsListResponse(String nextPageToken, String prevPageToken,
			List<AchievementEntry> items) {
		super();
		this.nextPageToken = nextPageToken;
		this.prevPageToken = prevPageToken;
		this.items = items;
	}

	/**
	 * @return the nextPageToken
	 */
	public String getNextPageToken() {
		return nextPageToken;
	}

	/**
	 * @return the prevPageToken
	 */
	public String getPrevPageToken() {
		return prevPageToken;
	}

	/**
	 * @return the achievements
	 */
	public List<AchievementEntry> getItems() {
		return items;
	}

	/**
	 * @param nextPageToken the nextPageToken to set
	 */
	public void setNextPageToken(String nextPageToken) {
		this.nextPageToken = nextPageToken;
	}

	/**
	 * @param prevPageToken the prevPageToken to set
	 */
	public void setPrevPageToken(String prevPageToken) {
		this.prevPageToken = prevPageToken;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(List<AchievementEntry> items) {
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
	
}
