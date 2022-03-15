package pt.up.fc.dcc.mooshak.managers;

import java.nio.file.Files;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.logging.Level;

import pt.up.fc.dcc.mooshak.content.MooshakContentException;
import pt.up.fc.dcc.mooshak.content.PathManager;
import pt.up.fc.dcc.mooshak.content.PersistentContainer.POStream;
import pt.up.fc.dcc.mooshak.content.PersistentObject;
import pt.up.fc.dcc.mooshak.content.PersistentObjectListener;
import pt.up.fc.dcc.mooshak.content.types.Balloon;
import pt.up.fc.dcc.mooshak.content.types.Contest;
import pt.up.fc.dcc.mooshak.content.types.HasListingRow;
import pt.up.fc.dcc.mooshak.content.types.HasListingRows;
import pt.up.fc.dcc.mooshak.content.types.Printout;
import pt.up.fc.dcc.mooshak.content.types.Question;
import pt.up.fc.dcc.mooshak.content.types.Session;
import pt.up.fc.dcc.mooshak.content.types.Submission;
import pt.up.fc.dcc.mooshak.content.types.Submissions;
import pt.up.fc.dcc.mooshak.evaluation.policy.RankingPolicy;
import pt.up.fc.dcc.mooshak.server.events.EventSender;
import pt.up.fc.dcc.mooshak.shared.MooshakException;
import pt.up.fc.dcc.mooshak.shared.events.BalloonsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.ListingUpdateEvent;
import pt.up.fc.dcc.mooshak.shared.events.PrintoutsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.QuestionsUpdate;
import pt.up.fc.dcc.mooshak.shared.events.Recipient;
import pt.up.fc.dcc.mooshak.shared.events.SubmissionsUpdate;


/**
 * Manages requests related to rows, and provides methods for row management
 * to the other managers
 *
 * @author José Paulo Leal <zp@dcc.fc.up.pt>
 */
public class RowManager extends Manager {
	
private static final EventSender EVENT_SENDER=EventSender.getEventSender();
	
	private static final PathManager PATH_MANAGER = PathManager.getInstance();
	
	private static volatile RowManager manager = null;
	
	/**
	 * Subscribe persistent object external changes and dispatch them to clients  
	 */
	private RowManager() {
		
		PATH_MANAGER.addPersistentObjectListener(Submission.class, 
				new PersistentObjectListener<Submission>() {

			@Override
			public void receivedPersistentObject(Submission submission)
					throws MooshakContentException {

				broadcastRow(submission,SubmissionsUpdate::new);
			}
		});
		
		PATH_MANAGER.addPersistentObjectListener(Question.class, 
				new PersistentObjectListener<Question>() {

			@Override
			public void receivedPersistentObject(Question question)
					throws MooshakContentException {

				broadcastRow(question, QuestionsUpdate::new);
			}
		});
		
		PATH_MANAGER.addPersistentObjectListener(Printout.class, 
				new PersistentObjectListener<Printout>() {

			@Override
			public void receivedPersistentObject(Printout printout)
					throws MooshakContentException {

				broadcastRow(printout, PrintoutsUpdate::new);
			}
		});
		
		PATH_MANAGER.addPersistentObjectListener(Balloon.class, 
				new PersistentObjectListener<Balloon>() {

			@Override
			public void receivedPersistentObject(Balloon balloon)
					throws MooshakContentException {

				broadcastRow(balloon,BalloonsUpdate::new);
			}
		});
	}
	
	/**
	 * Get single instance of this class
	 * @return
	 */
	public static RowManager getInstance() {
		if(manager == null)
			manager = new RowManager();
		
		return manager;
	}
	
	

	/**
	 * Convenience method to broadcast a submission
	 * @param submission
	 * @throws MooshakContentException 
	 */
	public void broadcast(Submission submission) {
		broadcastRow(submission,SubmissionsUpdate::new);
	}
	
	/**
	 * Convenience method to broadcast a question
	 *
	 * @param question
	 * @throws MooshakContentException 
	 */
	public void broadcast(Question question) {
		broadcastRow(question,QuestionsUpdate::new);
	}
	
	/**
	 * Convenience method to broadcast a printout
	 * 
	 * @param printout
	 */
	public void broadcast(Printout printout) {
		broadcastRow(printout,PrintoutsUpdate::new);
	}
	
	/**
	 * Convenience method to broadcast a balloon
	 * @param balloon
	 */
	public void broadcast(Balloon balloon) {
		broadcastRow(balloon,BalloonsUpdate::new);
	}

	
	/**
	 * Convenience method to send a submission to a session
	 * @param session
	 * @param submission
	 * @throws MooshakContentException 
	 */
	public void sendToSession(Session session,Submission submission) 
			throws MooshakContentException {
		
		Contest contest = session.getContest();
		String contestId = session.getContestId();
		Recipient recipient = makeRecipient(session);
		
		sendRow(contest,contestId,recipient,submission,SubmissionsUpdate::new);
	}
	
	/**
	 * Convenience method to send a question to session
	 * @param session
	 * @param question
	 * @throws MooshakContentException 
	 */
	public void sendToSession(Session session,Question question) 
			throws MooshakContentException {
		Contest contest = session.getContest();
		String contestId = session.getContestId();
		Recipient recipient = makeRecipient(session);
		
		sendRow(contest,contestId,recipient,question,QuestionsUpdate::new);
	}
	
	/**
	 * Convenience method to broadcast a printout to session
	 * @param session
	 * @param printout
	 */
	public void sendToSession(Session session,Printout printout) 
			throws MooshakContentException {
		Contest contest = session.getContest();
		String contestId = session.getContestId();
		Recipient recipient = makeRecipient(session);
		
		sendRow(contest,contestId,recipient,printout,PrintoutsUpdate::new);
	}
	
	/**
	 * Convenience method to broadcast a balloon
	 * @param session
	 * @param balloon
	 */
	public void sendToSession(Session session,Balloon ballon)
			throws MooshakContentException {
		Contest contest = session.getContest();
		String contestId = session.getContestId();
		Recipient recipient = makeRecipient(session);
		
		sendRow(contest,contestId,recipient,ballon,BalloonsUpdate::new);
	}
	
	ExecutorService rowExecutor = Executors.newCachedThreadPool();
	
	
	/**
	 * Refresh rows on client, typically after startup
	 * 
	 * @param session
	 * @throws MooshakException
	 */
	public void refreshRows(Session session) throws MooshakException {

		rowExecutor.execute( () -> {

			try {
				Contest contest = session.getContest();
				String contestId = session.getContestId();
				Recipient recipient = makeRecipient(session);

				switch(contest.getContestStatus()) {
				case RUNNING_VIRTUALLY:
					if (session.getProfile().getIdName().equals("team")) {
						long msInContest = contest.getSecondsPassed(
								recipient.getUserId())*1000L;

						refreshRowsWithDelay(contest,contestId,recipient,msInContest,
								"submissions",SubmissionsUpdate::new);
						refreshRowsWithDelay(contest,contestId,recipient,msInContest,
								"questions",QuestionsUpdate::new);
						refreshRowsWithDelay(contest,contestId,recipient,msInContest,
								"printouts",PrintoutsUpdate::new);
						refreshRowsWithDelay(contest,contestId,recipient,msInContest,
								"balloons",BalloonsUpdate::new);

						refreshRankingRowsWithDelay(contest,contestId,recipient,msInContest);
						break;
					}
				default:
					refreshRowsImmediatly(contest,contestId,recipient,
							"submissions",SubmissionsUpdate::new);
					refreshRowsImmediatly(contest,contestId,recipient,
							"questions",QuestionsUpdate::new);
					refreshRowsImmediatly(contest,contestId,recipient,
							"printouts",PrintoutsUpdate::new);
					refreshRowsImmediatly(contest,contestId,recipient,
							"balloons",BalloonsUpdate::new);

					contest.getRankingPolicy().sendRankingsTo(recipient);
					break;
				}

			} catch (MooshakException cause) {
				cause.printStackTrace();
				//throw new MooshakException(cause.getMessage(), cause);


				LOGGER.log(Level.SEVERE,"Sending rows to client",cause);
			}
		});
	}

	
	/**
	 * Refresh question rows on client, typically after startup
	 * 
	 * @param session
	 * @throws MooshakException
	 */
	public void refreshQuestionRows(Session session) throws MooshakException {

		try {
			Contest contest = session.getContest();
			String contestId = session.getContestId();
			Recipient recipient = makeRecipient(session);

			switch(contest.getContestStatus()) {
			case RUNNING_VIRTUALLY:
				if (session.getProfile().getIdName().equals("team")) {
					long msInContest = contest.getSecondsPassed(
							recipient.getUserId())*1000L;
					
					refreshRowsWithDelay(contest,contestId,recipient,msInContest,
							"questions",QuestionsUpdate::new);
					break;
				}
			default:
				refreshRowsImmediatly(contest,contestId,recipient,
						"questions",QuestionsUpdate::new);
				break;
			}

		} catch (MooshakException cause) {
			cause.printStackTrace();
			throw new MooshakException(cause.getMessage(), cause);
		}
	}

	
	/**
	 * Refresh my submissions rows on client, typically after startup
	 * 
	 * @param session
	 * @throws MooshakException
	 */
	public void refreshMySubmissionRows(Session session) throws MooshakException {

		try {
			Contest contest = session.getContest();
			String contestId = session.getContestId();
			Recipient recipient = makeRecipient(session);

			switch(contest.getContestStatus()) {
			case RUNNING_VIRTUALLY:
				if (session.getProfile().getIdName().equals("team")) {
					long msInContest = contest.getSecondsPassed(
							recipient.getUserId())*1000L;
					
					refreshTeamRowsWithDelay(contest,contestId,recipient,msInContest,
							"submissions",SubmissionsUpdate::new, session.getParticipant().getIdName());
					break;
				}
			default:
				refreshTeamRowsImmediatly(contest,contestId,recipient,
						"submissions",SubmissionsUpdate::new,session.getParticipant().getIdName());
				break;
			}

		} catch (MooshakException cause) {
			cause.printStackTrace();
			throw new MooshakException(cause.getMessage(), cause);
		}
	}
	
	/**
	 * Refresh rows of a given folder name using an event factory
	 * 
	 * @param contest
	 * @param contestId
	 * @param recipient
	 * @param folderName
	 * @param eventFactory
	 * @throws MooshakContentException
	 */
	private <T extends PersistentObject & HasListingRow> 
		void refreshRowsImmediatly(Contest contest, 
			String contestId,
			Recipient recipient,
			String folderName,
			Supplier<ListingUpdateEvent> eventFactory)
		throws MooshakContentException {
				
		if(Files.exists(contest.getAbsoluteFile(folderName))) {
			HasListingRows rowables = contest.open(folderName);
			
			try(@SuppressWarnings("unchecked")
			POStream<T> stream = (POStream<T>) rowables.getRows()) {
				for(T rowable: stream) {
					sendRow(contest, contestId, recipient,rowable,eventFactory);
				}
			} catch (Exception cause) {
				throw new MooshakContentException("Error iteratin over rows",
						cause);
			}
		}
	}
	
	/**
	 * Refresh rows of a given folder name and a given team
	 * using an event factory
	 * 
	 * @param contest
	 * @param contestId
	 * @param recipient
	 * @param folderName
	 * @param eventFactory
	 * @param team
	 * @throws MooshakContentException
	 */
	private <T extends PersistentObject & HasListingRow> 
		void refreshTeamRowsImmediatly(Contest contest, 
			String contestId,
			Recipient recipient,
			String folderName,
			Supplier<ListingUpdateEvent> eventFactory,
			String team)
		throws MooshakContentException {
				
		if(Files.exists(contest.getAbsoluteFile(folderName))) {
			HasListingRows rowables = contest.open(folderName);
			
			try(@SuppressWarnings("unchecked")
			POStream<T> stream = (POStream<T>) rowables.getRows()) {
				for(T rowable: stream) {
					if (rowable.getTeam() != null && team.equals(rowable.getTeam().getIdName()))
						sendRow(contest, contestId, recipient,rowable,eventFactory);
				}
			} catch (Exception cause) {
				throw new MooshakContentException("Error iteratin over rows",
						cause);
			}
		}
	}
	
	private Timer virtualContestTimer = null;
	private Timer getVirtualContestTimer() {
		if(virtualContestTimer == null)
			virtualContestTimer = new Timer("Virtual contest timer",true);
		return virtualContestTimer;
	}
	
	/**
	 * Refresh rows of a given folder name using an event factory
	 * 
	 * @param contest
	 * @param contestId
	 * @param recipient
	 * @param folderName
	 * @param eventFactory
	 * @throws MooshakContentException
	 */
	private <T extends PersistentObject & HasListingRow> 
		void refreshRowsWithDelay(Contest contest, 
			String contestId,
			Recipient recipient,
			long timeInContest,
			String folderName,
			Supplier<ListingUpdateEvent> eventFactory)
		throws MooshakContentException {
		
		
		if(Files.exists(contest.getAbsoluteFile(folderName))) {
			HasListingRows rowables = contest.open(folderName);
			
			
			try(@SuppressWarnings("unchecked")
			POStream<T> stream = (POStream<T>) rowables.getRows()) {
				for(T rowable: stream) {
					Date time = rowable.getTime();

					long delay = 0;

					if(time == null) {
						LOGGER.warning("No time in delayed row from "+folderName);
					} else if((delay = time.getTime() - timeInContest) <= 0) {
						// send it straight away;
						sendRow(contest, contestId,recipient,rowable,eventFactory);
					} else {
						getVirtualContestTimer().schedule(new TimerTask() {

							@Override
							public void run() {

								try {
									sendRow(contest, contestId, 
											recipient,rowable,eventFactory);
								} catch (MooshakContentException cause) {
									LOGGER.log(Level.SEVERE
											,"Error sending virtual row from "
													+folderName
													,cause);
								}
							}
						}, delay);
					}
				}
			} catch (Exception cause) {
				throw new MooshakContentException("Error iterating over rows",
						cause);
			}
		}
	}
	
	/**
	 * Refresh rows of a given folder name and team using an event factory
	 * 
	 * @param contest
	 * @param contestId
	 * @param recipient
	 * @param folderName
	 * @param eventFactory
	 * @param team
	 * @throws MooshakContentException
	 */
	private <T extends PersistentObject & HasListingRow> 
		void refreshTeamRowsWithDelay(Contest contest, 
			String contestId,
			Recipient recipient,
			long timeInContest,
			String folderName,
			Supplier<ListingUpdateEvent> eventFactory,
			final String team)
		throws MooshakContentException {
		
		
		if(Files.exists(contest.getAbsoluteFile(folderName))) {
			HasListingRows rowables = contest.open(folderName);
			
			
			try(@SuppressWarnings("unchecked")
			POStream<T> stream = (POStream<T>) rowables.getRows()) {
				for(T rowable: stream) {
					Date time = rowable.getTime();

					long delay = 0;

					if(time == null) {
						LOGGER.warning("No time in delayed row from "+folderName);
					} else if((delay = time.getTime() - timeInContest) <= 0) {
						if (team.equals(rowable.getTeam().getIdName())) {
							// send it straight away;
							sendRow(contest, contestId,recipient,rowable,eventFactory);
						}
					} else {
						getVirtualContestTimer().schedule(new TimerTask() {

							@Override
							public void run() {

								try {
									if (team.equals(rowable.getTeam().getIdName())) {
										sendRow(contest, contestId, 
												recipient,rowable,eventFactory);
									}
								} catch (MooshakContentException cause) {
									LOGGER.log(Level.SEVERE
											,"Error sending virtual row from "
													+folderName
													,cause);
								}
							}
						}, delay);
					}
				}
			} catch (Exception cause) {
				throw new MooshakContentException("Error iterating over rows",
						cause);
			}
		}
	}
	
	/**
	 * Refresh rankings from a virtual contest with appropriate delays
	 * 
	 * @param contest
	 * @param contestId
	 * @param recipient
	 * @throws MooshakException
	 */
	private void refreshRankingRowsWithDelay(Contest contest, 
			String contestId,
			Recipient recipient,
			long timeInContest) throws MooshakException {
		
		RankingPolicy policy = contest.getFreshRankingPolicy();	
		Submissions submissions = contest.open("submissions");
		
		policy.sendRankingsTo(recipient); // send empty ranking
		
		try(POStream<Submission> stream = submissions.newPOStream()) {
			for(Submission submission: stream) {
				Date time = submission.getTime();
				long delay = 0;

				if(time == null) {
					LOGGER.warning("No time in delayed ranking row ");
				} else if((delay = time.getTime() - timeInContest) <= 0) {
					// send it straight away;
					policy.addSubmission(submission);
				} else {

					getVirtualContestTimer().schedule(new TimerTask() {

						@Override
						public void run() {

							try {
								policy.addSubmission(submission);
							} catch (MooshakContentException cause) {
								LOGGER.log(Level.SEVERE
										,"Error sending virtual ranking row from "
										,cause);
							}
						}
					}, delay);
				}
			}
		} catch(Exception cause) {
			LOGGER.log(Level.SEVERE,"Error iterating over submissions",cause);
		}
	}
	
	/**
	 * Broadcast a row update to everyone on this contest 
	 * @param rowable an object that may be seen as a row (e.g. submission)
	 * @param event   an concrete instance of a ListingUpdateEvent
	 */
	public <T extends PersistentObject & HasListingRow> 
		void broadcastRow(
				T rowable, 
				Supplier<ListingUpdateEvent> eventFactory) {
		
		try {
			Contest contest = rowable.getGrandParent();
			String contestId = contest.getIdName();
		
			sendRow(contest, contestId, null,  rowable, eventFactory);
		} catch(MooshakContentException cause) {
			severe("Bradcasting "+rowable
					+" as instance of "+eventFactory.getClass().getName(),
					cause);
		}
	}
		
	/**
	 * General method to send a row update events to contest
	 * 
	 * @param contest
	 * @param contestId
	 * @param recipient
	 * @param team
	 * @param event factory
	 * @throws MooshakContentException
	 */
	private <T extends PersistentObject & HasListingRow> void sendRow(
			Contest contest, 
			String contestId,
			Recipient recipient, 
			T rowable,
			Supplier<ListingUpdateEvent> eventfactory)
			throws MooshakContentException {
		
		ListingUpdateEvent event = eventfactory.get();
		
		if(recipient == null && contest.isFreezingListing()) {
			// force a recipient for balloons recipient during freezing
			if (rowable.getTeam() != null)
				recipient = new Recipient(rowable.getTeam().getIdName());
		}
		
		event.setId(rowable.getIdName());
		event.setRecord(rowable.getRow());
		event.setRecipient(recipient);
		EVENT_SENDER.send(contestId, event);
	}
}
