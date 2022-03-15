package pt.up.fc.dcc.mooshak.server;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.util.Dates;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.gwt.user.server.rpc.SerializationPolicy;
import com.google.gwt.user.server.rpc.SerializationPolicyLoader;

/**
 * Methods required by Mooshak service servlets, such as those to 
 * 1) overwrite RemoteServiceServlet for proxy
 * 2) handle request logging 
 * 
 * @author josepaiva
 */
public class MooshakServiceServlet extends RemoteServiceServlet {
	private static final long serialVersionUID = 1L;

	
	/**
	 * Attempt to load the RPC serialization policy normally. If it isn't found,
	 * try loading it using the context path instead of the URL.
	 */
	@Override
	protected SerializationPolicy doGetSerializationPolicy(HttpServletRequest request, String moduleBaseURL,
			String strongName) {
		SerializationPolicy policy = super.doGetSerializationPolicy(request, moduleBaseURL, strongName);
		if (policy == null) {
			return MooshakServiceServlet.loadSerializationPolicy(this, request, moduleBaseURL, strongName);
		} else {
			return policy;
		}
	}

	/**
	 * Load the RPC serialization policy via the context path.
	 */
	static SerializationPolicy loadSerializationPolicy(HttpServlet servlet, HttpServletRequest request,
			String moduleBaseURL, String strongName) {
		// The serialization policy path depends only by context path
		String contextPath = request.getContextPath();
		SerializationPolicy serializationPolicy = null;
		String contextRelativePath = contextPath + "/";
		String serializationPolicyFilePath = SerializationPolicyLoader
				.getSerializationPolicyFileName(contextRelativePath + strongName);

		// Open the RPC resource file and read its contents.
		InputStream is = servlet.getServletContext().getResourceAsStream(serializationPolicyFilePath);
		try {
			if (is != null) {
				try {
					serializationPolicy = SerializationPolicyLoader.loadFromStream(is, null);
				} catch (ParseException e) {
					servlet.log("ERROR: Failed to parse the policy file '" + serializationPolicyFilePath + "'", e);
				} catch (IOException e) {
					servlet.log("ERROR: Could not read the policy file '" + serializationPolicyFilePath + "'", e);
				}
			} else {
				String message = "ERROR: The serialization policy file '" + serializationPolicyFilePath
						+ "' was not found; did you forget to include it in this deployment?";
				servlet.log(message);
			}
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					// Ignore this error
				}
			}
		}

		return serializationPolicy;
	}

	/******************************************************************\
	 *      Request logging                                           *
	\******************************************************************/

	protected final static Logger AUDIT_LOGGER = Logger.getLogger("audit_log");
	protected final static Logger SCRIPT_LOGGER = Logger.getLogger("script_log");
	
	
	private static boolean recording = false;
	
	
	public static void startScriptLogging(Path script) throws MooshakException  {
		
		Configurator.configureAuditLogging(script,SCRIPT_LOGGER);

		recording = true;	
	}
	
	public static void stopScriptLogging() {
		recording = false;
	}
	
	public static boolean isScriptRecording() {
		return recording;
	}
	
	/**
	 * Return list of available script
	 * @return
	 * @throws MooshakException
	 */
	public static List<String> getAvailableScripts(Path scriptFolder) 
			throws MooshakException {
		List<String> scriptList = new ArrayList<>();
		
		
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(scriptFolder)) {
			for(Path path: stream)
				scriptList.add(Filenames.getSafeFileName(path));
		} catch (IOException cause) {
			throw new MooshakException("IO error reading script names",cause);
		}
		
		return scriptList;
	}
		
	
	
	/**
	 * Record this command just for replay not for auditing
	 * 
	 * @param command name
	 * @param args
	 */
	protected void scriptLog(String command,String... args) {
		if(recording)
			auditAndRecord(false,command,args);
			
	}
	
	/**
	 * Record this command just for auditing not replay
	 * 
	 * @param command name
	 * @param args
	 */
	protected void justAuditLog(String command,String... args) {
		if(! recording)
			auditAndRecord(true,command,args);
			
	}
	
	/**
	 * Record this command both for replay and auditing
	 * @param command name
	 * @param args
	 */
	protected void auditLog(String command,String... args) {
		auditAndRecord(true,command,args);
	}
	
	private void auditAndRecord(boolean auditing,String command,String... args){
		Session session = (Session) 
				getThreadLocalRequest().getSession().getAttribute("session");
		StringBuilder builder = new StringBuilder();

		builder.append(Dates.show(new Date()));
		builder.append(" ");

		if(session != null) {
			String profile = "<profile?>";

			try {
				profile = session.getProfile().getIdName();
			} catch (Exception e) {
				// ignore missing profiles
			}

			builder.append(profile);
			builder.append(" ");
			builder.append(session.getIdName());
			builder.append(" ");
		}

		builder.append(command);
		builder.append(" ");
		for(String arg: args) {
			builder.append(arg);
			builder.append(" ");	
		}		
	
		if(auditing)
			AUDIT_LOGGER.info(builder.toString());
		if(recording)
			SCRIPT_LOGGER.info(builder.toString());
	}
	
}
