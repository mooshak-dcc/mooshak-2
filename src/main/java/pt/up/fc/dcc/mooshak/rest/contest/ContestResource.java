package pt.up.fc.dcc.mooshak.rest.contest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.HasListingRow;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.Printouts;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Questions;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.contest.model.ContestModel;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.language.LanguagesResource;
import pt.up.fc.dcc.mooshak.rest.printout.PrintoutsResource;
import pt.up.fc.dcc.mooshak.rest.problem.ProblemsResource;
import pt.up.fc.dcc.mooshak.rest.question.QuestionsResource;
import pt.up.fc.dcc.mooshak.rest.submission.SubmissionsResource;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class ContestResource extends Resource {
    
    private Contest contest;
    
    public ContestResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
    		Contest contest) {
    	super(securityContext, uriInfo, request);
        this.contest = contest;
    }

	@Secured(Role.ADMIN)
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response createOrUpdate(ContestModel contest) {
		
		return null;
	}
	
	@Secured(Role.ADMIN)
	@PATCH
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response patch(ContestModel contest) {
		
		return null;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ContestModel get() {
		
		ContestModel cm = new ContestModel();
		cm.copyFrom(contest);
		
		return cm;
	}
	
	@Secured(Role.ADMIN)
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public Response delete() {
		
		return null;
	}
	
	@Path("/problems")
	public ProblemsResource problems() {
		
		Problems problems;
		try {
			problems = contest.open("problems");
		} catch (MooshakContentException e) {
			throw new NotFoundException("The object problems does not exist.");
		}
		
		return new ProblemsResource(securityContext, uriInfo, request, contest, problems);
	}
	
	@Path("/submissions")
	public SubmissionsResource submissions() {
		
		Submissions submissions;
		try {
			submissions = contest.open("submissions");
		} catch (MooshakContentException e) {
			throw new NotFoundException("The object submissions does not exist.");
		}
		
		return new SubmissionsResource(securityContext, uriInfo, request, contest, submissions);
	}
	
	@Path("/validations")
	public SubmissionsResource validations() {
		
		Submissions submissions;
		try {
			submissions = contest.open("validations");
		} catch (MooshakContentException e) {
			throw new NotFoundException("The object validations does not exist.");
		}
		
		return new SubmissionsResource(securityContext, uriInfo, request, contest, submissions);
	}
	
	@Path("/questions")
	public QuestionsResource questions() {
		
		Questions questions;
		try {
			questions = contest.open("questions");
		} catch (MooshakContentException e) {
			throw new NotFoundException("The object questions does not exist.");
		}
		
		return new QuestionsResource(securityContext, uriInfo, request, contest, questions);
	}
	
	@Path("/printouts")
	public PrintoutsResource printouts() {
		
		Printouts printouts;
		try {
			printouts = contest.open("printouts");
		} catch (MooshakContentException e) {
			throw new NotFoundException("The object printouts does not exist.");
		}
		
		return new PrintoutsResource(securityContext, uriInfo, request, contest, printouts);
	}
	
	@Path("/languages")
	public LanguagesResource languages() {
		
		Languages languages;
		try {
			languages = contest.open("languages");
		} catch (MooshakContentException e) {
			throw new NotFoundException("The object languages does not exist.");
		}
		
		return new LanguagesResource(securityContext, uriInfo, request, contest, languages);
	}

	@GET
	@Path("/rankings")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public String rankings() {
		
		POStream<? extends HasListingRow> rows;
		try {
			rows = contest.getRankingPolicy().getRows();
		} catch (MooshakException e) {
			throw new InternalServerException("Could not get rows", e);
		}
		
		JsonArrayBuilder jab = Json.createArrayBuilder();
		
		for (HasListingRow hasListingRow : rows) {
			
			JsonObjectBuilder job = Json.createObjectBuilder();
			
			try {
				Map<String, String> row = hasListingRow.getRow();
				for (String key : row.keySet()) {
					job.add(key, row.get(key));
				}
				jab.add(job.build());
			} catch (MooshakContentException e) {
				LOGGER.severe("Could not get row: " + e.getMessage());
			}
		}
		
		return jab.build().toString();
	}
	
	
}
