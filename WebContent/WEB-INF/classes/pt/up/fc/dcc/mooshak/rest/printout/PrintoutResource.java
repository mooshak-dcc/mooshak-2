package pt.up.fc.dcc.mooshak.rest.printout;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Printout;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.printout.model.PrintoutModel;

public class PrintoutResource extends Resource {

	private Contest contest;
	private Printout printout;
	
	public PrintoutResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
			Contest contest, Printout printout) {
		super(securityContext, uriInfo, request);
		this.contest = contest;
		this.printout = printout;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public PrintoutModel get() {
		
		return toModel(printout);
	}

	/**
	 * Convert a {@link Printout} to a {@link PrintoutModel}
	 * @param printout
	 * @return {@link PrintoutModel}
	 */
	public static PrintoutModel toModel(Printout printout) {
		
		PrintoutModel pm = new PrintoutModel();
		pm.setId(printout.getIdName());
		
		try {
			if (printout.getProblem() != null)
				pm.setProblemId(printout.getProblem().getIdName());
		} catch (MooshakContentException e) {
			LOGGER.severe(e.getMessage());
		}
		
		try {
			if (printout.getTeam() != null)
				pm.setTeamId(printout.getTeam().getIdName());
		} catch (MooshakContentException e) {
			LOGGER.severe(e.getMessage());
		}
		
		pm.setDelay(Long.toString(printout.getDelay().getTime() / 1000));
		pm.setDate(Long.toString(printout.getDate().getTime() / 1000));
		pm.setTime(Long.toString(printout.getTime().getTime() / 1000));
		pm.setState(printout.getState().toString().toLowerCase());
		if (printout.getProgram() != null && printout.getProgram().getFileName() != null)
			pm.setProgram(printout.getProgram().getFileName().toString());
		return pm;
	}
}
