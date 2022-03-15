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
import pt.up.fc.dcc.mooshak.content.util.Dates;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.events.RankingUpdate;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo;
import pt.up.fc.dcc.mooshak.shared.results.ColumnInfo.ColumnType;


/**
 * Implementation of an evaluation policy for ICPC contests.
 * 
 * An instance of this class must be obtained using 
 * {@code RankingPolicy.getPolicy(Policy.ICPC,contest);}
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class IcpcRankingPolicy extends RankingPolicy {

	private static final long serialVersionUID = 1L;
	private static final int MS_IN_MINUTE = 60 * 1000;
	public static final int PENALTY = 20 * MS_IN_MINUTE;
	private static final Logger LOGGER = Logger.getLogger("");
	
	public IcpcRankingPolicy(Contest contest) throws MooshakContentException {
		super(contest);
		reloadTeams();
	}
	
	@Override
	public void reset() throws MooshakContentException {
		
		ranks.clear();
		
		super.reset();
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
		long totalTime = 0;;
		
		Map<Problem,ProblemInfo> infos = new HashMap<>();
		
		TeamRank(Team team) {
			this.team = team;
			for(Problem problem: problems)
				infos.put(problem, new ProblemInfo());
		}
		
		void addFailedAtempt(Problem problem, Date time) {
			ProblemInfo problemInfo = infos.get(problem);
			
			if(problemInfo == null) {
				// problem may not be available (removed, renamed, frozen)
			} else {
				problemInfo.addFailedAttempt(time);
				
				Date acceptedTime = problemInfo.getFirstAcceptedTime();
				if (acceptedTime != null && acceptedTime.getTime() > time.getTime())
					totalTime += PENALTY;
			}
		}
		
		void addSolvedAttempt(Problem problem,Date time) {
			ProblemInfo problemInfo = infos.get(problem);
			
			if(problemInfo == null) {
				// problem may not be available (removed, renamed, frozen)
			} else if(problemInfo.isSolved()) {
				totalTime -= problemInfo.getFirstAcceptedTime().getTime();
				totalTime -= problemInfo.countFailed() * PENALTY;
				problemInfo.addSolvedAttempt(time);
				totalTime += problemInfo.getFirstAcceptedTime().getTime();
				totalTime += problemInfo.countFailed() * PENALTY;
			} else {
				problemInfo.addSolvedAttempt(time);
				countSolved++;
				totalTime += time.getTime();
				totalTime += problemInfo.countFailed() * PENALTY;
			}
		}
		
		public void removeSolvedAttempt(Problem problem, Date time) {
			ProblemInfo problemInfo = infos.get(problem);
			
			if(problemInfo == null) {
				// problem may not be available (removed, renamed, frozen)
			} else if(problemInfo.isSolved()) {
				totalTime -= problemInfo.getFirstAcceptedTime().getTime();
				totalTime -= problemInfo.countFailed() * PENALTY;
				problemInfo.removeSolvedAttempt(time);
				
				if (!problemInfo.isSolved())
					countSolved--;
				else {
					totalTime += problemInfo.getFirstAcceptedTime().getTime();
					totalTime += problemInfo.countFailed() * PENALTY;
				}
			} else {
				problemInfo.removeSolvedAttempt(time);
			}
		}

		public void removeFailedAtempt(Problem problem, Date time) {
			ProblemInfo problemInfo = infos.get(problem);
			
			if(problemInfo == null) {
				// problem may not be available (removed, renamed, frozen)
			} else if(problemInfo.isSolved()) {
				totalTime -= problemInfo.countFailed() * PENALTY;
				problemInfo.removeFailedAttempt(time);
				totalTime += problemInfo.countFailed() * PENALTY;
			} else {
				problemInfo.removeFailedAttempt(time);
			}
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
				
				
				if(info.isSolved()) 
					text.append(Dates.showTime(info.getFirstAcceptedTime()));	
				else if(info.countFailed() > 0)
					text.append("------");
				if(info.isSolved() || info.countFailed() > 0) {
					text.append(" ");
					text.append("(");
					text.append(info.countFailed());
					text.append(")");
				}
				
				data.put(name, text.toString());
			}
			
			data.put("solved", String.format("%d", countSolved));
			data.put("points",Dates.showTime(new Date(totalTime))); 
			data.put("order", getOrder());
			
			return data;
		}
		
		/**
		 * Ranking order. This string is used to sort teams in ranking
		 * @return
		 */
		private String getOrder() {
			return String.format("%03d:%d",	countSolved,
					Integer.MAX_VALUE-(totalTime/1000));
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
		List<Date> times = new ArrayList<>();
		List<Date> failingTimes = new ArrayList<>();
		
		void addFailedAttempt(Date time) {
			failingTimes.add(time);
		}

		public void removeFailedAttempt(Date time) {
			failingTimes.remove(time);
		}

		void addSolvedAttempt(Date time) {
			times.add(time);
		}
		
		public void removeSolvedAttempt(Date time) {
			times.remove(time);
		}

		public Date getFirstAcceptedTime() {
			if (times.isEmpty())
				return null;
			
			Collections.sort(times);
			return times.get(0);
		}
		
		public int countFailed() {
			if (failingTimes.isEmpty())
				return 0;
			
			int count = 0;
			
			Date firstAccepted = getFirstAcceptedTime();
			if (firstAccepted == null)
				return failingTimes.size();
			
			for (Date date : failingTimes) {
				if (!date.after(firstAccepted))
					count++;
			}
			
			return count;
		}

		public boolean isSolved() {
			return !times.isEmpty();
		}
	}
	
	@Override
	public synchronized void addSubmission(Submission submission) 
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
			rank.addSolvedAttempt(problem, submission.getTime());
			break;
		default:
			rank.addFailedAtempt(problem, submission.getTime());		
		}
		
        EventSender sender = EventSender.getEventSender();
		
		sender.send(contest.getIdName(), rank.getEvent(null));
	}
	
	@Override
	public synchronized void removeSubmission(Submission submission) 
			throws MooshakContentException{
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
			rank.removeSolvedAttempt(problem, submission.getTime());
			break;
		default:
			rank.removeFailedAtempt(problem, submission.getTime());		
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
				// nothing to be closed in this case
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
	public int compare(Team t1, Team t2) {
		
		return ranks.get(t2.getIdName()).getOrder().compareTo(ranks.get(t1.getIdName()).getOrder());
	}

	@Override
	public boolean hasRanking(Team team) {
		return ranks.get(team.getIdName()).countSolved > 0;
	}

	
}
