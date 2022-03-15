package pt.up.fc.dcc.mooshak.shared.results.sequencing;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Student profile inside a course
 * 
 * @author josepaiva
 */
public class StudentProfile implements IsSerializable {

	private String studentName;
	private Date registrationDate;
	private Integer solvedExercises;
	private Integer unsolvedExercises;
	private Integer videoResourcesSeen;
	private Integer staticResourcesSeen;
	private Integer submissions;
	private Integer acceptedSubmissions;
	private String currentPart;
	
	public StudentProfile() {
		super();
	}

	/**
	 * @return the studentName
	 */
	public String getStudentName() {
		return studentName;
	}

	/**
	 * @param studentName the studentName to set
	 */
	public void setStudentName(String studentName) {
		this.studentName = studentName;
	}

	/**
	 * @return the registrationDate
	 */
	public Date getRegistrationDate() {
		return registrationDate;
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		this.registrationDate = registrationDate;
	}

	/**
	 * @return the solvedExercises
	 */
	public int getSolvedExercises() {
		return solvedExercises == null ? 0 : solvedExercises;
	}

	/**
	 * @param solvedExercises the solvedExercises to set
	 */
	public void setSolvedExercises(Integer solvedExercises) {
		this.solvedExercises = solvedExercises;
	}

	/**
	 * @return the unsolvedExercises
	 */
	public int getUnsolvedExercises() {
		return unsolvedExercises == null ? 0 : unsolvedExercises;
	}

	/**
	 * @param unsolvedExercises the unsolvedExercises to set
	 */
	public void setUnsolvedExercises(Integer unsolvedExercises) {
		this.unsolvedExercises = unsolvedExercises;
	}

	/**
	 * @return the videoResourcesSeen
	 */
	public int getVideoResourcesSeen() {
		return videoResourcesSeen == null ? 0 : videoResourcesSeen;
	}

	/**
	 * @param videoResourcesSeen the videoResourcesSeen to set
	 */
	public void setVideoResourcesSeen(Integer videoResourcesSeen) {
		this.videoResourcesSeen = videoResourcesSeen;
	}

	/**
	 * @return the staticResourcesSeen
	 */
	public int getStaticResourcesSeen() {
		return staticResourcesSeen == null ? 0 : staticResourcesSeen;
	}

	/**
	 * @param staticResourcesSeen the staticResourcesSeen to set
	 */
	public void setStaticResourcesSeen(Integer staticResourcesSeen) {
		this.staticResourcesSeen = staticResourcesSeen;
	}

	/**
	 * @return the submissions
	 */
	public int getSubmissions() {
		return submissions == null ? 0 : submissions;
	}

	/**
	 * @param submissions the submissions to set
	 */
	public void setSubmissions(Integer submissions) {
		this.submissions = submissions;
	}

	/**
	 * @return the acceptedSubmissions
	 */
	public int getAcceptedSubmissions() {
		return acceptedSubmissions == null ? 0 : acceptedSubmissions;
	}

	/**
	 * @param acceptedSubmissions the acceptedSubmissions to set
	 */
	public void setAcceptedSubmissions(Integer acceptedSubmissions) {
		this.acceptedSubmissions = acceptedSubmissions;
	}

	/**
	 * @return the currentPart
	 */
	public String getCurrentPart() {
		return currentPart;
	}

	/**
	 * @param currentPart the currentPart to set
	 */
	public void setCurrentPart(String currentPart) {
		this.currentPart = currentPart;
	}

	

}
