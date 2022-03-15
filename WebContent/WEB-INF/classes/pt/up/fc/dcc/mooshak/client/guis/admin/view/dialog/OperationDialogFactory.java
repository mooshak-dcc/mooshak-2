package pt.up.fc.dcc.mooshak.client.guis.admin.view.dialog;

import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;

/**
 * A factory of dialogs for collecting parameters of Mooshak methods 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface OperationDialogFactory {
	
	/**
	 * Return a dialog for a Mooshak method
	 * @param method for the dialog
	 * @param context for method
	 * @return
	 */
	OperationDialog<?> getOperationDialog(MooshakMethod method,
			MethodContext context);

}
