package pt.up.fc.dcc.mooshak.server.commands;

import java.util.List;

import pt.up.fc.dcc.mooshak.client.services.AdminCommandService;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.server.Configurator;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;
import pt.up.fc.dcc.mooshak.shared.results.ServerStatus;

/**
 * Administration Commands Service Implementation
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class AdminCommandServiceImpl extends CommandService 
	implements AdminCommandService {
	
	private static final long serialVersionUID = 1L;

	@Override
	public MooshakObject getMooshakObject(String id) throws MooshakException {
				
		authorized(ADMIN_PROFILE_ID, JUDGE_PROFILE_ID, CREATOR_PROFILE_ID,
				RUNNER_PROFILE_ID);
		
		MooshakObject object = administratorManager.getMooshakObject(
				sanitizePathId(id));
		auditLog("getMooshakObject",id);
		return object;
	}


	@Override
	public MooshakType getMooshakType(String type) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID, JUDGE_PROFILE_ID, CREATOR_PROFILE_ID);
		
		MooshakType mooshakType = administratorManager.getMooshakType(
				sanitizePathSegment(type)); 
		auditLog("getMooshakType",type);
		
		return mooshakType;
	}


	@Override
	public void setMooshakObject(MooshakObject data) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID, JUDGE_PROFILE_ID, CREATOR_PROFILE_ID,
				RUNNER_PROFILE_ID);
		
		data.setId(sanitizePathId(data.getId()));
		data.setType(sanitizePathSegment(data.getType()));
		
		administratorManager.setMooshakObject(data);
		
		auditLog("setMooshakObject");
	}


	@Override
	public CommandOutcome execute(String id, MooshakMethod method,
			MethodContext context) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID, JUDGE_PROFILE_ID, CREATOR_PROFILE_ID);

		CommandOutcome outcome = administratorManager.execute(
				getSession(),
				sanitizePathId(id),
				method,
				context);
		
		auditLog("execute",id,method.getName()
				,context == null? "" : context.toString());
		return outcome;
	}


	@Override
	public boolean canRecover(String id, boolean redo) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID);

		boolean canRecover = 
				administratorManager.canRecover(sanitizePathId(id), redo);
		auditLog("canRecover",id,Boolean.toString(redo));
		
		return canRecover;
	}


	@Override
	public MooshakObject recover(String id, boolean redo) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID);

		MooshakObject recover = 
				administratorManager.recover(sanitizePathId(id), redo);
		auditLog("recover",id,Boolean.toString(redo));
		
		return recover;
	}


	@Override
	public void createMooshakObject(String id, String name)
			throws MooshakException {
		authorized(ADMIN_PROFILE_ID, JUDGE_PROFILE_ID, CREATOR_PROFILE_ID);
		administratorManager.createMooshakObject(
				sanitizePathId(id),
				sanitizePathSegment(name));
		
		auditLog("createMooshakObject",id,name);
	}


	@Override
	public void destroyMooshakObject(String id) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID, CREATOR_PROFILE_ID);
		administratorManager.destroyMooshakObject(sanitizePathId(id));
		
		auditLog("destroyMooshakObject",id);
	}


	@Override
	public void importMooshakObject(String id, String name, byte[] content)
			throws MooshakException {
		authorized(ADMIN_PROFILE_ID);
		administratorManager.importMooshakObject(id, name, content);
		
		auditLog("importMooshakObject", id, name);
	}


	@Override
	public String exportMooshakObject(String id) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID);
		String objectId = administratorManager.exportMooshakObject(sanitizePathId(id));
		auditLog("exportMooshakObject", id);
		return objectId;
	}


	@Override
	public void freeze(String id) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID);
		administratorManager.freeze(sanitizePathId(id));
		
		auditLog("freeze",id);
	}


	@Override
	public void unfreeze(String id) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID);
		administratorManager.unfreeze(sanitizePathId(id));
		
		auditLog("unfreeze",id);
	}


	@Override
	public boolean isFrozen(String id) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID);
		
		boolean frozen = administratorManager.isFrozen(sanitizePathId(id));
		
		auditLog("isFrozen",id);
		return frozen;
	}


	@Override
	public String getMimeType(String extension) throws MooshakException {
		String mime = Configurator.getMime(extension);
		
		auditLog("getMimeType",extension);
		return mime;
	}


	@Override
	public void pasteMooshakObject(String id, String copiedId) throws MooshakException {
		authorized(ADMIN_PROFILE_ID);
		administratorManager.pasteMooshakObject(
				sanitizePathId(id),
				sanitizePathId(copiedId));
		
		auditLog("pasteMooshakObject",id,copiedId);
	}


	@Override
	public void renameMooshakObject(String id, String name) throws MooshakException {
		authorized(ADMIN_PROFILE_ID);
		administratorManager.renameMooshakObject(
				sanitizePathId(id),
				sanitizePathSegment(name));
		
		auditLog("renameMooshakObject",id,name);
	}



	@Override
	public boolean isRenameable(String id) throws MooshakException {
		
		authorized(ADMIN_PROFILE_ID);
		
		boolean isRenameable = administratorManager.isRenameable(id);
		
		auditLog("isRenameable",id);
		return isRenameable;
	}


	@Override
	public void switchProfile(String contest, String profile) throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID);
		
		administratorManager.switchProfile(session, contest, profile);

		auditLog("switchProfile", contest, profile);
	}

	@Override
	public List<String> findMooshakObjectIds(String term, boolean nameNotContent)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID);
		
		List<String> ids = administratorManager.searchFor(term, nameNotContent);
		
		auditLog("findMooshakObjectIds",term,Boolean.toString(nameNotContent));
		
		return ids;
	}

	@Override
	public ServerStatus getServerStatus() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID);
		
		ServerStatus serverStatus = administratorManager.getServerStatus();
		
		auditLog("getServerStatus");
		
		return serverStatus;
	}

	@Override
	public void removeFile(String objectId, String field, String fileName) 
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, ADMIN_PROFILE_ID);
		
		administratorManager.removeFile(session, objectId, field, fileName);
		
		auditLog("removeFile", objectId, field, fileName);
	}
	
}
