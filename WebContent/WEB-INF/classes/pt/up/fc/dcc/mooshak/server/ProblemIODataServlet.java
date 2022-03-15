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
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Test;
import pt.up.fc.dcc.mooshak.content.types.Tests;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.server.commands.CommandService;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

import com.google.gwt.thirdparty.guava.common.io.Files;

/**
 * Servlet responding with test data of a problem 
 * URLS of the form {@code test-data/$problem/$test/$kind are mapped to a file
 * in folder of {@code $problem/test/$test} in the current contest. 
 */
// @WebServlet("/test-data")
public class ProblemIODataServlet extends HttpServlet {
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
		String testId = path.getName(1).toString();
		String kind = path.getName(2).toString();
					
				
		HttpSession httpSession = request.getSession();
		Session session = (Session) httpSession.getAttribute("session");
		
		problemId = CommandService.sanitizePathId(problemId);
		
		try {
			
			if(session == null) 
				throw new MooshakException("No session found");
			
			authManager.autorize(session,"team");
			
			Contest contest = session.getContest();
			Path filePath = getTestDataAbsolutePath(contest,problemId,testId,kind);
			
			response.setContentType(Configurator.getMime("text/pain"));
				
			ServletOutputStream stream = response.getOutputStream();
			Files.copy(filePath.toFile(),stream);
			stream.close();
				
		} catch (MooshakException cause) {
			response.setContentType("text/plain");
			Writer writer = response.getWriter();
				
			writer.write("Mooshak: Error downloading "+kind+" from test "+testId+
					" of problem "+problemId+" \n\n");
							
			writer.write(cause.getMessage());;
			writer.close();
		}
	}
	
	Path getTestDataAbsolutePath(Contest contest,String problemId,String testId,String kind) 
			throws MooshakContentException, ServletException {
		Problems problems = contest.open("problems");
		Problem problem = problems.find(problemId);
		Tests tests = problem.open("tests");
		Test test = tests.open(testId);
		Path path;
		
		if(! test.isShow()) 
			throw new MooshakContentException("This test is not public");
		
		switch(kind) {
		case "input" :
			path = test.getInput();
			break;
		case "output":
			path = test.getOutput();
			break;
		default:
			throw new ServletException("Invalid kind of teste data '"+kind+"'");
		}
		
		return PersistentCore.getAbsoluteFile(path);
	}
	
}
