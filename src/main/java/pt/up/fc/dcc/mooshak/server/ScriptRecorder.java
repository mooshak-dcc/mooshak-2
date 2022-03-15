package pt.up.fc.dcc.mooshak.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

//@WebServlet("/script")
public class ScriptRecorder  extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	protected final static String SCRIPT_FOLDER = "scripts";

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

		Path scripts = getScriptFolder();
		String command = (String) request.getParameter("command");
		String scriptName = (String) request.getParameter("script");
		String error="";
		String feedback="";
		boolean recording =MooshakServiceServlet.isScriptRecording();

		if(command != null)
			switch(command.toLowerCase()) {
			case "stop":

				if(recording) { 
					MooshakServiceServlet.stopScriptLogging();
					feedback = "Recording stopped";
				} else
					error = "Not recording yet";

				break;
			case "record":

				if(recording) 
					error = "Already recording";
				else {
					if("".equals(scriptName))
						error = "No name given";
					else {
						Path script = scripts.resolve(scriptName);
						
						feedback = "Recording '"+scriptName+"'";
						try {
							MooshakServiceServlet.startScriptLogging(script);
						} catch (MooshakException e) {
							error = e.getMessage();
						}
					}
				}
				break;
			}

		output(response,recording,feedback,error);

	}

	void output(HttpServletResponse response,boolean recording, String feedback, String error) 
			throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/HTML");

		out.println("<!doctype html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<style> .error { color: red; }</style>");
		out.println("<title>Script Recorder</title>");
		out.println("</head>");
		out.println("<body>");

		out.println("<form action='script' method='GET'>");
		if(recording)
			out.println("<input type='submit' name='command' value='stop'>");
		else {
			out.println("<input type='text'   name='script'  value='script'>");
			out.println("<input type='submit' name='command' value='record'>");
		}
		out.println("</form>");
		out.println("<div >"+feedback+"</div>");
		out.println("<div class='error'>"+error+"</div>");
		out.println("</body>");
		out.println("</html>");
	}


	/**
	 * Get folder script where scripts 
	 * (sequences of requests received by the server, including events)
	 * are recorded 
	 * @return
	 * @throws IOException
	 */
	private Path getScriptFolder() throws IOException {

		ServletContext context = getServletContext();
		Path webappPath = Paths.get(context.getRealPath("/"));
		Path webappFolderPath = webappPath.getParent();
		String catalina = null;
		Path logsPath = null;
		Path scripts = null;

		if(webappFolderPath == null)
			throw new RuntimeException("Cannot find webapp folder path");

		if((catalina = System.getProperty("catalina.base")) == null) {
			logsPath = webappFolderPath.resolve(Configurator.LOG_FOLDER);
		} else {
			logsPath = Paths.get(catalina).resolve("logs").resolve("mooshak");
		}

		Files.createDirectories(logsPath);
		scripts = logsPath.resolve(SCRIPT_FOLDER);	
		Files.createDirectories(scripts);

		return scripts;
	}


}
