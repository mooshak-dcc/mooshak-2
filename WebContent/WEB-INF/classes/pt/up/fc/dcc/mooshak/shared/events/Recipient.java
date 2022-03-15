package pt.up.fc.dcc.mooshak.shared.events;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Recipient of an event. There are 2 kind of recipients:
 *  just with used id, to broadcast to all session of given user;
 *  with both user and session id, to send events to a specific session.
 *
 * The equals method is tweaked to match recipients is session id is null 
 * in any of the instances.  
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class Recipient implements IsSerializable {
	private String userId;
	private String sessionId;

	public Recipient() {}
	
	/**
	 * Recipient just with user id. Events with this kind of recipient are sent
	 * to all sessions of given user   
	 * @param userId
	 */
	public Recipient(String userId) {
		super();
		this.userId = userId;
	}
	
	/**
	 * Recipient with user and id. Events with this kind of recipient are sent
	 * to specific sessions of a user  
	 * @param userId
	 * @param sessionId
	 */
	public Recipient(String userId, String sessionId) {
		super();
		this.userId = userId;
		this.sessionId = sessionId;
	}

	/**
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}
	/**
	 * @param userId the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}
	/**
	 * @return the sessionId
	 */
	public String getSessionId() {
		return sessionId;
	}
	/**
	 * @param sessionId the sessionId to set
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Recipient other = (Recipient) obj;
		// changed this part !! null session IDs match with anything
		if (sessionId != null && other.sessionId != null &&
			!sessionId.equals(other.sessionId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Recipient [userId=" + userId + ", sessionId=" + sessionId + "]";
	}

	
}
