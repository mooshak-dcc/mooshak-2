package pt.up.fc.dcc.mooshak.shared.events;

import java.util.Date;

import pt.up.fc.dcc.mooshak.shared.results.sequencing.StudentProfile;

public class StudentProfileUpdateEvent extends MooshakEvent {
	
	private StudentProfile profile = new StudentProfile();
	
	public StudentProfileUpdateEvent() {
		super();
	}

	/**
	 * @return the studentName
	 */
	public String getStudentName() {
		return profile.getStudentName();
	}

	/**
	 * @param studentName the studentName to set
	 */
	public void setStudentName(String studentName) {
		profile.setStudentName(studentName);
	}

	/**
	 * @return the registrationDate
	 */
	public Date getRegistrationDate() {
		return profile.getRegistrationDate();
	}

	/**
	 * @param registrationDate the registrationDate to set
	 */
	public void setRegistrationDate(Date registrationDate) {
		profile.setRegistrationDate(registrationDate);
	}

	/**
	 * @return the solvedExercises
	 */
	public Integer getSolvedExercises() {
		return profile.getSolvedExercises();
	}

	/**
	 * @param solvedExercises the solvedExercises to set
	 */
	public void setSolvedExercises(Integer solvedExercises) {
		profile.setSolvedExercises(solvedExercises);
	}

	/**
	 * @return the unsolvedExercises
	 */
	public Integer getUnsolvedExercises() {
		return profile.getUnsolvedExercises();
	}

	/**
	 * @param unsolvedExercises the unsolvedExercises to set
	 */
	public void setUnsolvedExercises(Integer unsolvedExercises) {
		profile.setUnsolvedExercises(unsolvedExercises);
	}

	/**
	 * @return the videoResourcesSeen
	 */
	public Integer getVideoResourcesSeen() {
		return profile.getVideoResourcesSeen();
	}

	/**
	 * @param videoResourcesSeen the videoResourcesSeen to set
	 */
	public void setVideoResourcesSeen(Integer videoResourcesSeen) {
		profile.setVideoResourcesSeen(videoResourcesSeen);
	}

	/**
	 * @return the staticResourcesSeen
	 */
	public Integer getStaticResourcesSeen() {
		return profile.getStaticResourcesSeen();
	}

	/**
	 * @param staticResourcesSeen the staticResourcesSeen to set
	 */
	public void setStaticResourcesSeen(Integer staticResourcesSeen) {
		profile.setStaticResourcesSeen(staticResourcesSeen);
	}

	/**
	 * @return the submissions
	 */
	public Integer getSubmissions() {
		return profile.getSubmissions();
	}

	/**
	 * @param submissions the submissions to set
	 */
	public void setSubmissions(Integer submissions) {
		profile.setSubmissions(submissions);
	}

	/**
	 * @return the acceptedSubmissions
	 */
	public Integer getAcceptedSubmissions() {
		return profile.getAcceptedSubmissions();
	}

	/**
	 * @param acceptedSubmissions the acceptedSubmissions to set
	 */
	public void setAcceptedSubmissions(Integer acceptedSubmissions) {
		profile.setAcceptedSubmissions(acceptedSubmissions);
	}

	/**
	 * @return the currentPart
	 */
	public String getCurrentPart() {
		return profile.getCurrentPart();
	}

	/**
	 * @param currentPart the currentPart to set
	 */
	public void setCurrentPart(String currentPart) {
		profile.setCurrentPart(currentPart);
	}

}
