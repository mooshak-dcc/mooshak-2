package pt.up.fc.dcc.mooshak.content.types;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.UserPrincipal;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.evaluation.EvaluationParameters;
import pt.up.fc.dcc.mooshak.evaluation.MooshakSafeExecutionException;
import pt.up.fc.dcc.mooshak.evaluation.SafeExecution;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * Programming language available for submission and related operations:
 * compilation and execution in a safe environment
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @version 2.0
 * @since June 2012
 * 
 *         Recoded in Java in June 2012 
 *         From a Tcl module coded in April 2001
 */
public class Language extends PersistentObject {
	private static final long serialVersionUID = 1L;

	private static final int KBYTE = 1024;

	@MooshakAttribute(
			name = "Fatal", 
			type = AttributeType.LABEL)
	private String fatal = null;

	@MooshakAttribute(
			name = "Warning", 
			type = AttributeType.LABEL)
	private String warning = null;

	@MooshakAttribute(
			name = "Name", 
			tip = "Language name (ex: Java)")
	private String name = null;

	@MooshakAttribute(
			name = "Extension", 
			tip = "Extension used for program files, without the dot (ex: java")
	private String extension = null;

	@MooshakAttribute(
			name = "Compiler", 
			tip = "Name of compiler (ex: jdk)")
	private String compiler = null;

	@MooshakAttribute(
			name = "Version", 
			tip = "Version of compiler (ex: 1.7)")
	private String version = null;

	@MooshakAttribute(name = "Compile", 
			tip = "Command line for compilation (ex: javac $file)", 
			help = "Command line for compilation, " +
					"including the following variables:\n\n"
			+ " 		$home	- Mooshak's home directory\n"
			+ "         $file   - submission file name (with extension)\n"
			+ "         $name   - submission file name (without extension)\n"
			+ "         $extension  - extension\n"
			+ "			$environment - Problem environment file\n"
			+ "			$solution - Problem solution\n"
			+ "			$gameArtifact - Artifact ID of the game (just available for games)\n"
			+ "			$gamePackage - Path to game package (just available for games)\n"
			+ "			$gameExtras - Path to game extras (just available for games)\n")
	private String compile = null;

	@MooshakAttribute(name = "Execute", 
			tip = "Command line for execution (ex: java -classpath . $name)", 
			help = "Command line for execution, " +
					"including the following variables:\n\n"
			+ "  		$home	- Mooshak's home directory\n"
			+ "         $file   - submission file name (with extension)\n"
			+ "         $name   - submission file name (without extension)\n"
			+ "                   	$extension  - extension\n"
			+ "		   	$args    - arguments defined for each test\n"
			+ "		   	$context - context file for each test\n"
			+ "			$gameArtifact - Artifact ID of the game (just available for games)\n")
	private String execute = null;

	@MooshakAttribute(
			name = "Data", 
			type = AttributeType.LONG, 
			tip = "Maximum size of data segment in bytes "
			+ "[Overrides MaxData defined in Languages]")
	private Long data = null;

	@MooshakAttribute(
			name = "Fork", 
			type = AttributeType.LONG, 
			tip = "Maximum forks in execution "
			+ "[Overrides MaxExecFork defined in Languages] ")
	private Long fork = null;

	@MooshakAttribute(
			name = "Omit", 
			tip = "Regular expression matching lines "
			+ "to be ommited from the compilers output")
	private String omit = null;

	@MooshakAttribute(name = "UID", 
			type = AttributeType.INTEGER, 
			tip = "Fixed User ID to compile and execute execute (Mono),"
			+ "\n overrides the MINUID-MAXUID range")
	private Integer uid = null;
	
	@MooshakAttribute(
			name="EditFileContents",
			type=AttributeType.MENU,
		    tip="Determines if the files uploaded by participants must be editable")
	private MooshakAttribute.YesNo editableContents = null;

	@MooshakAttribute(
			name="Configuation",
			type=AttributeType.FILE,
			tip="Language configuration file")
	private Path configuration = null;
	
	//-------------------- Setters and getters ----------------------//
	
	/**
	 * Fatal errors messages of this folder
	 * @return
	 */
	public String getFatal() {
		if(fatal == null)
			return "";
		else
			return fatal;
					
	}
	
	/**
	 * Set fatal error messages
	 * @param fatal
	 */
	public void setFatal(String fatal) {
		this.fatal = fatal;
	}

	
	/**
	 * Warning errors messages of this folder
	 * @return
	 */
	public String getWarning() {
		if(warning == null)
			return "";
		else
			return warning;
					
	}
	
	/**
	 * Set warning error messages
	 * @param fatal
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	/**
	 * Get name of this language
	 * @return
	 */
	public String getName() {
		if(name == null)
			return "";
		else
			return name;
	}

	/**
	 * Set name of this language
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get extension used for program files, without the dot (ex: java") 
	 * @return
	 */
	public String getExtension() {
		if(extension == null)
			return "";
		else
			return extension;
	}

	/**
	 * Set extension used for program files, without the dot (ex: java") 
	 */
	public void setExtension(String extension) {
		this.extension = extension;
	}

	/**
	 * Get name of compiler (ex: JDK)
	 * @return
	 */
	public String getCompiler() {
		if(compiler == null)
			return "";
		else
			return compiler;
	}

	/**
	 * Set name of compiler (ex: JDK)
	 * @param compiler
	 */
	public void setCompiler(String compiler) {
		this.compiler = compiler;
	}

	/**
	 * get version of compiler (ex: 1.7.0-ea)
	 * @return
	 */
	public String getVersion() {
		if(version == null)
			return "";
		else
			return version;
	}

	/**
	 * Set version of compiler (ex: 1.7.0-ea)
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/**
	 * Get command line for compilation
	 * @return
	 */
	public String getCompile() {
		if(compile == null)
			return "";
		else
			return compile;
	}

	/**
	 * Set command line for compilation
	 * @param compile
	 */
	public void setCompile(String compile) {
		this.compile = compile;
	}

	/**
	 * Get command line for execution
	 * @return
	 */
	public String getExecute() {
		if(execute == null)
			return "";
		else
			return execute;
	}

	/**
	 * Set command line for execution
	 * @param execute
	 */
	public void setExecute(String execute) {
		this.execute = execute;
	}

	/**
	 * Get maximum size of data segment in bytes 
	 * [Overrides MaxData defined in Languages]
	 * @return
	 */
	public Long getData() {
		if(data == null)
			return 0L;
		else
			return data;
	}

	/**
	 * Set maximum size of data segment in bytes 
	 * [Overrides MaxData defined in Languages]
	 * @param data
	 */
	public void setData(Long data) {
		this.data = data;
	}

	/**
	 * Get maximum forks in execution 
	 * [Overrides MaxExecFork defined in Languages]
	 * @return
	 */
	public Long getFork() {
		if(fork == null)
			return 0L;
		else
			return fork;
	}

	/**
	 * Set maximum forks in execution 
	 * [Overrides MaxExecFork defined in Languages] 
	 * @param fork
	 */
	public void setFork(Long fork) {
		this.fork = fork;
	}

	/**
	 * Get the regular expression string matching the lines 
	 * to be omitted from the compilers output
	 * @return
	 */
	public String getOmit() {
		if(omit == null)
			return "";
		else
			return omit;
	}

	/**
	 * Set the regular expression string matching the lines 
	 * to be omitted from the compilers output	  
	 * @param omit regular expression string
	 */
	public void setOmit(String omit) {
		this.omit = omit;
	}

	/**
	 * Set fixed User ID to compile and execute (old versions of Mono)
	 * overrides the MINUID-MAXUID range
	 * @return
	 */
	public Integer getUid() {
		return uid;
	}

	/**
	 * Set fixed User ID to compile and execute (old versions of Mono)
	 * overrides the MINUID-MAXUID range
	 * @param uid
	 */
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	
	/**
	 * Must the files uploaded by participants be editable?
	 * 
	 * @return <code>false</code> only if explicitly stated; 
	 * 		<code>true</code> otherwise 
	 */
	public boolean isEditableContents() {
		
		if(YesNo.NO.equals(editableContents))
			return false;
		else
			return true;		
	}
	
	/**
	 * Set if files uploaded by participants must be editable
	 * 
	 * @param <code>false</code> only if explicitly stated; 
	 * 		<code>true</code> otherwise 
	 */
	public void setEditableContents(boolean editableContents) {
		if(editableContents)
			this.editableContents = YesNo.YES;
		else
			this.editableContents = YesNo.NO;
			
	}

	/**
	 * Set a file to complement the language definition
	 * @return
	 */
	public Path getConfiguration() {
		if(configuration == null)
			return null;
		else
			return getPath().resolve(configuration);
	}

	/**
	 * Set a configuration file to complement the language definition 
	 * @param configuration
	 */
	public void setConfiguration(Path configuration) {
		this.configuration = configuration.getFileName();
	}
	//-------------------- Methods ----------------------//
	
	/**
	 * Compile a file in this programming language
	 * 
	 * @param parameters
	 *            parameters collected and used throughout the evaluation
	 * @throws MooshakTypeException
	 *             on IO or compilation errors
	 * @throws MooshakSafeExecutionException
	 *             on execution errors
	 * @return compilation error messages, 
	 * 				an empty string if compilation succeeds
	 */
	public String compile(EvaluationParameters parameters)
			throws MooshakTypeException, MooshakSafeExecutionException {

		String commandLine = expandCommandLine(compile,parameters);
		String limits = limits(parameters, true);

		SafeExecution safeExecution = new SafeExecution();

		parameters.setMonitorResources(true); // force 
		
		safeExecution.setParameters(parameters);
		safeExecution.setCommandLine(commandLine);
		safeExecution.setLimits(limits);

		safeExecution.execute();

		String errorInfo = safeExecution.getSafeExecutionErrors();
		String output;
		
		if("OK".equals(errorInfo) || "".equals(errorInfo))
			output = safeExecution.getOutput() + safeExecution.getErrors();
		else
			output = "SafeExec error during compilation:"+errorInfo;
		
		// remove "valid" parts of error (again, fpc banners)
		if (omit != null && ! "".equals(omit)) 
			output = output.replaceAll(omit, "").trim();
		
		return output;
	}

	/**
	 * Create a safe execution with given parameters
	 * 
	 * @param parameters
	 *            parameters collected and used throughout the evaluation
	 * @throws MooshakTypeException
	 *             on IO on IO errors
	 * @throws MooshakSafeExecutionException
	 *             on compilation errors
	 * @return SafeExecution  that can be executed and analyzed
	 */
	public SafeExecution newSafeExecution(EvaluationParameters parameters)
			throws MooshakTypeException, MooshakSafeExecutionException {

		String commandLine = expandCommandLine(execute,parameters);
		String limits = limits(parameters, false);

		SafeExecution safeExecution = new SafeExecution();

		safeExecution.setParameters(parameters);
		safeExecution.setCommandLine(commandLine);
		safeExecution.setLimits(limits);

		return safeExecution;
	}

	
	/**
	 * Make command line by expanding variables in template by their
	 * current values in collected parameters
	 * 
	 * @param parameters
	 *            collected and used throughout the evaluation
	 * @return execution command line as String
	 */
	String expandCommandLine(String command,EvaluationParameters parameters) {
		return expand(command, parameters.collectVariables());
	}
	

	/**
	 * Replace variables by their value on a command line where their names are
	 * prefixed with a dollar. The values assigned to variables are given as a
	 * map with the variable (without the dollar) as key. Undefined variables
	 * are not replaced.
	 * 
	 * @param command
	 *            command line with variables
	 * @param vars
	 *            map with variable assignments
	 * @return command line with variables expanded
	 */
	 public String expand(String command, Map<String, String> vars) {
		StringBuffer expanded = new StringBuffer(command);
		int start, end, last = 0;
		String name, value;

		while ((start = expanded.indexOf("$", last)) != -1) {

			for (end = start + 1; end < expanded.length()
					&& Character.isLetter(expanded.charAt(end)); end++)
				;
			name = expanded.substring(start + 1, end);
			if (vars.containsKey(name)) {
				value = vars.get(name);
				expanded.replace(start, end, value);
				last = start + value.length();
			} else {
				last = end;
			}
		}
		return expanded.toString();
	}
	
	/**
	 * Use as User ID one already in use in this directory to avoid write
	 * permission permission errors when rewriting object files
	 * 
	 * @param diretory
	 * @return
	 * @throws MooshakException
	 */
	int reuseUserId(Path directoryFile) throws MooshakTypeException {
		int uid = 0;

		Languages languages;
		try {
			languages = getParent();
		} catch (MooshakContentException cause) {
			String message = "Cannot load languages configurations";
			throw new MooshakTypeException(message, cause);
		}
		Path directory = PersistentCore.getAbsoluteFile(directoryFile);
		
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(directory)){

			UserPrincipal owner = null;
			int itemUid;
			for (Path item : stream) {
				try {
					owner = Files.getOwner(item);
				} catch (IOException e) {

				}
				try {
					itemUid = Integer.parseInt(owner.getName());
				} catch (NumberFormatException e) {
					itemUid = -1;
				}
				if (
					itemUid >= languages.getMinUID() && 
					itemUid <= languages.getMaxUID()
				) {
				return itemUid;
				}
			}
		} catch (IOException cause) {
			String message = "IO error reading file is submission directory";
			throw new MooshakTypeException(message, cause);
		}

		return uid;
	}

	/**
	 * Options for limits to include in command line
	 * 
	 * @param parameters
	 *            collected and used trough execution
	 * @param isCompiling
	 *            true if limits are for a compilation; false for execution
	 * @return string with limits
	 * @throws MooshakException
	 */
	String limits(EvaluationParameters parameters, boolean isCompiling)
			throws MooshakTypeException {
		StringBuilder flags = new StringBuilder();

		Languages languages = parameters.getLanguages();
		flags.append(" --core ");
		flags.append(languages.getMaxCore() / KBYTE);

		flags.append(" --mem ");
		if (data != null)
			flags.append(data / KBYTE);
		else
			flags.append(languages.getMaxData() / KBYTE);

		addCpuLimitFlags(parameters, isCompiling, flags);

		flags.append(" --clock ");
		flags.append(languages.getRealTimeout());

		addUserIDsLimitFlags(parameters, isCompiling, flags);

		addForkLimitFlags(parameters, isCompiling, flags);

		return flags.toString();
	}

	/**
	 * Add safeeexec (sandbox) flags to limit CPU timeout in process execution
	 * 
	 * @param loaded
	 *            persistent object with global parameters
	 * @param isCompiling
	 *            CPU usage differs if it is a compilation
	 * @param flags
	 *            string buffer where flags are added
	 * @throws MooshakTypeException
	 */
	private void addCpuLimitFlags(EvaluationParameters parameters,
			boolean isCompiling, StringBuilder flags)
			throws MooshakTypeException {
		flags.append(" --cpu ");
		if (isCompiling)
			flags.append(parameters.getLanguages().getCompTimeout());
		else
			flags.append(parameters.getLanguages().getExecTimeout());
	}

	// must be static otherwise executions of different languages collide
	private static int serialUid = 0;
	
	/**
	 * Add safeeexec (sandbox) flags to limit the user IDs for process execution
	 * 
	 * @param directory
	 *            where execution takes place
	 * @param loaded
	 *            persistent object with global parameters
	 * @param flags
	 *            string buffer where flags are added
	 * @throws MooshakException
	 */
	private void addUserIDsLimitFlags(
			EvaluationParameters parameters,
			boolean isCompiling,
			StringBuilder flags) throws MooshakTypeException {

		int minUid = parameters.getLanguages().getMinUID();
		int maxUid = parameters.getLanguages().getMaxUID();
		int reuseUid;

		if (uid != null)
			minUid = maxUid = uid.intValue();
		else if (isCompiling && 
				(reuseUid = reuseUserId(parameters.getDirectory())) != 0)
			minUid = maxUid = reuseUid;
		else {
			synchronized (this) {
				if(serialUid < minUid || serialUid == maxUid)
					serialUid = minUid;
				minUid = maxUid = serialUid++;
			}
		}

		flags.append(" --uids ");
		flags.append(minUid);
		flags.append(' ');
		flags.append(maxUid);

	}

	/**
	 * Add safeeexec (sandbox) flags to limit the number of processes (forks)
	 * allowed in an execution
	 * 
	 * @param loaded
	 *            persistent object with global parameters
	 * @param isCompiling
	 *            # of forks change if this is a compilation
	 * @param flags
	 *            string buffer where flags are added
	 * @throws
	 */
	private void addForkLimitFlags(EvaluationParameters parameters,
			boolean isCompiling, StringBuilder flags)
			throws MooshakTypeException {

		flags.append(" --nproc ");
		if (fork == null) {
			Languages languages = parameters.getLanguages();
			if (isCompiling)
				flags.append(languages.getMaxCompFork());
			else
				flags.append(languages.getMaxExecFork());
		} else {
			flags.append(fork);
		}
	}
	
	@Override
	protected boolean updated() throws MooshakContentException {
		((Languages) getParent()).clearCache();
		return super.updated();
	}

}