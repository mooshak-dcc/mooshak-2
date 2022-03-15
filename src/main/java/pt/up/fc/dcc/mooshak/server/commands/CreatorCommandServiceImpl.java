package pt.up.fc.dcc.mooshak.server.commands;

import java.io.File;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.services.CreatorCommandService;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;

public class CreatorCommandServiceImpl extends CommandService implements
		CreatorCommandService {

	private static final long serialVersionUID = 1L;

	@Override
	public String getProblemsPath() throws MooshakException {
		Session session = getSession();
		
		authManager.autorize(session, CREATOR_PROFILE_ID);

		Contest contest = session.getContest();
		Problems problems = contest.open("problems");
		String pathname = problems.getPath().toString() + File.separator;
		auditLog("getProblemsPath");
		return pathname;
	}

	@Override
	public String getTestsPath() {
		auditLog("getTestsPath");
		return File.separator + "tests";
	}

	@Override
	public String getSkeletonsPath() {
		auditLog("getSkeletonsPath");
		return File.separator + "skeletons";
	}

	@Override
	public String getImagesPath() {
		auditLog("getImagesPath");
		return File.separator + "images";
	}

	@Override
	public String getSolutionsPath() {
		auditLog("getSolutionsPath");
		return File.separator + "solutions";
	}

	@Override
	public Map<String, List<String>> getOptionsValues() throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID);
		
		Map<String, List<String>> values = 
				administratorManager.getProblemOptionsValues();
		auditLog("getOptionsValues");
		return values;
	}
	
	@Override
	public String checkLanguages() throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID);
		
		String errors = 
				administratorManager.checkLanguages(session);
		auditLog("checkLanguages");
		return errors;
	}

	@Override
	public String createNewDefaultProblem(String objectId)
			throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID);

		String id = administratorManager.createNewDefaultProblem(session, 
				sanitizePathId(objectId));
		auditLog("createNewDefaultProblem",objectId);
		return id;
	}

	@Override
	public MooshakValue uploadFile(String objectId, byte[] content,
			String name, String field) throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID);
		
		MooshakValue value =  administratorManager.uploadFile(
				session, 
				sanitizePathId(objectId), 
				content, 
				sanitizePathSegment(name), 
				null,
				field);
		auditLog("uploadFile");
		return value;
	}
	
	@Override
	public void deleteProblem(String id) throws MooshakException {
		administratorManager.destroyMooshakObject(id);
		auditLog("deleteProblem", id);
	}

	@Override
	public MooshakObject addNewDefaultTest(String objectId)
			throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID);
		
		MooshakObject object = administratorManager
				.addNewDefaultTest(sanitizePathId(objectId));
		
		auditLog("addNewDefaultTest", objectId);
		return object;
	}

	@Override
	public void deleteTest(String id) throws MooshakException {
		administratorManager.destroyMooshakObject(id);
		auditLog("deleteTest", id);
	}


	@Override
	public MooshakValue addNewDefaultFile(String problemId, String field)
			throws MooshakException {
		
		authorized(CREATOR_PROFILE_ID);
		
		MooshakValue value = new MooshakValue(field, field, 
				"".getBytes());
		MooshakObject problem = administratorManager
				.getMooshakObject(sanitizePathId(problemId));
		
		problem.setFieldValue(field, value);
		administratorManager.setMooshakObject(problem);
		
		auditLog("addNewDefaultFile",problemId,field);
		return value;
	}

	@Override
	public void updateTestsResults(String problemId) throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID);

		administratorManager.updateTestsResult(session, sanitizePathId(problemId));
		
		auditLog("updateTestsResults",problemId);
	}

	@Override
	public MooshakObject addNewDefaultSkeleton(String objectId)
			throws MooshakException {

		MooshakObject mooshakObject = administratorManager
				.addNewSkeleton(sanitizePathId(objectId));
		auditLog("addNewDefaultSkeleton",objectId);
		return mooshakObject;
	}

	@Override
	public void deleteSkeleton(String id) throws MooshakException {
		administratorManager.destroyMooshakObject(sanitizePathId(id));
	}

	@Override
	public MooshakObject addNewDefaultSolution(String objectId)
			throws MooshakException {
		
		MooshakObject object = administratorManager
				.addNewSolution(sanitizePathId(objectId));
		auditLog("addNewDefaultSolution", objectId);
		return object;
	}

	@Override
	public void changeProgramType(String problemId, 
			String objectId, MooshakValue value,
			String newType) throws MooshakException {
		authorized(CREATOR_PROFILE_ID);
		
		administratorManager.changeProgramType(problemId, 
				objectId, value, newType);
		auditLog("changeProgramType",problemId,objectId,newType);
	}

	@Override
	public void removeFileFromObject(String objectId, String name)
			throws MooshakException {
		authorized(CREATOR_PROFILE_ID);
		
		administratorManager.removeFileFromObject(objectId, name);
		auditLog("removeFileFromObject",objectId,name);
	}

	@Override
	public void removeSolution(String problemId, String name)
			throws MooshakException {
		authorized(CREATOR_PROFILE_ID);
		
		administratorManager.removeSolution(getSession(),sanitizePathId(problemId),
				sanitizePathSegment(name));
		auditLog("removeSolution",sanitizePathId(problemId),
				sanitizePathSegment(name));
	}

	@Override
	public void removeSkeleton(String problemId, String name)
			throws MooshakException {
		authorized(CREATOR_PROFILE_ID);
		
		administratorManager.removeSkeleton(getSession(),sanitizePathId(problemId),
				sanitizePathSegment(name));
		auditLog("removeSkeleton",sanitizePathId(problemId),
				sanitizePathSegment(name));
	}

	@Override
	public void removeFile(String problemId, String field, String name)
			throws MooshakException {
		authorized(CREATOR_PROFILE_ID);
		
		administratorManager.removeFile(getSession(),sanitizePathId(problemId),
				field, sanitizePathSegment(name));
		auditLog("removeFile",sanitizePathId(problemId), field,
				sanitizePathSegment(name));
	}

	@Override
	public MooshakValue getSolutionsValue(String problemId) 
			throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, CREATOR_PROFILE_ID);
		
		MooshakValue value = 
				administratorManager.getSolutionsValue(session, problemId);
		auditLog("getSolutionsValue",problemId);
		return value;
	}

}
