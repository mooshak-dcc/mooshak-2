package pt.up.fc.dcc.mooshak.shared.results;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Statistics of submissions to a problem
 * 
 * @author josepaiva
 */
public class ProblemStatistics implements IsSerializable {

	private int totalStudents;
	private int solvingAttempts;
	private int numberOfStudentsWhoTried;
	private int numberOfStudentsWhoSolved;
	private int solvedAtFirst;
	private int solvedAtSecond;
	private int solvedAtThird;
	
	public ProblemStatistics() {
		super();
		
		totalStudents = 0;
		solvingAttempts = 0;
		numberOfStudentsWhoTried = 0;
		numberOfStudentsWhoSolved = 0;
		solvedAtFirst = 0;
		solvedAtSecond = 0;
		solvedAtThird = 0;
	}

	public ProblemStatistics(int totalStudents, int solvingAttempts, 
			int numberOfStudentsWhoTried,
			int numberOfStudentsWhoSolved, int solvedAtFirst,
			int solvedAtSecond, int solvedAtThird) {
		super();
		this.totalStudents = totalStudents;
		this.solvingAttempts = solvingAttempts;
		this.numberOfStudentsWhoTried = numberOfStudentsWhoTried;
		this.numberOfStudentsWhoSolved = numberOfStudentsWhoSolved;
		this.solvedAtFirst = solvedAtFirst;
		this.solvedAtSecond = solvedAtSecond;
		this.solvedAtThird = solvedAtThird;
	}

	/**
	 * @return the totalStudents
	 */
	public int getTotalStudents() {
		return totalStudents;
	}

	/**
	 * @param totalStudents the totalStudents to set
	 */
	public void setTotalStudents(int totalStudents) {
		this.totalStudents = totalStudents;
	}

	/**
	 * @return the solvingAttempts
	 */
	public int getSolvingAttempts() {
		return solvingAttempts;
	}

	/**
	 * @return the numberOfStudentsWhoTried
	 */
	public int getNumberOfStudentsWhoTried() {
		return numberOfStudentsWhoTried;
	}

	/**
	 * @return the numberOfStudentsWhoSolved
	 */
	public int getNumberOfStudentsWhoSolved() {
		return numberOfStudentsWhoSolved;
	}

	/**
	 * @return the solvedAtFirst
	 */
	public int getSolvedAtFirst() {
		return solvedAtFirst;
	}

	/**
	 * @return the solvedAtSecond
	 */
	public int getSolvedAtSecond() {
		return solvedAtSecond;
	}

	/**
	 * @return the solvedAtThird
	 */
	public int getSolvedAtThird() {
		return solvedAtThird;
	}

	/**
	 * @param solvingAttempts the solvingAttempts to set
	 */
	public void setSolvingAttempts(int solvingAttempts) {
		this.solvingAttempts = solvingAttempts;
	}

	/**
	 * @param numberOfStudentsWhoTried the numberOfStudentsWhoTried to set
	 */
	public void setNumberOfStudentsWhoTried(int numberOfStudentsWhoTried) {
		this.numberOfStudentsWhoTried = numberOfStudentsWhoTried;
	}

	/**
	 * @param numberOfStudentsWhoSolved the numberOfStudentsWhoSolved to set
	 */
	public void setNumberOfStudentsWhoSolved(int numberOfStudentsWhoSolved) {
		this.numberOfStudentsWhoSolved = numberOfStudentsWhoSolved;
	}

	/**
	 * @param solvedAtFirst the solvedAtFirst to set
	 */
	public void setSolvedAtFirst(int solvedAtFirst) {
		this.solvedAtFirst = solvedAtFirst;
	}

	/**
	 * @param solvedAtSecond the solvedAtSecond to set
	 */
	public void setSolvedAtSecond(int solvedAtSecond) {
		this.solvedAtSecond = solvedAtSecond;
	}

	/**
	 * @param solvedAtThird the solvedAtThird to set
	 */
	public void setSolvedAtThird(int solvedAtThird) {
		this.solvedAtThird = solvedAtThird;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ProblemStatistics [totalStudents=" + totalStudents
				+ ", solvingAttempts=" + solvingAttempts
				+ ", numberOfStudentsWhoTried=" + numberOfStudentsWhoTried
				+ ", numberOfStudentsWhoSolved=" + numberOfStudentsWhoSolved
				+ ", solvedAtFirst=" + solvedAtFirst + ", solvedAtSecond="
				+ solvedAtSecond + ", solvedAtThird=" + solvedAtThird + "]";
	}
	
	
}
