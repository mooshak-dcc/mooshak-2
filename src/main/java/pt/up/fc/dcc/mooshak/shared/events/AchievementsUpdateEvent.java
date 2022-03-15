package pt.up.fc.dcc.mooshak.shared.events;

import pt.up.fc.dcc.mooshak.shared.results.gamification.AchievementsListResponse;

/**
 * Event to notify students of changes in their achievements
 * 
 * @author josepaiva
 */
public class AchievementsUpdateEvent extends MooshakEvent {
	
	private AchievementsListResponse achievements = null;
	private String resourceId = null;
	private String state = null;
	
	public AchievementsUpdateEvent() {
		super();
	}

	/**
	 * @return the achievements
	 */
	public AchievementsListResponse getAchievements() {
		return achievements;
	}

	/**
	 * @param achievements the achievements to set
	 */
	public void setAchievements(AchievementsListResponse achievements) {
		this.achievements = achievements;
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

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}

}
