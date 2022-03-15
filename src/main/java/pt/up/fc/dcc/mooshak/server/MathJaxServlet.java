package pt.up.fc.dcc.mooshak.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet to find and retrieve MathJax
 * 
 * @author josepaiva
 */
public class MathJaxServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final String WEBCONTENT_PATH_TO_MATHJAX = "mathjax/";

	private static final Pattern MATHJAX_VERSION_PATTERN = Pattern.compile("/((\\d\\.?)+|latest)\\/",
			Pattern.CASE_INSENSITIVE);

	private static final int BUFFER_SIZE = 8192;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String filename = MATHJAX_VERSION_PATTERN.matcher(req.getPathInfo())
				.replaceFirst("");
		
		InputStream is = new FileInputStream(new File(getServletContext()
				.getRealPath(WEBCONTENT_PATH_TO_MATHJAX + filename)));
		resp.setHeader("Content-Type", getServletContext().getMimeType(filename));
		resp.setHeader("Content-Length", String.valueOf(is.available()));
		resp.setHeader("Content-Disposition", "inline; filename=\"" + filename + "\"");
		
		copy(is, resp.getOutputStream());
	}

	/**
	 * Get MathJax file
	 * 
	 * @param file
	 *            path to file
	 * @return MathJax file
	 */
	protected Path getMathJaxScriptFile(String file) {

		ServletContext context = getServletContext();
		Path webappPath = Paths.get(context.getRealPath("/"));

		if (webappPath == null)
			throw new RuntimeException("Cannot find webapp folder path");

		Path script = webappPath.resolve(Paths.get(WEBCONTENT_PATH_TO_MATHJAX, file));

		return script;
	}

	/**
	 * Reads all bytes from an input stream and writes them to an output stream.
	 */
	private static long copy(InputStream source, OutputStream sink) 
			throws IOException {
		
		long nread = 0L;
		byte[] buf = new byte[BUFFER_SIZE];
		int n;
		while ((n = source.read(buf)) > 0) {
			sink.write(buf, 0, n);
			nread += n;
		}
		return nread;
	}
}
