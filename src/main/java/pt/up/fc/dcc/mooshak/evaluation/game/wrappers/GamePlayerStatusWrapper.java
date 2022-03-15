package pt.up.fc.dcc.mooshak.evaluation.game.wrappers;

import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * This class wraps a game player status instance providing methods that invoke
 * methods of game player status
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class GamePlayerStatusWrapper extends GameBaseWrapper {

	public GamePlayerStatusWrapper(Object gamePlayerStatusObj) {
		super(gamePlayerStatusObj);
	}

	/**
	 * Get the points from the player status
	 * 
	 * @return the points
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public int getPoints() throws MooshakException {
		
		return (int) invoke("getPoints");
	}

	/**
	 * Get the classification from the player status
	 * 
	 * @return {@link Classification} the classification
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public Classification getClassification() throws MooshakException {
		
		String classificationLabel = (String) invoke("getClassificationLabel");
		
		if (classificationLabel == null)
			return Classification.REQUIRES_REEVALUATION;
		
		return Classification.valueOf((String) classificationLabel);
	}

	/**
	 * Get the observations from the player status
	 * 
	 * @return the observations
	 * @throws MooshakException - an error occurred while invoking the method
	 */
	public String getObservations() throws MooshakException {

		String obs = (String) invoke("getObservations");
		
		if (obs == null)
			return "";
		
		return (String) obs;
	}

}
