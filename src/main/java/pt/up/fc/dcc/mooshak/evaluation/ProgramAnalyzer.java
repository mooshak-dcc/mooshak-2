package pt.up.fc.dcc.mooshak.evaluation;

import static pt.up.fc.dcc.mooshak.evaluation.SubmissionSecurity.Level.COMPILATION;
import static pt.up.fc.dcc.mooshak.evaluation.SubmissionSecurity.Level.EXECUTION;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.erl.ReportType;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.Language;
import pt.up.fc.dcc.mooshak.content.types.Languages;
import pt.up.fc.dcc.mooshak.content.types.MooshakTypeException;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Test;
import pt.up.fc.dcc.mooshak.content.types.Tests;
import pt.up.fc.dcc.mooshak.content.types.UserTestData;
import pt.up.fc.dcc.mooshak.content.util.Compare;
import pt.up.fc.dcc.mooshak.content.util.FileUtils;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.evaluation.ExecutionResourceUsage.UsageVars;
import pt.up.fc.dcc.mooshak.evaluation.feedback.Feedbacker;
import pt.up.fc.dcc.mooshak.evaluation.feedback.FeedbackerFactory;
import pt.up.fc.dcc.mooshak.shared.MooshakException;

/**
 * An analyzer of program submissions. Program analysis is divided in 2 phases:
 * static analysis and dynamic analysis. Static analysis checks submission
 * data, collects parameters and performs compilation. Dynamic analysis
 * executes the program with test data and compares expected and obtained
 * output, if available. The static and dynamic phases can be complemented 
 * with a special corrector.
 * 
 * @author José Paulo Leal <code>zp@dcc.fc.up.pt</code>
 */
public class ProgramAnalyzer implements Analyzer {

	private static final int SLACK_TIME = 10;
	protected Submission submission = null;
	protected Reporter reporter = null;
	
	protected final static Logger LOGGER = Logger.getLogger("");
	
	protected static final int 	AVAILABLE 
			= Runtime.getRuntime().availableProcessors();
	protected static final ExecutorService POOL
			= Executors.newFixedThreadPool(AVAILABLE);
	
	static {
		LOGGER.log(Level.INFO,"Available processors/workers: "+AVAILABLE);
	}
		
	public ProgramAnalyzer(Submission submission) {
		this.submission = submission;
	}
	
	/**
	 * Data collected and preserved for each test run
	 */
	public class TestRun {
		int mark;
		public Classification classification;
		public String observations = "";
		boolean skipTests = false;
		public EvaluationParameters parameters;
	}
		
	private List<TestRun> testRuns = new ArrayList<TestRun>();
	
	private Reporter getReporter() {
		if(reporter == null)
			reporter = new Reporter(submission);
		return reporter;
	}
	
	@Override
	public void setSubmission(Submission submission) {
		this.submission = submission;
	}

	@Override
	public Submission getSubmission() {
		return submission;
	}
	
	/**
	 * Return all reports identified by its number for this analysis
	 * @return
	 * @throws MooshakSafeExecutionException
	 */
	@Override
	public List<ReportType> getAllReports() throws MooshakException {
		return getReporter().getAllReports();
	}
	
	/**
	 * Return a report identified by its number for this analysis
	 * @return
	 * @throws MooshakSafeExecutionException
	 */
	@Override
	public ReportType getReport(int reportNumber) throws MooshakException {
		return getReporter().getReport(reportNumber);
	}
	

	@Override
	public void analyze() {
		List<PersistentObject> paths = new ArrayList<>();
		paths.add(submission);
		
		try {
			paths.add(submission.getParent());
		} catch (MooshakContentException cause) {
			LOGGER.log(Level.SEVERE,"Error getting user data directory",cause);
			submission.setClassify(Classification.REQUIRES_REEVALUATION);
			submission.setObservations(cause.getMessage());
			return;
		}
		
		if(submission.hasUserTestData()) {
			UserTestData userData;
			try {
				userData = submission.getUserTestData();
				paths.add(userData);
			} catch (MooshakContentException e) {
				LOGGER.log(Level.WARNING,"Error getting user data directory",e);
			}
		}
		
		PersistentObject.executeIgnoringFSNotifications(this::analyzeSafe,paths);
	}
	
	private void analyzeSafe() {
		
		EvaluationParameters parameters = new EvaluationParameters();
		Contest contest;
		
		getReporter().start();
		
		try {
			contest = submission.getParent().getParent();
		} catch (MooshakContentException cause) {
			submission.setClassify(Classification.REQUIRES_REEVALUATION);
			submission.setObservations(cause.getMessage());
			return;
		}
		
		parameters.setSubmission(submission);
		parameters.setContest(contest);
		parameters.setDirectory(submission.getPath());
		
		try {
			staticAnalysis(parameters);
			dynamicAnalysis(parameters);
		} catch(MooshakEvaluationException cause) {
			if(cause.getClassification() == Classification.REQUIRES_REEVALUATION)
				LOGGER.log(Level.SEVERE,"Error requiring reevaluation",cause);
			submission.setClassify(cause.getClassification());
			submission.setObservations(cause.getMessage());
			submission.setFeedback(cause.getFeedback());
			
			Integer mark = cause.getMark();
			if(mark != null)
				submission.setMark(mark);
		}
		
		try {
			reporter.save();
			submission.save();
		
			submission.setEvaluated(true);
		} catch (Exception cause) {
			submission.setObservations("Saving submission/report failed:"+
					cause.getMessage()+
					"\n\tPossible classification:"+submission.getClassify());	
			submission.setClassify(Classification.REQUIRES_REEVALUATION);
			LOGGER.log(Level.SEVERE,"Error saving report",cause);
		}

	}

	/**
	 * Perform the static analysis of the submission
	 * 
	 * @param parameters {@link EvaluationParameters} parameters collected and used throughout the evaluation
	 * @throws MooshakEvaluationException if any check fails
	 */
	protected void staticAnalysis(EvaluationParameters parameters) throws MooshakEvaluationException {
		checkTeam(parameters); 
		checkLanguage(parameters); 
		checkProblem(parameters); 
		checkProgram(parameters); 
		checkCompilation(parameters);
		staticCorrector(parameters);
	}
	
	/**
	 * Perform the dynamic analysis of the submission
	 * 
	 * @param parameters {@link EvaluationParameters} parameters collected and used throughout the evaluation
	 * @throws MooshakEvaluationException if any check fails
	 */
	protected void dynamicAnalysis(EvaluationParameters parameters) throws MooshakEvaluationException {
		
		runTests(parameters);
	}

	/**
	 *  Step 1 - Check team making this submission and set team as parameter 
	 * @param parameters collected and used throughout the evaluation  
	 * @throws MooshakEvaluationException if teams of groups cannot be loaded
	 */
	private void checkTeam(EvaluationParameters parameters) 
		throws MooshakEvaluationException {
		
		try {
			Groups groups = parameters.getContest().open("groups");
			parameters.setTeam(groups.find(submission.getTeamId()));
		} catch (MooshakContentException cause) {
			String message = "Cannot load team named "+submission.getTeamId();
			throw new MooshakEvaluationException(message,cause);
		} 
	}
	
	/**
	 *  Step 2 - Check if language is admissible and sets language 
	 *  and related parameters.
	 * @param parameters collected and used throughout the evaluation
	 * @throws MooshakEvaluationException if languages are not available
	 */
	private void checkLanguage(EvaluationParameters parameters) 
			throws MooshakEvaluationException {
		Path program = submission.getProgram();
		Path programFileName = null;
		String programName = null;
		String extension = null;
		Language language;
		
		if(program == null)
			throw new MooshakEvaluationException("Program not set");
		
		programFileName = program.getFileName();
		
		if(programFileName == null)
			throw new MooshakEvaluationException("Program file name empty");
			
		programName = programFileName.toString();
		extension = Filenames.extension(programName);
		
		try {
			Languages languages = parameters.getContest().open("languages");
			if(extension == null) 
				throw new MooshakEvaluationException(
						"Program filename without extension!");
			else if((language=
					languages.findLanguageWithExtension(extension))==null)
				throw new MooshakEvaluationException(
						"Cannot load language with extension " + 
								extension);	
			else {
				parameters.setLanguage(language);
				submission.setLanguage(language);
			}
		} catch (MooshakContentException cause) {
			String message = "Cannot load language with extension "+extension;
			throw new MooshakEvaluationException(message,cause);
		}
		
		reporter.setProgrammingLanguage(language);
		
		try {
			int maxOutput = parameters.getLanguages().getMaxOutput();
			parameters.setMaxOutput(maxOutput);
		} catch (MooshakTypeException cause) {
			String message = "Cannot load languages definition of max output";
			throw new MooshakEvaluationException(message,cause);
		}
		
	}

	private static final int TIMEOUT = 1; // seconds

	/**
	 * 3 - Check if problem exists and sets it in the evaluation parameters.
	 *   Use a null problem id if problem is undefined
	 * 
	 * @param parameters
	 *            collected and used throughout the evaluation
	 * @throws MooshakTypeException
	 */ 
	private void checkProblem(EvaluationParameters parameters)
			throws MooshakEvaluationException {

		if (submission.getProblemId() == null) {
			parameters.setProblem(null);
			parameters.setTimeout(TIMEOUT);
		} else {
			try {
				Problems problems = submission.open("../../problems");
				Problem problem = problems.find(submission.getProblemId());
				parameters.setProblem(problem);
				parameters.setTimeout(problem.getTimeout());
			} catch (MooshakContentException cause) {
				String message = "Cannot load problem with ID " + 
						submission.getProblemId();
				throw new MooshakEvaluationException(message, cause);
			}

		}
		
		reporter.setExercise(parameters.getProblem());
		
		
	}

	/**
	 * Step 4 - Check program size. 
	 * @param parameters collected and used throughout the evaluation
	 * @throws MooshakEvaluationException if program too large
	 */
	private void checkProgram(EvaluationParameters parameters) 
			throws MooshakEvaluationException {
		
		Path program = submission.getProgram();
		
		if(! Files.isReadable(PersistentCore.getAbsoluteFile(program))) {
			String message = "Cannot read program "+ program;
			throw new MooshakEvaluationException(message);
		}
		parameters.setProgram(program);
		
		// on older versions size may not be set 
		if ( submission.getSize() == 0 )
			try {
				submission.setSize(Files.size(program.toAbsolutePath()));
			} catch (IOException cause) {
				throw new MooshakEvaluationException("getting program size",
						cause);
			}
		
		// on older versions line count may not be set 
		if ( submission.getLines() == 0 ) {
			try {
				submission.setLines(FileUtils.lineCount(program.toAbsolutePath()));
			} catch (IOException cause) {
				throw new MooshakEvaluationException("counting program lines",
						cause);
			}
		}

		try {
			if(submission.getSize() > parameters.getLanguages().getMaxProg()) 
				throw new MooshakEvaluationException(
						"Program too long "+submission.getSize(),
						Classification.PROGRAM_SIZE_EXCEEDED);
		} catch (MooshakTypeException cause) {
			String message = "Error checking program "+program;
			throw new MooshakEvaluationException(message,cause);
		}
	}
	
	/**
	 *  Step 5 - Compile program and check compilation errors.
	 * @param parameters collected and used throughout the evaluation
	 * @throws MooshakEvaluationException on unexpected compilation output
	 */
	protected void checkCompilation(EvaluationParameters parameters) 
			throws MooshakEvaluationException {
		
			Language language = parameters.getLanguage();
			String compile = language.getCompile().trim();
			
			if("".equals(compile)) {
				// silently ignore missing compilation line
			} else if (! evaluatedJavaSpecialCorrector(compile,parameters))
				compileCommandLine(language, parameters);
	}
	
	
	
	/**
	 * Perform all tests available for this problem
	 * @param parameters collected and used throughout the evaluation
	 * @throws MooshakEvaluationException
	 * @throws MooshakSafeExecutionException 
	 */
	private void runTests(final EvaluationParameters parameters) 
			throws MooshakEvaluationException {
		
		try {
			Submissions submissions = submission.getParent();
			parameters.setRunAllTests(submissions.isRunAllTests());
		} catch(MooshakContentException cause) {}
			
		try {
			
			SubmissionSecurity security = 
					new SubmissionSecurity(parameters,EXECUTION);

			Classification submissionClassification = Classification.ACCEPTED;
			int submissionMark = 0;

			parameters.setMonitorResources(true); // monitor resource usage;

			List<Future<TestRun>> futures = new ArrayList<>();
			if(submission.isConsider())
				futures = runProblemTests(parameters);
			else	
				futures = runInputData(parameters);
			
			security.relax();
			
			for(Future<TestRun> future: futures) {
				TestRun testRun;
				
				try {
					testRun = future.get();
				} catch (InterruptedException | ExecutionException cause) {
					String message = "Error running test case:"+cause.getMessage();
					throw new MooshakEvaluationException(message,cause,
							Classification.REQUIRES_REEVALUATION);
				}
				if(submissionClassification.compareTo(testRun.classification) < 0) {
					submissionClassification = testRun.classification;
					submission.setObservations(testRun.observations);
				}
				submissionMark = submissionMark + testRun.mark;
				testRuns.add(testRun);
			}
		
			security.tighten();
			
			handleFeedback();
			
			submission.setClassify(submissionClassification);
			submission.setMark(submissionMark);
			
			reporter.conclude();
			
		} catch(MooshakSafeExecutionException cause) {
			
			throw new MooshakEvaluationException(
					"Unexpected security error",cause,
					Classification.REQUIRES_REEVALUATION);
		}
	}
	
	private static FeedbackerFactory feedbackerFactory 
		= FeedbackerFactory.getInstance();
	
	/**
	 * Handle feedback, if configured in submissions: 
	 * 1) get a feedbacker, 
	 * 2) make it summarize testRuns,
	 * 3) record feedback in the report and on submission   
	 */
	protected void handleFeedback() {

		try {
			Submissions submissions = submission.getParent();

			
			String team = labelOf((PersistentObject) submission.getTeam());
			String problem = labelOf(submission.getProblem());
			Feedbacker feedbacker = feedbackerFactory.
					getFeedbacker(submissions.getGiveFeedback(),team, problem);
			String feedback = feedbacker.summarize(testRuns);
			
			if(feedback != null)	
				submission.setFeedback(feedback);
	
		} catch (Exception cause) {
			LOGGER.log(Level.SEVERE, "Error providing feedback", cause);
		}
	}
	
	private String labelOf(PersistentObject object) {
		if(object == null)
			return "";
		else
			return object.getPath().toString();
	}
	

	/**
	 * Run test cases given by input data
	 * @param parameters and used throughout the evaluation
	 * @return
	 * @throws MooshakEvaluationException
	 */
	private List<Future<TestRun>> runInputData(
			final EvaluationParameters parameters) 
	    throws MooshakEvaluationException {
		final List<Future<TestRun>> futures = new ArrayList<>();
		
		if(! submission.hasUserTestData())
			return futures;
		
		try {
			final UserTestData userTestData = submission.getUserTestData();
			
			parameters.setUserTestData(userTestData);
			
			for(final Path input: userTestData.getInputFiles()) {
				final EvaluationParameters runParameters =  parameters.clone();
				final Path inputFileName = input.getFileName();
				
				if (inputFileName == null)
					continue;
				
				final String outputName = 
						inputFileName.toString() .replace("in", "out");
				
				final Path inputParent = input.getParent();
				
				if(inputParent == null)
					continue;
				
				final Path obtained = inputParent.resolve(outputName);
				
				runParameters.setInput(input);
				runParameters.setObtained(obtained);
				runParameters.setUserDataOrder(
						userTestData.orderOfOutputFile(obtained));
				
				futures.add(POOL.submit(new Callable<TestRun>() {
					@Override
					public TestRun call() throws Exception {	
						return runSingleTest(runParameters);
					}		
				}));
			}
		} catch (MooshakException cause) {
			throw new MooshakEvaluationException("Cannot read inputs",cause);
		}
		
		return futures;
	}

	/**
	 * Run test cases defined in given problem
	 * @param parameters and used throughout the evaluation
	 * @return
	 * @throws MooshakEvaluationException
	 */
	private List<Future<TestRun>> runProblemTests(
			final EvaluationParameters parameters) 
					throws MooshakEvaluationException {

		final List<Future<TestRun>> futures = new ArrayList<>();
		final Tests testsFolder;
		final List<Test> allTests;
		
		parameters.setUserTestData(null); // unset uset test cases
				
		try {
			testsFolder = parameters.getProblem().getData(Tests.class);
		} catch (MooshakContentException cause) {		
			String message = "Cannot load tests data";
			throw new MooshakEvaluationException(message,cause,
					Classification.REQUIRES_REEVALUATION);
		}	
		
		try {
			allTests = testsFolder.getChildren(Test.class, true);
		} catch (MooshakContentException cause) {			 
			String message = "Cannot load test collection";
			throw new MooshakEvaluationException(message,cause,
					Classification.REQUIRES_REEVALUATION);
		}
		
		Collections.sort(allTests);
		
		int testOrder = 0;
		for(final Test test: allTests) {
			final EvaluationParameters runParameters =  parameters.clone();
			final Path testPath = test.getPath();
			
			if(testPath == null)
				continue;
			
			Path testPathFileName = testPath.getFileName();
			
			if(testPathFileName == null)
				continue;
			
			String testName = testPathFileName.toString();
			String obtainedName = "."+testName+".obtained";
			Path obtained = submission.getPath().resolve(obtainedName);
			
			runParameters.setInput(test.getInput());
			runParameters.setExpected(test.getOutput());
			runParameters.setObtained(obtained);
			runParameters.setArgs(test.getArgs());
			runParameters.setPoints(test.getPoints());
			runParameters.setFeedback(test.getFeedback());
			runParameters.setShow(test.isShow());
			runParameters.setTestId(test.getIdName());
			runParameters.setTestOrder(testOrder++);
			
			futures.add(POOL.submit(new Callable<TestRun>() {
				@Override
				public TestRun call() throws Exception {	
					return runSingleTest(runParameters);
				}		
			}));
		}
		return futures;
	}
	
	/**
	 * Run a single test case
	 * @param parameters	and used for the evaluation of this run
	 * @return
	 * @throws MooshakTypeException
	 * @throws MooshakSafeExecutionException on IOException
	 */
	private TestRun runSingleTest(EvaluationParameters parameters) 
			throws MooshakTypeException, MooshakSafeExecutionException  {		
		TestRun testRun = new TestRun();		
		SafeExecution safeExecution;
		
		if(parameters.isSkipTests()) {
			testRun.observations = "Not evaluated due to excessive use of resources";
			testRun.classification = Classification.EVALUATION_SKIPPED;
			reporter.addTest(parameters, testRun, null);
			
			return testRun;
		}

		safeExecution = parameters.getLanguage().newSafeExecution(parameters);
		safeExecution.execute();

		String expected = getExpected(parameters.getExpected());
		String obtained = getObtained(safeExecution,parameters.getObtained());
		
		ExecutionResourceUsage usage = null;
		int exitCode  = safeExecution.getExitCode();
		String errors = safeExecution.getErrors();
		
		testRun.classification = null;
		testRun.parameters = parameters;
		
		/****************************************\
		 *  DON'T SHOW STDERR ON OBSERVATIONS   *
		\****************************************/

		// first round: test integrity
		if(exitCode != 0) 
			classifyExitCode(exitCode,testRun);
		else if(! "".equals(errors)) {
			testRun.classification = Classification.RUNTIME_ERROR;
		} else if(obtained.length() >= parameters.getMaxOutput())
			testRun.classification = Classification.OUTPUT_LIMIT_EXCEEDED;
		else {
			usage = safeExecution.getUsage();
			updateUsageAttributes(usage);
			String processingErrors = usage.getProcessingErrors();
			int errorCode = usage.getErrorCodeValue();

			if( processingErrors != null) {
				testRun.classification = Classification.REQUIRES_REEVALUATION;
				testRun.observations = processingErrors;
			} else if(errorCode > 0)
				classifyStderr(usage,testRun,obtained);
			else if(errorCode < 0 ) // signal != null
				classifySignal(parameters,usage,obtained,testRun);
			else // errorCode == 0
				classifyMessages(parameters,usage,testRun);
		}
	
		// second round: test outputs
		if(testRun.classification == null) {
			
			if(expected == null)
				testRun.classification = Classification.ACCEPTED;
			else if(expected.equals(obtained)) 
				testRun.classification = Classification.ACCEPTED;
			else if(Compare.normalize(expected).equals(Compare.normalize(obtained)))
				if (submission.isFormative())
					testRun.classification = Classification.ACCEPTED;
				else
					testRun.classification = Classification.PRESENTATION_ERROR;
			else 
				testRun.classification = Classification.WRONG_ANSWER; 
		}
		
		UserTestData usertestData = parameters.getUserTestData();
		if(usertestData != null) {
			int order = parameters.getUserDataOrder();
			if (usage != null) {
				try {
					usertestData.addExecutionTimes(order, usage.getUsage(UsageVars.cpu));
				} catch (MooshakSafeExecutionException e) {
					usertestData.addExecutionTimes(order, -1);
				}
			} else
				usertestData.addExecutionTimes(order, -1);
		}
		
		if(testRun.skipTests && ! parameters.isRunAllTests()) {
			parameters.setSkipTests(true);
		}
		
		if(testRun.classification == Classification.ACCEPTED)
			testRun.mark = parameters.getPoints();
		
		reporter.addTest(parameters, testRun, usage);
		return testRun;
	}

	/**
	 * Get obtained output from safe execution and save if to given file, 
	 * if one is given (non null)
	 * @param safeExecution	
	 * @param obtainedFile	 from where obtained output data is written, if non null
	 * @return
	 * @throws MooshakSafeExecutionException on IOException
	 */
	private String getObtained(SafeExecution safeExecution,Path obtainedFile) 
			throws MooshakSafeExecutionException {
		String obtained = safeExecution.getOutput();
		
		if(obtainedFile != null) {
			try(OutputStream stream = Files.newOutputStream(obtainedFile)) {
				stream.write(obtained.getBytes());
			} catch (IOException cause) {
				String message = "writing obtained output file";
				throw new MooshakSafeExecutionException(message,cause);
			}
			try {
				Files.setPosixFilePermissions(obtainedFile, 
						PersistentCore.OWNER_READ_WRITE_PERMISSIONS);
			} catch (IOException cause) {
				String message = "setting permissions is obtained file";
				throw new MooshakSafeExecutionException(message,	cause);
			}
		}
		return obtained;
	}

	/**
	 * Get expected output from given file, or null if no file given (null) 
	 * @param output	file, or null	
	 * @return
	 * @throws MooshakTypeException on IOException
	 */
	private String getExpected(Path output) throws MooshakTypeException {
		String expected = null;
	
		if(output != null) {
		
			try {
				expected = new String(Files.readAllBytes(output));
			} catch (IOException cause) {
				String message = "reading output file";
				throw new MooshakTypeException(message,cause);
			}
		}
		return expected;
	}
	
	private static final int KILL_SIGNAL_EXIT_VALUE = 137;
	
	private void classifyExitCode(int exitCode,TestRun testRun) {
		String message = null;;
		Classification classification = Classification.REQUIRES_REEVALUATION;

		switch(exitCode) {
		
		case KILL_SIGNAL_EXIT_VALUE:
			classification = Classification.MEMORY_LIMIT_EXCEEDED;
			message = "safeexec was probably victim of OOM kill"+
					"due to overuse of memory by evaluated process (check it)";
			break;
		default:
			message = "Unexepected safeexec exit code:"+exitCode;
		}
		
		if(message != null)
			testRun.observations = message;
		testRun.classification = classification;
	}

	private void classifySignal(EvaluationParameters parameters,
			ExecutionResourceUsage usage, 
			String obtained,
			TestRun testRun) 
			throws MooshakSafeExecutionException, MooshakTypeException {
		
		Integer errorCode = usage.getErrorCode();
		
	   if( 
			   errorCode == 13 && 
			   obtained.length() > parameters.getLanguages().getMaxOutput()
			   ) {
		   testRun.classification = Classification.OUTPUT_LIMIT_EXCEEDED;
		   testRun.skipTests = true;
	   } else if(requiresReevaluation(usage.getErrorInfo())) {
		   testRun.classification = Classification.REQUIRES_REEVALUATION;
	   } else {
		   testRun.classification = Classification.RUNTIME_ERROR;
			if (submission.isFormative())
				testRun.observations += "Error " + errorCode + ": "
						+ usage.getErrorInfo() + "\n";
	   }
	}


	private void classifyStderr(
				ExecutionResourceUsage usage, 
				TestRun testRun,
				String obtained)
			throws MooshakSafeExecutionException {
		
		String errorInfo = usage.getErrorInfo();
		
		if(errorInfo == null || "".equals(errorInfo)) {
			
			if(usage.getErrorCode() == 200 && 
				obtained.startsWith("Runtime error")) { 
				// on runtime error fpc writes to stdout and exits with 200 :-(
				
				testRun.classification = Classification.RUNTIME_ERROR;
			} else {
				testRun.classification = Classification.INVALID_EXIT_VALUE;
				testRun.observations += 
					"Invalid exit value: "+usage.getErrorCode()+"\n";
				if(usage.getProcessingErrors() != null)
					testRun.observations += usage.getProcessingErrors()+"\n";
			}
		} else if(errorInfo.contains("OutOfMemoryError")) {
			testRun.classification = Classification.MEMORY_LIMIT_EXCEEDED;
			testRun.skipTests = true;
		} else if(requiresReevaluation(errorInfo)) {
			testRun.classification = Classification.REQUIRES_REEVALUATION;
			testRun.skipTests = true;
		} else {
			testRun.classification = Classification.RUNTIME_ERROR;
			if (submission.isFormative())
				testRun.observations += "Error " + usage.getErrorCode() + ": "
						+ errorInfo + "\n";
		}
	}


	/** 
	 * Classification based error message of safeexec 
	 * @param parameters
	 * @param usage
	 * @param testRun
	 * @throws MooshakSafeExecutionException
	 * @throws MooshakTypeException
	 */
	private void classifyMessages(
			EvaluationParameters parameters,
			ExecutionResourceUsage usage, 
			TestRun testRun) 
			throws MooshakSafeExecutionException, MooshakTypeException {
		
		switch(usage.getErrorInfo()) {
		case "Memory Limit Exceeded":
			testRun.classification = Classification.MEMORY_LIMIT_EXCEEDED;
			break;
		case "Time Limit Exceeded":
			testRun.classification = Classification.TIME_LIMIT_EXCEEDED;
			
			if(usage.getUsage(UsageVars.elapsed) > 
					parameters.getLanguages().getRealTimeout()) {
				testRun.observations += "Real timeout exceeded";
				testRun.observations += "\n(tried to read past end of file?)";
			} else {
				testRun.observations += "Execution timeout exceeded";
			}
			testRun.skipTests = true;
			break;
		case "Output Limit Exceeded":
			testRun.classification = Classification.OUTPUT_LIMIT_EXCEEDED;
			testRun.skipTests = true;
			break;
		case "Invalid Function":
		case "Internal Error":
			testRun.classification = Classification.INVALID_FUNCTION;
			testRun.observations = usage.getErrorInfo();
			break;
		}
		
	}


	private void updateUsageAttributes(ExecutionResourceUsage usage)
			throws MooshakSafeExecutionException {
		Double runElapsed = usage.getUsage(UsageVars.elapsed);
		if(runElapsed > submission.getElapsed())
			submission.setElapsed(runElapsed);
		
		Double runCpu = usage.getUsage(UsageVars.cpu);
		if(runCpu > submission.getCpu())
			submission.setCpu(runCpu);
		
		Double runMemory = usage.getUsage(UsageVars.memory);
		if(runMemory > submission.getMemory())
			submission.setMemory(runMemory);
	}
	
	
	/**
	 * Compile from a command line, relaxing security on the file system
	 * during compilation 
	 * 
	 * @param language
	 * @param parameters
	 * @throws MooshakEvaluationException
	 */
	private void compileCommandLine(
			Language language, 
			EvaluationParameters parameters) 
					throws MooshakEvaluationException {

		String message = "";

		try {
			SubmissionSecurity security = 
					new SubmissionSecurity(parameters, COMPILATION);
			
				security.relax();
				message = language.compile(parameters);
				security.tighten();

		} catch (MooshakSafeExecutionException | MooshakTypeException cause) {
			String errorMessage = "Unexpected compilation error";
			LOGGER.log(Level.SEVERE,errorMessage,cause);
			throw new MooshakEvaluationException(errorMessage,cause,
					Classification.COMPILE_TIME_ERROR);
		}
		
		Classification classification;
		if(! "".equals(message))  {
			if(requiresReevaluation(message))
				classification = Classification.REQUIRES_REEVALUATION;
			else
				classification = Classification.COMPILE_TIME_ERROR;

			reporter.setCompilationErrors(message);

			throw new MooshakEvaluationException(message,classification);
		}
	}
	
	
	/**
	 * Performs static evaluation, if a problem was defined, and if it
	 * contains a static evaluator
	 * @param parameters
	 * @throws MooshakEvaluationException
	 */
	private void staticCorrector(EvaluationParameters parameters)
			throws MooshakEvaluationException {

		if(parameters.getProblem() == null)
			return;
		
		String staticCorrectorName = getStaticCorretorName(parameters);
		
		if(staticCorrectorName == null || "".equals(staticCorrectorName))
			return;
		else if(! evaluatedJavaSpecialCorrector(staticCorrectorName, parameters)) {
				
			Language language = parameters.getLanguage();
			Map<String,String> vars = parameters.collectVariables();
			String commandLine = language.expand(staticCorrectorName, vars);
			
			LOGGER.severe(commandLine);
		
			invokeCorretor(parameters,commandLine,vars);
		}
	}
	
	private String getStaticCorretorName(EvaluationParameters parameters) {
		
		Problem problem = parameters.getProblem();
		
		if(problem == null)
			return null;
		else {
			String name = problem.getStaticCorrector();
			
			if(name == null)
				return name;
			else 
				return name.trim();
		}

	}
	
	
	/**
	 * Tries to evaluate a special corrector with given binary name (as string)
	 * using given evaluation parameters. This method returns {code true} if the
	 * evaluation was performed and {@code false} otherwise. 
	 * The execution does not return a value, but should throw a
	 * {@code MooshakEvaluationException} is the submission should not be 
	 * accepted. The exception should indicate the reasons for not accepting.
	 * 
	 * @param evaluator
	 * @param parameters
	 * @return
	 * @throws MooshakEvaluationException
	 */
	private boolean evaluatedJavaSpecialCorrector(
			String evaluator,
			EvaluationParameters parameters) 
					throws MooshakEvaluationException {
		SpecialCorrector specialEvaluator;
		
		if((specialEvaluator = getJavaSpecialCorrector(evaluator)) == null)
			return false;
		else {	
			try {
				specialEvaluator.evaluate(parameters);
			} catch(MooshakEvaluationException cause) {
				throw cause;
			} catch(Exception cause) {
				throw new MooshakEvaluationException(
						"Unexpected exception while executing "+
						"java special corrector as compiler",
						cause,Classification.REQUIRES_REEVALUATION);
			}
			return true;
		}
	}
	
	
	/**
	 * Get a static corrector from a Java binary name. Only valid class
	 * binary names are actually looked using the current class loader.
	 * If no class is found then {@code null} is returned. Otherwise,
	 * as instance of static corrector is returned.
	 *  
	 * @param binaryName
	 * @return a static corrector, or {@code null} if none found
	 * @throws MooshakEvaluationException if class was found but could
	 * 				not be instantiated 
	 */
	private SpecialCorrector getJavaSpecialCorrector(String binaryName) 
					throws MooshakEvaluationException {
		
		return getInstanceOf(binaryName,SpecialCorrector.class);
	}
		
	/*
	private SpecialCorrector getJavaSpecialCorrector(String binaryName) 
		// 
		if(! BINARY_NAME.matcher(binaryName).matches())
			return null;
		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class<?> clazz =  loader.loadClass(binaryName);
		
			if(SpecialCorrector.class.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				Class<SpecialCorrector> staticCorrectorClass =
						(Class<SpecialCorrector>) clazz;	
				
				return staticCorrectorClass.newInstance();
			} else 
				throw new MooshakEvaluationException(
						"Not a SpecialCorrector: "+binaryName,
						Classification.REQUIRES_REEVALUATION);
							
		} catch(ClassNotFoundException cause) {
			return null;
		} catch (InstantiationException | IllegalAccessException cause) {
			throw new MooshakEvaluationException(
					"Cannot call empty constructor of "+binaryName,
					cause,Classification.REQUIRES_REEVALUATION);
		}
	}
	*/
	
	static final Pattern BINARY_NAME = Pattern.compile("\\w+(\\.\\w+)*");
	
	/**
	 * Get an instance from a Java binary name using an empty constructor.
	 * Only valid class binary names are actually looked using the current class loader.
	 * If no class is found then {@code null} is returned. Otherwise,
	 * as instance of that class is returned.
	 *  
	 * @param binaryName
	 * @return a instance of given class, or {@code null} if none found
	 * @throws MooshakEvaluationException if class was found but could not be instantiated 
	 */
	private <T> T getInstanceOf(String binaryName,Class<?> T) throws MooshakEvaluationException {
		
		if(! BINARY_NAME.matcher(binaryName).matches())
			return null;
		
		try {
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			Class<?> clazz =  loader.loadClass(binaryName);
		
			if(T.isAssignableFrom(clazz)) {
				@SuppressWarnings("unchecked")
				Class<T> staticCorrectorClass =
						(Class<T>) clazz;	
				
				return staticCorrectorClass.newInstance();
			} else 
				throw new MooshakEvaluationException(
						"Not a SpecialCorrector: "+binaryName,
						Classification.REQUIRES_REEVALUATION);
							
		} catch(ClassNotFoundException cause) {
			return null;
		} catch (InstantiationException | IllegalAccessException cause) {
			throw new MooshakEvaluationException(
					"Cannot call empty constructor of "+binaryName,
					cause,Classification.REQUIRES_REEVALUATION);
		}
	}
	
		
	private static final Runtime RUNTIME = Runtime.getRuntime();
	
	/**
	 * Invoke a command line with a special corrector and report
	 * with a MooshakEvaluationException when appropriate.
	 * Exit values from the special corrector are mapped into classifications
	 * when positive, and to symmetric of marks when negatives 
	 * (e.g. exit code -20 is mark=20)
	 * 
	 * @param parameters
	 * @param commandLine
	 * @param vars
	 * @throws MooshakEvaluationException
	 */
	void invokeCorretor(
			EvaluationParameters parameters,
			String commandLine,
			Map<String,String> vars) 	throws MooshakEvaluationException {
		
		try {
			SubmissionSecurity security = new SubmissionSecurity(parameters,EXECUTION);
			
			security.relax();
			
			Path workDirectory = parameters.getSubmission().getAbsoluteFile();
			Process process = null;
			OutputBuffer outputBuffer = null;
			OutputBuffer errorBuffer = null;
			int exitCode = 0;
			String[] env = makeEnvironment(vars);
			
			try {	
				// avoiding ProcessBuilder not to parse commandLine
				process = RUNTIME.exec(commandLine,env,workDirectory.toFile());
				
				errorBuffer = new OutputBuffer(parameters,process.getErrorStream());
				outputBuffer = new OutputBuffer(parameters,process.getInputStream());
				
				if (!process.waitFor(parameters.getTimeout(), TimeUnit.SECONDS)) 
					exitCode = Arrays.asList(Classification.values()).indexOf(Classification.TIME_LIMIT_EXCEEDED);
				else
					exitCode = process.exitValue();
				
				Thread.sleep(SLACK_TIME); // give time to read output
			} catch (Exception cause) {
				throw new MooshakEvaluationException(
						"Executing special corrector",cause,
						Classification.REQUIRES_REEVALUATION);
			} finally {
				if(process != null)
					process.destroyForcibly();
				if(outputBuffer != null) 
					outputBuffer.terminate();
				if(errorBuffer != null)
					errorBuffer.terminate();
			}		

			security.tighten();
			
			analizeOutcomeOfCorrector(exitCode,
					outputBuffer.toString(),
					errorBuffer.toString());
			
		} catch (MooshakSafeExecutionException cause) {
			String message = "Error while managing security for special corretor";
			throw new MooshakEvaluationException(message,cause,
					Classification.REQUIRES_REEVALUATION);
		}
	}
	
	private String[] makeEnvironment(Map<String, String> vars) {
		ProcessBuilder builder = new ProcessBuilder();
		Map<String,String> environment = builder.environment();
		
		for(String name: vars.keySet()) {
			String value = vars.get(name);
			if(name == null || value == null)
				continue;
			environment.put(name.toUpperCase(), vars.get(name));
		}
		
		int count = 0;
		String[] env = new String[environment.size()];
		for(String name: environment.keySet()) 
			env[count++] = name+"="+environment.get(name);

		return Arrays.copyOf(env, count);
	}

	/**
	 * Analyze the exit code of a safe execution from a special corrector
	 * and generate   MooshakEvaluationException when appropriate
	 * 
	 * @param safeExecution
	 * @throws MooshakEvaluationException
	 */
	void analizeOutcomeOfCorrector(int exitCode,String message,String feedback) 
			throws MooshakEvaluationException {

		Classification classification = Classification.ACCEPTED;
		int mark = 0;
		
		
		// no news is good news :-)
		if(exitCode == 0 ) 
			return;
			
		if(exitCode > 128) { // complement to negative number
			mark = 256 - exitCode;
		} else if(exitCode < Classification.values().length) 
			classification = Classification.values()[exitCode];
		else 
			throw new MooshakEvaluationException(
					"Invalid exit code of special corrector: "+exitCode,
					Classification.REQUIRES_REEVALUATION);
			
		throw new MooshakEvaluationException(message, null, 
				classification, feedback, mark);
	}
	

	/*
	 * Some messages reveal that programs should be reevaluated
	 */
	static final List<Pattern> reevalPatterns = new ArrayList<Pattern>(); 
	static {
		reevalPatterns.add(Pattern.compile("Resource temporarily unavailable"));
		reevalPatterns.add(Pattern.compile("virtual memory exhausted"));
	}
	
	/**
	 * Checks if this message reveals that this submission needs to be 
	 * reevaluated 
	 * @param output
	 * @return true if needs to be reevaluated, false otherwise 
	 */
	private boolean requiresReevaluation(String output)  {
		for(Pattern pattern: reevalPatterns)
			if(pattern.matcher(output).find())
				return true;
		return false;
	}
}
