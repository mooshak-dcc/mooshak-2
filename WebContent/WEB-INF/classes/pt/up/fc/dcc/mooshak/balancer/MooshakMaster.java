package pt.up.fc.dcc.mooshak.balancer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.security.InvalidParameterException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.CustomData;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationQueue;
import pt.up.fc.dcc.mooshak.evaluation.StandardEvaluationQueue;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

public class MooshakMaster extends UnicastRemoteObject 
	implements LoadBalancingService{
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger("");
	private static final String PROPERTIES_FILENAME = "MooshakProperties.xml";
	
	private Properties properties = new Properties();
	private PersistentObject data;
	private EvaluationQueue evaluationQueue = null;

	public static void main(String[] args) throws RemoteException  {		

		assert System.getProperty("java.security.policy") != null : 
			"Server policy not found\n"
			+ "Check if '-Djava.security.policy=server.policy'"
			+ " is in -VM args";

		initRMI(Paths.get("WebContent").toAbsolutePath());
	}

	private static void initRMI(Path base)     {
		System.out.println("RMI server started");

		if (System.getSecurityManager() == null) 
			System.setSecurityManager(new SecurityManager());
		else
			LOGGER.info("Security manager already exists.");

		try {
			LocateRegistry.createRegistry(RMI_PORT);
		} catch (RemoteException e) { 
			//ignore: exception simply means that registry already exists
			LOGGER.info("RMI registry already exists.");
		}

		try {	            
			MooshakMaster master = new MooshakMaster(base);	
			Naming.rebind(RMI_URL,master);

		} catch (Exception cause) {
			LOGGER.log(Level.SEVERE,"RMI server exception:",cause);
		}
	}

	protected MooshakMaster(Path base) throws IOException {
		super();
		Path parent = base.getParent();
		
		if(parent == null)
			throw new InvalidParameterException("base without parent");
		
		Path propertiesPath = parent.resolve(PROPERTIES_FILENAME);
		
		try(InputStream stream = Files.newInputStream(propertiesPath)) {
			properties.loadFromXML(stream);
		} catch (IOException cause) {
			LOGGER.log(Level.WARNING,"Error loading properties",cause);
		}
		
		try {
			evaluationQueue = StandardEvaluationQueue.getInstance();
		} catch (MooshakException cause) {
			LOGGER.log(Level.SEVERE,"Error getting evaluation queue",cause);
		}
		

		String relHome = properties.getProperty("homeDirectory",CustomData.HOME);
		String home = base.resolve(relHome).toString();
		
		PersistentObject.setHome(home);
		SafeExecution.setWebInf(base.resolve("WEB-INF"));

		try {
			data = PersistentObject.openPath("data");
		} catch (MooshakContentException cause) {
			throw new RemoteException("Error setting root",cause);
		}

	}

	private static String DATA = "data";
	private static String PATH_PREFIX = DATA+"/";
	private static int PATH_PREFIX_LENGTH = PATH_PREFIX.length();
	
	@Override
	public <T extends PersistentObject> T getPersistentObject(String path) 
			throws RemoteException{
		T persistent = null ;
		
		if(path==null)
			throw new RemoteException("");
		else if(path.startsWith(PATH_PREFIX)) {	
			try {
				persistent = data.open(path.substring(PATH_PREFIX_LENGTH));
			} catch (MooshakContentException cause) {
				throw new RemoteException("Error openning "+path,cause);
			}
			return persistent;
		} else if(path.equals(DATA)) {
			@SuppressWarnings("unchecked")
			T t = (T) data;
			return t;
		} else throw new RemoteException("Path doesn't start with \""+DATA+"\"");
	}

	/**
	 * This method isn't implemented since workers cannot send 
	 * evaluation request to masters. 
	 * 
	 * In the future it may be interesting to allow masters to send 
	 * evaluations requests among themselves
	 */
	@Override
	public void enqueueEvaluationRequest(EvaluationRequest request) {}

	@Override
	public EvaluationRequest dequeueEvaluationRequest() {
		
		EvaluationRequest request = null;
		
		try {
			request = evaluationQueue.dequeueEvaluationRequest();
		} catch (MooshakException cause) {
			LOGGER.log(Level.SEVERE,"Master: Dequeuing evaluation request",cause);
		}
		
		return request;
	}

	@Override
	public void concludeEvaluation(Submission submission) {
		
		try {
			if(submission != null)
				submission.save();
		} catch (MooshakContentException cause) {
			LOGGER.log(Level.SEVERE,"Error saving submission sent by RMI",cause);
		}
		
		try {
			evaluationQueue.concludeEvaluation(submission);
		} catch (MooshakException cause) {
			 LOGGER.log(Level.SEVERE,"Sending concluded evaluation",cause);
		}
	}

}
