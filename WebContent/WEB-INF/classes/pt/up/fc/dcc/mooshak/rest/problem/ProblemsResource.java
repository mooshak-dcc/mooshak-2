package pt.up.fc.dcc.mooshak.rest.problem;

import java.io.IOException;
import java.io.InputStream;
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

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.content.util.Streams;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.BadRequestException;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.problem.model.ProblemModel;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class ProblemsResource extends Resource {
	
    private Contest contest;
    private Problems problems;
    
    public ProblemsResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
    		Contest contest, Problems problems) {
    	super(securityContext, uriInfo, request);
        this.contest = contest;
        this.problems = problems;
    }

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<ProblemModel> list() {
		
		List<Problem> problemList;
		try {
			problemList = problems.getChildren(true);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		List<ProblemModel> problemModels = new ArrayList<>();
		
		for (Problem problem : problemList) {
			ProblemModel problemModel = new ProblemModel();
			problemModel.copyFrom(problem);
			problemModels.add(problemModel);
		}
		
		return problemModels;
	}

	@Secured({ Role.ADMIN, Role.CREATOR })
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProblemModel create(ProblemModel contest) {
		
		return null;
	}

	@Secured({ Role.ADMIN, Role.CREATOR })
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProblemModel importFromZip(
		@FormDataParam("file") InputStream uploadedInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileDetails
	) {
		String parent = problems.getPath().toString();
		String poName = Filenames.getFileNameWithoutExtension(fileDetails.getFileName());
		try {
			
			AdministratorManager.getInstance()
				.importMooshakObject(
						parent,
						poName,
						Streams.getBytesFromInputStream(uploadedInputStream)
				);
		} catch (MooshakException e) {
			throw new InternalServerException("There was a problem importing the Problem object.");
		} catch (IOException e) {
			throw new BadRequestException("There was a problem reading the uploaded file.");
		}
		
		Problem problem;
		try {
			problem = problems.open(poName);
		} catch (MooshakContentException e) {
			throw new InternalServerException("There was a problem importing the Problem object.");
		}
		
		ProblemModel pm = new ProblemModel();
		pm.copyFrom(problem);

		return pm;
	}
	
	@Path("/{problemId}")
	public ProblemResource problem(@PathParam("problemId") String problemId) {
		
		Problem problem;
		try {
			problem = problems.open(problemId);
		} catch (MooshakContentException e) {
			throw new NotFoundException("The problem " + problemId + " does not exist.");
		}
		
		return new ProblemResource(securityContext, uriInfo, request, contest, problem);
	}
}
