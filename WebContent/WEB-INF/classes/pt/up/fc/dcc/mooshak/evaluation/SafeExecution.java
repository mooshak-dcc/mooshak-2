package pt.up.fc.dcc.mooshak.evaluation;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.shared.MooshakException;

/** 
 * Class for wrapping safe executions (including compilations)
 * An instance is a single execution
 */
public class SafeExecution {

	private final static Runtime RUNTIME = Runtime.getRuntime();
	private final static Logger LOGGER = Logger.getLogger("");

	private OutputBuffer output = null;
	private OutputBuffer errors = null;
	private int exitCode = 0;
	
	private EvaluationParameters parameters = null;
	private ExecutionResourceUsage usage = null;
	private String commandLine = null;
	private String[] environment = null;
	private String limits;

	/**
	 * Safexec absolute pathname as string 
	 */
	private static String safeexec = null;

	/**
	 * Set path to WEB-INF directory
	 */
	public static void setWebInf(Path webInfPath) {
		String architecture = System.getProperty("os.arch","");
		String extension = 
				System.getProperty("os.name","").startsWith("Windows") ?
						".exe":	"";
		String name = "safeexec_"+architecture+extension;
		Path path = webInfPath.resolve("bin").resolve(name).toAbsolutePath();

		safeexec = path.toString();
		
		if(!Files.exists(path))
			LOGGER.severe("SafeExec path doesn't exist:"+path);
		/* else if(!Files.isExecutable(path)) // setuid conflicts with execution
			LOGGER.severe("SafeExec isn't executable:"+path); */
		else if(! isOwnedByRoot(path))
			LOGGER.severe("SafeExec isn't owned by root:"+path);
		else if(safeexecLacksPermissions())
			LOGGER.severe("SafeExec lacks permissions:"+path);
		else
			LOGGER.info("SafeExec OK");
	}
	
	private static boolean isOwnedByRoot(Path path) {
		try {
			return "root".equals(Files.getOwner(path).getName());
		} catch (IOException cause) {
			LOGGER.log(Level.SEVERE,"Coudn't check owner of "+path,cause);
			return false;
		}
	}
	
	private static boolean safeexecLacksPermissions() {
		try {
			return ! isSafexecOk();
		} catch (MooshakException cause) {
			LOGGER.log(Level.SEVERE,"Coudn't check SafeExec permissions",cause);
			return true;
		}
	}
	
	/**
	 * Checks is safeexec has the right permissions
	 * @return {@code true} if its OK, {@code false} otherwise
	 * @throws MooshakException if could not be checked
	 */
	public static boolean isSafexecOk() throws MooshakException {
		String permissions = getSafeexecPermissions();
		
		return  permissions.startsWith("-rwsr-xr-x") 
				|| permissions.startsWith("-rwSr-xr-x");

	}
	
	/**
	 * Returns string with safeexec permissions (Linux only)
	 * @return String with permissions 
	 * @throws MooshakException if could not be checked
	 */
	public static String getSafeexecPermissions() throws MooshakException {
		StringBuilder message = new StringBuilder();
		String osName = System.getProperty("os.name","");
		
		if(osName.startsWith("Windows"))
			throw new MooshakException("operation not available on Windows");
		else {
			String command =  "/bin/ls -l "+safeexec;
			
			try {
				Process process = RUNTIME.exec(command);
				process.waitFor();
				readFrom(process.getErrorStream(),message);
				if(message.length() > 0)
					throw new MooshakException(message.toString());
				readFrom(process.getInputStream(),message);
				
			}  catch (IOException | InterruptedException cause) {
				throw new MooshakException("Executing ls safeexec",cause);
			}
		}
		return message.toString().split("\\s")[0];
	}
	
	/**
	 * Append chars from input stream to String builder
	 * @param stream
	 * @param message
	 * @throws IOException
	 */
	private static void readFrom(InputStream stream,StringBuilder message) 
			throws IOException {
		
		try(
				Reader reader = new InputStreamReader(stream);
				BufferedReader fromProcess = new BufferedReader(reader);
				) {
			String line;
			while((line = fromProcess.readLine())!= null) {
				message.append(line);
				message.append('\n');
			}
		}
		stream.close();
	}
	
	public static String getSafeexec() {
		return safeexec;
	}
	
	
	public void setParameters(EvaluationParameters parameters) {
		this.parameters = parameters;
	}
	
	public void setLimits(String limits) {
		this.limits = limits;
	}
	
	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	/**
	 * Proceed with this safe execution, writing the contents of the {@link InputStream} 
	 * from the parameters to the {@link OutputStream} of the process and attaching an 
	 * {@link OutputBuffer} to the {@link InputStream} and another to the error
	 * {@link InputStream} of the process.
	 * 
	 * @throws MooshakSafeExecutionException
	 */
	public void execute() throws MooshakSafeExecutionException {
					
		if(commandLine == null || "".equals(commandLine))
			return;

		Process process = getProcess();
		
		OutputStream outputStream = process.getOutputStream();
		writeStream(parameters.getInput(),outputStream);
		
		errors = new OutputBuffer(parameters,process.getErrorStream());
		output = new OutputBuffer(parameters,process.getInputStream());
		
		try {
			
			exitCode = process.waitFor();
			
		} catch (InterruptedException cause) {
			throw new MooshakSafeExecutionException("Waiting for execution",cause);
		} finally {
			if(usage != null)
				usage.process();	
			
			process.destroyForcibly();
			
			output.terminate();
			errors.terminate();
		}		
	}
	
	/**
	 * Start a process with this safe execution without attaching I/O streams to the
	 * process, only an {@link OutputBuffer} to the {@link InputStream} and another 
	 * to the error {@link InputStream} of the process.
	 * 
	 * WARNING: do not use this method in conjunction with <code>execute()</code>
	 * 
	 * @return {@link Process} started process from this safe execution
	 * @throws MooshakSafeExecutionException - if an error occurs while starting the process
	 */
	public Process startProcessWithoutIO() throws MooshakSafeExecutionException {
		
		if(commandLine == null || "".equals(commandLine))
			throw new MooshakSafeExecutionException("There is no command line to "
					+ "start process.");
			
		Process process = getProcess();
		
		errors = new OutputBuffer(parameters, process.getErrorStream());
		
		return process;
	}
	
	/**
	 * Stop a process that was started with this safe execution without attaching 
	 * I/O streams to the process.
	 * 
	 * WARNING: do not use this method in conjunction with <code>execute()</code>
	 * 
	 * @return {@link Process} started process from this safe execution
	 * @throws MooshakSafeExecutionException - if an error occurs while stopping the process
	 */
	public void stopProcessWithoutIO(Process process) throws MooshakSafeExecutionException {
		
		try {
			exitCode = process
					.destroyForcibly()
					.waitFor();
			errors.terminate();
		} catch (InterruptedException cause) {
			throw new MooshakSafeExecutionException("Waiting for execution", cause);
		}
	}
	
	/**
	 * Get a process for this safe execution
	 * 
	 * @return {@link Process} process for this safe execution
	 * @throws MooshakSafeExecutionException
	 */
	private Process getProcess() throws MooshakSafeExecutionException {
		Process process = null;
		String command = makeSafeCommandLine();
		LOGGER.severe("Running safeexec command ... " + command);
		Path workingDirectory = parameters.getDirectoryPath();		
		// Path input = parameters.getInput();
		
		try {	
			process = RUNTIME.exec(command,environment,workingDirectory.toFile());
		} catch (IOException e) {
			throw new MooshakSafeExecutionException("Executing safely",e);
		}
		LOGGER.info("safe execution:\n\tcd"+workingDirectory+"\n\t");
		//		+command+(input == null ? "" : " < "+input));
		
		return process;
	}
	
	/**
	 * Get a process builder for this safe execution
	 * 
	 * @return {@link ProcessBuilder} the process builder
	 * @throws MooshakSafeExecutionException
	 */
	public ProcessBuilder getProcessBuilder() throws MooshakSafeExecutionException {
		ProcessBuilder processBuilder = null;
		String command = makeSafeCommandLine();
		LOGGER.severe("Running safeexec command ... " + command);
		Path workingDirectory = parameters.getDirectoryPath();
		
		List<String> processArgs = new ArrayList<String>(Arrays.asList(command.split("\\s+")));
		
		processBuilder = new ProcessBuilder(processArgs);
		processBuilder.directory(workingDirectory.toFile());
		
		if (environment != null) {
			Map<String, String> env = processBuilder.environment();
			env.clear();
			
			for (String envstring : environment) {
				int eqlsign = envstring.indexOf('=');
	                
                if (eqlsign != -1)
                    env.put(envstring.substring(0, eqlsign), 
                    		envstring.substring(eqlsign + 1));
			}
		}
		
		return processBuilder;
	}
	
	
	/**
	 * Return errors resulting from safe execution. For instance, errors 
	 * occurring in compilation due to insufficient resources granted to the 
	 * compiler, such as memory.
	 * 
	 * @return
	 * @throws MooshakSafeExecutionException
	 */
	public String getSafeExecutionErrors() 
			throws MooshakSafeExecutionException {
		if(usage != null)
			return usage.getErrorInfo().trim();
		else
			return null;
	}
	
	
	/**
	 * Make a command line for safeexec 
	 * @return command line
	 * @throws MooshakSafeExecutionException on usage file IO errors
	 */
	private String makeSafeCommandLine() throws MooshakSafeExecutionException {
		
		StringBuilder safeexecCommandLine = new StringBuilder();
		
		safeexecCommandLine.append(safeexec);
		safeexecCommandLine.append(limits);
		if(parameters.isMonitorResources()) {
			usage = new ExecutionResourceUsage();
			safeexecCommandLine.append(" --usage ");
			safeexecCommandLine.append(usage.getUsageFile());
		} else
			safeexecCommandLine.append(" --silent ");
		safeexecCommandLine.append(" --exec ");
		safeexecCommandLine.append(commandLine);
		
		String args = parameters.getArgs();
		if(args != null && ! "".equals(args)) {
			safeexecCommandLine.append(" ");
			safeexecCommandLine.append(args);
		}
		
		return safeexecCommandLine.toString();
	}
		
	private static int SIZE = 2 << 10;
	private byte[] buffer = new byte[SIZE];
	
	/**
	 * Copy data from input file to the standard input of a process
	 * @param input file
	 * @param outputStream connected to the process's standard input
	 * @throws MooshakSafeExecutionException of file an IO errors
	 */
	void writeStream(Path input, OutputStream outputStream) 
			throws MooshakSafeExecutionException {
		
		if(input != null) {
				// File copy will block if a problem occurs Avoid it!
				//  Files.copy(input,outputStream);
			
			try(
				InputStream inputStream = Files.newInputStream(input);
				BufferedOutputStream output = 
						new BufferedOutputStream(outputStream)) {
				
				int len = 0;
				while((len = inputStream.read(buffer, 0, SIZE)) > -1) 
						output.write(buffer, 0, len);
			} catch (IOException cause) {
				/*
				 * The exception was most probably raised due to a process
				 * crashed while reading the input
				 * Avoid rethrowing again an exception and let the analyzer do
				 * its thing
				 */
				//throw new MooshakSafeExecutionException("Write stream error!!", cause);
			} 
		}
	}
	
	static int nBuffer = 0;
	
	

	/**
	 * Get standard output of safe execution as a string
	 * @return standard output
	 */
	public String getOutput() {
		if(output == null)
			return "";
		else
			return output.toString();
	}
	
	/**
	 * Get standard output of safe execution as a string
	 * @return standard error
	 */
	public String getErrors() {
		if(errors == null)
			return "";
		else
			return errors.toString();
	}
	
	/**
	 * Get resources used during safe execution
	 * @return 
	 */
	public ExecutionResourceUsage getUsage() {
		return usage;
	}

	
	/**
	 * Return exit code of sandbox (safeexec).
	 * @return
	 */
	public int getExitCode() {
		return exitCode;
	}

	/**
	 * @return the environment
	 */
	public String[] getEnvironment() {
		return environment;
	}

	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(String[] environment) {
		this.environment = environment;
	}

}

