package pt.up.fc.dcc.mooshak.shared.results.gamification;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AchievementEntry implements IsSerializable {
	
	private String achievementId;
	private String playerId;
	private int currentSteps;
	private Achievement achievement;
	private String formattedCurrentStepsString;
	private String achievementState;
	private long lastUpdatedTimestamp;
	private long experiencePoints;
	
	public AchievementEntry() {
	}
	
	public AchievementEntry(String achievementId, String playerId, Achievement achievement,
			int currentSteps, String formattedCurrentStepsString,
			String achievementState, long lastUpdatedTimestamp,
			long experiencePoints) {
		super();
		this.achievementId = achievementId;
		this.playerId = playerId;
		this.achievement = achievement;
		this.currentSteps = currentSteps;
		this.formattedCurrentStepsString = formattedCurrentStepsString;
		this.achievementState = achievementState;
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
		this.experiencePoints = experiencePoints;
	}

	/**
	 * @return the achievementId
	 */
	public String getAchievementId() {
		return achievementId;
	}

	/**
	 * @return the playerId
	 */
	public String getPlayerId() {
		return playerId;
	}

	/**
	 * @return the achievement
	 */
	public Achievement getAchievement() {
		return achievement;
	}

	/**
	 * @param achievement the achievement to set
	 */
	public void setAchievement(Achievement achievement) {
		this.achievement = achievement;
	}

	/**
	 * @return the currentSteps
	 */
	public int getCurrentSteps() {
		return currentSteps;
	}

	/**
	 * @return the formattedCurrentStepsString
	 */
	public String getFormattedCurrentStepsString() {
		return formattedCurrentStepsString;
	}

	/**
	 * @return the achievementState
	 */
	public String getAchievementState() {
		return achievementState;
	}

	/**
	 * @return the lastUpdatedTimestamp
	 */
	public long getLastUpdatedTimestamp() {
		return lastUpdatedTimestamp;
	}

	/**
	 * @return the experiencePoints
	 */
	public long getExperiencePoints() {
		return experiencePoints;
	}

	/**
	 * @param achievementId the achievementId to set
	 */
	public void setAchievementId(String achievementId) {
		this.achievementId = achievementId;
	}

	/**
	 * @param playerId the playerId to set
	 */
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	/**
	 * @param currentSteps the currentSteps to set
	 */
	public void setCurrentSteps(int currentSteps) {
		this.currentSteps = currentSteps;
	}

	/**
	 * @param formattedCurrentStepsString the formattedCurrentStepsString to set
	 */
	public void setFormattedCurrentStepsString(String formattedCurrentStepsString) {
		this.formattedCurrentStepsString = formattedCurrentStepsString;
	}

	/**
	 * @param achievementState the achievementState to set
	 */
	public void setAchievementState(String achievementState) {
		this.achievementState = achievementState;
	}

	/**
	 * @param lastUpdatedTimestamp the lastUpdatedTimestamp to set
	 */
	public void setLastUpdatedTimestamp(long lastUpdatedTimestamp) {
		this.lastUpdatedTimestamp = lastUpdatedTimestamp;
	}

	/**
	 * @param experiencePoints the experiencePoints to set
	 */
	public void setExperiencePoints(long experiencePoints) {
		this.experiencePoints = experiencePoints;
	}

}
