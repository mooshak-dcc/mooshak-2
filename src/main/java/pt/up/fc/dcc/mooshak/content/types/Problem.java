package pt.up.fc.dcc.mooshak.content.types;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PathManager;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.erl.ReportType;
import pt.up.fc.dcc.mooshak.content.erl.TestType;
import pt.up.fc.dcc.mooshak.content.util.FileUtils;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.content.util.Strings;
import pt.up.fc.dcc.mooshak.evaluation.game.GameManagerPool;
import pt.up.fc.dcc.mooshak.evaluation.game.wrappers.GameManagerWrapper;
import pt.up.fc.dcc.mooshak.managers.ParticipantManager;
import pt.up.fc.dcc.mooshak.server.commands.CommandService;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.EditorKind;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakValue;
import pt.up.fc.dcc.mooshak.shared.events.ProblemDescriptionChangedEvent;

public class Problem extends PersistentObject {
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(Problem.class.getSimpleName());
	
	private static final String EXTRAS_FOLDER = "extras";
	private static final String IMAGES_FOLDER = "images";
	private static final String SOLUTIONS_FOLDER = "solutions";
	private static final String SKELETONS_FOLDER = "skeletons";

	
	public enum ProblemID {
		A, B, C, D, E, F, G, H, I, J, K, L, M, 
		N, O, P, Q, R, S, T, U, V, W, X, Y, Z;
		
		public String toString() {
			return Strings.toTitle(super.toString());
		}
	}

	public enum ProblemDifficulty {
		VERY_EASY, EASY, MEDIUM, DIFFICULT, VERY_DIFFICULT;  
		
		public String toString() {
			return Strings.toTitle(super.toString().replace("_", " "));
		}
	}

	public enum ProblemType {
		SORTING, GRAPHS, GEOMETRY, COMBINATORIAL, STRINGS, MATHEMATICS, AD_HOC;
		
		public String toString() {
			return super.toString().replace("_", "-").toLowerCase();
		}
	}

	@MooshakAttribute( 
			name="Fatal", 
			type=AttributeType.LABEL)
	private String fatal = null;

	@MooshakAttribute( 
			name="Warning",
			type=AttributeType.LABEL)
	private String warning = null;

	@MooshakAttribute(
			name="Name",
			maxLength=3,
			type=AttributeType.TEXT,
			tip="Problem Name")
	private String name = null;

	@MooshakAttribute(
			name="Color",
			type=AttributeType.COLOR,
			tip="Ballon color")
	private Color color = null;


	@MooshakAttribute(
			name="Title",
			tip="Type of this problem"
			)
	private String title = null;

	@MooshakAttribute(
			name="Difficulty",
			type=AttributeType.MENU,
			tip="Difficulty of this problem")
	private ProblemDifficulty difficulty = null;

	@MooshakAttribute(
			name="Type",
			type=AttributeType.MENU,
			tip="Type of problem")
	private ProblemType type = null;

	@MooshakAttribute(
			name="Description",
			type=AttributeType.FILE,
			complement=".html",
			tip="Problem HTML description")
	private Path description = null;

	@MooshakAttribute(
			name="PDF",
			type=AttributeType.FILE,
			complement=".pdf",
			tip="Problem description in PDF format")
	private Path pdf = null;

	@MooshakAttribute(
			name="Program",
			type=AttributeType.FILE,
			tip="Source code of problem solution")
	private Path program = null;

	@MooshakAttribute(
			name=SOLUTIONS_FOLDER,
			type=AttributeType.DATA,
			tip="Source code of problem solutions")
	private Solutions solutions = null;

	@MooshakAttribute(
			name="Environment",
			type=AttributeType.FILE,
			tip="File with environment for compilation")
	private Path environment = null;

	@MooshakAttribute(
			name="Timeout",
			type=AttributeType.INTEGER,
			tip="Maximum time for executin this problem, in seconds")
	private Integer timeout = null;
	
	@MooshakAttribute(
			name = "quiz", 
			type = AttributeType.FILE,
			quizEditor = true)
	private Path quiz;
	
	

	private static final String EXIT_VALUES = 
		"  0 - Accepted\n"+
	    "  1 - Presentation Error\n"+		
	    "  2 - Wrong Answer\n"+
	    "  3 - Evaluation Skipped\n"+
	    "  4 - Output Limit Exceeded\n"+
	    "  5 - Memory Limit Exceeded\n"+
	    "  6 - Time Limit Exceeded\n"+
	    "  7 - Invalid Function\n"+
	    "  8 - Invalid Exit Value\n"+
	    "  9 - Runtime Error\n"+
	    " 10 - Compile Time Error\n"+
	    " 11 - Invalid Submission\n"+
	    " 12 - Program Size Exceeded\n"+
	    " 13 - Requires Reevaluation\n"+
		" 14 - Evaluating\n\n"+
				    
		"If the exit value is a negative value, then its simmetric\n"+
		"is taken as the submission mark, and the classification is\n"+
		" Accepted (e.g. If exit value is -100 then the mark is 100).";
	
	@MooshakAttribute(
			name = "Static_corrector",
			tip="Command line for static corrector",
			help =  
		"A static corrector is an external program that is invoked\n" +
		"before dynamic correction to classify/process the program's\n" +
		"source code. In this field you can write a command line\n" + 
		"to invoke a static corrector and you may use these variables:\n\n" +

	    "$home         - Mooshak's home director\n" +
	    "$program      - absolute pathname of file with submitted program\n"+
	    "$problem      - absolute pathname of problem\n"+
	    "$solution     - absolute pathname of problem solution file\n" +
	    "$environment  - absolute pathname of environment data file\n\n" +

	    "The values of these variables will also be available to the \n" +
	    "process as environment variables with the same names in capitals.\n"+  
	    "For instance, the command line variable $home will be available\n" +
	    "as as the environment variable HOME\n\n" +

	    "The special corrector must return the new classification\n" +
	    "as its exit code. The correspondence between exit values and\n" + 
	    "classifications is the following:\n\n" +EXIT_VALUES)
	private String staticCorrector;

	@MooshakAttribute(
			name = "Dynamic_corrector",
			tip="Command line for dynamic corrector",
			help=
		"A dynamic corrector is an external program that is invoked\n" +
		"after Mooshak's correction to classify each run. In this field\n" + 
		"you can write a command line to invoke a dynamic corrector\n" +
		"and you may use these variables:\n\n" +

	    "$home       - Mooshak's home director\n" +
	    "$program    - absolute pathname of file with submitted program\n" +
	    "$problem    - absolute pathname of problem\n"+
	    "$input      - absolute pathname of input data file\n"+
	    "$expected   - absolute pathname of file with expected output\n"+
	    "$obtained   - absolute pathname of file with obtained output\n"+
	    "$error      - absolute pathname of file with obtained error output\n"+
	    "$args       - command line arguments\n"+
	    "$context    - absolute path name of context file\n"+
	    "$classify_code  - (integer) current classification on Mooshak\n"+

	    "The values of these variables will also be available to the" +
	    "process as environment variables with the same names in capitals."+ 
	    "For instance, the command line variable $classify will be available"+
	    "as as the environment variable CLASSIFY_CODE\n\n"+

	    "The special corrector must return the new classification"+
	    "as its exit code, i.e. the new value for CLASSIFY_CODE. The"+ 
	    "correspondence between exit values and classifications is the"+ 
	    "following:\n\n"+EXIT_VALUES)
	private String dynamicCorrector = null;

	@MooshakAttribute(
			name="Game_manager",
			type=AttributeType.TEXT,
			tip="Qualified name of a game manager")
	private String gameManager = null;

	@MooshakAttribute(
			name="Game_package",
			type=AttributeType.FILE,
			tip="Game JAR package")
	private Path gamePackage = null;
	
	@MooshakAttribute(
			name="Original_location",
			type=AttributeType.LABEL,
			tip="URL from where problem was downloaded")
	private String originalLocation = null;
	
	@MooshakAttribute(
			name="Editor_kind",
			type=AttributeType.MENU,
			tip="Kind of editor to use with this problem")
	private EditorKind editorKind = null;


	@MooshakAttribute(
			name="Start",
			type=AttributeType.DATE,
			tip="Activate problem only at this moment")
	private Date start = null;

	@MooshakAttribute(
			name="Stop",
			type=AttributeType.DATE,
			tip="Deactivate after this moment")
	private Date stop = null;

	@MooshakAttribute(
			name=IMAGES_FOLDER,
			type=AttributeType.DATA)
	private Images images = null;

	@MooshakAttribute(
			name="tests",
			type=AttributeType.DATA)
	private Tests tests = null;

	@MooshakAttribute(
			name="skeletons",
			type=AttributeType.DATA)
	private Skeletons skeletons = null;


	/**
	 * Get environment as a pathname 
	 * @return pathname as string or the empty string if undefined or unreadable
	 */
	public String getEnvironmentPathname() {

		if(environment == null)
			return "";
		else {
			Path envPath = getAbsoluteFile(Filenames.getSafeFileName(environment));
			if(Files.exists(envPath) && Files.isReadable(envPath))
				return envPath.toString();
			else
				return "";
		}
	}

	/**
	 * Get solution program as a pathname
	 * @return pathname as string or the empty String if undefined
	 */
	public String getSolutionPathname() {
		if(program == null)
			return "";
		else {
			List<Path> solutions;
			try {
				solutions = new ArrayList<>(getSolutions());
			} catch (MooshakException e) {
				solutions = new ArrayList<>();
			}
			if(solutions.size() <= 0 )
				return "";

			return PersistentObject.getAbsoluteFile(solutions.get(0)).toString();
		}
	}

	/**
	 * Get game package as a pathname
	 * 
	 * @return pathname as string or the empty String if it is not a game or game package
	 * 		does not exist
	 */
	public String getGamePackagePathname() {
		if(!isGame() || getGamePackage() == null)
			return "";

		Path absPath = getAbsoluteFile(getGamePackage().getFileName().toString());
		
		if (!Files.exists(absPath))
			return "";
		
		return absPath.toString();
	}

	/**
	 * Get extras folder as a pathname
	 * 
	 * @return pathname as string or the empty String if it is not a game or extras folder
	 * 		does not exist
	 */
	public String getExtrasPathname() {
		if(!isGame())
			return "";

		Path extrasAbsPath = getAbsoluteFile(EXTRAS_FOLDER);
		
		if (!Files.exists(extrasAbsPath))
			return "";
		
		return extrasAbsPath.toString();
	}

	/**
	 * Get game artifact ID
	 * 
	 * @return game artifact ID or the empty String if it is not a game
	 */
	public String getGameArtifactId() {
		if(!isGame() || getGamePackage() == null)
			return "";
		
		return Filenames.getFileNameWithoutExtension(getGamePackage().getFileName().toString());
	}


	 /*-------------------------------------------------------------------*\
	  * 		            Operations                                    *
	 \*-------------------------------------------------------------------*/

	
	@MooshakOperation(
			name="Test Solution",
			inputable=false,
			tip="Test the program against test cases")
	private CommandOutcome testSolution() throws MooshakException {
		CommandOutcome outcome = new CommandOutcome();
		
		Contest contest = getParent().getParent();
		
		List<Path> solutions = new ArrayList<>(getSolutions());
		if(solutions.size() == 0) {
			outcome.setMessage("Missing program file");
			return outcome;
		}
		
		Tests tests = null;
		try {
			tests = open("tests");
		} catch (MooshakContentException e) {
			outcome.setMessage("Tests not found");
			LOGGER.log(Level.SEVERE, outcome.getMessage());
			return outcome;
		}
		
		Submissions submissions = contest.open("submissions");
		List<Test> testsList = new ArrayList<>();
		for (PersistentObject testPO : tests.getChildren(false)) {
			Test test = (Test) testPO;
			
			byte[] inputBytes = null;
			try {
				inputBytes = Files.readAllBytes(test.getAbsoluteFile()
						.resolve(test.getInput().getFileName()));
			} catch (IOException e1) {
				LOGGER.log(Level.SEVERE, "Can't read test input: " 
						+ test.getInput().getFileName().toString());
			}
			if (inputBytes == null)
				continue;

			byte[] outputBytes = null;
			try {
				outputBytes = Files.readAllBytes(test.getAbsoluteFile()
						.resolve(test.getOutput().getFileName()));
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, "Can't read test output: " 
						+ test.getOutput().getFileName().toString());
			}
			if (outputBytes == null)
				continue;
			
			test.setTimeout(0);
			test.setResult("");
			test.setSolutionErrors("");
			
			testsList.add(test);
		}
		Collections.sort(testsList, new Comparator<Test>() {

			@Override
			public int compare(Test t1, Test t2) {
				return t1.getIdName().compareTo(t2.getIdName());
			}
		});

		long problemTimeout = 0;
		for (Path solution : solutions) {
			outcome.addPair("result", solution.getFileName().toString() + "\n");
			
			if (testsList.isEmpty()) {
				outcome.addPair("result", "No tests found\n");
				continue;
			}
			
			long solutionTimeout = 0;
			Path solutionName = solution.getFileName();
			if (solutionName == null)
				continue;
			byte[] solutionCode = null;
			try {
				solutionCode = Files.readAllBytes(PersistentObject.getAbsoluteFile(solution));
			} catch (IOException e) {
				throw new MooshakException("Error reading solution!");
			}
			
			String id = contest.getTransactionId("admin", getIdName());
			
			final Submission submission = submissions.create(id, Submission.class);
			submission.receive(
					"admin", 
					null,
					solutionName.toString(), 
					solutionCode, 
					getIdName(),
					null,
					true);

			submission.analyze();

			ReportType reportType = submission.getAllReportTypes().get(
					submission.getAllReportTypes().size() - 1);
			
			if (reportType.getTests() == null) {
				
				for (Test test : testsList) {
					if (test.getResult().equals("") ||
							test.getResult().equalsIgnoreCase("Passed")) {
						
						test.setResult("Wrong");
					}
					
					test.setSolutionErrors(test.getSolutionErrors() + 
							solutionName.toString() + "; ");
					outcome.addPair("result", test.getIdName() + "\n" 
							+ "Fail");
				}
				
				try {
					submission.delete();
				} catch (MooshakContentException e) {
					LOGGER.log(Level.WARNING, "Test submission not removed");
				}
				
				continue;
			}
			
			int i = 0;
			for (TestType testType : reportType.getTests().getTest()) {
				Test test = testsList.get(i);
				long timeout = test.getTimeout();
				if (testType.getExecutionTime() != null && testType.getExecutionTime() > timeout)
					timeout = testType.getExecutionTime();
				test.setTimeout((int) timeout);
				
				if (timeout > solutionTimeout) 
					solutionTimeout = timeout;
				
				if (test.getResult().equals("") ||	test.getResult().equalsIgnoreCase("Passed")) {
					
					if (!testType.getClassify().equalsIgnoreCase("accepted")) 
						test.setResult("Wrong");
					else
						test.setResult("Passed");
				}
				
				if (!testType.getClassify().equalsIgnoreCase("accepted")) {
					test.setSolutionErrors(test.getSolutionErrors() + 
							solutionName.toString() + "; ");
					outcome.addPair("result", test.getIdName() + "\n" 
							+ "Fail");
				} else
					outcome.addPair("result", test.getIdName() + "\n" + "Success");
				
				i++;
			}
			
			solutionTimeout++;
			if (!solutionName.toString().endsWith(".java"))
				solutionTimeout *= 2;
			
			if (solutionTimeout > problemTimeout)
				problemTimeout = solutionTimeout;
			
			try {
				submission.delete();
			} catch (MooshakContentException e) {
				LOGGER.log(Level.WARNING, "Test submission not removed");
			}
		}

		if (problemTimeout > 0)
			setTimeout((int) problemTimeout);
		
		try {
			save();
		} catch (MooshakContentException e) {}
		
		outcome.setContinuation("TestSolutionResult");
		
		return outcome;
	}
	

	@MooshakOperation(
			name="TestSolutionResult",
			inputable=true,
			show=false)
	private CommandOutcome testSolutionShowResult(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();
		outcome.setMessage("Time Out:" + timeout);
		return outcome;
	}
	
	@MooshakOperation(
			name="Generate Outputs",
			inputable=false,
			tip="Generate outputs for the current test vector")
	private CommandOutcome generateOutputs() throws MooshakException {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Collection<Path> solutions = getSolutions();
			for (Path solution : solutions) {
				String filename = Filenames.getFileNameWithoutExtension(
						solution.getFileName().toString());
				outcome.addPair("solution", filename);
				outcome.addPair(filename + "_path", solution.toString());
			}
		} catch (MooshakException e) {
			outcome.setMessage("Finding solutions for problem "
					+ getIdName() + e.getMessage());
			return outcome;
		}
		
		outcome.setContinuation("Generate Outputs From Solution");
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Generate Outputs From Solution",
			inputable=true,
			show=false)
	private CommandOutcome generateOutputsFromSolution(MethodContext context)
			throws MooshakException {
		CommandOutcome outcome = new CommandOutcome();
		
		Contest contest = getGrandParent();
		Submissions submissions = contest.open("submissions");
		
		Path solution = Paths.get(context.getValue("solution"));
		
		Path solutionName = solution.getFileName();

		byte[] solutionCode = null;
		try {
			solutionCode = Files.readAllBytes(PersistentObject.getAbsoluteFile(solution));
		} catch (IOException e) {
			throw new MooshakException("Error reading solution!");
		}
		
		Tests tests = null;
		try {
			tests = open("tests");
		} catch (MooshakContentException e) {
			outcome.setMessage("Tests not found");
			return outcome;
		}
		
		Map<Integer, Path> testsPaths = new HashMap<>();
		List<String> inputs = new ArrayList<>();
		try(POStream<Test> stream = tests.newPOStream()) {
			for (Test test : stream) {
				String input = PersistentObject.getAbsoluteFileContentGuessingCharset(
						PersistentObject.getAbsoluteFile(test.getInput()));
				testsPaths.put(inputs.size(), test.getPath());
				inputs.add(input);
			}
		} catch (Exception e1) {
			outcome.setMessage("Error iterating over tests");
			LOGGER.log(Level.SEVERE, outcome.getMessage());
		}
		
		String id = contest.getTransactionId("admin", getIdName());
		
		final Submission submission = submissions.create(id, Submission.class);
		submission.receive(
				"admin", 
				null,
				solutionName.toString(), 
				solutionCode, 
				getIdName(),
				inputs,
				false);

		submission.analyze();
		
		Map<Integer, String> outputs = submission.getUserOutputs();
		
		for (Integer testId : outputs.keySet()) {
			Path testPath = testsPaths.get(testId);
			Path outputPath = testPath.resolve("output");
			Test test = PersistentObject.open(testPath);
			try {
				test.executeIgnoringFSNotifications( 
						() -> Files.write(PersistentObject.getAbsoluteFile(outputPath), 
								outputs.get(testId).getBytes()));
				test.setOutput(outputPath);
			} catch (IOException e) {
				throw new MooshakException(e.getMessage());
			}
			
			test.save();
		}

		try {
			submission.delete();
		} catch (MooshakContentException e) {
			LOGGER.log(Level.WARNING, "Test submission not removed");
		}
		
		outcome.setMessage("Output Generated");
		
		return outcome;
	}
	
	@MooshakOperation(name = "Set Up Problem SAs", 
			inputable = false, 
			tip = "Submit SAs developed as solutions to the problem")
	private CommandOutcome setUpProblemSAs() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Groups groups = getGrandParent().open("groups");
			for (PersistentObject groupPO : groups.getChildren(true)) {
				outcome.addPair("group_id", groupPO.getIdName());
				
				String designation = ((Group) groupPO).getDesignation();
				outcome.addPair(groupPO.getIdName() + "_name", 
						!designation.isEmpty() ? designation : groupPO.getIdName());
			}
		} catch (MooshakContentException e) {
			outcome.setMessage("Finding groups for problem "
					+ getIdName() + e.getMessage());
			return outcome;
		}
		
		try {
			Collection<Path> solutions = getSolutions();
			for (Path solution : solutions) {
				String filename = Filenames.getSafeFileName(solution).replace('.', '_');
				outcome.addPair("solution", filename);
				outcome.addPair(filename + "_path", solution.toString());
			}
		} catch (MooshakException e) {
			outcome.setMessage("Finding solutions for problem "
					+ getIdName() + e.getMessage());
			return outcome;
		}
		
		outcome.setContinuation("Submit Problem SAs");
		
		return outcome;
	}
	
	@MooshakOperation(name = "Submit Problem SAs", 
			inputable = true, 
			show = false)
	private CommandOutcome submitProblemSAs(MethodContext context) {
		CommandOutcome outcome = new CommandOutcome();
		
		String groupId = context.getValue("group_id");
		if (groupId == null) {
			outcome.setMessage("Group ID was not provided.");
			return outcome;
		}
		
		groupId = CommandService.sanitizePathSegment(groupId);
		if (groupId.isEmpty()) {
			outcome.setMessage("A valid group ID was not provided.");
			return outcome;
		}
		
		Groups groups = null;
		Group group = null;		
		try {
			groups = getGrandParent().open("groups");
			for (PersistentObject groupPO : groups.getChildren(true)) {
				if (groupPO.getIdName().equals(groupId)) {
					group = (Group) groupPO;
					break;
				}
			}
			
			if (group == null)
				group = groups.create(groupId, Group.class);
		} catch (MooshakContentException e) {
			outcome.setMessage("Creating or retrieving group ID " 
					+ groupId);
			return outcome;
		}
		
		List<String> solutionNames = context.getValues("solution");
		
		if (solutionNames.isEmpty()) {
			outcome.setMessage("No solutions selected.");
			return outcome;
		}
		
		for (String solutionName : solutionNames) {
			
			Team team = null;		
			try {
				team = group.find(solutionName);
				
				if (team == null && groups.find(solutionName) != null) {
					outcome.setMessage("Team ID " + solutionName + 
							" already exists in another group.");
					return outcome;
				}
				
				if (team == null)
					team = group.create(solutionName, Team.class);
			} catch (MooshakContentException e) {
				outcome.setMessage("Creating or retrieving team ID " 
						+ solutionName);
				return outcome;
			}
			
			// delete older submissions if present
			/*try {
				
				Submissions submissions = getGrandParent().open("submissions");
				List<Submission> olderSubmissions = submissions
						.getSubmissionsOfParticipantToProblem(getIdName(), team.getIdName());
				if (olderSubmissions != null) {
					for (Submission submission : olderSubmissions) 
						submission.delete();
				}
			} catch (MooshakException e) {
				outcome.setMessage("Error removing older submissions" + e.getMessage());
				return outcome;
			}*/
		
			// submit new problem SA
			try {
				
				String solutionPathStr = context.getValue(solutionName + "_path");
				Path solutionPath = PersistentObject.getAbsoluteFile(Paths.get(solutionPathStr));
				
				byte[] code = Files.readAllBytes(solutionPath);
				ParticipantManager.getInstance().evaluate(getGrandParent().getIdName(), 
						team.getIdName(), 
						null,
						solutionPath.getFileName().toString(), code,
						getIdName(), null, true);
			} catch (MooshakException e) {
				e.printStackTrace();
				outcome.setMessage("Submitting solution " + solutionName);
				return outcome;
			} catch (IOException e) {
				e.printStackTrace();
				outcome.setMessage("Could not read solution " + solutionName);
				return outcome;
			}
		}
		
		outcome.setMessage("Problem solutions have been submitted");
		  
		return outcome;
	}

	//-------------------- Setters and getters ----------------------//

	/**
	 * Get fatal errors messages
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
	 * get warning errors messages
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
	 * @param warning
	 */
	public void setWarning(String warning) {
		this.warning = warning;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Color assigned to problem
	 * @return
	 */
	public Color getColor() {
		if(color == null)
			return Color.black;
		else
			return color;
	}

	/**
	 * Set color assigned to problem. Use {@code null} to revert to default.
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Problem title
	 * @return
	 */
	public String getTitle() {
		if(title == null)
			return "";
		else		
			return title;
	}

	/**
	 * Set problem title
	 * @param title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Get difficulty of this problem
	 * @return
	 */
	public ProblemDifficulty getDifficulty() {
		return difficulty;
	}

	/**
	 * Set difficulty of this problem 
	 * @param difficulty
	 */
	public void setDifficulty(ProblemDifficulty difficulty) {
		this.difficulty = difficulty;
	}

	/**
	 * Get type of this problem 
	 * @return
	 */
	public ProblemType getType() {
		return type;
	}

	/**
	 * Set type of this problem 
	 * @param type
	 */
	public void setType(ProblemType type) {
		this.type = type;
	}

	public Path getHtmlDescription() {
		if(description == null)
			return null;
		else if(Files.exists(getAbsoluteFile(getPath(),description.toString())))
			return getPath().resolve(description);
		else
			return null;
	}

	public void setHtmlDescription(Path description) {
		if (description == null)
			this.description = description;
		else
			this.description = description.getFileName();
	}

	/**
	 * Get Path to PDF with a statement, or null if that file doesn't exist
	 * @return
	 */
	public Path getPdfDescription() {
		
		if(pdf == null)
			return null;
		else if(Files.exists(getAbsoluteFile(getPath(), pdf.toString())))
			return getPath().resolve(pdf);
		else
			return null;
	}

	/**
	 * Set Path to PDF with a statement
	 * @param pdf
	 */
	public void setPdfDescription(Path pdf) {
		if (pdf == null)
			this.pdf = pdf;
		else
			this.pdf = pdf.getFileName();
	}

	/**
	 * Get program solving this problem
	 * @deprecated use {@code getSolutions()} instead
	 * @return
	 */
	public Path getProgram() {
		if(program == null)
			return null;
		else
			return getPath().resolve(program);
	}

	/**
	 * Set program solving this problem
	 * @deprecated use {@code setSolutions()} instead
	 * @param program
	 */
	public void setProgram(Path program) {
		if(program != null)
			this.program = program.getFileName();
		else 
			this.program = program;
	}

	public Path getEnvironment() {
		if(environment == null)
			return null;
		else
			return getPath().resolve(environment);
	}

	public void setEnvironment(Path environment) {
		if (environment == null)
			this.environment = environment;
		else
			this.environment = environment.getFileName();
	}

	/**
	 * Current problem timeout in seconds
	 * @return
	 */
	public int getTimeout() {
		if(timeout == null)
			return 1;
		else
			return timeout;
	}

	/**
	 * Set Current problem timeout in seconds. 
	 * Use {@code null} to set default.
	 * @param timeout
	 */
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public String getStaticCorrector() {
		return staticCorrector;
	}

	public void setStaticCorrector(String staticCorrector) {
		this.staticCorrector = staticCorrector;
	}

	public String getDynamicCorrector() {
		return dynamicCorrector;
	}

	public void setDynamicCorrector(String dynamicCorrector) {
		this.dynamicCorrector = dynamicCorrector;
	}
	

	/**
	 * Binary name of game manager class
	 * @return the gameManager
	 */
	public String getGameManager() {
		return gameManager;
	}

	/**
	 * Set Binary name of game manager class
	 * @param gameManager the gameManager to set
	 */
	public void setGameManager(String gameManager) {
		this.gameManager = gameManager;
	}
	
	
	

	public String getOriginalLocation() {
		return originalLocation;
	}

	public void setOriginalLocation(String originalLocation) {
		this.originalLocation = originalLocation;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStop() {
		return stop;
	}

	public void setStop(Date stop) {
		this.stop = stop;
	}

	/**
	 * Get quiz config
	 *
	 * @return quiz config
	 */
	public Path getQuiz() {
		if(quiz == null)
			return null;
		else
			return getPath().resolve(quiz);
	}

	/**
	 * Set quiz config
	 * 
	 * @param quiz
	 */
	public void setQuiz(Path quiz) {
		if(quiz != null)
			this.quiz = quiz.getFileName();
		else 
			this.quiz = quiz;
	}
	
	/**
	 * Get the path to the package with the game configuration
	 * 
	 * @return {@link Path} to the package with the game configuration
	 */
	public Path getGamePackage() {
		if(gamePackage == null)
			return null;
		else
			return getPath().resolve(gamePackage);
	}

	/**
	 * Set the path to the package with the game configuration
	 * 
	 * @param gamePackage {@link Path} to the package with the game configuration
	 */
	public void setGamePackage(Path gamePackage) {
		this.gamePackage = gamePackage.getFileName();
	}
	
	/**
	 * Get the path to the extras of the game
	 * 
	 * @return {@link Path} to the extras of the game
	 */
	public Path getGameExtras() {
		if(!isGame() || gamePackage == null)
			return null;
		else
			return getPath().resolve(EXTRAS_FOLDER);
	}
	
	/**
	 * Return the kind of editor to use with this problem
	 * @return the editorKind
	 */
	public EditorKind getEditorKind() {
		if(editorKind == null)
			return EditorKind.CODE;
		else
			return editorKind;
	}

	/**
	 * Set the kind of editor to use with this problem
	 * @param editorKind the editorKind to set
	 */
	public void setEditorKind(EditorKind editorKind) {
		this.editorKind = editorKind;
	}
	

	/**
	 * Convenience method to return problem statement formated as an HTM string
	 * @return string containing statement in HTML format or null if unavailable
	 * @throws MooshakContentException on IO Exception
	 */
	public String getHTMLstatement() throws MooshakContentException  {
		Path htmlPath = getHtmlDescription();
		String htmlData = null;

		if(htmlPath != null)
			try {
				
				htmlData=PersistentCore
						.getRelativeFileContentGuessingCharset(htmlPath);

			} catch (IOException cause) {
				String message ="Error reading HTML statement of problem "+name;
				throw new MooshakContentException(message, cause);
			}

		return htmlData;
	}	
	
	/**
	 * Convenience method to return PDF filename;
	 * @return
	 */
	public String getPDFfilename() {
		Path pdf = getPdfDescription();
		
		if(pdf == null)
			return null;
		else
			return pdf.toString();
	}
	
	@Override
	protected void created() throws MooshakContentException {
		resetGameManagerPool();
		
		attachGamePackageWatcher();
	}
	
	@Override
	protected boolean updated() throws MooshakContentException {
		broadcastProblemChanges();

		attachGamePackageWatcher();
		
		return false;
	}
	
	@Override
	protected void destroyed() throws MooshakContentException {
		
		detachGamePackageWatcher();
		
		super.destroyed();
	}

	/**
	 * Informs all listeners of this problem that a change happened
	 * @throws MooshakContentException 
	 */
	public void broadcastProblemChanges() throws MooshakContentException {
		if(getGrandParent() == null)
			return;
		
		LOGGER.log(Level.INFO, "Problem changed - broadcasting");
		
		ProblemDescriptionChangedEvent event = new ProblemDescriptionChangedEvent();
		event.setProblemId(getIdName());
		
		EventSender.getEventSender().send(getGrandParent().getIdName(), event);
	}

	/**
	 * Select non hidden files (exclude directories and hidden files)
	 */
	private static final Filter<Path> FILE_CONTENT_FILTER = 
			new DirectoryStream.Filter<Path>() {

		@Override
		public boolean accept(Path entry)
				throws IOException {
			
			if(Files.isDirectory(entry))
				return false;
			if(Filenames.getSafeFileName(entry).startsWith("."))
				return false;
			
			return true;
		}
	};

	private final static Pattern PATTERN_NOT_PROGRAM_EXTENSION = 
			Pattern.compile("^(.*\\.(?!(out|class|exe)$))?[^.]*$");
	
	/**
	 * Returns a list with solutions (list with one element if program 
	 * not null)
	 * @return
	 * @throws MooshakException 
	 */
	public Collection<Path> getSolutions() throws MooshakException {
		List<Path> files = new ArrayList<>();
		
		if(program != null)
			files.add(getProgram());
		
		try( DirectoryStream<Path> stream=Files.newDirectoryStream(
				Paths.get(getAbsoluteFile().toString(), SOLUTIONS_FOLDER) ,
				FILE_CONTENT_FILTER)) {
		
			for(Path file: stream) {
				if (PATTERN_NOT_PROGRAM_EXTENSION.matcher(
											Filenames.getSafeFileName(file)).matches())
					files.add(getPath().resolve(Paths.get(SOLUTIONS_FOLDER, 
							Filenames.getSafeFileName(file))));
			}
		
		} catch (IOException cause) {
			String message = "Listing contents";
			throw new MooshakException(message, cause);
		}
		
		return files;
	}
	
	/**
	 * Get solutions as a Mooshak Value 
	 * @return
	 * @throws MooshakException
	 */
	public MooshakValue getSolutionsAsValue() throws MooshakException {

		if(getProgram() != null)
			try {
				String programFileName=Filenames.getSafeFileName(getProgram()); 
				return new MooshakValue("Program", 
						programFileName,
						Files.readAllBytes(Paths.get(getAbsoluteFile().toString(), 
								programFileName)));
			} catch (IOException e) {
				throw new MooshakException("Could not read program");
			}
		
		MooshakValue value = new MooshakValue("Solution");
		
		List<Path> sols = new ArrayList<>(getSolutions());
		String solFileName = "";
		for (Path sol : sols) {
			try {
				solFileName = Filenames.getSafeFileName(sol);
				value.addFileValue(new MooshakValue("Solution", 
						solFileName, 
						Files.readAllBytes(PersistentObject.getAbsoluteFile(sol))));
			} catch (IOException e) {
				throw new MooshakException("Could not read solution " 
							+ Paths.get(getAbsoluteFile().toString(), SOLUTIONS_FOLDER,
									solFileName).toString(), e);
			}
		}
		
		return value;
	}

	/**
	 * Check if problem is open
	 * @return <code>true</code> if problem is open, <code>false</code> otherwise
	 */
	public boolean isOpen() {
		if (getStart() == null && getStop() == null)
			return true;
		Date now = new Date();
		if (getStart() == null)
			return now.before(getStop());
		if (getStop() == null)
			return now.after(getStart());
		return now.before(getStop()) && now.after(getStart());
	}
	
	/**
	 * Is it a game problem?
	 * @return
	 */
	public boolean isGame() {
		return editorKind == EditorKind.GAME &&
				gameManager != null && ! "".equals(gameManager.trim()); 
	}
	
	private String gamePackageAttached = null;
	private Runnable gamePackageCbDetachModification = null;
	private Runnable gamePackageCbDetachDeletion = null;
	
	/**
	 * Attach course file watcher
	 */
	public synchronized void attachGamePackageWatcher() {
		
		if (getGamePackage() == null) {
			detachGamePackageWatcher();
			deleteGameExtrasFolder();
			return;
		}
		
		Path gamePackageFileName = getGamePackage().getFileName();
		
		if (gamePackageFileName != null && gamePackageAttached != null
				&& gamePackageFileName.toString().equals(gamePackageAttached)) {
			return;
		} else if (gamePackageFileName != null) {
			
			LOGGER.severe("Unpacking game " + gamePackageFileName.toString());
			
			detachGamePackageWatcher();
			
			try {
				unpackGamePackage();
			} catch (MooshakContentException e) {
				LOGGER.severe("Unpacking game package file. Error: " + e.getMessage());
			}
			
			Path gamePackageFilePath = getAbsoluteFile(gamePackageFileName.toString());
			
			gamePackageCbDetachModification = PathManager.getInstance()
				.watchRegularFileForModification(gamePackageFilePath.toString(), new Runnable() {
				
				@Override
				public void run() {
					
					if (gamePackageFileName != null && gamePackageAttached != null
							&& gamePackageFileName.toString().equals(gamePackageAttached))
						return;
					
					LOGGER.severe("Unpacking game after modification " 
							+ gamePackageFileName.toString());
					
					try {
						unpackGamePackage();
					} catch (MooshakContentException e) {
						LOGGER.severe("Unpacking game package. Error: " + e.getMessage());
					}
				}
			});
			
			gamePackageCbDetachDeletion = PathManager.getInstance()
				.watchRegularFileForDeletion(gamePackageFilePath.toString(), new Runnable() {
				
				@Override
				public void run() {
					
					// delete old files
					deleteGameExtrasFolder();
				}
			});
			
			gamePackageAttached = gamePackageFileName.toString();
			
			resetGameManagerPool();
		}
	}
	
	/**
	 * Detach game package file watcher
	 */
	public void detachGamePackageWatcher() {
		
		if (gamePackageCbDetachModification != null)
			gamePackageCbDetachModification.run();
		
		if (gamePackageCbDetachDeletion != null)
			gamePackageCbDetachDeletion.run();
		
		gamePackageCbDetachModification = null;
		gamePackageCbDetachDeletion = null;
		gamePackageAttached = null;
	}

	private GameManagerPool gameManagerPool = null;

	private void resetGameManagerPool() {
		
		if (getGamePackage() == null || getGameManager() == null)
			gameManagerPool = null;
		else {
			if (gameManagerPool == null)
				gameManagerPool = new GameManagerPool(getGamePackage(), getGameManager());
			else {
				gameManagerPool.setGamePackagePath(getGamePackage());
				gameManagerPool.setGameManagerClassname(getGameManager());
			}
		}
	}
	
	public GameManagerWrapper checkoutGameManager() throws MooshakException {

		if (gameManagerPool == null)
			throw new MooshakException("Problem has no associated game package or manager class!");
		
		return gameManagerPool.checkOut();
	}
	
	public void checkinGameManager(GameManagerWrapper gameManager)
			throws MooshakException {

		if (gameManagerPool == null)
			throw new MooshakException("Problem has no associated game package or manager class!");
		
		gameManagerPool.checkIn(gameManager);
	}
	
	private static final String PACKAGE_PREFIX_WRAPPERS_FOLDER = "wrappers/";
	private static final String PACKAGE_PREFIX_IMAGES_FOLDER = "images/";
	private static final String PACKAGE_PREFIX_GAME_WRAPPERS_FOLDER = "%s/wrappers/";
	private static final String PACKAGE_PREFIX_SOLUTIONS_FOLDER = "%s/solutions/";
	private static final String PACKAGE_PREFIX_SKELETONS_FOLDER = "%s/skeletons/";

	/**
	 * Unpack the game package 
	 * @throws MooshakContentException - 
	 * 
	 * @throws IOException - If an I/O error occurs while reading the game package 
	 */
	private synchronized void unpackGamePackage() throws MooshakContentException {
		
		// delete old files
		deleteGameExtrasFolder();
		
		if (getGamePackage() == null) 
			return;
		
		// unpack
		String packageName = getGamePackage().getFileName().toString();
		String artifactId = Filenames.getFileNameWithoutExtension(packageName);
		
		JarFile packageFile;
		try {
			packageFile = new JarFile(getAbsoluteFile(packageName).toFile());
		} catch (IOException e) {
			throw new MooshakContentException("Cannot create JarFile for game package file: " 
					+ packageName);
		}
		
		String gameWrappersFolder = String.format(PACKAGE_PREFIX_GAME_WRAPPERS_FOLDER, 
				artifactId);
		String solutionsFolder = String.format(PACKAGE_PREFIX_SOLUTIONS_FOLDER,
				artifactId);
		String skeletonsFolder = String.format(PACKAGE_PREFIX_SKELETONS_FOLDER,
				artifactId);
		
		Enumeration<JarEntry> enumEntries = packageFile.entries();
		while (enumEntries.hasMoreElements()) {
		    JarEntry entry = (JarEntry) enumEntries.nextElement();
		    
		    if (entry.isDirectory())
		    	continue;
		    
		    String fileName = entry.getName();
		    
		    if (fileName.startsWith(PACKAGE_PREFIX_WRAPPERS_FOLDER)) {
		    	
		    	Path filePath = Paths.get(fileName);
		    	extractPackageEntry(packageFile, entry, getAbsoluteFile(EXTRAS_FOLDER)
		    			.resolve(filePath));
		    } else if (fileName.startsWith(gameWrappersFolder)) {
		    	
		    	Path filePath = Paths.get(fileName);
		    	extractPackageEntry(packageFile, entry, getAbsoluteFile(EXTRAS_FOLDER)
		    			.resolve(filePath));
		    } else if (fileName.startsWith(solutionsFolder)) {
		    	
		    	Path filePath = Paths.get(fileName);
		    	extractPackageEntry(packageFile, entry, getAbsoluteFile(SOLUTIONS_FOLDER)
		    			.resolve(filePath.subpath(2, filePath.getNameCount())));
		    } else if (fileName.startsWith(skeletonsFolder)) {
		    	
		    	Path filePath = Paths.get(fileName);
		    	
		    	if (filePath.getNameCount() < 4)
		    		continue;
		    	
		    	String languageExt = filePath.getName(2).toString();
		    	Path skeletonFilePath = filePath.subpath(3, filePath.getNameCount());
		    	
		    	Skeletons skeletons = open(SKELETONS_FOLDER);
		    	Skeleton skeleton = PersistentObject.create(
		    			skeletons.getPath().resolve(languageExt), Skeleton.class);
		    	skeleton.setExtension(languageExt);
		    	skeleton.setShowing(true);
		    	skeleton.setSkeleton(skeletonFilePath);
		    	skeleton.save();
		    	
		    	extractPackageEntry(packageFile, entry, skeleton.getAbsoluteFile()
		    			.resolve(skeletonFilePath));
		    } else if (fileName.startsWith(PACKAGE_PREFIX_IMAGES_FOLDER)) {
		    	
		    	Path filePath = Paths.get(fileName);
		    	extractPackageEntry(packageFile, entry, getAbsoluteFile(IMAGES_FOLDER)
		    			.resolve(filePath.subpath(1, filePath.getNameCount())));
		    }
		}
		
		try {
			packageFile.close();
		} catch (IOException e) {
			throw new MooshakContentException("Cannot close game package JarFile.");
		}
		
	}

	/**
	 * Extract an entry (file) from the package to a destination file
	 * 
	 * @param packageFile
	 * 			  {@link JarFile} package file
	 * @param entry
	 *            {@link JarEntry} entry (file) to extract
	 * @param destinationPath
	 *            {@link Path} path of the destination file
	 */
	private void extractPackageEntry(JarFile packageFile, 
			JarEntry entry, Path destinationPath) {
		
		if (!Files.exists(destinationPath.getParent()))
			destinationPath.getParent().toFile().mkdirs();

		try (
				InputStream is = packageFile.getInputStream(entry);
				FileOutputStream fos = new FileOutputStream(destinationPath.toFile());
				) {
		
			byte[] buf = new byte[8192];

	        int i;
	        while ((i = is.read(buf)) != -1)
	            fos.write(buf, 0, i);
		} catch (IOException e) {
			LOGGER.severe("Could not extract file from package. Error: " +
					e.getMessage());
		}
	}
	
	/**
	 * Delete game extras folder
	 */
	private void deleteGameExtrasFolder() {
		
		try {
			Path absPath = getAbsoluteFile(EXTRAS_FOLDER);
			if (Files.exists(absPath))
				FileUtils.deleteRecursively(absPath, false);
		} catch (IOException e) {
			LOGGER.severe("Cannot delete previous game files. Error: " + e.getMessage());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((difficulty == null) ? 0 : difficulty.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Problem other = (Problem) obj;
		if (difficulty != other.difficulty)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

}
