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

import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Flags;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.server.commands.CommandService;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

import com.google.gwt.thirdparty.guava.common.io.Files;

/**
 * Servlet responding with the image of a flag.
 * URLS of the form {@code flag/code} are mapped to a flag with ISO code.
 * Doesn't need to be authenticated to access this servlet since rankings
 * have flags and guest must see this listings 
 */
// @WebServlet("/Flag")
public class Flag extends HttpServlet {
	private static final long serialVersionUID = 1L;
		

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
		String flagId = path.getName(0).toString();		
				
		HttpSession httpSession = request.getSession();
		Session session = (Session) httpSession.getAttribute("session");
		
		flagId = CommandService.sanitizePathId(flagId);
		
		try {

			if(session == null) 
				throw new MooshakException("No session found");
						
			Flags flags = PersistentObject.openPath("data/configs/flags");
			pt.up.fc.dcc.mooshak.content.types.Flag flag = flags.open(flagId);
			String image = Filenames.getSafeFileName(flag.getImage()); 
			String extension = image.substring(image.indexOf('.'));		
			Path imagePath = flag.getAbsoluteFile(image);
			
			response.setContentType(Configurator.getMime(extension));
				
			ServletOutputStream stream = response.getOutputStream();
			Files.copy(imagePath.toFile(),stream);
			stream.close();
				
		} catch (MooshakException cause) {
			response.setContentType("text/plain");
			Writer writer = response.getWriter();
				
			writer.write("Mooshak: Error downloading flag "+flagId+"\n\n");
							
			writer.write(cause.getMessage());;
			writer.close();
		}
	}	

}
