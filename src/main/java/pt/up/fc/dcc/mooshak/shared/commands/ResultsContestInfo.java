package pt.up.fc.dcc.mooshak.shared.commands;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Contains important information about the contest to Authentication
 * 
 * @author josepaiva
 */
public class ResultsContestInfo implements IsSerializable { 
	
	public enum ContestStatus implements IsSerializable { 
		CREATED, READY, 
		RUNNING, RUNNING_VIRTUALLY,
		FINISHED, CONCLUDED }
	
	private String contestId;
	
	private String contestName;
	
	private ContestStatus contestStatus;
	
	private boolean register;
	
	private boolean publicScoreboard;
	
	private boolean frozen;
	
	public ResultsContestInfo() {
	}

	/**
	 * @param contestId
	 * @param contestStatus
	 * @param register
	 */
	public ResultsContestInfo(String contestId, String contestName,
			ContestStatus contestStatus,
			boolean register,
			boolean publicScoreboard,
			boolean frozen) {
		super();
		this.contestId = contestId;
		this.contestName = contestName;
		this.contestStatus = contestStatus;
		this.register = register;
		this.publicScoreboard = publicScoreboard;
		this.frozen = frozen;
	}

	/**
	 * @return the contestId
	 */
	public String getContestId() {
		return contestId;
	}

	/**
	 * @param contestId the contestId to set
	 */
	public void setContestId(String contestId) {
		this.contestId = contestId;
	}

	/**
	 * @return the contestName
	 */
	public String getContestName() {
		return contestName;
	}

	/**
	 * @param contestName the contestName to set
	 */
	public void setContestName(String contestName) {
		this.contestName = contestName;
	}

	/**
	 * @return the frozen
	 */
	public boolean isFrozen() {
		return frozen;
	}

	/**
	 * @param frozen the frozen to set
	 */
	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}

	/**
	 * @return the register
	 */
	public boolean getRegister() {
		return register;
	}

	/**
	 * @param register the register to set
	 */
	public void setRegister(boolean register) {
		this.register = register;
	}

	/**
	 * @return the contestStatus
	 */
	public ContestStatus getContestStatus() {
		return contestStatus;
	}

	/**
	 * @param contestStatus the contestStatus to set
	 */
	public void setContestStatus(ContestStatus contestStatus) {
		this.contestStatus = contestStatus;
	}

	/**
	 * @return the publicScoreboard
	 */
	public boolean isPublicScoreboard() {
		return publicScoreboard;
	}

	/**
	 * @param publicScoreboard the publicScoreboard to set
	 */
	public void setPublicScoreboard(boolean publicScoreboard) {
		this.publicScoreboard = publicScoreboard;
	}
}
