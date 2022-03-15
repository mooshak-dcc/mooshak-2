package pt.up.fc.dcc.mooshak.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.stream.Collectors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import pt.up.fc.dcc.mooshak.balancer.MooshakWorker;
import pt.up.fc.dcc.mooshak.content.Healer;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PathManager;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationQueue;
import pt.up.fc.dcc.mooshak.evaluation.Evaluator;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.evaluation.StandardEvaluationQueue;
import pt.up.fc.dcc.mooshak.managers.AuthManager;
import pt.up.fc.dcc.mooshak.managers.EnkiManager;
import pt.up.fc.dcc.mooshak.shared.MooshakException;


/**
 * The purpose of this servlet is to centralize configurations used by 
 * other servlets, including those used by GWT's RPC, and start evaluators.
 * 
 * It also responds to a request for displaying current configurations.
 * 
 * This servlet is configured to be the first to be loaded and it 
 * receives configurations both as parameters and local resources (MIME types)  
 *   
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class Configurator  extends HttpServlet {
	public static final String LOG_FOLDER = "logs";
	private static final String AUDIT_LOG_FILE = "audit_log";
	private static final String ERROR_LOG_FILE = "error_log";

	private static final long serialVersionUID = 1L;

    protected static final Charset CHARSET = Charset.defaultCharset();
	
	private static final Logger LOGGER = Logger.getLogger("");
	protected final static Logger AUDIT_LOGGER = 
			Logger.getLogger(Configurator.AUDIT_LOG_FILE);
	
	private static final String PROPERTIES_FILENAME = "MooshakProperties.xml";

	
	private static volatile int sessionCount = 0;
	

	/**
	 * Get number of open sessions in Mooshak 
	 * @return the sessionCount
	 */
	public static int getSessionCount() {
		return sessionCount;
	}



	private Properties properties = new Properties();
	
	private List<Evaluator> evaluators = new ArrayList<>();

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		
		ServletContext context = config.getServletContext();
		Path webappPath = Paths.get(context.getRealPath("/"));
		Path webappFolderPath = webappPath.getParent();
		
		if(webappFolderPath == null)
			throw new ServletException("Cannot find webapp folder path");
		
		Path propertiesPath= webappFolderPath.resolve(PROPERTIES_FILENAME);
		String catalina;
		Path logsPath = null;
		
		context.setAttribute("properties", 	properties);

		try(InputStream stream = Files.newInputStream(propertiesPath)) {
			properties.loadFromXML(stream);
		} catch (IOException cause) {
			LOGGER.log(Level.WARNING,"Error loading properties",cause);
		}
		
		try {
			if((catalina = System.getProperty("catalina.base")) == null) {
				logsPath = webappFolderPath.resolve(LOG_FOLDER);
			} else {
				logsPath = Paths.get(catalina).resolve("logs").resolve("mooshak");
			}
			
			Files.createDirectories(logsPath);
			
			configureErrorLogging(logsPath.resolve(ERROR_LOG_FILE));
			configureAuditLogging(logsPath.resolve(AUDIT_LOG_FILE),AUDIT_LOGGER);
			
		} catch (IOException cause) {
			LOGGER.log(Level.SEVERE,"Creating log directory: "+logsPath,cause);
		}
		
		
		
		context.addListener(new HttpSessionListener(){

			@Override
			public void sessionCreated(HttpSessionEvent arg0) {
				//TODO cleanup session directory
				
				sessionCount++;
			}

			@Override
			public void sessionDestroyed(HttpSessionEvent event) {
				HttpSession httpSession = event.getSession();
				
				Session session = (Session) httpSession.getAttribute("session");
				try {
					if(session != null && session.inFileSystem())
						session.delete();
				} catch (MooshakContentException cause) {
					LOGGER.log(Level.SEVERE,"Removing session info",cause);
				}
				
				sessionCount--;
			}
			
		});
		
		
		String home = properties.getProperty("homeDirectory",CustomData.HOME);
		
		PersistentObject.setHome(home);
		SafeExecution.setWebInf(webappPath.resolve("WEB-INF"));
		
		try {
			new Healer().heal();
		} catch (MooshakContentException cause) {
			LOGGER.log(Level.SEVERE,"Error healing data",cause);
		}
			
		startEvaluators();

		String gamificationServer = properties.getProperty("gamificationServiceURL",
				"http://localhost:8080/Odin/");
		String sequenciationServer = properties.getProperty("sequenciationServiceURL",
				"http://localhost:8080/Seqins/");
		
		EnkiManager.setGamificationServer(gamificationServer);
		EnkiManager.setSequenciationServer(sequenciationServer);
	}
	
	
	private void configureErrorLogging(Path errorLogPath) {
		
		String errorLogPathname = errorLogPath.toAbsolutePath().toString();
		
		String levelName = properties.getProperty("loggerLevel","WARNING");
		Level level = Level.WARNING;
		
		try {
			level = Level.parse(levelName);
		} catch(IllegalArgumentException cause) {
			LOGGER.log(Level.SEVERE,"Illegal level name: "+levelName,cause);
		}
			
		
		try {
			Handler handler = new FileHandler(errorLogPathname,	true);
			handler.setFormatter(new SimpleFormatter());
			LOGGER.addHandler(handler);
			LOGGER.setLevel(level);
			
		} catch (SecurityException | IOException cause) {
			LOGGER.log(Level.SEVERE,"Error setting log file:"+errorLogPathname,
					cause);
		}
	}
	
	/**
	 * Configure audit logging. It can be used also for script logging
	 * @param auditLogPath
	 * @param logger
	 */
	public static void configureAuditLogging(Path auditLogPath,Logger logger) {
		
		String auditLogPathname = auditLogPath.toAbsolutePath().toString();
		
		try {
			Handler handler = new FileHandler(auditLogPathname,	true);
			handler.setFormatter(new Formatter() {

				@Override
				public String format(LogRecord record) {
					return formatMessage(record)+"\n";
				}
				
			});
			logger.addHandler(handler);
			logger.setLevel(Level.INFO);
			logger.setUseParentHandlers(false);
			
		} catch (SecurityException | IOException cause) {
			LOGGER.log(Level.SEVERE,"Error setting log file:"+auditLogPathname,
					cause);
		}
	}


	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	@Override
	public void destroy() {
		super.destroy();
		
		// terminate all running threads
		
		PathManager.getInstance().stopWatchingFS();
		AuthManager.getInstance().stopScheduler();
		
		for(Evaluator evaluator: evaluators)
			evaluator.end();
	}


	/**
	 * Start an evaluator, reading request either locally or remotely,
	 * depending on the master host name set on the container
	 * configuration file (web.xml) 	
	 */
	public void startEvaluators() {
		EvaluationQueue evaluationQueue = getEvaluationQueue();
		int evaluatorCount = getEvaluatorCount();
		
		for (int count = 0; count < evaluatorCount; count++) {
		
			try {
				
				String name = "StandardEvaluator#"+count;
				Evaluator evaluator = new Evaluator(name,evaluationQueue);
				
				evaluator.begin();
				evaluators.add(evaluator);
				
			} catch(MooshakException cause) {
				LOGGER.log(Level.SEVERE,"Starting evaluator #"+count,cause);
			} 
		}
				
	}

	/**
	 * Get number of standard evaluators for handling participant submissions
	 * from configurations
	 * @return
	 */
	private int getEvaluatorCount() {
		String evaluatorCount = properties.getProperty("evaluatorCount","1");
		int count = 1;
		
		try {
			count = Integer.parseInt(evaluatorCount);
		} catch(NumberFormatException cause) {
			String message = "The evaluatorCount should be an integer." +
					"using 1 as default";
			LOGGER.log(Level.WARNING,message,cause);
		}

		return count;
	}

	/**
	 * Get a standard evaluation queue for processing participant submissions
	 * @return
	 */
	private EvaluationQueue getEvaluationQueue() {
		EvaluationQueue evaluationQueue = null;
		String masterHostName = properties.getProperty("masterHostName",null);
		
		if (isLocalhost(masterHostName)) {
			masterHostName = null;
		} else {

			try {
				InetAddress localhost = InetAddress.getLocalHost();
				InetAddress masterHost = InetAddress
						.getByName(masterHostName);

				if (localhost.equals(masterHost))
					masterHostName = null;
			} catch (UnknownHostException cause) {
				LOGGER.log(Level.SEVERE, "Detecting host address",
						cause.getMessage());
				masterHostName = null;
			}
		}

		
		if(masterHostName == null)
			try {
				evaluationQueue = StandardEvaluationQueue.getInstance();
			} catch (MooshakException cause) {
				LOGGER.log(Level.SEVERE, "Getting evaluation queue",cause);
			}
		else {
			MooshakWorker worker = new MooshakWorker();
			evaluationQueue = worker.getLoadBalancingService(masterHostName);
		}
		
		return evaluationQueue;
	}
	
	/**
	 * Check if host name string is local host.
	 * Null string is taken as local host.
	 * @param host
	 * @return
	 */
	private boolean isLocalhost(String host) {
		
		return "127.0.0.1".equals(host) || "localhost".equals(host); 
	}
	
	
	
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		process(request,response);
	}

	private void process(
			HttpServletRequest request,
			HttpServletResponse response) 
					throws ServletException, IOException {

		Writer out = response.getWriter();

		Map<String,String[]> httpParameters = request.getParameterMap();
		Map<String,String[]> headerValues = getHeaders(request);
		Map<String,String[]> globalParameters = new HashMap<>();
		Map<String,String[]> safeexecParameters = new HashMap<>();
		Map<String,String[]> systemProperties = new HashMap<>();
		Map<String,String[]> charsetParameters = new HashMap<>();
		Map<String,String[]> systemThreadInfo = new HashMap<>();
		Map<String,String[]> mainThreadInfo = new HashMap<>();
		Map<String,String[]> otherThreadInfo = new HashMap<>();
		
		globalParameters.put("homeDirectory",new String[] { 
				properties.getProperty("homeDirectory","?") });
		globalParameters.put("masterHostName",new String[] { 
				properties.getProperty("masterHostName","?")});

		Path safeexec = Paths.get(SafeExecution.getSafeexec());		
		String safeexecPermissions= "?";
		
		try {
			safeexecPermissions = SafeExecution.getSafeexecPermissions();
		} catch (Exception e) {}
		
		
		safeexecParameters.put("path",new String[] {
				safeexec.toString()});
		safeexecParameters.put("executable?",new String[] {
				(Files.isExecutable(safeexec)?"yes":"no")});
		safeexecParameters.put("owner",new String[] {
				Files.getOwner(safeexec).getName()});
		safeexecParameters.put("attributes",new String[]{safeexecPermissions});
				
		for(Object object: System.getProperties().keySet()) {
			String key = (String) object;
			systemProperties.put(key, new String[] { System.getProperty(key)});
		}

		charsetParameters.put("defaultCharset", new String[] { CHARSET.toString() });
		charsetParameters.put("fileEncoding", new String[] { System.getProperty("file.encoding") });
		charsetParameters.put("streamWriterDefaultCharset", new String[] { getDefaultCharset() });
		
		Map<Thread, StackTraceElement[]> threadMap = Thread.getAllStackTraces();
		List<Thread> threads = new ArrayList<>(threadMap.keySet());
		
		showThreads(threads.stream()
				.filter(t -> "main".equals(t.getThreadGroup().getName()))
				.collect(Collectors.toList()), mainThreadInfo);
		showThreads(threads.stream()
				.filter(t -> "system".equals(t.getThreadGroup().getName()))
				.collect(Collectors.toList()), systemThreadInfo);
		showThreads(threads.stream()
				.filter(t -> ! "main".equals(t.getThreadGroup().getName()) 
						  && ! "system".equals(t.getThreadGroup().getName()))
				.collect(Collectors.toList()), otherThreadInfo);
		
		response.setContentType("text/html");

		out.write("<!doctype html>\n");
		out.write("<html>\n");
		out.write("	<head><title>Mooshak info</title></head>\n");
		out.write("	<body>\n");
		out.write("		<h1>Mooshak info</h1>\n");		

		
		showNameValuePairs(out,"Global parameters",globalParameters);
		showNameValuePairs(out,"SafeExec",safeexecParameters);
		showNameValuePairs(out,"Header values",headerValues);
		showNameValuePairs(out,"System properties",systemProperties);
		showNameValuePairs(out,"HTTP parameters",httpParameters);
		showNameValuePairs(out,"Charset parameters", charsetParameters);
		showNameValuePairs(out,"Main threads",mainThreadInfo);
		showNameValuePairs(out,"System threads",systemThreadInfo);
		showNameValuePairs(out,"Other threads",otherThreadInfo);

		out.write("	</body>\n");
		out.write("</html>\n");
		out.close();

	}
	
	
	private void showThreads(List<Thread> threads,Map<String,String[]> info) { 
	
		for(Thread thread: threads)
			info.put(thread.getName(),new String[] {
				"priority: "+thread.getPriority()+" ",
				"state: "+thread.getState().toString()+" ",
				"group: "+thread.getThreadGroup().getName()
			});
	}

	private Map<String, String[]> getHeaders(HttpServletRequest request) {
		Map<String,String[]> headers = new HashMap<>();
		
		for(Enumeration<String> enumeration = request.getHeaderNames(); 
				enumeration.hasMoreElements();) {
					String name = enumeration.nextElement();
					headers.put(name,new String[] { request.getHeader(name) });
		}
				
		return headers;
	}


	private void showNameValuePairs(Writer out, String title, 
			Map<String,String[]> nameValuepairs) throws IOException {

		out.write("		<h2>"+title+" ("+nameValuepairs.size()+")"+"</h2>\n");
		out.write("		<table>\n");
		out.write("		<tr><th>Name</th><th>Value</th>\n");
		
		List<String> names = new ArrayList<>(nameValuepairs.keySet()); 
		Collections.sort(names,(a,b) -> a.compareTo(b));
		for(String name: names) {
			out.write("		<tr>\n");
			out.write("        <td>"+name+"</td>\n");
			out.write("        <td>");
			boolean first = true;
			for(String value: nameValuepairs.get(name)) {
				if(value == null)
					continue;
				if(! first) {
					out.write("<br>");
					first = false;
				}
				out.write(value);
			}
			out.write("        </td>\n");
			
			out.write("		</tr>\n");
		}
		out.write("		</table>\n");
	}
	

	

	/*-------------------------------------------------------------------*\
	 *							MIME types								 * 
	\*-------------------------------------------------------------------*/

	private static final String MIME_FILE = 
			"pt/up/fc/dcc/mooshak/server/mime.types";

	private static final HashMap<String,String> MIME = new HashMap<>();

	static { populateMimeTable(); }

	private static void populateMimeTable() {

		ClassLoader loader = Thread.currentThread().getContextClassLoader();

		try(
				InputStream stream = loader.getResourceAsStream(MIME_FILE);
				BufferedReader reader = 
						new BufferedReader(new InputStreamReader(stream));
				) {
			String line;
			while((line=reader.readLine()) != null) {
				line = line.trim();
				if("".equals(line) || line.startsWith("#"))
					continue;

				String[] part = line.split("\\s+");

				for(int i=1; i<part.length; i++)
					MIME.put(part[i],part[0]);
			}
		} catch (IOException cause) {
			LOGGER.log(Level.WARNING,"Error reading "+MIME_FILE,cause);
		}

	}

	/**
	 * Returns a MIME type for a file with a given extension 
	 * @param extension
	 * @return
	 */
	public static String getMime(String extension) {
		return MIME.get(extension);
	}
	
	/**
	 * Get default charset used in stream writer
	 * @return default charset used in stream writer
	 */
	private static String getDefaultCharset() {
        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
        return writer.getEncoding();
    }


}
