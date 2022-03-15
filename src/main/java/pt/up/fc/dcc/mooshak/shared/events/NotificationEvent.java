package pt.up.fc.dcc.mooshak.shared.events;

public class NotificationEvent extends MooshakEvent {

	private String text;
	
	public NotificationEvent() {
		super();
	}
	
	public NotificationEvent(Recipient recipient, String text) {
		this.recipient = recipient;
		this.text = text;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}

}
