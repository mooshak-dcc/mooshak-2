package pt.up.fc.dcc.mooshak.rest.problem;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Images;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Skeleton;
import pt.up.fc.dcc.mooshak.content.types.Skeletons;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.managers.ParticipantManager;
import pt.up.fc.dcc.mooshak.rest.Resource;
import pt.up.fc.dcc.mooshak.rest.auth.security.Role;
import pt.up.fc.dcc.mooshak.rest.auth.security.Secured;
import pt.up.fc.dcc.mooshak.rest.exception.BadRequestException;
import pt.up.fc.dcc.mooshak.rest.exception.InternalServerException;
import pt.up.fc.dcc.mooshak.rest.exception.NotFoundException;
import pt.up.fc.dcc.mooshak.rest.problem.model.ProblemInfoModel;
import pt.up.fc.dcc.mooshak.rest.problem.model.ProblemModel;
import pt.up.fc.dcc.mooshak.rest.problem.model.PublicTestCaseModel;
import pt.up.fc.dcc.mooshak.rest.problem.model.SkeletonModel;
import pt.up.fc.dcc.mooshak.rest.submission.model.SubmissionModel;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.ResultsContestInfo.ContestStatus;

public class ProblemResource extends Resource {
	public static final long REQUEST_TIMEOUT = 60 * 1000;
    
    private Contest contest;
    private Problem problem;
    
    public ProblemResource(SecurityContext securityContext, UriInfo uriInfo, Request request,
    		Contest contest, Problem problem) {
    	super(securityContext, uriInfo, request);
        this.contest = contest;
        this.problem = problem;
    }

	@Secured({ Role.ADMIN, Role.CREATOR })
	@PUT
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProblemModel createOrUpdate(ProblemModel problem) {
		
		return null;
	}
	
	@Secured({ Role.ADMIN, Role.CREATOR })
	@PATCH
	@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProblemModel patch(ProblemModel problem) {
		
		return null;
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProblemModel get() {
		
		ProblemModel pm = new ProblemModel();
		pm.copyFrom(problem);
		
		return pm;
	}
	
	@Secured({ Role.ADMIN, Role.CREATOR })
	@DELETE
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProblemModel delete() {
		
		return null;
	}
	
	@Secured({ Role.ADMIN, Role.CREATOR, Role.JUDGE, Role.TEAM })
	@GET
	@Path("/view")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public ProblemInfoModel view(@DefaultValue("true") @QueryParam("complete") boolean complete) {

		ProblemInfoModel info = new ProblemInfoModel();
		
		try {
			info.setId(problem.getIdName());
			info.setTitle(problem.getTitle());
			if(problem.getName() == null)
				info.setLabel(problem.getIdName());
			else
				info.setLabel(problem.getName().toString());
			if(complete) {			
				info.setStatement(problem.getHTMLstatement());
				info.setPDFviewable(problem.getPDFfilename() != null);
			}
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		return info;
	}
	
	@Secured({ Role.ADMIN, Role.CREATOR, Role.JUDGE, Role.TEAM })
	@GET
	@Path("/pdf-statement")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, "application/pdf" })
	public Response pdfStatement() {
		
		if (problem.getPDFfilename() == null)
			throw new NotFoundException("No PDF found for problem");
		
		ResponseBuilder response = Response.ok((Object) PersistentCore
				.getAbsoluteFile(problem.getPdfDescription()).toFile());
		response.header("Content-Type", "application/pdf")
			.header("Content-Disposition", "attachment; filename=" +
					problem.getPdfDescription().getFileName().toString());
		return response.build();
	}
			
	static Pattern imgPattern = Pattern.compile("<img([^>]*)src=(\"|')([^\"']*)(\"|')([^>]*)>", 
					Pattern.CASE_INSENSITIVE);
	
	@Secured({ Role.ADMIN, Role.CREATOR, Role.JUDGE, Role.TEAM })
	@GET
	@Path("/statement")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.TEXT_HTML })
	public String statement() {
		
		String html;
		try {
			html = problem.getHTMLstatement();
			
			Matcher matcher = imgPattern.matcher(html);
			
			Images images = problem.open("images");
			
			while (matcher.find()) {
				
				String image = matcher.group(3);
				java.nio.file.Path path = images.getAbsoluteFile(image);
				
				String contentType = Files.probeContentType(path);
				
				byte[] data = Files.readAllBytes(path);

				String base64str = new String(Base64Coder.encode(data));
				
				// create "data URI"
				StringBuilder sb = new StringBuilder();
				sb.append("data:");
				sb.append(contentType);
				sb.append(";base64,");
				sb.append(base64str);
				
				html = matcher.replaceFirst("<img$1src=$2" + sb.toString() + "$4$5>");
	        }
			
		} catch (MooshakContentException | IOException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		return html;
	}
	
	@Secured({ Role.ADMIN, Role.CREATOR, Role.JUDGE, Role.TEAM })
	@GET
	@Path("/skeletons")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<SkeletonModel> skeletons() {
		 
		List<SkeletonModel> result = new ArrayList<>();
		try {
			
			Skeletons skeletons = problem.open("skeletons");
			
			List<Skeleton> skeletonList = skeletons.getChildren(true);
			
			for (Skeleton skeleton : skeletonList) {
				SkeletonModel item = new SkeletonModel();
				item.setId(skeleton.getIdName());
				item.setExtension(skeleton.getExtension());
				item.setCode(skeleton.getSkeletonCode());
				result.add(item);
			}
			
		} catch (MooshakContentException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		return result;
	}
	
	@Secured({ Role.ADMIN, Role.CREATOR, Role.JUDGE, Role.TEAM })
	@GET
	@Path("/public-tests")
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public List<PublicTestCaseModel> publicTestCases() {
		 
		List<PublicTestCaseModel> result = new ArrayList<>();
		try {
						
			Map<String, String> publicTestCases = ParticipantManager.getInstance()
				.getPublicTestCases(getSession(), problem.getId().toString());
			
			for (Entry<String, String> testcase : publicTestCases.entrySet()) {
				PublicTestCaseModel item = new PublicTestCaseModel();
				item.setInput(testcase.getKey());
				item.setOutput(testcase.getValue());
				result.add(item);
			}
			
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		return result;
	}
	
	@Secured({ Role.TEAM })
	@POST
	@Path("/evaluate")
	@Consumes({ MediaType.MULTIPART_FORM_DATA })
	@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
	public SubmissionModel evaluate(
			@FormDataParam("input") List<String> inputs,
			@DefaultValue("true") @FormDataParam("consider") boolean consider,
			@FormDataParam("program") InputStream programInputStream,
			@FormDataParam("program") FormDataContentDisposition programDetails) {
		
		byte[] code;
		try {
			code = readInputStreamToByteArray(programInputStream);
		} catch (IOException e) {
			throw new InternalServerException("Reading program.");
		}
		
		Session session = getSession();
		
		Submission submission = null;
		try {
			if (session.getContest() == null) {
				session = session.copy();
				session.setContest(contest);
			}
			
			if (contest.getContestStatus().equals(ContestStatus.READY)) 
				throw new BadRequestException("Submissions NOT allowed, "
						+ "contest ready");
	
			String programName = sanitizePathSegment(programDetails.getFileName());
			
			submission = ParticipantManager.getInstance()
					.evaluate(
							session.getContestId(),
							session.getParticipant().getIdName(),
							session.getIdName(),
							programName, code,
							problem.getIdName(),
							inputs, consider
					);
		} catch (MooshakException e) {
			throw new InternalServerException(e.getMessage(), e);
		}
		
		SubmissionModel submissionModel = new SubmissionModel();
		submissionModel.copyFrom(submission);
		
		return submissionModel;
	}
}
