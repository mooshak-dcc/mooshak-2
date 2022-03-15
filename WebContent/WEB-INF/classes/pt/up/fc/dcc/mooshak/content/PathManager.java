package pt.up.fc.dcc.mooshak.content;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.shared.MooshakException;
/**
 * <p><code>PathManager</code> is a support class of 
 * <code>PersistentObject</code>. It provides a cache for persistent
 * objects indexed by their path. The methods to control the life-cycle
 * of persistent objects  available in this class must be used from
 * extensions of <code>PersistentObject</code>
 * </p>
 * 
 * <p>A singleton of this class manages all persistent object used in the
 *  application. A cache is used to promote reuse and prevent duplicate
 *  objects  bound to the same paths. The methods in this class
 *  control the life-cycle of a persistent object as in the following 
 *  example</p>
 * 
 * <pre>
 * 	PathManager manager = PathManager.getInstance();
 * 
 *  File path = new File(...);
 *  
 *  Foo foo1 = manager.create(path,Foo.class);
 * 
 *  Foo foo2 = manager.open(path)
 * 
 *  if(foo1.equals(foo2)) {
 *     // success
 *  }
 *  
 *  manager.save(path);
 *  
 *  manager.close(path)
 * 
 * </pre>
 * 
 * The PathManager is able to notify of changes in POs 
 * detected on the file system
 * 
 * 
 * @author José Paulo Leal zp@dcc.fc.up.pt
 *
 */
public final class PathManager extends PersistentObjectSender {
	
	
	private final static Logger LOGGER = Logger.getLogger("");
	
	private WatchService watchService;
	private Map<Path, PersistentObject> cache = 
		Collections.synchronizedMap(new PersistentObjectCache());
	
	private HashSet<Path> marks = new HashSet<>();
	
	private Map<String, Map<Integer, Runnable>> regularFileModificationWatchers = new HashMap<>();	
	private Map<String, Map<Integer, Runnable>> regularFileDeletionWatchers = new HashMap<>();

	
	/**
	 * Current maximum number of entries in cache
	 * @return the maxEntries
	 */
	 static int getMaxEntries() {
		return PersistentObjectCache.maxEntries;
	}

	/**
	 * Set maximum number of entries in cache.
	 * For unit testing purposes only 
	 * Use this for debugging purposes only
	 * @param maxEntries the maxEntries to set
	 */
	static void setMaxEntries(int maxEntries) {
		PersistentObjectCache.maxEntries = maxEntries;
	}

	/**
	 * Check if path is in cache
	 * Use this for debugging purposes only
	 * @param path
	 * @return
	 */
	static boolean check(Path path) {
		return manager.cache.containsKey(path);
	}
	
	
	/**
	 * Cache of persistent object with a maximum capacity
	 * @author José Paulo Leal zp@dcc.fc.up.pt
	 *
	 */
	private static class PersistentObjectCache
		extends LinkedHashMap<Path,PersistentObject> {
		
		private static final long serialVersionUID = 1L;
		private static final int INITIAL_CAPACITY = 5000;
		private static final float LOAD_FACTOR = 0.75F;
		private static final boolean ACCESS_ORDER = true;
		
		private static int maxEntries = 5*INITIAL_CAPACITY;
		
		PersistentObjectCache() {
			super(INITIAL_CAPACITY, LOAD_FACTOR, ACCESS_ORDER);
		}
		
		protected boolean removeEldestEntry(
				Map.Entry<Path,PersistentObject> eldest) {
			
			if(size() > maxEntries) {
				manager.mark(eldest.getKey());
				try {
					((PersistentObject) eldest.getValue()).save();
				} catch (MooshakException e) {
					String msg = "Removing eldest cache entry";
					LOGGER.log(Level.WARNING, msg , e);
				}
				return true;
			} else {
				return false;
			}
	    }
	}


	private PathManager() { 
		
		try {
			
			watchService = PersistentObject.FILE_SYSTEM.newWatchService();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					while (true) {
						try {
							WatchKey key = watchService.take();
							if (key.isValid() )
								processKey(key);
						} catch (InterruptedException cause) {
							String msg = "Interrupted while taking updates";
							LOGGER.log(Level.SEVERE, msg, cause);
							
						} catch (ClosedWatchServiceException cause) {
							String msg = "Terminated watch service";
							LOGGER.log(Level.INFO, msg);
							return;
						}
					}
				}
			
			},"File system watcher").start();
		
		} catch (IOException cause) {
			String message = "I/O error creating watch service.";
			
			if(System.getProperty("os.name","").startsWith("Linux")) 
				message +=
					"Consider executing the following command as root.\n"
					+ "\techo 100000 > /proc/sys/fs/inotify/max_user_instances";
			
			LOGGER.log(Level.SEVERE,message,cause);
		}	
		
	}
	
	/**
	 * Stop file system watch service. This will terminate the thread that is
	 * taking watch keys
	 */
	public void stopWatchingFS() {
		try {
			
			watchService.close();
			
		} catch (IOException cause) {
			LOGGER.log(Level.SEVERE,"Error closing FS watch service",cause);
		}
		
		 POSTPONE_PO_LOADING.cancel();
	}
	
	/**
	 * Watch a regular file for modifications events
	 * 
	 * @param file {@link String} path to file to watch
	 * @param modifyCallback {@link Runnable} modification callback
	 * @return {@link Runnable} callback to remove the attached modification callback 
	 */
	public Runnable watchRegularFileForModification(final String watchFile, Runnable modifyCallback) {
		
		final int key = (int) (Math.random() * Integer.MAX_VALUE);
		
		synchronized (regularFileModificationWatchers) {
			Map<Integer, Runnable> watchers = regularFileModificationWatchers
					.computeIfAbsent(watchFile, i -> new HashMap<>());			
			watchers.put(key, modifyCallback);
		}
		
		return () -> {
			
			synchronized (regularFileModificationWatchers) {				
				Map<Integer, Runnable> watchFileWatchers = regularFileModificationWatchers.get(watchFile);
				watchFileWatchers.remove(key);
			}
		};
	}
	
	/**
	 * Watch a regular file for deletion events
	 * 
	 * @param file {@link String} path to file to watch
	 * @param deleteCallback {@link Runnable} deletion callback
	 * @return {@link Runnable} callback to remove the attached deletion callback 
	 */
	public Runnable watchRegularFileForDeletion(final String watchFile, Runnable deleteCallback) {
		
		final int key = (int) (Math.random() * Integer.MAX_VALUE);
		
		synchronized (regularFileDeletionWatchers) {
			Map<Integer, Runnable> watchers = regularFileDeletionWatchers
					.computeIfAbsent(watchFile, i -> new HashMap<>());
			watchers.put(key, deleteCallback);
		}
		
		return () -> {
			
			synchronized (regularFileDeletionWatchers) {				
				Map<Integer, Runnable> watchFileWatchers = regularFileDeletionWatchers.get(watchFile);
				watchFileWatchers.remove(key);
			}
		};
	}
	
	
	String errorMessage = "Unmarshaling persistent object changed in the file system";

	final static Path DATA_PATH = Paths.get(PersistentObject.DATA_FILE);
	
	/**
	 * Process pending events associated with  watch key 
	 * @param key
	 */
	private void processKey(WatchKey key) {
		
		for (WatchEvent<?> event : key.pollEvents()) {
			Path fullPath = (Path) key.watchable();
			
			Path contextPath = null;
			Path fullContentPath = null;
			Kind<?> kind = event.kind();
			
			Object context = event.context();
			if(context instanceof Path) { 
				contextPath = (Path) context;
				fullContentPath = fullPath.resolve(contextPath);
			}
						
			try {
				if (kind.equals(StandardWatchEventKinds.ENTRY_CREATE)) {
					if(Files.isDirectory(fullContentPath))
						persistentObjectCreation(fullContentPath);
				} else if (kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)) {
					persistentObjectModification(fullPath);	
					if(Files.isDirectory(fullContentPath))
						persistentObjectModification(fullContentPath);
					else {
						
						Map<Integer, Runnable> fileWatchers = regularFileModificationWatchers
								.get(fullContentPath.toString());
						if (fileWatchers != null) {
							for (Integer fileWatchKey : fileWatchers.keySet())
								new Thread(fileWatchers.get(fileWatchKey)).run();
						}
					}
				} else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
					Path deleted = PersistentCore.getRelativePath(fullContentPath);
					
					synchronized (cache) {
						if(cache.containsKey(deleted)) {
							cache.get(deleted).notifyParentRemoteCopies();
						}
					}
					forget(deleted);
					
					if (deleted.toFile().isFile()) {
						
						Map<Integer, Runnable> fileWatchers = regularFileDeletionWatchers
								.get(fullContentPath.toString());
						if (fileWatchers != null) {
							for (Integer fileWatchKey : fileWatchers.keySet()) 
								new Thread(fileWatchers.get(fileWatchKey)).run();
						}
					}
				}

			} catch(NullPointerException | MooshakContentException cause) {
				LOGGER.log(Level.SEVERE, errorMessage,cause);
			}
		} 
		
		key.reset();
	}
	
	private static final Timer POSTPONE_PO_LOADING = 
			new Timer("Postpone PO loading");
	private static final long POSTPONE_PO_LOADING_DELAY = 200;
	
	private void persistentObjectCreation(Path fullPath) {
		Path path = PersistentCore.getRelativePath(fullPath);

		if (! marks.contains(path) && ! cache.containsKey(path)) {

			POSTPONE_PO_LOADING.schedule(new TimerTask() {

				@Override
				public void run() {
					try {
						System.out.println("loading postponed:"+fullPath);
						loadAndBroadcast(fullPath);
					} catch( MooshakContentException cause) {
						LOGGER.log(Level.SEVERE, errorMessage,cause);
					}
				}
				
			},POSTPONE_PO_LOADING_DELAY);
			
		}
	}

	/**
	 * Process modification events on a given path 
	 * @param path
	 * @throws MooshakContentException 
	 */
	private void persistentObjectModification(Path fullPath) 
			throws MooshakContentException {
		
			Path path = PersistentCore.getRelativePath(fullPath);
		
			if(! marks.contains(path)) {
				if (cache.containsKey(path)) {
					PersistentObject persistent = cache.get(path);
					if(persistent.isIgnoringFileSystemNotifications()) {
						// Ignore notifications to this PO
					} else {
						if(persistent.inFileSystem()) {
							cache.get(path).unmarshal();
							LOGGER.log(Level.INFO,"Reloading "+path);
						} 
					} 
				} else { // not in cache; modifications made be a third party
					loadAndBroadcast(fullPath);
				}
			}
		
	}
	
	/**
	 * If this the full path has a PersistentObject then broadcast it
	 * @param path
	 * @throws MooshakContentException
	 */
	private void loadAndBroadcast(Path fullPath) throws MooshakContentException {
		
		if(PersistentObject.isPersistentObjectAbsolutePath(fullPath)) {
			Path path = PersistentCore.getRelativePath(fullPath);
			
			System.out.println("\tloading:"+ path);
			PersistentObject po = PersistentObject.open(path);
			LOGGER.log(Level.INFO,"broadcasting "+path);
			broadcat(po);
		}
	}


	/**
	 * Single instance of this class
	 */
	protected static PathManager manager = null;
	/**
	 * Returns a single instance of this class
	 * @return
	 * @throws MooshakException
	 */
	public synchronized static PathManager getInstance() {
		
		if (manager == null)
			manager = new PathManager();
		return  manager;
	}
	
	/**
	 * Number of persistent objects in cache
	 * @return
	 */
	public static int getPersistentObjectCount() {
		if(manager == null)
			return 0;
		else
			return manager.cache.size();
	}
	
	/*----------------------------------------------------------------------*
	 *  cache management methods                                            *
	 *----------------------------------------------------------------------*/

	/**
	 * Mark a path as being created
	 * @param path
	 */
	protected void mark(Path path) {
		synchronized (cache) {
			marks.add(path);
		}
	}
	
	/**
	 * Stores persistent object in cache using path as watchKey
	 * @param path
	 * @param persistent
	 * @throws MooshakContentException 
	 */
	protected void store(Path path, PersistentObject persistent) 
		throws MooshakContentException {
		
		synchronized (cache) {
			cache.put(path, persistent);
			marks.remove(path);
		}
		
		persistent.watchKey = register(path);
	}
	
	private WatchKey register(Path path) throws MooshakContentException {
		Path absolute = PersistentCore.getAbsoluteFile(path);
		
		try {
			return absolute.register(watchService, 
					StandardWatchEventKinds.ENTRY_CREATE, 
					StandardWatchEventKinds.ENTRY_DELETE, 
					StandardWatchEventKinds.ENTRY_MODIFY);
		} catch (IOException cause) {
			String msg = "Registering events for "+absolute +"\n" +
			 "Consider raising the max number of inodes\n\n" +
			"\t sysctl -w fs.inotify.max_user_watches=100000 \n";
			throw new MooshakContentException(msg,cause);
		}
	}
	
	/**
	 * Returns persistent object with given path if it exists in cache.
	 *
	 * @param path of directory where data on this object is persisted
	 * @return reused persistent object or null path not in cache
	 */
	protected PersistentObject retrieve(Path path) {
		if(cache.containsKey(path))
			return cache.get(path);
		else
			return null;
	}
	
	/**
	 * Clears path from cache id defined
	 * @param path
	 */
	protected void forget(Path path) {
		synchronized (cache) {
			if(cache.containsKey(path)) {
				WatchKey watchKey = cache.get(path).watchKey;
				
				if(watchKey != null)
					watchKey.cancel();		
				cache.remove(path);	
			}
		}
	}
	
	/**
	 * Before termination of the JVM all watch keys are closed
	 */
	protected void finalize() {
		for(Path path: cache.keySet()) {
			cache.get(path).watchKey.cancel();
		}
	}	
	
}
