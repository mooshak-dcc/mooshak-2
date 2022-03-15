/**
 * 
 */
package pt.up.fc.dcc.mooshak.content;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <p>Special persistent object that contains other objects of 
 * PersistentObject extension defined as a type parameter.
 * A persistent container may have containers of its own type.</p>
 * 
 * 
 * <p>Persistent objects (PO) containers are iterable using PO streams. 
 * These streams implement the {@code Iterable} and {@code Closeable}, thus
 * they should be retrieved in try-with-resources, so that they are 
 * automatically closed when no longer needed, and used with extended loops,
 * as in the following example.</p> 
 *
 * <pre>
 * Container<Submission> submissions;
 * ...
 * try(POStream poStream = container.getPOStream()) {
 * 		for(Submission submission: poStream) {
 * 			...
 * 		}
 * }
 * </pre>
 * 
 * <p>
 * The {@code getContent()} method retrieves all content
 * of this container, including sub-containers.  </+<
 * 
 * The {@code find()} method retrieves an object in this container, 
 * including sub-containers of the same type,
 * with given idName (last name in id Path).   
 * 
 * <b>Note</b>: Iteration does not raise exceptions. I/O 
 * and Mooshak content exceptions are just logged. 
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */

public class PersistentContainer<T extends PersistentObject> 
	extends PersistentObject {

	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger("");
	
	/**
	 * Returns type of the PersistentObjects contained in this container
	 * @return the class of contained objects
	 */
	public Class<T> getContainedClass() {
		return getContainedClass(getClass());
	}
	
	/**
	 * Returns type of the PersistentObjects childs of this class
	 * @return the class of child objects of this class
	 */
	@SuppressWarnings({ "unchecked", "hiding" })
	public <T extends PersistentObject> Class<T> getDescendantType() {
	     return (Class<T>) getContainedClass();
	}

	/**
	 * Static method to compute a contained class of a persistent object class
	 * using reflection. Returns {@code null} if given class is not of type
	 * {@code PersistentContainer<T>}.
	 * @param clazz
	 * @return
	 */
	public static <T extends PersistentObject> 
		Class<T> getContainedClass(Class<?> clazz) {
		
		if(PersistentContainer.class.isAssignableFrom(clazz)) {
		
			Type genericSuperClass = clazz.getGenericSuperclass();
		
			if(genericSuperClass instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) genericSuperClass;
			
				Type[] param = paramType.getActualTypeArguments();
			
				if(param.length == 1) {
					@SuppressWarnings("unchecked")
					Class<T> type = (Class<T>) param[0];
					return type;
				} else
					return null;
			} else
				return null;
		} else
			return null;
	}
	
	
	/**
	 * Returns a list of <code>PersistentObjects</code> hold by this container,
	 * including those in sub containers of the same type. 
	 * @return list of POs
	 */
	public List<T> getContent() {
		ArrayList<T> content = new ArrayList<>();
		
		try(POStream<T> stream = newPOStream()) {
			for(T persistent: stream)
				content.add(persistent);
		} catch (Exception cause) {
			String message = "Error processing PO stream:"
					+cause.getMessage();
			LOGGER.log(Level.SEVERE,message,cause);
		}
		
		return content;
	}
	
	/**
	 * Fast-expiration cache of child objects, with a maximum capacity, to prevent
	 * successive walks on the same tree
	 * 
	 * @author José Carlos Paiva <code>josepaiva94@gmail.com</code>
	 */
	private static class ChildCache<T> extends LinkedHashMap<String, T> {		
		private static final long serialVersionUID = 1L;
		
		private static final int INITIAL_CAPACITY = 500;
		private static final float LOAD_FACTOR = 0.75F;
		private static final boolean ACCESS_ORDER = true;
		
		private static final long MAX_EXPIRY_TIME = 10 * 1000;
		
		private static int maxEntries = 5*INITIAL_CAPACITY;
		
		private Map<String, Long> expiryTimes = new HashMap<>(INITIAL_CAPACITY);
		
		ChildCache() {
			super(INITIAL_CAPACITY, LOAD_FACTOR, ACCESS_ORDER);
		}
		
		protected boolean removeEldestEntry(
				Map.Entry<String, T> eldest) {
			
			if(size() > maxEntries) {
				return true;
			} else {
				return false;
			}
	    }
		
		@Override
		public T get(Object key) {
			
			Long expiryTime = expiryTimes.get(key);
			
			if (expiryTime == null || expiryTime < new Date().getTime()) {
				remove(key);
				return null;
			}
			
			return super.get(key);
		}
		
		@Override
		public T put(String key, T value) {
			T t = super.put(key, value);
			
			expiryTimes.put(key, new Date().getTime() + MAX_EXPIRY_TIME);
			
			return t;
		}
		
		@Override
		public T remove(Object key) {
			expiryTimes.remove(key);
			return super.remove(key);
		}
	}
	
	private ChildCache<T> childCache = new ChildCache<>(); 

	/**
	 * Finds a persistent object in this container, or in its
	 * sub-containers of the same type, with given idName (last name in id path)
	 * @param idName of persistent object (last name in path) 
	 * @return persistent object with given id, or {@code null} if not found
	 * @throws MooshakContentException if more than one PO has given id, or
	 * 								   an IO error occurs
	 */
	public T find(final String idName) throws MooshakContentException  {
		
		/*T found = childCache.get(idName);
		if (found != null)
			return found;*/
	
		final List<T> paths = new ArrayList<>();
		final Class<T> type = getContainedClass();
		T found = null;
		/*RecursiveFindWalk w = new RecursiveFindWalk(paths, type, getAbsoluteFile(), idName);
        ForkJoinPool p = new ForkJoinPool();
        p.invoke(w);*/
		
		try {
			Files.walkFileTree(getAbsoluteFile(), new SimpleFileVisitor<Path>() {
				
				@Override
				public FileVisitResult preVisitDirectory(Path dir, 
						BasicFileAttributes attrs)
                          throws IOException {
					
					Path fileName = dir.getFileName();
					
					if(fileName != null && fileName.toString().equals(idName)) {
						try {
							T candidate = open(getRelativePath(dir));
							
							if(candidate.getClass().equals(type)) {
								
								childCache.put(fileName.toString(), candidate);
								
								paths.add(candidate);
								return FileVisitResult.SKIP_SUBTREE;
							} else
								return FileVisitResult.CONTINUE;
							
						} catch (MooshakContentException cause) {
							String message = "Loading PO in find()";
							LOGGER.log(Level.WARNING,message,cause);
							return FileVisitResult.CONTINUE;
						}
							
					} else
						return FileVisitResult.CONTINUE;
				}
			} );
		} catch (IOException cause) {
			String message = "Error walking file tree "+path+
					" for finding "+idName+ "\n in file: "+ cause.getMessage();
			LOGGER.log(Level.SEVERE,message);
			// Just log error to avoid request from failing
			// throw new MooshakContentException(message,cause);
		}
		
		switch(paths.size()) {
		case 0:
			break;
		case 1:
			found = paths.get(0); 
			break;
		default:
			throw new MooshakContentException("Several "+idName+" in"+path);
		}
		
		return found;
	}
	
	public interface POStream<T> extends Iterable<T>, AutoCloseable {}

	/**
	 * Creates a {@code POStream<T>} to iterate over the POs in this
	 * container. These include POs in sub containers. The stream must be 
	 * closed to free its resources. The correct idiom to use streams is
	 * the following, in this example with iteration over submissions.
	 * <pre>
	 * Container&lt;Submission&gt; submissions;
	 * ...
	 * try(POStream poStream = container.getPOStream()) {
	 * 		for(Submission submission: poStream) {
	 * 			...
	 * 		}
	 * } 
	 * </pre>
	 * 
	 * @return POStream<T>;
	 */
	public POStream<T> newPOStream() {
		
		return new POStream<T>() {
			POIterator poIterator = null;
			
			@Override
			public Iterator<T> iterator() {
				return poIterator = new POIterator();
			}

			@Override
			public void close() throws Exception {
				if(poIterator != null)
					poIterator.close();
			}
			
		};
	};
	
	
	private class RecursiveFindWalk extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		
		private List<T> paths;
		private Class<T> type;
		private Path dir;
		private String target;
		
		public RecursiveFindWalk(List<T> paths, Class<T> type, Path dir, String target) {
			this.paths = paths;
			this.type = type;
			this.dir = dir;
			this.target = target;
		}

		@Override
		protected void compute() {
			final List<RecursiveFindWalk> walks = new ArrayList<>();
            try {
                Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                	
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, 
                    		BasicFileAttributes attrs) throws IOException {
            	
						Path fileName = dir.getFileName();
						
						if(fileName != null && fileName.toString().equals(target)) {
							try {
								T candidate = open(getRelativePath(dir));
								
								if(candidate.getClass().equals(type)) {
									
									childCache.put(fileName.toString(), candidate);
									
									synchronized (paths) {
										paths.add(candidate);
									}
									return FileVisitResult.SKIP_SUBTREE;
								} 								
							} catch (MooshakContentException cause) {
								String message = "Loading PO in find()";
								LOGGER.log(Level.WARNING,message,cause);
								return FileVisitResult.CONTINUE;
							}
						} 
						
						if (dir.equals(RecursiveFindWalk.this.dir))
							return FileVisitResult.CONTINUE;
						
                        RecursiveFindWalk w = new RecursiveFindWalk(paths, type, 
                        		getAbsoluteFile().resolve(dir), target);
                        w.fork();
                        walks.add(w);
                        
						return FileVisitResult.SKIP_SUBTREE;
                    }
                });
            } catch (IOException cause) {
    			// Just log error to avoid request from failing
    			// throw new MooshakContentException(message,cause);
    			String message = "Error walking file tree "+path+
    					" for finding "+target+ "\n in file: "+ cause.getMessage();
    			LOGGER.log(Level.SEVERE,message);
            }

            for (RecursiveFindWalk w : walks)
                w.join();
            
            quietlyComplete();
		}
		
	}
	
	/**
	 *  Iterator over PO in this container, including sub-containers of the
	 *  same type
	 **/
	private class POIterator implements Iterator<T> {
		private LinkedList<Iterator<Path>> fifo = new LinkedList<>();
		private Class<T> containedClass = getContainedClass();
		private Map<Iterator<Path>,DirectoryStream<Path>> streamOf = 
				new HashMap<>();
		private T next;
		
		POIterator()  {
			offerIteratorFor(getAbsoluteFile());
		}
		
		@Override
		public boolean hasNext() {
			while(fifo.peek() != null) {
				if(fifo.peek().hasNext()) {
					Path absolutePath = fifo.peek().next();
					Path relativePath = getRelativePath(absolutePath);
					PersistentObject persistentObject = null;
					
					try {
						 persistentObject = open(relativePath);
					} catch (MooshakContentException cause) {
						LOGGER.log(Level.SEVERE,"opening on iteration",cause);
					}
					if(persistentObject == null)
						continue;
					
					if(persistentObject.isFrozen())
						continue;
					
					if(persistentObject.getClass().equals(containedClass)) {
						@SuppressWarnings("unchecked")
						T checked = (T) persistentObject;
						next = checked;
						return true;
					} else if(persistentObject instanceof PersistentContainer) {
						PersistentContainer<?> container = 
								(PersistentContainer<?>) persistentObject;
						if(container.getContainedClass().equals(containedClass))		
							offerIteratorFor(absolutePath);
					}
					
				} else 
					closeHeadIteratorStream(false);
			}
			return false;
		}

		@Override
		public T next() {
			return next;
		}

		@Override
		public void remove() {
			// not implemented yet			
		}
		
		/**
		 * Close all remaining streams and release their resources
		 */
		void close() {
			while(! fifo.isEmpty())
				closeHeadIteratorStream(true);
				
		}
			
		/**
		 * Close stream of iterator at head of the stack 
		 */
		private void closeHeadIteratorStream(boolean raiseException) {
			Iterator<Path> iterator = fifo.poll();
			
			try {
				streamOf.get(iterator).close();
			} catch (IOException cause) {
				String message = "Error closing stream on PO iteration:"
						+cause.getMessage();
				
				if(raiseException)
					throw new RuntimeException(message,cause);
				else
					LOGGER.log(Level.SEVERE,message,cause);
			}
		}
		
		/**
		 * Create a directory stream for given path and add it to the FIFO.
		 * The stream returns only directories holding persistent objects
		 * @param path
		 */
		private void offerIteratorFor(Path path) {
			Iterator<Path> iterator = null; 
			DirectoryStream<Path> stream; 
			
			Filter<Path> filter = new DirectoryStream.Filter<Path>() {
				// select directories containing a file defining its type
				@Override
				public boolean accept(Path entry) throws IOException {
					boolean accept = false;
					
					accept = Files.isDirectory(entry)
					&& Files.exists(entry.resolve(TYPE_FILE));
					
					return accept;
				}
			};
			try { 
				stream = Files.newDirectoryStream(path,filter);
				iterator = stream.iterator();
				streamOf.put(iterator,stream);
			} catch (IOException cause) {
				String message = "Error opening stream for PO iteration"+
						cause.getMessage();
				LOGGER.log(Level.SEVERE,message,cause);
			}
					
			if(iterator != null)
				fifo.offer(iterator);
			
		}
	}
	
	
	@Override
	public boolean isRenameable() {
		return false;
	}

}
