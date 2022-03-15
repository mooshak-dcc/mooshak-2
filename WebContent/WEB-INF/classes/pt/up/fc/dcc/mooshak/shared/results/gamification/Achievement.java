package pt.up.fc.dcc.mooshak.shared.results.gamification;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Achievement implements IsSerializable {

	private String achievementId;
	private String leaderboardId;
	private String name;
	private String description;
	private String achievementType;
	private int totalSteps;
	private String formattedTotalSteps;
	private String revealedIconUrl;
	private boolean isRevealedIconUrlDefault;
	private String unlockedIconUrl;
	private boolean isUnlockedIconUrlDefault;
	private String initialState;
	private long experiencePoints;
	private long lastUpdatedTimestamp;
	
	
	public Achievement() {	}
	
	public Achievement(String id, String name, String leaderboardId) {
		this.achievementId = id;
		this.name = name;
		this.leaderboardId = leaderboardId;
	}

	public Achievement(String achievementId, String name, 
			String leaderboardId, String description, String achievementType, 
			Integer totalSteps,	String formattedTotalSteps, String revealedIconUrl,
			Boolean isRevealedIconUrlDefault, String unlockedIconUrl,
			Boolean isUnlockedIconUrlDefault, String initialState,
			Long experiencePoints) {
		super();
		this.achievementId = achievementId;
		this.leaderboardId = leaderboardId;
		this.name = name;
		this.description = description;
		this.achievementType = achievementType;
		this.totalSteps = totalSteps;
		this.formattedTotalSteps = formattedTotalSteps;
		this.revealedIconUrl = revealedIconUrl;
		this.isRevealedIconUrlDefault = isRevealedIconUrlDefault;
		this.unlockedIconUrl = unlockedIconUrl;
		this.isUnlockedIconUrlDefault = isUnlockedIconUrlDefault;
		this.initialState = initialState;
		this.experiencePoints = experiencePoints;
	}

	/**
	 * @return the achievementId
	 */
	public String getAchievementId() {
		return achievementId;
	}

	/**
	 * @return the leaderboardId
	 */
	public String getLeaderboardId() {
		return leaderboardId;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the achievementType
	 */
	public String getAchievementType() {
		return achievementType;
	}

	/**
	 * @return the totalSteps
	 */
	public Integer getTotalSteps() {
		return totalSteps;
	}

	/**
	 * @return the formattedTotalSteps
	 */
	public String getFormattedTotalSteps() {
		return formattedTotalSteps;
	}

	/**
	 * @return the revealedIconUrl
	 */
	public String getRevealedIconUrl() {
		return revealedIconUrl;
	}

	/**
	 * @return the isRevealedIconUrlDefault
	 */
	public Boolean getIsRevealedIconUrlDefault() {
		return isRevealedIconUrlDefault;
	}

	/**
	 * @return the unlockedIconUrl
	 */
	public String getUnlockedIconUrl() {
		return unlockedIconUrl;
	}

	/**
	 * @return the isUnlockedIconUrlDefault
	 */
	public Boolean getIsUnlockedIconUrlDefault() {
		return isUnlockedIconUrlDefault;
	}

	/**
	 * @return the initialState
	 */
	public String getInitialState() {
		return initialState;
	}

	/**
	 * @return the experiencePoints
	 */
	public Long getExperiencePoints() {
		return experiencePoints;
	}

	/**
	 * @param achievementId the achievementId to set
	 */
	public void setAchievementId(String achievementId) {
		this.achievementId = achievementId;
	}

	/**
	 * @param leaderboardId the leaderboardId to set
	 */
	public void setLeaderboardId(String leaderboardId) {
		this.leaderboardId = leaderboardId;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param achievementType the achievementType to set
	 */
	public void setAchievementType(String achievementType) {
		this.achievementType = achievementType;
	}

	/**
	 * @param totalSteps the totalSteps to set
	 */
	public void setTotalSteps(Integer totalSteps) {
		this.totalSteps = totalSteps;
	}

	/**
	 * @param formattedTotalSteps the formattedTotalSteps to set
	 */
	public void setFormattedTotalSteps(String formattedTotalSteps) {
		this.formattedTotalSteps = formattedTotalSteps;
	}

	/**
	 * @param revealedIconUrl the revealedIconUrl to set
	 */
	public void setRevealedIconUrl(String revealedIconUrl) {
		this.revealedIconUrl = revealedIconUrl;
	}

	/**
	 * @param isRevealedIconUrlDefault the isRevealedIconUrlDefault to set
	 */
	public void setIsRevealedIconUrlDefault(Boolean isRevealedIconUrlDefault) {
		this.isRevealedIconUrlDefault = isRevealedIconUrlDefault;
	}

	/**
	 * @param unlockedIconUrl the unlockedIconUrl to set
	 */
	public void setUnlockedIconUrl(String unlockedIconUrl) {
		this.unlockedIconUrl = unlockedIconUrl;
	}

	/**
	 * @param isUnlockedIconUrlDefault the isUnlockedIconUrlDefault to set
	 */
	public void setIsUnlockedIconUrlDefault(Boolean isUnlockedIconUrlDefault) {
		this.isUnlockedIconUrlDefault = isUnlockedIconUrlDefault;
	}

	/**
	 * @param initialState the initialState to set
	 */
	public void setInitialState(String initialState) {
		this.initialState = initialState;
	}

	/**
	 * @param experiencePoints the experiencePoints to set
	 */
	public void setExperiencePoints(Long experiencePoints) {
		this.experiencePoints = experiencePoints;
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
