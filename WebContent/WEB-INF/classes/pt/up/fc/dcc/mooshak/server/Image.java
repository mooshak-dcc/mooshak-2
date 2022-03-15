package pt.up.fc.dcc.mooshak.server;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
// import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Images;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.server.commands.CommandService;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

import com.google.gwt.thirdparty.guava.common.io.Files;

/**
 * Servlet responding with an image of a problem.
 * URLS of the form {@code image/problem/name.ext} are mapped to an image
 * named {name.ext} in folder {@code images} of {@code problem} in the contest
 * current contest. 
 */
// @WebServlet("/Image")
public class Image extends HttpServlet {
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
		String problemId = path.getName(0).toString();
		String image = path.getName(path.getNameCount()-1).toString();
		String extension = image.substring(image.indexOf('.'));			
				
		HttpSession httpSession = request.getSession();
		Session session = (Session) httpSession.getAttribute("session");
		
		problemId = CommandService.sanitizePathId(problemId);
		image = CommandService.sanitizePathId(image);
		
		try {

			if(session == null) 
				throw new MooshakException("No session found");
			
			authManager.autorize(session,"team", "judge", "creator");
			
			Contest contest = session.getContest();
			Path imagePath = getImageAbsolutePath(contest,problemId,image);
			
			response.setContentType(Configurator.getMime(extension));
				
			ServletOutputStream stream = response.getOutputStream();
			Files.copy(imagePath.toFile(),stream);
			stream.close();
				
		} catch (MooshakException cause) {
			response.setContentType("text/plain");
			Writer writer = response.getWriter();
				
			writer.write("Mooshak: Error downloading image "+image+
						" from problem "+problemId+"\n\n");
							
			writer.write(cause.getMessage());;
			writer.close();
		}
	}

	Path getImageAbsolutePath(Contest contest,String problemId, String image) 
			throws MooshakContentException {
		Problems problems = contest.open("problems");
		Problem problem = problems.find(problemId);
		Images images = problem.open("images");
		
		return images.getAbsoluteFile(image);
	}
	
}
