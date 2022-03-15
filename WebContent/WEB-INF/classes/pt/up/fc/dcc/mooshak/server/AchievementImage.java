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

import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.AchievementsImages;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.server.commands.CommandService;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Servlet responding with the image of an achievement.
 * URLS of the form {@code achievementsImages/image} are mapped to an image with given name.
 */
public class AchievementImage extends HttpServlet {
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
		String imageId = path.getName(0).toString();		
				
		HttpSession httpSession = request.getSession();
		Session session = (Session) httpSession.getAttribute("session");
		
		imageId = CommandService.sanitizePathId(imageId);
		
		try {

			if(session == null) 
				throw new MooshakException("No session found");
			
			authManager.autorize(session,"team");
						
			AchievementsImages achievementsImages = PersistentObject.openPath("data/configs/achievements");
			pt.up.fc.dcc.mooshak.content.types.AchievementImage achievementImage = achievementsImages.open(imageId);
			String image = achievementImage.getImage().getFileName().toString(); 
			String extension = image.substring(image.indexOf('.'));		
			Path imagePath = achievementImage.getAbsoluteFile(image);
			
			response.setContentType(Configurator.getMime(extension));
				
			ServletOutputStream stream = response.getOutputStream();
			Files.copy(imagePath.toFile(),stream);
			stream.close();
				
		} catch (MooshakException cause) {
			response.setContentType("text/plain");
			Writer writer = response.getWriter();
				
			writer.write("Mooshak: Error downloading achievement image "+imageId+"\n\n");
							
			writer.write(cause.getMessage());;
			writer.close();
		}
	}	
}
