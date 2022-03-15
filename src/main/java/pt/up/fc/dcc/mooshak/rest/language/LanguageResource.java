package pt.up.fc.dcc.mooshak.rest.language;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.ResponseBuilder;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.language.model.LanguageModel;

public class LanguageResource extends Resource {

	private Contest contest;
	private Language language;
	
	public LanguageResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
			Contest contest, Language language) {
		super(securityContext, uriInfo, request);
		this.contest = contest;
		this.language = language;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public LanguageModel get() {
		
		return toModel(language);
	}
	
	@Secured({ Role.ADMIN, Role.CREATOR, Role.JUDGE, Role.TEAM })
	@GET
	@Path("/configuration")
	@Produces(MediaType.APPLICATION_OCTET_STREAM)
	public Response configuration() {
		
		if (language.getConfiguration() == null)
			throw new NotFoundException("No configuration found for language");
		
		ResponseBuilder response = Response.ok((Object) PersistentCore
				.getAbsoluteFile(language.getConfiguration()).toFile());
		response.header("Content-Disposition", "attachment; filename=" +
					language.getConfiguration().getFileName().toString());
		return response.build();
	}

	/**
	 * Convert a {@link Language} to a {@link LanguageModel}
	 * @param language
	 * @return {@link LanguageModel}
	 */
	public static LanguageModel toModel(Language language) {
		
		LanguageModel lm = new LanguageModel();
		lm.copyFrom(language);
		return lm;
	}
}
