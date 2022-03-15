package pt.up.fc.dcc.mooshak.shared.results.gamification;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Leaderboard single entry representation provided by Odin
 * 
 * @author josepaiva
 */
public class LeaderboardEntry implements IsSerializable {

	private String kind;
	private StudentInfo player;
	private float scoreValue;
	private String formattedScore;
	private String timeSpan;
	private long writeTimestampMillis;
	private String scoreTag;
	
	public LeaderboardEntry() {
	}
	
	/**
	 * @return the kind
	 */
	public String getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	/**
	 * @return the player
	 */
	public StudentInfo getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(StudentInfo player) {
		this.player = player;
	}

	/**
	 * @return the scoreValue
	 */
	public float getScoreValue() {
		return scoreValue;
	}

	/**
	 * @param scoreValue the scoreValue to set
	 */
	public void setScoreValue(float scoreValue) {
		this.scoreValue = scoreValue;
	}

	/**
	 * @return the formattedScore
	 */
	public String getFormattedScore() {
		return formattedScore;
	}

	/**
	 * @param formattedScore the formattedScore to set
	 */
	public void setFormattedScore(String formattedScore) {
		this.formattedScore = formattedScore;
	}

	/**
	 * @return the timeSpan
	 */
	public String getTimeSpan() {
		return timeSpan;
	}

	/**
	 * @param timeSpan the timeSpan to set
	 */
	public void setTimeSpan(String timeSpan) {
		this.timeSpan = timeSpan;
	}

	/**
	 * @return the writeTimestampMillis
	 */
	public long getWriteTimestampMillis() {
		return writeTimestampMillis;
	}

	/**
	 * @param writeTimestampMillis the writeTimestampMillis to set
	 */
	public void setWriteTimestampMillis(long writeTimestampMillis) {
		this.writeTimestampMillis = writeTimestampMillis;
	}

	/**
	 * @return the scoreTag
	 */
	public String getScoreTag() {
		return scoreTag;
	}

	/**
	 * @param scoreTag the scoreTag to set
	 */
	public void setScoreTag(String scoreTag) {
		this.scoreTag = scoreTag;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((kind == null) ? 0 : kind.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LeaderboardEntry other = (LeaderboardEntry) obj;
		if (kind == null) {
			if (other.kind != null)
				return false;
		} else if (!kind.equals(other.kind))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		return true;
	}

	
}
