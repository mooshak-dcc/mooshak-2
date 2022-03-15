package pt.up.fc.dcc.mooshak.rest.contest;

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
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.content.util.Streams;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.NotSecured;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.contest.model.ContestModel;
import pt.up.fc.dcc.mooshak.rest.exception.BadRequestException;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class ContestsResource extends Resource {
	
	private Contests contests;

    public ContestsResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
    		Contests contests) {
    	super(securityContext, uriInfo, request);
    	this.contests = contests;
    }

    @NotSecured
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<ContestModel> list() {
		
		List<Contest> contestList;
		try {
			contestList = contests.getChildren(true);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		List<ContestModel> contestModels = new ArrayList<>();
		
		for (Contest contest : contestList) {
			ContestModel contestModel = new ContestModel();
			contestModel.copyFrom(contest);
			contestModel.setContestType(null);
			contestModel.setEmail(null);
			contestModel.setOrganizes(null);
			contestModel.setHideListings(null);
			contestModel.setPolicy(null);
			contestModel.setTransactionLimit(null);
			contestModel.setTransactionLimitTime(null);
			contestModel.setService(null);
			contestModel.setNotes(null);
			contestModel.setFatal(null);
			contestModel.setWarning(null);
			contestModels.add(contestModel);
		}
		
		return contestModels;
	}

	@Secured(Role.ADMIN)
	@POST
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ContestModel create(ContestModel contest) {
		
		return null;
	}

	@Secured({ Role.ADMIN, Role.CREATOR })
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ContestModel importFromZip(
		@FormDataParam("file") InputStream uploadedInputStream,
	    @FormDataParam("file") FormDataContentDisposition fileDetails
	) {
		String parent = contests.getPath().toString();
		String poName = Filenames.getFileNameWithoutExtension(fileDetails.getFileName());
		try {
			
			AdministratorManager.getInstance()
				.importMooshakObject(
						parent,
						poName,
						Streams.getBytesFromInputStream(uploadedInputStream)
				);
		} catch (MooshakException e) {
			throw new InternalServerException("There was a problem importing the Contest object.");
		} catch (IOException e) {
			throw new BadRequestException("There was a problem reading the uploaded file.");
		}
		
		Contest contest;
		try {
			contest = contests.open(poName);
		} catch (MooshakContentException e) {
			throw new InternalServerException("There was a problem importing the Contest object.");
		}
		
		ContestModel cm = new ContestModel();
		cm.copyFrom(contest);
		
		return cm;
	}
	
	@Path("/{contestId}")
	public ContestResource contest(@PathParam("contestId") String contestId) {
		
		Contest contest;
		try {
			contest = contests.open(contestId);
		} catch (MooshakContentException e) {
			throw new NotFoundException("The contest " + contestId + " does not exist.");
		}
		
		return new ContestResource(securityContext, uriInfo, request, contest);
	}
}
