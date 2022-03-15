package pt.up.fc.dcc.mooshak.content.types;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakAttribute.YesNo;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentCore;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.erl.ReportType;
import pt.up.fc.dcc.mooshak.content.types.Contest.Gui;
import pt.up.fc.dcc.mooshak.content.util.Bytes;
import pt.up.fc.dcc.mooshak.content.util.Dates;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.content.util.Serialize;
import pt.up.fc.dcc.mooshak.content.util.Strings;
import pt.up.fc.dcc.mooshak.evaluation.Analyzer;
import pt.up.fc.dcc.mooshak.evaluation.GameAnalyzer;
import pt.up.fc.dcc.mooshak.evaluation.ProgramAnalyzer;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.events.AlertNotificationEvent;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

/**
 * Submission of a source program to solve a problem (run). An instance of this
 * class is created for each submission. This class includes methods for 
 * evaluation and re-evaluation of submissions.  Instances of this class 
 * are persisted locally.
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * Recoded in Java in         June  2012
 * From a Tcl module coded in April 2001
 */
public class Submission extends PersistentObject implements HasListingRow {
	private static final long serialVersionUID = 1L;
	
	public static final Path REPORT_FILE_NAME = Paths.get("Report.xml");
	
	private static final Set<PosixFilePermission> userReadPerms = 
			new HashSet<PosixFilePermission> ();

	private static final String FEEDBACK_FILE_NAME = "feedback.txt";
	
	static {
		userReadPerms.add(PosixFilePermission.OWNER_READ);
	}

	
	public enum  Classification { 
			ACCEPTED,		      
			PRESENTATION_ERROR,		
			WRONG_ANSWER,
			EVALUATION_SKIPPED,
			OUTPUT_LIMIT_EXCEEDED,
			MEMORY_LIMIT_EXCEEDED,
			TIME_LIMIT_EXCEEDED,
			INVALID_FUNCTION,
			INVALID_EXIT_VALUE,
			RUNTIME_ERROR,
			COMPILE_TIME_ERROR,
			INVALID_SUBMISSION,
			PROGRAM_SIZE_EXCEEDED,
			REQUIRES_REEVALUATION,
			EVALUATING;
			
			public String toString() {
				return Strings.toTitle(super.toString().replace("_", " "));
			}
			
			public String getAcronym() {
				String c = super.toString().replace("_", " ");
				return Strings.acronymOf(c);
			}
		};	
		
	public enum State { PENDING, FINAL;
	
		public String toString() {
			return super.toString().toLowerCase();
		}
	}
	
	@MooshakAttribute(
			name="Consider",
			type=AttributeType.MENU,
		    tip="Consider this submission for ranking/classification?")
	private MooshakAttribute.YesNo consider = null;

	@MooshakAttribute(
			name="Date",
			type=AttributeType.DATE)
	private Date date = null;

	@MooshakAttribute(
			name="EvaluatedAt",
			type=AttributeType.DATE)
	private Date evaluatedAt = null;
	
	//?? 
	@MooshakAttribute(
			name="Time",
			type=AttributeType.DATE)
	private Date time = null;
	
	@MooshakAttribute(
			name="Problem",	
			type=AttributeType.PATH,
			refType = "Problem",
			complement="../../problems")
	private Path problem = null;
	
	@MooshakAttribute(
			name="Team",
			type=AttributeType.PATH,
			refType = "Team",
			complement="../../groups/*")
	private Path team = null;
	
	@MooshakAttribute(
			name="Classify",
			type=AttributeType.MENU)
	private Classification classify = null;
	
	@MooshakAttribute(
			name="Mark",
			type=AttributeType.INTEGER)
	private Integer mark =null;
	
	@MooshakAttribute(
			name="Size",
			type=AttributeType.LONG)
	private Long size = null;
	
	@MooshakAttribute(
			name="Lines",
			type=AttributeType.LONG)
	private Integer lines = null;

	@MooshakAttribute(
			name="Observations")
	private String observations = null;

	@Deprecated
	@MooshakAttribute(
			name="Execution",
			tip="Avoid using this attribute")
	private String execution = null;
	
	@MooshakAttribute(
			name="State", 
			type=AttributeType.MENU)
	private State state = null;
	
	@MooshakAttribute(
			name="Language",
			type=AttributeType.PATH,
			refType = "Language",
			complement="../../languages")
	private Path language = null;
	
	@MooshakAttribute(
			name="Program",
			type=AttributeType.FILE)
	private Path program = null;
	
	@MooshakAttribute(
			name="UserTestData",
			type=AttributeType.DATA) 
	private UserTestData userTestData;	
	
	@MooshakAttribute(
			name="Report",
			type=AttributeType.FILE,
			complement=".html")
	private Path reportPath = null;

	@MooshakAttribute(
			name="reports",
			type=AttributeType.DATA)
	private Reports reports = null;

	@MooshakAttribute(
			name="Elapsed",
			type=AttributeType.FLOAT)
	private Double elapsed = null;
	
	@MooshakAttribute(
			name="CPU",
			type=AttributeType.FLOAT)
	private Double cpu = null;

	@MooshakAttribute(
			name="Memory",
			type=AttributeType.FLOAT)
	private Double memory=null;
	
	@MooshakAttribute(
			name="Signals",
			type=AttributeType.INTEGER)
	private Integer signals = null;

	@MooshakAttribute(
			name="Feedback",
			type=AttributeType.LONG_TEXT)
	private String feedback = null;
	
	@MooshakAttribute(
			name="ReviewerObservations",
			type=AttributeType.LONG_TEXT)
	private String reviewerPrivateObservations = null;
	
	@MooshakAttribute(
			name="ReviewerFeedback",
			type=AttributeType.LONG_TEXT)
	private String reviewerFeedback = null;
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((analyzer == null) ? 0 : analyzer.hashCode());
		result = prime * result
				+ ((classify == null) ? 0 : classify.hashCode());
		result = prime * result
				+ ((consider == null) ? 0 : consider.hashCode());
		result = prime * result + ((cpu == null) ? 0 : cpu.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((evaluatedAt == null) ? 0 : evaluatedAt.hashCode());
		result = prime * result + ((elapsed == null) ? 0 : elapsed.hashCode());
		result = prime * result
				+ ((execution == null) ? 0 : execution.hashCode());
		result = prime * result
				+ ((feedback == null) ? 0 : feedback.hashCode());
		result = prime * result
				+ ((language == null) ? 0 : language.hashCode());
		result = prime * result + ((mark == null) ? 0 : mark.hashCode());
		result = prime * result + ((memory == null) ? 0 : memory.hashCode());
		result = prime * result
				+ ((observations == null) ? 0 : observations.hashCode());
		result = prime * result + ((problem == null) ? 0 : problem.hashCode());
		result = prime * result
				+ ((problemId == null) ? 0 : problemId.hashCode());
		result = prime * result + ((program == null) ? 0 : program.hashCode());
		result = prime * result
				+ ((reportPath == null) ? 0 : reportPath.hashCode());
		result = prime * result + ((signals == null) ? 0 : signals.hashCode());
		result = prime * result + ((size == null) ? 0 : size.hashCode());
		result = prime * result + ((lines == null) ? 0 : lines.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		result = prime * result
				+ ((teamId == null) ? 0 : teamId.hashCode());
		result = prime * result + ((time == null) ? 0 : time.hashCode());
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		Submission other = (Submission) obj;
		if (analyzer == null) {
			if (other.analyzer != null)
				return false;
		} else if (!analyzer.equals(other.analyzer))
			return false;
		if (classify != other.classify)
			return false;
		if (consider != other.consider)
			return false;
		if (cpu == null) {
			if (other.cpu != null)
				return false;
		} else if (!cpu.equals(other.cpu))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (evaluatedAt == null) {
			if (other.evaluatedAt != null)
				return false;
		} else if (!evaluatedAt.equals(other.evaluatedAt))
			return false;
		if (elapsed == null) {
			if (other.elapsed != null)
				return false;
		} else if (!elapsed.equals(other.elapsed))
			return false;
		if (execution == null) {
			if (other.execution != null)
				return false;
		} else if (!execution.equals(other.execution))
			return false;
		if (feedback == null) {
			if (other.feedback != null)
				return false;
		} else if (!feedback.equals(other.feedback))
			return false;
		if (language == null) {
			if (other.language != null)
				return false;
		} else if (!language.equals(other.language))
			return false;
		if (mark == null) {
			if (other.mark != null)
				return false;
		} else if (!mark.equals(other.mark))
			return false;
		if (memory == null) {
			if (other.memory != null)
				return false;
		} else if (!memory.equals(other.memory))
			return false;
		if (observations == null) {
			if (other.observations != null)
				return false;
		} else if (!observations.equals(other.observations))
			return false;
		if (problem == null) {
			if (other.problem != null)
				return false;
		} else if (!problem.equals(other.problem))
			return false;
		if (problemId == null) {
			if (other.problemId != null)
				return false;
		} else if (!problemId.equals(other.problemId))
			return false;
		if (program == null) {
			if (other.program != null)
				return false;
		} else if (!program.equals(other.program))
			return false;
		if (reportPath == null) {
			if (other.reportPath != null)
				return false;
		} else if (!reportPath.equals(other.reportPath))
			return false;
		if (signals == null) {
			if (other.signals != null)
				return false;
		} else if (!signals.equals(other.signals))
			return false;
		if (size == null) {
			if (other.size != null)
				return false;
		} else if (!size.equals(other.size))
			return false;
		if (lines == null) {
			if (other.lines != null)
				return false;
		} else if (!lines.equals(other.lines))
			return false;
		if (state != other.state)
			return false;
		if (team == null) {
			if (other.team != null)
				return false;
		} else if (!team.equals(other.team))
			return false;
		if (teamId == null) {
			if (other.teamId != null)
				return false;
		} else if (!teamId.equals(other.teamId))
			return false;
		if (time == null) {
			if (other.time != null)
				return false;
		} else if (!time.equals(other.time))
			return false;
		return true;
	}

	/*-------------------------------------------------------------------*\
	 * Custom serialization due to sun.nio.fs.UnixPath					 *
	\*-------------------------------------------------------------------*/
	
	
	/**
	 * Marshal PO state (Path path) which is not a serializable type.
	 *  
	 * @param out
	 * @throws IOException
	 */
	 private void writeObject(ObjectOutputStream out)
		     throws IOException {
		
		Serialize.writeEnum(consider,out);
		
		Serialize.writeDate(date, out);
		Serialize.writeDate(evaluatedAt, out);
		Serialize.writeDate(time, out);
							
		Serialize.writePath(problem, out);
		Serialize.writePath(team, out);
		
		Serialize.writeEnum(classify, out);
		
		Serialize.writeInt(mark, out);
		Serialize.writeLong(size, out);
		Serialize.writeInt(lines, out);
		
		Serialize.writeString(observations,out);
		Serialize.writeString(execution,out);
		
		Serialize.writeEnum(state,out);
		
		Serialize.writePath(language, out);
		Serialize.writePath(program, out);
		Serialize.writePath(reportPath, out);
		
		Serialize.writeDouble(elapsed, out);
		Serialize.writeDouble(cpu, out);
		Serialize.writeDouble(memory, out);
		
		Serialize.writeInt(signals, out);
		
		Serialize.writeString(feedback, out);

		Serialize.writeString(reviewerPrivateObservations, out);
		Serialize.writeString(reviewerFeedback, out);
	}	
	 
	 
	 /**
	  * Unmarshal PO state (Path path) which is not a serializable type.
	  * @param in
	  * @throws IOException
	  * @throws ClassNotFoundException
	  */
	 private void readObject(ObjectInputStream in)
		     throws IOException, ClassNotFoundException {
	
		 	
		 	consider = Serialize.readEnum(YesNo.class, in);
		 
		 	date = Serialize.readDate(in);
		 	evaluatedAt = Serialize.readDate(in);
		 	time = Serialize.readDate(in);
		 	
		 	problem = Serialize.readPath(in);
		 	team = Serialize.readPath(in);
		 	
		 	classify = Serialize.readEnum(Classification.class,in);
			
			mark = Serialize.readInt(in);
			size = Serialize.readLong(in);
			lines = Serialize.readInt(in);
			
			observations = Serialize.readString(in);
			execution = Serialize.readString(in);
			
			state = Serialize.readEnum(State.class,in);
			
			language =  Serialize.readPath(in);
			program =  Serialize.readPath(in);
			reportPath =  Serialize.readPath(in);
			
			elapsed = Serialize.readDouble(in);
			cpu = Serialize.readDouble(in);
			memory = Serialize.readDouble(in);
			
			signals = Serialize.readInt(in);
			feedback = Serialize.readString(in);
	
	 }
	 
	private String lastSentReviewerFeedback = "";
	 
	@Override
	protected boolean updated() throws MooshakContentException {
		
		if (reviewerFeedback != null && !reviewerFeedback.trim().isEmpty()
				&& !reviewerFeedback.equalsIgnoreCase(lastSentReviewerFeedback)) {
			sendReviewerFeedbackNotification();
			lastSentReviewerFeedback = reviewerFeedback;
		}
		
		return super.updated();
	}
	
	 /*-------------------------------------------------------------------*\
	  * 		            Setters and getters                           *
	 \*-------------------------------------------------------------------*/
	
	/**
	 * Is this submission in a formative environment?
	 * True only if explicitly stated; false otherwise 
	 * @return
	 */
	public boolean isFormative() {
		
		if(YesNo.NO.equals(formative))
			return false;
		else
			return true;		
	}
	
	/**
	 * Set if this submission is in a formative environment
	 * @return
	 */
	public void setFormative(boolean formative) {
		if(formative)
			this.formative = YesNo.YES;
		else
			this.formative = YesNo.NO;
			
	}
	
	/**
	 * Must this submission be considered for grading/ranking?
	 * False only if explicitly stated; true otherwise 
	 * @return
	 */
	public boolean isConsider() {
		
		if(YesNo.NO.equals(consider))
			return false;
		else
			return true;		
	}
	
	/**
	 * Set if this submission must considered for grading/ranking
	 * @return
	 */
	public void setConsider(boolean consider) {
		if(consider)
			this.consider = YesNo.YES;
		else
			this.consider = YesNo.NO;
			
	}

	
	/**
	 * Moment in which submission was created
	 * @return
	 */
	public Date getDate() {
		if(date == null)
			return new Date(0);
		else
			return date;
	}
	
	/**
	 * Change moment which submission was created
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}
	
	/**
	 * Moment in which submission was evaluated
	 * @return
	 */
	public Date getEvaluatedAt() {
		if(evaluatedAt == null)
			return new Date(0);
		else
			return evaluatedAt;
	}
	
	/**
	 * Change moment which submission was evaluated
	 * @param evaluatedAt
	 */
	public void setEvaluatedAt(Date evaluatedAt) {
		this.evaluatedAt = evaluatedAt;
	}
	
	/**
	 * Get time since beginning of contest
	 * @return
	 */
	public Date getTime() {
		if(time == null)
			return new Date(0);
		else
			return time;
	}
	
	/**
	 * Change time since beginning of contest
	 * @param time
	 */
	public void setTime(Date time) {
		this.time = time;
	}
	
	/**
	 * Get the team that produced this submission
	 * @throws MooshakContentException 
	 */
	public Authenticable getTeam() throws MooshakContentException {
		String teamId;
		if(team == null) {
			if (this.teamId == null)
				return null;
			else
				teamId = this.teamId;
		} else
			teamId = team.toString();
		
		Contest contest = getContest();
		Groups groups = contest.open("groups");
		
		Authenticable authenticable;
		if ((authenticable = groups.find(teamId)) != null)
			return authenticable;
		
		Users users = contest.open("users");
		if ((authenticable = users.find(teamId)) != null)
			return authenticable;
		
		Configs configs = PersistentObject.openPath("data/configs");
		users = configs.open("users");
		if ((authenticable = users.find(teamId)) != null)
			return authenticable;
		
		return null;
		
	}
	
	/**
	 * Change this submission's team (a file pointer) from the team object
	 * @param team
	 */
	public void setTeam(Authenticable team) {
		if(team == null)
			this.team = null;
		else {
			this.team = ((PersistentObject) team).getId();
			this.teamId = ((PersistentObject) team).getIdName();
		}
	}
	
	/**
	 * Gets the problem this submission attempted to solve
	 * @return problem
	 * @throws MooshakContentException
	 */
	public Problem getProblem() throws MooshakContentException {
		if(problem == null)
			return null;
		else 
			return openRelative("Problem", Problem.class);
	}
	
	/**
	 * Change the problem this submission attempts to solve
	 * @param team
	 */
	public void setProblem(Problem problem) {
		if(problem == null)
			this.problem = null;
		else
			this.problem = problem.getId();
	}
	
	/**
	 * Get current classification
	 * @return
	 */
	public Classification getClassify() {
		if(classify == null)
			return Classification.REQUIRES_REEVALUATION;
		else
			return classify;
	}
	
	/**
	 * Change current classification
	 * @param classify
	 */
	public void setClassify(Classification classify) {
		this.classify = classify;
	}
	
	/**
	 * Get current mark
	 * @return
	 */
	public int getMark() {
		if(mark== null)
			return 0;
		else
			return mark;
	}
	
	/**
	 * Set current mark
	 * @param mark
	 */
	public void setMark(int mark) {
		this.mark = mark;
	}
	
	/**
	 * Get program size in bytes
	 * @return
	 */
	public long getSize() {
		if(size == null)
			return 0;
		else
			return size;
	}
	
	/**
	 * Change program size in bytes
	 * @param size
	 */
	public void setSize(long size) {
		this.size = size;
	}
	
	/**
	 * Get lines of code
	 * @return
	 */
	public int getLines() {
		if(lines == null)
			return 0;
		else
			return lines;
	}

	/**
	 * Change lines of code
	 * @param lines
	 */
	public void setLines(int lines) {
		this.lines = lines;
	}
	
	/**
	 * Get observations
	 * @return
	 */
	public String getObservations() {
		if(observations == null)
			return "";
		else
			return observations;
	}
	
	/**
	 * Set observations
	 * @param observations
	 */
	public void setObservations(String observations) {
		this.observations = observations;
	}
	
	/**
	 * Get current state
	 * @return
	 */
	public State getState() {
		if(state == null)
			return State.PENDING;
		else
			return state;
	}
	
	/**
	 * Set current state
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}
	
	/**
	 * Language of this submission's program
	 * @return
	 * @throws MooshakContentException
	 */
	public Language getLanguage() throws MooshakContentException {
		if(language == null)
			return null;
		else
			return openRelative("Language", Language.class);
	}
	
	/**
	 * Sets this submission's programming language
	 * @param language
	 */
	public void setLanguage(Language language) {
		if(language == null)
			this.language = null;
		else 
			this.language = language.getId();
	}	
	
	/**
	 * Get the program file
	 * @return
	 */
	public Path getProgram() {
		if(program == null)
			return null;
		else
			return getPath().resolve(program);
	}
	
	/** 
	 * Convenience method for returning program name as a string 
	 * @return name of program or {@code null} if undefined
	 */
	public String getProgramName() {
		String name = null;
		Path resolvedProgram = getProgram(); 
		
		if(resolvedProgram  != null) {
			 Path fileName = resolvedProgram.getFileName();
			 
			 if(fileName != null)
				 name = fileName.toString();
		}
	
		return name;
	}


	
	/**
	 * Sets this program's file
	 * @param program
	 */
	public void setProgram(Path program) {
		if (program == null) 
			this.program = program;
		else
			this.program = program.getFileName();
	}

	/**
	 * Get current reportPath
	 * @return
	 */
	public Path getReportPath() {
		if(reportPath == null)
			return null;
		else
			return getPath().resolve(reportPath);
	}
	
	/**
	 * Set current reportPath
	 * @param reportPath
	 */
	public void setReportPath(Path report) {
		this.reportPath = report.getFileName();
	}
	
	/**
	 * Get time elapsed during execution
	 * @return
	 */
	public double getElapsed() {
		if(elapsed == null)
			return 0;
		else
			return elapsed;
	}
	
	/**
	 * Set time elapsed in this execution
	 * @param elapsed
	 */
	public void setElapsed(double elapsed) {
		this.elapsed = elapsed;
	}
	
	/**
	 * Get cpu time during execution
	 * @return
	 */
	public double getCpu() {
		if(cpu == null)
			return 0;
		else
			return cpu;
	}
	
	/**
	 * Set cpu time during execution
	 */
	public void setCpu(double cpu) {
		this.cpu = cpu;
	}
	
	/**
	 * Get maximum memory used during execution
	 * @return
	 */
	public double getMemory() {
		if(memory == null)
			return 0;
		else
			return memory;
	}
	
	/**
	 * Set maximum memory used during execution
	 * @return
	 */
	public void setMemory(double memory) {
		this.memory = memory;
	}
	
	/**
	 * Get signals received during execution
	 * @return
	 */
	public int getSignals() {
		if(signals == null)
			return 0;
		else
			return signals;
	}
	
	/**
	 * Change signals received during execution
	 * @param signals
	 */
	public void setSignals(int signals) {
		this.signals = signals;
	}
	
	/**
	 * Get feedback given to submission
	 * @return
	 */
	public String getFeedback() {
		if(feedback == null)
			return "";
		else { 
			Path path; 
			if (Strings.isValidPath(feedback) && (path = getAbsoluteFile(feedback)) != null
					&& Files.exists(path))
				try {
					return new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
				} catch (IOException e) {
					return feedback;
				}
			else
				return feedback;
		}
	}
	
	/**
	 * Change feedback given to submission
	 * @param feedback
	 */
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	
	/**
	 * Change feedback given to submission
	 * 
	 * @param feedback
	 * @param toFile write feedback to file
	 * @throws MooshakException 
	 */
	public void setFeedback(String feedback, boolean toFile) throws MooshakException {
		
		if (!toFile) {
			setFeedback(feedback);
		}
		
		File file = getAbsoluteFile(FEEDBACK_FILE_NAME).toFile();
		
		try (
				BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			
			bw.append(feedback);
			bw.flush();
		} catch (IOException e) {
			throw new MooshakException("Writing file: " + e.getMessage());
		}
		
		this.feedback = file.toPath().getFileName().toString();
	}
	
	
	/**
	 * Convenience method to add feedback 
	 * @param feedback
	 */
	public void addFeedback(String feedback) {
		if(feedback != null)
			this.feedback += feedback; 
	}

	public String getProblemId() {
		if(problemId == null) {
			if(problem == null)
				return null;
			else
				return Filenames.getSafeFileName(problem); 
		} else
			return problemId;
	}
	
	public void setProblemId(String problemId) {
		this.problemId = problemId;
	}
	
	/**
	 * Get id of this Team
	 * @return
	 */
	public String getTeamId() {
		if(teamId == null) {
			if(team == null)
				return null;
			else
				return Filenames.getSafeFileName(team);
		} else
			return teamId;
	}
	
	/**
	 * Record the team that originated this submission
	 * @param teamName
	 */
	public void setTeamId(String teamName) {
		this.teamId = teamName;
	}

	/**
	 * Get session that originated this submission
	 * @return
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * Record the session where this submission was originated 
	 * @param sessionId
	 */
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	/**
	 * @return the evaluated
	 */
	public boolean isEvaluated() {
		return evaluated;
	}


	/**
	 * @param evaluated the evaluated to set
	 */
	public void setEvaluated(boolean evaluated) {
		this.evaluated = evaluated;
	}


	/**
	 * Check if this submission is still pending
	 * @return
	 */
	public boolean isPending() {
		return state==State.PENDING;
	}

	
	private Path getContestPath() {
		Path path = null;
		
		if((path = getPath()) == null)
			return null;
		else if((path = path.getParent()) == null)
			return null;
		else if((path = path.getParent()) == null)
			return null;
		else 
			return path;
	}
	
	/**
	 * Contest of this submission
	 * @return
	 * @throws MooshakContentException
	 */
	public Contest getContest() throws MooshakContentException {
		Path path = getContestPath();
		
		if(path == null)
			return null;
		else 
			return PersistentObject.open(path);
	}
	
	/**
	 * Name id the contest of this submission 
	 * or null if none was defined
	 * @return
	 */
	public String getContestId() {
		Path path = getContestPath();
		
		if(path == null)
			return null;
		else if((path = path.getFileName()) == null)
			return null;
		else
			return path.toString();
	}

	
	/**
	 * @return the reviewerPrivateObservations
	 */
	public String getReviewerPrivateObservations() {
		return reviewerPrivateObservations;
	}


	/**
	 * @param reviewerPrivateObservations the reviewerPrivateObservations to set
	 */
	public void setReviewerPrivateObservations(String reviewerPrivateObservations) {
		this.reviewerPrivateObservations = reviewerPrivateObservations;
	}


	/**
	 * @return the reviewerFeedback
	 */
	public String getReviewerFeedback() {
		return reviewerFeedback;
	}


	/**
	 * @param reviewerFeedback the reviewerFeedback to set
	 */
	public void setReviewerFeedback(String reviewerFeedback) {
		
		if (reviewerFeedback != null && !reviewerFeedback.trim().isEmpty()) {
			
			if (this.reviewerFeedback == null || 
					!this.reviewerFeedback.trim().equalsIgnoreCase(reviewerFeedback.trim())) {
				this.reviewerFeedback = reviewerFeedback;
				sendReviewerFeedbackNotification();
			}
		}
			
		this.reviewerFeedback = reviewerFeedback;
	}

	
	 /*-------------------------------------------------------------------*\
	  * 		            Public methods                                *
	 \*-------------------------------------------------------------------*/


	public void sendReviewerFeedbackNotification() {
		AlertNotificationEvent notification = new AlertNotificationEvent();
		notification.setRecipient(new Recipient(getTeamId()));
		notification.setMessage(reviewerFeedback);
		EventSender.getEventSender().send(getContestId(), notification);
	}


	public boolean receiveQuiz() {
		// TODO implement this method
		return false;
	}	
	
	private String problemId = null;
	private String teamId = null;
	private String sessionId = null;
	private YesNo formative = YesNo.NO;
	private boolean evaluated = false;
	
	/**
	 * Receives initialization data.
	 * 
	 * @param teamId
	 * @param programName
	 * @param programCode
	 * @param problemId
	 * @param inputs
	 * @param consider
	 * @param formative
	 * @return
	 * @throws MooshakException
	 */
	public boolean receive(
			String teamId,
			String sessionId,
			String programName, 
			final byte[] programCode,
			String problemId,
			final List<String> inputs,
			boolean consider
	) throws MooshakException {
		
		Submissions submissions = getParent();
		Contest contest = submissions.getParent();

		programName = sanitizeFilename(programName);

		setProblemId(problemId);

		setDate(new Date());
		setTime(contest.transactionTime(teamId));
		setTeamId(teamId);		
		setSessionId(sessionId);
		
		Authenticable team = contest.getTeam(teamId);
		if (team == null) {
			Users users = contest.open("users");
			team = users.find(teamId);
			
			if (team == null) {
				Configs configs = PersistentObject.openPath("data/configs");
				users = configs.open("users");
				team = users.find(teamId);
			}
		}

		setTeam(team);

		if (problemId != null) { // when validating the problem id is omitted  
			setProblem(((Contest)getParent().getParent())
				.open("problems").open(problemId));
		}

		setClassify(Classification.EVALUATING);
		setState(submissions.getDefaultState());
		setProgram(Paths.get(programName));
		setReportPath(REPORT_FILE_NAME);
		setSize((long) programCode.length);
		setLines(Bytes.lineCount(programCode));
		setConsider(consider);
		setFormative(contest.getGui().equals(Gui.ENKI) && teamId != null);

		executeIgnoringFSNotifications(() -> writeProgram(programCode));
		
		if(! consider)
			writeInputs(inputs);

		return true;
	}
	
	private static final Pattern INVALID_PROGRAM_CHARS = Pattern.compile("[^_\\w\\.]"); //$NON-NLS-1$
	
	/**
	 * avoid funny chars in filenames, replace them by underscores
	 * @param name of file
	 * @return sanitized filename
	 */
	private String sanitizeFilename(String name) {
		 return INVALID_PROGRAM_CHARS.matcher(name).replaceAll("_"); //$NON-NLS-1$
	}
	
	/**
	 * Write this program code in program file
	 * @param programCode
	 * @throws MooshakContentException
	 */
	private void writeProgram(byte[] programCode) 
			throws MooshakContentException {
		Path file = getAbsoluteFile(getProgram());
		
		if(Files.isDirectory(file))
			throw new MooshakContentException("No filename given");
		
		try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file.toFile()))) {
			bos.write(programCode);
			bos.flush();
		} catch (IOException cause) {
				throw new MooshakContentException(
						"Error writing program source code:\n"+file,cause);
		}
		
		try {
			Files.setPosixFilePermissions(file, OWNER_READ_WRITE_PERMISSIONS);
		} catch (IOException cause) {
			throw new MooshakContentException("setting permissions is source code",
					cause);
		}
	}
	
	/**
	 * Write inputs in this submission, if any were defined.
	 * @param inputs
	 * @throws MooshakContentException
	 */
	private void writeInputs(final List<String> inputs) 
			throws MooshakContentException {
		
		if(inputs == null || inputs.size() == 0)
			return;
		
		final UserTestData data = getUserTestData();
	
		data.executeIgnoringFSNotifications( 
				() ->  writeInputsOnData(inputs,data));
	}
	
	/**
	 * Write inputs in this submission user data data 
	 * @param inputs
	 * @param data
	 * @throws MooshakContentException
	 */
	private void writeInputsOnData(
			final List<String> inputs,
			final UserTestData data) 
			throws MooshakContentException 	{
		
		Charset cs = getCharset();

		int count=0;
		for(String input: inputs) {
			String name = "in"+(count++)+".txt";
			Path file = data.getAbsoluteFile(name); 

			try(Writer writer = Files.newBufferedWriter(file, cs)) {
				writer.write(input);
			} catch (IOException cause) {
				String message = "writing input file:"+name;
				throw new MooshakContentException(message,cause);
			}

			try {
				Files.setPosixFilePermissions(file, 
						OWNER_READ_WRITE_PERMISSIONS);
			} catch (IOException cause) {
				String message="setting permissions in input file";
				throw new MooshakContentException(message,cause);
			}

		}

	}	
	
	/**
	 * Check if there is user provided test data
	 * @return
	 */
	public boolean hasUserTestData() {
		
		return !isConsider();
	}
	
	/**
	 * Get user provided test data, if it exists.
	 * @see hasUserTestData()
	 * @return
	 * @throws MooshakContentException 
	 */
	public UserTestData getUserTestData() throws MooshakContentException {
		
		if (userTestData == null) {
			if (Files.isReadable(getAbsoluteFile("UserTestData")))
				return open("UserTestData");
			else
				return create("UserTestData", UserTestData.class);
		} 
		
		return userTestData;
	}

	/**
	 * Get a map with outputs of user submitted tests, indexed by order
	 * @return
	 * @throws MooshakException
	 */
	public Map<Integer,String> getUserOutputs() throws MooshakException {
		Map<Integer,String> outputs = new HashMap<>();
		
		if (hasUserTestData()) {
			UserTestData userTestData = getUserTestData();
			try {
				for (Path path : userTestData.getOutputFiles()) {
					
					outputs.put(
							userTestData.orderOfOutputFile(path), 
							new String(Files.readAllBytes(path),
									PersistentCore.CHARSET));
				}
			} catch (MooshakException | IOException e) {
				throw new MooshakException("reading output files", e);
			}
		}
		
		return outputs;
	}
	
	/**
	 * Get a map with execution times of user submitted tests, indexed by order
	 * @return
	 * @throws MooshakException
	 */
	public Map<Integer, String> getUserExecutionTimes() throws MooshakException {
		Map<Integer,String> executionTimes = null;
				
		if( hasUserTestData() ) {
			UserTestData userTestData = getUserTestData();
			executionTimes = userTestData.getExecutionTimes();
		}
		
		return executionTimes;
	}

	//--------- submission analysis: delegate in analyzer ---------------//
	
	
	private  Analyzer analyzer = null;
	
	/**
	 * Gets analyzer for this submission. If none exists then one is created.
	 * Different analyzers may be created according to the kind of submission 
	 * to be analyzed. 
	 * 
	 * Currently only program analysis is supported and warnings are
	 * suppressed in this method since no test to select an analyzer is
	 * actually implemented.
	 * 
	 * @return
	 * @throws MooshakException
	 */
	@SuppressWarnings("unused")
	private Analyzer getAnalyzer() throws MooshakException {
		if(analyzer == null) {
			Problem problem = getProblem();
			if (problem != null && problem.isGame())
				analyzer = new GameAnalyzer(this);
			else if(true)
				analyzer = new ProgramAnalyzer(this);
			else
				throw new MooshakException("Unknown analyzer type");
		} 
		
		return analyzer;
	}
	

	/**
	 * Analyze this submission by delegating on an analyzer.
	 * @throws MooshakException
	 */
	public void analyze() throws MooshakException {
		evaluatedAt = new Date();
		getAnalyzer().analyze();
	}
	
	/**
	 * Gets all analysis reports of this submission by delegating on an analyzer.
	 * @throws MooshakException
	 */
	public List<ReportType> getAllReportTypes() throws MooshakException {
		return getAnalyzer().getAllReports();
	}
	
	/**
	 * Gets the analysis report of this submission by delegating on an analyzer.
	 * @throws MooshakException
	 */
	public ReportType getReportType(int reportNumber) throws MooshakException {
		return getAnalyzer().getReport(reportNumber);
	}
	
	/**
	 * Return a map with a set of values to populate a submission listing line 
	 * @return map with name-value pairs
	 * @throws MooshakContentException if an error occurs when loading group
	 */
	public Map<String,String> getRow() throws MooshakContentException {
		Map<String,String> record = new HashMap<>();
		String teamName = "??";
		String teamId = "??";
		String groupAcronym = "??";
		String groupName = "??";
		String problemId = "??";
		String problemName  = "??";
		String flagName = "00";
		String colorCode = "#000000";
		Authenticable team = getTeam();
		Problem problem = getProblem();
		
		if(team != null) { 
			teamName = team.getName();
			teamId = team.getIdName();
			
			if (team instanceof Team) {
				Group group = ((Team) team).getParent();
				if (group != null) {
					groupAcronym = group.getAcronym();
					groupName = group.getDesignation();
					
					Flag flag = group.getFlag();
					
					if(flag != null)
						flagName = flag.getIdName();
					
					Color color = group.getColor();
					
					if (color != null)
						colorCode = String.format("#%02x%02x%02x", color.getRed(),
								color.getGreen(), color.getBlue());
				}
			}
		}
		
		if(problem != null) {
			if (problem.getName() != null)
				problemName = problem.getName().toString();
			else
				problemName = problem.getIdName();
			problemId = problem.getIdName();
		}
		
		record.put("id", getRowId());
		record.put("time", Dates.showTime(getTime()));
		record.put("absTime", Dates.showDate(getDate()));
		record.put("team", teamName);
		record.put("teamId", teamId);
		record.put("group", groupAcronym);
		record.put("groupName", groupName);
		record.put("problem",problemName);
		record.put("problemId",problemId);
		if (problem != null) {
			Color color = problem.getColor();
			
			if (color != null)
				record.put("problem_" + problem.getIdName() + "_color",String
					.format("#%02x%02x%02x", color.getRed(), color.getGreen(),
							color.getBlue()));
		}
		record.put("flag", flagName);
		record.put("color", colorCode);
		record.put("classification", getClassify().toString());
		record.put("state", getState().toString());
		
		if(mark != null)
			record.put("mark",Integer.toString(mark));
		
		return record;
	}

	private String rowId = null;
	
	@Override
	public String getRowId() throws MooshakContentException {
		if(rowId == null)
			rowId = getParent().getRowId("S");
		
		return rowId;
	}


	/**
	 * Cleanup compilation
	 * @throws MooshakException 
	 */
	public void cleanup() throws MooshakException {
		File[] files = getAbsoluteFile().toFile().listFiles();
		
		if (files == null)
			return;
	    for (int i = 0; i < files.length; i++) {
	    	
	        if (files[i].isFile()){ 
		    	String name = files[i].getName();

	            if (name.startsWith(".") ||
	            		(program != null 
	            			&& getProgram().getFileName().toString().equals(name)) ||
	            		(reportPath != null
	            			&& getReportPath().getFileName().toString().equals(name)))
	            	continue;
	            	
            	files[i].delete();
	        }
	    }
	}

}


