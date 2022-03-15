package pt.up.fc.dcc.mooshak.content.types;

import java.awt.Color;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.util.Dates;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;

public class Balloon extends PersistentObject implements HasListingRow {
	private static final long serialVersionUID = 1L;

	public enum State { DELIVERED, UNDELIVERED;
	
		public String toString() {
			return super.toString().toLowerCase();
		}
	
	}
	
	@MooshakAttribute(
			name="Date",
			type=AttributeType.DATE)
	private Date date = null;
	
	@MooshakAttribute(
			name="Time",
			type=AttributeType.DATE)
	private Date time = null;
	
	@MooshakAttribute(
			name="Delay",
			type=AttributeType.DATE)
	private Date delay = null;
	
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
			complement="../../groups")
	private Path team = null;
	
	@MooshakAttribute(
			name="State",
			type=AttributeType.MENU)
	private State state = null;
	
	@MooshakAttribute(
			name="Submission",
			type=AttributeType.PATH,
			complement="../../submissions")
	private Path submission = null;

	//-------------------- Setters and getters ----------------------//

	
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
	 * Contest of this balloon
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
	 * Name id the contest of this balloon 
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
		 * Moment in which balloon was created
		 * @return
		 */
		public Date getDate() {
			if(date == null)
				return new Date(0);
			else
				return date;
		}
		
		/**
		 * Change moment which balloon was created
		 * @param date
		 */
		public void setDate(Date date) {
			this.date = date;
		}
		
		/**
		 * time since beginning of contest when balloon was created
		 * @return
		 */
		public Date getTime() {
			if(time == null)
				return new Date(0);
			else
				return time;
		}
		
		/**
		 * Change time since beginning of contest when balloon was created
		 * @param time
		 */
		public void setTime(Date time) {
			this.time = time;
		}
		
		/**
		 * time taken to deliver this balloon
		 * @return
		 */
		public Date getDelay() {
			if(delay == null)
				return new Date(0);
			else
				return delay;
		}		

		/**
		 * Change time taken to deliver this balloon
		 * @param time
		 */
		public void setDelay(Date delay) {
			this.delay = delay;
		}
		
		/**
		 * Get the team that produced this balloon
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
		 * Change this balloon's team (a file pointer) from the team object
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
		 * Gets the problem this balloon attempted to solve
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
		 * Change the problem this balloon attempts to solve
		 * @param team
		 */
		public void setProblem(Problem problem) {
			if(problem == null)
				this.problem = null;
			else
				this.problem = problem.getId();
		}

		
		/**
		 * Get current state of this balloon
		 * @return
		 */
		public State getState() {
			if(state == null)
				return State.UNDELIVERED;
			else
				return state;
		}

		/**
		 * Set state of this balloon, null for default
		 * @param state
		 */
		public void setState(State state) {
			this.state = state;
		}
		
		/**
		 * Get the submission that originated this balloon 
		 * @throws MooshakContentException 
		 */
		public Submission getSubmission() throws MooshakContentException {
			if(submission == null)
				return null;
			else 
				return openRelative("Submission", Submission.class);
		}
		
		/**
		 * Change the submission (a file pointer) that originated this balloon
		 * @param submission
		 */
		public void setSubmission(Submission submission) {
			if(submission == null)
				this.submission = null;
			else {
				this.submission = submission.getId();
			}
		}
		
		/**
		 * Return a map with a set of values to populate a balloon listing line 
		 * @return map with name-value pairs
		 * @throws MooshakContentException if an error occurs when loading group
		 */
		public Map<String,String> getRow() throws MooshakContentException {
			Map<String,String> record = new HashMap<>();
			String teamName = "??";
			String groupAcronym = "??";
			String groupName = "??";
			String problemName  = "??";
			String colorCode = "#000000";
			Authenticable team = getTeam();
			Problem problem = getProblem();
			
			if(team != null) { 
				try {
					teamName = team.getName();
					if (team instanceof Team) {
						Group group = ((Team) team).getParent();
		
						if(group != null) {
							groupAcronym = group.getAcronym();
							groupName = group.getDesignation();
						
							Color color = group.getColor();
							
							if (color != null)
								colorCode = String.format("#%02x%02x%02x", color.getRed(),
										color.getGreen(), color.getBlue());
						}
					}
				} catch(MooshakContentException cause)  {
					// ignore these errors and use default values
				}
			}
			
			if(problem != null) {
				problemName = problem.getName().toString();
			}
			
			record.put("id", getRowId());
			record.put("time", Dates.showTime(getTime()));
			record.put("absTime", Dates.showDate(getDate()));
			record.put("team", teamName);
			record.put("color", colorCode);
			record.put("group", groupAcronym);
			record.put("groupName", groupName);
			record.put("problem",problemName);
			if (problem != null) {
				Color color = problem.getColor();
				
				if (color != null)
					record.put("problem_" + problem.getIdName() + "_color",String
						.format("#%02x%02x%02x", color.getRed(), color.getGreen(),
								color.getBlue()));
			}
			record.put("state", getState().toString());
			
			return record;
		}

		private String rowId = null;
		
		@Override
		public String getRowId() throws MooshakContentException {
			if(rowId == null)
				rowId = getParent().getRowId("B");
			
			return rowId;
		}

}
