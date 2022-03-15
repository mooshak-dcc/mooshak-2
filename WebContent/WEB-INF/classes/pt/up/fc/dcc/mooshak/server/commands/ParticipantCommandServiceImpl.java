package pt.up.fc.dcc.mooshak.server.commands;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import pt.up.fc.dcc.mooshak.client.services.ParticipantCommandService;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Questions;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo.ContestStatus;
import pt.up.fc.dcc.mooshak.shared.commands.SelectableOption;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ProblemInfo;

/**
 * Team Commands Service Implementation
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class ParticipantCommandServiceImpl extends CommandService implements
		ParticipantCommandService {

	private static final long serialVersionUID = 1L;

	@Override
	public List<SelectableOption> getProblems() throws MooshakException {
		List<SelectableOption> problems = new ArrayList<>();

		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, 
				JUDGE_PROFILE_ID, CREATOR_PROFILE_ID);

		Map<String, String> problemMap = participantManager
				.getProblems(session);

		for (String key : problemMap.keySet())
			problems.add(new SelectableOption(key, problemMap.get(key)));
		auditLog("getProblems");
		return problems;
	}

	@Override
	public ProblemInfo view(String problemId,boolean complete) 
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, JUDGE_PROFILE_ID);
		
		Contest contest = session.getContest();
		if (contest.getContestStatus().equals(ContestStatus.READY)) {
			return null;
		}

		problemId = sanitizePathSegment(problemId);

		Problem problem = participantManager.getProblem(session, problemId);
		ProblemInfo info = new ProblemInfo();
		
		try {
			info.setId(problemId);
			info.setTitle(problem.getTitle());
			if(problem.getName() == null)
				info.setLabel(problemId);
			else
				info.setLabel(problem.getName().toString());
			if(complete && problem.isOpen()) {			
				info.setStatement(problem.getHTMLstatement());
				info.setPDFviewable(problem.getPDFfilename() != null);
			}
		} catch (MooshakContentException cause) {
			throw new MooshakException(cause.getMessage(), cause);
		}
		auditLog("view",problemId,Boolean.toString(complete));
		return info;
	}

	@Override
	public void evaluate(String programName, byte[] programCode,
			String problemId, List<String> inputs, boolean consider)
			throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, KORA_PROFILE_ID);
		
		Contest contest = session.getContest();
		if (contest.getContestStatus().equals(ContestStatus.READY)) {
			throw new MooshakException("Submissions NOT allowed, "
					+ "contest ready");
		}

		programName = sanitizePathSegment(programName);
		problemId = sanitizePathSegment(problemId);
		
		Problems problems = contest.open("problems");
		Problem problem = problems.find(problemId);
		
		if (problem == null || !problem.isOpen())
			throw new MooshakException("Submissions NOT allowed, "
					+ "problem not open");
		
		if(consider)
			participantManager.makeTransaction(session, "submissions");
		else
			participantManager.makeTransaction(session, "validations");

		programName = sanitizePathSegment(programName);
		problemId = sanitizePathSegment(problemId);
		
		participantManager.evaluate(session.getContestId(), 
				session.getParticipant().getIdName(), 
				session.getIdName(),
				programName, programCode,
				problemId, inputs, consider);
		
		auditLog("evaluate",programName,problemId,Boolean.toString(consider),
				session.getParticipant().getIdName(), 
				new SimpleDateFormat("dd-MM-yy_hh:mm:ss").format(new Date()));
	}

	
	
	
	@Override
	public void ask(String problem, String subject, String question)
			throws MooshakException {
		Session session = getSession();
		
		Contest contest = session.getContest();
		if (contest.getContestStatus().equals(ContestStatus.READY)) {
			throw new MooshakException("Questions NOT allowed, "
					+ "contest ready");
		}

		participantManager.makeTransaction(session, "questions");
		
		problem = sanitizePathSegment(problem);

		authManager.autorize(session, TEAM_PROFILE_ID);

		participantManager.askQuestion(session, session.getParticipant()
				.getIdName(), problem, subject, question);
		auditLog("ask", problem, subject, question);
	}

	@Override
	public void print(String problemId, String content, String fileName) 
			throws MooshakException {

		try {
			Session session = getSession();
			authManager.autorize(session, TEAM_PROFILE_ID);

			Contest contest = session.getContest();
			if (contest.getContestStatus().equals(ContestStatus.READY)) {
				throw new MooshakException("Printouts NOT allowed, "
						+ "contest ready");
			}

			problemId = sanitizePathSegment(problemId);
			fileName = sanitizePathSegment(fileName);
			
			Problems problems = contest.open("problems");
			Problem problem = problems.find(problemId);
			
			if (problem == null || !problem.isOpen())
				throw new MooshakException("Submissions NOT allowed, "
						+ "problem not open");

			participantManager.makeTransaction(session, "printouts");

			participantManager.submitPrintoutAndPrint(session, problemId, content, 
					fileName);
		} catch(MooshakContentException cause) {
			throw new MooshakException("Error printing "+fileName,cause);
		}
		auditLog("print",problemId,fileName);
	}

	@Override
	public EvaluationSummary getEvaluationSummary(
				String submissionId, 
				boolean consider)
			throws MooshakException {

		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, KORA_PROFILE_ID);
		
		EvaluationSummary report = participantManager
				.getEvaluationSummary(session, sanitizePathSegment(submissionId), 
						consider);
		
		auditLog("getEvaluationSummary",submissionId,Boolean.toString(consider));
		return report;
	}

	@Override
	public MooshakObject getAnsweredQuestion(String id) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID);

		Contest contest = session.getContest();
		Questions questions = contest.open("questions");

		MooshakObject mooshakObject = administratorManager.getMooshakObject(
				questions.getPath().toString() + 
				File.separator + 
				sanitizePathSegment(id));
	
		 auditLog("getAnsweredQuestion",id);
		 return mooshakObject;
	}

	@Override
	public TransactionQuota getTransactionsData(String type)
			throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, KORA_PROFILE_ID);
		
		TransactionQuota quota = participantManager.getTransactionsData(
				session, sanitizePathSegment(type.toLowerCase()));
		
		auditLog("getTransactionsData",type);
		return quota;
	}
	
	@Override
	public String getProgramSkeleton(String problemId, String extension) 
			throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID);

		problemId = sanitizePathSegment(problemId);
		extension = sanitizePathSegment(extension);
		
		Problems problems = session.getContest().open("problems");
		Problem problem = problems.find(problemId);
		
		if (problem == null || !problem.isOpen())
			throw new MooshakException("Submissions NOT allowed, "
					+ "problem not open");
		
		String skeleton = participantManager.getProgramSkeleton(
				session, problemId, extension);
		auditLog("getProgramSkeleton",problemId,extension);
		return skeleton;
	}
	
	@Override
	public String getParticipantLogged() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID);
		String idName =session.getParticipant().getIdName();
		
		auditLog("getParticipantLogged");
		return idName;
	}

	@Override
	public MooshakValue getSubmissionContent(String id, String team)
			throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, KORA_PROFILE_ID);
		
		MooshakValue value = 
				participantManager.getSubmissionContent(session, id);
		auditLog("getSubmissionContent", id, team);
		return value;
	}

	@Override
	public boolean getShowOwnCode() throws MooshakException {
		
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID);
		
		boolean isShowOwnCode = participantManager.getShowOwnCode(session);
		auditLog("getShowOwnCode");
		return isShowOwnCode;
	}
	
	@Override
	public Map<String, String> getAvailableLanguages() throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, KORA_PROFILE_ID, CREATOR_PROFILE_ID);
		
		Map<String, String> availableLanguages = participantManager
				.getAvailableLanguages(session);
		auditLog("getAvailableLanguages");
		return availableLanguages;
	}
	
	@Override
	public EditorKind getEditorKind(String problemId) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID, JUDGE_PROFILE_ID);
		
		EditorKind kind = enkiManager.getEditorKind(session, 
				sanitizePathId(problemId));
		auditLog("getEditorKind", problemId);
		
		return kind;
	}


	@Override
	public String getSubmissionContentsWithoutId(String teamId,
			String problemId, long evalTime, String programName) throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID);
		
		String program = participantManager.getSubmissionContentsWithoutId(session, 
				sanitizePathId(teamId),	sanitizePathId(problemId), evalTime, 
				programName);
		auditLog("getSubmissionContentsWithoutId", teamId, problemId, Long.toString(evalTime),
				programName);
		
		return program;
	}

	@Override
	public boolean isEditableContents(String extension)
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID);
		
		boolean editableContents = participantManager.isEditableContents(session, 
				extension);
		auditLog("isEditableContents", extension);
		
		return editableContents;
	}
	
	public Map<String, String> getOpponents(String problemId) 
			throws MooshakException {
		Session session = getSession();
		authManager.autorize(session, TEAM_PROFILE_ID);
		
		Map<String, String> opponents = participantManager
				.getOpponents(session.getContest(), problemId);
		
		auditLog("getOpponents", problemId);
		
		return opponents;
	}
}
