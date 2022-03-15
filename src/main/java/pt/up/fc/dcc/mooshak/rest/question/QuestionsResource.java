package pt.up.fc.dcc.mooshak.rest.question;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Question;
import pt.up.fc.dcc.mooshak.content.types.Questions;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.managers.ParticipantManager;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.BadRequestException;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.exception.UnprocessableEntityException;
import pt.up.fc.dcc.mooshak.rest.problem.ProblemResource;
import pt.up.fc.dcc.mooshak.rest.question.model.QuestionModel;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo.ContestStatus;

public class QuestionsResource extends Resource {

	private Contest contest;
	private Questions questions;

	public QuestionsResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
			Contest contest, Questions questions) {
		super(securityContext, uriInfo, request);
		this.contest = contest;
		this.questions = questions;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<QuestionModel> list() {
		
		List<Question> questionList;
		try {
			questionList = questions.getChildren(true);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		List<QuestionModel> questionModels = new ArrayList<>();
		
		for (Question question : questionList)
			questionModels.add(QuestionResource.toModel(question));
		
		return questionModels;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public QuestionModel create(QuestionModel questionModel) {
		
		if (questionModel.getProblemId() == null || 
				questionModel.getSubject() == null ||
				questionModel.getQuestion() == null)
			throw new UnprocessableEntityException();
		
		Session session = getSession();
		
		try {
			if (session.getContest() == null) {
				session = session.copy();
				session.setContest(contest);
			}
			
			if (contest.getContestStatus().equals(ContestStatus.READY)) {
				throw new BadRequestException("Questions NOT allowed, "
						+ "contest ready");
			}

			ParticipantManager.getInstance().makeTransaction(session, "questions");
			
			String problemId = sanitizePathSegment(questionModel.getProblemId());
			
			String id = ParticipantManager.getInstance().askQuestion(session, 
					session.getParticipant().getIdName(),
					problemId, questionModel.getSubject(), 
					questionModel.getQuestion());
			
			Question question = questions.open(id);
			
			return QuestionResource.toModel(question);
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}
	
	@Path("/{questionId}")
	public QuestionResource question(@PathParam("questionId") String questionId) {
		
		Question question;
		try {
			question = questions.open(questionId);
		} catch (MooshakContentException e) {
			throw new NotFoundException("The question " + questionId + " does not exist.");
		}
		
		return new QuestionResource(securityContext, uriInfo, request, contest, question);
	}
}
