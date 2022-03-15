package pt.up.fc.dcc.mooshak.managers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.client.utils.IntegerPowerOfTwoUtils;
import pt.up.fc.dcc.mooshak.content.Attributes;
import pt.up.fc.dcc.mooshak.content.Attributes.Attribute;
import pt.up.fc.dcc.mooshak.content.BackupObject;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.Operations;
import pt.up.fc.dcc.mooshak.content.Operations.Operation;
import pt.up.fc.dcc.mooshak.content.PathManager;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.erl.ReportType;
import pt.up.fc.dcc.mooshak.content.erl.TestType;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Contests;
import pt.up.fc.dcc.mooshak.content.types.Group;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.util.Charsets;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.content.util.FilesDiff;
import pt.up.fc.dcc.mooshak.content.util.FilesDiff.Change;
import pt.up.fc.dcc.mooshak.content.util.FilesDiffPrint;
import pt.up.fc.dcc.mooshak.content.util.Password;
import pt.up.fc.dcc.mooshak.content.util.ScriptFileParser;
import pt.up.fc.dcc.mooshak.content.util.StringEscapeUtils;
import pt.up.fc.dcc.mooshak.content.util.Strings;
import pt.up.fc.dcc.mooshak.evaluation.Evaluator;
import pt.up.fc.dcc.mooshak.evaluation.quiz.utils.JSONHandlerEditor;
import pt.up.fc.dcc.mooshak.server.Configurator;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakField;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakType.MooshakMethod;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.events.ObjectUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.ProblemTestSummary;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.results.ServerStatus;

/**
 * Manages all administrators requests. This class ignores types from any
 * particular communication layer, such as GWT RPC or Jersey
 * 
 * @author José paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class AdministratorManager extends Manager {
	public static final String REGEX_URL = "\\b(https?|ftp|file)://"
			+ "[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
	public static final String PATTERN_VALID_MOOSHAK_OBJECT_ID = "^[a-zA-Z0-9_-]+$";
	
	private static final RegExp REGEX_VALID_MOOSHAK_OBJECT_ID = 
			RegExp.compile(PATTERN_VALID_MOOSHAK_OBJECT_ID);
	
	private static final long MAX_RESPONSE_FILE_SIZE_MB = 2 * 1024 * 1024;

	static AdministratorManager manager = null;

	/**
	 * Get a single instance of this class
	 * 
	 * @return
	 */
	public static AdministratorManager getInstance() {

		if (manager == null)
			manager = new AdministratorManager();
		return manager;
	}
	
	private AdministratorManager() {}

	/**
	 * Get a data transfer object reflecting type given name
	 * 
	 * @param typeName
	 * @return DTO
	 * @throws MooshakException
	 *             on errors loading type
	 */
	public MooshakType getMooshakType(String typeName) throws MooshakException {

		MooshakType mooshakType = new MooshakType();
		Class<? extends PersistentObject> objectType;

		try {
			objectType = PersistentObject.loadType(typeName);
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading type " + typeName, cause);
		}

		mooshakType.setType(typeName);

		Class<?> contentType = PersistentContainer
				.getContainedClass(objectType);

		if (contentType != null) {
			if (objectType.equals(Groups.class)) // special case
				mooshakType.setContentType(Group.class.getSimpleName());
			else
				mooshakType.setContentType(contentType.getSimpleName());
		}

		List<MooshakField> fields = mooshakType.getFields();
		for (Attribute attribute : Attributes.getAttributes(objectType)) {
			MooshakField field = new MooshakField();
			List<String> possibleValues = attribute.getPossibleValues();
			
			field.setName(attribute.getName());
			field.setType(attribute.getType());
			field.setAlternatives(possibleValues);
			field.setTip(attribute.getTip());
			field.setHelp(attribute.getHelp());
			field.setDocSpec(readFileFromClasspath(objectType, attribute.getDocumentSpecification()));
			field.setQuizEditor(attribute.isQuizEditor());
			field.setMaxLength(attribute.getMaxLength());

			if(possibleValues != null && possibleValues.size() == 0)
				field.setComplement(attribute.getComplement());
			
			fields.add(field);
		}

		List<MooshakMethod> methods = mooshakType.getMethods();
		for (Operation operation : Operations.getOperations(objectType)) {
			MooshakMethod method = new MooshakMethod();

			method.setName(operation.getName());
			method.setCategory(operation.getCategory());
			method.setShowable(operation.getShowable());
			method.setInputable(operation.getInputable());
			method.setTip(operation.getTip());
			method.setHelp(operation.getHelp());
			method.setUpdateEvents(operation.getSendEvents());

			methods.add(method);
		}

		return mooshakType;
	}

	/**
	 * Read the contents of a file, given the path of the file relative to class path, to a string
	 * @param clazz class to which is given the relative path to a file
	 * @param relPath path of the file relative to class path
	 * @return the contents of a file as a string
	 */
	private String readFileFromClasspath(Class<?> clazz, String relPath) {
		
		if (relPath == null || relPath.isEmpty())
			return null;
		
		InputStream is = clazz.getResourceAsStream(relPath);
		
		if (is == null)
			return null;
		
		InputStreamReader isr = new InputStreamReader(is);
		
		BufferedReader br = new BufferedReader(isr);
		String line = null;
	    StringBuilder builder = new StringBuilder();
	    try {
			while((line = br.readLine()) != null) {
			    builder.append(line);
			    builder.append("\n");
			}
	    
		    br.close();
		    isr.close();
		    is.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Reading file " + relPath + " relative to class " + clazz.getCanonicalName());
			return null;
		}
	    
	    return builder.toString();
	}

	/**
	 * Get a data transfer object reflecting the persistent object with given ID
	 * 
	 * @param id
	 *            of PO
	 * @return DTO
	 * @throws MooshakException
	 * @throws MooshakContentException
	 */
	public MooshakObject getMooshakObject(String id) throws MooshakException {

		MooshakObject mooshakObject = new MooshakObject();

		PersistentObject persistent;
		try {
			persistent = PersistentObject.openPath(id);
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading object " + id, cause);
		}

		mooshakObject.setId(id);

		copyMooshakObjectTypeAndValues(mooshakObject, persistent);

		copyMooshakObjectChildren(mooshakObject, persistent);

		return mooshakObject;
	}

	/**
	 * Select non hidden files (exclude directories and hidden files)
	 */
	private static final Filter<Path> FILE_CONTENT_FILTER = new DirectoryStream.Filter<Path>() {

		@Override
		public boolean accept(Path entry) throws IOException {
			Path fileName;
			
			if (Files.isDirectory(entry))
				return false;
			if ((fileName = entry.getFileName()) != null && 
					fileName.toString().startsWith("."))
				return false;

			return true;
		}
	};

	/**
	 * Copy type and values maps in a Mooshak object to DTO
	 * 
	 * @param mooshakObject
	 *            DTO reflecting PersistentObject
	 * @param persistent
	 *            PersistentObject reflected by DTO
	 * @throws MooshakException
	 */
	private void copyMooshakObjectTypeAndValues(MooshakObject mooshakObject,
			PersistentObject persistent) throws MooshakException {

		Class<? extends PersistentObject> objectType = persistent.getClass();

		mooshakObject.setType(objectType.getSimpleName());

		Map<String, MooshakValue> values = mooshakObject.getValues();
		for (Attribute attribute : Attributes.getAttributes(objectType)) {
			String field = attribute.getName();
			MooshakValue value = null;
			try {
				switch (attribute.getType()) {
				case INTEGER:
					String number = Attributes.getStringValue(attribute,
							persistent);

					try {
						int n = Integer.parseInt(number);
						value = new MooshakValue(field,
								IntegerPowerOfTwoUtils.formatInt(n));
					} catch (Exception e) {
						value = new MooshakValue(field, number);
					}

					break;
				case FILE:

					Path path = Attributes
							.getValueAsPath(attribute, persistent);

					if (path == null)
						value = new MooshakValue(field, null, null);
					else {
						Path full = persistent.getAbsoluteFile(path.toString());

						value = getMooshakFileValue(field, full);
					}
					break;

				case CONTENT:
					if (!(persistent instanceof PersistentContainer)) {
						value = new MooshakValue(field);

						try (DirectoryStream<Path> stream = Files
								.newDirectoryStream(
										persistent.getAbsoluteFile(),
										FILE_CONTENT_FILTER)) {

							for (Path file : stream)
								value.addFileValue(getMooshakFileValue(field,
										file));

						} catch (IOException cause) {
							String message = "Listing content of PO";
							throw new MooshakException(message, cause);
						}
					}
					break;
				case PASSWORD:
					// don't send hash values, just an empty string
					value = new MooshakValue(field, "");
					break;
				default:
					String text = Attributes.getStringValue(attribute,
							persistent);

					value = new MooshakValue(field, text == null ? "" : text);
				}
			} catch (MooshakContentException cause) {
				String message = "Getting attribute " + attribute + " from "
						+ persistent;
				throw new MooshakException(message, cause);
			}
			values.put(field, value);
		}
	}

	/**
	 * Get a MooshakValue for a field with the contest of an absolute path
	 * 
	 * @param persistent
	 * @param field
	 *            name of field
	 * @param path
	 *            absolute path to field content
	 * @return
	 * @throws MooshakException
	 */
	private MooshakValue getMooshakFileValue(String field, Path path)
			throws MooshakException {
		if(path == null)
			throw new MooshakException("Null path");

		Path fileName = path.getFileName();
		
		if(fileName == null)
			throw new MooshakException("Null pathname");
		
		String name = fileName.toString();
		byte[] content = null;

		try {
			if (Files.exists(path)) {
				String type = Files.probeContentType(path);
		        if (type == null || !type.startsWith("text")) {
		        	
		        	if (Files.size(path) > MAX_RESPONSE_FILE_SIZE_MB) {
		        		content = new byte[] { 0 };
		        	} else {
		        		content = Files.readAllBytes(path);
		        	}
		        } else 
		        	content = PersistentCore.getAbsoluteFileContentGuessingCharset(path).getBytes();
			}
		} catch (IOException cause) {
			String message = "Reading file content:" + path;
			throw new MooshakException(message, cause);
		} catch (NullPointerException cause) {
			LOGGER.log(Level.SEVERE, cause.getMessage());
		}

		return new MooshakValue(field, name, content);
	}

	/**
	 * copy children of mooshakObject to a DTO
	 * 
	 * @param mooshakObject
	 *            DTO reflecting PersistentObject
	 * @param persistent
	 *            PersistentObject reflected by DTO
	 * @throws MooshakContentException
	 */
	private void copyMooshakObjectChildren(MooshakObject mooshakObject,
			PersistentObject persistent) throws MooshakException {

		List<String> children = new ArrayList<>();

		try {
			for (PersistentObject child : persistent.getChildren(false))
				children.add(child.getPath().toString());
		} catch (MooshakContentException cause) {
			error("Getting children of " + persistent, cause);
		}

		mooshakObject.setChildren(children);
	}

	/**
	 * Set data on a corresponding persistent object
	 * 
	 * @param data
	 * @throws MooshakException
	 */
	public void setMooshakObject(MooshakObject data) throws MooshakException {
		if(data == null)
			throw new MooshakException("Cannot set data on null object");
		
		PersistentObject persistent = PersistentObject.openPath(data.getId());
		Class<? extends PersistentObject> objectType = persistent.getClass();
		
		List<String> filesWritten = new ArrayList<>();

		Map<String, MooshakValue> values = data.getValues();
		for (Attribute attribute : Attributes.getAttributes(objectType)) {
			MooshakValue value = values.get(attribute.getName());

			switch (attribute.getType()) {
			case INTEGER:

				String number = value.getSimple();

				if(number != null && !number.isEmpty() && !number.equals("null"))
					number = IntegerPowerOfTwoUtils.parseInt(value.getSimple())
							+ "";

				try {
					Attributes.setStringValue(attribute, persistent, number);
				} catch (MooshakContentException cause) {
					String message = "Setting attribute " + attribute + " to "
							+ persistent + " with value \"" + value + "\"";
					throw new MooshakException(message, cause);
				}
				break;
			case FILE:
				Path path = null;

				if (value != null && !filesWritten.contains(value.getName())) {
					
					if (attribute.isQuizEditor()) {
						
						if (value.getContent() != null) {
							String xml = JSONHandlerEditor
									.jsonToXml(Charsets.fixCharset(value.getContent()));
							value = new MooshakValue(value.getField(), value.getName(), 
									xml == null ? null : xml.getBytes());
						}
					}
					
					// check if file was too big to send to the client
					if (value.getContent() != null && value.getContent().length == 1 
							&& value.getContent()[0] == 0) 
						continue;
					
					path = writeFileValue(persistent, value.getName(),
							value.getContent());
					if (path != null && path.getFileName() != null)
						filesWritten.add(path.getFileName().toString());
				}

				if (path != null)
					Attributes.setValueAsPath(attribute, persistent, path);
				else {
					Path oldPath = Attributes.getValueAsPath(attribute, persistent);
					if (oldPath != null) {
						Path full = persistent.getAbsoluteFile(oldPath.toString());
						try {
							Files.deleteIfExists(full);
						} catch (IOException e) {
							LOGGER.severe("Could not delete file " + oldPath.getFileName().toString());
						}
					}
					Attributes.setStringValue(attribute, persistent, null);
				}
				break;
			case DATA:
				// just ignore this are mostly containers
				break;
			case CONTENT:
				if (!(persistent instanceof PersistentContainer)) {
					Set<String> fileNames = value.getFileNames();

					if (fileNames != null)
						for (String fileName : fileNames) {
							if (!filesWritten.contains(fileName)) {
								writeFileValue(persistent, fileName,
										value.getContent(fileName));
								filesWritten.add(fileName);
							}
						}
				}
				// don't save any attributes
				break;
			case PASSWORD:
				String plain = value.getSimple();
				if (plain != null)
					plain = StringEscapeUtils.removeInvalidChars(plain);

				if (plain != null && !"".equals(plain)) {
					// ignore passwords if not set

					String hash = Password.crypt(plain);

					try {
						Attributes.setStringValue(attribute, persistent, hash);
					} catch (MooshakContentException cause) {
						String message = "Setting a password to " + persistent;
						throw new MooshakException(message, cause);
					}
				}

				break;
			default:
				String simple = value.getSimple();
				if (simple != null)
					simple = StringEscapeUtils.removeInvalidChars(simple);

				try {
					Attributes.setStringValue(attribute, persistent, simple);
				} catch (MooshakContentException cause) {
					String message = "Setting attribute " + attribute + " to "
							+ persistent + " with value \"" + value + "\"";
					throw new MooshakException(message, cause);
				}
			}
		}

		persistent.save();
	}

	private Path writeFileValue(PersistentObject persistent, String name,
			byte[] content) throws MooshakException {
		Path path = null;

		if(persistent == null)
			throw new MooshakException("Cannot write file value on null");
			
		if (name != null && !"".equals(name) && content != null) {
			path = Paths.get(name).getFileName();
			
			if(path == null) 
				throw new MooshakException("Cannot write on null file");
			else {
				Path full = persistent.getAbsoluteFile(path.toString());

				try {
					persistent.executeIgnoringFSNotifications(() -> Files.write(
							full, content));
				} catch (IOException cause) {
					String message = "Writing file " + path;
					throw new MooshakException(message, cause);
				}
			}
		}

		return path;
	}

	public CommandOutcome execute(Session session, String id,
			MooshakMethod method, MethodContext context)
			throws MooshakException {
		PersistentObject persistent = null;
		Operation operation = null;

		try {
			if(context != null)
				context.setRecipient(makeRecipient(session));
			
		
			persistent = PersistentObject.openPath(id);
			operation = Operations.getOperation(persistent.getClass(),
					method.getName());

		} catch (MooshakContentException cause) {
			error("Error loading object " + id, cause);
		}

		return operation.execute(persistent, context);
	}

	/**
	 * Uploads a file to the server
	 * 
	 * @param session
	 * @param objectId
	 * @param content
	 * @param name
	 * @param field
	 * @throws MooshakException
	 * @return new MooshakValues
	 */
	public MooshakValue uploadFile(Session session, String objectId,
			byte[] content, String name, String path, String field) throws MooshakException {

		String extension = getFileExtension(name);

		if (field == null)
			return uploadFile(session, objectId, content, name, path, true);

		String values[] = field.split(",");

		MooshakObject object;
		MooshakValue value;
		String file;
		switch (values[0]) {
		case "test":
			object = getMooshakObject(objectId + File.separator + "tests"
					+ File.separator + values[1]);
			file = values[2];

			value = uploadAndReplaceFile(session, object, file, content, name);

			runTestsAgainstSolutions(session, getMooshakObject(objectId),
					getMooshakObject(objectId + File.separator + "tests"));

			return value;
		case "image":
			object = getMooshakObject(objectId + File.separator + "images");
			return uploadAndReplaceImage(session, object, values[1], content,
					name);
		case "solution":

			Problem problem = PersistentObject.openPath(objectId);
			problem.setProgram(null);
			problem.save();

			file = values[1];

			object = getMooshakObject(objectId + File.separator + "solutions");
			value = uploadAndReplaceSolution(session, object, file, content,
					name);

			runTestsAgainstSolutions(session, getMooshakObject(objectId),
					getMooshakObject(objectId + File.separator + "tests"));

			return value;
		default:
			object = getMooshakObject(objectId);
			file = values[1];
			break;
		}
		
		if (extension.equals("zip"))
			return uploadFile(session, objectId, content, name, path, true);

		return uploadAndReplaceFile(session, object, file, content, name);
	}
	
	
	private static final RegExp REGEX_ALL_NUMBERED = 
			RegExp.compile("^\\d+$");
	
	private static final RegExp REGEX_HAS_NUMBER = 
			RegExp.compile("\\d+");
	
	private static final RegExp REGEX_VALID_FILENAME = 
			RegExp.compile("^([A-Za-z0-9])+[-_.A-Za-z0-9\\s()]*$");

	/**
	 * Uploads a file to the server without replacement
	 * 
	 * @param session
	 * @param objectId
	 * @param content
	 * @param name
	 * @return
	 * @throws MooshakException
	 */
	private MooshakValue uploadFile(Session session, String objectId,
			byte[] content, String name, String path, boolean runTests) throws MooshakException {
		
		if (name.equals(".data.tcl")) {
			Path relativeTmpPath = null;
			if (path.indexOf("/") >= 0)
				relativeTmpPath = Paths.get(path.substring(path.indexOf("/")));
			else
				relativeTmpPath = Paths.get("");

			PersistentObject po = PersistentObject.openPath(Paths.get(objectId, relativeTmpPath.toString()).toString());
			writeFileValue(po, name, content);
			po.reopen();
			return null;
		}
		
		if (!REGEX_VALID_FILENAME.test(name))
			throw new MooshakException(name + " is not a valid file name!");

		String extension = getFileExtension(name).toLowerCase();

		switch (extension) {
		case "zip":
			receivedZipFile(session, objectId, name, content);
			return null;
		case "pdf":
			return receivedPdfFile(session, objectId, name, content);
		case "gif":
		case "jpeg":
		case "jpg":
		case "png":
			return receivedImageFile(session, objectId + File.separator
					+ "images", name, content);
		case "html":
			return receivedHtmlFile(session, objectId, name, content);
		case "in":
			if (path != null && path.indexOf("/tests") == -1 && path.indexOf("tests/") == -1)
				return null;
			MooshakValue value = receivedTestFile(session, objectId, name, path,
					content, runTests);
			return value;
		case "out":
			if (path != null && path.indexOf("/tests") == -1 && path.indexOf("tests/") == -1)
				return null;
			value = receivedTestFile(session, objectId, name, path, content, runTests);
			return value;
		case "env":
			return receivedEnvironmentFile(session, objectId, name, content);
		}

		Languages languages = session.getContest().open("languages");
		try(POStream<Language> stream = languages.newPOStream()) {
			for (Language language : stream) {
				if (extension.equalsIgnoreCase(language.getExtension())) {
					MooshakValue value = receivedSolutionFile(session, objectId,
							name, content);
					MooshakObject tests = getMooshakObject(objectId
							+ File.separator + "tests");

					if (runTests) {
						try {
							runTestsAgainstSolutions(session,
									getMooshakObject(objectId), tests);
						} catch (Exception e) {
							LOGGER.log(
									Level.SEVERE,
									"Could not test uploaded solution:\n"
											+ e.getMessage());
						}
					}

					return value;
				}
			}
		} catch(Exception cause) {
			LOGGER.log(Level.SEVERE,"Error iterating over languages",cause);
		}

		if (extension.equals("") || extension.equalsIgnoreCase("txt")
				|| REGEX_ALL_NUMBERED.test(extension)) {
			
			if (name.toLowerCase().indexOf("in") != -1
					|| name.toLowerCase().indexOf("out") != -1)
				return receivedTestFile(session, objectId, name, path, content, runTests);
			else if (name.toLowerCase().indexOf("env") != -1)
				return receivedEnvironmentFile(session, objectId, name, content);
		}

		throw new MooshakException("Unrecognized extension");
	}

	private MooshakValue receivedEnvironmentFile(Session session,
			String objectId, String name, byte[] content)
			throws MooshakException {

		MooshakObject object = getMooshakObject(objectId);

		String previous = object.getFieldValue("Environment").getName();

		object.setFieldValue("Environment", new MooshakValue("Environment",
				name, content));
		setMooshakObject(object);

		// delete the previous file
		if (!name.equals(previous))
			deleteFile(objectId, previous);

		return object.getFieldValue("Environment");
	}

	/**
	 * Received a test file
	 * 
	 * @param session
	 * @param objectId
	 * @param name
	 * @param content
	 * @return
	 * @throws MooshakException
	 */
	private MooshakValue receivedTestFile(Session session, String objectId,
			String name, String path, byte[] content, boolean runTests) throws MooshakException {
		MooshakObject object = getMooshakObject(objectId + File.separator
				+ "tests");

		MooshakObject test = null;
		MooshakValue value = null;
		if (name.toLowerCase().indexOf("in") != -1) {

			if (path == null) {
				test = getTestNameForInput(objectId, name, object);
			} else {
				Path fileNamePath = Paths.get(path).getFileName();
				if(fileNamePath != null) {
					
					try {
						test = getMooshakObject(Paths.get(object.getId(), 
								fileNamePath.toString()).toString());
					} catch (MooshakException e) {
						createMooshakObject(object.getId(), fileNamePath.toString());
						test = getMooshakObject(Paths.get(object.getId(), 
								fileNamePath.toString()).toString());
					}
					
				}
				
			}
			
			if (test == null)
				throw new MooshakException("Could get a name for the new test");
			
			value = new MooshakValue("input", name, content);
			test.setFieldValue("input", value);
		} else if (name.toLowerCase().indexOf("out") != -1) {

			if (path == null) {
				test = getTestNameForOutput(objectId, name, object);
			} else {
				Path fileNamePath = Paths.get(path).getFileName();
				if(fileNamePath != null) {
					
					try {
						test = getMooshakObject(Paths.get(object.getId(), 
								fileNamePath.toString()).toString());
					} catch (MooshakException e) {
						createMooshakObject(object.getId(), fileNamePath.toString());
						test = getMooshakObject(Paths.get(object.getId(), 
								fileNamePath.toString()).toString());
					}
					
				}
				
			}
			
			if (test == null)
				throw new MooshakException("Could get a name for the new test");
			
			value = new MooshakValue("output", name, content);
			test.setFieldValue("output", value);
		}
		setMooshakObject(test);

		if (runTests) {
			try {
				runTestsAgainstSolutions(session, getMooshakObject(objectId),
						object);
			} catch (Exception e) {
				LOGGER.log(Level.INFO, "Could not test uploaded test");
			}
		}

		return value;
	}

	/**
	 * @param objectId
	 * @param name
	 * @param object
	 * @param test
	 * @return
	 * @throws MooshakException
	 */
	private MooshakObject getTestNameForOutput(String objectId, String name, MooshakObject object)
			throws MooshakException {
		
		MooshakObject test = null;
		
		Map<String, String> outputNames = new HashMap<String, String>();
		List<String> testsMissingOutput = new ArrayList<>();
		for (String testName : object.getChildren()) {
			MooshakObject tmp = getMooshakObject(testName);
			if (tmp.getFieldValue("output") == null
					|| tmp.getFieldValue("output").getName() == null)
				testsMissingOutput.add(testName);
			
			if (tmp.getFieldValue("input") != null
					&& tmp.getFieldValue("input").getName() != null) 
				outputNames.put(testName, tmp.getFieldValue("input").getName());
		}

		if (testsMissingOutput.size() == 0) {
			test = addNewTest(objectId);
		} else {
			String numberStr = name.replaceAll("[^0-9]", "");
			if (numberStr.isEmpty())
				return getMooshakObject(testsMissingOutput.get(0));
			int number = Integer
					.parseInt(numberStr);

			for (String testName : testsMissingOutput) {
				int testNumber = -1;
				try {
					if (outputNames.get(testName) != null)
						testNumber = Integer.parseInt(outputNames.get(testName)
								.replaceAll("[^0-9]", ""));
					else
						testNumber = Integer.parseInt(testName.replaceAll(
								"[^0-9]", ""));
				} catch (Exception e) {
					if (REGEX_HAS_NUMBER.test(name))
						test = addNewTest(objectId);
					else 
						test = getMooshakObject(testsMissingOutput.get(0));
				}

				if (number == testNumber) {
					test = getMooshakObject(testName);
					break;
				}
			}

			if (test == null) {
				if (REGEX_HAS_NUMBER.test(name))
					test = addNewTest(objectId);
				else 
					test = getMooshakObject(testsMissingOutput.get(0));
			}
		}
		return test;
	}

	/**
	 * @param objectId
	 * @param name
	 * @param object
	 * @param test
	 * @return
	 * @throws MooshakException
	 */
	private MooshakObject getTestNameForInput(String objectId, String name, MooshakObject object)
			throws MooshakException {
		
		MooshakObject test = null;
		
		Map<String, String> inputNames = new HashMap<String, String>();
		List<String> testsMissingInput = new ArrayList<>();
		for (String testName : object.getChildren()) {
			MooshakObject tmp = getMooshakObject(testName);
			if (tmp.getFieldValue("input") == null
					|| tmp.getFieldValue("input").getName() == null)
				testsMissingInput.add(testName);
			
			if (tmp.getFieldValue("output") != null
					&& tmp.getFieldValue("output").getName() != null) 
				inputNames.put(testName, tmp.getFieldValue("output").getName());
		}

		if (testsMissingInput.size() == 0) {
			test = addNewTest(objectId);
		} else {
			String numberStr = name.replaceAll("[^0-9]", "");
			if (numberStr.isEmpty())
				return getMooshakObject(testsMissingInput.get(0));
			int number = Integer
					.parseInt(numberStr);

			for (String testName : testsMissingInput) {
				int testNumber = -1;
				
				try {
					if (inputNames.get(testName) != null)
						testNumber = Integer.parseInt(inputNames.get(testName)
								.replaceAll("[^0-9]", ""));
					else
						testNumber = Integer.parseInt(testName.replaceAll(
								"[^0-9]", ""));
				} catch (Exception e) {
					if (REGEX_HAS_NUMBER.test(name))
						test = addNewTest(objectId);
					else 
						test = getMooshakObject(testsMissingInput.get(0));
				}

				if (number == testNumber) {
					test = getMooshakObject(testName);
					break;
				}
			}

			if (test == null) {
				if (REGEX_HAS_NUMBER.test(name))
					test = addNewTest(objectId);
				else 
					test = getMooshakObject(testsMissingInput.get(0));
			}

		}
		return test;
	}

	/**
	 * Adds a new test on objectId
	 * 
	 * @param objectId
	 * @return
	 * @throws MooshakException
	 */
	public MooshakObject addNewTest(String objectId) throws MooshakException {
		MooshakObject object = getMooshakObject(objectId + File.separator
				+ "tests");

		String name = objectId
				.substring(objectId.lastIndexOf(File.separator) + 1);
		int number = getChildNextIndex(name, object);
		createMooshakObject(object.getId(), name + number);
		object = getMooshakObject(objectId + File.separator + "tests"
				+ File.separator + name + number);

		return object;
	}

	/**
	 * Adds a new skeleton on objectId
	 * 
	 * @param objectId
	 * @return
	 * @throws MooshakException
	 */
	public MooshakObject addNewSkeleton(String objectId)
			throws MooshakException {
		MooshakObject object = getMooshakObject(objectId + File.separator
				+ "skeletons");

		int number = getChildNextIndex(objectId
				.substring(objectId.lastIndexOf(File.separator) + 1), object);

		createMooshakObject(object.getId(), "skeleton" + number);
		object = getMooshakObject(objectId + File.separator + "skeletons"
				+ File.separator + "skeleton" + number);

		object.setFieldValue("Skeleton", new MooshakValue("Skeleton",
				"skeleton.ske", "".getBytes()));
		setMooshakObject(object);

		return object;
	}

	/**
	 * Adds a new solution on objectId
	 * 
	 * @param objectId
	 * @return
	 * @throws MooshakException
	 */
	public MooshakObject addNewSolution(String objectId)
			throws MooshakException {
		MooshakObject object = getMooshakObject(objectId + File.separator
				+ "solutions");

		MooshakValue value = object.getFieldValue("Solution");

		int number = 1;
		for (String child : value.getFileNames()) {
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(child);
			int childNumber = 0;
			while (m.find())
				childNumber = Integer.parseInt(m.group());

			if (childNumber >= number)
				number = childNumber + 1;

		}

		String name = "solution" + number;
		value.addFileValue(new MooshakValue("Solution", name, "".getBytes()));
		object.setFieldValue("Solution", value);
		setMooshakObject(object);

		return object;
	}

	/**
	 * Gets a continuation number for next child
	 * @param objectId 
	 * 
	 * @param object
	 * @return
	 */
	private int getChildNextIndex(String parentId, MooshakObject object) {
		int number = 1;
		for (String child : object.getChildren()) {
			Pattern p = Pattern.compile("\\d+");
			Matcher m = p.matcher(child.replace(parentId, ""));
			int childNumber = 0;
			while (m.find())
				childNumber = Integer.parseInt(m.group());
			if (childNumber >= number)
				number = childNumber + 1;

		}
		return number;
	}

	/**
	 * Process a Description File upload
	 * 
	 * @param session
	 * @param objectId
	 * @param name
	 * @param content
	 * @return
	 * @throws MooshakException
	 */
	private MooshakValue receivedHtmlFile(Session session, String objectId,
			String name, byte[] content) throws MooshakException {

		MooshakObject object = getMooshakObject(objectId);

		String previous = object.getFieldValue("Description").getName();

		object.setFieldValue("Description", new MooshakValue("Description",
				name, content));
		setMooshakObject(object);

		// delete the previous file
		if (!name.equals(previous))
			deleteFile(objectId, previous);

		return object.getFieldValue("Description");
	}

	/**
	 * Process an Image upload
	 * 
	 * @param session
	 * @param objectId
	 * @param name
	 * @param content
	 * @return
	 * @throws MooshakException
	 */
	private MooshakValue receivedImageFile(Session session, String objectId,
			String name, byte[] content) throws MooshakException {

		MooshakObject object = getMooshakObject(objectId);
		MooshakValue value = object.getFieldValue("Image");
		value.addFileValue(new MooshakValue("Image", name, content));
		object.setFieldValue("Image", value);
		setMooshakObject(object);

		return object.getFieldValue("Image");
	}

	/**
	 * Process a Solution File upload
	 * 
	 * @param session
	 * @param objectId
	 * @param name
	 * @param content
	 * @return
	 * @throws MooshakException
	 */
	private MooshakValue receivedSolutionFile(Session session, String objectId,
			String name, byte[] content) throws MooshakException {

		Problem problem = PersistentObject.openPath(objectId);
		problem.setProgram(null);
		problem.save();

		MooshakObject object = getMooshakObject(objectId + File.separator
				+ "solutions");

		MooshakValue value = getSolutionsValue(session, problem.getIdName());
		value.addFileValue(new MooshakValue("Solution", name, content));
		object.setFieldValue("Solution", value);
		setMooshakObject(object);

		// delete the previous file
		/*
		 * if(!name.equals(previous)) deleteFile(objectId, previous);
		 */

		return object.getFieldValue("Solution");
	}

	/**
	 * Process a PDF File upload
	 * 
	 * @param session
	 * @param objectId
	 * @param name
	 * @param content
	 * @throws MooshakException
	 */
	private MooshakValue receivedPdfFile(Session session, String objectId,
			String name, byte[] content) throws MooshakException {

		MooshakObject object = getMooshakObject(objectId);

		String previous = object.getFieldValue("PDF").getName();

		object.setFieldValue("PDF", new MooshakValue("PDF", name, content));
		setMooshakObject(object);

		// delete the previous file
		if (!name.equals(previous))
			deleteFile(objectId, previous);

		return object.getFieldValue("PDF");
	}


	private Pattern problemPattern = Pattern.compile("^(tests|images|"
			+ "solutions|skeletons).*");
	private Pattern problemsPattern = Pattern.compile("^([A-Za-z0-9_ ]+)/?(tests|images|"
			+ "solutions|skeletons)?.*");
	private Pattern problemObjectIdPattern = Pattern.compile(".*/problems/([A-Za-z0-9_ ]+)$");
	private Pattern problemsObjectIdPattern = Pattern.compile(".*/problems$");
	
	private static final int 	AVAILABLE 
			= Runtime.getRuntime().availableProcessors();
	private static final ExecutorService POOL
			= Executors.newFixedThreadPool(AVAILABLE);
	
	/**
	 * Process a Zip File upload
	 * 
	 * @param session
	 * @param objectId
	 * @param name
	 * @param content
	 * @throws MooshakException
	 */
	private void receivedZipFile(final Session session, String objectId, String name,
			byte[] content) throws MooshakException {
		
		Set<String> changedProblems = new HashSet<>();
		
		try {
			ByteArrayInputStream byteStream = new ByteArrayInputStream(content);
			ZipInputStream zipInput = new ZipInputStream(byteStream);
			ZipEntry zipEntry;
			while ((zipEntry = zipInput.getNextEntry()) != null) {

				if (!zipEntry.isDirectory()) {
					String fileName = null;
					String filePath = null;
					String tmpObjectId = objectId;
					
					int indexPathSep = zipEntry.getName().lastIndexOf(File.separator);
					if (indexPathSep >= 0) {
						
						filePath = zipEntry.getName().substring(0, indexPathSep);
						
						Matcher problemMatcher = problemPattern.matcher(filePath);
						Matcher problemsMatcher = problemsPattern.matcher(filePath);
						if (problemMatcher.matches()) {
							Matcher problemObjIdMatcher = problemObjectIdPattern
									.matcher(objectId);
							if (!problemObjIdMatcher.matches())
								throw new MooshakException("Invalid file structure!");
						} else if (problemsMatcher.matches()) {
							Matcher problemsObjIdMatcher = problemsObjectIdPattern
									.matcher(objectId);
							if (!problemsObjIdMatcher.matches()) {
								int index = objectId.lastIndexOf(File.separator);
								if (index >= 0)  
									tmpObjectId = objectId.substring(0, index);
								else
									throw new MooshakException("Invalid file structure!");
							}
							
							tmpObjectId = tmpObjectId + File.separator
									+ problemsMatcher.group(1);
						} else {
							throw new MooshakException("Invalid file structure!");
						}
						fileName = zipEntry.getName().substring(indexPathSep + 1);
					}
					else
						fileName = zipEntry.getName();
					
					byte[] entryContent = IOUtils.toByteArray(zipInput);
					
					try {
						uploadFile(session, tmpObjectId, entryContent, fileName, filePath,
								false);
					} catch (MooshakException e) {
						// ignore failed upload
					}
					
					changedProblems.add(tmpObjectId);
				} else {
					String filePath = zipEntry.getName();
					String[] dirs = filePath.split(File.separatorChar=='\\' ? "\\\\"
							: File.separator);
					//int i = 0;
					String tmpDir = objectId;
					Matcher problemMatcher = problemPattern.matcher(filePath);
					Matcher problemsMatcher = problemsPattern.matcher(filePath);
					if (problemMatcher.matches()) {
						Matcher problemObjIdMatcher = problemObjectIdPattern
								.matcher(objectId);
						if (!problemObjIdMatcher.matches())
							throw new MooshakException("Invalid file structure!");
					} else if (problemsMatcher.matches()) {
						Matcher problemsObjIdMatcher = problemsObjectIdPattern
								.matcher(objectId);
						if (!problemsObjIdMatcher.matches()) {
							int index = objectId.lastIndexOf(File.separator);
							if (index >= 0)  
								tmpDir = objectId.substring(0, index);
							else
								throw new MooshakException("Invalid file structure!");
						}
					} else {
						throw new MooshakException("Invalid file structure!");
					}
					
					for (String dir : dirs) {
						
						try {
							getMooshakObject(tmpDir + File.separator + dir);
						} catch (MooshakException e) {
							try {
								createMooshakObject(tmpDir, dir);
							} catch (MooshakException e2) {
								throw new MooshakException("Invalid file structure!");
							}
						}
						
						tmpDir += File.separator + dir;
						//i++;
					}
				}
			}
			zipInput.close();
		} catch (IOException ioe) {
			error(ioe.getMessage());
		}

		
		if (changedProblems.size() == 1) {
			String id = changedProblems.iterator().next();
			runTestsAgainstSolutions(session, getMooshakObject(id),
					getMooshakObject(id + File.separator + "tests"));
		} else {
			for (String p : changedProblems) {
				POOL.execute(new Runnable() {
					
					@Override
					public void run() {
						try {
							runTestsAgainstSolutions(session, getMooshakObject(p),
									getMooshakObject(p + File.separator + "tests"));
						} catch (MooshakException e) {
							// ignore errors here
						}
					}
				});
			}
		}
	}

	/**
	 * Uploads a file replacing an existent file
	 * 
	 * @param session
	 * @param object
	 * @param file
	 * @param content
	 * @param name
	 * @return new value
	 */
	private MooshakValue uploadAndReplaceFile(Session session,
			MooshakObject object, String fileName, byte[] content, String name)
			throws MooshakException {
		String field = findKeyWithValue(object, fileName);

		Path testFolder = PersistentObject.openPath(object.getId())
				.getAbsoluteFile();

		if (!name.equals(fileName))
			renameFile(testFolder.toString(), fileName, name);

		object.setFieldValue(field, new MooshakValue(field, name, content));
		setMooshakObject(object);

		return object.getFieldValue(field);
	}

	/**
	 * Uploads a file replacing an existent image
	 * 
	 * @param session
	 * @param object
	 * @param file
	 * @param content
	 * @param name
	 * @return new MooshakValue
	 */
	private MooshakValue uploadAndReplaceImage(Session session,
			MooshakObject object, String fileName, byte[] content, String name)
			throws MooshakException {
		String field = "Image";

		Path imageFolder = PersistentObject.openPath(object.getId())
				.getAbsoluteFile();

		if (!name.equals(fileName))
			renameFile(imageFolder.toString(), fileName, name);

		MooshakValue value = object.getFieldValue(field);
		value.removeFile(fileName);
		value.addFileValue(new MooshakValue(field, name, content));
		setMooshakObject(object);

		return object.getFieldValue(field);
	}

	/**
	 * Uploads a file replacing an existent solution
	 * 
	 * @param session
	 * @param object
	 * @param file
	 * @param content
	 * @param name
	 * @return new MooshakValue
	 */
	private MooshakValue uploadAndReplaceSolution(Session session,
			MooshakObject object, String fileName, byte[] content, String name)
			throws MooshakException {
		String field = "Solution";

		Path solutionFolder = PersistentObject.openPath(object.getId())
				.getAbsoluteFile();

		if (!name.equals(fileName))
			renameFile(solutionFolder.toString(), fileName, name);

		MooshakValue value = object.getFieldValue(field);
		value.removeFile(fileName);
		value.addFileValue(new MooshakValue(field, name, content));
		setMooshakObject(object);

		return object.getFieldValue(field);
	}

	/**
	 * Runs tests against solutions and sets the result
	 * 
	 * @param session
	 * @param problemObject
	 * @param testsObject
	 * @throws MooshakException
	 */
	public void runTestsAgainstSolutions(Session session,
			MooshakObject problemObject, MooshakObject testsObject)
			throws MooshakException {

		Contest contest = session.getContest();
		Problem problem = PersistentObject.openPath(problemObject.getId());

		List<Path> solutions = new ArrayList<>(problem.getSolutions());
		
		StringBuilder sbSummary = new StringBuilder();
		
		Submissions submissions = contest.open("submissions");
		List<String> testsMOs = new ArrayList<>();
		for (String objectId : testsObject.getChildren()) {
			MooshakObject mo = getMooshakObject(objectId);
			
			byte[] inputBytes = mo.getFieldValue("input")
					.getContent();
			if (inputBytes == null)
				continue;

			byte[] outputBytes = mo.getFieldValue("output")
					.getContent();
			if (outputBytes == null)
				continue;
			
			mo.setFieldValue("Timeout", new MooshakValue("Timeout", "0"));
			mo.setFieldValue("Result", new MooshakValue("Result", ""));
			mo.setFieldValue("SolutionErrors", new MooshakValue("SolutionErrors", ""));
			setMooshakObject(mo);
			testsMOs.add(objectId);
		}
		Collections.sort(testsMOs, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				
				return o1.compareTo(o2);
			}
		});

		long problemTimeout = 0;
		for (Path solution : solutions) {
			long solutionTimeout = 0;
			
			Path solutionName = solution.getFileName();
			if (solutionName == null)
				continue;

			String filename = solutionName.toString();
			byte[] solutionCode = null;
			try {
				solutionCode = Files.readAllBytes(PersistentObject
						.getAbsoluteFile(solution));
			} catch (IOException e) {
				throw new MooshakException("Error reading solution!");
			}
			
			String id = contest.getTransactionId("creator", problem.getIdName())
					+ (int) (Math.random() * 100);
			final Submission submission;
			try {
				submission = submissions.create(id, Submission.class);
			} catch (MooshakException e) {
				continue;
			}
			
			try {
				
				submission.receive(
						session.getParticipant().getIdName(), 
						session.getIdName(),
						filename, 
						solutionCode, 
						problem.getIdName(),
						new ArrayList<String>(),
						true);
	
				submission.analyze();
			} catch (MooshakException e) {
				submission.delete();
				continue;
			}
			
			if (submission.getAllReportTypes().isEmpty()) {
				submission.delete();
				continue;
			}

			ReportType reportType = submission.getAllReportTypes().get(
					submission.getAllReportTypes().size() - 1);
			
			if (reportType.getTests() == null) {
				
				for (String testId : testsMOs) {
					MooshakObject testMO = getMooshakObject(testId);
					testMO.setFieldValue("Result", new MooshakValue("Result",
							Classification.COMPILE_TIME_ERROR.getAcronym()));
					testMO.setFieldValue("SolutionErrors", new MooshakValue(
							"SolutionErrors", testMO.getFieldValue("SolutionErrors").getSimple() + 
							filename + "; "));
					
					sbSummary.append("Error in test " + Paths.get(testMO.getId()).getFileName().toString()
							+ " (solution " + filename + ")\n");
					if (reportType.getCompilationErrors() != null)
						sbSummary.append(reportType.getCompilationErrors() + "\n");
					
					setMooshakObject(testMO);
				}
				
				submission.delete();
				continue;
			}
			
			int i = 0;
			for (TestType testType : reportType.getTests().getTest()) {
				MooshakObject testMO = getMooshakObject(testsMOs.get(i));
				long timeout = Long.parseLong(testMO.getFieldValue("Timeout").getSimple());
				if (testType.getExecutionTime() != null && testType.getExecutionTime() > timeout)
					timeout = testType.getExecutionTime();
				testMO.setFieldValue("Timeout", new MooshakValue("Timeout",
						 timeout + ""));
				
				if (timeout > solutionTimeout) 
					solutionTimeout = timeout;
				
				if (testMO.getFieldValue("Result").getSimple().equalsIgnoreCase("A") ||
						testMO.getFieldValue("Result").getSimple().equals(""))
					testMO.setFieldValue("Result", new MooshakValue("Result",
							Strings.acronymOf(testType.getClassify())));
				
				if (!testType.getClassify().equalsIgnoreCase("accepted")) {
					testMO.setFieldValue("SolutionErrors", new MooshakValue(
							"SolutionErrors", testMO.getFieldValue("SolutionErrors").getSimple() + 
							filename + ";"));
					
					sbSummary.append("Error in test " + Paths.get(testMO.getId()).getFileName().toString()
							+ " (solution " + filename + ")\n");
					if (testType.getFeedback() != null)
						sbSummary.append(testType.getFeedback() + "\n");
				}
				
				setMooshakObject(testMO);
				
				i++;
			}
			
			solutionTimeout++;
			if (!filename.endsWith(".java"))
				solutionTimeout *= 2;
			
			if (solutionTimeout  > problemTimeout)
				problemTimeout = solutionTimeout;
			
			submission.delete();
		}
		
		MooshakObject problemMO = getMooshakObject(problem.getPath().toString());
		problemMO.setFieldValue("Timeout", new MooshakValue("Timeout", problemTimeout + ""));
		setMooshakObject(problemMO);
		
		ProblemTestSummary event = new ProblemTestSummary();
		event.setProblemId(problem.getIdName());
		event.setText(sbSummary.toString());
		event.setRecipient(new Recipient("creator", session.getIdName()));
		EventSender.getEventSender().send(session.getContestId(), event);
	}
	
	/**
	 * Renames a file in id with name fileName to name
	 * 
	 * @param id
	 * @param fileName
	 * @param name
	 * @throws MooshakException
	 */
	private void renameFile(String id, String fileName, String name)
			throws MooshakException {
		String source = id + File.separator + fileName;

		File file = new File(source);

		if (!file.exists())
			error("File not exists " + source);

		if (!file.renameTo(new File(file.getParentFile(), name))) {
			error("Couldn't rename file " + fileName + " to " + name);
		}
	}

	/**
	 * Finds the key in object with value file
	 * 
	 * @param object
	 * @param file
	 * @return key
	 */
	private String findKeyWithValue(MooshakObject object, String file) {
		for (String key : object.getValues().keySet())
			if (!object.getValues().get(key).isSimpleValue())
				if (object.getValues().get(key).getName() != null
						&& object.getValues().get(key).getName().equals(file))
					return key;

		return null;
	}

	/**
	 * Returns the extension of a given file
	 * 
	 * @param file
	 * @return
	 */
	private String getFileExtension(String file) {
		String extension = "";
		int i = file.lastIndexOf('.');
		if (i > 0)
			extension = file.substring(i + 1);

		return extension;
	}

	/**
	 * Deletes a given file
	 * 
	 * @param objectId
	 * @param fileName
	 * @throws MooshakException
	 */
	private void deleteFile(String objectId, String fileName)
			throws MooshakException {
		if (fileName == null)
			return;

		try {
			PersistentObject persistent = PersistentObject.openPath(objectId);

			Files.delete(persistent.getAbsoluteFile(fileName));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not delete " + objectId + " "
					+ fileName);
		}
	}

	/**
	 * Creates a new Mooshak Object in object identified by id
	 * 
	 * @param id
	 *            The parent id
	 * @param name
	 *            Name of the new object
	 * @throws MooshakException
	 */
	public <T extends PersistentObject> void createMooshakObject(String id,
			String name) throws MooshakException {

		if (!REGEX_VALID_MOOSHAK_OBJECT_ID.test(name)) {
			throw new MooshakException("Invalid name for a Mooshak Object");
		}
		
		try {
			PersistentContainer<T> container = PersistentContainer.openPath(id);
			Class<T> type = container.getDescendantType();

			if (type != null) {
				T created = container.create(name, type);

				createDependants(created);

				created.save();
			}

		} catch (ClassCastException | MooshakContentException cause) {
			throw new MooshakException("Creating a mooshak object named "
					+ name + " on " + id, cause);
		}
	}

	/**
	 * Import zip content to Mooshak Object
	 * 
	 * @param id
	 * @param name
	 * @param content
	 * @throws MooshakException
	 */
	public void importMooshakObject(String id, String name, byte[] content)
			throws MooshakException {
		PersistentObject persistent;
		try {
			persistent = PersistentObject.openPath(id);
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading object " + id, cause);
		}

		String rootPath = null;
		if ((rootPath = parseXMLDescriptor(id, name, content)) == null)
			throw new MooshakException("No XML descriptor found");

		try {
			persistent = PersistentObject.openPath(rootPath);
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading object " + rootPath,
					cause);
		}

		unzipFiles(persistent.getAbsoluteFile().toString(), content);

	}

	/**
	 * Parse descriptor and create file structure
	 * 
	 * @param name
	 * @param content
	 */
	private String parseXMLDescriptor(String id, String name, byte[] content) {
		String rootPath = null;
		try {
			ZipInputStream zipinputstream = null;
			ZipEntry zipentry;
			zipinputstream = new ZipInputStream(new ByteArrayInputStream(
					content));

			zipentry = zipinputstream.getNextEntry();
			while (zipentry != null) {
				String entryName = zipentry.getName();
				if (entryName.equals("Content.xml")) {
					StringBuilder s = new StringBuilder();
					byte[] buffer = new byte[1024];
					int read = 0;

					while ((read = zipinputstream.read(buffer, 0, 1024)) >= 0)
						s.append(new String(buffer, 0, read));

					try {
						rootPath = parseXMLDescriptor(id, name, s.toString());
					} catch (Exception e) {
						zipinputstream.closeEntry();
						zipinputstream.close();
						e.printStackTrace();
					}

					break;
				}

				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();

			}

			zipinputstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return rootPath;
	}

	/**
	 * Create file system structure from xml string
	 * 
	 * @param id {@link String} ID of the parent object
	 * @param xml
	 * @return
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws MooshakException
	 */
	private String parseXMLDescriptor(String id, String name, String xml)
			throws MooshakException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			xml = xml.replaceAll("&([^;]+(?!(?:\\w|;)))", "&amp;$1");
			doc = db.parse(new InputSource(new StringReader(xml)));
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new MooshakException("Cannot parse xml file");
		}

		Element root = doc.getDocumentElement();
		NamedNodeMap attrs = root.getAttributes();

		String poName = attrs.getNamedItem("xml:id") != null ?
				attrs.getNamedItem("xml:id").getNodeValue() :
				Filenames.getFileNameWithoutExtension(name);
		
		String rootPath = "";
		MooshakObject mo = null;
		try {
			
			PersistentObject persistentObject = PersistentObject
					.openPath(id);
						
			if (persistentObject instanceof PersistentContainer<?>) {
			
				PersistentContainer<?> container = (PersistentContainer<?>)
					persistentObject;
				Class<PersistentObject> type = container.getDescendantType();
	
				if (type.getSimpleName().equals(root.getNodeName())) {
					try {
						createMooshakObject(id, poName);
					} catch (Exception e) {
						// may be it already exists
					}
					mo = getMooshakObject(id + File.separator + poName);
	
					rootPath = id + File.separator + poName;
				} else if (container.getIdName().equalsIgnoreCase(poName)) {
					try {
						mo = getMooshakObject(id);
					} catch (MooshakException e) {
						LOGGER.log(Level.INFO, "Object " + container.getIdName()
								+ " not created yet!");
					}
	
					rootPath = id;
				} else {
					try {
						mo = getMooshakObject(id + File.separator
								+ poName);
					} catch (MooshakException e) {
						LOGGER.log(Level.INFO, "Object " + poName
								+ " not created yet!");
					}
	
					rootPath = id + File.separator + poName;
				}
			} else {
				Class<? extends PersistentObject> type = persistentObject.getClass();
				if (type.getSimpleName().equals(root.getNodeName())) {
					String parentId = persistentObject.getParent().getPath().toString();
					destroyMooshakObject(persistentObject.getPath().toString());
					createMooshakObject(parentId, poName);
					mo = getMooshakObject(parentId + File.separator + poName);
	
					rootPath = parentId + File.separator + poName;
				} else {
					throw new MooshakException("Could not import object in selected object");
				}
			}
			
		} catch (ClassCastException e) {
			throw new MooshakException("Problem casting classes", e);
		}

		if (mo == null)
			throw new MooshakException("Error creating object  " + poName + "!");

		PersistentObject persistent;
		try {
			persistent = PersistentObject.openPath(rootPath);
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading object " + rootPath,
					cause);
		}
		Class<? extends PersistentObject> objectType = persistent.getClass();
		for (Attribute attr : Attributes.getAttributes(objectType)) {
			if (attrs.getNamedItem(attr.getName()) == null)
				continue;
			if (attr.getType() == AttributeType.FILE)
				mo.setFieldValue(
						attrs.getNamedItem(attr.getName()).getNodeName(),
						new MooshakValue(attrs.getNamedItem(attr.getName())
								.getNodeName(), attrs.getNamedItem(
								attr.getName()).getNodeValue(), "".getBytes()));
			else
				mo.setFieldValue(
						attrs.getNamedItem(attr.getName()).getNodeName(),
						new MooshakValue(attrs.getNamedItem(attr.getName())
								.getNodeName(), attrs.getNamedItem(
								attr.getName()).getNodeValue()));
		}

		setMooshakObject(mo);

		NodeList childs = root.getElementsByTagName("*");
		for (int i = 0; i < childs.getLength(); i++) {
			Node child = childs.item(i);
			createStructureFromXMLNode(rootPath, child);
		}

		return rootPath;
	}

	private void createStructureFromXMLNode(String rootId, Node node)
			throws MooshakException {

		NamedNodeMap attrs = node.getAttributes();
		String id = "", name = "";
		try {
			
			if (attrs.getNamedItem("xml:id") != null) {
				
				String xmlId = attrs.getNamedItem("xml:id").getNodeValue();
	
				int index = xmlId.lastIndexOf(".");
				if (index > -1) {
					id = xmlId.substring(0, index)
							.replaceAll("\\.", File.separator);
					name = xmlId.substring(index + 1);
				} else
					name = xmlId;
			} else {
				
			
			}
		} catch (DOMException e) {
			e.printStackTrace();
		}

		MooshakObject mo = null;
			
		PersistentObject po = PersistentObject.open(Paths.get(rootId, id));
		if (po instanceof PersistentContainer<?>) {
			PersistentContainer<PersistentObject> container = 
					(PersistentContainer<PersistentObject>) po;
			Class<PersistentObject> type = container.getDescendantType();
		
			if (type.getSimpleName().equals(node.getNodeName())) {
				try {
					createMooshakObject(Paths.get(rootId, id).toString(), name);
				} catch (MooshakException e) {
					// maybe it already exists
				}
			}
		}
		
		try {
			mo = getMooshakObject(Paths.get(rootId, id, name).toString());
		} catch (MooshakException e) {
			LOGGER.log(Level.INFO, "Object " + name
					+ " not created yet!");
		}
		
		if (mo == null)
			throw new MooshakException("Error creating object " + name + "!");

		PersistentObject persistent;
		try {
			persistent = PersistentObject.openPath(mo.getId());
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading object " + mo.getId(),
					cause);
		}
		Class<? extends PersistentObject> objectType = persistent.getClass();
		for (Attribute attr : Attributes.getAttributes(objectType)) {
			if (attrs.getNamedItem(attr.getName()) == null)
				continue;
			if (attr.getType() == AttributeType.CONTENT)
				continue;
			if (attr.getType() == AttributeType.DATA)
				continue;
			if (attr.getType() == AttributeType.FILE)
				mo.setFieldValue(
						attrs.getNamedItem(attr.getName()).getNodeName(),
						new MooshakValue(attrs.getNamedItem(attr.getName())
								.getNodeName(), attrs.getNamedItem(
								attr.getName()).getNodeValue(), "".getBytes()));
			else
				mo.setFieldValue(
						attrs.getNamedItem(attr.getName()).getNodeName(),
						new MooshakValue(attrs.getNamedItem(attr.getName())
								.getNodeName(), attrs.getNamedItem(
								attr.getName()).getNodeValue()));
		}
		setMooshakObject(mo);

		NodeList childs = node.getChildNodes();
		for (int i = 0; i < childs.getLength(); i++) {

			Node child = childs.item(i);
			if (child.getNodeType() == Element.ELEMENT_NODE) {
				Element element = (Element) child;
				createStructureFromXMLNode(rootId, element);
			}
		}

	}

	/**
	 * Unzip files in given zip file to path
	 * 
	 * @param absolutePath
	 * @param content
	 */
	private void unzipFiles(String absolutePath, byte[] content) {
		try {
			byte[] buf = new byte[1024];
			ZipInputStream zipinputstream = null;
			ZipEntry zipentry;
			zipinputstream = new ZipInputStream(new ByteArrayInputStream(
					content));

			zipentry = zipinputstream.getNextEntry();
			while (zipentry != null) {
				String entryName = zipentry.getName();
				int n;
				FileOutputStream fileoutputstream;
				File newFile = new File(absolutePath + File.separator
						+ entryName);

				if (newFile.isDirectory())
					break;

				new File(newFile.getParent()).mkdirs();

				fileoutputstream = new FileOutputStream(newFile);

				while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
					fileoutputstream.write(buf, 0, n);

				fileoutputstream.close();
				zipinputstream.closeEntry();
				zipentry = zipinputstream.getNextEntry();

			}

			zipinputstream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Exports the mooshak object in id to a zip file
	 * 
	 * @param id
	 * @return 
	 * @throws MooshakException
	 */
	public String exportMooshakObject(String id) throws MooshakException {
		PersistentObject persistent;
		try {
			persistent = PersistentObject.openPath(id);
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading object " + id, cause);
		}
		
		ZipFile file = persistent.exportMe();

		try {
			return new String(Base64Coder.encode(Files.readAllBytes(
					persistent.getAbsoluteFile(file.getName()))));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			throw new MooshakException("Couldn't create zip file");
		}
	}

	/**
	 * Create dependent persistent objects, such as submissions inside contests
	 * 
	 * @param id
	 * @param type
	 * @param created
	 * @throws MooshakException
	 * @throws MooshakContentException
	 */
	public <T extends PersistentObject> void createDependants(T created)
			throws MooshakException, MooshakContentException {

		for (Attribute attribute : Attributes.getAttributes(created.getClass())) {
			if (attribute.getType() == AttributeType.DATA) {
				String subName = attribute.getName();
				Class<T> subType = attribute.getFieldType();

				T sub;

				try {
					sub = created.create(subName, subType);
				} catch (MooshakContentException cause) {
					throw new MooshakException("Creating sub object named "
							+ subName + " on " + created.getIdName(), cause);
				}
				sub.save();
			}
		}
	}

	/**
	 * Destroy the Mooshak Object identified by id
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	public <T extends PersistentObject> void destroyMooshakObject(String id)
			throws MooshakException {

		try {
			T persistent = PersistentObject.openPath(id);
			T parent = persistent.getParent();

			persistent.delete();

			createDependants(parent);

		} catch (MooshakContentException cause) {
			throw new MooshakException("Destroying a mooshak object:" + id,
					cause);
		}
	}

	/**
	 * Verifies if it is possible to recover this MooshakObject
	 * 
	 * @param id
	 * @param redo
	 * @return
	 * @throws MooshakException
	 */
	public boolean canRecover(String id, boolean redo) throws MooshakException {

		if (PersistentObject.openPath(id).isFrozen())
			return false;

		String object = id;
		String pathParent = "";

		if (id.contains(File.separator)) {
			object = id.substring(id.lastIndexOf(File.separator) + 1);
			pathParent = id.substring(0, id.lastIndexOf(File.separator));
		}

		try {

			BackupObject backup = BackupObject.getBackupObject(object);

			return backup.canRecover(
					PersistentObject.open(Paths.get(pathParent))
							.getAbsoluteFile(), redo);

		} catch (MooshakException cause) {
		}

		return false;
	}

	/**
	 * Create a new descendant with given name on the container with given id
	 * 
	 * @param id
	 * @param redo
	 * @throws MooshakException
	 */
	public MooshakObject recover(String id, boolean redo)
			throws MooshakException {

		if (!canRecover(id, redo))
			throw new MooshakException("Cannot recover");

		String object = id;
		String pathParent = "";

		if (id.contains(File.separator)) {
			object = id.substring(id.lastIndexOf(File.separator) + 1);
			pathParent = id.substring(0, id.lastIndexOf(File.separator));
		}

		PersistentObject po = PersistentObject.open(Paths.get(pathParent));
		try {

			BackupObject backup = BackupObject.getBackupObject(object);

			backup.recover(po.getAbsoluteFile(), redo);

		} catch (MooshakException cause) {
			throw new MooshakException("Recovering a mooshak object:" + id,
					cause);
		}

		EventSender.getEventSender().send(new ObjectUpdateEvent(id));

		return getMooshakObject(id);
	}

	/**
	 * Freezes the object with given id
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	public void freeze(String id) throws MooshakException {
		PersistentObject po = PersistentObject.open(Paths.get(id));

		for (PersistentObject child : po.getChildren(false)) {
			freeze(child.getPath().toString());
		}
		
		po.setFrozen(true);
	}

	/**
	 * Unfreezes the object with given id
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	public void unfreeze(String id) throws MooshakException {
		PersistentObject po = PersistentObject.open(Paths.get(id));

		for (PersistentObject child : po.getChildren(false)) {
			unfreeze(child.getPath().toString());
		}
		
		po.setFrozen(false);
	}

	/**
	 * Verifies if the object with given id is frozen or not
	 * 
	 * @param id
	 * @throws MooshakException
	 */
	public boolean isFrozen(String id) throws MooshakException {
		PersistentObject po = PersistentObject.open(Paths.get(id));

		return po.isFrozen();
	}

	/**
	 * Changes the type of a program file
	 * 
	 * @param problemId
	 * @param objectId
	 * @param value
	 * @param newType
	 */
	public void changeProgramType(String problemId, String objectId,
			MooshakValue value, String newType) throws MooshakException {
		MooshakObject solutions = getMooshakObject(problemId + File.separator
				+ "solutions");
		MooshakValue sols = solutions.getFieldValue("Solution");

		switch (newType) {
		case "Solution":
			destroyMooshakObject(objectId);

			sols.addFileValue(new MooshakValue("Solution", value.getName(),
					value.getContent()));
			solutions.setFieldValue("Solution", sols);
			setMooshakObject(solutions);
			break;
		case "Skeleton":

			if (value.getField().equals("Program")) {
				Problem problem = PersistentObject.openPath(problemId);
				problem.setProgram(null);
				problem.save();
			} else {
				sols.removeFile(value.getName());
				solutions.setFieldValue("Solution", sols);
				try {
					PersistentObject persistent = PersistentObject
							.openPath(solutions.getId());

					Files.delete(persistent.getAbsoluteFile(value.getName()));
				} catch (IOException e) {
					LOGGER.log(Level.SEVERE, "Could not delete file");
				}
			}

			MooshakObject skeletons = getMooshakObject(problemId
					+ File.separator + "skeletons");
			createMooshakObject(skeletons.getId(), "skeleton"
					+ getChildNextIndex(problemId
							.substring(problemId.lastIndexOf(File.separator) + 1),
							skeletons));

			MooshakObject skeleton = getMooshakObject(skeletons.getId()
					+ File.separator + "skeleton"
					+ getChildNextIndex(problemId
							.substring(problemId.lastIndexOf(File.separator) + 1), skeletons));
			skeleton.setFieldValue("Skeleton", new MooshakValue("Skeleton",
					value.getName(), value.getContent()));

			setMooshakObject(skeleton);
			setMooshakObject(solutions);

			break;

		default:
			break;
		}
	}

	public void removeFileFromObject(String objectId, String name)
			throws MooshakException {
		try {
			PersistentObject persistent = PersistentObject.openPath(objectId);

			Files.delete(persistent.getAbsoluteFile(name));
		} catch (IOException e) {
			throw new MooshakException("Error removing file");
		}
	}

	public MooshakValue getSolutionsValue(Session session, String problemId)
			throws MooshakException {

		Contest contest = session.getContest();

		Problems problems = contest.open("problems");

		Problem problem = problems.find(problemId);
		
		if (problem == null)
			throw new MooshakException("Problem not found!");

		return problem.getSolutionsAsValue();
	}

	/**
	 * Problem select options values
	 * @return
	 * @throws MooshakException
	 */
	public Map<String, List<String>> getProblemOptionsValues()
			throws MooshakException {
		
		Map<String, List<String>> values = new HashMap<>();

		List<String> names = new ArrayList<>();
		for (Problem.ProblemID id : Problem.ProblemID.values())
			names.add(id.toString());

		values.put("name", names);

		List<String> types = new ArrayList<>();
		for (Problem.ProblemType id : Problem.ProblemType.values())
			types.add(id.toString());

		values.put("type", types);

		List<String> difficulties = new ArrayList<>();
		for (Problem.ProblemDifficulty id : Problem.ProblemDifficulty.values())
			difficulties.add(id.toString());

		values.put("difficulty", difficulties);
		
		return values;
	}

	private static final RegExp REGEX_ID_NUMBER = RegExp.compile("([^0-9]*)(\\d+)([^\\d]*)$");
	public String createNewDefaultProblem(Session session, String objectId) 
			throws MooshakException {
		String id = "A";
		List<String> problemNames = new ArrayList<>();

		Contest contest = session.getContest();

		Problems problems = contest.open("problems");

		for (Problem problem : problems.getChildren(Problem.class, false)) {
			if(problem.getName() == null)
				problemNames.add(problem.getIdName());
			else
				problemNames.add(problem.getName().toString());
		}

		if (!problemNames.isEmpty()) {
			Collections.sort(problemNames, new Comparator<String>() {

				@Override
				public int compare(String s1, String s2) {
					if (!(REGEX_ID_NUMBER.test(s1) && REGEX_ID_NUMBER.test(s2))) {
						if (s1.length() == s2.length())
							return s1.compareTo(s2);
						else if (s1.length() < s2.length())
							return -1;
						else return 1;
					}
					
					MatchResult r1 = REGEX_ID_NUMBER.exec(s1);
					MatchResult r2 = REGEX_ID_NUMBER.exec(s2);
					
					if (r1.getGroupCount() == r2.getGroupCount()) {
						for (int i = 1; i < r1.getGroupCount(); i++) {
							if (i == 2) {
								Integer i1 = Integer.parseInt(r1.getGroup(i));
								Integer i2 = Integer.parseInt(r2.getGroup(i));
								
								if (i1.compareTo(i2) != 0)
									return i1.compareTo(i2);
							}
	
							if (!r1.getGroup(i).equals(r2.getGroup(i)))
								return r1.getGroup(i).compareTo(r2.getGroup(i));
						}
					}
					
					return s1.compareTo(s2);
				}
			});
			String lastName = problemNames.get(problemNames.size() - 1);
			
			MatchResult matchResult = REGEX_ID_NUMBER.exec(lastName);
			if (matchResult != null) {
				int n = Integer.parseInt(matchResult.getGroup(2));
				n++;
				id = matchResult.getGroup(1) + n + matchResult.getGroup(3);
			}
			else {
				id = "";
				boolean incremented = false;
				for (int i = lastName.length() - 1; i >= 0; i--) {
					char c = lastName.charAt(i);
					if (c >= 'A' && c <= 'Z' && !incremented) { 
						if (c == 'Z')
							id = 'A' + id;
						else {
							id = (char) (c + 1) + id;
							incremented = true;
						}
					}
					else if (c >= 'a' && c <= 'z' && !incremented) {
						if (c == 'z')
							id = 'a' + id;
						else {
							id = (char) (c + 1) + id;
							incremented = true;
						}
					}
					else 
						id = c + id;
					
				}
				
				if (!incremented) {
					char c = lastName.charAt(0);
					if (c >= 'A' && c <= 'Z')
						id = 'A' + id;
					else 
						id = 'a' + id;
				}
			}
		}

		AdministratorManager.getInstance()
			.createMooshakObject(objectId, id);

		// Set problem name to avoid errors on getProblems
		contest = session.getContest();
		problems = contest.open("problems");
		Problem problem = problems.open(id);
		problem.setName(id);
		problem.save();
		
		return id;
	}

	public MooshakObject addNewDefaultTest(String objectId) 
			throws MooshakException {

		MooshakObject object = addNewTest(objectId);
		
		MooshakValue input = new MooshakValue("input", "input.in",
				"".getBytes());
		MooshakValue output = new MooshakValue("output", "output.out",
				"".getBytes());
		
		object.setFieldValue("input", input);
		object.setFieldValue("output", output);
		
		setMooshakObject(object);
		
		return getMooshakObject(object.getId());
	}

	public void updateTestsResult(Session session, String problemId) 
			throws MooshakException {
		MooshakObject problem = getMooshakObject(problemId);
		
		MooshakObject tests = getMooshakObject(problemId + File.separator 
				+ "tests");
		
		try {
			runTestsAgainstSolutions(session, problem, tests);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Could not test uploaded solution:\n" +
					e.getMessage());
		}
	}

	/**
	 * Remove a solution from the problem
	 * @param problemId
	 * @param name
	 * @throws MooshakException 
	 */
	public void removeSolution(Session session, String problemId, String name) 
			throws MooshakException {
		Problem problem = PersistentObject.openPath(problemId);
		if (problem.getProgram() != null && 
				name.equals(problem.getProgram().getFileName().toString())) {
			problem.setProgram(null);
			problem.save();
			
			try {
				Files.delete(problem.getAbsoluteFile(name));
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Could not delete file");
			}
			
			return;
		}
			
		MooshakObject solutions = getMooshakObject(problemId + File.separator
				+ "solutions");
		try {
			PersistentObject persistent = PersistentObject.openPath(solutions.getId());
			
			LOGGER.log(Level.SEVERE, persistent.getAbsoluteFile(name).toString());
			Files.delete(persistent.getAbsoluteFile(name));
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Could not delete file");
		}
		solutions.setFieldValue("Solution", problem.getSolutionsAsValue());
		setMooshakObject(solutions);
		
		runTestsAgainstSolutions(session, getMooshakObject(problem.getPath().toString()), 
				getMooshakObject(problem.open("tests").getPath().toString()));
	}

	/**
	 * Remove a skeleton from the problem
	 * @param session
	 * @param problemId
	 * @param name
	 * @throws MooshakException
	 */
	public void removeSkeleton(Session session, String problemId, String name) 
			throws MooshakException {
		MooshakObject skeletons = getMooshakObject(problemId + File.separator
				+ "skeletons");
		
		List<String> children = skeletons.getChildren();
		for (String child : children) {
			MooshakObject skeleton = getMooshakObject(child);
			MooshakValue skelFiles = skeleton.getFieldValue("Skeleton");
			
			if (skelFiles.getName().contains(name)) {
				skelFiles.removeFile(name);
				skeletons.setFieldValue("Skeleton", skelFiles);
			
				try {
					PersistentObject persistent = PersistentObject.openPath(skeleton.getId());
					
					Files.delete(persistent.getAbsoluteFile(name));
					
				} catch (IOException e) {
					throw new MooshakException("Could not delete file", e);
				}
				destroyMooshakObject(child);
				return;
			}
		}
	}

	/**
	 * Paste persistent object with given copiedId in object with given id
	 * @param id
	 * @param copiedId
	 * @throws MooshakException 
	 */
	public void pasteMooshakObject(String id, String copiedId) 
			throws MooshakException {
		
		MooshakObject toCopy = getMooshakObject(copiedId);
		
		MooshakObject parent = getMooshakObject(id);
		
		// Check type match
		Class<? extends PersistentObject> parentType;

		try {
			parentType = PersistentObject.loadType(parent.getType());
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading type " + parent.getType(), cause);
		}

		Class<?> contentType = PersistentContainer.getContainedClass(parentType);
		
		Class<? extends PersistentObject> copiedType;

		try {
			copiedType = PersistentObject.loadType(toCopy.getType());
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading type " + toCopy.getType(), cause);
		}
		
		for (Attribute attribute : Attributes.getAttributes(parentType)) {
			if (attribute.getType() == AttributeType.DATA) {
				if (attribute.getFieldType().equals(copiedType)) {
					String copyName = toCopy.getId();
					if (toCopy.getId().lastIndexOf("/") >= 0)
						copyName = toCopy.getId().substring(toCopy.getId().lastIndexOf("/") + 1);
					
					pasteMooshakObjectWithName(id, copiedId, copyName);
					return;
				}
			}
		}
		
		if (contentType == null || !copiedType.equals(contentType)) {
			
			if (copiedType.equals(parentType)) {
				
				String parentId = "/", name = id.substring(id.lastIndexOf("/") + 1);
				if (id.lastIndexOf("/") >= 0) 
					parentId = id.substring(0, id.lastIndexOf("/"));
				if (id.equals(parentId + "/" + name))
					destroyMooshakObject(id);
				pasteMooshakObjectWithName(parentId, copiedId, name);
				if (!id.equals(parentId + "/" + name))
					destroyMooshakObject(id);
				return;
			}
			
			throw new MooshakException("Can't copy here");
		}
		
		String copyName = toCopy.getId();
		if (toCopy.getId().lastIndexOf("/") >= 0)
			copyName = toCopy.getId().substring(toCopy.getId().lastIndexOf("/") + 1);
		
		if (copiedType.equals(contentType)) {
			PersistentObject p = PersistentObject.openPath(id);
			try {
				p.open(copyName);
				copyName += "_copy";
			} catch (MooshakException e) {
				// ignore
			}
		}
		
		pasteMooshakObjectWithName(id, copiedId, copyName);
		
	}

	/**
	 * Paste a Mooshak Object with a new name
	 * @param id
	 * @param toCopy
	 * @param copyName
	 * @throws MooshakException
	 */
	private void pasteMooshakObjectWithName(String id, String copiedId, 
			String copyName) throws MooshakException {
		
		final Path sourceDir = Paths.get(PersistentObject.getHomePath().toString(), copiedId);

		final Path targetDir = Paths.get(PersistentObject.getHomePath().toString(), id, copyName);
		new File(targetDir.toString()).mkdir();
		
		try {
			Files.walkFileTree(sourceDir, EnumSet.of(FileVisitOption.FOLLOW_LINKS), Integer.MAX_VALUE,
				 new SimpleFileVisitor<Path>() {
				    @Override
				    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException  {
				        Path target = targetDir.resolve(sourceDir.relativize(dir));
				        try {
				            Files.copy(dir, target);
				        } catch (FileAlreadyExistsException e) {
				             if (!Files.isDirectory(target)) {
				                 Files.delete(target);
				                 Files.copy(dir, target);
				             }
				        }
				        return FileVisitResult.CONTINUE;
				    }
				    @Override
				    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				    	Path target = targetDir.resolve(sourceDir.relativize(file));
				    	try {
				            Files.copy(file, target);
				        } catch (FileAlreadyExistsException e) {
				             if (!Files.isDirectory(target)) {
				                 Files.delete(target);
				                 Files.copy(file, target);
				             }
				        }
				        return FileVisitResult.CONTINUE;
				    }
			});
		} catch (IOException e) {
			e.printStackTrace();
			throw new MooshakException("Error: " + e.getMessage());
		}
		
		getMooshakObject(id + "/" + copyName);
	}

	/**
	 * Rename a PO identified by id to name
	 * @param id
	 * @param name
	 * @throws MooshakException 
	 */
	public void renameMooshakObject(String id, String name) throws MooshakException {

		if (!REGEX_VALID_MOOSHAK_OBJECT_ID.test(name)) {
			throw new MooshakException("Invalid name for a Mooshak Object");
		}
		
		String parentId = "/";
		if (id.lastIndexOf("/") >= 0) {
			parentId = id.substring(0, id.lastIndexOf("/"));
			String lastName = id.substring(id.lastIndexOf("/") + 1);
			
			if (lastName.equals(name))
				throw new MooshakException("Can't rename to the same name!");
		}

		MooshakObject mo = getMooshakObject("data/contests");
		PersistentObject ref = PersistentObject.openPath(id);
		updateObjectReferences(ref, id, parentId + "/" + name, mo);
		
		pasteMooshakObjectWithName(parentId, id, name);
		
		destroyMooshakObject(id);
	}

	/**
	 * Update references to this Object
	 * @param prevId
	 * @param newId
	 * @param mo 
	 * @throws MooshakException 
	 */
	private void updateObjectReferences(PersistentObject ref, String prevId, String newId, MooshakObject mo) 
			throws MooshakException {
		
		Class<? extends PersistentObject> refType = ref.getClass();

		PersistentObject persistent = PersistentObject.openPath(mo.getId());
		Class<? extends PersistentObject> objectType = persistent.getClass();

		Collection<Attribute> attributes = Attributes.getAttributes(objectType);
		for (Field field : persistent.getClass().getDeclaredFields()) {
			String fieldName = field.getName();

			try {
				switch (field.getType().getSimpleName()) {
				case "Path":
					MooshakAttribute ma = field.getAnnotation(MooshakAttribute.class);
					if (ma != null && ma.refType() != null 
							&& ma.refType().equals(refType.getSimpleName())) {
						
						for (Attribute attribute : attributes) {
							if (fieldName.equalsIgnoreCase(attribute.getName())) {
								String value = Attributes.getStringValue(attribute, persistent);
								if (value != null) {
									String pathId =persistent.getPath().resolve(ma.complement() + "/" 
											+ value).toString();
									if (pathId != null) {
										PersistentObject persistentRef = PersistentObject.openPath(pathId);
										if (ref.getAbsoluteFile().normalize().equals(persistentRef
												.getAbsoluteFile().normalize())) {
											Attributes.setStringValue(attribute, persistent, Paths.get(newId)
													.getFileName().toString());
											persistent.save();
										}
									}
								}
								
								break;
							}
						}
					}
						
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// ignore silently
			}
		}
		
		for (String child : mo.getChildren()) {
			try {
				updateObjectReferences(ref, prevId, newId, getMooshakObject(child));
			} catch (MooshakException e) {
				// silently ignore
			}
		}
	}

	/**
	 * Check whether PO is renameable or not
	 * @param id
	 * @return
	 * @throws MooshakException 
	 */
	public boolean isRenameable(String id) throws MooshakException {
		
		PersistentObject persistent;
		try {
			persistent = PersistentObject.openPath(id);
		} catch (MooshakContentException cause) {
			throw new MooshakException("Error loading object " + id, cause);
		}
		return persistent.isRenameable();
	}

	/**
	 * Switch profile from admin to another profile
	 * @param session
	 * @param contest
	 * @param profile
	 */
	public void switchProfile(Session session, String contestName, 
			String profileName) throws MooshakException {
		
		Contests contests = PersistentObject.openPath("data/contests");
		Contest contest = contests.find(contestName);
		
		session.setContest(contest);
		
		Authenticable person;
		
		if((person = AuthManager.getInstance().globalUser(profileName)) == null &&
		   (person = AuthManager.getInstance().localUser(contestName,profileName)) == null)
			throw new MooshakException("Unknown user");
		session.setProfile(person.getProfile());
	}


	/**
	 * Switch profile back to admin
	 * @param session
	 * @param contest
	 * @param profile
	 * @throws MooshakException 
	 */
	public void switchProfileBackToAdmin(Session session) throws MooshakException {
		Authenticable person;
		
		if (!session.getParticipant().getIdName().equals("admin"))
			throw new MooshakException("Not allowed to login as admin!");
		
		if((person = AuthManager.getInstance().globalUser("admin")) == null)
			throw new MooshakException("Unknown user");
		session.setProfile(person.getProfile());
	}
			
	
	public List<String> searchFor(String term,boolean nameNotContent) 
			throws MooshakException {
		Path home = PersistentObject.getHomePath();
		List<String> list = new ArrayList<>();
		FileVisitor<Path> visitor;
		Pattern pattern;		
		
		try {
			pattern = Pattern.compile(term);
		} catch(PatternSyntaxException cause) {
			throw new 
				MooshakException("Invalid regular expression\n"+term,cause);
		}
			
		if(nameNotContent) {
			visitor = new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					
					Path fileName = dir.getFileName();
					if(fileName != null) {
						String name = fileName.toString();
						if(pattern.matcher(name).find()) 
							list.add(home.relativize(dir).toString());
					}	
					return FileVisitResult.CONTINUE;
				}
			};
		} else { // contentNotName
			visitor = new SimpleFileVisitor<Path>() {

				@Override
				public FileVisitResult preVisitDirectory(Path dir,
						BasicFileAttributes attrs) throws IOException {
					
					Path data = dir.resolve(".data.tcl");
					if(Files.exists(data) && fileContentMatches(data,pattern))
						list.add(home.relativize(dir).toString());
					
					return FileVisitResult.CONTINUE;
				}
			};
		}
		
		
		try {
			Files.walkFileTree(home,visitor);
		} catch (IOException cause) {
			throw new MooshakException(
					"I/O error while searching data objects' "+
							(nameNotContent ? "name" : "content"),cause);
		}
		
		return list;
	}
	
	private static final Pattern LINE_CONTENT = 
			Pattern.compile("set\\s+\\w+\\s+\\{?(.*)\\}?");
	
	/**
	 * Checks if any field of given file content of PO matches pattern.
	 * Only the part of the file with field content is actually checked  
	 * @param file	to be checked
	 * @param pattern	to search on content
	 * @return {@code true} if file content matches pattern; 
	 * 		otherwise {@code false}  
	 * @throws IOException
	 */
	private boolean fileContentMatches(Path file,Pattern pattern) 
			throws IOException {
		
		for(String line: Files.readAllLines(file)) {
			Matcher contentMatcher = LINE_CONTENT.matcher(line);
			if(contentMatcher.matches()) {
				if(pattern.matcher(contentMatcher.group(1)).find())
						return true;
			}			
		}

		return false;
	}
	
	/**
	 * Information of the server status
	 * 
	 * @return
	 */
	public ServerStatus getServerStatus() {
		ServerStatus serverStatus = new ServerStatus();
		 
		 serverStatus.setSessionCount(
				 Configurator.getSessionCount());
		 serverStatus.setPersistentObjectCount(
				 PathManager.getPersistentObjectCount());
		 serverStatus.setEvaluationsInProgress(
				 Evaluator.getEvaluationsInProgress());
		 serverStatus.setActiveThreadCount(
				 Thread.activeCount());
		 serverStatus.setAvailableProcessors(
				 OS.getAvailableProcessors());
		  serverStatus.setSystemLoadAverage(
				 OS.getSystemLoadAverage());
		 serverStatus.setFreeMemory(
				 RUNTIME.freeMemory());
		 serverStatus.setMaxMemory(
				 RUNTIME.maxMemory());
		 
	      if(LINUX != null) {
	    	  
	    	  serverStatus.setMaxFileDescriptorCount(
	    			  LINUX.getMaxFileDescriptorCount());
	    	  serverStatus.setOpenFileDescriptorCount(
	    			  LINUX.getOpenFileDescriptorCount());
	    	  
	    	}
	      
	      return serverStatus;
	}
	
	/**
	 * Compare two strings and return a list of changes
	 * @param obtained
	 * @param expected
	 * 
	 * @return string with the differences
	 */
	public String diffStrings(String obtained, String expected) {
	 
		String[] lines1 = obtained.split("\\r?\\n");
		String[] lines2 = expected.split("\\r?\\n");

		FilesDiff fdUtils = new FilesDiff(lines1, lines2);
		
		Change rslt = fdUtils.diff_2(false);
	    FilesDiffPrint.UnifiedPrint dp = new FilesDiffPrint.UnifiedPrint(lines1, lines2);
	    StringWriter sw = new StringWriter();
	    dp.setOutput(sw);
	    dp.print_script(rslt);
	    return sw.toString();
	}

	/**
	 * Get a script file
	 * @param path
	 * @return a script file
	 * @throws MooshakException 
	 */
	public String getScriptFile(String path) throws MooshakException {
		
		Path webappFolderPath = new File(".").getAbsoluteFile().toPath();
		
		if(webappFolderPath == null)
			throw new MooshakException("Cannot find webapp folder path");
		
		String catalina = null;
		Path logsPath = null;
		
		if((catalina = System.getProperty("catalina.base")) == null) {
			logsPath = webappFolderPath.resolve(Configurator.LOG_FOLDER);
		} else {
			logsPath = Paths.get(catalina).resolve("logs").resolve("mooshak");
		}
		
		Path scriptsPath = logsPath.resolve("scripts");
		Path scriptPath = scriptsPath.resolve(path);
		
		try {
			return String.join("\n", Files.readAllLines(scriptPath));
		} catch (IOException e) {
			throw new MooshakException("Error reading script file");
		}
	}
	
	public List<List<String>> getTaskList(String script) throws MooshakException {
		
		try {
			ScriptFileParser parser = new ScriptFileParser(script);
			List<List<String>> taskList = parser.parseScript();
			
			return taskList;
		} catch (IOException e) {
			throw new MooshakException("Error parsing script: " + e.getMessage());
		}
		
	}

	
	/**
	 * Check if languages are configured
	 * @param session
	 * @return errors in languages configuration
	 * @throws MooshakException 
	 */
	public String checkLanguages(Session session) throws MooshakException {
		
		Contest contest = session.getContest();
		
		StringBuilder sbErrors = new StringBuilder();
		
		Languages languages = contest.open("languages");
		
		List<Language> langs = languages.getContent();
		
		if (langs.size() <= 0)
			sbErrors.append("No languages defined. This may cause some problems!\n");
		
		return sbErrors.toString();
	}

	/**
	 * Remove file from problem
	 * @param session 
	 * @param objectId id of object
	 * @param fileName 
	 * @throws MooshakException 
	 */
	public void removeFile(Session session, String objectId, String field, String fileName) 
			throws MooshakException {
		
		MooshakObject mo = getMooshakObject(objectId);
		mo.setFieldValue(field, null);
		setMooshakObject(mo);

		try {
			removeFileFromObject(objectId, fileName);
		} catch (MooshakException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}
	
}
