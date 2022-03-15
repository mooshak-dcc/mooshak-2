package pt.up.fc.dcc.mooshak.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.commons.io.FileDeleteStrategy;

import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

@WebServlet(
		name = "import-content",
		urlPatterns = { "/import-content" }
)
public class ImportContentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

	private static final long MAX_FILE_SIZE = 10 * 1000 * 1024;
	private static final long MAX_MEM_SIZE = 4 * 1024;
       
	private AuthManager authManager = AuthManager.getInstance();

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		HttpSession httpSession = request.getSession();
		Session session = (Session) httpSession.getAttribute("session");
		
		try {

			if(session == null) 
				throw new MooshakException("No session found");
			
			authManager.autorize(session, "admin", "creator");
			
		} catch (MooshakException e) {
			response.setContentType("text/plain");
			Writer writer = response.getWriter();
			writer.write("Mooshak: Error importing content\n\n");
			writer.write(e.getMessage());
			writer.close();
		}

		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		response.setContentType("text/html");
		java.io.PrintWriter out = response.getWriter();

		if (!isMultipart) {
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<p>No file uploaded</p>");
			out.println("</body>");
			out.println("</html>");
			return;
		}

		DiskFileItemFactory factory = new DiskFileItemFactory();

		// maximum size that will be stored in memory
		factory.setSizeThreshold((int) MAX_MEM_SIZE);

		// Location to save data that is larger than maxMemSize.
		factory.setRepository(new File(TEMP_DIR));

		// Set the cleaning tracker
		factory.setFileCleaningTracker(new DeleteFilesOnEndUploadCleaningTracker());

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// maximum file size to be uploaded.
		upload.setSizeMax(MAX_FILE_SIZE);

		try {
			// Parse the request to get file items.
			List<FileItem> fileItems = upload.parseRequest(request);

			// Process the uploaded file items
			Iterator<FileItem> i = fileItems.iterator();

			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet upload</title>");
			out.println("</head>");
			out.println("<body>");

			while (i.hasNext()) {
				FileItem fi = (FileItem) i.next();
				if (!fi.isFormField()) {
					// Get the uploaded file parameters
					// String fieldName = fi.getFieldName();
					String fileName = fi.getName();
					// String contentType = fi.getContentType();
					// boolean isInMemory = fi.isInMemory();
					// long sizeInBytes = fi.getSize();

					// get the file contents
					byte[] contents = fi.get();
					out.println("Uploaded Filename: " + fileName + "<br>");
				} else {
					
				}
			}
			out.println("</body>");
			out.println("</html>");
		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	/**
	 * Read
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException           private void readFileToBytes() throws
	 *                               FileNotFoundException, IOException {
	 *                               FileInputStream fis = new
	 *                               FileInputStream(file); byte[] byteArray = new
	 *                               byte[(int) file.length()];
	 * 
	 *                               // Add the bytes from the file to the array
	 *                               for(int j = 0; j < byteArray.length; j++){
	 *                               byteArray[j] = (byte)fis.read(); // Just to
	 *                               show the bytes are in the array.
	 *                               System.out.println(byteArray[j]); }
	 *                               fis.close(); }
	 */

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, java.io.IOException {

		throw new ServletException("GET method used with " + getClass().getName() + ": POST method required.");
	}

	/**
	 * Cleaning tracker to clean files after each upload with special method
	 * invocation. Not thread safe and must be used with 1 factory = 1 thread
	 * policy.
	 */
	public class DeleteFilesOnEndUploadCleaningTracker extends FileCleaningTracker {

		private List<String> filesToDelete = new ArrayList<>();

		public void deleteTemporaryFiles() {
			for (String file : filesToDelete) {
				new File(file).delete();
			}
			filesToDelete.clear();
		}

		@Override
		public synchronized void exitWhenFinished() {
			deleteTemporaryFiles();
		}

		@Override
		public int getTrackCount() {
			return filesToDelete.size();
		}

		@Override
		public void track(File file, Object marker) {
			filesToDelete.add(file.getAbsolutePath());
		}

		@Override
		public void track(File file, Object marker, FileDeleteStrategy deleteStrategy) {
			filesToDelete.add(file.getAbsolutePath());
		}

		@Override
		public void track(String path, Object marker) {
			filesToDelete.add(path);
		}

		@Override
		public void track(String path, Object marker, FileDeleteStrategy deleteStrategy) {
			filesToDelete.add(path);
		}
	}
}
