package pt.up.fc.dcc.mooshak.client.services;

import java.util.List;

import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;
import pt.up.fc.dcc.mooshak.shared.results.ServerStatus;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Asynchronous service interface for a admin commands
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public interface AdminCommandServiceAsync {

	void getMooshakType(String type, AsyncCallback<MooshakType> callback);

	void getMooshakObject(String id, AsyncCallback<MooshakObject> callback);
	
	void setMooshakObject(MooshakObject data, AsyncCallback<Void> callback);

	void execute(String id, MooshakMethod method, MethodContext context,
			AsyncCallback<CommandOutcome> callback);

	void createMooshakObject(String id, String name,
			AsyncCallback<Void> callback);

	void destroyMooshakObject(String id, AsyncCallback<Void> callback);

	void recover(String id, boolean redo, AsyncCallback<MooshakObject> callback);

	void canRecover(String id, boolean redo, AsyncCallback<Boolean> callback);

	void freeze(String id, AsyncCallback<Void> callback);

	void isFrozen(String id, AsyncCallback<Boolean> callback);

	void unfreeze(String id, AsyncCallback<Void> callback);

	void getMimeType(String extension, AsyncCallback<String> callback);

	void exportMooshakObject(String id, AsyncCallback<String> callback);

	void importMooshakObject(String id, String name, byte[] content,
			AsyncCallback<Void> callback);

	void pasteMooshakObject(String id, String copiedId, AsyncCallback<Void> callback);

	void renameMooshakObject(String id, String name, AsyncCallback<Void> asyncCallback);

	void isRenameable(String id, AsyncCallback<Boolean> callback);

	void switchProfile(String contest, String profile, AsyncCallback<Void> callback);

	void getServerStatus(AsyncCallback<ServerStatus> callback);

	void findMooshakObjectIds(String term, boolean nameNotContent,
			AsyncCallback<List<String>> callback);

	void removeFile(String objectId, String field, String fileName, 
			AsyncCallback<Void> callback);
}
