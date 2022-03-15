package pt.up.fc.dcc.mooshak.content.types;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import pt.up.fc.dcc.mooshak.content.MooshakAttribute;
import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.MooshakOperation;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.util.Dates;
import pt.up.fc.dcc.mooshak.content.util.Filenames;
import pt.up.fc.dcc.mooshak.content.util.StringEscapeUtils;
import pt.up.fc.dcc.mooshak.managers.AdministratorManager;
import pt.up.fc.dcc.mooshak.print.PrinterController;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.commands.AttributeType;
import pt.up.fc.dcc.mooshak.shared.commands.CommandOutcome;
import pt.up.fc.dcc.mooshak.shared.commands.MooshakObject;


/**
 * A balloon requested by teams during contests
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @since July 2013
 * @version 2.0
 *
 *        Recoded in Java in July 2013 
 *         From a Tcl module coded in April 2005
 */
public class Printout extends PersistentObject implements HasListingRow {
	private static final long serialVersionUID = 1L;

	public enum State {  DELIVERED, UNDELIVERED;
	
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
			name="Program",
			type=AttributeType.FILE)
	private Path program = null;
	
	/****************************************************************\
	 * 						Operations								*
	 ****************************************************************/
	
	@MooshakOperation(
			name="Print",
			inputable=false,
			tip="Print this printout")
	CommandOutcome print() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			sendToPrinter();
			outcome.setMessage("Job sent to printer ...");
		} catch (Exception e) {
			outcome.setMessage("Error: "+e.getLocalizedMessage());
		}
		
		return outcome;
	}

	/**
	 * Send printout to printer
	 * @throws MooshakException
	 */
	public void sendToPrinter()
			throws MooshakException {
		String programFileName = null;
		
		if(program == null || 
			(programFileName = Filenames.getSafeFileName(program)) == null ||
			"".equals(programFileName)) 
			throw new MooshakException("No filename given");
		
		if(!Files.exists(getAbsoluteFile(programFileName)))
			throw new MooshakException("No file found");

		if(((Printouts) getParent()).getConfig() == null)
			((Printouts) getParent()).createDefaultConfig();
		if(((Printouts) getParent()).getTemplate() == null)
			((Printouts) getParent()).createDefaultTemplate();

		MooshakObject printouts = AdministratorManager.getInstance()
				.getMooshakObject(getParent().getPath().toString());

		if(!printouts.getFieldValue("Active").getSimple().toLowerCase()
				.equals("yes")) 
			throw new MooshakException("Printing is not active");
		
		File template = new File(getParent().getAbsoluteFile(printouts
				.getFieldValue("Template").getName()).toString());
		if(!template.exists()) 
			throw new MooshakException("Missing printout template");

		File config = new File(getParent().getAbsoluteFile(printouts
				.getFieldValue("Config").getName()).toString());
		if(!config.exists()) 
			throw new MooshakException("Missing CSS config file");

		PrinterController printer = null;
		try {
			String content = setTemplateValues(
					new String(printouts.getFieldValue("Template")
					.getContent(), "UTF-8"));

			printer = new PrinterController(content,
					new String(printouts.getFieldValue("Config")
							.getContent(), "UTF-8"));
		} catch (Exception e) {
			throw new MooshakException("Could not read template file");
		}
		String filename = "Mooshak-" + getParent().getParent().getIdName() +
				"-printout-" + getIdName() + ".pdf";
		File file = new File(getAbsoluteFile().toString() + File.separator +
				filename);
		printer.printToFile(file, true, false);
		
		String service = ((Printouts)getParent()).getPrinter();
		
		String os = System.getProperty("os.name");
		if(os.toLowerCase().indexOf("linux") != -1) {
			Process pr = null;
			try {
				String printerArg = "";
				if(!service.equals(""))
					printerArg = " -P" + service;
				pr = Runtime.getRuntime().exec("lpr" + printerArg + " -J" + filename + " "
						+ file.getAbsolutePath());
			} catch (IOException e) {
				throw new MooshakException("Could not send file to printer");
			}
			
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
			
			String result = "";
			try {
				String s;
				while ((s = in.readLine()) != null) 
					result += s;
			} catch (IOException e) {
				throw new MooshakException(e.getMessage());
			}
			
			if (!result.equals(""))
				throw new MooshakException(result);
		}
		else
			throw new MooshakException();
	}
	
	
	/**
	 * Sets values in template
	 * @param template
	 * @return
	 */
	private String setTemplateValues(String template) {
		String content = "";
		String programFileName = Filenames.getSafeFileName(program);
		try {
			content = new String(Files
					.readAllBytes(Paths.get(getAbsoluteFile().toString(), 
							programFileName)), "UTF-8");
			content = StringEscapeUtils.escapeHtml(content);
		} catch (IOException e) {
		}
		
		String temp = template;
		if(problem != null)
			temp = temp.replaceAll("\\$Problem", Filenames.getSafeFileName(problem));
		else
			temp = temp.replaceAll("\\$Problem", "A");

		if(team != null)
			temp = temp.replaceAll("\\$Team", Filenames.getSafeFileName(team));
		else
			temp = temp.replaceAll("\\$Team", "Coders");

		temp = temp.replaceAll("\\$Name", getIdName());
		try {
			Authenticable teamObj = getTeam();
			if (teamObj instanceof Team)
				temp = temp.replaceAll("\\$Location", ((Team) teamObj).getLocation());
			else
				throw new MooshakContentException();
		} catch (MooshakContentException e) {
			temp = temp.replaceAll("\\$Location", "?Unknown team?");
		}
		temp = temp.replaceAll("\\$Program", programFileName);
		temp = temp.replaceAll("\\$AbsoluteTime", Dates.showDate(getDate()));
		temp = temp.replaceAll("\\$ContestTime", Dates.showTime(getTime()));
		
		if(temp.indexOf("$Content") == -1)
			temp += content;
		
		temp = temp.replaceAll("\\$Content", content.replaceAll("\\\\","\\\\\\\\"));
		
		return temp;
	}
	
	@MooshakOperation(
			name="TestPage",
			inputable=false,
			show=false)
	CommandOutcome createTestPrintout() {
		CommandOutcome outcome = new CommandOutcome();
		
		try {
			setDate(new Date());
			setTime(new Date());
			setState(State.DELIVERED);
			
			Team team = null;
			if(getParent().getParent().open("groups").getChildren(false).size() > 0
					&& getParent().getParent().open("groups").getChildren(false)
					.get(0).getChildren(false).size() > 0)
				team = (Team) getParent().getParent().open("groups")
					.getChildren(false).get(0).getChildren(false).get(0);
			setTeam(team);
			
			Problem problem = null;
			if(getParent().getParent().open("problems").getChildren(false)
					.size() > 0)
				problem = (Problem) getParent().getParent().open("problems")
					.getChildren(false).get(0);
			setProblem(problem);
			
			setDelay(new Date(0));

			Path program = getAbsoluteFile(
					Filenames.getSafeFileName(Paths.get("hello.c")));

			String content = "#include <stdio.h>\n#include <stdlib.h>"
					+ "\n\nmain() {\n\tprintf(\"hello world \\n\");\n}";
			try {
				Files.write(program, content.getBytes());
			} catch (IOException e) {
				throw new MooshakException(e.getMessage());
			}
			setProgram(program);
			
			save();
		} catch (MooshakException e) {
			outcome.setMessage("Error:"+program.toString());
		}
		
		return outcome;
	}

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
	 * Contest of this printout
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
	 * Name id the contest of this printout 
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
	 * @return
	 */
	public Date getDate() {
		if(date == null)
			return new Date(0);
		else
			return date;
	}

	/**
	 * Change moment which question was created
	 * @param date
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * time since beginning of contest when question was created
	 * @return
	 */
	public Date getTime() {
		if(time == null)
			return new Date(0);
		else
			return time;
	}		

	/**
	 * Change time since beginning of contest when question was created
	 * @param time
	 */
	public void setTime(Date time) {
		this.time = time;
	}

	/**
	 * time taken to deliver this printout
	 * @return
	 */
	public Date getDelay() {
		if(delay == null)
			return new Date(0);
		else
			return delay;
	}		

	/**
	 * Change time taken to deliver this printout
	 * @param time
	 */
	public void setDelay(Date delay) {
		this.delay = delay;
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
	 * Get the team that produced this printout
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
	 * Change this printout's team (a file pointer) from the team object
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
	 * Get current state of this question
	 * @return
	 */
	public State getState() {
		if(state == null)
			return State.UNDELIVERED;
		else
			return state;
	}

	/**
	 * Set state of this question, null for default
	 * @param state
	 */
	public void setState(State state) {
		this.state = state;
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
	 * Sets this program's file
	 * @param program
	 */
	public void setProgram(Path program) {
		this.program = program.getFileName();
	}
	
	public void setProgram(String filename, String content) throws MooshakException {
		Path path = null;
		if(filename != null && ! "".equals(filename) && content!= null) {
			path = Paths.get(filename).getFileName();
			if(path != null) {
				Path full = getAbsoluteFile(path.toString());

				try {
					executeIgnoringFSNotifications( 
							() -> Files.write(full, content.getBytes())
							);
				} catch (IOException cause) {
					String message = "Writing file "+path;
					throw new MooshakException(message, cause);
				}
				setProgram(path);
			}
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
		Authenticable team = getTeam();
		String colorCode = "#000000";
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
		record.put("problem", problemName);
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
			rowId = getParent().getRowId("P");
		
		return rowId;
	}
}
