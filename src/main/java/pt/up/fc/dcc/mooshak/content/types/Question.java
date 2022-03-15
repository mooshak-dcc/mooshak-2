package pt.up.fc.dcc.mooshak.content.types;

import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.util.Dates;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

/**
 * Question asked by team during contest
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @version 2.0
 * @since July 2013
 */
public class Question extends PersistentObject 
	implements HasListingRow{
	private static final long serialVersionUID = 1L;
	
	public enum State {
		UNANSWERED, ALREADY_ANSWERED, WITHOUT_ANSWER, ANSWERED;
		
		public String toString() {
			return super.toString().toLowerCase();
		}
	}

	@MooshakAttribute(
			name = "Date", 
			type = AttributeType.DATE)
	private Date date = null;

	@MooshakAttribute(
			name = "Time", 
			type = AttributeType.DATE)
	private Date time = null;

	@MooshakAttribute(
			name = "Delay", 
			type = AttributeType.DATE)
	private Date delay = null;

	@MooshakAttribute(
			name = "Problem", 
			type = AttributeType.PATH, 
			refType = "Problem",
			complement = "../../problems")
	private Path problem = null;

	@MooshakAttribute(
			name = "Team", 
			refType = "Team",
			type = AttributeType.PATH, 
			complement = "../../groups")
	private Path team = null;

	@MooshakAttribute(
			name = "State", 
			type = AttributeType.MENU, 
			tip = "State if question")
	private State state = null;

	@MooshakAttribute(
			name = "Subject", 
			tip = "Subject of the question")
	private String subject = null;

	@MooshakAttribute(
			name = "Question", 
			type = AttributeType.LONG_TEXT,
			tip = "text of question")
	private String question = null;

	@MooshakAttribute(
			name = "Answer", 
			type = AttributeType.LONG_TEXT,
			tip = "Answer to this question")
	private String answer = null;

	// -------------------- Setters and getters ----------------------//

	
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
	 * Contest of this question
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
	 * Name id the contest of this question 
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
	 * Moment in which question was created
	 * 
	 * @return
	 */
	public Date getDate() {
		if (date == null)
			return new Date(0);
		else
			return date;
	}

	/**
	 * Change moment which question was created
	 * 
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * time since beginning of contest when question was created
	 * 
	 * @return
	 */
	public Date getTime() {
		if (time == null)
			return new Date(0);
		else
			return time;
	}

	/**
	 * Change time since beginning of contest when question was created
	 * 
	 * @param time
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * Delay between posing and answering this question
	 * 
	 * @return
	 */
	public Date getDelay() {
		if (delay == null)
			return new Date(0);
		else
			return delay;
	}

	/**
	 * Set the delay between posing and answering this question
	 * 
	 * @param delay
	 */
	public void setDelay(Date delay) {
		this.delay = delay;
	}

	/**
	 * Gets the problem this submission attempted to solve
	 * 
	 * @return problem
	 * @throws MooshakContentException
	 */
	public Problem getProblem() throws MooshakContentException {
		if (problem == null)
			return null;
		else
			return openRelative("Problem", Problem.class);
	}

	/**
	 * Change the problem this submission attempts to solve
	 * 
	 * @param team
	 */
	public void setProblem(Problem problem) {
		if (problem == null)
			this.problem = null;
		else
			this.problem = problem.getId();
	}
	
	/**
	 * Get the team that asked this question
	 * @throws MooshakContentException 
	 */
	public Authenticable getTeam() throws MooshakContentException {
		if(team == null)
			return null;
		else {
			Contest contest = getContest();
			Groups groups = contest.open("groups");
			
			Authenticable authenticable;
			if ((authenticable = groups.find(team.toString())) != null)
				return authenticable;
			
			Users users = contest.open("users");
			if ((authenticable = users.find(team.toString())) != null)
				return authenticable;
			
			Configs configs = PersistentObject.openPath("data/configs");
			users = configs.open("users");
			if ((authenticable = users.find(team.toString())) != null)
				return authenticable;
			
			return null;
		}
	}
	
	/**
	 * Change this question's team (a file pointer) from the team object
	 * @param team
	 */
	public void setTeam(Authenticable team) {
		if(team == null)
			this.team = null;
		else {
			this.team = ((PersistentObject) team).getId();
		}
	}
	
	/**
	 * Get current state
	 * 
	 * @return
	 */
	public State getState() {
		if (state == null)
			return State.UNANSWERED;
		else
			return state;
	}

	/**
	 * Set current state, null for default
	 * 
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
	}

	/**
	 * Get this message's subject
	 * @return
	 */
	public String getSubject() {
		
		if(subject == null) 
			return "";
		else 
			return subject;
	}
	
	/**
	 * Set this message's subject; null reverts to default
	 * @param subject
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * Get this message's question
	 * @return
	 */
	public String getQuestion() {
		
		if(question == null) 
			return "";
		else 
			return question;
	}
	
	/**
	 * Set this message's subject; null reverts to default
	 * @param question
	 */
	public void setQuestion(String question) {
		this.question = question;
	}
	
	/**
	 * Get this message's answer
	 * @return
	 */
	public String getAnswer() {
		
		if(answer == null) 
			return "";
		else 
			return answer;
	}
	
	/**
	 * Set this message's answer; null reverts to default
	 * @param answer
	 */
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	/**
	 * Return a map with a set of values to populate a question listing line 
	 * @return map with name-value pairs
	 * @throws MooshakContentException if an error occurs when loading group
	 */
	public Map<String,String> getRow() throws MooshakContentException {
		Map<String,String> record = new HashMap<>();
		String teamName = "??";
		String groupAcronym = "??";
		String groupName = "??";
		String problemId = "??";
		String problemName  = "??";
		Authenticable team = getTeam();
		Problem problem = getProblem();
		
		if(team != null) { 
			if (team instanceof Team) {
				Group group = ((Team) team).getParent();
				groupAcronym = group.getAcronym();
				groupName = group.getDesignation();
			}
			teamName = team.getName();
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
		record.put("group", groupAcronym);
		record.put("groupName", groupName);
		record.put("problem",problemName);
		record.put("problemId",problemId);
		record.put("subject", getSubject());
		record.put("state", getState().toString());
		
		return record;
	}
	
	private String rowId = null;
	
	@Override
	public String getRowId() throws MooshakContentException {
		if(rowId == null)
			rowId = getParent().getRowId("Q");
		
		return rowId;
	}
}
