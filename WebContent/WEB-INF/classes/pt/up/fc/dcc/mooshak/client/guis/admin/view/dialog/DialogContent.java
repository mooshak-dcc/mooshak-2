package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

/**
 * Type of a widget usable as a dialog content 
 * for collecting operation parameters
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface DialogContent {

	
	/**
	 * Return context collected by this operation
	 * @return
	 */
	public MethodContext getContext();


	/**
	 * Assign context to this operation
	 * @param context
	 */
	public void setContext(MethodContext context);
	
	
	/**
	 * get width to set on dialog box 
	 */
	public String getWidth();
	
	/**
	 * get width to set on dialog box 
	 */
	public String getHeight();

}
