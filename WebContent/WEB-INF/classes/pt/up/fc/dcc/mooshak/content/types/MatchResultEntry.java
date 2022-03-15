package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * {@link MatchResultEntry} contains the score awarded by a submission in a
 * {@link Match}
 * 
 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
 */
public class MatchResultEntry extends PersistentObject {
	private static final long serialVersionUID = 1L;

	@MooshakAttribute(name = "Fatal", type = AttributeType.LABEL)
	private String fatal;

	@MooshakAttribute(name = "Warning", type = AttributeType.LABEL)
	private String warning;

	@MooshakAttribute(
			name="Submission",	
			type=AttributeType.PATH,
			refType = "Submission",
			complement="../../../../../../submissions")
	private Path submission = null;
	
	@MooshakAttribute(
			name="Points",
			type=AttributeType.FLOAT)
	private Float points;
	
	@MooshakAttribute(
			name="Position",
			type=AttributeType.INTEGER)
	private Integer position;
	
	@MooshakAttribute(
			name="Qualified",
			type=AttributeType.MENU)
	private MooshakAttribute.YesNo qualified = null;

	
	/*-------------------------------------------------------------------*\
	 * 		            Setters and getters                              *
	\*-------------------------------------------------------------------*/

	/**
	 * Fatal errors messages of this folder
	 * 
	 * @return the fatal
	 */
	public String getFatal() {
		if (fatal == null)
			return "";
		else
			return fatal;
	}

	/**
	 * Set fatal error messages
	 * 
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	/**
	 * Warning errors messages of this folder
	 * 
	 * @return the warning
	 */
	public String getWarning() {
		if (warning == null)
			return "";
		else
			return warning;
	}

	/**
	 * Set warning error messages
	 * 
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}
	
	/**
	 * @return the points
	 */
	public float getPoints() {
		if(points == null)
			return 0;
		else
			return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(float points) {
		this.points = points;
	}

	/**
	 * @return the position
	 */
	public Integer getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(Integer position) {
		this.position = position;
	}

	/**
	 * Gets the submission that this {@link MatchResultEntry} refers to
	 * 
	 * @return submission
	 * @throws MooshakContentException
	 */
	public Submission getSubmission() throws MooshakContentException {
		if(submission == null)
			return null;
		else 
			return openRelative("Submission", Submission.class);
	}
	
	/**
	 * Change the submission that this {@link MatchResultEntry} refers to
	 * 
	 * @param submission
	 */
	public void setSubmission(Submission submission) {
		if(submission == null)
			this.submission = null;
		else
			this.submission = submission.getId();
	}

	/**
	 * Gets the player id that this {@link MatchResultEntry} refers to
	 * 
	 * @return player id
	 * @throws MooshakContentException
	 */
	public String getPlayerId() throws MooshakContentException {
		
		Submission submission = getSubmission();
		if(submission == null)
			return null;
		else 
			return submission.getTeamId();
	}
		
	/**
	 * Has this player qualified to the next round/stage
	 * 
	 * @return {@code boolean} <code>true</code> if player qualified, 
	 * 		<code>false</code> otherwise
	 */
	public boolean hasQualified() {
		
		if(YesNo.NO.equals(qualified))
			return false;
		else
			return true;		
	}
	
	/**
	 * Set if this player has qualified to the next round/stage
	 * 
	 * @return <code>true</code> if player qualified, 
	 * 		<code>false</code> otherwise
	 */
	public void setQualified(boolean qualified) {
		if(qualified)
			this.qualified = YesNo.YES;
		else
			this.qualified = YesNo.NO;
	}
	
	/**
	 * Build {@link MatchResultEntry} json
	 * 
	 * @return {@link MatchResultEntry} json
	 * @throws MooshakException 
	 */
	public JsonObject buildMatchResultEntryJson() throws MooshakException {
		
		Submission submission = getSubmission();
		if(submission == null)
			throw new MooshakException("Submission is not defined.");
			
		JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();

		jsonObjectBuilder.add("player_id", submission.getIdName());
		jsonObjectBuilder.add("points", getPoints());
		jsonObjectBuilder.add("qualified", hasQualified());
		
		return jsonObjectBuilder.build();
	}
	
}
