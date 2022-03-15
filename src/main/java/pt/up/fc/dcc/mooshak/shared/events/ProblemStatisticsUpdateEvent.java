package pt.up.fc.dcc.mooshak.shared.events;

import pt.up.fc.dcc.mooshak.shared.results.ProblemStatistics;

public class ProblemStatisticsUpdateEvent extends MooshakEvent {
	
	private ProblemStatistics statistics = new ProblemStatistics();
	private String resourceId = null;
	
	public ProblemStatisticsUpdateEvent() {
		super();
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
	 * @return the totalStudents
	 */
	public int getTotalStudents() {
		return statistics.getTotalStudents();
	}

	/**
	 * @param totalStudents the totalStudents to set
	 */
	public void setTotalStudents(int totalStudents) {
		statistics.setTotalStudents(totalStudents);
	}

	/**
	 * @return the solvingAttempts
	 */
	public int getSolvingAttempts() {
		return statistics.getTotalStudents();
	}

	/**
	 * @return the numberOfStudentsWhoTried
	 */
	public int getNumberOfStudentsWhoTried() {
		return statistics.getNumberOfStudentsWhoTried();
	}

	/**
	 * @return the numberOfStudentsWhoSolved
	 */
	public int getNumberOfStudentsWhoSolved() {
		return statistics.getNumberOfStudentsWhoSolved();
	}

	/**
	 * @return the solvedAtFirst
	 */
	public int getSolvedAtFirst() {
		return statistics.getSolvedAtFirst();
	}

	/**
	 * @return the solvedAtSecond
	 */
	public int getSolvedAtSecond() {
		return statistics.getSolvedAtSecond();
	}

	/**
	 * @return the solvedAtThird
	 */
	public int getSolvedAtThird() {
		return statistics.getSolvedAtThird();
	}

	/**
	 * @param solvingAttempts the solvingAttempts to set
	 */
	public void setSolvingAttempts(int solvingAttempts) {
		statistics.setSolvingAttempts(solvingAttempts);
	}

	/**
	 * @param numberOfStudentsWhoTried the numberOfStudentsWhoTried to set
	 */
	public void setNumberOfStudentsWhoTried(int numberOfStudentsWhoTried) {
		statistics.setNumberOfStudentsWhoTried(numberOfStudentsWhoTried);
	}

	/**
	 * @param numberOfStudentsWhoSolved the numberOfStudentsWhoSolved to set
	 */
	public void setNumberOfStudentsWhoSolved(int numberOfStudentsWhoSolved) {
		statistics.setNumberOfStudentsWhoSolved(numberOfStudentsWhoSolved);
	}

	/**
	 * @param solvedAtFirst the solvedAtFirst to set
	 */
	public void setSolvedAtFirst(int solvedAtFirst) {
		statistics.setSolvedAtFirst(solvedAtFirst);
	}

	/**
	 * @param solvedAtSecond the solvedAtSecond to set
	 */
	public void setSolvedAtSecond(int solvedAtSecond) {
		statistics.setSolvedAtSecond(solvedAtSecond);
	}

	/**
	 * @param solvedAtThird the solvedAtThird to set
	 */
	public void setSolvedAtThird(int solvedAtThird) {
		statistics.setSolvedAtThird(solvedAtThird);
	}
	
}
