package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

/**
 * Handler of dialog submit events, high level event thrown when 
 * submitting a dialog. The event includes the parameters that are submitted.
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface DialogSubmitHandler {
	
	/**
	 * A dialog request was initiated with the given data
	 * @param event
	 */
	void onSubmit(DialogSubmitEvent event);

}
