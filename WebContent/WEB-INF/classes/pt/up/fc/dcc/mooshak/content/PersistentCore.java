package pt.up.fc.dcc.mooshak.content;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnmappableCharacterException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilderFactory;

import org.mozilla.universalchardet.UniversalDetector;

import pt.up.fc.dcc.mooshak.shared.Platform;

/**
 * This class contains static methods related to persistent objects,
 * and in particular to the to the installation directory and root object.
 * It also contains static definitions shared by PersistentObjects such as
 * character set, builders, default file permissions, etc.
 * 
 * 
 * Paths of persistent objects have a common <b>root</b> (usually called "data")
 * and this is the first name of every path. A persistent object representing
 * the single root can be obtained with <code>getRoot()</code>.
 * 
 * These paths are relative to an installation directory known as <b>home</b>
 * that can be managed with static getters and setters. It should be noted that
 * the home doesn't appear in regular paths, only in absolute paths. 
 * An absolute paths is required relate paths to the file system and these 
 * are given by the <code>getAbsoluteFile()</code> methods.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 *
 */
public class PersistentCore {
	
	/**
	 * The file system in used in this application
	 */
	final static FileSystem FILE_SYSTEM = FileSystems.getDefault();
	
	
	protected static final DocumentBuilderFactory BUILDER_FACTORY  = 
		DocumentBuilderFactory.newInstance();
	protected static final String CONTENT_XML = "Content.xml";

	protected static final Charset CHARSET = Charset.defaultCharset();
	
	
	protected static Path home = Paths.get("");
	static final String ROOT_NAME = "data";	
		
	protected final static PathManager manager = PathManager.getInstance();
	
	/**
	 * Get name of root folder
	 * @return
	 */
	public static String getRootName() {
		return ROOT_NAME;
	}
	
	/**
	 * Get installation directory as a Path
	 * @return
	 */
	public static Path getHomePath() {
		return home.toAbsolutePath();
	}
	
	/**
	 * Get installation directory as a String
	 * @return
	 */
	public static String getHome() {
		return home.toAbsolutePath().toString();
	}
	
	/**
	 * Set installation home
	 * @param pathname
	 * @throws MooshakContentException
	 * @param directoryName a string with the name of data directory
	 */
	public static final void setHome(String pathname) {
		home = Paths.get(pathname);
	}
		
	/**
	 * A root for all persistent objects
	 * @return
	 * @throws MooshakContentException
	 */
	public static final PersistentObject getRoot() // NO_UCD (test only)
			throws MooshakContentException { 
		
		Path rootDirectory = Paths.get(ROOT_NAME);
		
		PersistentObject root;
		if((root = manager.retrieve(rootDirectory)) != null)
				return root;
		else {
			root = PersistentObject.newInstance(PersistentObject.class);
			if(! exists(rootDirectory)) 
				root.initCreate(rootDirectory);
			else 
				root.initOpen(rootDirectory);
			
			manager.store(rootDirectory, root);
			return root;
		}
	}
	
	
	/**
	 * Get the absolute file for this PersistentObject path.
	 * The file returned by this method should be used whenever a file
	 * in this PersistentObject needs to be written or read. 
	 * @param name
	 * @return
	 */	
	public static Path getAbsoluteFile(Path path) {
		// TODO change method name
		return home.resolve(path).toAbsolutePath();
	}
	
	protected static Path getRelativePath(Path path) {
		return home.toAbsolutePath().relativize(path);
	}
	
	
	/**
	 * in this PersistentObject path.
	 * The file returned by this method should be used whenever a file
	 * in this PersistentObject needs to be written or read. 
	 * @param name
	 * @return
	 */
	protected static Path getAbsoluteFile(Path path, String name) {
		// TODO change method name
		return home.resolve(path).resolve(name).toAbsolutePath();
	}
	
	private static boolean exists(Path path) {
		return Files.exists(home.resolve(path).toAbsolutePath());
	}

	/*----------------------------------------------------------------------*
	 *  Static instantiation and initialization of persistent objects       *
	 *----------------------------------------------------------------------*/
	
	 
	public static final <T extends PersistentObject> T
		create(Path path, Class<T> type) throws MooshakContentException {
	
		T persistent = PersistentObject.newInstance(type);
		
		synchronized (manager) {
			manager.mark(path); 
			persistent.initCreate(path);
			manager.store(path, persistent);
		}
		
		return persistent;
	}
	
	@SuppressWarnings("unchecked")
	public static final <T extends PersistentObject> T retrieve(Path path) {
		
		return (T) manager.retrieve(path);
	}
	
	/*----------------------------------------------------------------------*
	 *                        Type files handling                           *
	 *----------------------------------------------------------------------*/
	
	protected static final String TYPE_FILE = ".class.tcl";
	private static final  Pattern typeFileLine = Pattern
	.compile("^\\s*return\\s+(\\w+)(.*)\\s*$");
	
	private static final String LOAD_TYPE_ERROR = 
		"Unknow persistent object class "; 
	@SuppressWarnings("unchecked")
	protected  static final <T extends PersistentObject> Class<T> 
		loadTypeFrom(Path path) throws MooshakContentException {
	
		String typeName = loadTypeName(path);
	
		Class<T> type;
		try {			 
			type = (Class<T>) Class.forName(typeName);
		} catch (ClassNotFoundException e) {
			throw new MooshakContentException(LOAD_TYPE_ERROR+typeName,e);
		}
		return type;
	}
	
	static String loadTypeName(Path path) throws MooshakContentException {
		String line;
		Matcher matcher;
		String type = null;
		Path file = getAbsoluteFile(path,TYPE_FILE);
		
		try(BufferedReader reader=Files.newBufferedReader(file,getCharset())) {
				
			while ((line = reader.readLine()) != null) {
				matcher = typeFileLine.matcher(line);
				if (matcher.matches()) {
					type = matcher.group(1);
				}
			}
		} catch (FileNotFoundException cause) {
			throwException("Load type", path,"file not found", cause);
		} catch (IOException cause) {
			throwException("Load type", path, "reading type", cause);
		} 
		
		return PACKAGE_PREFIX + type;
	}
	
	protected static final String PACKAGE_PREFIX = 
			"pt.up.fc.dcc.mooshak.content.types.";
	
	@SuppressWarnings("unchecked")
	public static final  <T extends PersistentObject> Class<T> 
	loadType(String typeName) throws MooshakContentException {
		
		String fullTypeName = PACKAGE_PREFIX + typeName;
		
		Class<?> type;
		try {			 
			type = (Class<?>) Class.forName(fullTypeName);
		} catch (ClassNotFoundException e) {
			throw new MooshakContentException(LOAD_TYPE_ERROR+typeName,e);
		}
		
		if(! PersistentObject.class.isAssignableFrom(type))
			throw new MooshakContentException(LOAD_TYPE_ERROR+type);
		
		return (Class<T>) type;
	}
		
	private static final String NEW_INSTANCE_ERROR = 
		"Creating persistence object instance of "; 
	protected static final <T extends PersistentObject> T 
		newInstance(Class<T> type) throws MooshakContentException {
		T persistent=null;

		try {
			 persistent = type.newInstance();
		} catch (IllegalArgumentException e) {
			throw new MooshakContentException(NEW_INSTANCE_ERROR+type,e);
		} catch (SecurityException e) {
			throw new MooshakContentException(NEW_INSTANCE_ERROR+type,e);
		} catch (InstantiationException e) {
			throw new MooshakContentException(NEW_INSTANCE_ERROR+type,e);
		} catch (IllegalAccessException e) {
			throw new MooshakContentException(NEW_INSTANCE_ERROR+type,e);
		}
		return persistent;
	}
	
	
	/*--------------------------------------------------------------------*\
	 *                        File permissions                            *
	\*--------------------------------------------------------------------*/
	
	
	public static final Set<PosixFilePermission> OWNER_READ_WRITE_PERMISSIONS = 
			new HashSet<PosixFilePermission>();
	static {
			OWNER_READ_WRITE_PERMISSIONS.add(PosixFilePermission.OWNER_READ);
			OWNER_READ_WRITE_PERMISSIONS.add(PosixFilePermission.OWNER_WRITE);
		}
	
	public static final Set<PosixFilePermission> ALL_READ_PERMISSIONS = 
			new HashSet<PosixFilePermission>();
	static {
			ALL_READ_PERMISSIONS.add(PosixFilePermission.OWNER_READ);
			ALL_READ_PERMISSIONS.add(PosixFilePermission.GROUP_READ);
			ALL_READ_PERMISSIONS.add(PosixFilePermission.OTHERS_READ);
		}

	
	/*--------------------------------------------------------------------*\
	 *                     Char set handling			                  *
	\*--------------------------------------------------------------------*/

	protected static final SortedMap<String, Charset> CHARSETS = 
			Charset.availableCharsets();
	private static final String[] PREFERED_CHARSETS = {
		"ISO-8859-1", 
		"UTF-8", 
		Charset.defaultCharset().name()
	};
	protected static final List<String> CHARSET_NAMES = 
			new ArrayList<String>(CHARSETS.keySet());
	
	static {

		for(String name: PREFERED_CHARSETS) {
			if(CHARSET_NAMES.remove(name)) {
				CHARSET_NAMES.add(0, name);
			} else
				System.err.println("invalid charset: "+name);
		}		
	}
	
	/**
	 * Try different char sets (starting with preferred ones) and
	 * read the content of a file as a string
	 * @param relativePathto file with content to read
	 * @return content as string
	 * @throws IOException              on I/O errors
	 * @throws MooshakContentException  if no char set handles the file content
	 */
	public static String getRelativeFileContentGuessingCharset(Path relativePath) 
			throws IOException, MooshakContentException {
		
		return getAbsoluteFileContentGuessingCharset(
				getAbsoluteFile(relativePath));
	}

	/**
	 * Try different char sets (starting with preferred ones) and
	 * read the content of a file as a string
	 * @param absolutePath to file with content to read
	 * @return content as string 
	 * @throws IOException              on I/O errors
	 * @throws MooshakContentException  if no char set handles the file content
	 */
	public static String getAbsoluteFileContentGuessingCharset(Path absolutePath) 
			throws IOException, MooshakContentException {
		
		StringBuilder buffer = new StringBuilder();
		
		boolean converted = false;
		for(String charsetName: CHARSET_NAMES) {
			try {
				Charset aCharset = CHARSETS.get(charsetName);
				for(String line: Files.readAllLines(absolutePath,aCharset)) {
						buffer.append(line);
						buffer.append('\n');
				}
				converted = true;
				break;
			} catch(UnmappableCharacterException | MalformedInputException e) {}
		}
		if(! converted) 
			throw new MooshakContentException("Unknown charset in "+absolutePath);
	
		return buffer.toString();
	}
	
	/**
	 * Converts bytes to a string with the best possible character encoding.
	 * If an encoding can be determined from the bytes 
	 * (using Mozilla's universal detector) and that encoding is available
	 * then that is the preferred encoding.
	 * Otherwise, an encoding is selected according to the platform from 
	 * which these bytes where received.
	 * Finally, if none of these succeeds then it is used the server default
	 * encoding.
	 * 
	 * @param bytes
	 * @param platform from which these bytes came
	 * @return
	 * @throws MooshakContentException
	 */
	public static String getString(byte[] bytes,Platform platform) {
		
		Charset charset = getEncondingFromData(bytes);
		
		if(charset == null)
			charset = getEncodingFromPlatform(platform);
		if(charset == null)
			charset = Charset.defaultCharset();
		
		return new String(bytes,charset);
	}
	
	private static Charset getEncodingFromPlatform(Platform platform) {
		
		switch(platform) {
		case WINDOWS:
			return Charset.forName("windows-1252");
		case LINUX:
			return Charset.forName("UTF-8");
		case MAC:
			return Charset.forName("x-MacRoman");
		default:
			return Charset.defaultCharset();
		}
	}

	/**
	 * Get a charset for given bytes using universal detector
	 * @param bytes
	 * @return
	 */
	private static Charset getEncondingFromData(byte[] bytes) {
		String charsetName;
		    
		UniversalDetector detector = new UniversalDetector(null);	
		detector.handleData(bytes,0,bytes.length);		    
		detector.dataEnd();
		charsetName = detector.getDetectedCharset();
		// detector.reset();
		
		return Charset.forName(charsetName);
	}
	
	
	/**
	 * @return the charset
	 */
	public static Charset getCharset() {
		return CHARSET;
	}
	
	
	/*--------------------------------------------------------------------*\
	 *                             Exception throwing                     *
	\*--------------------------------------------------------------------*/

		
	private static void throwException(String context, 
			Path path,
			String operation,
			Throwable trowable) throws MooshakContentException {
		String message = context + " " + path + ": " + operation;
		throw new MooshakContentException(message, trowable);
	}
	

}
