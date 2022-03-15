package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * High level event thrown when submitting a dialog.
 * The event includes the parameters that are submitted
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class DialogSubmitEvent {
	
	private MethodContext context;
	
	DialogSubmitEvent(MethodContext context) {
		this.context = context;
	}

	/**
	 * Get submitted context
	 * @return the context
	 */
	public MethodContext getContext() {
		return context;
	}

	/**
	 * Set context to submit 
	 * @param context the context to set
	 */
	public void setContext(MethodContext context) {
		this.context = context;
	}

	

}
