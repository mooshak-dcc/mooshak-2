package pt.up.fc.dcc.mooshak.evaluation.policy;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.types.Authenticable;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Flag;
import pt.up.fc.dcc.mooshak.content.types.Group;
import pt.up.fc.dcc.mooshak.content.types.HasListingRow;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo.ColumnType;

/**
 * Implementation of an evaluation policy for Exam contests.
 * 
 * An instance of this class must be obtained using 
 * {@code RankingPolicy.getPolicy(Policy.Exam,contest);}
 *
 * @author josepaiva
 */
public class ExamRankingPolicy extends RankingPolicy {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger("");

	public ExamRankingPolicy(Contest contest) throws MooshakContentException {
		super(contest);

		reloadTeams();
	}
	
	@Override
	protected void reloadTeams() {

		for (String teamId: teams.keySet()) {
			
			TeamRank rank;
			if ((rank = ranks.get(teamId)) != null)
				rank.team = teams.get(teamId);
			else
				ranks.put(teamId, new TeamRank(teams.get(teamId)));
		}
		
		for (String teamId : new ArrayList<>(ranks.keySet())) {
			if (!teams.containsKey(teamId))
				ranks.remove(teamId);
		}
	}
	

	Map<String,TeamRank> ranks = new HashMap<>();
	
	class TeamRank {
		Team team = null;
		int countSolved = 0;
		int countFailed = 0;
		int sumMarks = 0;
		
		Map<Problem,ProblemInfo> infos = new HashMap<>();
		
		TeamRank(Team team) {
			this.team = team;
			for(Problem problem: problems)
				infos.put(problem, new ProblemInfo());
		}
		
		void addFailedAtempt(Problem problem, int mark) {
			ProblemInfo problemInfo = infos.get(problem);
			
			if(problemInfo == null) {
				// problem may not be available (removed, renamed, frozen)
			} else {
				problemInfo.addFailedAttempt(mark);
				countFailed++;
			}
				
			recalculatePoints();
		}
		
		public void removeFailedAtempt(Problem problem, int mark) {
			ProblemInfo problemInfo = infos.get(problem);
			
			if(problemInfo == null) {
				// problem may not be available (removed, renamed, frozen)
			} else {
				problemInfo.removeFailedAttempt(mark);
				countFailed--;
			}
				
			recalculatePoints();
		}
		
		void addSolvedAttempt(Problem problem, int mark) {
			ProblemInfo problemInfo = infos.get(problem);
	
			if(problemInfo == null) {
				// problem may not be available (removed, renamed, frozen)
			} else if(problemInfo.isSolved()) {
				problemInfo.addSolvedAttempt(mark);
			} else {
				problemInfo.addSolvedAttempt(mark);
				countSolved++;
			}
				
			recalculatePoints();
		}

		public void removeSolvedAttempt(Problem problem, int mark) {
			ProblemInfo problemInfo = infos.get(problem);
			
			if(problemInfo == null) {
				// problem may not be available (removed, renamed, frozen)
			} else if(problemInfo.isSolved()) {
				problemInfo.removeSolvedAttempt(mark);
				
				if (!problemInfo.isSolved())
					countSolved--;
			} else {				
				problemInfo.removeSolvedAttempt(mark);
				countSolved--;
			}

			recalculatePoints();
		}
		
		public void recalculatePoints() {
			int sum = 0;
			for (Problem problem : infos.keySet()) {
				ProblemInfo info = infos.get(problem);
				if (info != null)
					sum += info.mark;
			}
			sumMarks = sum;
		}
		
		public Map<String, String> getData() {
			HashMap<String,String> data = new HashMap<>();

			String groupAcronym = "??";
			String groupName = "??";
			String flagName = "00";
			String colorCode = "#000000";
			
			try {
				Group group = team.getParent();
				Flag flag = group.getFlag();
				
				groupAcronym = group.getAcronym();
				groupName = group.getDesignation();
				if(flag != null)
					flagName = flag.getIdName();
				
				Color color = group.getColor();
				
				if (color != null)
					colorCode = String.format("#%02x%02x%02x", color.getRed(),
							color.getGreen(), color.getBlue());
				
			} catch(MooshakContentException cause)  {
				// ignore these errors and use default values
			}
			
			data.put("#", "0");
			data.put("team",team.getName());
			data.put("group", groupAcronym);
			data.put("groupName", groupName);
			data.put("flag", flagName);
			data.put("color", colorCode);
			
			for(Problem problem: problems) {
				ProblemInfo info = infos.get(problem);
				StringBuilder text = new StringBuilder();
				String id = problem.getName();
				String name = "?";
				
				if(id != null)
					name=id;
				
				
				if(info.isSolved() || info.failedAttempts > 0) {
					text.append(info.mark);
					text.append(" ");
					text.append("(");
					text.append(info.failedAttempts);
					text.append(")");
				}
				
				data.put(name, text.toString());
			}
			
			data.put("solved", String.format("%d", countSolved));
			data.put("points", String.format("%d",sumMarks));
			data.put("order", getOrder());
			
			return data;
		}
		
		/**
		 * Ranking order. This string is used to sort teams in ranking
		 * @return
		 */
		private String getOrder() {
			return String.format("%d",sumMarks);
		}
		
		public RankingUpdate getEvent(Recipient recipient) {						
			RankingUpdate event = new RankingUpdate();
			
			event.setId(team.getIdName());
			event.setRecord(getData());
			event.setRecipient(recipient);
			
			return event;
		}
		
	}
	
	static class ProblemInfo {
		int failedAttempts = 0;
		int solvedAttempts = 0;
		int mark = 0;
		List<Integer> marks = new ArrayList<>();
		
		void addFailedAttempt(int mark) {
			failedAttempts++;
			
			marks.add(mark);
			
			this.mark = Collections.max(marks);
		}
		

		public void removeFailedAttempt(int mark) {
			failedAttempts--;
			
			marks.remove(new Integer(mark));
			
			this.mark = marks.isEmpty() ? 0 : Collections.max(marks);
		}


		void addSolvedAttempt(int mark) {
			marks.add(new Integer(mark));
			this.mark =  Collections.max(marks);
			solvedAttempts++;
		}
		
		public void removeSolvedAttempt(int mark) {
			marks.remove(new Integer(mark));
			
			this.mark = marks.isEmpty() ? 0 : Collections.max(marks);
			
			solvedAttempts--;
		}
		
		public boolean isSolved() {
			return solvedAttempts > 0;
		}
	}

	@Override
	public POStream<? extends HasListingRow> getRows() {
		final Iterator<String> iterator = ranks.keySet().iterator();
		
		return new POStream<HasListingRow>(){

			@Override
			public Iterator<HasListingRow> iterator() {
				return new Iterator<HasListingRow>() {

					@Override
					public boolean hasNext() {
						return iterator.hasNext();
					}

					@Override
					public HasListingRow next() {
						final String teamId = iterator.next();
						final TeamRank teamRank = ranks.get(teamId);
						
						return new HasListingRow() {

							@Override
							public Map<String, String> getRow()	{						
								return teamRank.getData();
							}

							@Override
							public Team getTeam() 
									throws MooshakContentException {
								return teams.get(teamId);
							}

							@Override
							public String getRowId() 
									throws MooshakContentException {
								return teamId;
							}

							@Override
							public Date getTime() {
								return null;
							}
						};
					}

					@Override
					public void remove() {
						iterator.remove();
					}
				};		
			}

			@Override
			public void close() throws Exception {
				// nothing to close
			}};
	}
	
	
	@Override
	public List<ColumnInfo> getColumns() {
		
		List<ColumnInfo> columns = new ArrayList<>();
		List<String> problemNames = new ArrayList<>();
		
		columns.add(new ColumnInfo("order",0));
		columns.add(new ColumnInfo("#",12,ColumnType.RANK));
		columns.add(new ColumnInfo("flag",14,ColumnType.FLAG));
		
		ColumnInfo.addColumns(columns,20,"group");
		columns.add(new ColumnInfo("team",40,ColumnType.TEAM));
		
		Map<String, String> colors = new HashMap<>();

		for(Problem problem: problems) {
			String id = problem.getName();
			String name = "?";
			
			if(id!=null)
				name = id;
			problemNames.add(name);
			
			Color color = problem.getColor();
			String colorCode = null;
			if (color != null)
				colorCode = String.format("#%02x%02x%02x", color.getRed(),
						color.getGreen(), color.getBlue());
			colors.put(name, colorCode);
		}
		Collections.sort(problemNames);
		
		for(String name: problemNames)
			columns.add(new ColumnInfo(name,20, ColumnType.LABEL, colors.get(name)));
		
		ColumnInfo.addColumns(columns,17,"solved");
		ColumnInfo.addColumns(columns,20,"points");
		
		return columns;
	}
	
	@Override
	public synchronized void addSubmission(Submission submission) 
			throws MooshakContentException {
		Authenticable team = submission.getTeam();
		Problem problem = submission.getProblem();
		TeamRank rank;
		
		if(!submission.isConsider() || team == null) // ignore submissions without a team (validations)
			return;
		
		if((rank = ranks.get(team.getIdName())) == null) {
			LOGGER.log(Level.SEVERE,
					"Submission with unkown team:"+team.getIdName());
			return;
		}
			
		switch(submission.getClassify()) {
		case ACCEPTED:
			rank.addSolvedAttempt(problem, submission.getMark());
			break;
		default:
			rank.addFailedAtempt(problem, submission.getMark());		
		}
		
        EventSender sender = EventSender.getEventSender();
		
		sender.send(contest.getIdName(), rank.getEvent(null));
	}
	
	@Override
	public void removeSubmission(Submission submission)
			throws MooshakContentException {
		Authenticable team = submission.getTeam();
		Problem problem = submission.getProblem();
		TeamRank rank;
		
		if(!submission.isConsider() || team == null || !(team instanceof Team)) // ignore submissions without a team (validations)
			return;
		
		if((rank = ranks.get(team.getIdName())) == null) {
			LOGGER.log(Level.SEVERE,
					"Submission with unkown team:"+team.getIdName());
			return;
		}
			
		switch(submission.getClassify()) {
		case ACCEPTED:
			rank.removeSolvedAttempt(problem, submission.getMark());
			break;
		default:
			rank.removeFailedAtempt(problem, submission.getMark());		
		}
		
        EventSender sender = EventSender.getEventSender();
		
		sender.send(contest.getIdName(), rank.getEvent(null));	
		
	}
	
	@Override
	public void sendRankingsTo(Recipient recipient) {
		EventSender sender = EventSender.getEventSender();
		String contestId = contest.getIdName();
		
		for(String teamId: teams.keySet())
			sender.send(contestId, ranks.get(teamId).getEvent(recipient));
		
	}

	@Override
	public int compare(Team t1, Team t2) {
		
		return ranks.get(t2.getIdName()).getOrder().compareTo(ranks.get(t1.getIdName()).getOrder());
	}

	@Override
	public boolean hasRanking(Team team) {
		return ranks.get(team.getIdName()).countSolved > 0;
	}

	
}
