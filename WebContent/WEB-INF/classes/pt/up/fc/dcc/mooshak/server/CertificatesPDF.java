package pt.up.fc.dcc.mooshak.server;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gwt.thirdparty.guava.common.io.Files;

import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.Person;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.server.commands.CommandService;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class CertificatesPDF extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private AuthManager authManager = AuthManager.getInstance();
		

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		process(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(
			HttpServletRequest request, 
			HttpServletResponse response) 
			throws ServletException, IOException {
		process(request,response);
	}

	private void process(
			HttpServletRequest request,
			HttpServletResponse response) 
			throws ServletException, IOException {
		
		String pathInfo = request.getPathInfo();
		Path path = Paths.get(pathInfo);
		String contestId = null;
		String teamId = null;
		String personId = null;
		String fileName = null;
		
		if (path.getNameCount() < 2 || path.getNameCount() > 4) {
			fail(response, "Invalid path");
			return;
		} else if (path.getNameCount() == 2) {
			contestId = path.getName(0).toString();
			fileName = path.getName(1).toString();
		} else if (path.getNameCount() == 3) {
			contestId = path.getName(0).toString();
			teamId = path.getName(1).toString();
			fileName = path.getName(2).toString();
		} else {
			contestId = path.getName(0).toString();
			teamId = path.getName(1).toString();
			personId = path.getName(2).toString();
			fileName = path.getName(3).toString();
		}					
				
		HttpSession httpSession = request.getSession();
		Session session = (Session) httpSession.getAttribute("session");
		
		if (teamId != null)
			teamId = CommandService.sanitizePathId(teamId);
		if (personId != null)
			personId = CommandService.sanitizePathId(personId);
		
		try {
			
			if(session == null) 
				throw new MooshakException("No session found");
			
			Contests contests = PersistentObject.openPath("data/contests");
			Contest contest = contests.find(contestId);
			
			session.setContest(contest);
			
			authManager.autorize(session,"admin");
			
			Path certificatePath = getPDFAbsolutePath(contest,teamId, personId,
					fileName);
			String pdf = certificatePath.toString();
			int index = pdf.indexOf('.');
			String extension;
			
			if(index == -1)
				extension = "pdf";
			else
				extension = pdf.substring(index);
			
			response.setContentType(Configurator.getMime(extension));
				
			ServletOutputStream stream = response.getOutputStream();
			Files.copy(certificatePath.toFile(),stream);
			stream.close();
				
		} catch (MooshakException cause) {
			fail(response, cause.getMessage());
		}
	}

	Path getPDFAbsolutePath(Contest contest,String teamId, String personId,
			String fileName) 
			throws MooshakException {
		
		Groups groups = contest.open("groups");
		
		if (teamId != null) {
			Team team = groups.find(teamId);
			
			if (personId == null) {
				return PersistentCore.getAbsoluteFile(team.getPath().resolve(fileName));
			} else {
				Person person = team.find(personId);
				return PersistentCore.getAbsoluteFile(person.getPath().resolve(fileName));
			}
		} else {
			return PersistentCore.getAbsoluteFile(groups.getPath().resolve(fileName));
		}
	}
	
	void fail(HttpServletResponse response, String message) throws IOException {

		response.setContentType("text/plain");
		Writer writer = response.getWriter();
			
		writer.write("Mooshak: Error downloading certificate!");
						
		writer.write(message);
		writer.close();
	}
}