package pt.up.fc.dcc.mooshak.shared.events;

import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo.ContestStatus;

/**
 * Event to notify authentication of changes to contests states
 * 
 * @author josepaiva
 */
public class ContestUpdateEvent extends MooshakEvent {

	private ResultsContestInfo info = new ResultsContestInfo();
	
	public ContestUpdateEvent() {
		super();
	}

	/**
	 * @return the contestId
	 */
	public String getContestId() {
		return info.getContestId();
	}

	/**
	 * @param contestId the contestId to set
	 */
	public void setContestId(String contestId) {
		info.setContestId(contestId);
	}

	/**
	 * @return the contest
	 */
	public String getContest() {
		return info.getContestName();
	}

	/**
	 * @param contest the contest to set
	 */
	public void setContest(String contest) {
		info.setContestName(contest);
	}

	/**
	 * @return the state
	 */
	public ContestStatus getState() {
		return info.getContestStatus();
	}

	/**
	 * @param state the state to set
	 */
	public void setState(ContestStatus state) {
		info.setContestStatus(state);
	}

	/**
	 * @return the frozen
	 */
	public boolean getFrozen() {
		return info.isFrozen();
	}

	/**
	 * @param frozen the frozen to set
	 */
	public void setFrozen(boolean frozen) {
		info.setFrozen(frozen);
	}

	/**
	 * @return the register
	 */
	public boolean getRegister() {
		return info.getRegister();
	}

	/**
	 * @param register the register to set
	 */
	public void setRegister(boolean register) {
		info.setRegister(register);
	}

	
}

