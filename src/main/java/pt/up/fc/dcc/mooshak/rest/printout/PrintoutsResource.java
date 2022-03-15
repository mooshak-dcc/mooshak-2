package pt.up.fc.dcc.mooshak.rest.printout;

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
import pt.up.fc.dcc.mooshak.content.types.Printout;
import pt.up.fc.dcc.mooshak.content.types.Printouts;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.util.Charsets;
import pt.up.fc.dcc.mooshak.managers.ParticipantManager;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.printout.model.PrintoutModel;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo.ContestStatus;

public class PrintoutsResource extends Resource {

	private Contest contest;
	private Printouts printouts;
	
	public PrintoutsResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
			Contest contest, Printouts printouts) {
		super(securityContext, uriInfo, request);
		this.contest = contest;
		this.printouts = printouts;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<PrintoutModel> list() {
		
		List<Printout> printoutList;
		try {
			printoutList = printouts.getChildren(true);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		List<PrintoutModel> printoutModels = new ArrayList<>();
		
		for (Printout printout : printoutList)
			printoutModels.add(PrintoutResource.toModel(printout));
		
		return printoutModels;
	}

	@Secured({ Role.TEAM })
	@POST
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public PrintoutModel create(
			@FormDataParam("problemId") String problemId,
			@FormDataParam("program") InputStream programInputStream,
			@FormDataParam("program") FormDataContentDisposition programDetails) {
		
		String code;
		try {
			code = Charsets.fixCharset(readInputStreamToByteArray(programInputStream));
		} catch (IOException e) {
			throw new InternalServerException("Reading program.");
		}
		
		Session session = getSession();
		
		try {
			if (session.getContest() == null) {
				session = session.copy();
				session.setContest(contest);
			}

			if (contest.getContestStatus().equals(ContestStatus.READY)) {
				throw new MooshakException("Printouts NOT allowed, "
						+ "contest ready");
			}

			ParticipantManager.getInstance().makeTransaction(session, "printouts");

			problemId = sanitizePathSegment(problemId);
			String fileName = sanitizePathSegment(programDetails.getFileName());

			String id = ParticipantManager.getInstance().submitPrintoutAndPrint(session, 
					problemId, code, fileName);
			
			Printout printout = printouts.open(id);
			
			return PrintoutResource.toModel(printout);
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
	}
	
	@Path("/{printoutId}")
	public PrintoutResource printout(@PathParam("printoutId") String printoutId) {
		
		Printout printout;
		try {
			printout = printouts.open(printoutId);
		} catch (MooshakContentException e) {
			throw new NotFoundException("The printouut " + printoutId + " does not exist.");
		}
		
		return new PrintoutResource(securityContext, uriInfo, request, contest, printout);
	}
}
