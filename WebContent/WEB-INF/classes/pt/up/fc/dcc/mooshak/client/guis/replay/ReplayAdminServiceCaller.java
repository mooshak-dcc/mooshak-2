package pt.up.fc.dcc.mooshak.client.guis.replay;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

import pt.up.fc.dcc.mooshak.client.services.AdminCommandService;
import pt.up.fc.dcc.mooshak.client.services.AdminCommandServiceAsync;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.results.ServerStatus;

public class ReplayAdminServiceCaller extends ReplayServiceCaller {
	private static AdminCommandServiceAsync adminService = 
			GWT.create(AdminCommandService.class);

	public static void getMooshakType(String type) {

		logMessage("Executing getMooshakType: " + type);
		adminService.getMooshakType(type, new AsyncCallback<MooshakType>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getMooshakType failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(MooshakType result) {
				logMessage("getMooshakType succeeded");
			}
		});
	}

	public static void getMooshakObject(String id) {

		logMessage("Executing getMooshakObject: " + id);
		adminService.getMooshakObject(id, new AsyncCallback<MooshakObject>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getMooshakObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(MooshakObject result) {
				logMessage("getMooshakObject succeeded");
			}
		});
	}

	public static void setMooshakObject(String data) {
// TODO
		logMessage("Executing setMooshakObject");
		adminService.setMooshakObject(null, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("setMooshakObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("setMooshakObject succeeded");
			}
		});
	}

	public static void canRecover(String id, String redo) {

		logMessage("Executing canRecover: " + id + " " + redo);
		adminService.canRecover(id, Boolean.parseBoolean(redo), 
				new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("canRecover failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(Boolean result) {
						logMessage("canRecover succeeded: " + result.booleanValue());
					}
				});
	}

	public static void createMooshakObject(String id,String name) {

		logMessage("Executing createMooshakObject: " + id + " " + name);
		adminService.createMooshakObject(id, name, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("createMooshakObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("createMooshakObject succeeded");
			}
		});
	}

	public static void destroyMooshakObject(String id) {

		logMessage("Executing destroyMooshakObject: " + id);
		adminService.destroyMooshakObject(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("destroyMooshakObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("destroyMooshakObject succeeded");
			}
		});
	}

	public static void renameMooshakObject(String id, String name) {

		logMessage("Executing renameMooshakObject: " + id + " " + name);
		adminService.renameMooshakObject(id, name, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("renameMooshakObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("renameMooshakObject succeeded");
			}
		});
	}

	public static void pasteMooshakObject(String id, String copiedId) {

		logMessage("Executing pasteMooshakObject: " + id + " " + copiedId);
		adminService.pasteMooshakObject(id, copiedId, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("pasteMooshakObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("pasteMooshakObject succeeded");
			}
		});
	}

	public static void recover(String id, String redo) {

		logMessage("Executing recover: " + id + " " + redo);
		adminService.recover(id, Boolean.parseBoolean(redo), new AsyncCallback<MooshakObject>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("recover failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(MooshakObject result) {
				logMessage("recover succeeded");
			}
		});
	}

	public static void freeze(String id) {

		logMessage("Executing freeze: " + id);
		adminService.freeze(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("freeze failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("freeze succeeded");
			}
		});
	}

	public static void unfreeze(String id) {

		logMessage("Executing unfreeze: " + id);
		adminService.unfreeze(id, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("unfreeze failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("unfreeze succeeded");
			}
		});
	}

	public static void isFrozen(String id) {

		logMessage("Executing isFrozen: " + id);
		adminService.isFrozen(id, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("isFrozen failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				logMessage("isFrozen succeeded: " + result.booleanValue());
			}
		});
	}

	public static void execute(String id, String method, String context) {
// TODO
		logMessage("Executing execute: " + id + " " + method + " " + context);
		adminService.execute(id, null, null, new AsyncCallback<CommandOutcome>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("execute failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(CommandOutcome result) {
				logMessage("execute succeeded");
			}
		});
	}

	public static void getMimeType(String extension) {

		logMessage("Executing getMimeType: " + extension);
		adminService.getMimeType(extension, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getMimeType failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("getMimeType succeeded: " + result);
			}
		});
	}

	public static void importMooshakObject(String id, String name, String content)  {
// TODO
		logMessage("Executing importMooshakObject: " + id + " " + name + " [content]");
		adminService.importMooshakObject(id, name, null, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("importMooshakObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("importMooshakObject succeeded");
			}
		});
	}

	public static void exportMooshakObject(String id) {

		logMessage("Executing exportMooshakObject: " + id);
		adminService.exportMooshakObject(id, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("exportMooshakObject failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(String result) {
				logMessage("exportMooshakObject succeeded: " + result);
			}
		});
	}

	public static void isRenameable(String id) {

		logMessage("Executing isRenameable: " + id);
		adminService.isRenameable(id, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("isRenameable failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				logMessage("isRenameable succeeded: " + result.booleanValue());
			}
		});
	}

	public static void switchProfile(String contest, String profile) {

		logMessage("Executing switchProfile: " + contest + " " + profile);
		adminService.switchProfile(contest, profile, new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("switchProfile failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				logMessage("switchProfile succeeded");
			}
		});
	}

	public static void findMooshakObjectIds(String term, String nameNotContent) {

		logMessage("Executing findMooshakObjectIds: " + term + " " + nameNotContent);
		adminService.findMooshakObjectIds(term, Boolean.parseBoolean(nameNotContent),
				new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable caught) {
						logMessage("findMooshakObjectIds failed: " + caught.getMessage());
					}

					@Override
					public void onSuccess(List<String> result) {
						logMessage("findMooshakObjectIds succeeded");
					}
				});
	}

	public static void getServerStatus() {

		logMessage("Executing getServerStatus");
		adminService.getServerStatus(new AsyncCallback<ServerStatus>() {

			@Override
			public void onFailure(Throwable caught) {
				logMessage("getServerStatus failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(ServerStatus result) {
				logMessage("getServerStatus succeeded");
			}
		});
	}


}
