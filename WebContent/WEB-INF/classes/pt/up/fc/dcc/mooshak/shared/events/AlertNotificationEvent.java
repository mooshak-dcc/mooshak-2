package pt.up.fc.dcc.mooshak.shared.events;

/**
 * Event with a message that should be displayed using an alert dialog
 * on the client. This type of events is used for notifying teams/students
 * of accepted problems, answered questions, judge warnings and other similar
 * situations. 
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class AlertNotificationEvent extends MooshakEvent {

	private String message;
	
	public AlertNotificationEvent() {
		super();
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	
}
