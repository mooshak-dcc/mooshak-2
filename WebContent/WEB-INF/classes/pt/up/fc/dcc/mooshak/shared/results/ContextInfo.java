package pt.up.fc.dcc.mooshak.shared.results;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Contextual information on the current activity (e.g. contest or test)
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class ContextInfo implements IsSerializable {
	
	private String activityId;
	private String activityName;
	private String participantId;
	private String participantName; 
	private Date start;
	private Date end;
	private Date current = new Date();
	private String version;
	
	/**
	 * Get the id of an activity, such as contest or an assignment 
	 * @return
	 */
	public String getactivityId() {
		return activityId;
	}
	
	/**
	 * Set the id of an activity id, such as contest or an assignment
	 * 
	 * @param activityId
	 */
	public void setactivityId(String activityId) {
		this.activityId = activityId;
	}
	
	/**
	 * Get the name of an activity, such as contest or an assignment 
	 * @return
	 */
	public String getactivityName() {
		return activityName;
	}
	
	/**
	 * Get the name of an activity, such as contest or an assignment 
	 * @return
	 */
	public void setactivityName(String activityName) {
		this.activityName = activityName;
	}
	
	/**
	 * Get the id of a participant, such as a team or a student
	 * @return
	 */
	public String getParticipantId() {
		return participantId;
	}
	
	/**
	 * Set the id of a participant, such as a team or a student
	 * @param participantId
	 */
	public void setParticipantId(String participantId) {
		this.participantId = participantId;
	}
	
	/**
	 * Get the name of a participant, such as a team or a student
	 * @return
	 */
	public String getParticipantName() {
		return participantName;
	}
	
	/**
	 * Set the name of a participant, such as a team or a student
	 * @return
	 */
	public void setParticipantName(String participantName) {
		this.participantName = participantName;
	}
	
	/**
	 * Get start date for this activity 
	 * @return the start
	 */
	public Date getStart() {
		return start;
	}

	/**
	 * Set start date for this activity
	 * @param start the start to set
	 */
	public void setStart(Date start) {
		this.start = start;
	}

	/**
	 * Get end date for this activity
	 * @return the end
	 */
	public Date getEnd() {
		return end;
	}

	/**
	 * Set end date for this activity
	 * @param end the end to set
	 */
	public void setEnd(Date end) {
		this.end = end;
	}	
	
	/**
	 * Get current time (when this info was produced)
	 * @return the current
	 */
	public Date getCurrent() {
		return current;
	}

	/**
	 * Set current time (when this info was produced)
	 * @param current the current to set
	 */
	public void setCurrent(Date current) {
		this.current = current;
	}

	/**
	 * Get the version of Mooshak
	 * @return
	 */
	public String getVersion() {
		return version;
	}
	
	/**
	 * Set the version of Mooshak
	 * @return
	 */
	public void setVersion(String version) {
		this.version = version;
	}

}
