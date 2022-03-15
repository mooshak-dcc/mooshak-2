package pt.up.fc.dcc.mooshak.content;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchKey;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import pt.up.fc.dcc.mooshak.content.Attributes.Attribute;
import pt.up.fc.dcc.mooshak.content.util.Serialize;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.events.ObjectUpdateEvent;


/**
 * A common type to all persistent content types. Fields of these
 * persistent types are stored in plain text files in the file system in
 * the original TCL format used by Mooshak 1.*. 
 * 
 * A persistent fields in an extension of this class must have
 * <ol>
 *  <li> {@code @MooshakAttribute} annotation
 * 	<li> {@code private} visibility </li>
 *  <li> a complex type (replace simple types by wrappers)</li>
 *  <li> {@code null} as default </li>
 *  <li> getter and setter.
 * </ol>
 * 
 *Getters and setters for field fields should
 *
 *<ol>
 *	<li> replace {@code null} by a default (if possible)
 *	<li> return and receive simple types
 *	<li> dereference paths (use {@code openRelative()} method)
 *  <li> be tested in JUnit classes 
 *</ol>
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 *
 */
public class PersistentObject extends PersistentCore implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static final EventSender EVENT_SENDER=EventSender.getEventSender();
	
	static {
		PathManager.getInstance()
			.addPersistentObjectListener(PersistentObject.class, 
					new PersistentObjectListener<PersistentObject>() {

						@Override
						public void receivedPersistentObject(
								PersistentObject persistent)
								throws MooshakContentException {
							
							persistent.notifyMyRemoteCopies();
							persistent.notifyParentRemoteCopies();
						}
			});
	}
	
	protected Path path = null;
	transient WatchKey watchKey = null;
	
	private boolean frozen = false;
	
	
	/*--------------------------------------------------------------------*\
	 *         Custom serialization due to sun.nio.fs.UnixPath            *
	\*--------------------------------------------------------------------*/
	
	/**
	 * Marshal PO state (Path path) which is not a serializable type.
	 *  
	 * @param out
	 * @throws IOException
	 */
	 private void writeObject(ObjectOutputStream out)
		     throws IOException {
		 
		 Serialize.writePath(path,out);
	 }

	 /**
	  * Unmarshal PO state (Path path) which is not a serializable type.
	  * @param in
	  * @throws IOException
	  * @throws ClassNotFoundException
	  */
	 private void readObject(ObjectInputStream in)
			 throws IOException, ClassNotFoundException {

		 path = Serialize.readPath(in);
	 }
	
	 // private void readObjectNoData() throws ObjectStreamException {}

	 
	 @SuppressWarnings("unchecked")
	 public <T extends PersistentObject> T copy() throws MooshakContentException {
		 T persistentObject=null;
		 byte[] buffer;

		 try(
				 ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
				 ObjectOutputStream out = new ObjectOutputStream(outBytes); 
				 ) {
			 out.writeObject(this);
			 buffer = outBytes.toByteArray();
		 } catch (IOException cause) {
			 throw new MooshakContentException("Copying out object",cause);
		 }


		 try(
				 ByteArrayInputStream inBytes = new ByteArrayInputStream(buffer);
				 ObjectInputStream in = new ObjectInputStream(inBytes)) {

			 persistentObject = (T) in.readObject();


		 } catch(ClassNotFoundException | IOException cause) {
			 throw new MooshakContentException("Copying int object",cause);
		 }

		 return persistentObject;
	 }
 
	 
	/*--------------------------------------------------------------------*\
	 * Ignore file system notifications for this/given persistent object  *
	\*--------------------------------------------------------------------*/
	
	 
	private static final Timer STOP_IGNORING_TIMER = 
			new Timer("Stop ignoring timer");
	public static final long PROTECTED_DELAY = 200;
	
	private int ignoreFSNotifications = 0;
	
	/**
	 * A functional interface similar to runnable but able to throw exceptions 
	 *
	 * @author José Paulo Leal <zp@dcc.fc.up.pt>
	 */
	@FunctionalInterface
	public interface IgnoreFSNotificationExecution<T extends Exception> {
		void run() throws T;
	}

	/**
	 * Ignore file system notifications on this PO while executing this method.
	 * Notifications start being ignored immediately before executing command
	 * and continue for a short delay after its completion.
	 * 
	 * @param runnable	 the method to execute
	 */
	public <T extends Exception> void  executeIgnoringFSNotifications(
				IgnoreFSNotificationExecution<T> runnable) 
			throws T {
		
		synchronized (this) {
			ignoreFSNotifications++;
		}
		
		runnable.run();
		
		synchronized (this) {
			if(ignoreFSNotifications > 1)
				ignoreFSNotifications--;
			else
				STOP_IGNORING_TIMER.schedule(new TimerTask() {
						@Override
						public void run() {
							ignoreFSNotifications--;
							cancel();
						}
				}, PROTECTED_DELAY );
		}
	}

	/**
	 * Ignore file system notifications on these PO while executing given method.
	 * Notifications start being ignored immediately before executing command
	 * protected mode and continue for a short delay after its completion.
	 * 
	 * @param runnable		the	method to execute
	 * @param persistents 	list of persistent objects to be ignored
	 */
	static public <T extends Exception>  void  executeIgnoringFSNotifications(
			IgnoreFSNotificationExecution<T> runnable,
			List<PersistentObject> persistents) 
					throws T {


		for(PersistentObject persistent: persistents) {			
			synchronized (persistent) {
				persistent.ignoreFSNotifications++;
			}
		}

		runnable.run();

		for(PersistentObject persistent: persistents) {
			synchronized (persistent) {
				if(persistent.ignoreFSNotifications > 1)
					persistent.ignoreFSNotifications--;
				else
					STOP_IGNORING_TIMER.schedule(new TimerTask() {
						@Override
						public void run() {
							persistent.ignoreFSNotifications--;
							cancel();
						}
					}, PROTECTED_DELAY );
			}
		}
		
	}
	
	
	/**
	 * Checks if this PO is ignoring file system notifications
	 * @return
	 */
	public boolean isIgnoringFileSystemNotifications() {
			
			return ignoreFSNotifications > 0;
	}
	
	
	/*********************************************************************\
	 *  Count the number of threads using this PO in safe mode.          *
	 *  Safe mode allows changing directories without processing updates * 
	\*********************************************************************/
	
	/*
	 * Number of thread using this objects in safe mode 
	 */
	@Deprecated
	private transient int updateCount = 0;
	
	/**
	 * No one is using this PO in safe mode?
	 * @return
	 */
	@Deprecated
	synchronized boolean isUpToDate() {
		
		if(updateCount == 0)
			return true;
		else {
			updateCount--;
			return false;
		}
	}
	
	/**
	 * @return the frozen
	 */
	public boolean isFrozen() {
		return frozen;
	}

	/**
	 * @param frozen the frozen to set
	 */
	public void setFrozen(boolean frozen) throws MooshakException {
		this.frozen = frozen;
		
		executeIgnoringFSNotifications( () -> marshal() );	
	}

	/**
	 * Increment the number of threads using this PO in safe mode
	 */
	@Deprecated
	public synchronized void incrementUpdateCount() {
		
		updateCount++;
	}
	
	/**
	 * Increment update nBuffer of a given absolute path
	 * @param absolute
	 */
	@Deprecated
	static public void incrementUpdateCount(Path absolute) {
		Path path = PersistentCore.getRelativePath(absolute);
		
		PersistentObject persistent = manager.retrieve(path);
		if(persistent != null)
			persistent.incrementUpdateCount();
	}
	

	/**
	 * The path of this persistent object relative to the data directory
	 * @return
	 */
	final public Path getPath() {
		return path;
	}

	/**
	 * Get the absolute file for this PersistentObject path.
	 * The file returned by this method should be used whenever a file
	 * in this PersistentObject needs to be written or read. 
	 * @param name
	 * @return
	 */	
	public Path getAbsoluteFile() {
		// TODO change method name
		return getAbsoluteFile(path);
	}

	/**
	 * in this PersistentObject path.
	 * The file returned by this method should be used whenever a file
	 * in this PersistentObject needs to be written or read. 
	 * @param name
	 * @return
	 */
	public Path getAbsoluteFile(String name) {
		// TODO change method name
		return getAbsoluteFile(path,name);
	}
		
	/**
	 * The last part of object' path, a relative file in its container
	 * @return
	 */
	public Path getId() {
		return path.getFileName();
	}
	
	
	/**
	 * The last part of object' path as string
	 * @return
	 */
	public String getIdName() {
		return getId().toString();
	}
	
	/*----------------------------------------------------------------------*\
	 *  Life-cycle methods & hooks        		                            *
	\*----------------------------------------------------------------------*/	
	
	private static final Set<PosixFilePermission> PO_PERMS = 
			new HashSet<PosixFilePermission>();
	private static final FileAttribute<Set<PosixFilePermission>>  PO_ATTRS;
	
	static {
			PO_PERMS.add(PosixFilePermission.OWNER_READ);
			PO_PERMS.add(PosixFilePermission.OWNER_WRITE);
			PO_PERMS.add(PosixFilePermission.OWNER_EXECUTE);
			PO_PERMS.add(PosixFilePermission.GROUP_EXECUTE);
			PO_PERMS.add(PosixFilePermission.OTHERS_EXECUTE);
			
			PO_ATTRS = PosixFilePermissions.asFileAttribute(PO_PERMS);
		}

	
	/**
	 * Initialize persistent object after its creation 
	 * @param path
	 * @throws MooshakContentException
	 */
	final void initCreate(Path path) throws MooshakContentException {

		try {
			Files.createDirectories(getAbsoluteFile(path),PO_ATTRS);
		} catch (IOException cause) {
			throw new MooshakContentException(
					"Could not make directory for "+ path,
					cause);
		}
				
		this.path = path;
		
		try {
			executeIgnoringFSNotifications( () -> {
					setType();
					marshal();
					
					created();			
			});
		} catch (Exception cause) {
			throw new MooshakContentException(cause);
		}
		
		notifyParentRemoteCopies();
	}
	
	/**
	 * Initialize persistent object after opened by data manager 
	 * @param path
	 * @throws MooshakContentException
	 */
	final void initOpen(Path path) throws MooshakContentException {
		this.path=path;

		try {
			Files.createDirectories(getAbsoluteFile(path));
		} catch (IOException cause) {
			throw new MooshakContentException(
					"Could not make directory for "+ path,
					cause);
		}
		unmarshal();
		created();
	}
	
	
		
	/**
	 * Convenience method for opening an absolute path as string
	 * @param name
	 * @return
	 * @throws MooshakContentException
	 */
	public static final <T extends PersistentObject> T
		openPath(String name) throws MooshakContentException  {
		
		Path path;
		
		if(name.startsWith("/"))
			path = Paths.get(name.substring(1));
		else
			path	= Paths.get(name);
		
		
		return open(path);
	}
	
	/**
	 * Opens an existing persistent object bound to this path
	 * An already opened persisting object is returned if available. 
	 * Otherwise an instance of an extension of PersistentObject 
	 * is created and the data on path is loaded to this object.
	 * @param path
	 * @return
	 * @throws MooshakContentException
	 */
	public static final <T extends PersistentObject> T 
		open(Path path) throws MooshakContentException {
		
		T persistent;
		
		@SuppressWarnings("unchecked")
		T retrieve = (T) manager.retrieve(path);
		
		if((persistent = retrieve) == null) {
			Class<T> type = loadTypeFrom(path);
			persistent = PersistentObject.newInstance(type);
			persistent.initOpen(path);
			
			manager.store(path, persistent);
		} 
		return persistent;
	}
	
	/**
	 * Saves a persistent object in the file system.
	 *  
	 * @throws MooshakContentException if raised by marshall
	 */
	public final void save() throws MooshakContentException {
		
		if(isFrozen())
			return;
		
		executeIgnoringFSNotifications( () -> { 
			
			if(updated())
				getParent().save();
			
			backup(); 
			marshal(); 
		
		} );	
		
		notifyMyRemoteCopies();
	}
	
	
	/**
	 * Backup data when saving. 
	 * This method may need to be redefined to disable backup for some types,
	 * such as session.  
	 */
	protected void backup() throws MooshakContentException {

		BackupObject backup = BackupObject.getBackupObject(getIdName());
		PersistentObject parent = getParent();

		if(parent != null)
			backup.record(parent.getAbsoluteFile());
	}
	
	
	
	/**
	 * Reopen this persistent data from the file system.
	 * Useful for debugging in unit tests
	 * @throws MooshakContentException
	 */
	public final void reopen() // NO_UCD (test only)
			throws MooshakContentException {
		
		initOpen(path);
	}
	
	/**
	 * Convenience method to close a persistent object relative to this one
	 * @see mooshak.content.core.PathManager#close(String,Class).
	 * Used mostly is unit tests.
	 * @param name f persistent object in its container (this object) 
	 * @throws MooshakContentException
	 */
	public final void close() throws MooshakContentException { // NO_UCD (test only)
		manager.forget(path);
	}
	
	
	/**
	 * Recursively remove a path and its descendants 
	 * @param path
	 * @throws MooshakContentException 
	 */
	public final void delete() throws MooshakContentException {
		delete(true);
	}
		
	/**
	 * Recursively remove a path and its descendants 
	 * @param notify parent of this update;
	 * @throws MooshakContentException
	 */
	private final void delete(boolean notify) throws MooshakContentException {
		Path absolute = getAbsoluteFile(path);
		
		if(Files.isDirectory(absolute)) {
			// all persistent objects are removed first 
			for(PersistentObject child: getChildren(false))
				child.delete(false); // don't notify me

			// remove also individual files and (possible) directories
			try {
				Files.walkFileTree(absolute, new SimpleFileVisitor<Path>() {
					
					public FileVisitResult	visitFile(Path file, 
							BasicFileAttributes attrs) throws IOException {
						executeIgnoringFSNotifications( () ->
							Files.delete(file.toAbsolutePath()) );
						return FileVisitResult.CONTINUE;
					}
					
					public FileVisitResult	postVisitDirectory(Path dir, 
							IOException exc) throws IOException {
						
						executeIgnoringFSNotifications( () ->
							Files.delete(dir.toAbsolutePath()) );
						return FileVisitResult.CONTINUE;
					}
					
				});
				
			} catch (IOException cause) {
				throw new MooshakContentException("removing file:"+path,cause);
			}
						
		}
		manager.forget(path);
		
		destroyed();
		
		if(notify)
			notifyParentRemoteCopies();
	}	

	/*----------------------------------------------------------------------*\
	 *  Notify of changes using events                                      *
	\*----------------------------------------------------------------------*/	

	void notifyParentRemoteCopies() throws MooshakContentException {
		PersistentObject parent = null;
		
		if( (parent = getParent()) != null) {
			String parentId = parent.getPath().toString();
			EVENT_SENDER.send(new ObjectUpdateEvent(parentId));
		}
	}
	
	private void notifyMyRemoteCopies() {
		EVENT_SENDER.send(new ObjectUpdateEvent(getPath().toString()));
	}
	
	/*----------------------------------------------------------------------*\
	 *  Hooks in life-cycle methods                                         *
	\*----------------------------------------------------------------------*/	

	
	/**
	 * Extend this method if some action must be performed when creating
	 * a persistent object in some extension class
	 */
	protected void created() // NO_UCD (use private)
			throws MooshakContentException {} 
	
	/**
	 * Extend this method if some actions must be performed when saving
	 * a persistent object of some extension class
	 * Return true if parent must be updated
	 */
	protected boolean updated() // NO_UCD (use private)
			throws MooshakContentException { return false;}
	

	/**
	 * Extend this method if some actions must be performed when destroying
	 * a persistent object of some extension class
	 */
	protected void destroyed() // NO_UCD (use private)
			throws MooshakContentException {}

	
	/*----------------------------------------------------------------------*
	 *  Life-cycle methods of relatives 		                            *
	 *----------------------------------------------------------------------*/	
	
	/**
	 * Creates a persistent object with given relative name and type.
	 * This instance is stored for latter reuse.
	 * @param name of persistent object relatively to its container (this object)
	 * @param type class extending <code>PersistentObject</code>
	 * @return new persistent object
	 * @throws MooshakContentException
	 */
	public final <T extends PersistentObject> T create(String name,Class<T> type) 
		throws MooshakContentException {

		Path newPath = path.resolve(name);
		
		if (Files.exists(PersistentCore.getAbsoluteFile(newPath)))
			throw new MooshakContentException("Object " + name + " already exists");
		
		//manager.mark(newPath); 
		T persistent = PersistentObject.create(newPath,type);
		//manager.store(newPath, persistent);
		
		return persistent;
	}
	
	/**
	 * Opens an existing persistent object contained in this object
	 * with given name. An already opened persisting object is returned 
	 * if available. 
	 * Otherwise an instance of an extension of PersistentObject 
	 * is created and the data on path is loaded to this object.
	 *  
	 * @param name of persistent object in its container (this object) 
	 * @return new or existing persistent object
	 * @throws MooshakContentException
	 */
	public <T extends PersistentObject> T open(String name) 
		throws MooshakContentException {
		Path newPath;
		
		if(name.startsWith("/"))
			newPath = Paths.get(name.substring(1));
		else
			newPath	= path.resolve(name).normalize();
		
		return open(newPath);
	}
	
	/**
	 * Open a persistent object contained in this object with given name,
	 * if it exists. Otherwise search the ancestors of a persistent object
	 * with that name.
	 *  
	 * @param name
	 * @return
	 * @throws MooshakContentException
	 */
	public <T extends PersistentObject> T openOrInherit(String name) 
			throws MooshakContentException {
		Path absolutePath = PersistentCore.getAbsoluteFile(path.resolve(name));	
			
		if(Files.exists(absolutePath))
			return open(name);
		else {
			PersistentObject parent = getParent();
			
			if(parent == null)
				return null;
			else
				return parent.openOrInherit(name);
		}
	}
	
	/**
	 * Open the object referred by this given field name that must be a File
	 * (a relative file based on this object's path) 
	 * @param attributeName	name of Mooshak attribute
	 * @param type of object to open
	 * @return
	 * @throws MooshakContentException if cannot access attribute or 
	 *			attribute is not of type File
	 */
	protected <T extends PersistentObject> T
	openRelative(String attributeName, Class<T> type) 
			throws MooshakContentException {

		Attribute attribute = Attributes.getAttribute(getClass(), attributeName);
		Object value;
		try {
			synchronized (attribute.field) {
				attribute.field.setAccessible(true);
				value = attribute.field.get(this);
				attribute.field.setAccessible(false);
			}
		} catch (IllegalArgumentException | IllegalAccessException cause) {
			throw new MooshakContentException(
					"Acessing field in open relative",cause);
		}
		if(value instanceof Path) {
			Path path = (Path) value;
			String complement = attribute.getComplementBase();
			T base = open(complement);
			if(base instanceof PersistentContainer) {
				PersistentContainer<?> container=(PersistentContainer<?>) base;
				
				@SuppressWarnings("unchecked")
				T persistent = (T) container.find(path.toString());
				return persistent;
			} else {
				throw new MooshakContentException(
						"Complement must be a container");
			}
		} else
			throw new MooshakContentException("Field "+attributeName+
					" not of type File");
	}

	
	
	/*----------------------------------------------------------------------*
	 *  Relatives: parent & descendants			                            *
	 *----------------------------------------------------------------------*/
	
	/**
	 * Checks if this object is the root object
	 * @return
	 */
	public boolean isRoot() {
		return getPath().toString().equals(ROOT_NAME);
	}
	
	/**
	 * Get the parent as a {@code PersistentObject}
	 * @return parent PO or {@code null} if parent folder is not a PO
     * (this may happen during tests or with root PO)
     * 
	 * @throws MooshakContentException
	 */
	public <T extends PersistentObject> T getParent() 
		throws MooshakContentException {
		
		Path parentPath = path.getParent();
		
		@SuppressWarnings("unchecked")
		T parent = (T) manager.retrieve(parentPath);
		if(
				parent == null 			&& 
				parentPath != null 		&& 
				Files.exists(
						PersistentCore.getAbsoluteFile(parentPath,TYPE_FILE))) {
			
			parent = PersistentObject.open(parentPath);
			manager.store(parentPath, parent);
		} 
		return parent;
	}
	
	/**
	 * Convenience method to access the parent of the parent of this 
	 * {@code PersistentObject} also as a {@code PersistentObject} 
	 * if it exists. Otherwise {@code null} is returned
	 * @return
	 * @throws MooshakContentException 
	 */
	public <T extends PersistentObject> T getGrandParent() 
			throws MooshakContentException {
		PersistentObject persistent = getParent();

		if(persistent == null)
			return null;
		else
			return persistent.getParent();
	}
	
	/**
	 * Returns a list of PersistentObjects that are children of this one
	 * @param ignoreFrozen if true then it does not list frozen children
	 * @return list of PersistentObjects 
	 * @throws MooshakContentException
	 */
	public <T extends PersistentObject> List<T> getChildren(boolean ignoreFrozen) 
			throws MooshakContentException {
		List<T> children = new ArrayList<T>();
		T persistent;
		
		for(Path child: listChildrenPaths()) {
			
			@SuppressWarnings("unchecked")
			final T retrieve = (T) manager.retrieve(child);
			
			if((persistent = retrieve) != null) {
				
				if(persistent.isFrozen() && ignoreFrozen)
					continue;
				
				children.add(persistent);
			} else if(
					Files.isDirectory(PersistentCore.getAbsoluteFile(child)) && 
					Files.exists(PersistentCore.getAbsoluteFile(child,TYPE_FILE))
			) {
			
				@SuppressWarnings("unchecked")
				T open = (T) PersistentObject.open(child);
				
				if(open.isFrozen() && ignoreFrozen)
					continue;
				
				children.add(open);
			}
		}
		return children;
	}
	
	/**
	 * Returns a data descent of given type
	 * @param type
	 * @param ignoreFrozen if true then it does not list frozen children
	 * @return data object
	 * @throws MooshakContentException - if more, of less, than instance exist
	 */
	public <T extends PersistentObject> T getData(Class<T> type) 
			throws MooshakContentException {
		List<T> list= getChildren(type, true);
		
		switch(list.size()) {
		case 0: throw new MooshakContentException(
				"Empty list of descendets with type "+type); 
		case 1 :
			return list.get(0);
		default: throw new MooshakContentException(
				"Too many descendets with type "+type); 
		}
	}
	
	/**
	 * Returns a list of children of PersistentObjects with given type
	 * @param type
	 * @return PersistentObjects 
	 * @throws MooshakContentException
	 */
	public <T extends PersistentObject> List<T> getChildren(Class<T> type,
			boolean ignoreFrozen)
			throws MooshakContentException {
		List<T> descendants = new ArrayList<T>();
		T persistent;
		String typeName = type.getName();

		for (Path child : listChildrenPaths()) {
			try {
				@SuppressWarnings("unchecked")
				final T retrieve = (T) manager.retrieve(child);

				if ((persistent = retrieve) != null) {

					if(persistent.isFrozen() && ignoreFrozen)
						continue;

					if (persistent.getClass().equals(type)) {
						descendants.add(persistent);
					}
				} else if (Files.isDirectory(home.resolve(child))
						&& Files.exists(getAbsoluteFile(TYPE_FILE))
						&& typeName.equals(loadTypeName(child))) {

					@SuppressWarnings("unchecked")
					final T open = (T) PersistentObject.open(child);

					if(open.isFrozen() && ignoreFrozen)
						continue;

					descendants.add(open);
				}
			} catch(MooshakContentException cause) {
				Logger.getLogger("").log(Level.SEVERE,
						"Iterating over descendants", cause);
			}
		}
		
		return descendants;
	}		
			
	/**
	 * Return the list of paths of children (files and directories)
	 * relative to the home directory
	 * @return
	 * @throws MooshakContentException
	 */
	private List<Path> listChildrenPaths() throws MooshakContentException {
		List<Path> children = new ArrayList<>();
		
		Path absolute = getAbsoluteFile();
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(absolute)){
			for(Path child: stream) {
				children.add(PersistentCore.getRelativePath(child));
			}
		} catch (IOException cause) {
			throw new MooshakContentException("Listing "+path,cause);
		}
		return children;
	}
	
		
	/*----------------------------------------------------------------------*
	 *  Fields and methods for data file handling                           *
	 *----------------------------------------------------------------------*/
	
	static final String DATA_FILE = ".data.tcl";
	static final Pattern PROPERTY_FILE_LINE = Pattern
			.compile("^\\s*set\\s+(\\w+)\\s+\\{?(.*?)\\}?$");
	
	/**
	 * Checks if given path corresponds to a persistent object
	 * @param somePath to a directory (a possible persistent object path)
	 * @return
	 */
	public static boolean isPersistentObjectAbsolutePath(Path somePath) {
			return Files.exists(somePath)
				&& Files.exists(somePath.resolve(DATA_FILE))
				&& Files.exists(somePath.resolve(TYPE_FILE));
	}
	
	/**
	 * Checks if this objects is persisted in the file system
	 * @return
	 */
	public boolean inFileSystem() {
		return Files.exists(getAbsoluteFile())
				&& Files.exists(getAbsoluteFile(DATA_FILE))
				&& Files.exists(getAbsoluteFile(TYPE_FILE));
	}
	
	private static final  Pattern WITH_BLANCS = Pattern.compile("\\s+");
	
	/**
	 * Marshaling of this persistent object in its path
	 * 
	 * @throws MooshakContentException
	 */
	private void marshal() throws MooshakContentException {
		
		Path file = getAbsoluteFile(DATA_FILE);
		int count = 0;
		
		try(Writer writer = Files.newBufferedWriter(file, getCharset())) {

			for(Attribute attribute: Attributes.getAttributes(getClass())) {
				count++;
				String name = attribute.getName();
				String value = Attributes.getStringValue(attribute,this);
				
				if (value == null || "".equals(value))
					value="{}";
				else if (WITH_BLANCS.matcher(value).find())
					value = "{" + value + "}";
				try {
					
					writer.write("set " + name + " " + value + "\n");
				} catch (IOException we) {
					String msg = "Marshaling " + path + ": property " + name;
					throw new MooshakContentException(msg, we);
				}
			}
			
			writer.write("set frozen " + frozen + "\n");
		} catch (IOException cause) {
			throwException("Marshaling",count, "writing file", cause);
		} 
		try {
			Files.setPosixFilePermissions(file, OWNER_READ_WRITE_PERMISSIONS);
		} catch (IOException cause) {
			throwException("Marshaling",count, "setting permissions", cause);
		}
	}
	
	/**
	 * Unmarshaling of this persistent object from its path
	 * 
	 * @throws MooshakContentException
	 */
	void unmarshal() throws MooshakContentException {
		
		// create containers if they don't exist
		for (Attribute attribute : Attributes.getAttributes(getClass())) {
			if(attribute.getType() == AttributeType.DATA
					&& PersistentObject.class.isAssignableFrom(attribute.getFieldType())) {
				Path folder = getAbsoluteFile(attribute.getName());
				if(!Files.exists(folder))
					create(attribute.getName(), attribute.getFieldType());
			}
		}
		
		Path file = getAbsoluteFile(DATA_FILE);
		
		String line;		
		String name, value = null;
		Attributes.Attribute attribute = null;
		
		for(String charsetName: CHARSET_NAMES) {
			Charset charset = CHARSETS.get(charsetName);
			int count = 0;
			
			try(BufferedReader reader = Files.newBufferedReader(file,charset)) {
				
				Matcher matcher;
				while ((line = reader.readLine()) != null) {
					count++;
					matcher = PROPERTY_FILE_LINE.matcher(line);
					if (matcher.matches()) {
						name = matcher.group(1);
						value = matcher.group(2);

						if(name.equalsIgnoreCase("frozen")) {
							frozen = Boolean.parseBoolean(value);
							continue;
						}

						attribute = Attributes.getAttribute(getClass(),name);
						try {
							Attributes.setStringValue(attribute,this,value);
						} catch(IllegalArgumentException cause) {
							String message = path+": line "+count+":"+
									"illegal argument "+attribute+"="+value;
							Logger.getLogger("").log(Level.WARNING,message);
						}
					}
				}
			} catch(UnmappableCharacterException | MalformedInputException e) {
				// try the next char set
				continue;
			} catch (IllegalArgumentException e) {
				String pair = attribute + "=" + value;
				throwException("Unmarshaling",count,"illegal argument:"+pair,e);
			} catch (FileNotFoundException oe) {
				String pair = attribute + "=" + value;
				throwException("Unmarshaling",count, "opening file:"+pair, oe);
			} catch (IOException re) {
				String pair = attribute + "=" + value;
				throwException("Unmarshaling",count,"reading properties:"+pair, re);
			}
			break;
		}
	}

	
	/*----------------------------------------------------------------------*
	 *  Fields and methods for type file handling                           *
	 *----------------------------------------------------------------------*/
	
	private void setType() throws MooshakContentException {
		Path file = getAbsoluteFile(TYPE_FILE);
		String type = tailName(getClass().getName());

		try(BufferedWriter writer = Files.newBufferedWriter(file, getCharset())) {
			writer.write("return " + type + "\n");
		} catch (IOException cause) {
			throwException("Setting type",1, "writting", cause);
		}
		
		try {
			Files.setPosixFilePermissions(file, OWNER_READ_WRITE_PERMISSIONS);
		} catch (IOException cause) {
			throwException("Marshaling",1, "setting permissions", cause);
		}
	}

	
	/*----------------------------------------------------------------------*
	 *  XML import export						                            *
	 *----------------------------------------------------------------------*/
	
	/**
	 * Import PO and its descendants from a ZIP file 
	 * 
	 * @param zip file in ZIP format
	 * @throws MooshakContentException on IO and XML processing errors
	 */
	public void importMe(ZipFile zip) // NO_UCD (unused code)
			throws MooshakContentException { 
		ZipEntry contentEntry = zip.getEntry(CONTENT_XML);
		Document contentDocument;
		String msg;
		
		try(InputStream contentInputStream = zip.getInputStream(contentEntry)){
			
			DocumentBuilder documentBuilder=BUILDER_FACTORY.newDocumentBuilder();
			contentDocument = documentBuilder.parse(contentInputStream);
				
		} catch (ParserConfigurationException e) {
			msg = "Parse configuration error when loading content";
			throw new MooshakContentException(msg,e);
		} catch (SAXException e) {
			msg = "XML error parsing content";
			throw new MooshakContentException(msg,e);
		} catch (IOException e) {
			msg = "I/O error parsing content";
			throw new MooshakContentException(msg,e);
		}
		
		importMe(contentDocument.getDocumentElement(),zip);
	}
	
	/**
	 * Export PO and its descendants to a ZIP file
	 * @return file in ZIP format
	 * @throws MooshakException 
	 */
	public ZipFile exportMe() throws MooshakException { // NO_UCD (unused code)
		ZipFile zip = null;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			Document doc = docBuilder.newDocument();
			Element rootEl = doc.createElement(getClass()
					.getSimpleName());
			doc.appendChild(rootEl);

			Class<? extends PersistentObject> objectType = getClass();

			Attr xmlns = doc.createAttribute("xmlns");
			xmlns.setValue("http://www.ncc.up.pt/mooshak/");
			rootEl.setAttributeNode(xmlns);
			
			Attr id = doc.createAttribute("xml:id");
			id.setValue(getIdName());
			rootEl.setAttributeNode(id);

			for (Attribute attribute : Attributes.getAttributes(objectType)) {
				String field = attribute.getName();
				Attr attr = null;
				switch (attribute.getType()) {
				case FILE:
					attr = doc.createAttribute(field);
					Path path = Attributes
							.getValueAsPath(attribute, this);
					attr.setValue(path == null ? "" : path.toString());
					rootEl.setAttributeNode(attr);

					break;
				case CONTENT:
				case DATA:
					break;
				default:
					attr = doc.createAttribute(field);
					String text = Attributes.getStringValue(attribute,
							this);
					attr.setValue(text == null ? "" : text);
					rootEl.setAttributeNode(attr);

					break;
				}
				;
			}

			for (PersistentObject po : getChildren(false)) {
				try {
					rootEl.appendChild(po.generateXMLTree(doc, ""));
				} catch (Exception e) {
					Logger.getLogger("").log(Level.SEVERE, "Generating " + po.getId().toString() 
							+ " XML tree: " + e.getMessage());
				}
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(
					"{http://xml.apache.org/xslt}indent-amount", "2");
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(getAbsoluteFile().toString()
					+ File.separator
					+ "Content.xml"));

			transformer.transform(source, result);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
		
		File directoryToZip = new File(getAbsoluteFile().toString());

		List<File> fileList = new ArrayList<File>();
		getAllFiles(directoryToZip, fileList);
		Path zipPath = writeZipFile(directoryToZip, fileList);
		
		try {
			zip = new ZipFile(new File(zipPath.toString()));
		} catch (ZipException e) {
			throw new MooshakException("Error creating ZIP file");
		} catch (IOException e) {
			throw new MooshakException("Error creating ZIP file");
		}
		return zip;
	}
	
	/**
	 * Generate the XML tree of the given persistent object
	 * 
	 * @param doc
	 * @param subpath
	 * @param po
	 * @return
	 * @throws MooshakException
	 */
	private Element generateXMLTree(Document doc, String subpath) throws MooshakException {

		Element el = doc.createElement(getClass().getSimpleName());

		Class<? extends PersistentObject> objectType = getClass();

		Attr id = doc.createAttribute("xml:id");
		id.setValue(subpath + getIdName());
		el.setAttributeNode(id);

		for (Attribute attribute : Attributes.getAttributes(objectType)) {
			String field = attribute.getName();
			Attr attr = null;
			switch (attribute.getType()) {
			case FILE:
				attr = doc.createAttribute(field);
				Path path = Attributes.getValueAsPath(attribute, this);
				attr.setValue(path == null ? "" : path.toString());
				el.setAttributeNode(attr);

				break;
			case CONTENT:
			case DATA:
				break;
			default:
				attr = doc.createAttribute(field);
				String text = Attributes.getStringValue(attribute, this);
				attr.setValue(text == null ? "" : text);
				el.setAttributeNode(attr);

				break;
			}
			;
		}

		for (PersistentObject child : getChildren(false)) {
			try {
				el.appendChild(child.generateXMLTree(doc, subpath + getIdName() + "."));
			} catch (Exception e) {
				Logger.getLogger("").log(Level.SEVERE, "Generating " + child.getId().toString() 
						+ " XML tree: " + e.getMessage());
			}
		}

		return el;
	}
	
	/**
	 * Get all files in a given directory dir
	 * @param dir
	 * @param fileList
	 */
	private void getAllFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		
		if(files != null)
			for (File file : files) {
				if (file.getName().startsWith("."))
					continue;
				fileList.add(file);
				if (file.isDirectory())
					getAllFiles(file, fileList);
			}
	}
	
	/**
	 * Write a zip to directoryToZip with files in fileList
	 * @param directoryToZip
	 * @param fileList
	 * @return path to zip file
	 * @throws MooshakException
	 */
	private Path writeZipFile(File directoryToZip, List<File> fileList) 
			throws MooshakException {

		try {
			Path zipPath = Files.createTempFile(directoryToZip.getName(), ".zip");
			FileOutputStream fos = new FileOutputStream(zipPath.toString());
			ZipOutputStream zos = new ZipOutputStream(fos);

			for (File file : fileList) {
				if (!file.isDirectory()) {
					addToZip(directoryToZip, file, zos);
				}
			}

			zos.close();
			fos.close();
			
			return zipPath;
		} catch (IOException e) {
			throw new MooshakException("Creating zip file");
		}
	}

	/**
	 * Add a file to zip
	 * @param directoryToZip
	 * @param file
	 * @param zos
	 * @throws MooshakException
	 */
	private void addToZip(File directoryToZip, File file, ZipOutputStream zos)
			throws MooshakException {

		try {
			FileInputStream fis = new FileInputStream(file);
			String zipFilePath = file.getCanonicalPath().substring(
					directoryToZip.getCanonicalPath().length() + 1,
					file.getCanonicalPath().length());
			ZipEntry zipEntry = new ZipEntry(zipFilePath);
			zos.putNextEntry(zipEntry);
	
			byte[] bytes = new byte[1024];
			int length;
			while ((length = fis.read(bytes)) >= 0) {
				zos.write(bytes, 0, length);
			}
	
			zos.closeEntry();
			fis.close();
		} catch (IOException e) {
			throw new MooshakException("Couldn't create zip file");
		} catch (Exception e) {
			Logger.getLogger("").log(Level.SEVERE, "Adding " + file.getPath() + " to zip: " + e.getMessage());
		}
	}
	
	
	@SuppressWarnings("unchecked")
	protected <T extends PersistentObject> 
	void importMe(Element element,ZipFile zip)  // NO_UCD (use private)
			throws MooshakContentException {
		String tailName = tailName(getClass().getName());
		
		if(! element.getNodeName().equals(tailName)) {
			throw new MooshakContentException("Invalid type. Expected "+tailName);
		}
		
		NamedNodeMap attributes = element.getAttributes();
		for(int attrIndex=0; attrIndex<attributes.getLength(); attrIndex++) {
			Node attribute = attributes.item(attrIndex);
			String name = attribute.getLocalName();
			String value = attribute.getNodeValue();
			
			Attribute mooshakAttribute = 
					Attributes.getAttribute(getClass(), name);
			
			Attributes.setStringValue(mooshakAttribute, this, value);
		}
		
		NodeList children = element.getChildNodes();
		for(int nodeIndex=0; nodeIndex < children.getLength(); nodeIndex++) {
			Element child = (Element) children.item(nodeIndex);
			String id = tailName(child.getAttribute("xml:id"));
			String typeFullName = PACKAGE_PREFIX + child.getLocalName();
			Class<T> type;
			try {
				type = (Class<T>) Class.forName(typeFullName);
			} catch (ClassNotFoundException e) {
				String msg ="persistent object class class not found:"+
					typeFullName;
				throw new MooshakContentException(msg,e);
			}
			
			T descendant = create(id, type);
			descendant.importMe(child,zip);
		}
	}
	
	private static final  Pattern properName = Pattern.compile("([^\\.]+)$");
	/**
	 * Returns the last part (tail) of a dot separated pathname
	 * such as a Java class name  or a  XML Id used by Mooshak
	 * @param fullName
	 * @return
	 */
	private String tailName(String fullName) {
		
		Matcher matcher = properName.matcher(fullName);
		if(matcher.find())
			return matcher.group(1);
		else
			return "";
	}
	
	/*--------------------------------------------------------------------*\
	 *                             Exception throwing                     *
	\*--------------------------------------------------------------------*/

	
	private void throwException(String context, int count,
			String operation,
			Throwable trowable) throws MooshakContentException {
		String message = context + " " + path + ": line "+count +":"+ operation;
		throw new MooshakContentException(message, trowable);
	}

	/*--------------------------------------------------------------------*\
	 *                       Overridden methods                           *
	\*--------------------------------------------------------------------*/
	
	/**
	 * Show this persistent object type and path 
	 */
	@Override
	public String toString() {
		return "["+getClass().getSimpleName()+":"+path+"]";
	}
	

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PersistentObject other = (PersistentObject) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	/*--------------------------------------------------------------------*\
	 *                             Utilities                              *
	\*--------------------------------------------------------------------*/
	
	
	private int descendantCount = 0;
	
	/**
	 * Get a row id based on a prefix and the number of descendants 
	 * (but avoid iterating over them)
	 * @return 
	 * @throws MooshakContentException 
	 */
	public synchronized String getRowId(String prefix)
			throws MooshakContentException {
		
		return String.format("%s%03d", prefix,++descendantCount);
	}
	
	/*--------------------------------------------------------------------*\
	 *                Methods that should be overridden                   *
	\*--------------------------------------------------------------------*/
	
	/**
	 * Can the name of this PO change
	 * @return
	 */
	public boolean isRenameable() { 
		return true; 
	}
}
