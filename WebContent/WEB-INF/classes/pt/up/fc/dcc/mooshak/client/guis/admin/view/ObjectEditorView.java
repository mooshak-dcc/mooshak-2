package pt.up.fc.dcc.mooshak.client.guis.admin.view;

import pt.up.fc.dcc.mooshak.client.View;
import pt.up.fc.dcc.mooshak.client.data.admin.FormDataProvider;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * Object View interface
 * 
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public interface ObjectEditorView extends ValueChangeHandler<MooshakValue>, View {
	
	
	public interface Presenter {
		void onChange(String id, MooshakValue value);
		void setCopiedId(String copiedId);
		void canUndo(String id);
		void canRedo(String id);
		void onCreate(String id, String name);
		void onDestroy(String id);
		void isRenameable(String id);
		void onRename(String id, String name);
		void onImport(String id, String name, byte[] content);
		void onExport(String id);
		void onUndo(String id);
		void onRedo(String id);
		void onExecute(String id, 
				MooshakMethod command, 
				MethodContext context);
		void onFind(String term);
		void onFreeze(String id);
		void onUnfreeze(String id);
		void isFrozen(String id);
		void onCopy(String id);
		void onPaste(String id, String copiedId);
	}

	void setPresenter(Presenter presenter);
	void setObjectType(MooshakType result);
	void setObjectId(String objectId);
	void setObjectDataProvider(FormDataProvider provider);
	void showMethodResult(CommandOutcome result);
	void setMessage(String message);
	void setRenameableEnabled(boolean isRenameable);
	void setUndoEnabled(boolean value);
	void setRedoEnabled(boolean value);
	void setFreezeEnabled(boolean value);
	void setUnfreezeEnabled(boolean value);
	void setFrozenObjectType(boolean value);
	void setCopiedId(String copiedId);
	boolean getFrozen();
}
