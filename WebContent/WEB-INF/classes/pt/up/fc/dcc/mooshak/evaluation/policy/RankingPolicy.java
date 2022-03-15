package pt.up.fc.dcc.mooshak.evaluation.policy;


/**
 * Common type of all classes providing a ranking policy.
 * Ranking policies should be placed in sub-package 
 * {@code policy} and should be instanced using 
 * {@code RankingPolicy.getPolicy(policy,contest);}
 * 
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 * @since 2.0
 * @version 2.0
 *
 */
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.Groups;
import pt.up.fc.dcc.mooshak.content.types.HasListingRows;
import pt.up.fc.dcc.mooshak.content.types.Problem;
import pt.up.fc.dcc.mooshak.content.types.Problems;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.content.types.Team;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;

public abstract class RankingPolicy implements 
	HasListingRows, Serializable, Comparator<Team> {

	private static final long serialVersionUID = 1L;

	
	public enum Policy { 
		DEFAULT(IcpcRankingPolicy.class), 
		ICPC(IcpcRankingPolicy.class), 
		IOI(IoiRankingPolicy.class), 
		XTREME(null), 
		EXAM(ExamRankingPolicy.class), 
		QUIZ(null), 
		SHORT(null); 
		
		Class<? extends RankingPolicy> type;
		
		Policy(Class<? extends RankingPolicy> type) {
			this.type = type;
		}
		
		Class<? extends RankingPolicy> getType() {
			return type;
		}
	}
	
	protected Map<String, Team> teams = new HashMap<>();
	protected List<Problem> problems = new ArrayList<>();
	protected Contest contest;
	
	protected RankingPolicy(Contest contest) throws MooshakContentException {
		this.contest = contest;
	
		prepare();
	}
	
	private Timer updateTimer = null;
	private static long UPDATE_DELAY_TIMER = 1000L;
	
	/**
	 * Update ranking by reloading teams. Several update requests may be
	 * received in a row and only the last is processed, if the time between 
	 * requests is less than a second
	 */
	public void update() {
		
		if(updateTimer != null)
			updateTimer.cancel();
		
		updateTimer = new Timer("Ranking update timer");
		updateTimer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					try {
						prepare();
						reloadTeams();
					} catch (MooshakContentException cause) {
						Logger.getLogger("").log(Level.SEVERE,
								"Updating ranking policy",cause);
					}
					updateTimer = null;
				}
		}, UPDATE_DELAY_TIMER);
	}
	
	public void reset() throws MooshakContentException {
		prepare();
		reloadTeams();
		loadSubmissions();
	}
	
	private void prepare() throws MooshakContentException {
		Groups groups = contest.openOrInherit("groups");
		
		teams.clear();
		for (Team team: groups.getContent())
			teams.put(team.getIdName(), team);

		Problems problemSet = contest.openOrInherit("problems");

		problems = problemSet.getContent();		
	}
	
	
	/**
	 * Load all the existing submissions before start accepting 
	 * incremental updates 
	 * 
	 * @throws MooshakContentException
	 */
	protected void loadSubmissions() throws MooshakContentException {
		Submissions submissions = contest.openOrInherit("submissions");
	
		List<Submission> submissionsList = new ArrayList<>();
		try(POStream<Submission> stream = submissions.newPOStream()) {
			for(Submission submission: stream)
				submissionsList.add(submission);
		} catch(Exception cause) {
			throw new MooshakContentException(
					"Error iterating over submissions",cause);
		}
		
		Collections.sort(submissionsList, new Comparator<Submission>() {

			@Override
			public int compare(Submission o1, Submission o2) {
				
				return o1.getIdName().compareTo(o2.getIdName());
			}
		});
		
		for (Submission submission : submissionsList)
			addSubmission(submission);
	}
	
	/**
	 * Teams where reloaded. Do what you need
	 */
	protected abstract void reloadTeams();
	
	/**
	 * Add a submission to this evaluation policy.
	 * The policy should evaluate incrementally its rankings.
	 * 
	 * @param submission	to add to the ranking
	 * @throws MooshakContentException
	 */
	public abstract void addSubmission(Submission submission)
			throws MooshakContentException;
	
	
	/**
	 * Remove a submission to this evaluation policy
	 * The submission should have been previously added and this
	 * method should revert its effects in the incremental evaluation of the 
	 * rankings
	 * 
	 * @param submission
	 * @throws MooshakContentException
	 */
	public abstract void removeSubmission(Submission submission)
			throws MooshakContentException;
	
	/**
	 * Send ranking update events with the current state of every team in 
	 * this contest to the given recipient  
	 * 
	 * @param recipient who will receive the events
	 * @throws MooshakContentException
	 */
	public abstract void sendRankingsTo(Recipient recipient) 
			throws MooshakContentException;
		
	
	/**
	 * Should this team have a ranking in certificates?
	 * Or should it be given an "Honorable Mention"?
	 * 
	 * @return {@code true} if team should be in rankings; and {@code false}
	 * 			if it should be and "Honorable Mention"
	 */
	public abstract boolean hasRanking(Team team);
	
	/**
	 * Compute final rankings and save them in teams 
	 * As a side effect teams are sorted in ascending order 
	 */
	public List<Team> finalizeRankings() {
		
		List<Team> ranking = new ArrayList<>(teams.values());
		
		// sort teams by ranking
		Collections.sort(ranking, this);
		
		int rank = 1;
		for(Team team: ranking) {
			if(hasRanking(team))
				team.setRank(rank++);
			else
				team.setRank(0);


			try {
				team.save();
			} catch (MooshakContentException cause) {
				Logger.getLogger("").log(Level.SEVERE,
						"Error saving team rank",cause);
			}
		}
		
		update();
	
		return ranking;
	}
	
	
	/**
	 * Get a ranking policy for a contest
	 * @param name of policy (prefix of a ranking policy class)
	 * @param contest (context object)
	 * @return
	 * @throws MooshakException
	 */
	public static <T extends RankingPolicy> T
			getPolicy(Policy policy,Contest contest) 
			throws MooshakException {
		T rankingPolicy;
		Constructor<?> constructor;
			
		if(policy.getType() == null)
			throw new MooshakException(
					"RankingPolicy not assigned to policy "+policy);
		
		try {
			constructor = policy.type.getConstructor(Contest.class);
			
			@SuppressWarnings("unchecked")
			T tmp  = (T) constructor.newInstance(contest);
			
			rankingPolicy = tmp;
			
		} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | 
					InvocationTargetException | 
					NoSuchMethodException | 
					SecurityException cause) {
				String message = "Cannot instance ranking policy "+policy;
				throw new MooshakException(message,cause);
		}
		
		switch(contest.getContestStatus()) {
		case RUNNING_VIRTUALLY:
				// don't load submissions if is running virtually
				// rankings cannot be broadcast
				// but sent incrementally to each team
			break;
			default:
				rankingPolicy.loadSubmissions();		
		}
		
		return rankingPolicy;
	}	

}
