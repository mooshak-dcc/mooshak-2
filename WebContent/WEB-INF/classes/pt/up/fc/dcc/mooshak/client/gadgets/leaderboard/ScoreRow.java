package pt.up.fc.dcc.mooshak.client.gadgets.leaderboard;

import com.google.gwt.view.client.ProvidesKey;

import pt.up.fc.dcc.mooshak.shared.results.gamification.StudentInfo;

public class ScoreRow implements Comparable<ScoreRow> {
	
	public static final ProvidesKey<ScoreRow> KEY_PROVIDER = 
			new ProvidesKey<ScoreRow>() {
        public Object getKey(ScoreRow item) {
            return item == null ? null : item.getId();
        }
    };
	
	private static int nextId = 0;

	private final int id;
	private StudentInfo player;
	private float scoreValue;
	private String formattedScore;
	private String timeSpan;
	private long writeTimestampMillis;
	private String scoreTag;

	public ScoreRow() {
		super();
		this.id = nextId;
        nextId++;
	}

	public ScoreRow(StudentInfo player, float scoreValue, String formattedScore,
			String timeSpan, long writeTimestampMillis, String scoreTag) {
		super();
		this.id = nextId;
        nextId++;
		this.player = player;
		this.scoreValue = scoreValue;
		this.formattedScore = formattedScore;
		this.timeSpan = timeSpan;
		this.writeTimestampMillis = writeTimestampMillis;
		this.scoreTag = scoreTag;
	}
	
	@Override
	public int compareTo(ScoreRow o) {
		return (o == null) ? -1 : -new Float(o.getScoreValue())
                .compareTo(new Float(scoreValue));
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
		ScoreRow other = (ScoreRow) obj;
		if (formattedScore == null) {
			if (other.formattedScore != null)
				return false;
		} else if (!formattedScore.equals(other.formattedScore))
			return false;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (scoreTag == null) {
			if (other.scoreTag != null)
				return false;
		} else if (!scoreTag.equals(other.scoreTag))
			return false;
		if (Float.floatToIntBits(scoreValue) != Float
				.floatToIntBits(other.scoreValue))
			return false;
		if (timeSpan == null) {
			if (other.timeSpan != null)
				return false;
		} else if (!timeSpan.equals(other.timeSpan))
			return false;
		if (writeTimestampMillis != other.writeTimestampMillis)
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((formattedScore == null) ? 0 : formattedScore.hashCode());
		result = prime * result + ((player == null) ? 0 : player.hashCode());
		result = prime * result
				+ ((scoreTag == null) ? 0 : scoreTag.hashCode());
		result = prime * result + Float.floatToIntBits(scoreValue);
		result = prime * result
				+ ((timeSpan == null) ? 0 : timeSpan.hashCode());
		result = prime * result
				+ (int) (writeTimestampMillis ^ (writeTimestampMillis >>> 32));
		return result;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return this.id;
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

}
