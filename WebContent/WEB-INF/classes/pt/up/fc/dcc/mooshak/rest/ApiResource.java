package pt.up.fc.dcc.mooshak.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import pt.up.fc.dcc.mooshak.rest.auth.security.NotSecured;
import pt.up.fc.dcc.mooshak.rest.model.SimpleResult;

@Path("/")
public class ApiResource extends Resource {

	@NotSecured
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SimpleResult<String> api() {
		return new SimpleResult<>("Welcome to Mooshak 2.0 API");
	}
}
