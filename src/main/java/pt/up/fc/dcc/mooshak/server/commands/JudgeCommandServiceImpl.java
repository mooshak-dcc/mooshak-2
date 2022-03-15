package pt.up.fc.dcc.mooshak.server.commands;

import java.io.File;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.services.JudgeCommandService;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;

public class JudgeCommandServiceImpl extends CommandService implements
		JudgeCommandService {

	private static final long serialVersionUID = 1L;

	@Override
	public Map<String, String> getSubmissionReports(String id) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);

		id = sanitizePathId(id);
		Map<String, String> reports = reviewerManager.getSubmissionReports(session, id);
		auditLog("getSubmissionReports", id);
		return reports;
	}

	@Override
	public byte[] getProgramCode(String submissionId) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);

		byte[] program = reviewerManager.getSubmissionProgram(
				sanitizePathSegment(submissionId), session);
		auditLog("getProgramCode", submissionId);
		return program;
	}

	@Override
	public void reevaluate(String programName, byte[] programCode,
			String problemId, String submissionId) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		reviewerManager.reevaluate(session, sanitizePathSegment(programName), 
				sanitizePathSegment(problemId), sanitizePathSegment(submissionId));

		auditLog("reevaluate", programName, problemId, submissionId);
	}

	@Override
	public void comment(String problemId, String subject, String comment)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		if(problemId != null)
			problemId    = sanitizePathSegment(problemId);
		
		reviewerManager.commentProblem(session, problemId, subject, comment);

		auditLog("comment", problemId, subject, comment);
	}

	@Override
	public String getFileSeparator() throws MooshakException {
		return File.separator;
	}

	
	@Override
	public void broadcastRowChange(String type, String id) 
			throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, RUNNER_PROFILE_ID,JUDGE_PROFILE_ID);
		
		reviewerManager.broadcastRowChange(session, type, sanitizePathId(id));

		auditLog("broadcastRowChange", type, id);
	}

	@Override
	public void createBalloon(String objectId) throws MooshakException {
		authorized(RUNNER_PROFILE_ID,JUDGE_PROFILE_ID);
		reviewerManager.createBalloon(getSession(), objectId);

		auditLog("createBalloon", objectId);
	}
	
	@Override
	public void sendAlertNotificationEvent(String objectId, String message) 
			throws MooshakException {
		authorized(RUNNER_PROFILE_ID,JUDGE_PROFILE_ID);
		
		reviewerManager.sendAlertNotification(getSession().getContestId(), 
				objectId != null ? sanitizePathId(objectId) : null, message);

		auditLog("sendAlertNotificationEvent", objectId, message);
	}
	
	@Override
	public List<SelectableOption> getQuestionsSubjectList(String questionId) 
			throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);

		List<SelectableOption> questions = 
				reviewerManager.getQuestionsSubjectList(session,questionId);
		auditLog("getQuestionsSubjectList", questionId);
		
		return questions;
	}

	@Override
	public String getProblemId(String submissionId) throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		String problemId = reviewerManager.getProblemIdAssociatedToSubmission(session,
				submissionId);
		auditLog("getProblemId", submissionId);
		return problemId;
	}

	@Override
	public void updateSubmissionResult(String submissionId, String oldResult, String newResult)
			throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		reviewerManager.updateSubmissionResult(session, submissionId, oldResult, newResult);
		
		auditLog("updateSubmissionResult", submissionId, oldResult, newResult);
	}
	
	@Override
	public boolean isPrintoutsListPending() throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		boolean listPending = reviewerManager.isPrintoutsListPending(session);
		
		auditLog("isPrintoutsListPending");
		
		return listPending;
	}
	
	@Override
	public boolean isBalloonsListPending() throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		boolean listPending = reviewerManager.isBalloonsListPending(session);
		
		auditLog("isBalloonsListPending");
		
		return listPending;
	}

	@Override
	public List<String> getObtainedOutputs(String submissionId) throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		List<String> outputs = reviewerManager.getObtainedOutputs(session, submissionId);
		
		auditLog("getObtainedOutput", submissionId);
		
		return outputs;
	}

	@Override
	public List<String> getExpectedOutputs(String submissionId) throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		List<String> outputs = reviewerManager.getExpectedOutputs(session, submissionId);
		
		auditLog("getExpectedOutput", submissionId);
		
		return outputs;
	}

	@Override
	public Map<String, String> getProblemTestCases(String problemId)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);
		
		Map<String, String> tests = reviewerManager.getProblemTestCases(session, problemId);
		auditLog("getProblemTestCases",problemId);
		return tests;
	}

	@Override
	public void validateSubmission(String submissionId, byte[] programCode, String problemId, 
			List<String> inputs) 
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);

		reviewerManager.validateSubmission(session, submissionId, programCode, problemId, 
				inputs);
		auditLog("validateSubmission",submissionId, problemId);
	}

	@Override
	public EvaluationSummary getValidationSummary(String submissionId)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, JUDGE_PROFILE_ID);

		EvaluationSummary report = reviewerManager.getValidationSummary(session, 
				submissionId);
		auditLog("getValidationSummary",submissionId);
		return report;
	}

}
