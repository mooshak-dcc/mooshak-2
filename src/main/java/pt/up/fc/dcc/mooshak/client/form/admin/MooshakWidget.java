package pt.up.fc.dcc.mooshak.client.form.admin;

import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.user.client.ui.HasValue;

/**
 * Common type of all widgets used in Mooshak forms. 
 * 
 * Currently it just requires HasValue<String> 
 * but other methods may be added in the future.
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface MooshakWidget extends HasValue<MooshakValue> {

	/**
	 * True when has focus, false otherwise
	 * @return
	 */
	boolean isEditing();
	
}
