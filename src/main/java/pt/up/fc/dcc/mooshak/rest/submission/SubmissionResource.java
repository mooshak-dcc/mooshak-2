package pt.up.fc.dcc.mooshak.rest.submission;

import java.io.IOException;
import java.nio.file.Files;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.managers.ParticipantManager;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.problem.model.EvaluationSummaryModel;
import pt.up.fc.dcc.mooshak.rest.submission.model.SubmissionModel;
import pt.up.fc.dcc.mooshak.shared.commands.EvaluationSummary;

public class SubmissionResource extends Resource {
    
    private Contest contest;
    private Submission submission;

	public SubmissionResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
			Contest contest, Submission submission) {
		super(securityContext, uriInfo, request);
		this.contest = contest;
		this.submission = submission;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.RUNNER, Role.TEAM })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SubmissionModel get() {
		
		SubmissionModel sm = new SubmissionModel();
		sm.copyFrom(submission);
		
		return sm;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.RUNNER, Role.TEAM })
	@GET
	@Path("/program")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String program() {
		
		String program;
		try {
			java.nio.file.Path programPath = PersistentCore.getAbsoluteFile(submission.getProgram());
			
			byte[] data = Files.readAllBytes(programPath);

			program = new String(Base64Coder.encode(data));
			
		} catch (IOException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		return program;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.RUNNER, Role.TEAM })
	@GET
	@Path("/evaluation-summary")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public EvaluationSummaryModel evaluationSummary() {
		
		Session session = getSession();
		
		try {
			if (session.getContest() == null) {
				session = session.copy();
				session.setContest(contest);
			}
			
			EvaluationSummary summary = ParticipantManager.getInstance()
					.getEvaluationSummary(
							session,
							submission.getIdName(),
							submission.isConsider()
					);
			return new EvaluationSummaryModel(summary);
		} catch (Exception e) {
			e.printStackTrace();
			throw new InternalServerException(e.getMessage(), e);
		}
	}
	
	
}
