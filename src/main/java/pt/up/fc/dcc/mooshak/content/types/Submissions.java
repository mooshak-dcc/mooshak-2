package pt.up.fc.dcc.mooshak.content.types;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import pt.up.fc.dcc.mooshak.client.utils.Base64Coder;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentContainer;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.types.Submission.Classification;
import pt.up.fc.dcc.mooshak.content.types.Submission.State;
import pt.up.fc.dcc.mooshak.evaluation.Replayer;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MethodContext;
import pt.up.fc.dcc.mooshak.shared.commands.TransactionQuota;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo.ColumnType;

public class Submissions extends PersistentContainer<Submission> 
	implements HasSubmissions, HasListingRows, Preparable, HasTransactions {
	
	private static final long serialVersionUID = 1L;
	
	private static final int HOURS_IN_MILLIS = 3600 * 1000;
	
	public enum FeedbackCategories {NONE, CLASSIFICATION, REPORT, ALL;
	
		public String toString() {
			return super.toString().toLowerCase();
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
			name="Default_state",
			type=AttributeType.MENU,
			tip = "Default submission state when received")
	private State defaultState;
	
	@MooshakAttribute( 
			name="Multiple_accepts",
			tip = "Allow resubmissions of already accepted problems",
			type= AttributeType.MENU)
	private MooshakAttribute.YesNo  multipleAccepts= null;
	
	@MooshakAttribute( 
			name="Accept_duplicates",
			tip = "Allow duplicate submissions",
			type= AttributeType.MENU)
	private MooshakAttribute.YesNo acceptDuplicates = null;
	
	@MooshakAttribute( 
			name="Replace_existing",
			tip = "Allow only one submission per student per problem,"
					+ " replacing if a new submission is received",
			type= AttributeType.MENU)
	private MooshakAttribute.YesNo replaceExisting = null;

	@MooshakAttribute( 
			name="Run_all_tests",
			tip = "Run all test even if program is consuming too much resources"
			    + "(ex: time, memory)",
			type= AttributeType.MENU)
	private MooshakAttribute.YesNo  runAllTests= null;
	
	@MooshakAttribute( 
			name="Show_own_code",
			tip = "Show the code to the team that submitted it",
			type= AttributeType.MENU)
	private MooshakAttribute.YesNo showOwnCode = null;

	
	@MooshakAttribute( 
			name="Give_feedback",
			tip = "Detail of feedback given to teams/students",
			help ="" 
+ "Use this field to control the amount of feedback given to teams/students,\n "
+ "and select:\n"
+ "\n" 
+ "\t\tnone            to give minimal feedback (Received)\n"
+ "\t\tclassification  to show classification and errors messages *\n"
+ "\t\treport          to show classification and feedback reports *\n"
+ "\t\tall                    all information available to judges\n"
+ "\n"
+ "	* Error messages and reports are associated "
+ "   with errors selected in Show errors",
			type= AttributeType.MENU)
	public FeedbackCategories giveFeedback = null;
	
	
	@MooshakAttribute( 
			name="Show_errors",
			tip = "Show error messages/reports for these error types",
			help = 
"When giving feedback use this selector to specify the type of errors" +
"that will receive feedback",
			type= AttributeType.LIST)
	private EnumSet<Submission.Classification> showErrors = null;

	@MooshakAttribute( 
			name="Feedback_delay",
			tip = "Minutes before showing a different feedback for the same error",
			help = "Time, in seconds, between two different incremental reports",
			type= AttributeType.INTEGER)
	private Integer feedbackDelay = null;
	
	@MooshakAttribute( 
			name="Minimum_interval",
			tip = "Number of seconds between submissions of same team",
			help = "Time, in seconds, between two different incremental reports",
			type= AttributeType.INTEGER)
	private Integer minimumInterval = null;
	
	@MooshakAttribute( 
			name="Maximum_pending",
			tip = "Maximum number of submissions waiting for validation"
			 + "(blocks new submissions)",
			type= AttributeType.INTEGER)
	private Integer maximumPending = null;
	
	@MooshakAttribute(
			name="TransactionLimit",
			type=AttributeType.INTEGER,
			tip="Maximum number of submissions to each user")
	private Integer transactionLimit;

	@MooshakAttribute(
			name="TransactionLimitTime",
			type=AttributeType.DOUBLE,
			tip="Time to reset the number of submissions of each\n"
					+ " user (hours ex.: 1.5)")
	private Double transactionLimitTime;
	
	@MooshakAttribute(
			name="NextTransactionReset",
			type=AttributeType.DATE,
			tip="Represents the date of the next transaction \n"
					+ "reset")
	private Date nextTransactionReset;
	
	@MooshakAttribute(
			name="Submission",
			type=AttributeType.CONTENT)
	private Void submission;	


	 /*-------------------------------------------------------------------*\
	  * 		            Operations                                    *
	 \*-------------------------------------------------------------------*/

	
	@MooshakOperation(
			name="Replay",
			inputable=false,
			tip="Replays subsmissions to this contest")
	private CommandOutcome replay() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Languages languages = open("../languages");
			
			for(Language language: languages.getContent())
				outcome.addPair("language",language.getIdName());
			
			Problems problems = open("../problems");
			for(Problem problem: problems.getContent())
				outcome.addPair("problem",problem.getIdName());
			
		} catch (MooshakContentException cause) {
			outcome.setMessage("Error:"+cause.getLocalizedMessage());
		}
		
		outcome.setContinuation("ReplayForm");
		return outcome;
	}
	
	
	@MooshakOperation(
			name="ReplayForm",
			inputable=true,
			show=false)
	private CommandOutcome replayForm(MethodContext context) {	
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			Replayer replayer = new Replayer(this);
			String delayText = context.getValue("maxDelay");
			int maxDelay = 0;
			
			try {
				maxDelay = Integer.parseInt(delayText);
			} catch(NumberFormatException e) {
				throw new MooshakException("Invalid delay:"+delayText,e);
			}
			
			replayer.setLanguages(context.getValues("language"));
			replayer.setProblems(context.getValues("problem"));
			replayer.setMaxDelay(maxDelay);
			replayer.setRecipient(context.getRecipient());
			
			outcome.addPair("replayer",replayer.getId());
			outcome.setContinuation("ReplayUpdates");
			
			replayer.begin();
			
		} catch (MooshakException e) {
			outcome.setMessage(e.getMessage());
		}
		
		return outcome;
	}
	
	@MooshakOperation(
			name="ReplayUpdates",
			inputable=true,
			show=false)
	private CommandOutcome replaUpdate(MethodContext context) {	
		CommandOutcome outcome = new CommandOutcome();
		
		Replayer replayer = Replayer.getReplayer(context.getValue("replayer"));
		
		if(replayer != null)
			replayer.end();
		
		return outcome;
	}
	
	@MooshakOperation(
			name="Export Classification",
			inputable=false,
			tip="Export classifications in tsv format")
	private CommandOutcome exportClassification() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			outcome.addPair("mimeType", "text/plain");
			
			String headerLine = "#";
			for (String header : columnNames)
				headerLine += "\t" + header;
			outcome.addPair("header", headerLine);
			
			int count = 1;
			for (Submission submission : getContent()) {
				String data = count + "";
				
				for (String header : columnNames) {
					String value = submission.getRow().get(header);
					if(value == null || value.equals(""))
						value = new String(Character.toChars(160));
					data += "\t" + value;
				}
				
				count++;
				outcome.addPair("data", Base64Coder.encodeString(data));
			}
		
			outcome.setContinuation("ExportClassificationResult");
			
		} catch (MooshakContentException cause) {
			outcome.setMessage("Error:"+cause.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	
	@MooshakOperation(
			name="ExportClassificationResult",
			inputable=true,
			show=false)
	private CommandOutcome exportClassificationResult(MethodContext context) {	
		return new CommandOutcome();
	}
	
	
	@MooshakOperation(
			name="Export Ranking",
			inputable=false,
			tip="Export ranking in tsv format")
	private CommandOutcome exportRanking() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			outcome.addPair("mimeType", "text/plain");

			Contest contest = getParent();
			List<String> headers = new ArrayList<>();
					
			for(ColumnInfo info: contest.getRankingPolicy().getColumns())
				headers.add(info.getName());

			String headerLine = "#";
			for (String header : headers)
				headerLine += "\t" + header;
			outcome.addPair("header", headerLine);
			
			int count = 1;
			
			try(POStream<? extends HasListingRow> stream = contest
					.getRankingPolicy().getRows()) {
				
				for(HasListingRow rower: stream) {
					Map<String, String> row = rower.getRow();
				
					String data = count + "";
				
					for (String header : headers) {
						data += "\t" + row.get(header);
					}
				
					count++;
					outcome.addPair("data", Base64Coder.encodeString(data));
				}
			} catch (Exception cause) {
				outcome.setMessage("Error iterating over rows:"+
						cause.getLocalizedMessage());
			}
			
			outcome.setContinuation("ExportRankingResult");
			
		} catch (MooshakException cause) {
			outcome.setMessage("Error:"+cause.getLocalizedMessage());
		}
		
		return outcome;
	}
	
	
	@MooshakOperation(
			name="ExportRankingResult",
			inputable=true,
			show=false)
	private CommandOutcome exportRankingResult(MethodContext context) {	
		return new CommandOutcome();
	}
	
	
	 /*-------------------------------------------------------------------*\
	  * 		            Setters and getters                           *
	 \*-------------------------------------------------------------------*/

	
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
	 * The default state assigned to submissions
	 * @return
	 */
	public State getDefaultState() {
		
		if(defaultState == null)
			return State.PENDING;
		else
			return defaultState;
	}

	/**
	 * Set the default state to assign to submissions
	 * @param defaultState
	 */
	public void setDefaultState(State defaultState) {
		this.defaultState = defaultState;
	}
	
	/**
	 * Run all tests, even if a serious error (e.g. RTL) is encountered
	 * @return true if running all test, false otherwise
	 */
	public boolean isRunAllTests() {
		if(runAllTests == null)
			return true;
		else if(runAllTests.equals(YesNo.YES))
			return true;
		else
			return false;
	}
	
	
	public void setRunAllTests(boolean runAllTests) {
		if(runAllTests)
			this.runAllTests = YesNo.YES;
		else
			this.runAllTests = YesNo.NO;
	}
	
	
	/**
	 * Can a team/student submit to an already accepted problem?
	 * True only if explicitly stated; false otherwise 
	 * @return
	 */
	public boolean isMultipleAccepts() {
		
		if(YesNo.YES.equals(multipleAccepts))
			return true;
		else
			return false;		
	}
	
	/**
	 * Set if a team/student can submit to an already accepted problem
	 * @return
	 */
	public void setMultipleAccepts(boolean multiple) {
		if(multiple)
			this.multipleAccepts = YesNo.YES;
		else
			this.multipleAccepts = YesNo.NO;
			
	}
	
	
	/**
	 * Can a team/student submit multiple times the same code to the same problem?
	 * True only if explicitly stated; false otherwise 
	 * @return
	 */
	public boolean isAcceptDuplicates() {
		
		if(YesNo.YES.equals(acceptDuplicates))
			return true;
		else
			return false;		
	}
	
	/**
	 * Set if a team/student can submit multiple times the same code to the same problem
	 * @return
	 */
	public void setAcceptDuplicates(boolean multiple) {
		if(multiple)
			this.acceptDuplicates = YesNo.YES;
		else
			this.acceptDuplicates = YesNo.NO;
			
	}
	
	
	/**
	 * Allow only one submission per student per problem, replacing if a 
	 * new submission is received
	 * 
	 * True only if explicitly stated; false otherwise 
	 * @return
	 */
	public boolean isReplaceExisting() {
		
		if(YesNo.YES.equals(replaceExisting))
			return true;
		else
			return false;		
	}
	
	/**
	 * Set if allow only one submission per student per problem,
	 * replacing if a new submission is received
	 * 
	 * @return
	 */
	public void setReplaceExisting(boolean replace) {
		if(replace)
			this.replaceExisting = YesNo.YES;
		else
			this.replaceExisting = YesNo.NO;
			
	}
	
	
	/**
	 * Can a team/student submit to an already accepted problem?
	 * False (No) only if explicitly stated; true (Yes) otherwise 
	 * @return
	 */
	public boolean isShowOwnCode() {
		
		if(YesNo.NO.equals(showOwnCode))
			return false;
		else
			return true;		
	}
	
	/**
	 * Set if a team/student can submit to an already accepted problem
	 * @return
	 */
	public void setShowOwnCode(boolean showOwnCode) {
		if(showOwnCode)
			this.showOwnCode = YesNo.YES;
		else
			this.showOwnCode = YesNo.NO;
			
	}
	
	/**
	 * Get set of classifications for which errors show be reported
	 * @return
	 */
	public EnumSet<Classification> getShowErrors() {
		if(showErrors == null)
			return EnumSet.noneOf(Classification.class);
		else
			return showErrors;
	}

	/**
	 * Set of classifications for which errors show be reported
	 * @return
	 */
	public void setShowErrors(EnumSet<Classification> showErrors) {
		this.showErrors = showErrors;
	}
	
	/**
	 * Kind of feedback to the team/student. Default is Report
	 * @return
	 */
	public FeedbackCategories getGiveFeedback() {
		if(giveFeedback == null)
			return FeedbackCategories.REPORT;
		else 
			return giveFeedback;
	}
	
	/**
	 * Set kind of feedback to the team/student as a {@code FeedbackCategory}
	 * @return
	 */
	public void setGiveFeedback(FeedbackCategories giveFeedback) {
		this.giveFeedback = giveFeedback;
	}
	
	/**
	 * Get current delay between incremental feedback messages 
	 * @param feedbackDelay
	 */	
	public int getFeedbackDelay() {
		
		if(feedbackDelay == null)
			return 0;
		else
			return feedbackDelay;
	}
	
	/**
	 * Set delay between incremental feedback messages 
	 * @param feedbackDelay
	 */
	public void setFeedbackDelay(int feedbackDelay) {
		this.feedbackDelay = feedbackDelay;
	}
	
	/**
	 * Get allowed minimum interval (in seconds) between submissions 
	 * of the same team
	 * @return seconds between submissions
	 */
	public int getMinimumInterval() {
		if(minimumInterval == null) 
			return Integer.MAX_VALUE;
		else
			return minimumInterval;
	}
	
	/**
	 * Set a minimum interval (in seconds) between submissions of the same team
	 * @param minimumInterval seconds between submissions
	 */
	public void setMinimumInterval(int minimumInterval) {
		this.minimumInterval =  minimumInterval;
	}
	
	/**
	 * Current maximum number of submissions waiting for validation 
	 * (blocks new submissions)
	 * @return
	 */
	public int getMaximumPending() {
		if(maximumPending == null)
			return Integer.MAX_VALUE;
		else
			return maximumPending;
			
	}
	
	/**
	 * Sets maximum number of submissions waiting for validation 
	 * (if this number is exceed them submissions are blocked)
	 * @param maximumPending
	 */
	public void setMaximumPending(int maximumPending) {
		this.maximumPending =  maximumPending;
	}
	
	/**
	 * @return the transactionLimit
	 */
	public Integer getTransactionLimit() {
		return transactionLimit;
	}

	/**
	 * @param transactionLimit the transactionLimit to set
	 */
	public void setTransactionLimit(Integer transactionLimit) {
		this.transactionLimit = transactionLimit;
	}

	/**
	 * @return the transactionLimitTime
	 */
	public Double getTransactionLimitTime() {
		return transactionLimitTime;
	}

	/**
	 * @param transactionLimitTime the transactionLimitTime to set
	 */
	public void setTransactionLimitTime(Double transactionLimitTime) {
		this.transactionLimitTime = transactionLimitTime;
	}

	/*---------------------------------------------------------------------*\
	 *                   Listing support                                   *
	 *                                                                     *
	 *                                                                     *
	 * (non-Javadoc)                                                       *
	 * @see pt.up.fc.dcc.mooshak.content.types.HasListingRows#getRows()    *
	 * @see pt.up.fc.dcc.mooshak.content.types.HasListingRows#getColumns() *
	\*---------------------------------------------------------------------*/
	
	@Override
	public POStream<? extends HasListingRow> getRows() {
		
		return  newPOStream();
	}
	
	List<String> columnNames = Arrays.asList(
			"team","flag", "group","problem","mark","classification","state");
	
	@Override
	public List<ColumnInfo> getColumns() {
		
		List<ColumnInfo> columns = new ArrayList<>();
		
		columns.add(new ColumnInfo("time", 17, ColumnType.TIME));
		columns.add(new ColumnInfo("id", 12));
		
		for (String name : columnNames) {
			if(name.equalsIgnoreCase("classification"))
				columns.add(new ColumnInfo(name, 12, ColumnType.CLASSIFICATION));
			else if(name.equalsIgnoreCase("flag"))
				columns.add(new ColumnInfo(name, 6, ColumnType.FLAG));
			else if(name.equalsIgnoreCase("team"))
				columns.add(new ColumnInfo(name, 12, ColumnType.TEAM));
			else if(name.equalsIgnoreCase("problem"))
				columns.add(new ColumnInfo(name, 12, ColumnType.PROBLEM));
			else
				columns.add(new ColumnInfo(name, 12));
		}
		
		return columns;
	}

	@Override
	public void prepare() throws MooshakException {
		try(POStream<Submission> stream = newPOStream()) {
			for(Submission submission: stream)
				submission.delete();
		} catch (Exception cause) {
			throw new MooshakContentException("Error iterating over submissions"
					,cause);
		}
	}	

	@Override
	public TransactionQuota getTransactionQuota(UseTransactions user)
			throws MooshakException {
		
		TransactionQuota quota = new TransactionQuota();
		
		Double transactionLimitTime = this.transactionLimitTime; 
		
		if(transactionLimit == null) {
			Contest contest = getParent();
			
			if(contest.getTransactionLimit() == null)
				return null;
			
			if(transactionLimitTime == null)
				transactionLimitTime = contest.getTransactionLimitTime();
			
			quota.setTransactionsLimit(contest.getTransactionLimit());
		}
		else {
			quota.setTransactionsLimit(getTransactionLimit());
		}
		
		quota.setTransactionsUsed(user.getTransactions("Submissions"));
		
		if(transactionLimitTime == null)
			return quota;
		
		try {
			Date now = new Date();
			
			synchronized (this) {
				if(nextTransactionReset == null)
					nextTransactionReset = new Date(0);
				
				long time = nextTransactionReset.getTime() - now.getTime();
				if(time < 0) {
					resetAllTransactions();
					quota.setTransactionsUsed(0);
					
					long r = (long) (time % (transactionLimitTime 
							* HOURS_IN_MILLIS));
					
					Date nextReset = new Date((long) (now.getTime() + 
							(transactionLimitTime *	HOURS_IN_MILLIS + r)));
					
					nextTransactionReset = nextReset;
					time = nextReset.getTime() - now.getTime();
					save();
				}
			}
			
			
			quota.setTimeToReset(nextTransactionReset.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return quota;
	}

	@Override
	public void resetTransactions(UseTransactions user)
			throws MooshakException {
		user.resetTransactions("Submissions");
	}

	@Override
	public void resetAllTransactions() throws MooshakException {
		Groups groups = getParent().open("groups");
		
		try(POStream<Team> stream = groups.newPOStream()) {
			for (Team team : stream) 
				resetTransactions(team);
		} catch (Exception cause) {
			throw new MooshakContentException("Error iterating over teams",
					cause);
		}
	}

	@Override
	public void makeTransaction(UseTransactions user) throws MooshakException {
		
		TransactionQuota quota = getTransactionQuota(user);
		
		if(quota == null)
			return;
		
		if(quota.getTransactionsUsed() < quota.getTransactionsLimit()) {
			user.makeTransaction("submissions");
			return;
		}
		
		throw new MooshakException("Transaction limit exceeded for this team");
			
	}
	
	/**
	 * Check if a submission from this team with this problem id is acceptable.
	 * It may not be acceptable if, for this problem and team:
	 *   1) there was a submission less than minimumInterval seconds ago
     *   2) the submitted file is exactly the same as a previous submission
     *   3) a previous submission has been accepted as solution 
     *      and the multipleAccepts field has not been set
     *   4) there are more than maximumPending submissions waiting to be
     *      validated
     * 
	 * @param teamId {@link String} Team that submitted the attempt.
	 * @param problemId {@link String} Problem to which the attempt refers.
	 * @param code {@code byte[]} Code of the solution.
	 */
	public void acceptable(String teamId, String problemId, byte[] code) 
			throws MooshakException {
		
		long now = new Date().getTime();
		int pending = 0;
		
		String glob = "*_" + problemId + "_" + teamId/* + "*" */; // removed to prevent checking AB team's solutions for A submissions
		
		try (DirectoryStream<Path> stream = 
				Files.newDirectoryStream(getAbsoluteFile(), glob)) {
			
			for(Path submissionPath : stream) {
				Path submissionFileName = submissionPath.getFileName();
				
				if(submissionFileName == null)
					continue;
				
				String submissionName = submissionFileName.toString();
				Submission submission = open(submissionName); 
				
				// 1) there was a submission less than minimumInterval seconds ago
				Date date = submission.getDate();
				if(minimumInterval != null && date != null && 
						(now - date.getTime()  < minimumInterval * 1000)) {
					throw new MooshakException("Too frequent submissions");
				}

				// 2) the submitted file is exactly the same as a previous one
				if (!isAcceptDuplicates() && code.length == submission.getSize()) {
					String programName = submission.getProgramName();
					Path programPath = submission.getAbsoluteFile(programName);
					byte[] data = Files.readAllBytes(programPath);

					if (Arrays.equals(data, code)) {
						throw new MooshakException("Duplicate submission");
					}
				}

				// 3) a previous submission has been accepted as solution
				if (submission.getClassify() == Classification.ACCEPTED &&
						multipleAccepts != null &&
						multipleAccepts != YesNo.YES) {
					
					Problem problem = open("../problems").openOrInherit(problemId);
					String message = String.format(
							"Problem %s - %s -already accepted", 
							problem.getId().toString(),
							problem.getTitle()); 
					throw new MooshakException(message);
				}
						
				// 4) more than maximumPending submissions waiting to be validated 
				if(	maximumPending != null 						&& 
						submission.getState() == State.PENDING 	&& 				
					    ++pending > maximumPending)
					
						throw new MooshakException("Too many pending submissions");
			}
		} catch (IOException cause) {
			throw new MooshakException("I/O error listing "+getPath(),cause);
		}
	}	
	
	/**
	 * Returns the submissions made to a problem
	 * @param problemId
	 * @return the submissions made to a problem
	 * @throws MooshakException 
	 */
	public List<Submission> getSubmissionsByProblem(String problemId) 
			throws MooshakException {
		
		List<Submission> problemSubmissions = new ArrayList<Submission>();
		
		try(POStream<Submission> stream = newPOStream()) {
			for (Submission submission : stream) {
				if (!submission.isConsider())
					continue;
				
				if (submission.getProblemId() != null && 
						submission.getProblemId().equals(problemId))
					problemSubmissions.add(submission);

			}
		} catch (Exception cause) {
			throw new MooshakContentException("Error iterating over submissions"
					,cause);
		}
		
		return problemSubmissions;
	}
	
	/**
	 * Returns the submissions made by a participant
	 * @param participant
	 * @return the submissions made to a problem
	 * @throws MooshakException 
	 */
	public List<Submission> getSubmissionsByParticipant(String participant) 
			throws MooshakException {
		
		List<Submission> participantSubmissions = new ArrayList<Submission>();
		
		try(POStream<Submission> stream = newPOStream()) {
		for (Submission submission : stream) {
			if (!submission.isConsider())
				continue;
			
			if (submission.getTeamId() != null && 
					submission.getTeamId().equals(participant))
				participantSubmissions.add(submission);
				
		}
		} catch (Exception cause) {
			throw new MooshakContentException("Error iterating over submissions"
					,cause);		
		}
		
		return participantSubmissions;
	}
	
	/**
	 * Returns the most recently accepted of each participant 
	 * 
	 * @param problemId {@link String} ID of the problem
	 * @return {@link Map<String,Submission>} the most recently accepted submission 
	 * 		of each participant indexed by participant ID
	 * @throws MooshakException 
	 */
	public Map<String, Submission> getMostRecentlyAcceptedOfEachParticipant(String problemId) 
			throws MooshakException {
		
		List<Submission> problemSubmissions = getSubmissionsByProblem(problemId);
		
		Map<String, Submission> playersSubmissions = problemSubmissions.stream()
				.filter(s -> 
						s.getDate() != null
						&& s.getTeamId() != null
						&& s.getClassify().equals(Classification.ACCEPTED))
				.collect(Collectors.groupingBy(Submission::getTeamId, 
						Collectors.collectingAndThen(
								Collectors.maxBy(Comparator.comparing(Submission::getDate)), 
								s -> s.orElse(null)
							)));
		
		return playersSubmissions;
	}
	
	/**
	 * Returns the submissions of a participant to a problem
	 * 
	 * @param problemId {@link String} ID of the problem
	 * @param participantId {@link String} ID of the participant
	 * @return {@link List} submissions of a participant to a problem
	 * @throws MooshakException 
	 */
	public List<Submission> getSubmissionsOfParticipantToProblem(String problemId,
			String participantId) throws MooshakException {
		
		List<Submission> problemSubmissions = getSubmissionsByProblem(problemId);
		
		List<Submission> submissions = problemSubmissions.stream()
			.filter(s -> 
					s.getTeamId() != null 
					&& s.getTeamId().equals(participantId))
			.collect(Collectors.toList());
		
		return submissions;
	}
	
	/**
	 * Returns the most recently accepted of a participant 
	 * 
	 * @param problemId {@link String} ID of the problem
	 * @param participantId {@link String} ID of the participant
	 * @return {@link Submission} the most recently accepted submission 
	 * 		of the participant
	 * @throws MooshakException 
	 */
	public Submission getMostRecentlyAcceptedOfParticipant(String problemId,
			String participantId) throws MooshakException {
		
		List<Submission> problemSubmissions = getSubmissionsByProblem(problemId);
		
		Optional<Submission> submission = problemSubmissions.stream()
			.filter(s -> 
					s.getDate() != null
					&& s.getTeamId() != null 
					&& s.getTeamId().equals(participantId) 
					&& s.getClassify().equals(Classification.ACCEPTED))
			.sorted(Comparator.comparing(Submission::getDate).reversed())
			.findFirst();
		
		return submission.orElse(null);
	}


	/**
	 * Cleanup compilations
	 * @throws MooshakException 
	 */
	public void cleanup() throws MooshakException {
		
		for (PersistentObject po : getChildren(false)) {
			((Submission) po).cleanup();
		}
	}
	
}



