package pt.up.fc.dcc.mooshak.evaluation.game;

import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;

/**
 * Data collected and preserved for each match run
 */
public class MatchRun {

	private Map<String, String> players = new HashMap<>();
	private Map<String, Integer> points = new HashMap<>();
	private Map<String, Classification> classifications = new HashMap<>();
	private Map<String, String> observations = new HashMap<>();
	private String movie = null;

	public MatchRun() {
	}

	/**
	 * @return the players
	 */
	public Map<String, String> getPlayers() {
		return players;
	}

	/**
	 * @return the points
	 */
	public Map<String, Integer> getPoints() {
		return points;
	}
	
	/**
	 * Get points of a submission
	 * 
	 * @param submissionId {@link String} ID of the submission
	 * @return {@link Integer} points of the submission
	 */
	public Integer getPoints(String submissionId) {
		return points.get(submissionId);
	}

	/**
	 * @return the classifications
	 */
	public Map<String, Classification> getClassifications() {
		return classifications;
	}
	
	/**
	 * Get classification of a submission
	 * 
	 * @param submissionId {@link String} ID of the submission
	 * @return {@link Classification} classification of the submission
	 */
	public Classification getClassification(String submissionId) {
		return classifications.get(submissionId);
	}

	/**
	 * @return the observations
	 */
	public Map<String, String> getObservations() {
		return observations;
	}
	
	/**
	 * Get observations of a submission
	 * 
	 * @param submissionId {@link String} ID of the submission
	 * @return {@link String} observations of the submission
	 */
	public String getObservations(String submissionId) {
		return observations.get(submissionId);
	}

	/**
	 * @return the movie
	 */
	public String getMovie() {
		return movie;
	}

	/**
	 * @param movie
	 *            the movie to set
	 */
	public void setMovie(String movie) {
		this.movie = movie;
	}
}
