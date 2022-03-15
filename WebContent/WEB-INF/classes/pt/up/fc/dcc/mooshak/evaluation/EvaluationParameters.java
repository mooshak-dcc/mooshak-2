package pt.up.fc.dcc.mooshak.evaluation;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Group;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.MooshakTypeException;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.content.types.UserTestData;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * Parameters collected and used throughout the evaluation.
 * Some parameters are computed from related parameters (e.g. groups are 
 * computed as parents of teams). Instances of this class are used as
 * parameters to the public methods of class Languages 
 * and evaluation methods of class Submission. 
 *  
 * @author <code>zp@dcc.fc.up.pt</code>
 */
public class EvaluationParameters implements Cloneable {
	private static final Logger LOGGER = Logger.getLogger("");
	
	private Contest contest;
	private Path directory;
	private Path program;
	private Authenticable team;
	private Group group = null;
	private Problem problem;
	private Collection<Path> solutions = null;
	private Languages languages;
	private Language language;
	private Submission submission;
	private String testId;
	private int testOrder;

	private int maxOutput = 0;
	private int timeout;
	private int points = 0;
	private String args = null;
	private Path context = null;
	private Path input = null;
	private Path expected = null;
	private Path obtained = null;
	private String feedback = null;
	private boolean show = false;
	private boolean monitorResources = false;
	private UserTestData userTestData = null;
	private int userDataOrder;
	private boolean runAllTests = true;
	private TestSkipper testSkipper= new TestSkipper();
	
	class TestSkipper  {
		boolean skipTests = false;
	}
	
	
	public EvaluationParameters clone() {
		EvaluationParameters clone = null;
		try {
			clone = (EvaluationParameters) super.clone();
		} catch (CloneNotSupportedException cause) {
			LOGGER.log(Level.SEVERE,"Error cloning evaluation parameters",cause);
		}
		return clone;
	}
	
	
	/**
	 * Get general contest data related to this evaluation
	 * @return contest
	 */
	public Contest getContest() {
		return contest;
	}

	/**
	 * Set general contest data related to this evaluation
	 * @param contest
	 */
	public void setContest(Contest contest) {
		this.contest = contest;
	}

	/**
	 * Get directory where this evaluation takes places
	 * @return
	 */
	public Path getDirectory() {
		return directory;
	}

	/**
	 * Set directory where this evaluation takes places
	 * @param directory
	 */
	public void setDirectory(Path diretory) {
		this.directory = diretory;
	}

	/**
	 * Get an absolute path to the directory where this evaluation takes place.
	 * @return
	 */
	public Path getDirectoryPath() {
		if(directory == null)
			return null;
		else
			return PersistentObject.getAbsoluteFile(directory);
	}
	
	
	/**
	 * Get program being evaluated (relative path)
	 * @return
	 */
	public Path getProgram() {
		return program;
	}

	/**
	 * Set program being evaluated
	 * @param program
	 */
	public void setProgram(Path program) {
		this.program = program;
	}

	/**
	 * Get data on team whose submission is under evaluation
	 * @return
	 */
	public Authenticable getTeam() {
		return team;
	}

	/**
	 * Set data on team whose submission is under evaluation
	 * @param team
	 */
	public void setTeam(Authenticable team) {
		this.team = team;
	}

	/**
	 * Get data on group whose submission is under evaluation.
	 * If needed this data is computed as the parent folder of team
	 * @return
	 * @throws MooshakTypeException if group cannot be loaded
	 */
	public Group getGroup() throws MooshakTypeException {
		if(group == null) {
			try {
				if (team instanceof Team)
					group = ((Team) team).getParent();
				else
					return null;
			} catch (MooshakContentException cause) {
				String message = "Error loading group of team "+team;
				throw new MooshakTypeException(message,cause);
			}
		}
		return group;
	}

	/**
	 * Set data on group whose submission is under evaluation.
	 * @param group
	 */
	public void setGroup(Group group) {
		this.group = group;
	}
	
	
	/**
	 * Get data on problem being solved by this submission.
	 * @return
	 */
	public Problem getProblem() {
		return problem;
	}

	/**
	 * Set data on problem being solved by this submission.
	 * @param problem
	 */
	public void setProblem(Problem problem) {
		this.problem = problem;
	}
	
	/**
	 * Get absolute path of this submission problem solutions.
	 * If needed, this data is retrieved from problem 
	 * @return solution
	 * @throws MooshakException 
	 */
	public Collection<Path> getSolutions() throws MooshakException {
		if(solutions == null) {
			if(problem != null)
				solutions = problem.getSolutions();
		}
		
		return solutions;
	}
	
	/**
	 * Sets a collections of solutions used for evaluation.
	 * When not set using this method, solutions are retrieved from the problem,
	 * if a problem was previously set. This method is mostly used for testing.
	 * 
	 * @param solutions
	 */
	public void setSolutions(Collection<Path> solutions) {
		this.solutions = solutions;
	}
	
	
	/**
	 * Get data on language used to code this submission.
	 * @return language
	 */
	public Language getLanguage() {
		return language;
	}

	/**
	 * Set data on language used to code this submission.
	 * @param language
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}

	/**
	 * Get general configurations on languages for this submission.
	 * If needed, this data computed as the parent folder of languages
	 * @return languages
	 * @throws MooshakTypeException if languages cannot be loaded
	 */
	public Languages getLanguages() throws MooshakTypeException {
		if(languages == null)
			try {
				languages = language.getParent();
			} catch (MooshakContentException cause) {
				String message = "Error loading general configs of language "+
						language;
				throw new MooshakTypeException(message,cause);
			}
		return languages;
	}

	/**
	 * Set general configurations on languages for this submission.
	 * @param languages
	 */
	public void setLanguages(Languages languages) {
		this.languages = languages;
	}

	/**
	 * Get max output size of a process
	 * @return
	 */
	public int getMaxOutput() {
		return maxOutput;
	}
	
	/**
	 * Set max output size of a process
	 * @param maxOutput
	 */
	public void setMaxOutput(int maxOutput) {
		this.maxOutput = maxOutput;
	}

	
	/**
	 * Get timeout for the next execution of this submission 
	 * @return
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * Get timeout for the next execution of this submission
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	/**
	 * Get a absolute path to the program being evaluated.
	 * @return path
	 */
	public Path getProgramPath() {
		return PersistentObject.getAbsoluteFile(program);	
	}
	
	
	
	/**
	 * Get the command line arguments for the execution 
	 * of the program being evaluated.
	 * @return args (command line arguments)
	 */
	public String getArgs() {
		return args;
	}

	/**
	 * Set the arguments for the execution of the program being evaluated.
	 * @param args
	 */
	public void setArgs(String args) {
		this.args = args;
	}


	/**
	 * Get points if this run next text is accepted
	 * @return the points
	 */
	public int getPoints() {
		return points;
	}


	/**
	 * Set points if this run next text is accepted
	 * @param points the points to set
	 */
	public void setPoints(int points) {
		this.points = points;
	}


	/**
	 * Get the context for the execution of the program being evaluated.
	 * @return file used  as context (as absolute file)
	 */
	public Path getContext() {
		if(context == null)
			return null;
		else
			return PersistentCore.getAbsoluteFile(context);
	}

	/**
	 * Set the context for the execution of the program being evaluated.
	 * @param context file used  as context
	 */
	public void setContext(Path context) {
		this.context = context;
	}
	
	/**
	 * Get the input file for the process execution
	 * @return input file (as an absolute file)
	 */
	public Path getInput() {
		if(input == null)
			return null;
		else
			return PersistentCore.getAbsoluteFile(input);
	}

	/**
	 * Set the input file for the process execution
	 * @param input file
	 */
	public void setInput(Path input) {
		this.input = input;
	}

	/**
	 * Get the expected output file for the process execution
	 * @return output file (as an absolute file)
	 */
	public Path getExpected() {
		if(expected == null)
			return null;
		else
			return PersistentCore.getAbsoluteFile(expected);
	}
	
	/**
	 * Set the expected output file for the process execution
	 * @param obtained
	 */
	public void setExpected(Path expected) {
		this.expected = expected;
		
	}
	
	/**
	 * Get the obtained output file for the process execution
	 * @return obtained file (as an absolute file)
	 */
	public Path getObtained() {
		if(obtained == null)
			return null;
		else
			return PersistentCore.getAbsoluteFile(obtained);
	}
	
	/**
	 * Set the obtained output file for the process execution
	 * @param obtained
	 */
	public void setObtained(Path obtained) {
		this.obtained = obtained;
		
	}
	
	/**
	 * Get feedback message (hint) to display when test fails 
	 * @return the feedback
	 */
	public String getFeedback() {
		return feedback;
	}


	/**
	 * Set feedback message (hint) to display when test fails 
	 * @param feedback the feedback to set
	 */
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}


	/**
	 * Can this test case (input/output) be shown to team/student?
	 * @return the show
	 */
	public boolean isShow() {
		return show;
	}


	/**
	 * Set if this test case (input/output) be shown to team/student?
	 * @param show the show to set
	 */
	public void setShow(boolean show) {
		this.show = show;
	}


	/**
	 * Is this evaluation monitoring usage resources?
	 * @return true is resource usage is being monitored; false otherwise
	 */
	public boolean isMonitorResources() {
		return monitorResources;
	}

	/**
	 * Set the monitor resource flag. Default is false.
	 * @param monitorResources
	 */
	public void setMonitorResources(boolean monitorResources) {
		this.monitorResources = monitorResources;
	}
	
	/**
	 * Get submission being processed
	 * @return
	 */
	public Submission getSubmission() {
		return submission;
	}

	/**
	 * Set submission being processed
	 * @return
	 */
	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	public Map<String, String> collectVariables() {

		Map<String, String> vars = new HashMap<>();
		Problem problem = getProblem();
		Path context = getContext();
		Path programPath = getProgramPath();
		String programFilename = programPath.toString();

		vars.put("home", PersistentObject.getHome().toString());
		vars.put("args", getArgs());
		if(context != null)
			vars.put("context", getContext().toString());
		
		LOGGER.severe(PersistentObject.getAbsoluteFile(problem.getPath()).toString());
	
		if(problem != null) {
			vars.put("problem", PersistentObject.getAbsoluteFile(problem.getPath()).toString());
			vars.put("environment", problem.getEnvironmentPathname());
			vars.put("solution", problem.getSolutionPathname());
		
			// game variables
			vars.put("gameArtifact", problem.getGameArtifactId());
			vars.put("gamePackage", problem.getGamePackagePathname());
			vars.put("gameExtras", problem.getExtrasPathname());
		}
		
		/*vars.put("programFolder", programPath.getParent().toString());*/
		vars.put("file", programFilename);
		vars.put("program", programFilename);
		vars.put("name", /*packagePath != null ? 
				Paths.get(packagePath, Filenames.rootName(programFilename)).toString() : */
				Filenames.rootName(programFilename));
		vars.put("extension", Filenames.extension(programFilename));

		if(expected != null)
			vars.put("expected",getExpected().toString());
		if(obtained != null)
			vars.put("obtained", getObtained().toString());
		
		
		return vars;
	}


	/**
	 * Should all tests be executed, 
	 * even if they are consuming too much resources? (e.g. TLE, MLE)
	 * @return the runAllTests
	 */
	public boolean isRunAllTests() {
		return runAllTests;
	}


	/**
	 * Specify if all tests should be executed, 
	 * even if they are consuming too much resources (e.g. TLE, MLE)
	 * @param runAllTests the runAllTests to set
	 */
	public void setRunAllTests(boolean runAllTests) {
		this.runAllTests = runAllTests;
	}


	/**
	 * Is it currently skipping tests, due to a previous error 
	 * that consumes too much resources? (e.g. TLE, MLE)
	 * @return the skipTests
	 */
	public boolean isSkipTests() {
		return testSkipper.skipTests;
	}


	/**
	 * Specify if remaining tests must be skipped,  due to an error 
	 * that consumes too much resources? (e.g. TLE, MLE)
	 * @param skipTests the skipTests to set
	 */
	public void setSkipTests(boolean skipTests) {
		synchronized(testSkipper) {
			testSkipper.skipTests = skipTests;
		}
	}
	
	/**
	 * Directory storing user test data, or {@code null} if none defined 
	 * @return the userTestDate
	 */
	public UserTestData getUserTestData() {
		return userTestData;
	}


	/**
	 * Set directory storing user test data; {@code null} by default (undefined) 
	 * @param userTestData the userTestDate to set
	 */
	public void setUserTestData(UserTestData userTestData) {
		this.userTestData = userTestData;
	}
	
	/**
	 * Get order of test files in user data
	 * @return the userDataOrder
	 */
	public int getUserDataOrder() {
		return userDataOrder;
	}


	/**
	 * Set order of test files in user data
	 * @param userDataOrder the userDataOrder to set
	 */
	public void setUserDataOrder(int userDataOrder) {
		this.userDataOrder = userDataOrder;
	}
	
	
	/**
	 * @return the testId
	 */
	public String getTestId() {
		return testId;
	}

	/**
	 * @param testId the testId to set
	 */
	public void setTestId(String testId) {
		this.testId = testId;
	}

	/**
	 * Get this test order in data set
	 * @return the testOrder
	 */
	public int getTestOrder() {
		return testOrder;
	}


	/**
	 * Set this test order in data set
	 * @param testOrder the testOrder to set
	 */
	public void setTestOrder(int testOrder) {
		this.testOrder = testOrder;
	}
}