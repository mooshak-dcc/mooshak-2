package pt.up.fc.dcc.mooshak.content.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;

public class Languages extends PersistentContainer<Language> {
	private static final long serialVersionUID = 1L;

	private static final int DEFAULT_MINIMUM_UID = 30000;
	private static final int DEFAULT_MAXIMUM_UID = 60000;
	private static final int DEFAULT_MAX_COMP_FORK = 10;
	private static final int DEFAULT_MAX_EXEC_FORK = 0;
	private static final int DEFAULT_MAX_CORE = 0;
	private static final int DEFAULT_MAX_DATA = 33554432;
	private static final int DEFAULT_MAX_OUTPUT = 512000;
	private static final int DEFAULT_MAX_STACK = 8388608;
	private static final int DEFAULT_MAX_PROG = 102400;
	private static final int DEFAULT_REAL_TIMEOUT = 60;
	private static final int DEFAULT_COMP_TIMEOUT = 60;
	private static final int DEFAULT_EXEC_TIMEOUT = 5;
	
	private static final Logger LOGGER = Logger.getLogger("");

	@MooshakAttribute( 
			name="Fatal", 
			type=AttributeType.LABEL)
	private String fatal = null;
	
	@MooshakAttribute( 
			name="Warning",
			type=AttributeType.LABEL)
	private String warning = null;
	
	@MooshakAttribute( 
			name="MaxCompFork",
			type=AttributeType.INTEGER,
			tip="Maximum Integer of forks in compilation")
	private Integer maxCompFork = 10;
	
	@MooshakAttribute( 
			name="MaxExecFork",
			type=AttributeType.INTEGER,
			tip="Maximum Integer of forks in execution")
	private Integer maxExecFork = 0;
	
	@MooshakAttribute( 
			name="MaxCore",
			type=AttributeType.INTEGER,
			tip="Maximum size of core files in bytes")
	private Integer maxCore = 0;
	
	@MooshakAttribute( 
			name="MaxData",
			type=AttributeType.INTEGER,
			tip="Maximum size of data segment in bytes")
	private Integer maxData = 33554432;

	@MooshakAttribute( 
			name="MaxOutput",
			type=AttributeType.INTEGER,
			tip="Maximum size of output files in bytes")
	private Integer  maxOutput= 512000;
	
	@MooshakAttribute( 
			name="MaxStack",
			type=AttributeType.INTEGER,
			tip="Maximum size of stack files in bytes")
	private Integer maxStack = 8388608;
	
	@MooshakAttribute( 
			name="MaxProg",
			type=AttributeType.INTEGER,
			tip="Maximum size of program in bytes")
	private Integer  maxProg= 102400;
	
	@MooshakAttribute( 
			name="RealTimeout",
			type=AttributeType.INTEGER,
			tip="Clock (real time) timeout in seconds")
	private Integer realTimeout = 60;
	
	@MooshakAttribute( 
			name="CompTimeout",
			type=AttributeType.INTEGER,
			tip="Compilation timeout in seconds")
	private Integer compTimeout = 60;
	
	@MooshakAttribute( 
			name="ExecTimeout",
			type=AttributeType.INTEGER,
			tip="Execution (cpu) timeout in seconds")
	private Integer execTimeout = 5;
	
	@MooshakAttribute( 
			name="MinUID",
			type=AttributeType.INTEGER,
			tip="Lower bound for generated UIDs acting as user nobody")
	private Integer minUID = 30000;
	
	@MooshakAttribute( 
			name="MaxUID",
			type=AttributeType.INTEGER,
			tip="Upper bound for generated UIDs acting as user nobody")
	private Integer maxUID = 60000;
	
	@MooshakAttribute(
			name="Language",
			type=AttributeType.CONTENT)
	public Void language;
	
	// backwards compatibility: deprecated attribute from Mooshak 1.* 
	@MooshakAttribute(
			name="UseSafeExec",
			type=AttributeType.HIDDEN)
	public Void useSafeExec = null;
	
	
	
	@MooshakOperation(name="Defaults",
			sendEvents=true)
	private CommandOutcome setDefaults() {
		CommandOutcome outcome = new CommandOutcome();
		
		setCompTimeout(DEFAULT_COMP_TIMEOUT);
		setExecTimeout(DEFAULT_EXEC_TIMEOUT);
		setMaxCompFork(DEFAULT_MAX_COMP_FORK);
		setMaxCore(DEFAULT_MAX_CORE);
		setMaxData(DEFAULT_MAX_DATA);
		setMaxExecFork(DEFAULT_MAX_EXEC_FORK);
		setMaxOutput(DEFAULT_MAX_OUTPUT);
		setMaxProg(DEFAULT_MAX_PROG);
		setMaxStack(DEFAULT_MAX_STACK);
		setRealTimeout(DEFAULT_REAL_TIMEOUT);
		setMinUID(DEFAULT_MINIMUM_UID);
		setMaxUID(DEFAULT_MAXIMUM_UID);
		
		try {
			save();
		} catch (MooshakContentException e) {
			outcome.setMessage("Defaults NOT Set");
			return outcome;
		}
		
		outcome.setMessage("Defaults Set");
		
		return outcome;
	}
	
	@MooshakOperation(name="Default Languages",
			inputable=true,
			sendEvents=true,
			tip="Sets the default languages")
	private CommandOutcome setDefaultLanguages(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			for (Language lang : getContent())
				lang.delete();
		} catch (MooshakContentException e) {
			outcome.setMessage("Error: Cannot delete languages");
			return outcome;
		}
		
		String os = System.getProperty("os.name");
		try {
			if(os.toLowerCase().indexOf("linux") != -1) {
				final String usrBinPath = "/usr" + File.separator + "bin"
						+ File.separator;
				
				if(new File(usrBinPath + "java").exists()) {
					Language java = create("Java", Language.class);
					java.setName("Java");
					java.setExtension("java");
					
					try {
					    java.setVersion(extractVersion("java -version"));
					} catch (Exception e) {
						java.setVersion("gcj-java-3.2.2");
					}
					
					java.setCompile(usrBinPath + "javac $file");
					java.setCompiler("jdk");
					java.setExecute(usrBinPath + "java -classpath . $name");
					java.setData(new Long("6342967296"));
					java.setFork(new Long("100"));
					java.save();
				}
				
				if(new File(usrBinPath + "gcc").exists()) {
					Language c = create("C", Language.class);
					c.setName("C");
					c.setExtension("c");
					
					try {
					    c.setVersion(extractVersion("gcc --version"));
					} catch (Exception e) {
						c.setVersion("2.96");
						LOGGER.log(Level.SEVERE, e.getMessage());
					}
					
					c.setCompile(usrBinPath + "gcc -Wall -lm $file");
					c.setCompiler("gcc");
					c.setExecute("a.out");
					c.save();
				}
				
				if(new File(usrBinPath + "g++").exists()) {
					Language cpp = create("CPP", Language.class);
					cpp.setName("C++");
					cpp.setExtension("cpp");
					
					try {
					    cpp.setVersion(extractVersion("cc --version"));
					} catch (Exception e) {
						cpp.setVersion("2.96");
						LOGGER.log(Level.SEVERE, e.getMessage());
					}
					
					cpp.setCompile(usrBinPath + "g++ -Wall $file");
					cpp.setCompiler("g++");
					cpp.setExecute("a.out");
					cpp.save();
				}
				
				if(new File(usrBinPath + "fpc").exists()) {
					Language fpc = create("Pascal", Language.class);
					fpc.setName("Pascal");
					fpc.setExtension("pas");
					
					try {
					    fpc.setVersion(extractVersion("fpc --version"));
					} catch (Exception e) {
						fpc.setVersion("1.0.4");
					}
					
					fpc.setCompile(usrBinPath + "fpc -v0w -oprog $file");
					fpc.setCompiler("Free Pascal");
					fpc.setExecute("prog");
					fpc.save();
				}
			}
			else if(os.toLowerCase().indexOf("windows") != -1) {
				File javaDir = new File(System.getProperty("java.home"))
					.getParentFile();
				File javaBinDir = new File(javaDir.getAbsolutePath() 
						+ File.separator + "bin" + File.separator);
				
				if(new File(javaBinDir.getAbsolutePath()
						+ File.separator + "java").exists()) {
					Language java = create("Java", Language.class);
					java.setName("Java");
					java.setExtension("java");
					java.setVersion(System.getProperty("java.vm.version"));
					java.setCompile(javaBinDir.getAbsolutePath()
							 + File.separator + "javac $file");
					java.setCompiler("jdk");
					java.setExecute(javaBinDir.getAbsolutePath()
							 + File.separator + "java -classpath . $name");
					java.setData(new Long("6342967296"));
					java.setFork(new Long("100"));
					java.save();
				}
			}
		} catch (MooshakContentException e) {
			outcome.setMessage("Default Languages NOT Set");
			return outcome;
		}

		outcome.setMessage("Default Languages Set");
		
		return outcome;
	}

	/**
	 * @param pattern
	 * @return
	 * @throws Exception
	 */
	private String extractVersion(String cmd) 
			throws Exception {
		String result = "", line;
		
		Process process = Runtime.getRuntime().exec(cmd);
		BufferedReader in = new BufferedReader(
		           new InputStreamReader(process.getInputStream()) );
		process.waitFor();
		if ((line = in.readLine()) != null)
			result += line;

		in.close();
		
		return result;
	}	
	
	private Map<String,Language> languagesWithExtension = null;
	
	private Map<String,Language> getLanguagesWithExtension() {
				
		if(languagesWithExtension == null) {
		
			languagesWithExtension = new HashMap<>();
			try(POStream<Language> stream = newPOStream()) {
				for(Language language: stream)
					languagesWithExtension.put(language.getExtension(),language);
			} catch(Exception cause) {
				LOGGER.log(Level.SEVERE,"Error iterating over languages",cause);
			}
		}
		
		return languagesWithExtension;
	}
	
	
	public void clearCache() {
		languagesWithExtension = null;
	}
	
	/**
	 * Find a language in this container with given extension
	 * @param extension
	 * @return language with given extension, or 
	 * 			{@code null} if not found
	 */
	public Language findLanguageWithExtension(String extension) {
		
		return getLanguagesWithExtension().get(extension);
	}


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
	 * Get the maximum number of forks during compilation  
	 * @return the maxim CompFork
	 */
	public int getMaxCompFork() {
		if(maxCompFork == null)
			return Languages.DEFAULT_MAX_COMP_FORK;
		else
			return maxCompFork;
	}

	/**
	 * Set the maximum number of forks during compilation  
	 * @param maxCompFork the maxCompFork to set
	 */
	public void setMaxCompFork(int maxCompFork) {
		this.maxCompFork = maxCompFork;
	}

	/**
	 * Set the maximum number of forks during execution 
	 * @return the maxExecFork
	 */
	public int getMaxExecFork() {
		if(maxExecFork == null)
			return Languages.DEFAULT_MAX_EXEC_FORK;
		else			
			return maxExecFork;
	}

	/**
	 * Get the maximum number of forks during execution 
	 * @param maxExecFork the maxExecFork to set
	 */
	public void setMaxExecFork(int maxExecFork) {
		this.maxExecFork = maxExecFork;
	}

	/**
	 * Get the maximum size of core generated by a process
	 * @return the maxCore
	 */
	public int getMaxCore() {
		if(maxCore == null)
			return Languages.DEFAULT_MAX_CORE;
		else
			return maxCore;
	}

	/**
	 * Set the maximum size of core generated by a process
	 * @param maxCore the maxCore to set
	 */
	public void setMaxCore(int maxCore) {
		this.maxCore = maxCore;
	}

	/**
	 * Set the maximum size of data available to a process
	 * @return the maxData
	 */
	public int getMaxData() {
		if(maxData == null)
			return Languages.DEFAULT_MAX_DATA;
		else
			return maxData;
	}

	/**
	 * Set the maximum size of data available to a process
	 * @param maxData the maxData to set
	 */
	public void setMaxData(int maxData) {
		this.maxData = maxData;
	}

	/**
	 * Get the maximum size of the output generated by a process
	 * @return the maxOutput
	 */
	public int getMaxOutput() {
		if(maxOutput == null)
			return Languages.DEFAULT_MAX_OUTPUT;
		else
			return maxOutput;
	}

	/**
	 * Set the maximum size of the output generated by a process
	 * @param maxOutput the maxOutput to set
	 */
	public void setMaxOutput(int maxOutput) {
		this.maxOutput = maxOutput;
	}

	/**
	 * Get the maximum size of the stack available to a process
	 * @return the maxStack
	 */
	public int getMaxStack() {
		if(maxStack == null)
			return Languages.DEFAULT_MAX_STACK;
		else
			return maxStack;
	}

	/**
	 * Set the maximum size of the stack available to a process
	 * @param maxStack the maxStack to set
	 */
	public void setMaxStack(int maxStack) {
		this.maxStack = maxStack;
	}

	/**
	 * Get the maximum size of a program code
	 * @return the maxProg
	 */
	public int getMaxProg() {
		if(maxProg == null)
			return Languages.DEFAULT_MAX_PROG;
		else
			return maxProg;
	}

	/**
	 * Set the maximum size of a program code
	 * @param maxProg the maxProg to set
	 */
	public void setMaxProg(int maxProg) {
		this.maxProg = maxProg;
	}

	/**
	 * Get the maximum real time a process can execute
	 * @return the realTimeout
	 */
	public int getRealTimeout() {
		if(realTimeout == null)
			return Languages.DEFAULT_REAL_TIMEOUT;
		else
			return realTimeout;
	}

	/**
	 * Get the maximum real time a process can execute
	 * @param realTimeout the realTimeout to set
	 */
	public void setRealTimeout(int realTimeout) {
		this.realTimeout = realTimeout;
	}

	/**
	 * Get the maximum real time a process use to compile
	 * @return the compTimeout
	 */
	public int getCompTimeout() {
		if(compTimeout == null)
			return Languages.DEFAULT_COMP_TIMEOUT;
		else
			return compTimeout;
	}

	/**
	 * Set the maximum real time a process use to compile
	 * @param compTimeout the compTimeout to set
	 */
	public void setCompTimeout(int compTimeout) {
		this.compTimeout = compTimeout;
	}

	/**
	 * Get the maximum processor time a process use to compile
	 * @return the execTimeout
	 */
	public int getExecTimeout() {
		if(execTimeout == null)
			return Languages.DEFAULT_EXEC_TIMEOUT;
		else
			return execTimeout;
	}

	/**
	 * Set the maximum processor time a process use to compile
	 * @param execTimeout the execTimeout to set
	 */
	public void setExecTimeout(int execTimeout) {
		this.execTimeout = execTimeout;
	}

	/**
	 * Get the lowest user ID a process can use for safe execution
	 * @return the minUID
	 */
	public int getMinUID() {
		if(minUID == null)
			return Languages.DEFAULT_MINIMUM_UID;
		else
			return minUID;
	}

	/**
	 * Set the lowest user ID a process can use for safe execution
	 * @param minUID the minUID to set
	 */
	public void setMinUID(int minUID) {
		this.minUID = minUID;
	}

	/**
	 * Get the highest user ID a process can use for safe execution
	 * @return the maxUID
	 */
	public int getMaxUID() {
		if(maxUID == null)
				return Languages.DEFAULT_MAXIMUM_UID;
		else
			return maxUID;
	}

	/**
	 * @param maxUID the maxUID to set
	 */
	public void setMaxUID(int maxUID) {
		this.maxUID = maxUID;
	}
	
}
