package pt.up.fc.dcc.mooshak.rest.data;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.contest.ContestsResource;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.model.SimpleResult;

@Path("/data")
public class DataResource extends Resource {
	
	public DataResource() {
	}

    public DataResource(SecurityContext securityContext, UriInfo uriInfo, Request request) {
    	super(securityContext, uriInfo, request);
    }
	
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SimpleResult<String> data() {
		return new SimpleResult<>(securityContext.getUserPrincipal().getName());
	}

	@Path("/contests")
	public ContestsResource contests() {
		
		Contests contests;
		try {
			contests = PersistentObject.openPath("data/contests");
		} catch (MooshakContentException e) {
			throw new NotFoundException("The object contests does not exist.");
		}
		
		return new ContestsResource(securityContext, uriInfo, request, contests);
	}
}
