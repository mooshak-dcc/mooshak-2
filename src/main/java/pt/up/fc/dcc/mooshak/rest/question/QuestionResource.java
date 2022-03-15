package pt.up.fc.dcc.mooshak.rest.question;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.PUT;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Question;
import pt.up.fc.dcc.mooshak.content.types.Question.State;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.ForbiddenException;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.UnprocessableEntityException;
import pt.up.fc.dcc.mooshak.rest.question.model.QuestionModel;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class QuestionResource extends Resource {

	private Contest contest;
	private Question question;

	public QuestionResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
			Contest contest, Question question) {
		super(securityContext, uriInfo, request);
		this.contest = contest;
		this.question = question;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public QuestionModel get() {
		
		return toModel(question);
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public QuestionModel update(QuestionModel questionModel) {
		
		Session session = getSession();
		authorize(session);
		
		if (questionModel.getSubject() == null ||
				questionModel.getQuestion() == null)
			throw new UnprocessableEntityException();
		
		try {
			if (session.getContest() == null) {
				session = session.copy();
				session.setContest(contest);
			}


			if (questionModel.getProblemId() != null) { 
				Problems problems = contest.open("problems");
				Problem problem = problems.open(questionModel.getProblemId());
				question.setProblem(problem);
			} else
				question.setProblem(null);
			question.setSubject(questionModel.getSubject());
			question.setQuestion(questionModel.getQuestion());
			if (questionModel.getState() != null)
				try {
					question.setState(State.valueOf(questionModel.getState().toUpperCase()));
				} catch (Exception e) {
					throw new UnprocessableEntityException();
				}
			else
				question.setState(null);
			question.setAnswer(questionModel.getAnswer());
			question.save();
			
			return QuestionResource.toModel(question);
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@PATCH
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public QuestionModel patch(QuestionModel questionModel) {
		
		Session session = getSession();
		authorize(session);
		
		try {
			if (session.getContest() == null) {
				session = session.copy();
				session.setContest(contest);
			}

			if (questionModel.getProblemId() != null) { 
				Problems problems = contest.open("problems");
				Problem problem = problems.open(questionModel.getProblemId());
				question.setProblem(problem);
			}
			if (questionModel.getSubject() != null)
				question.setSubject(questionModel.getSubject());
			if (questionModel.getQuestion() != null)
				question.setQuestion(questionModel.getQuestion());
			if (questionModel.getState() != null)
				try {
					question.setState(State.valueOf(questionModel.getState().toUpperCase()));
				} catch (Exception e) {
					throw new UnprocessableEntityException();
				}
			if (questionModel.getAnswer() != null)
				question.setAnswer(questionModel.getAnswer());
			question.save();
			
			return QuestionResource.toModel(question);
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}
	
	/**
	 * Check if current user can perform actions in this question
	 * 
	 * @param session
	 */
	private void authorize(Session session) {
		
		Role role;
		try {
			role = Role.valueOf(session.getProfile().getIdName().toUpperCase());
		
			if (role == Role.TEAM) {
				
				if (question.getTeam() != null && 
						session.getParticipant().getIdName().equals(question.getTeam().getIdName()))
					return;
			} else if (role == Role.JUDGE || role == Role.ADMIN)
				return;
		} catch (MooshakContentException e) {
			throw new ForbiddenException();
		}

		throw new ForbiddenException();
	}

	/**
	 * Convert {@link Question} to {@link QuestionModel}
	 * @param question {@link Question}
	 * @return {@link QuestionModel}
	 */
	public static QuestionModel toModel(Question question) {
		
		QuestionModel questionModel = new QuestionModel();
		questionModel.setId(question.getIdName());
		
		try {
			if (question.getProblem() != null)
				questionModel.setProblemId(question.getProblem().getIdName());
		} catch (MooshakContentException e) {
			LOGGER.severe(e.getMessage());
		}
		
		try {
			if (question.getTeam() != null)
				questionModel.setTeamId(question.getTeam().getIdName());
		} catch (MooshakContentException e) {
			LOGGER.severe(e.getMessage());
		}
		
		questionModel.setDelay(Long.toString(question.getDelay().getTime() / 1000));
		questionModel.setDate(Long.toString(question.getDate().getTime() / 1000));
		questionModel.setTime(Long.toString(question.getTime().getTime() / 1000));
		questionModel.setState(question.getState().toString());
		questionModel.setSubject(question.getSubject());
		questionModel.setQuestion(question.getQuestion());
		questionModel.setAnswer(question.getAnswer());
		
		return questionModel;
	}	
}
