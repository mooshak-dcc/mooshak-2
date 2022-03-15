package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import java.util.HashSet;
import java.util.Set;

import pt.up.fc.dcc.mooshak.client.widgets.OkCancelDialog;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

import com.google.gwt.user.client.ui.Widget;

/**
 * Dialogs for collecting parameters used by Mooshak operations.
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */

public class OperationDialog<T extends Widget & DialogContent> 
	extends OkCancelDialog {

	private Set<DialogSubmitHandler> handlers = 
			new HashSet<DialogSubmitHandler>();
	
	DialogContent content = null;
	
	OperationDialog(T content) {
		super(content);
		this.content = content;
	}

	/**
	 * Add an handler of this dialog submissions.
	 * It will be notified with {@code onSubmit(SubmissionEvent)}
	 * @param handler
	 */
	public void addDialogSubmitHandler(final DialogSubmitHandler handler) {
		
		handlers.add(handler);	
	}
	
	/**
	 * Fire an event to submit this dialog
	 */
	@Override
	protected void fireSubmit() {
		DialogSubmitEvent event=new DialogSubmitEvent(content.getContext());
		
		for(DialogSubmitHandler handler: handlers)
			handler.onSubmit(event);
	}

	public void setContext(MethodContext context) {
		content.setContext(context);
	}


}
