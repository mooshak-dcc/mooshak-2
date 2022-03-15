package pt.up.fc.dcc.mooshak.evaluation;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


class ExecutionResourceUsage {
	
	static enum UsageVars {
		elapsed, cpu, memory
	};
	
	private final static Logger LOGGER = Logger.getLogger("");

	private Path usageFile = null;
	private String errors = null;
	private Integer signal = null;

	private Integer errorCode = 0;
	private String errorInfo = "";

	
	private Map<UsageVars, Double> usage = null;
	
	private static final Pattern signalPattern = Pattern
			.compile("^Command terminated by signal \\((\\d+): (\\w*)\\)");

	private static final Pattern exitPattern = Pattern
			.compile("^Command exited with non-zero status \\((\\d+)\\)");

	private static final Pattern BLANC_PATTERN = Pattern.compile("\\s+");

	static Set<PosixFilePermission> usagePermissions = new HashSet<>();
	static {
		usagePermissions.add(PosixFilePermission.OWNER_READ);
		usagePermissions.add(PosixFilePermission.OWNER_WRITE);
		
		usagePermissions.add(PosixFilePermission.GROUP_READ);
		usagePermissions.add(PosixFilePermission.GROUP_WRITE);
		
		usagePermissions.add(PosixFilePermission.OTHERS_READ);
		usagePermissions.add(PosixFilePermission.OTHERS_WRITE);	
		
	}
	
	private static Path TMP = getDefaultTempFile();
	
	private static Path getDefaultTempFile() {
		switch(System.getProperty("os.name","")) {
		case "Windows":
			return Paths.get("C:\\Windows\\Temp");
		case "Linux":
			return Paths.get("/tmp");
		default:
			return Paths.get("");
			
		}
	}
	
	public String getUsageFile() throws MooshakSafeExecutionException {
		if(usageFile == null)
			try {
				usageFile = Files.createTempFile(TMP,"usage", ".txt");
				
				Files.setPosixFilePermissions(usageFile,usagePermissions);
			} catch (IOException cause) {
				String message = "I/O error creating temporary usage file";
				throw new MooshakSafeExecutionException(message,cause);
			}
		return usageFile.toString();
	}
	
	/**
	 * Returns usage value for known usage variables
	 * @param var UsageVar identifier
	 * @return usage value as Double if defined; null otherwise   
	 * @throws MooshakSafeExecutionException on error parsing usage data file
	 */
	Double getUsage(UsageVars var) throws MooshakSafeExecutionException {
		
		return usage.get(var);
	}
	
	/**
	 * Return process error code (exit value) . No error correspond to 0.
	 * @return exit code, if defined; 0 otherwise
	 * @throws MooshakSafeExecutionException on error parsing usage data file
	 */
	public int getErrorCodeValue() throws MooshakSafeExecutionException {
		if(errorCode == null)
			return 0;
		else
			return errorCode;
	}
	
	/**
	 * Return process error code (exit value)  
	 * @return exit code, if defined; null otherwise
	 * @throws MooshakSafeExecutionException on error parsing usage data file
	 */
	public Integer getErrorCode() throws MooshakSafeExecutionException {
		
		return errorCode;
	}
	
	/**
	 * Return process error info (exit message)  
	 * @return exit info, if defined; null otherwise
	 * @throws MooshakSafeExecutionException on error parsing usage data file
	 */
	public String getErrorInfo() throws MooshakSafeExecutionException {
		
		return errorInfo;
	}
	
	/**
	 * Return signal received by signal or null if no signal was received  
	 * @return signal as Integer and null if no signal was received
	 * @throws MooshakSafeExecutionException
	 */
	public Integer getSignal() throws MooshakSafeExecutionException {
		
		return signal;
	}
	
	/** 
	 * Process usage file after process termination
	 */
	public void process() {
	
		errors = null;
		try {
			parse();
		} catch (MooshakSafeExecutionException cause) {
			LOGGER.log(Level.WARNING,"Parsing usage file "+
			"(usage files may not have been created if the SO killed safeexec)"
					,cause);
		}
			
		
		try {
			cleanup();
		} catch (MooshakSafeExecutionException cause) {
			errors = cause.getMessage();
			LOGGER.log(Level.WARNING,"Usage file cleanup",cause);
		}
	}
	
	/**
	 * Errors occurs while processing 
	 * @return
	 */
	public String getProcessingErrors() {
		
		return errors;
	}
	
		
	private void parse() throws MooshakSafeExecutionException {	
		if(usageFile == null)
			throw new MooshakSafeExecutionException("No usage file created");
		
		usage = new HashMap<>();
		try(BufferedReader reader=Files.newBufferedReader(usageFile,
				Charset.defaultCharset())) {

			String line, args[];
			Matcher matcher;

			// parse first line
			line = reader.readLine();
			if(line == null)
				throw new MooshakSafeExecutionException(
						"No content in usage file");
			if ((matcher = signalPattern.matcher(line)).matches()) {
				signal = new Integer(matcher.group(1));
				errorCode = -signal; //signals converted to negative errorCodes
				errorInfo = matcher.group(2);
			} else if ((matcher = exitPattern.matcher(line)).matches()) {
				errorCode = new Integer(matcher.group(1));
			} else {
				errorInfo += line;
			}
			// parse other lines
			while ((line = reader.readLine()) != null) {
				args = BLANC_PATTERN.split(line);
				UsageVars var = UsageVars.valueOf(args[0]);
				usage.put(var, new Double(args[2]));
			}

		} catch (IOException cause) {
			String message = "Reading resource usage file";
			throw new MooshakSafeExecutionException(message, cause);
		}
		
	}
	
	
	private void cleanup() throws MooshakSafeExecutionException {
		if (Files.exists(usageFile)) {
			try {
				Files.delete(usageFile);
			} catch (IOException cause) {
				String message = "Error deleting usage file:"
						+ cause.getMessage();
				throw new MooshakSafeExecutionException(message, cause);
			}
		}
	}

	
}
