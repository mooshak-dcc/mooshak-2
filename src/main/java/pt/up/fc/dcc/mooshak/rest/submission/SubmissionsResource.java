package pt.up.fc.dcc.mooshak.rest.submission;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.submission.model.SubmissionModel;

public class SubmissionsResource extends Resource {
	
    private Contest contest;
    private Submissions submissions;

	public SubmissionsResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
			Contest contest, Submissions submissions) {
		super(securityContext, uriInfo, request);
		this.contest = contest;
		this.submissions = submissions;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<SubmissionModel> list() {
		
		List<Submission> submissionList;
		try {
			submissionList = submissions.getChildren(true);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		List<SubmissionModel> submissionModels = new ArrayList<>();
		
		for (Submission submission : submissionList) {
			SubmissionModel submissionModel = new SubmissionModel();
			submissionModel.copyFrom(submission);
			submissionModels.add(submissionModel);
		}
		
		return submissionModels;
	}
	
	@Path("/{submissionId}")
	public SubmissionResource problem(@PathParam("submissionId") String submissionId) {
		
		Submission submission;
		try {
			submission = submissions.open(submissionId);
		} catch (MooshakContentException e) {
			throw new NotFoundException("The submission " + submissionId + " does not exist.");
		}
		
		return new SubmissionResource(securityContext, uriInfo, request, contest, submission);
	}

}
