package pt.up.fc.dcc.mooshak.rest.language;

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
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.language.model.LanguageModel;

public class LanguagesResource extends Resource {

	private Contest contest;
	private Languages languages;

	public LanguagesResource(SecurityContext securityContext, UriInfo uriInfo,
			Request request, Contest contest,
			Languages languages) {
		super(securityContext, uriInfo, request);
		this.contest = contest;
		this.languages = languages;
	}

	@Secured({ Role.ADMIN, Role.JUDGE, Role.TEAM })
	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<LanguageModel> list() {

		List<Language> languageList;
		try {
			languageList = languages.getChildren(true);
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}

		List<LanguageModel> languageModels = new ArrayList<>();

		for (Language language : languageList)
			languageModels.add(LanguageResource.toModel(language));

		return languageModels;
	}

	@Path("/{languageId}")
	public LanguageResource language(@PathParam("languageId") String languageId) {

		Language language;
		try {
			language = languages.open(languageId);
		} catch (MooshakContentException e) {
			throw new NotFoundException("The language " + languageId + " does not exist.");
		}

		return new LanguageResource(securityContext, uriInfo, request, contest, language);
	}
}
