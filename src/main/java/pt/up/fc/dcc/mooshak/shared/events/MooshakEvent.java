package pt.up.fc.dcc.mooshak.shared.events;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Common class to all update events. These events report changes generated
 * by the server to registered clients. Once in the client these events
 * are delivered to subscribing components. A typical use is the propagation
 * on changes in listings. Different types of listings require different 
 * event types.
 *  
 * @author  José Paulo Leal <zp@dcc.fc.up.pt>
 * @version 2.0
 * @since   2013-02-15
 */
public class MooshakEvent implements IsSerializable { 

	protected Date date = new Date();
	private int serial;
	protected Recipient recipient = null;

	
	/**
	 * Returns date when message was posted
	 * @return
	 */
	public Date getDate() {
		return date;
	}
	
	/**
	 * Sets date when message was posted
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Returns the name of the recipient that receives this message.
	 * If null then this message is broadcast within the contest.   
	 * @return the recipient
	 */
	public Recipient getRecipient() {
		return recipient;
	}
	
	/**
	 * Sets the name of the recipient that receives this message.
	 * If null then this message is broadcast within the contest.
	 * @param recipient the recipient to set
	 */
	public void setRecipient(Recipient recipient) {
		this.recipient = recipient;
	}

	/**
	 * Return serial number assigned to message. Serial numbers are used
	 * to locate messages in a circular queue. 
	 * @return the serial
	 */
	public int getSerial() {
		return serial;
	}

	/**
	 * Sets serial number to message. Serial numbers are used to
	 * locate messages in a circular queue. 
	 * @param serial the serial to set
	 */
	public void setSerial(int serial) {
		this.serial = serial;
	}
	
	
}
